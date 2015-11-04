package sonic.oidc.requester;

public class RevokeOidcRequester
{
  
  private String accessToken;
  
  public RevokeOidcRequester(String accessToken)
  {
    this.accessToken = accessToken;
  }
  
  public String getAccessToken()
  {
    return accessToken;
  }
  
  public void setAccessToken(String accessToken)
  {
    this.accessToken = accessToken;
  }
}
