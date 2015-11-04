package sonic.oidc.code.requester;

import java.util.HashMap;

public class GetCodeRequester {

	private HashMap<String, String> valueByField;
	
	public GetCodeRequester(HashMap<String, String> valueByField) {
		this.valueByField = valueByField;
	}

	public HashMap<String, String> getValueByField() {
		return valueByField;
	}

	public void setValueByField(HashMap<String, String> valueByField) {
		this.valueByField = valueByField;
	}

}
