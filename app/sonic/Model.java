package sonic;

import java.io.Serializable;

/**
 * Toutes les entités métiers de l'application devront suivre ce contrat. Les
 * classes implémentants cette interface posséderont alors les accesseurs de
 * leur clef primaire.
 * 
 */
public interface Model extends Serializable
{

  /**
   * 
   * @return La clef primaire
   */
  Long getId();

  /**
   * Modifier la clef primaire.
   * 
   * @param id
   *          Nouvelle clef
   */
  void setId(Long id);
}
