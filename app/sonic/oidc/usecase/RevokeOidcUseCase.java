package sonic.oidc.usecase;

import sonic.oidc.code.repository.CodeRepository;
import sonic.oidc.idtoken.repository.IdTokenRepository;
import sonic.oidc.requester.RevokeOidcRequester;
import sonic.oidc.token.repository.TokenRepository;

public class RevokeOidcUseCase
{
  private static CodeRepository codeRepo;
  private static TokenRepository tokenRepo;
  private static IdTokenRepository idTokRepo;
  
  public RevokeOidcUseCase()
  {
  }
  
  public static RevokeOidcUseCase getInstance(CodeRepository codeRepository, TokenRepository tokenRepository,
      IdTokenRepository idTokRepository)
  {
    codeRepo = codeRepository;
    tokenRepo = tokenRepository;
    idTokRepo = idTokRepository;
    return Holder.SINGLETON;
  }
  
  private static class Holder
  {
    private static final RevokeOidcUseCase SINGLETON = new RevokeOidcUseCase();
  }
  
  public void execute(RevokeOidcRequester revokReq)
  {
    String accessToken = revokReq.getAccessToken();
    codeRepo.remove("accessToken", accessToken);
    tokenRepo.remove("accessToken", accessToken);
    idTokRepo.remove("accessToken", accessToken);
  }
}
