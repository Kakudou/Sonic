package sonic.application.model;

import java.util.LinkedHashMap;
import java.util.Map;

import sonic.GenericModel;
import sonic.fournisseuridentite.FournisseurIdentite;

public class Application extends GenericModel {

	private static final long serialVersionUID = 6855440034905911654L;

	private String name;
	private String keySecret;
	private String keyId;
	private String redirectUri;
	private Map<FournisseurIdentite, String> scopesByFi;
	private Boolean xAccess;
	private String timeOut;

	public Application() {
		super();
		scopesByFi = new LinkedHashMap<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKeySecret() {
		return keySecret;
	}

	public void setKeySecret(String keySecret) {
		this.keySecret = keySecret;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public Boolean getxAccess() {
		return xAccess;
	}

	public void setxAccess(Boolean xAccess) {
		this.xAccess = xAccess;
	}

	public String getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}

	public Map<FournisseurIdentite, String> getScopesByFi() {
		return scopesByFi;
	}

	public void setScopesByFi(Map<FournisseurIdentite, String> scopesByFi) {
		this.scopesByFi = scopesByFi;
	}

}
