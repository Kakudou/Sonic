package api.repository;

import sonic.oidc.code.model.Code;
import sonic.oidc.code.repository.CodeRepository;
import util.repository.AbstractRepository;

public class CodeRepositoryImpl extends AbstractRepository<Code> implements CodeRepository
{
  
  private CodeRepositoryImpl()
  {
    super();
  }
  
  public static CodeRepository getInstance()
  {
    return Holder.SINGLETON;
  }
  
  private static class Holder
  {
    private static final CodeRepository SINGLETON = new CodeRepositoryImpl();
  }
  
}