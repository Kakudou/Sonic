package util.json;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Convertisseur JSON -> Object.
 * 
 */
public final class JsonHandler
{

  /** Json vide. */
  public static final ObjectNode EMPTY = Json.newObject();

  /** Node de Json sans valeur. */
  public static final String NO_VALUE = "null";

  /** Classe métier pour le traitement. */
  private final Class<?> businessClass;

  /** Json de contenant d'éventuelles erreurs. */
  private ObjectNode errors;

  public JsonHandler(Class<?> clazz)
  {
    businessClass = clazz;
  }

  public <T> JsonNode formatFlat(JsonNode json)
  {
    List<T> list = new ArrayList<>();
    for (JsonNode node : json.get("results"))
    {
      T model = parseFlat(node);
      list.add(model);
    }
    ObjectNode resultJson = Json.newObject();
    resultJson.put("filters", json.get("filters"));
    resultJson.put("nbResults", json.get("nbResults"));
    resultJson.put("results", Json.toJson(list));
    return resultJson;
  }

  public <T> JsonNode formatDeep(JsonNode json)
  {
    List<T> list = new ArrayList<>();
    for (JsonNode node : json.get("results"))
    {
      T model = parseDeep(node);
      list.add(model);
    }
    ObjectNode resultJson = Json.newObject();
    resultJson.put("filters", json.get("filters"));
    resultJson.put("nbResults", json.get("nbResults"));
    resultJson.put("results", Json.toJson(list));
    return resultJson;
  }

  /**
   * Extrait les données du json et le converti en un objet du modèle.
   * 
   * @param json
   *          Représentation d'un modèle sérialisé
   * @return Le model
   */
  @SuppressWarnings("unchecked")
  public <T> T parseDeep(JsonNode json)
  {
    errors = Json.newObject();
    T businessInstance = (T) ReflectUtil.newInstance(businessClass);
    return parseNodeDeep(businessInstance, json);
  }

  @SuppressWarnings("unchecked")
  public <T> T parseFlat(JsonNode json)
  {
    errors = Json.newObject();
    T businessInstance = (T) ReflectUtil.newInstance(businessClass);
    return parseNodeFlat(businessInstance, json);
  }

  /**
   * 
   * @return Le(s) erreur(s) suite à la convertion
   */
  public JsonNode getErrors()
  {
    return errors;
  }

  /**
   * Doit être appelé après la méthode 'parseDeep()' ou 'parseFlat()'.
   * 
   * @return 'TRUE' si la convertion est un succès
   */
  public boolean isValid()
  {
    return EMPTY.equals(errors);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private <T> T parseNodeDeep(T businessInstance, JsonNode json)
  {
    List<Field> fields = ReflectUtil.getListFields(businessInstance.getClass());
    for (Field f : fields)
    {
      Class type = f.getType();
      String name = f.getName();
      if (json.has(name))
      {
        String value = json.get(name).asText();
        if (!NO_VALUE.equalsIgnoreCase(value))
        {
          Object valueOf;
          if (type.isEnum())
          {
            valueOf = ReflectUtil.enumValueOf(type, value);
          } else if (ReflectUtil.isAssignableFromBasic(type))
          {
            valueOf = ReflectUtil.basicValueOf(type, value);
          } else if (ReflectUtil.isAssignableFromTime(type))
          {
            valueOf = ReflectUtil.timeValueOf(type, value);
          } else if (ReflectUtil.isAssignableFromCollection(type))
          {
            valueOf = collectionValueOf(f, json.get(name));
          } else
          {
            valueOf = businessValueOf(type, json.get(name));
          }
          try
          {
            if (valueOf != null)
            {
              f.set(businessInstance, valueOf);
            }
          } catch (Exception e)
          {
            e.printStackTrace();
          }
        }
      }
    }
    return businessInstance;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private <T> T parseNodeFlat(T businessInstance, JsonNode json)
  {
    List<Field> fields = ReflectUtil.getListFields(businessInstance.getClass());
    for (Field f : fields)
    {
      Class type = f.getType();
      String name = f.getName();
      String[] flatName = name.split("_");
      T value = searchNode(f, flatName, json);
      Object valueOf = null;
      if (type.isEnum())
      {
        valueOf = ReflectUtil.enumValueOf(type, String.valueOf(value));
      } else if (ReflectUtil.isAssignableFromBasic(type))
      {
        valueOf = ReflectUtil.basicValueOf(type, String.valueOf(value));
      } else if (ReflectUtil.isAssignableFromTime(type))
      {
        valueOf = ReflectUtil.timeValueOf(type, String.valueOf(value));
      } else if (ReflectUtil.isAssignableFromCollection(type))
      {
        valueOf = value;
      }
      try
      {
        if (valueOf != null)
        {
          f.set(businessInstance, valueOf);
        }
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    return businessInstance;
  }

  @SuppressWarnings({ "unchecked" })
  private <T> T searchNode(Field field, String[] flatName, JsonNode node)
  {
    T valueOf = null;
    boolean array = ReflectUtil.isAssignableFromCollection(field.getType());
    for (int i = 0; i < flatName.length; i++)
    {
      String name = flatName[i];
      if (node != null)
      {
        if (node.has(name))
        {
          node = node.get(name);
        }
        if (array)
        {
          if (node.isArray())
          {
            valueOf = (T) collectionNodeValueOf(field, node, flatName);
          }
        } else
        {
          valueOf = (T) node.asText();
        }
      }
    }
    return valueOf;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private <T> Collection collectionValueOf(Field field, JsonNode node)
  {
    Class<?> clazz = ReflectUtil.getCollection(field.getType());
    Collection collection = (Collection) ReflectUtil.newInstance(clazz);
    if (collection != null)
    {
      ParameterizedType genericTypeList = (ParameterizedType) field.getGenericType();
      Class paramTypeList = (Class<T>) genericTypeList.getActualTypeArguments()[0];
      for (JsonNode n : node)
      {
        T valueOf;
        if (paramTypeList.isEnum())
        {
          valueOf = (T) ReflectUtil.enumValueOf(paramTypeList, n.asText());
        } else
        {
          T instance = (T) ReflectUtil.newInstance(paramTypeList);
          valueOf = (T) parseNodeDeep(instance, n);
        }
        collection.add(valueOf);
      }
    }
    return collection;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private <T> Collection collectionNodeValueOf(Field field, JsonNode node, String[] deepName)
  {
    Class<?> clazz = ReflectUtil.getCollection(field.getType());
    Collection collection = (Collection) ReflectUtil.newInstance(clazz);
    if (collection != null)
    {
      ParameterizedType genericTypeList = (ParameterizedType) field.getGenericType();
      Class paramTypeList = (Class<T>) genericTypeList.getActualTypeArguments()[0];
      for (JsonNode n : node)
      {
        JsonNode jn;
        if (deepName.length > 1)
        {
          jn = n.get(deepName[deepName.length - 1]);
        } else
        {
          jn = n;
        }
        T valueOf = null;
        if (paramTypeList.isEnum())
        {
          if (jn != null)
          {
            valueOf = (T) ReflectUtil.enumValueOf(paramTypeList, jn.asText());
          }
        } else
        {
          if (jn != null)
          {
            valueOf = (T) ReflectUtil.basicValueOf(paramTypeList, jn.asText());
          }
        }
        collection.add(valueOf);
      }
    }
    return collection;
  }

  private <T> T businessValueOf(Class<T> type, JsonNode json)
  {
    T businessInsance = ReflectUtil.newInstance(type);
    T valueOf = parseNodeDeep(businessInsance, json);
    return valueOf;
  }

}
