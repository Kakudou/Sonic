package sonic;

import java.util.Map;

import util.repository.Operateur;

public interface Filter
{
  
  /** Clef de filtre pour définir la taille de la page. */
  static final String PAGE_SIZE = "pageSize";
  
  /** Clef de filtre pour définir l'index de la page. */
  static final String INDEX = "index";
  
  /** Clef de filtre pour définir la colonne de tri. */
  static final String ORDER = "order";
  
  /** Clef de filtre pour définir un tri sur l'ID. */
  static final String DEFAULT_SORT = "id";
  
  /** Clef de filtre pour définir l'odre de tri. */
  static final String BY = "by";
  
  /** Tri ascendant. */
  static final String ASC = "asc";
  
  /** Tri descendant. */
  static final String DESC = "desc";
  
  int getFirstResult();
  
  int getMaxResult();
  
  Map<String, Map<Operateur, String>> getFormatedQuery();
  
  String[] getOrderBy();
  
  String getSort();
  
  Map<String, String[]> getRawQuery();
  
}
