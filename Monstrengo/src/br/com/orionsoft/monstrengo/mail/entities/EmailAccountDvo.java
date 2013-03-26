package br.com.orionsoft.monstrengo.mail.entities;

import br.com.orionsoft.monstrengo.mail.entities.EmailAccount;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoBasic;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
/**
 * @author Lucio 20120117
 */
public class EmailAccountDvo extends DvoBasic<EmailAccount> {

	public Class<EmailAccount> getEntityType() {
		return EmailAccount.class;
	}
	
	public void afterCreate(IEntity<EmailAccount> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException {
	}
	
	public void afterDelete(IEntity<EmailAccount> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException {
	}

	public void afterUpdate(IEntity<EmailAccount> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException {
    	/* Verifica se a entidade atual foi definida como useAsDefault para desmarcada
    	 *  qualquer outra que esteja marcada. */
		EmailAccount oEmailAccount = entity.getObject();
		if(oEmailAccount.isUseAsDefault()){
			try {
				IEntityList<EmailAccount> emailAccounts = UtilsCrud.list(this.getDvoManager().getEntityManager().getServiceManager(), EmailAccount.class, IDAO.PROPERTY_ID_NAME + "!=" + oEmailAccount.getId() + " AND " + EmailAccount.USE_AS_DEFAULT + "=true", serviceDataOwner);
				if(!emailAccounts.isEmpty()){
					for(IEntity<EmailAccount> emailAccount: emailAccounts){
						emailAccount.getObject().setUseAsDefault(false);
						UtilsCrud.update(this.getDvoManager().getEntityManager().getServiceManager(), emailAccount, serviceDataOwner);
					}
				}
			} catch (BusinessException e) {
				throw new DvoException(e.getErrorList());
			}
		}
	}

	public void beforeCreate(IEntity<EmailAccount> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException {
		
		
	}

	public void beforeDelete(IEntity<EmailAccount> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException {
		
		
	}

	public void beforeUpdate(IEntity<EmailAccount> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException {
		
	}

}
