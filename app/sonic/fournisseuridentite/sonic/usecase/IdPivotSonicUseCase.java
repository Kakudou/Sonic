package sonic.fournisseuridentite.sonic.usecase;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sonic.application.model.Application;
import sonic.application.repository.ApplicationRepository;
import sonic.fournisseuridentite.sonic.requester.IdPivotSonicRequester;
import sonic.oidc.responder.IdPivotResponder;

public class IdPivotSonicUseCase {

	private static ApplicationRepository repo;
	
	public IdPivotSonicUseCase() {
	}

	public static IdPivotSonicUseCase getInstance(ApplicationRepository repository) {
		repo = repository;
		return Holder.SINGLETON;
	}

	private static class Holder {
		private static final IdPivotSonicUseCase SINGLETON = new IdPivotSonicUseCase();
	}

	public IdPivotResponder execute(IdPivotSonicRequester ipReq) {
		
		String keyId = ipReq.getKeyId();
		String keySecret = ipReq.getKeySecret();
		String name = ipReq.getName();
		List<String> scopesSonic = ipReq.getScopesSonic();
		
		Map<String, String> valueOfField = new HashMap<>();
		valueOfField.put("keyId", keyId);
		valueOfField.put("keySecret", keySecret);
	    List<Application> apps = repo.find(valueOfField);
		
        Map<String, String> scopesData = new HashMap<>();
        if (!apps.isEmpty() && !scopesSonic.isEmpty()){
        	Application app = apps.get(0);
	        for (String scope : scopesSonic) {
	          String value = "";
	          try {
	        	  Field actualField = null;
	        	   for(Field field : Application.class.getDeclaredFields()){
	        		   if(field.getName().equalsIgnoreCase(scope)){
	        			   actualField = field;
	        			   break;
	        		   }
	        	   }
	        	   if(actualField != null){
	        		   actualField.setAccessible(true);
	        		   value = actualField.get(app).toString();
	        	   }
	          } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
	        	  e.printStackTrace();
	          }
	          scopesData.put(scope, value);
	        }
        }
        
        IdPivotResponder resp = new IdPivotResponder("", keyId, name, "", scopesData);

		return resp;
	}
}
