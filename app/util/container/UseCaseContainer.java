package util.container;

import java.util.Collection;

import sonic.application.repository.ApplicationRepository;
import sonic.application.usecase.GetApplicationUseCase;
import sonic.fournisseuridentite.sonic.usecase.IdPivotSonicUseCase;
import sonic.oidc.code.repository.CodeRepository;
import sonic.oidc.code.usecase.CreateCodeUseCase;
import sonic.oidc.code.usecase.GetCodeUseCase;
import sonic.oidc.idtoken.repository.IdTokenRepository;
import sonic.oidc.idtoken.usecase.CreateIdTokenUseCase;
import sonic.oidc.idtoken.usecase.GetIdTokenUseCase;
import sonic.oidc.token.repository.TokenRepository;
import sonic.oidc.token.usecase.CreateTokenUseCase;
import sonic.oidc.token.usecase.GetTokenUseCase;
import sonic.oidc.usecase.RevokeExpireOidcUseCase;
import sonic.oidc.usecase.RevokeOidcUseCase;

public final class UseCaseContainer extends Container
{
  
  private UseCaseContainer()
  {
    super();
  }
  
  @Override
  protected void initBinding()
  {
    add(GetApplicationUseCase.getInstance(RepositoryContainer.get(ApplicationRepository.class)));
    add(GetCodeUseCase.getInstance(RepositoryContainer.get(CodeRepository.class)));
    add(GetIdTokenUseCase.getInstance(RepositoryContainer.get(IdTokenRepository.class)));
    add(GetTokenUseCase.getInstance(RepositoryContainer.get(TokenRepository.class)));
    add(RevokeOidcUseCase.getInstance(RepositoryContainer.get(CodeRepository.class), RepositoryContainer.get(TokenRepository.class), RepositoryContainer.get(IdTokenRepository.class)));
    add(CreateCodeUseCase.getInstance(RepositoryContainer.get(CodeRepository.class)));
    add(CreateIdTokenUseCase.getInstance(RepositoryContainer.get(IdTokenRepository.class)));
    add(CreateTokenUseCase.getInstance(RepositoryContainer.get(TokenRepository.class)));
    add(IdPivotSonicUseCase.getInstance(RepositoryContainer.get(ApplicationRepository.class)));
    add(RevokeExpireOidcUseCase.getInstance(RepositoryContainer.get(CodeRepository.class), RepositoryContainer.get(TokenRepository.class), RepositoryContainer.get(IdTokenRepository.class)));
  }
  
  public static <T> T get(Class<T> repository)
  {
    return Holder.SINGLETON.getBind(repository);
  }
  
  public static Collection<?> getRepositories()
  {
    return Holder.SINGLETON.getListService();
  }
  
  private static class Holder
  {
    private static final UseCaseContainer SINGLETON = new UseCaseContainer();
  }
  
}
