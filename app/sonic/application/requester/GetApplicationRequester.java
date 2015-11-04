package sonic.application.requester;

import java.util.HashMap;


public class GetApplicationRequester {

	private HashMap<String, String> valueByField;
	
	public GetApplicationRequester(HashMap<String, String> valueByField) {
		this.valueByField = valueByField;
	}

	public HashMap<String, String> getValueByField() {
		return valueByField;
	}

	public void setValueByField(HashMap<String, String> valueByField) {
		this.valueByField = valueByField;
	}

}
