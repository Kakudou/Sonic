package sonic.application.usecase;

import sonic.application.repository.ApplicationRepository;

public class SaveApplicationUseCase
{
  
  private static ApplicationRepository repo;
  
  public SaveApplicationUseCase()
  {
  }
  
  public static SaveApplicationUseCase getInstance(ApplicationRepository repository)
  {
    repo = repository;
    return Holder.SINGLETON;
  }
  
  private static class Holder
  {
    private static final SaveApplicationUseCase SINGLETON = new SaveApplicationUseCase();
  }
//  
//  public SaveApplicationResponder execute(SaveApplicationRequester req)
//  {
//    Application application = new Application();
//    
//    Long id = req.getId();
//    String keyId = req.getKeyId();
//    String keySecret = req.getKeySecret();
//    String name = req.getName();
//    String redirectUri = req.getRedirectUri();
//    Boolean xAccess = req.getXAccess();
//    
//    Map<FournisseurIdentite, String> scopesByFi = req.getScopesByFi();
//    String timeOut = req.getTimeOut();
//    if (xAccess){
//      scopesByFi.values().clear();
//    }
//    
//    application.setId(id);
//    application.setKeyId(keyId);
//    application.setKeySecret(keySecret);
//    application.setName(name);
//    application.setRedirectUri(redirectUri);
//    application.setScopesByFi(scopesByFi);
//    application.setTimeOut(timeOut);
//    application.setxAccess(xAccess);
//    
//    repo.save(application);
//    SaveApplicationResponder resp = new SaveApplicationResponder(application);
//    
//    return resp;
//  }
  

}
