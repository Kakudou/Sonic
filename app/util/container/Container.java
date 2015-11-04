package util.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Conteneur de service abstrait. Les classes héritantes de celle-ci pourront
 * fourni une instance unique d'un service bien particulier. Pour fonctionner
 * correctement un singleton doit être implémenter dans les classes filles.
 * 
 */
public abstract class Container
{

  /** Stockage des services. */
  protected final Map<String, Object> bind;

  /**
   * Constructeur.
   */
  protected Container()
  {
    // Ne doit pas être instancier de manière anomyme.
    bind = new HashMap<>();
  }

  /**
   * Initialisation du conteneur, utiliser la méthode add() pour ajouter des
   * instances uniques dans ce conteneur.
   */
  protected abstract void initBinding();

  /**
   * Ajouter une instance dans le conteneur.
   * 
   * @param impl
   *          Instance de service
   */
  protected final void add(Object impl)
  {
    Class<?>[] itfs = impl.getClass().getInterfaces();
    if (itfs.length == 0)
    {
      bind.put(impl.getClass().getName(), impl);
    } else
    {
      for (int i = 0; i < itfs.length; i++)
      {
        bind.put(itfs[i].getName(), impl);
      }
    }
  }

  /**
   * 
   * @param clazz
   *          Classe ou interface représentant l'objet stocké
   * 
   * @return Un objet stocké dans ce conteneur
   */
  @SuppressWarnings("unchecked")
  protected <T> T getBind(Class<T> clazz)
  {
    T found = (T) bind.get(clazz.getName());
    if (found == null)
    {
      initBinding();
      found = (T) bind.get(clazz.getName());
      if (found == null)
      {
        String msg = String.format("[%s] NOT FOUND!", clazz.getName());
        throw new IllegalArgumentException(msg);
      }
    }
    return found;
  }

  /**
   * 
   * @return Tous les services de ce conteneur
   */
  protected Collection<?> getListService()
  {
    if (bind.isEmpty())
    {
      initBinding();
    }
    return bind.values();
  }

}
