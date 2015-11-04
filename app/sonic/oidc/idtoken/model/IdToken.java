package sonic.oidc.idtoken.model;

import java.util.Map;

import sonic.GenericModel;

public class IdToken extends GenericModel {

	private static final long serialVersionUID = 8080805664082420494L;

	private String iss;
	private String sub;
	private String aud;
	private String exp;
	private String iat;
	private String accessToken;
	private String nom;
	private String prenom;
	private String email;
	private Map<String, String> scopeData;

	public IdToken() {
		super();
	}

	public String getIss() {
		return iss;
	}

	public void setIss(String iss) {
		this.iss = iss;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Map<String, String> getScopeData() {
		return scopeData;
	}

	public void setScopeData(Map<String, String> scopeData) {
		this.scopeData = scopeData;
	}

	public String getAud() {
		return aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public String getIat() {
		return iat;
	}

	public void setIat(String iat) {
		this.iat = iat;
	}

}
