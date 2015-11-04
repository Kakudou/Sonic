package api.controller.oidc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import sonic.application.requester.GetApplicationRequester;
import sonic.application.responder.GetApplicationResponder;
import sonic.application.usecase.GetApplicationUseCase;
import sonic.oidc.code.requester.GetCodeRequester;
import sonic.oidc.code.responder.GetCodeResponder;
import sonic.oidc.code.usecase.GetCodeUseCase;
import sonic.oidc.idtoken.model.IdToken;
import sonic.oidc.idtoken.requester.GetIdTokenRequester;
import sonic.oidc.idtoken.responder.GetIdTokenResponder;
import sonic.oidc.idtoken.usecase.GetIdTokenUseCase;
import sonic.oidc.requester.RevokeOidcRequester;
import sonic.oidc.token.model.Token;
import sonic.oidc.token.requester.GetTokenRequester;
import sonic.oidc.token.responder.GetTokenResponder;
import sonic.oidc.token.usecase.GetTokenUseCase;
import sonic.oidc.usecase.RevokeExpireOidcUseCase;
import sonic.oidc.usecase.RevokeOidcUseCase;
import util.container.UseCaseContainer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Sonic extends Controller
{
  
  public static Result token_endpoint()
  {
    Result result = null;
    ObjectNode jsonError = Json.newObject();
    
    JsonNode postContent = request().body().asJson();
    
    if (!postContent.has("key_id")) {
      jsonError.put("BadRequest", "Valid key_id needed");
      result = badRequest(jsonError);
    }
    
    if (!postContent.has("redirect_uri")) {
      jsonError.put("BadRequest", "Valid redirect_uri needed");
      result = badRequest(jsonError);
    }
    
    if (!postContent.has("code")) {
      jsonError.put("BadRequest", "Valid code needed");
      result = badRequest(jsonError);
    }
    
    if (!postContent.has("grant_type") && !postContent.get("grant_type").equals("authorization_code")) {
      jsonError.put("BadRequest", "Valid grant_type needed");
      result = badRequest(jsonError);
    }
    
    if (result == null) {
      UseCaseContainer.get(RevokeExpireOidcUseCase.class).execute();
    	
      String keyId = postContent.get("key_id").asText();
      String redirectUri = postContent.get("redirect_uri").asText();
      String code = postContent.get("code").asText();
      // On verifie que l'application existe avec keyId+redirectUri

      HashMap<String, String> valueByField = new HashMap<>();
      valueByField.put("keyId", keyId);
      valueByField.put("redirectUri", redirectUri);
      GetApplicationRequester appReq = new GetApplicationRequester(valueByField);
      GetApplicationResponder appResp = UseCaseContainer.get(GetApplicationUseCase.class).execute(appReq);
      
      if (appResp.getResult() == null) {
        jsonError.put("BadRequest", "Valid application credential needed");
        result = badRequest(jsonError);
      }
      
      if (result == null) {
        // On verifie que le code renvoi bien un accessToken
    	valueByField.clear();
    	valueByField.put("code", code);
        GetCodeRequester codeReq = new GetCodeRequester(valueByField);
        GetCodeResponder codeResp = UseCaseContainer.get(GetCodeUseCase.class).execute(codeReq);
        
        if (codeResp.getResult() == null) {
          jsonError.put("BadRequest", "Valid code needed");
          result = badRequest(jsonError);
        }
        
        if (result == null) {
        	valueByField.clear();
          // On recuperer le token lié à l'accessToken
          String accessTokens = codeResp.getResult().getAccessToken();
          valueByField.put("accessToken", accessTokens);
          GetTokenRequester tokenReq = new GetTokenRequester(valueByField);
          GetTokenResponder tokenResp = UseCaseContainer.get(GetTokenUseCase.class).execute(tokenReq);
          
          if (tokenResp.getResult() != null) {
            result = ok(Json.toJson(tokenResp.getResult()));
          } else {
            jsonError.put("BadRequest", "No valid token found");
            result = badRequest(jsonError);
          }
        }
      }
    }
    return result;
  }
  
  public static Result userinfo_endpoint()
  {
    Result result = null;
    ObjectNode jsonError = Json.newObject();
    
    // Header: authorization, keyId, keySecret
    if (request().getHeader("Authorization") == null
        && !request().getHeader("Authorization").split(" ")[0].equals("Bearer")) {
      jsonError.put("BadRequest", "Header Authorization : Bearer is required");
      result = badRequest(jsonError);
    }
    
    if (request().getHeader("key_id") == null) {
      jsonError.put("BadRequest", "Header key_id is required");
      result = badRequest(jsonError);
    }
    if (request().getHeader("key_secret") == null) {
      jsonError.put("BadRequest", "Header key_secret is required");
      result = badRequest(jsonError);
    }
    
    if (result == null) {
      // On verifie que l'application existe avec keyId+keySecret
      String keyId = request().getHeader("key_id");
      String keySecrets = request().getHeader("key_secret");
      
      HashMap<String, String> valueByField = new HashMap<>();
      valueByField.put("keyId",keyId);
      valueByField.put("keySecrets", keySecrets);
      GetApplicationRequester appReq = new GetApplicationRequester(valueByField);
      GetApplicationResponder appResp = UseCaseContainer.get(GetApplicationUseCase.class).execute(appReq);
      
      if (appResp.getResult() == null) {
        jsonError.put("BadRequest", "Valid key_id and key_secret needed");
        result = badRequest(jsonError);
      }
      
      if (result == null) {
    	valueByField.clear();
        // On recupere l'idToken lié à l'accessToken
        String accessToken = request().getHeader("Authorization").split(" ")[1];
        valueByField.put("accessToken", accessToken);
        GetIdTokenRequester idtokReq = new GetIdTokenRequester(valueByField);
        GetIdTokenResponder idTokResp = UseCaseContainer.get(GetIdTokenUseCase.class).execute(idtokReq);
        
        if (idTokResp.getResult()!=null) {
          IdToken idToken = idTokResp.getResult();
          if (idToken.getIss().equals("Sonic")) {
            result = ok(Json.toJson(idToken));
          }
        }
      }
      
      if (result == null) {
        jsonError.put("BadRequest", "No valid idToken found");
        result = badRequest(jsonError);
      }
      
    }
    return result;
  }
  
  public static Result checktoken_endpoint()
  {
    Result result = null;
    ObjectNode jsonError = Json.newObject();
    
    JsonNode postContent = request().body().asJson();
    
    if (!postContent.has("token")) {
      jsonError.put("BadRequest", "Valid token needed");
      result = badRequest(jsonError);
    }
    
    if (result == null) {
      HashMap<String, String> valueByField = new HashMap<>();
      
      // On recupere le idToken et token pour foger le checkToken
      String accessToken = postContent.get("token").asText();
      valueByField.put("accessToken", accessToken);
      
      GetIdTokenRequester idtokReq = new GetIdTokenRequester(valueByField);
      GetIdTokenResponder idTokResp = UseCaseContainer.get(GetIdTokenUseCase.class).execute(idtokReq);
      
      GetTokenRequester tokReq = new GetTokenRequester(valueByField);
      GetTokenResponder tokResp = UseCaseContainer.get(GetTokenUseCase.class).execute(tokReq);
      
      if (idTokResp.getResult() != null && tokResp.getResult() != null) {
        IdToken idToken = idTokResp.getResult();
        Token token = tokResp.getResult();
        if (idToken.getIss().equals("Sonic")) {
          ObjectNode checkedToken = Json.newObject();
          checkedToken.put("sub", idToken.getSub());
          checkedToken.put("aud", idToken.getAud());
          checkedToken.put("provider", token.getProvider());
          result = ok(checkedToken);
        }
      }
    }
    return result;
  }
  
  public static Result revoke_endpoint()
  {
    Result result = null;
    ObjectNode jsonError = Json.newObject();
    
    // QP: accessToken
    Map<String, String[]> param = request().queryString();
    if (!param.containsKey("access_token")) {
      jsonError.put("BadRequest", "QueryParam access_token is required");
      result = badRequest(jsonError);
    }
    if (result == null) {
      RevokeOidcRequester revokReq = new RevokeOidcRequester(param.get("access_token")[0]);
      UseCaseContainer.get(RevokeOidcUseCase.class).execute(revokReq);
      response().discardCookie("sonicToken");
      result = ok(Json.newObject());
    }
    
    return result;
  }
  
  public static Result discovery()
  {
    File discoveryDocument = Play.application().getFile("conf/discoveryDocument.json");
    InputStream is = null;
    
    try {
      is = new FileInputStream(discoveryDocument);
    } catch (FileNotFoundException e) {
    }
    
    String prettyprint;
    try {
      prettyprint = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(Json.parse(is));
    } catch (JsonProcessingException e) {
      prettyprint = Json.stringify(Json.parse(is));
    }
    
    return ok(prettyprint);
  }

}
