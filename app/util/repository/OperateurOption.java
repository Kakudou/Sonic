package util.repository;

import java.util.HashMap;
import java.util.Map;

import play.i18n.Messages;

public final class OperateurOption
{
  public static Map<String, String> getList()
  {
    Map<String, String> options = new HashMap<String, String>();
    for (Operateur operateur : Operateur.values())
    {
      options.put(operateur.name(), Messages.get("f."+operateur.name()));
    }

    return options;
  }
  
  public static String getFirst(){
    return Operateur.EQ.toString();
  }
}

