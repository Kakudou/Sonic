package util.repository;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import play.db.jpa.JPA;
import play.libs.F.Function0;
import sonic.Filter;
import sonic.Model;
import sonic.Repository;
import util.json.ReflectUtil;

/**
 * Repository générique abstrait pour les méthodes de CRUD.
 * 
 * @param <E> Model
 */
public abstract class AbstractRepository<E extends Model> implements Repository<E>
{
  
  protected static final String NO_VALUE = new String();
  
  /** Type paramétré avec générique. */
  protected Class<E> paramType;
  
  @SuppressWarnings("unchecked")
  protected AbstractRepository()
  {
    ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
    paramType = (Class<E>) type.getActualTypeArguments()[0];
  }
  
  protected AbstractRepository(Class<E> paramType)
  {
    this.paramType = paramType;
  }
  
  @Override
  public E find(final Object id)
  {
    Function0<E> function = new Function0<E>()
    {
      @Override
      public E apply() throws Throwable
      {
        E result;
        if (id != null && !id.toString().isEmpty()) {
          long trueID = parseLong(id.toString());
          result = JPA.em().find(paramType, trueID);
        } else {
          result = null;
        }
        return result;
      }
    };
    return doInTransaction(function);
  }
  
  @Override
  public List<E> find()
  {
    return find(new String[0]);
  }
  
  @Override
  public List<E> find(final String... orderBy)
  {
    Function0<List<E>> function = new Function0<List<E>>()
    {
      
      @Override
      public List<E> apply() throws Throwable
      {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<E> c = cb.createQuery(paramType);
        Root<E> from = c.from(paramType);
        addSort(cb, c, from, orderBy);
        TypedQuery<E> query = JPA.em().createQuery(c);
        return query.getResultList();
      }
    };
    return doInTransaction(function);
  }
  
  @Override
  public List<E> find(String field, Object value)
  {
    return find(field, value, new String[0]);
  }
  
  @Override
  public List<E> find(final String field, final Object value, final String... orderBy)
  {
    Function0<List<E>> function = new Function0<List<E>>()
    {
      
      @Override
      public List<E> apply() throws Throwable
      {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<E> c = cb.createQuery(paramType);
        Root<E> from = c.from(paramType);
        Predicate restriction = cb.equal(from.get(field), value);
        c.where(restriction);
        addSort(cb, c, from, orderBy);
        TypedQuery<E> query = JPA.em().createQuery(c);
        
        List<E> list = query.getResultList();
        return list;
      }
    };
    return doInTransaction(function);
  }
  
  @Override
  public List<E> find(final Map<String, String> valueByField)
  {
    Function0<List<E>> function = new Function0<List<E>>()
        {
      
      @Override
      public List<E> apply() throws Throwable
      {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<E> c = cb.createQuery(paramType);
        Root<E> from = c.from(paramType);
          List<Predicate> restrictions = new ArrayList<>();

            for (Entry<String, String> entry : valueByField.entrySet())
            {
              String field = entry.getKey();
              Object value = entry.getValue();
              Predicate restriction = cb.equal(from.get(field), value);
              restrictions.add(restriction);
            }
            c.where(restrictions.toArray(new Predicate[restrictions.size()]));
        
        addSort(cb, c, from, new String[0]);
        TypedQuery<E> query = JPA.em().createQuery(c);
        List<E> list = query.getResultList();
        return list;
      }
        };
        return doInTransaction(function);
  }
  
  @Override
  public List<E> find(final Filter filter)
  {
    Function0<List<E>> function = new Function0<List<E>>()
    {
      @Override
      public List<E> apply() throws Throwable
      {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<E> query = cb.createQuery(paramType);
        Root<E> from = query.from(paramType);
        List<Predicate> predicates = getPredicates(cb, from, filter);
        
        addSort(cb, query, from, filter.getOrderBy()[0], filter.getSort());
        
        if (predicates != null && !predicates.isEmpty()) {
          query.where(predicates.toArray(new Predicate[predicates.size()]));
        }
        
        TypedQuery<E> tq = JPA.em().createQuery(query);
        
        tq.setFirstResult(filter.getFirstResult());
        tq.setMaxResults(filter.getMaxResult());
        
        List<E> list = tq.getResultList();
        
//        String q = tq.unwrap(org.hibernate.Query.class).getQueryString();
//        System.out.println("================================" + q);
        return list;
      }
    };
    return doInTransaction(function);
  }
  
