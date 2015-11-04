package api.repository;

import sonic.application.model.Application;
import sonic.application.repository.ApplicationRepository;
import util.repository.AbstractRepository;

public class ApplicationRepositoryImpl extends AbstractRepository<Application> implements ApplicationRepository
{
  
  private ApplicationRepositoryImpl()
  {
    super();
  }
  
  public static ApplicationRepository getInstance()
  {
    return Holder.SINGLETON;
  }
  
  private static class Holder
  {
    private static final ApplicationRepository SINGLETON = new ApplicationRepositoryImpl();
  }
 
}