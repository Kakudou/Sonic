package api.controller.fournisseuridentite.sonic;

import java.util.HashMap;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import sonic.application.model.Application;
import sonic.application.requester.GetApplicationRequester;
import sonic.application.responder.GetApplicationResponder;
import sonic.application.usecase.GetApplicationUseCase;
import sonic.fournisseuridentite.FournisseurIdentite;
import sonic.fournisseuridentite.sonic.requester.IdPivotSonicRequester;
import sonic.fournisseuridentite.sonic.usecase.IdPivotSonicUseCase;
import sonic.oidc.code.model.Code;
import sonic.oidc.code.responder.CreateCodeResponder;
import sonic.oidc.code.usecase.CreateCodeUseCase;
import sonic.oidc.idtoken.requester.CreateIdTokenRequester;
import sonic.oidc.idtoken.responder.CreateIdTokenResponder;
import sonic.oidc.idtoken.usecase.CreateIdTokenUseCase;
import sonic.oidc.responder.IdPivotResponder;
import sonic.oidc.token.requester.CreateTokenRequester;
import sonic.oidc.token.usecase.CreateTokenUseCase;
import util.container.UseCaseContainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Auth extends Controller
{
  
  public static Result auth()
  {
    Result result = null;
    JsonNode credential = request().body().asJson();
    
    // On recupere l'application si elle existe
    String keyId = credential.get("keyId").asText();
    String keySecret = credential.get("keySecret").asText();
    
    HashMap<String, String> valueByField = new HashMap<>();
    valueByField.put("keyId",keyId);
    valueByField.put("keySecret", keySecret);
    GetApplicationRequester appReq = new GetApplicationRequester(valueByField);
    GetApplicationResponder appResp = UseCaseContainer.get(GetApplicationUseCase.class).execute(appReq);

    if (appResp.getResult() != null) {
      Application app = appResp.getResult();
        
        //on cree idpivot avec scope data
        String scopesSonic = app.getScopesByFi().get(FournisseurIdentite.SONIC);
        IdPivotSonicRequester idPivotReq = new IdPivotSonicRequester(app.getKeyId(), app.getKeySecret(), app.getName(), scopesSonic);
        IdPivotResponder idPivotResp = UseCaseContainer.get(IdPivotSonicUseCase.class).execute(idPivotReq);
        
        // On cree Code
        CreateCodeResponder codeResp = UseCaseContainer.get(CreateCodeUseCase.class).execute();
        Code code = codeResp.getCode();
        
        // On cree IdToken
        CreateIdTokenRequester crIdTokReq = new CreateIdTokenRequester(code.getAccessToken(), idPivotResp.getEmail(),
        		app.getKeyId(), app.getName(), idPivotResp.getPrenom(), app.getTimeOut(), idPivotResp.getScopesData(), app.getKeySecret(), app.getName());
        CreateIdTokenResponder crIdTokResp = UseCaseContainer.get(CreateIdTokenUseCase.class).execute(crIdTokReq);
        
        // On cree Token
        CreateTokenRequester crTokReq = new CreateTokenRequester(code.getAccessToken(), crIdTokResp.getJwtIdToken(), crIdTokResp.getExp(), FournisseurIdentite.SONIC.name());
        UseCaseContainer.get(CreateTokenUseCase.class).execute(crTokReq);
        
        // On return le Code
        result = ok(Json.toJson(code));
    }
    
    ObjectNode jsonError = Json.newObject();
    jsonError.put("BadRequest", "Valid account Sonic needed");
    if (result == null) {
      result = badRequest(jsonError);
    }
    
    return result;
  }
  
}