  @Override
  public boolean contains(E model)
  {
    Function0<Boolean> function = new Function0<Boolean>()
    {
      
      @Override
      public Boolean apply() throws Throwable
      {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<E> c = cb.createQuery(paramType);
        Expression<E> param = cb.parameter(paramType);
        Predicate restriction = cb.in(param);
        c.where(restriction);
        TypedQuery<E> query = JPA.em().createQuery(c);
        List<E> result = query.getResultList();
        return !result.isEmpty();
      }
    };
    return doInTransaction(function);
  }
  
  @Override
  public long size()
  {
    Function0<Long> function = new Function0<Long>()
    {
      
      @Override
      public Long apply() throws Throwable
      {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<E> from = query.from(paramType);
        query.select(cb.count(from));
        TypedQuery<Long> tq = JPA.em().createQuery(query);
        return tq.getSingleResult();
      }
    };
    return doInTransaction(function);
  }
  
  @Override
  public long size(final Filter filter)
  {
    Function0<Long> function = new Function0<Long>()
    {
      @Override
      public Long apply() throws Throwable
      {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<E> from = query.from(paramType);
        List<Predicate> predicates = getPredicates(cb, from, filter);
        
        if (predicates != null && !predicates.isEmpty()) {
          query.where(predicates.toArray(new Predicate[predicates.size()]));
        }
        
        query.select(cb.count(from));
        TypedQuery<Long> tq = JPA.em().createQuery(query);
        
        Long size = tq.getSingleResult();
        return size;
      }
    };
    return doInTransaction(function);
  }
  
  @Override
  public void save(final E model)
  {
    Function0<Void> function = new Function0<Void>()
    {
      
      @Override
      public Void apply() throws Throwable
      {
        if (model.getId() == null) {
          
          JPA.em().persist(model);
        } else {
          JPA.em().merge(model);
        }
        return null;
      }
    };
    doInTransaction(function);
  }
  
  @Override
  public void remove(final E model)
  {
    Function0<Void> function = new Function0<Void>()
    {
      
      @Override
      public Void apply() throws Throwable
      {
        PersistenceUnitUtil puu = JPA.em().getEntityManagerFactory().getPersistenceUnitUtil();
        Object identifier = puu.getIdentifier(model);
        E attachedEntity = (E) JPA.em().getReference(paramType, identifier);
        JPA.em().remove(attachedEntity);
        attachedEntity = null;
        return null;
      }
    };
    doInTransaction(function);
  }
  
  @Override
  public void remove(final String field, final Object value)
  {
    Function0<Void> function = new Function0<Void>()
    {
      
      @Override
      public Void apply() throws Throwable
      {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaDelete<E> c = cb.createCriteriaDelete(paramType);
        Root<E> from = c.from(paramType);
        Predicate restriction = cb.equal(from.get(field), value);
        c.where(restriction);
        Query query = JPA.em().createQuery(c);
        query.executeUpdate();
        return null;
      }
    };
    doInTransaction(function);
  }
  
  @Override
  public void clear()
  {
    Function0<Void> function = new Function0<Void>()
    {
      
      @Override
      public Void apply() throws Throwable
      {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaDelete<E> c = cb.createCriteriaDelete(paramType);
        Query query = JPA.em().createQuery(c);
        query.executeUpdate();
        return null;
      }
    };
    doInTransaction(function);
  }
  
  @Override
  public void refresh(final E model)
  {
    Function0<Void> function = new Function0<Void>()
    {
      
      @Override
      public Void apply() throws Throwable
      {
        PersistenceUnitUtil puu = JPA.em().getEntityManagerFactory().getPersistenceUnitUtil();
        Object identifier = puu.getIdentifier(model);
        E refresh = (E) JPA.em().getReference(paramType, identifier);
        JPA.em().refresh(refresh);
        return null;
      }
    };
    doInTransaction(function);
  }
  
