package sonic.oidc.usecase;

import java.util.List;

import sonic.oidc.code.repository.CodeRepository;
import sonic.oidc.idtoken.model.IdToken;
import sonic.oidc.idtoken.repository.IdTokenRepository;
import sonic.oidc.token.repository.TokenRepository;

public class RevokeExpireOidcUseCase {
	private static CodeRepository codeRepo;
	private static TokenRepository tokenRepo;
	private static IdTokenRepository idTokRepo;

	public RevokeExpireOidcUseCase() {
	}

	public static RevokeExpireOidcUseCase getInstance(
			CodeRepository codeRepository, TokenRepository tokenRepository,
			IdTokenRepository idTokRepository) {
		codeRepo = codeRepository;
		tokenRepo = tokenRepository;
		idTokRepo = idTokRepository;
		return Holder.SINGLETON;
	}

	private static class Holder {
		private static final RevokeExpireOidcUseCase SINGLETON = new RevokeExpireOidcUseCase();
	}

	public void execute() {

		List<IdToken> idTokenExp = idTokRepo.findExpire();
		for (IdToken idToken : idTokenExp) {
			codeRepo.remove("accessToken", idToken.getAccessToken());
			tokenRepo.remove("accessToken", idToken.getAccessToken());
			idTokRepo.remove("accessToken", idToken.getAccessToken());
		}
	}
}
