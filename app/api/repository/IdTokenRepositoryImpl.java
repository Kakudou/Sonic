package api.repository;

import java.util.Calendar;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import play.db.jpa.JPA;
import play.libs.F.Function0;
import sonic.oidc.idtoken.model.IdToken;
import sonic.oidc.idtoken.repository.IdTokenRepository;
import util.repository.AbstractRepository;

public class IdTokenRepositoryImpl extends AbstractRepository<IdToken>
		implements IdTokenRepository {

	private IdTokenRepositoryImpl() {
		super();
	}

	public static IdTokenRepository getInstance() {
		return Holder.SINGLETON;
	}

	private static class Holder {
		private static final IdTokenRepository SINGLETON = new IdTokenRepositoryImpl();
	}

	public List<IdToken> findExpire() {

		Calendar now = Calendar.getInstance();
		final String nowInMilli = String.valueOf(now.getTimeInMillis());

		Function0<List<IdToken>> function = new Function0<List<IdToken>>() {

			@Override
			public List<IdToken> apply() throws Throwable {
				CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
				CriteriaQuery<IdToken> c = cb.createQuery(IdToken.class);
				Root<IdToken> from = c.from(IdToken.class);
				Expression<String> field = from.get("exp");
		        Predicate restriction = cb.lessThanOrEqualTo(field, nowInMilli);
				c.where(restriction);
				addSort(cb, c, from, new String[0]);
				TypedQuery<IdToken> query = JPA.em().createQuery(c);
				List<IdToken> list = query.getResultList();
				return list;
			}
		};
		return doInTransaction(function);
	}

}
