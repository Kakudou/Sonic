package sonic.oidc.idtoken.responder;

import sonic.oidc.idtoken.model.IdToken;

public class GetIdTokenResponder {

	private IdToken result;

	public GetIdTokenResponder(IdToken token) {
		this.result = token;
	}

	public IdToken getResult() {
		return result;
	}

}
