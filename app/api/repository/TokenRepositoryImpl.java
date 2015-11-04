package api.repository;

import sonic.oidc.token.model.Token;
import sonic.oidc.token.repository.TokenRepository;
import util.repository.AbstractRepository;

public class TokenRepositoryImpl extends AbstractRepository<Token> implements TokenRepository
{
  
  private TokenRepositoryImpl()
  {
    super();
  }
  
  public static TokenRepository getInstance()
  {
    return Holder.SINGLETON;
  }
  
  private static class Holder
  {
    private static final TokenRepository SINGLETON = new TokenRepositoryImpl();
  }
 
}