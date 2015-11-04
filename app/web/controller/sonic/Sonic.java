package web.controller.sonic;

import java.util.HashMap;
import java.util.Map;

import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import sonic.fournisseuridentite.FournisseurIdentite;
import util.api.InvokeApi;
import util.security.Crypto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import web.view.html.login;

public class Sonic extends Controller
{
  
  public static final JsonNode DISCOVERY = InvokeApi.invokeGet("http://localhost:9000/api/v1", null, null);
  public static final String KEYID = Play.application().configuration().getString("keyId");
  public static final String KEYSECRET = Play.application().configuration().getString("keySecret");
  public static final String REDIRECTURI = Play.application().configuration().getString("redirectUri");
  public static final Boolean XACCESS = Boolean.valueOf(Play.application().configuration().getString("xAccess"));
  
  public static Result index()
  {
    Result result = ok(login.render());
    
    if (session().get("X-ACCESS-TOKEN") != null) {
      if (FournisseurIdentite.SONIC.name().equalsIgnoreCase(session().get("provider"))) {
        result = redirect(web.controller.application.routes.Application.edition());
      } else {
        result = redirect(web.controller.application.routes.Application.creation());
      }
    }
    
    return result;
  }
  
  public static Result callauth()
  {
    String authorization_endpoint = DISCOVERY.get("authorization_endpoint").asText();
    String returnUrl = authorization_endpoint + "?scope=openid&redirect_uri=" + REDIRECTURI + "&key_id=" + KEYID
        + "&response_type=code";
    return redirect(returnUrl);
  }
  
  public static Result callback()
  {
    Result result = null;
    ObjectNode jsonError = Json.newObject();
    
    Map<String, String[]> param = request().queryString();
    // QP code
    if (!param.containsKey("code")) {
      jsonError.put("BadRequest", "QueryParam code is required");
      result = badRequest(jsonError);
    }
    
    if (result == null) {
      String token_Endpoint = DISCOVERY.get("token_endpoint").asText();
      
      ObjectNode jsonContent = Json.newObject();
      jsonContent.put("key_id", KEYID);
      jsonContent.put("redirect_uri", REDIRECTURI);
      jsonContent.put("code", param.get("code")[0]);
      jsonContent.put("grant_type", "authorization_code");
      JsonNode token = InvokeApi.invokePost(token_Endpoint, jsonContent);
      String jwtToken = token.get("idToken").asText();
      
      boolean isSigned = Crypto.verify(KEYSECRET, jwtToken);
      if (isSigned || XACCESS) {
        JsonNode idToken = Crypto.decode(jwtToken.split("\\.")[1]);
        
        session().put("X-ACCESS-TOKEN", idToken.get("AccessToken").asText());
        session().put("provider", token.get("provider").asText());
        
        if (token.get("provider").asText().equalsIgnoreCase(FournisseurIdentite.SONIC.name())) {
          // Ecran edition application
          result = redirect(web.controller.application.routes.Application.edition());
        } else {
          // Ecran creation application
          result = redirect(web.controller.application.routes.Application.creation());
        }
      }
    }
    
    if (result == null) {
      result = redirect(web.controller.sonic.routes.Sonic.index());
    }
    
    return result;
  }
  
  public static Result logout()
  {
    String revoke_endpoint = DISCOVERY.get("revoke_endpoint").asText();
    
    Map<String, String[]> queryParams = new HashMap<>();
    String[] accessToken = { session().get("X-ACCESS-TOKEN") };
    queryParams.put("access_token", accessToken);
    
    InvokeApi.invokeGet(revoke_endpoint, queryParams, null);
    
    session().clear();
    return redirect(web.controller.sonic.routes.Sonic.index());
  }
  
  public static Result untrail(String path)
  {
    return redirect("/" + path);
  }
}
