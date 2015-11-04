package sonic.oidc.idtoken.responder;

import sonic.oidc.idtoken.model.IdToken;
import util.security.Crypto;

public class CreateIdTokenResponder {
	private IdToken idtoken;
	private String jwtIdToken;

	public CreateIdTokenResponder(IdToken idtoken, String keySecret) {
		this.idtoken = idtoken;
		this.jwtIdToken = Crypto.sign(idtoken, keySecret);
	}

	public String getJwtIdToken() {
		return jwtIdToken;
	}

	public String getExp() {
		return idtoken.getExp();
	}

}
