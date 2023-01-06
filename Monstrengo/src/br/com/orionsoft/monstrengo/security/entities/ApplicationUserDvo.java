package br.com.orionsoft.monstrengo.security.entities;

import org.apache.commons.codec.digest.DigestUtils;

import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoBasic;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
/**
 * @author Sergio
 * @spring.bean id="ApplicationUserDvo" init-method="registerDvo"
 * @spring.property name="dvoManager" ref="DvoManager"
 */
public class ApplicationUserDvo extends DvoBasic<ApplicationUser> {

	public Class<ApplicationUser> getEntityType() {
		return ApplicationUser.class;
	}
	
	public void afterCreate(IEntity<ApplicationUser> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException {
    	/* Define a senha igual ao login novamente.
    	 * Isto é útil para o cadastro de novos operadores.
    	 * Os metadados devem alertar o operador desta situação. */
		ApplicationUser oApplicationUser = entity.getObject();
		oApplicationUser.setPassword(DigestUtils.md5Hex(oApplicationUser.getLogin()));
	}
	
	public void afterDelete(IEntity<ApplicationUser> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException {
		

	}

	public void afterUpdate(IEntity<ApplicationUser> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException {
	
		
	}

	public void beforeCreate(IEntity<ApplicationUser> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException {
		
		
	}

	public void beforeDelete(IEntity<ApplicationUser> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException {
		
		
	}

	public void beforeUpdate(IEntity<ApplicationUser> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException {
		
	}

}
