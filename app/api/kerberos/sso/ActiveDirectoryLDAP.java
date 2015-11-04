package api.kerberos.sso;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public final class ActiveDirectoryLDAP
{
	
	
  private static final String CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
  private static final String AUTHENTICATION = "simple";
  private static final String URL = "ldap://";
  private static final String ldap_dn = "";
  private static final String ldap_pw = "";
  private static final String base_dn = "";
  
  private static DirContext seConnecter()
  {
    
    Hashtable<String, String> env1 = new Hashtable<String, String>();
    env1.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
    env1.put(Context.PROVIDER_URL, URL);
    env1.put(Context.SECURITY_AUTHENTICATION, AUTHENTICATION);
    env1.put(Context.SECURITY_PRINCIPAL, ldap_dn);
    env1.put(Context.SECURITY_CREDENTIALS, ldap_pw);
    
    DirContext ctx  = null;
    
    try {
		ctx = new InitialDirContext(env1);
	} catch (NamingException e) {
	}

    
    return ctx;
  }
    
  public static String getSamAccountNameFromLdap(String uid)
  {
    try {
      DirContext context = seConnecter();
      SearchControls ctrl = new SearchControls();
      ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
      ctrl.setReturningAttributes(new String[] { "sAMAccountName" });
      NamingEnumeration<SearchResult> results;
      results = context.search(base_dn, "(name=" + uid + ")", ctrl);
      SearchResult result = results.next();
      Attributes entry = result.getAttributes();
      context.close();
      return entry.get("sAMAccountName").get().toString();
    } catch (NamingException e) {
    	System.out.println("NamingException: " + uid);
    }
    return null;
  }
  
}
