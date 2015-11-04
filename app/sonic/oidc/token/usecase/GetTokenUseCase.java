package sonic.oidc.token.usecase;

import java.util.HashMap;
import java.util.List;

import sonic.oidc.token.model.Token;
import sonic.oidc.token.repository.TokenRepository;
import sonic.oidc.token.requester.GetTokenRequester;
import sonic.oidc.token.responder.GetTokenResponder;

public class GetTokenUseCase {
	private static TokenRepository repo;

	public GetTokenUseCase() {
	}

	public static GetTokenUseCase getInstance(TokenRepository repository) {
		repo = repository;
		return Holder.SINGLETON;
	}

	private static class Holder {
		private static final GetTokenUseCase SINGLETON = new GetTokenUseCase();
	}

	public GetTokenResponder execute(GetTokenRequester req) {
		HashMap<String, String> valueByField = req.getValueByField();

		List<Token> results = repo.find(valueByField);

		Token result = null;
		if (!results.isEmpty()) {
			result = results.get(0);
		}

		GetTokenResponder resp = new GetTokenResponder(result);
		return resp;
	}

}
