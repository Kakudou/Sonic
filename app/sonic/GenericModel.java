package sonic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Entité générique abstraite pour le métier. Elle redéfinie equals(),
 * hashCode() et toString() pour toutes ces classes filles.
 * 
 */
public abstract class GenericModel implements Model
{

  /** Numéro de série générique. */
  private static final long serialVersionUID = 1L;

  /** Clef primaire. */
  protected Long id;

  /**
   * Constructeur.
   */
  protected GenericModel()
  {
    // Ne doit pas être instancier de manière anomyme.
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GenericModel other = (GenericModel) obj;
    if (id == null)
    {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public final String toString()
  {
    final String exclude = "id";
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Class<?> superClazz = getClass();
    String clazzName = superClazz.getSimpleName();
    StringBuilder sb = new StringBuilder();
    sb.append(clazzName);
    sb.append("[");
    sb.append(id);
    sb.append("](");
    String attr;
    do
    {
      for (Field f : superClazz.getDeclaredFields())
      {
        attr = f.getName();
        if (!Modifier.isStatic(f.getModifiers()))
        {
          if (!attr.equalsIgnoreCase(exclude))
          {
            if (!f.isAccessible())
            {
              f.setAccessible(true);
            }
            sb.append(attr);
            try
            {
              Object data = f.get(this);
              if (data instanceof Model)
              {
                Model model = (Model) data;
                sb.append("[");
                sb.append(model.getId());
                sb.append("]");
              } else if (data instanceof Date)
              {
                sb.append("=");
                sb.append(dateFormat.format(data));
              } else if (data instanceof Calendar)
              {
                Calendar cal = (Calendar) data;
                sb.append("=");
                sb.append(dateFormat.format(cal.getTime()));
              } else
              {
                sb.append("=");
                sb.append(data);
              }
            } catch (IllegalArgumentException | IllegalAccessException e)
            {
              sb.append("???");
              e.printStackTrace();
            }
            sb.append(", ");
          }
        }
      }
      superClazz = superClazz.getSuperclass();
    } while (superClazz != null);
    sb.delete(sb.length() - 2, sb.length());
    sb.append(")");
    return sb.toString();
  }

  @Override
  public Long getId()
  {
    return id;
  }

  @Override
  public void setId(Long id)
  {
    this.id = id;
  }
 
}
