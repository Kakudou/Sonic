package sonic.oidc.code.model;

import sonic.GenericModel;

public class Code extends GenericModel {

	private static final long serialVersionUID = -3882507932736538567L;

	private String accessToken;
	private String code;

	public Code() {
		super();
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
