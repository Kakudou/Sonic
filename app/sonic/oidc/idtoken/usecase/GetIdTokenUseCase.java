package sonic.oidc.idtoken.usecase;

import java.util.HashMap;
import java.util.List;

import sonic.oidc.idtoken.model.IdToken;
import sonic.oidc.idtoken.repository.IdTokenRepository;
import sonic.oidc.idtoken.requester.GetIdTokenRequester;
import sonic.oidc.idtoken.responder.GetIdTokenResponder;

public class GetIdTokenUseCase {
	private static IdTokenRepository repo;

	public GetIdTokenUseCase() {
	}

	public static GetIdTokenUseCase getInstance(IdTokenRepository repository) {
		repo = repository;
		return Holder.SINGLETON;
	}

	private static class Holder {
		private static final GetIdTokenUseCase SINGLETON = new GetIdTokenUseCase();
	}

	public GetIdTokenResponder execute(GetIdTokenRequester req) {
		HashMap<String, String> valueByField = req.getValueByField();

		List<IdToken> results = repo.find(valueByField);

		IdToken result = null;
		if (!results.isEmpty()) {
			result = results.get(0);
		}

		GetIdTokenResponder resp = new GetIdTokenResponder(result);
		return resp;
	}

}
