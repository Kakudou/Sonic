package sonic.oidc.code.responder;

import sonic.oidc.code.model.Code;

public class GetCodeResponder {

	private Code result;

	public GetCodeResponder(Code code) {
		this.result = code;
	}

	public Code getResult() {
		return result;
	}

}
