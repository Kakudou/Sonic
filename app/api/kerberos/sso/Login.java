package api.kerberos.sso;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.twirl.api.Content;

public class Login implements SecuredInf
{
  @Override
  public boolean isLoggedIn()
  {
    String loggedIn = Controller.session("loggedIn");
    if (loggedIn == null) return false;
    return loggedIn.equals("true");
  }
  
  @Override
  public void userLoggedIn(String sam)
  {
    Controller.session("loggedIn", "true");
    String samAccount = sam.split("@")[0];
    String acc = ActiveDirectoryLDAP.getSamAccountNameFromLdap(samAccount);
    Controller.session("acc", acc);
  }
  
  @Override
  public Content getHTML() {
      ObjectNode jsonError = Json.newObject();
      jsonError.put("BadRequest", "SSO works only for domain");
      return apiweb.view.fournisseuridentite.html.error.render(jsonError);
  }
}
