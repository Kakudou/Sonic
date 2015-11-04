package sonic.oidc.code.usecase;

import java.util.UUID;

import sonic.oidc.code.model.Code;
import sonic.oidc.code.repository.CodeRepository;
import sonic.oidc.code.responder.CreateCodeResponder;
import util.security.Crypto;

public class CreateCodeUseCase {
	private static CodeRepository repo;

	public CreateCodeUseCase() {
	}

	public static CreateCodeUseCase getInstance(CodeRepository repository) {
		repo = repository;
		return Holder.SINGLETON;
	}

	private static class Holder {
		private static final CreateCodeUseCase SINGLETON = new CreateCodeUseCase();
	}

	public CreateCodeResponder execute() {
	
		Code code = new Code();
		String accessToken = UUID.randomUUID().toString().replace("-", "");
		code.setAccessToken(accessToken);
		code.setCode(Crypto.toSha256(accessToken));
		repo.save(code);

		CreateCodeResponder resp = new CreateCodeResponder(code);
		return resp;
	}

}
