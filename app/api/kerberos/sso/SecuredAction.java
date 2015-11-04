package api.kerberos.sso;

import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.xerces.impl.dv.util.Base64;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Content;

public class SecuredAction extends Action<Secured>
{
  final SecuredLoginConfiguration securedLoginConfiguration;
  private LoginContext loginContext;
  
  public SecuredAction()
  {
    securedLoginConfiguration = new SecuredLoginConfiguration();
    Set<Principal> principals = new HashSet<>();
    String principal = securedLoginConfiguration.getPrincipal();
    if (principal == null) {
      throw new NullPointerException("Principal can't be null.");
    }
    principals.add(new KerberosPrincipal(principal));
    Subject subject = new Subject(false, principals, new HashSet<>(), new HashSet<>());
    try {
      loginContext = new LoginContext("", subject, null, securedLoginConfiguration);
      loginContext.login();
    } catch (LoginException e) {
      e.printStackTrace();
    }
  }
  
  private Return before(final Http.Context ctx)
  {
    SecuredInf securedInf;
    try {
      securedInf = configuration.INF().newInstance();
    } catch (InstantiationException e) {
      e.printStackTrace();
      return new Return(false);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      return new Return(false);
    }
    if (securedInf == null) return new Return(badRequest("An error has occurred!!!"));
    if (securedInf.isLoggedIn()) {
      return new Return(true);
    } else if (ctx.request().getHeader("Authorization") != null) {
      try {
        Subject serverSubject = loginContext.getSubject();
        GSSContext context = Subject.doAs(serverSubject, new PrivilegedExceptionAction<GSSContext>()
        {
          
          @Override
          public GSSContext run() throws Exception
          {
            GSSManager gssManager = GSSManager.getInstance();
            
            Oid spnegoOid = new Oid("1.3.6.1.5.5.2");
            GSSName serverName = gssManager.createName(securedLoginConfiguration.getPrincipal(),
                GSSName.NT_HOSTBASED_SERVICE);
            
            GSSCredential serverCreds = gssManager.createCredential(serverName, GSSCredential.DEFAULT_LIFETIME,
                spnegoOid, GSSCredential.ACCEPT_ONLY);
            
            GSSContext context = gssManager.createContext(serverCreds);
            String authorization = ctx.request().getHeader("Authorization");
            authorization = authorization.substring(authorization.indexOf(" ") + 1);
            
            byte[] authorizationBytes = Base64.decode(authorization);
            
            context.acceptSecContext(authorizationBytes, 0, authorizationBytes.length);
            
            return context;
          }
        });
        if (context.isEstablished()) {
          securedInf.userLoggedIn(context.getSrcName().toString());
          if (securedInf.isLoggedIn()) {
            return new Return(true);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    ctx.response().setHeader(Http.HeaderNames.WWW_AUTHENTICATE, "Negotiate");
    
    Content html = securedInf.getHTML();
    if (html == null) {
        html = new Content() {
            @Override
            public String body() {
                return "Unauthorized";
            }

            @Override
            public String contentType() {
                return "text/html";
            }
        };
    }
    return new Return(unauthorized(html).as("text/html"));
  }
  
  private void after(Http.Context ctx)
  {
    
  }
  
  @Override
  public F.Promise<Result> call(Http.Context ctx) throws Throwable
  {
    try {
      Return before = before(ctx);
      if (!before.ok) {
        return F.Promise.pure(before.result);
      }
      F.Promise<Result> result = delegate.call(ctx);
      after(ctx);
      return result;
    } catch (RuntimeException e) {
      throw e;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }
  
  private class Return
  {
    public final boolean ok;
    public Result result;
    
    private Return(boolean ok)
    {
      this.ok = ok;
    }
    
    private Return(Result result)
    {
      this.ok = false;
      this.result = result;
    }
  }
  
}
