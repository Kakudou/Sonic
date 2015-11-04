package util.json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class ReflectUtil
{

  /** Sans valeur. */
  public static final String NO_VALUE = "null";

  /** Contient les types Java basiques. */
  private static final Map<String, Class<?>> BASICS;

  /** Contient les types Java Date, Calendar. */
  private static final Map<String, Class<?>> TIMES;

  /** Contient les types Java List et Set. */
  private static final Map<String, Class<?>> COLLECTIONS;

  /** Contient les types Java Chaine de caractère. */
  private static final Map<String, Class<?>> CHAR_SEQUENCES;

  static
  {
    BASICS = generateBasics();
    TIMES = generateTimes();
    COLLECTIONS = generateCollections();
    CHAR_SEQUENCES = generateCharSequences();
  }

  private ReflectUtil()
  {

  }

  public static <T> T newInstance(Class<T> clazz)
  {
    T instance = null;
    int mod = clazz.getModifiers();
    if (Modifier.isAbstract(mod) || !clazz.isEnum())
    {
      try
      {
        instance = clazz.newInstance();
      } catch (InstantiationException | IllegalAccessException e)
      {
        e.printStackTrace();
      }
    }
    return instance;
  }

  @SuppressWarnings("unchecked")
  public static <T> T basicValueOf(Class<T> clazz, String value)
  {
    T valueOf = null;
    Class<?> basic = BASICS.get(clazz.getName());
    if (basic != null)
    {
      if (basic.isAssignableFrom(String.class))
      {
        valueOf = (T) value;
      } else
      {
        if (value == null || value.isEmpty() || NO_VALUE.equalsIgnoreCase(value))
        {
          value = "0";
        }
        try
        {
          Method method = basic.getMethod("valueOf", String.class);
          valueOf = (T) method.invoke(null, value);
        } catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
    return valueOf;
  }

  @SuppressWarnings("unchecked")
  public static <T> T timeValueOf(Class<T> clazz, String value)
  {
    T valueOf = null;
    long time = parseTime(value);
    if (time > 0)
    {
      if (clazz.isAssignableFrom(Date.class))
      {
        valueOf = (T) new Date(time);
      } else if (clazz.isAssignableFrom(Calendar.class))
      {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        valueOf = (T) cal;
      } else if (clazz.isAssignableFrom(GregorianCalendar.class))
      {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(time);
        valueOf = (T) cal;
      } else
      {
        System.out.println("UNPARSABLE TIME!");
      }
    }
    return valueOf;
  }

  public static <T extends Enum<T>> T enumValueOf(Class<T> clazz, String value)
  {
    T valueOf = Enum.valueOf(clazz, value);
    return valueOf;
  }

  public static <T> boolean isEnumExist(Class<T> clazz, String string)
  {
    boolean result = false;
    if (clazz.isEnum())
    {
      try
      {
        Method method = clazz.getMethod("values");
        Object[] enumValues = (Object[]) method.invoke(clazz);
        for (Object e : enumValues)
        {
          String value = (String) e.getClass().getMethod("name").invoke(e);
          if (value.equals(string))
          {
            result = true;
            break;
          }
        }
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    return result;
  }

  public static boolean isAssignableFromBasic(Class<?> clazz)
  {
    return BASICS.containsKey(clazz.getName());
  }

  public static boolean isAssignableFromTime(Class<?> clazz)
  {
    return TIMES.containsKey(clazz.getName());
  }

  public static boolean isAssignableFromCollection(Class<?> clazz)
  {
    return COLLECTIONS.containsKey(clazz.getName());
  }

  public static boolean isAssignableFromCharSequence(Class<?> clazz)
  {
    return CHAR_SEQUENCES.containsKey(clazz.getName());
  }

  public static <T> Class<?> getBasic(Class<T> clazz)
  {
    return BASICS.get(clazz.getName());
  }

  public static <T> Class<?> getTime(Class<T> clazz)
  {
    return TIMES.get(clazz.getName());
  }

  public static <T> Class<?> getCollection(Class<T> clazz)
  {
    return COLLECTIONS.get(clazz.getName());
  }

  public static <T> Class<?> getCharSequence(Class<T> clazz)
  {
    return CHAR_SEQUENCES.get(clazz.getName());
  }

  public static List<Field> getListFields(Class<?> clazz)
  {
    List<Field> list = new ArrayList<>();
    if (!clazz.isEnum())
    {
      Class<?> superClass = clazz;
      do
      {
        Field[] fields = superClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
        {
          int mod = fields[i].getModifiers();
          if (!(Modifier.isFinal(mod) || Modifier.isStatic(mod)))
          {
            if (!fields[i].isAccessible())
            {
              fields[i].setAccessible(true);
            }
            list.add(fields[i]);
          }
        }
        superClass = superClass.getSuperclass();
      } while (!superClass.isAssignableFrom(Object.class));
    }
    return list;
  }

  public static Map<String, Class<?>> getMapFields(Class<?> clazz)
  {
    Map<String, Class<?>> map = new HashMap<>();
    if (clazz != null && !clazz.isEnum())
    {
      Class<?> superClass = clazz;
      do
      {
        Field[] fields = superClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
        {
          int mod = fields[i].getModifiers();
          if (!Modifier.isFinal(mod) || !Modifier.isStatic(mod))
          {
            if (!fields[i].isAccessible())
            {
              fields[i].setAccessible(true);
            }
            map.put(fields[i].getName(), fields[i].getType());
          }
        }
        superClass = superClass.getSuperclass();
      } while (superClass != null);
    }
    return map;
  }

  private static Map<String, Class<?>> generateBasics()
  {
    Map<String, Class<?>> map = new HashMap<>();
    map.put(boolean.class.getName(), Boolean.class);
    map.put(Boolean.class.getName(), Boolean.class);
    map.put(byte.class.getName(), Byte.class);
    map.put(Byte.class.getName(), Byte.class);
    map.put(char.class.getName(), Character.class);
    map.put(Character.class.getName(), Character.class);
    map.put(short.class.getName(), Short.class);
    map.put(Short.class.getName(), Short.class);
    map.put(int.class.getName(), Integer.class);
    map.put(Integer.class.getName(), Integer.class);
    map.put(long.class.getName(), Long.class);
    map.put(Long.class.getName(), Long.class);
    map.put(float.class.getName(), Float.class);
    map.put(Float.class.getName(), Float.class);
    map.put(double.class.getName(), Double.class);
    map.put(Double.class.getName(), Double.class);
    map.put(String.class.getName(), String.class);
    return map;
  }

  private static Map<String, Class<?>> generateTimes()
  {
    Map<String, Class<?>> map = new HashMap<>();
    map.put(Date.class.getName(), Date.class);
    map.put(Calendar.class.getName(), Calendar.class);
    map.put(GregorianCalendar.class.getName(), GregorianCalendar.class);
    return map;
  }

  private static Map<String, Class<?>> generateCollections()
  {
    Map<String, Class<?>> map = new HashMap<>();
    map.put(List.class.getName(), ArrayList.class);
    map.put(Set.class.getName(), HashSet.class);
    return map;
  }

  private static Map<String, Class<?>> generateCharSequences()
  {
    Map<String, Class<?>> map = new HashMap<>();
    map.put(char.class.getName(), Character.class);
    map.put(Character.class.getName(), Character.class);
    map.put(String.class.getName(), String.class);
    map.put(StringBuilder.class.getName(), StringBuilder.class);
    return map;
  }

  private static long parseTime(String value)
  {
    long time = 0;
    Pattern regex = Pattern.compile(".*\\D.*");
    if (!regex.matcher(value).matches())
    {
      time = Long.parseLong(value);
    }
    return time;
  }

}
