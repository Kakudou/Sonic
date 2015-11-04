package api.controller.fournisseuridentite.sso;

import java.util.HashMap;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import sonic.application.model.Application;
import sonic.application.requester.GetApplicationRequester;
import sonic.application.responder.GetApplicationResponder;
import sonic.application.usecase.GetApplicationUseCase;
import sonic.fournisseuridentite.FournisseurIdentite;
import sonic.oidc.code.model.Code;
import sonic.oidc.code.responder.CreateCodeResponder;
import sonic.oidc.code.usecase.CreateCodeUseCase;
import sonic.oidc.idtoken.requester.CreateIdTokenRequester;
import sonic.oidc.idtoken.responder.CreateIdTokenResponder;
import sonic.oidc.idtoken.usecase.CreateIdTokenUseCase;
import sonic.oidc.token.requester.CreateTokenRequester;
import sonic.oidc.token.usecase.CreateTokenUseCase;
import util.container.UseCaseContainer;
import api.kerberos.sso.Login;
import api.kerberos.sso.Secured;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Auth extends Controller
{
  @Secured(INF = Login.class)
  public static Result auth()
  {
    Result result = null;
    
    // On recupere l'application avec keyId et redirectUri
    String keyId = session().get("keyId");
    String redirectUri = session().get("redirectUri");
    
    HashMap<String, String> valueByField = new HashMap<>();
    valueByField.put("keyId",keyId);
    valueByField.put("redirectUri", redirectUri);
    GetApplicationRequester appReq = new GetApplicationRequester(valueByField);
    GetApplicationResponder appResp = UseCaseContainer.get(GetApplicationUseCase.class).execute(appReq);
    
    if (appResp.getResult() != null) {
    	
      Application app = appResp.getResult();
        if(app.getScopesByFi().containsKey(FournisseurIdentite.SSO)){
          
          // On cree Code
          CreateCodeResponder codeResp = UseCaseContainer.get(CreateCodeUseCase.class).execute();
          Code code = codeResp.getCode();
          
          // On cree IdToken
          CreateIdTokenRequester crIdTokReq = new CreateIdTokenRequester(code.getAccessToken(), "email",
              "uid", "nom", "prenom", app.getTimeOut(),
              null, app.getKeySecret(), app.getName());
          CreateIdTokenResponder crIdTokResp = UseCaseContainer.get(CreateIdTokenUseCase.class).execute(crIdTokReq);
          
          // On cree Token
          CreateTokenRequester crTokReq = new CreateTokenRequester(code.getAccessToken(), crIdTokResp.getJwtIdToken(),
              crIdTokResp.getExp(), FournisseurIdentite.SSO.name());
          UseCaseContainer.get(CreateTokenUseCase.class).execute(crTokReq);
          
          // On return le Code
          String callback = session().get("redirectUri");
          if (Boolean.valueOf(session().get("xAccess"))){
            response().setCookie("sonicToken", code.getCode(), 7200);
          }
          session().remove("xAccess");
          session().remove("redirectUri");
          session().remove("keyId");
          result = redirect(callback + "?code="+ code.getCode());
        }
    }
    
    ObjectNode jsonError = Json.newObject();
    jsonError.put("BadRequest", "Valid Account in domain needed");
    if (result == null) {
      result = badRequest(jsonError);
    }
        
    return result;
  }
}
