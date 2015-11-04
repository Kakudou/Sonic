package sonic.oidc.idtoken.requester;

import java.util.HashMap;

public class GetIdTokenRequester {
	
	private HashMap<String, String> valueByField;

	public GetIdTokenRequester(HashMap<String, String> valueByField) {
		this.valueByField = valueByField;
	}

	public HashMap<String, String> getValueByField() {
		return valueByField;
	}

	public void setValueByField(HashMap<String, String> valueByField) {
		this.valueByField = valueByField;
	}

}
