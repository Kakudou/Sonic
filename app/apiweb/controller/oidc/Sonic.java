package apiweb.controller.oidc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import sonic.fournisseuridentite.FournisseurIdentite;
import util.api.InvokeApi;
import apiweb.view.fournisseuridentite.sonic.html.formulaireSonic;
import apiweb.view.html.fournisseuridentite;
import apiweb.view.fournisseuridentite.html.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Sonic extends Controller
{
  public static Result authorize()
  {
    Result result = null;
    ObjectNode jsonError = Json.newObject();
    
    Map<String, String[]> param = request().queryString();
    // QP response_type
    if (!param.containsKey("response_type") || !param.get("response_type")[0].equals("code")) {
      jsonError.put("BadRequest", "QueryParam response_type is required");
      result = badRequest(error.render(jsonError));
    }
    // QP application_id
    if (!param.containsKey("key_id")) {
      jsonError.put("BadRequest", "QueryParam key_id is required");
      result = badRequest(error.render(jsonError));
    }
    // QP redirect_uri
    if (!param.containsKey("redirect_uri")) {
      jsonError.put("BadRequest", "QueryParam redirect_uri is required");
      result = badRequest(error.render(jsonError));
    }
    // QP scope
    if (!param.containsKey("scope") || !param.get("scope")[0].equals("openid")) {
      jsonError.put("BadRequest", "QueryParam scope is required");
      result = badRequest(error.render(jsonError));
    }
    
    if (result == null) {
      Map<String, String[]> rawfilter = new HashMap<>();
      
      // On recupere l'application (api)
      String[] keyId = { param.get("key_id")[0] };
      rawfilter.put("key_id", keyId);
      String[] redirectUri = { param.get("redirect_uri")[0] };
      rawfilter.put("redirect_uri", redirectUri);
      
      String urlApplication = api.controller.application.routes.Application.getApplication().absoluteURL(request());
      
      JsonNode app = InvokeApi.invokeGet(urlApplication, rawfilter, null);
      
      // On regarde si on a une application
      if (app.has("BadRequest")) {
        jsonError.put("BadRequest", "Valid Application is needed");
        result = badRequest(error.render(app));
      } else {
        // On regarde si l'application authorise le xAccess.
        boolean xAccess = app.get("xAccess").asBoolean();
        session().put("xAccess", Boolean.toString(xAccess));
        if (xAccess) {
          // On regarde si un claimCode est dans le cookie "sonicToken"
          if (request().cookie("sonicToken") != null) {
            String urlTokenEndpoint = api.controller.oidc.routes.Sonic.token_endpoint().absoluteURL(request());
            
            ObjectNode jsonContent = Json.newObject();
            jsonContent.put("key_id", param.get("key_id")[0]);
            jsonContent.put("redirect_uri", param.get("redirect_uri")[0]);
            jsonContent.put("code", request().cookie("sonicToken").value());
            jsonContent.put("grant_type", "authorization_code");
            
            // On token_endpoint le claim code
            JsonNode token = InvokeApi.invokePost(urlTokenEndpoint, jsonContent);
            // On regarde si token possede un provider fi abonne
            if (token.has("provider")) {
              boolean asProvider = app.get("scopesByFi").has(token.get("provider").asText());
              if (asProvider) {
                // Si oui result=redirectUri(claimCode)
                result = redirect(jsonContent.get("redirect_uri").asText() + "?code="
                    + request().cookie("sonicToken").value());
              }
            }
          }
        }
        
        if (result == null) {
          response().discardCookie("sonicToken");
          session().put("keyId", param.get("key_id")[0]);
          session().put("redirectUri", param.get("redirect_uri")[0]);
          
          LinkedList<String> listFis = new LinkedList<>();
          Iterator<String> i = app.get("scopesByFi").fieldNames();
          while (i.hasNext()) {
            String fi = (String) i.next();
            listFis.add(fi);
          }
          
          Collections.sort(listFis, String.CASE_INSENSITIVE_ORDER);
          result = ok(fournisseuridentite.render(Json.toJson(listFis)));
        }
      }
      
    }
    
    return result;
  }
  
  public static Result selectFI()
  {
    Result result = badRequest("Choisir un FI.");
    
    // On recupere le fi du form
    DynamicForm fiForm = Form.form().bindFromRequest();
    String fiValue = fiForm.get("submit");
    
    // On verifie que l'app à le droit de l'utiliser
    Map<String, String[]> rawfilter = new HashMap<>();
    
    // On recupere l'application (api)
    String[] keyId = { session().get("keyId") };
    rawfilter.put("key_id", keyId);
    String[] redirectUri = { session().get("redirectUri") };
    rawfilter.put("redirect_uri", redirectUri);
    
    String urlApplication = api.controller.application.routes.Application.getApplication().absoluteURL(request());
    
    JsonNode app = InvokeApi.invokeGet(urlApplication, rawfilter, null);
    boolean asProvider = app.get("scopesByFi").has(fiValue);
    // Si l'app a accès au FI
    if (asProvider) {
      FournisseurIdentite fi = FournisseurIdentite.valueOf(fiValue);
      JsonNode nodeScopes = app.get("scopesByFi").get(fiValue);
      List<String> listScope = new ArrayList<String>();
      if (nodeScopes.asText().length() > 0) {
        listScope = Arrays.asList(nodeScopes.asText().split(","));
      }
      // on le redirige vers l'apiweb correspondante
      switch (fi) {
        case SONIC:
          result = ok(formulaireSonic.render(listScope));
          break;
        case SSO:
          result = redirect(api.controller.fournisseuridentite.sso.routes.Auth.auth());
          break;
      }
    }
    
    return result;
  }
  
}
