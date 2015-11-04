package sonic.oidc.code.usecase;

import java.util.HashMap;
import java.util.List;

import sonic.oidc.code.model.Code;
import sonic.oidc.code.repository.CodeRepository;
import sonic.oidc.code.requester.GetCodeRequester;
import sonic.oidc.code.responder.GetCodeResponder;

public class GetCodeUseCase {
	private static CodeRepository repo;

	public GetCodeUseCase() {
	}

	public static GetCodeUseCase getInstance(CodeRepository repository) {
		repo = repository;
		return Holder.SINGLETON;
	}

	private static class Holder {
		private static final GetCodeUseCase SINGLETON = new GetCodeUseCase();
	}

	public GetCodeResponder execute(GetCodeRequester req) {
		HashMap<String, String> valueByField = req.getValueByField();

		List<Code> results = repo.find(valueByField);

		Code result = null;
		if (!results.isEmpty()) {
			result = results.get(0);
		}

		GetCodeResponder resp = new GetCodeResponder(result);
		return resp;
	}

}
