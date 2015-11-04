package sonic.oidc.idtoken.requester;

import java.util.Map;

public class CreateIdTokenRequester {

	private String accessToken;
	private String email;
	private String identifiant;
	private String nom;
	private String prenom;
	private String addTimeOut;
	private Map<String, String> scopeData;
	private String keySecret;
	private String aud;
	
	public CreateIdTokenRequester(String accessToken, String email,
			String identifiant, String nom, String prenom, String addTimeOut,
			Map<String, String> scopeData, String keySecret, String aud) {
		this.accessToken = accessToken;
		this.email = email;
		this.identifiant = identifiant;
		this.nom = nom;
		this.prenom = prenom;
		this.scopeData = scopeData;
		this.addTimeOut = addTimeOut;
		this.keySecret = keySecret;
		this.aud = aud;
	}


	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
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

	public Map<String, String> getScopeData() {
		return scopeData;
	}

	public void setScopeData(Map<String, String> scopeData) {
		this.scopeData = scopeData;
	}

	public String getAddTimeOut() {
		return addTimeOut;
	}

	public void setAddTimeOut(String addTimeOut) {
		this.addTimeOut = addTimeOut;
	}

	public String getKeySecret() {
		return keySecret;
	}

	public void setKeySecret(String keySecret) {
		this.keySecret = keySecret;
	}


	public void setAud(String aud){
		this.aud = aud;
	}
	
	public String getAud() {
		return aud;
	}

}
