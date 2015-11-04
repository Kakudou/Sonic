package api.kerberos.sso;

import play.twirl.api.Content;

public interface SecuredInf
{
  public boolean isLoggedIn();
  
  public void userLoggedIn(String username);
  
  public Content getHTML();
}