  /**
   * 
   * @param function
   * 
   * @return Le résultat de la transaction
   */
  protected <T> T doInTransaction(Function0<T> function)
  {
    T result = null;
    try {
      result = JPA.withTransaction(function);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return result;
  }
  
  /**
   * Appliquer le tri.
   * 
   * @param cb
   * @param query
   * @param from
   * @param orderBy
   */
  protected void addSort(CriteriaBuilder cb, CriteriaQuery<E> query, Root<E> from, String... orderBy)
  {
    if (orderBy != null && orderBy.length > 0) {
      List<Order> orders = new ArrayList<Order>();
      for (String orderParameter : orderBy) {
        Order order = cb.asc(from.get(orderParameter));
        orders.add(order);
      }
      query.orderBy(orders);
    }
  }
  
  /**
   * Appliquer le tri.
   * 
   * @param cb
   * @param query
   * @param from
   * @param field
   * @param by
   */
  protected void addSort(CriteriaBuilder cb, CriteriaQuery<E> query, Root<E> from, String field, String by)
  {
    Order order;
    
    String[] subClass = field.split("_");
    Path<Object> path = from.get(subClass[0]);
    Path<Object> param = path;
    
    if (field.contains("_")) {
      for (int i = 1; i < subClass.length; i++) {
        try {
          path = path.get(subClass[i]);
        } catch (Exception e) {
        }
      }
      param = path;
    }
    
    if (Filter.ASC.equalsIgnoreCase(by)) {
      order = cb.asc(param);
    } else {
      order = cb.desc(param);
    }
    query.orderBy(order);
  }
  
  private long parseLong(String value)
  {
    long time = 0;
    Pattern regex = Pattern.compile(".*\\D.*");
    if (!regex.matcher(value).matches()) {
      time = Long.parseLong(value);
    }
    return time;
  }
  
  private List<Predicate> getPredicates(CriteriaBuilder cb, Root<E> from, Filter filter)
  {
    
    List<Predicate> predicates = new ArrayList<Predicate>();
    
    Map<String, Map<Operateur, String>> filteredMap = new HashMap<>();
    filteredMap.putAll(filter.getFormatedQuery());
    
    for (Entry<String, Map<Operateur, String>> entry : filteredMap.entrySet()) {
      String field = entry.getKey();
      
      for (Entry<Operateur, String> tuple : entry.getValue().entrySet()) {
        Predicate predicate = generatePredicate(cb, from, field, tuple.getKey(), tuple.getValue());
        if (predicate != null) {
          predicates.add(predicate);
        }
      }
    }
    
    return predicates;
  }
  
  private Predicate generatePredicate(CriteriaBuilder cb, Root<E> from, String field, Operateur operateur, String valeur)
  {
    
    Predicate predicate = null;
    
    String[] subClass = field.split("_");
    Path<String> path = from.get(subClass[0]);
    Expression<String> expression = path;
    
    if (field.contains("_")) {
      for (int i = 1; i < subClass.length; i++) {
        try {
          path = path.get(subClass[i]);
        } catch (Exception e) {
        }
      }
      expression = path;
    }
    
    Object value = getValueObject(field, valeur);
    if (value != null || (valeur.contains(",") && (Operateur.NIN.equals(operateur) || Operateur.IN.equals(operateur)))) {
      switch (operateur) {
        case EQ:
          predicate = cb.equal(expression, value);
          if (value.getClass().isAssignableFrom(String.class)) {
            predicate = cb.like(cb.upper(expression), stringValueOf(value));
          }
          if(value.toString().equals("null")){
            predicate = cb.isNull(expression);
          }
          break;
        case GT:
          if (!value.getClass().isEnum() && !value.getClass().isAssignableFrom(GregorianCalendar.class)) {
            predicate = cb.greaterThan(expression, String.valueOf(value));
          } else if (value.getClass().isAssignableFrom(GregorianCalendar.class)) {
            predicate = cb.greaterThan(expression.as(GregorianCalendar.class), (GregorianCalendar) value);
          }
          break;
        case GTE:
          if (!value.getClass().isEnum() && !value.getClass().isAssignableFrom(String.class)
              && !value.getClass().isAssignableFrom(GregorianCalendar.class)) {
            predicate = cb.greaterThanOrEqualTo(expression, String.valueOf(value));
          } else {
            predicate = cb.equal(expression, value);
            if (value.getClass().isAssignableFrom(String.class)) {
              predicate = cb.like(cb.upper(expression), stringValueOf(value));
            }
            if (value.getClass().isAssignableFrom(GregorianCalendar.class)) {
              predicate = cb.greaterThanOrEqualTo(expression.as(GregorianCalendar.class), (GregorianCalendar) value);
            }
          }
          break;
        case LT:
          if (!value.getClass().isEnum() && !value.getClass().isAssignableFrom(GregorianCalendar.class)) {
            predicate = cb.lessThan(expression, String.valueOf(value));
          } else if (value.getClass().isAssignableFrom(GregorianCalendar.class)) {
            predicate = cb.lessThan(expression.as(GregorianCalendar.class), (GregorianCalendar) value);
          }
          break;
        case LTE:
          if (!value.getClass().isEnum() && !value.getClass().isAssignableFrom(String.class)
              && !value.getClass().isAssignableFrom(GregorianCalendar.class)) {
            predicate = cb.lessThanOrEqualTo(expression, String.valueOf(value));
          } else {
            predicate = cb.equal(expression, value);
            if (value.getClass().isAssignableFrom(String.class)) {
              predicate = cb.like(cb.upper(expression), stringValueOf(value));
            }
            if (value.getClass().isAssignableFrom(GregorianCalendar.class)) {
              predicate = cb.lessThanOrEqualTo(expression.as(GregorianCalendar.class), (GregorianCalendar) value);
            }
          }
          break;
        case NEQ:
          predicate = cb.notEqual(expression, value);
          if (value.getClass().isAssignableFrom(String.class)) {
            predicate = cb.notLike(cb.upper(expression), stringValueOf(value));
          }
          if(value.toString().equals("null")){
            predicate = cb.isNotNull(expression);
          }
          break;
        case NIN:
          Collection<Object> notIn = new ArrayList<>();
          for (String val : Arrays.asList(((String) valeur).split(","))) {
            notIn.add(getValueObject(subClass[subClass.length - 1], val));
          }
          predicate = cb.not(expression.in(notIn));
          break;
        case IN:
          Collection<Object> in = new ArrayList<>();
          for (String val : Arrays.asList(((String) valeur).split(","))) {
            in.add(getValueObject(subClass[subClass.length - 1], val));
          }
          predicate = expression.in(in);
          break;
      }
    }
    
    return predicate;
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Object getValueObject(String field, String valeur)
  {
    Object value = null;
    String[] splitField = field.split("_");
    Class classToTest = paramType;
    for (int i = 0; i < splitField.length; i++) {
      Map<String, Class<?>> fields = ReflectUtil.getMapFields(classToTest);
      if (!fields.containsKey(splitField[i])) {
        break;
      } else {
        classToTest = fields.get(splitField[i]);
      }
    }
    
    if (ReflectUtil.isEnumExist(classToTest, valeur)) {
      value = ReflectUtil.enumValueOf(classToTest, valeur);
    } else if (ReflectUtil.isAssignableFromBasic(classToTest)) {
      value = ReflectUtil.basicValueOf(classToTest, valeur);
    } else if (ReflectUtil.isAssignableFromTime(classToTest)) {
      value = ReflectUtil.timeValueOf(classToTest, valeur);
    }
    
    if(valeur.equals("null")){
      value=valeur;
    }
    
    return value;
  }
  
  private String stringValueOf(Object value)
  {
    String valueOf = "%" + unAccent(String.valueOf(value).trim().toUpperCase()) + "%";
    return valueOf;
  }
  
  private String unAccent(String source)
  {
    source = source.replaceAll("[^\\p{ASCII}]", "_");
    return source;
  }
}
