package sonic.oidc.token.responder;

import sonic.oidc.token.model.Token;

public class GetTokenResponder {

	private Token result;

	public GetTokenResponder(Token token) {
		this.result = token;
	}

	public Token getResult() {
		return result;
	}

}
