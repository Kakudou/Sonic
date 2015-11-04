package sonic.oidc.idtoken.repository;

import java.util.List;

import sonic.Repository;
import sonic.oidc.idtoken.model.IdToken;

public interface IdTokenRepository extends Repository<IdToken>{

	/**
	   * Recherche les IdToken qui ont la date d'expiration atteinte ou depassé
	   * @return Toutes les entités à supprimer car expiré
	   */
	List<IdToken> findExpire();
}
