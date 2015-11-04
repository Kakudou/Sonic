package sonic.application.responder;

import sonic.application.model.Application;

public class GetApplicationResponder {

	private Application result;

	public GetApplicationResponder(Application applications) {
		this.result = applications;
	}

	public Application getResult() {
		return result;
	}
}
