package sonic.oidc.idtoken.usecase;

import java.util.Calendar;
import java.util.Map;

import sonic.oidc.idtoken.model.IdToken;
import sonic.oidc.idtoken.repository.IdTokenRepository;
import sonic.oidc.idtoken.requester.CreateIdTokenRequester;
import sonic.oidc.idtoken.responder.CreateIdTokenResponder;

public class CreateIdTokenUseCase {
	private static IdTokenRepository repo;

	public CreateIdTokenUseCase() {
	}

	public static CreateIdTokenUseCase getInstance(IdTokenRepository repository) {
		repo = repository;
		return Holder.SINGLETON;
	}

	private static class Holder {
		private static final CreateIdTokenUseCase SINGLETON = new CreateIdTokenUseCase();
	}

	public CreateIdTokenResponder execute(CreateIdTokenRequester idTokenReq) {

		IdToken idToken = new IdToken();

		String accessToken = idTokenReq.getAccessToken();
		String aud = idTokenReq.getAud();
		String email = idTokenReq.getEmail();
		Calendar now = Calendar.getInstance();
		String iat = String.valueOf(now.getTimeInMillis());

		String sub = idTokenReq.getIdentifiant();
		String nom = idTokenReq.getNom();
		String prenom = idTokenReq.getPrenom();
		String addTime = idTokenReq.getAddTimeOut();
		Map<String, String> scopeData = idTokenReq.getScopeData();

		
		idToken.setAccessToken(accessToken);
		idToken.setAud(aud);
		idToken.setEmail(email);
		idToken.setExp(String.valueOf(Long.parseLong(iat) + Long.parseLong(addTime)*1000));
		idToken.setIat(iat);
		idToken.setIss("Sonic");
		idToken.setNom(nom);
		idToken.setPrenom(prenom);
		idToken.setScopeData(scopeData);
		idToken.setSub(sub);

		repo.save(idToken);

		CreateIdTokenResponder resp = new CreateIdTokenResponder(idToken,
				idTokenReq.getKeySecret());

		return resp;
	}

}
