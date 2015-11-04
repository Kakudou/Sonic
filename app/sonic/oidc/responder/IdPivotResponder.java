package sonic.oidc.responder;

import java.util.Map;

public class IdPivotResponder {

	private String email;
	private String uid;
	private String nom;
	private String prenom;
	private Map<String, String> scopesData;

	public IdPivotResponder(String email, String uid, String nom,
			String prenom, Map<String, String> scopesData) {
		super();
		this.email = email;
		this.uid = uid;
		this.nom = nom;
		this.prenom = prenom;
		this.scopesData = scopesData;
	}

	public String getEmail() {
		return email;
	}

	public String getUid() {
		return uid;
	}

	public String getNom() {
		return nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public Map<String, String> getScopesData() {
		return scopesData;
	}

}
