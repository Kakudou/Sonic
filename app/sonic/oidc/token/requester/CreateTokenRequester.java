package sonic.oidc.token.requester;

public class CreateTokenRequester {
	private String accessToken;
	private String idToken;
	private String exp;
	private String provider;

	public CreateTokenRequester(String accessToken, String idToken, String exp, String provider) {
		this.accessToken = accessToken;
		this.idToken = idToken;
		this.exp = exp;
		this.provider = provider;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getIdToken() {
		return idToken;
	}

	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProvider() {
		return provider;
	}

}
