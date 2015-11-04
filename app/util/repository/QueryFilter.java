package util.repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import sonic.Filter;
import util.json.ReflectUtil;

public final class QueryFilter implements Filter
{
  public static final int DEFAULT_PAGE_SIZE = 10;
  
  public static final int DEFAULT_INDEX = 1;
  
  public static final String[] DEFAULT_ORDER = { "id" };
  
  public static final String DEFAULT_SORT = "desc";
  
  private final Map<String, String[]> rawQuery;
  
  public QueryFilter(Map<String, String[]> rawQuery)
  {
    this.rawQuery = rawQuery;
  }
  
  @Override
  public Map<String, String[]> getRawQuery()
  {
    return rawQuery;
  }
  
  @Override
  public String[] getOrderBy()
  {
    String[] q = rawQuery.get(ORDER);
    return (q != null && !q[0].isEmpty()) ? q : DEFAULT_ORDER;
  }
  
  @Override
  public String getSort()
  {
    String[] q = rawQuery.get(BY);
    return q != null ? q[0] : DEFAULT_SORT;
  }
  
  @Override
  public int getFirstResult()
  {
    int i = DEFAULT_INDEX;
    String[] idx = rawQuery.get(INDEX);
    if (idx != null) {
      i = parseInteger(idx[0]);
    }
    int j = DEFAULT_PAGE_SIZE;
    String[] ps = rawQuery.get(PAGE_SIZE);
    if (ps != null) {
      j = parseInteger(ps[0]);
    }
    int firstResult = (i - 1) * j;
    return firstResult < 0 ? 0 : firstResult;
  }
  
  @Override
  public int getMaxResult()
  {
    int i = -1;
    String[] q = rawQuery.get(PAGE_SIZE);
    if (q != null) {
      i = parseInteger(q[0]);
    }
    return i < 0 ? DEFAULT_PAGE_SIZE : i;
  }
  
  private int parseInteger(String value)
  {
    int n = -1;
    if (value != null && value.matches("\\d+")) {
      n = Integer.parseInt(value);
    }
    return n;
  }
  
  @Override
  public Map<String, Map<Operateur, String>> getFormatedQuery()
  {
    Map<String, Map<Operateur, String>> formatedQuery = new HashMap<String, Map<Operateur, String>>();
    
    Map<String, String[]> raw = new HashMap<>();
    raw.putAll(rawQuery);
    raw.remove(INDEX);
    raw.remove(PAGE_SIZE);
    raw.remove(ORDER);
    raw.remove(BY);
    
    for (Entry<String, String[]> entry : raw.entrySet()) {
      String key = entry.getKey();
      String[] tuples = entry.getValue();
      
      Map<Operateur, String> tupledMap = new HashMap<Operateur, String>();
      for (String tuple : tuples) {
        String[] split = tuple.split(",");
        if (split.length == 2) {
          String ope = split[0];
          if (ReflectUtil.isEnumExist(Operateur.class, ope.toUpperCase())) {
            Operateur operateur = Operateur.valueOf(ope.toUpperCase());
            String valeur = split[1];
            tupledMap.put(operateur, valeur);
          }
        }else if(split.length>2){
          String ope = split[0];
          if (ReflectUtil.isEnumExist(Operateur.class, ope.toUpperCase())) {
            Operateur operateur = Operateur.valueOf(ope.toUpperCase());
            if(Operateur.NIN.equals(operateur) || Operateur.IN.equals(operateur)){
              List<String> lists = Arrays.asList(split);
              String toString =lists.subList(1, lists.size()).toString();
              String valeur = toString.substring(1, toString.length()-1).replaceAll(" ", "");
              tupledMap.put(operateur, valeur);
            }
          }
        }
      }
      formatedQuery.put(key, tupledMap);
    }
    
    return formatedQuery;
  }
  
}
