package sonic.oidc.token.requester;

import java.util.HashMap;

public class GetTokenRequester {

	private HashMap<String, String> valueByField;

	public GetTokenRequester(HashMap<String, String> valueByField) {
		this.valueByField = valueByField;
	}

	public HashMap<String, String> getValueByField() {
		return valueByField;
	}

	public void setValueByField(HashMap<String, String> valueByField) {
		this.valueByField = valueByField;
	}


}
