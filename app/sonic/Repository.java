package sonic;

import java.util.List;
import java.util.Map;

/**
 * Tous les repositories de l'application devront suivre ce contrat. Les classes
 * implétants cette interface possédront les méthodes de CRUD.
 * 
 * @param <E>
 *          Model
 */
public interface Repository<E>
{

  /**
   * Recherche spécifique d'un élément.
   * 
   * @param id
   *          Clef primaire
   * @return L'entité trouvée, sinon 'NULL'
   */
  E find(Object id);

  /**
   * Recherche global de tous les éléments.
   * 
   * @return Toutes les entités
   */
  List<E> find();

  /**
   * Recherche global de tous les éléments triés par ordre alphabétique.
   * Plusieurs critères de tri possible.
   * 
   * @param orderBy
   *          Noms des colonnes pour un tri alphabétique type ASC
   * @return Toutes les entités triées
   */
  List<E> find(String... orderBy);

  /**
   * Recherche simple par valeur.
   * 
   * @param field
   *          Nom de l'attribut
   * @param value
   *          Valeur pour cette colonne
   * @return Les entités trouvées pour ce filtre
   */
  List<E> find(String field, Object value);

  /**
   * Recherche simple par valeur et tri par ordre alphabétique. Plusieurs
   * citères de tri possible.
   * 
   * @param field
   *          Nom de l'attribut
   * @param value
   *          Valeur pour cette colonne
   * @param orderBy
   *          Noms des colonnes pour un tri alphabétique type ASC
   * @return Les entités trouvées pour ce filtre et triées
   */
  List<E> find(String field, Object value, String... orderBy);

  /**
   * Recherche simple par plusieurs valeur
   * @param valueByField
   *          key = Nom de l'attribut
   *          value = valeur pour cette attribut
   * @return Les entités trouvées pour ces valeurs
   */
  List<E> find(Map<String, String> valueByField);
  
  /**
   * Recherche complexe avec filtre.
   * 
   * @param filter
   *          Filtre de recherche complexe avec pagination et index
   * @return Les entités trouvées pour ce filtre
   */
  List<E> find(Filter filter);

  /**
   * Vérifier l'existence d'un élément.
   * 
   * @param model
   *          Instance métier
   * @return 'TRUE' si l'objet existe déjà
   */
  boolean contains(E model);

  /**
   * Récupérer le nombre d'élément global.
   * 
   * @return Le nombre d'occurence
   */
  long size();


  /**
   * Récupérer le nombre d'élément avec un filtre complexe.
   * 
   * @param filter
   *          Filtre de recherche complexe
   * @return Le nombre d'occurence pour ce filtre
   */
  long size(Filter filter);

  /**
   * Sauvegarder une entité.
   * 
   * @param model
   *          Instance métier
   */
  void save(E model);

  /**
   * Supprimer une entité.
   * 
   * @param model
   */
  void remove(E model);

  /**
   * Suppression simple par valeur.
   * 
   * @param field
   *          Nom de l'attribut
   * @param value
   *          Valeur pour cette colonne
   */
  void remove(String field, Object value);

  /**
   * Effacer toutes les entités.
   */
  void clear();

  /**
   * Rafraichir en mémoire une entité.
   * 
   * @param model
   *          Instance métier
   */
  void refresh(E model);


}
