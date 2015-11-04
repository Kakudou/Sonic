package api.controller.application;

import java.util.HashMap;
import java.util.Map;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import sonic.application.requester.GetApplicationRequester;
import sonic.application.responder.GetApplicationResponder;
import sonic.application.usecase.GetApplicationUseCase;
import util.container.UseCaseContainer;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Application extends Controller
{
  
  public static Result getApplication()
  {
    Result result = null;
    ObjectNode jsonError = Json.newObject();
    
    Map<String, String[]> param = request().queryString();
    // QP application_id
    if (!param.containsKey("key_id")) {
      jsonError.put("BadRequest", "QueryParam key_id is required");
      result = badRequest(jsonError);
    }
    // QP redirect_uri
    if (!param.containsKey("redirect_uri")) {
      jsonError.put("BadRequest", "QueryParam redirect_uri is required");
      result = badRequest(jsonError);
    }
    
    if (result == null) {
      String keyId = param.get("key_id")[0];
      String redirectUri = param.get("redirect_uri")[0];
      
      HashMap<String, String> valueByField = new HashMap<>();
      valueByField.put("keyId", keyId);
      valueByField.put("redirectUri", redirectUri);
      GetApplicationRequester req = new GetApplicationRequester(valueByField);
      GetApplicationResponder resp = UseCaseContainer.get(GetApplicationUseCase.class).execute(req);
      
      if (resp.getResult() != null) {
        result = ok(Json.toJson(resp.getResult()));
      }
    }
    
    if (result == null) {
      jsonError.put("BadRequest", "Valid keyId & redirectUri needed");
      result = badRequest(jsonError);
    }
    
    return result;
  }
  
}
