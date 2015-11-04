package sonic.oidc.token.usecase;

import sonic.oidc.token.model.Token;
import sonic.oidc.token.repository.TokenRepository;
import sonic.oidc.token.requester.CreateTokenRequester;

public class CreateTokenUseCase {
	private static TokenRepository repo;

	public CreateTokenUseCase() {
	}

	public static CreateTokenUseCase getInstance(TokenRepository repository) {
		repo = repository;
		return Holder.SINGLETON;
	}

	private static class Holder {
		private static final CreateTokenUseCase SINGLETON = new CreateTokenUseCase();
	}

	public void execute(CreateTokenRequester tokenReq) {
		Token token = new Token();
		
		String accessToken = tokenReq.getAccessToken();
		String exp = tokenReq.getExp();
		String idToken = tokenReq.getIdToken();
		String provider = tokenReq.getProvider();
		
		token.setAccessToken(accessToken);
		token.setExp(exp);
		token.setIdToken(idToken);
		token.setProvider(provider);
		token.setTokenType("Bearer");
		
		repo.save(token);
	}

}
