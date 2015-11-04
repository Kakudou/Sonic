package apiweb.controller.fournisseuridentite.sonic;

import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.api.InvokeApi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import apiweb.view.fournisseuridentite.html.error;

public class Auth extends Controller {

	public static Result authenticateSonic() {
		DynamicForm sonicForm = Form.form().bindFromRequest();
		String keyId = sonicForm.get("keyId");
		String keySecret = sonicForm.get("keySecret");

		String url = api.controller.fournisseuridentite.sonic.routes.Auth.auth().absoluteURL(request());
		ObjectNode json = Json.newObject();
		json.put("keyId", keyId);
		json.put("keySecret", keySecret);
		json.put("redirectUri", session().get("redirectUri"));

		JsonNode webServiceResponse = InvokeApi.invokePost(url, json);

		Result result = null;
		if (!webServiceResponse.has("BadRequest")) {
			String callback = session().get("redirectUri");
			String code = webServiceResponse.get("code").asText();
			if (Boolean.valueOf(session().get("xAccess"))){
				response().setCookie("sonicToken", code, 7200); 
			}
			
	      session().remove("xAccess");
	      session().remove("redirectUri");
	      session().remove("keyId");
	      result = redirect(callback + "?code="+ code);
		}
		
		if (result == null) {
		    result = badRequest(error.render(webServiceResponse));
		}
		return result;
	}

}
