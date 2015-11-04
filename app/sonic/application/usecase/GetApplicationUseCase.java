package sonic.application.usecase;

import java.util.HashMap;
import java.util.List;

import sonic.application.model.Application;
import sonic.application.repository.ApplicationRepository;
import sonic.application.requester.GetApplicationRequester;
import sonic.application.responder.GetApplicationResponder;

public class GetApplicationUseCase {
	private static ApplicationRepository repo;

	public GetApplicationUseCase() {
	}

	public static GetApplicationUseCase getInstance(
			ApplicationRepository repository) {
		repo = repository;
		return Holder.SINGLETON;
	}

	private static class Holder {
		private static final GetApplicationUseCase SINGLETON = new GetApplicationUseCase();
	}

	public GetApplicationResponder execute(GetApplicationRequester req) {
		HashMap<String, String> valueByField = req.getValueByField();

		List<Application> results = repo.find(valueByField);

		Application result = null;
		if (!results.isEmpty()) {
			result = results.get(0);
		}

		GetApplicationResponder resp = new GetApplicationResponder(result);
		return resp;
	}

}
