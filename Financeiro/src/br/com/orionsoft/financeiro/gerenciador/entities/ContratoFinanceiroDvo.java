package br.com.orionsoft.financeiro.gerenciador.entities;

import br.com.orionsoft.basic.entities.ContratoDvo;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
/**
 * Classe que valida entidades do tipo Contrato do Financeiro. <br>
 * 
 * @author Lucio
 * @spring.bean id="ContratoFinanceiroDvo" init-method="registerDvo"
 * @spring.property name="dvoManager" ref="DvoManager"
 * 
 */
public class ContratoFinanceiroDvo extends ContratoDvo {

	/**
	 * Metodo que retorna a classe da entidade.
	 */
	public Class getEntityType(){
		return ContratoFinanceiro.class;	
	}
	
	/* M�todo que verifica o CNPJ de uma entidade do tipo Juridica antes de grav�-la.
	 * Ele verifica se existe um CNPJ para valid�-lo, se o documento � obrigat�rio � evitada a grava��o sem o CNPJ.
	 */
	public void afterCreate(IEntity entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		 
	}
	
	/* M�todo que verifica o CPF de uma entidade do tipo Fisica antes de alter�-la. 
	 * Ele verifica se existe um CPF para valid�-lo, se o documento � obrigat�rio � evitada a grava��o sem o CPF.
	 */
	public void afterUpdate(IEntity entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		super.afterUpdate(entity, userSession, serviceData);
	}
	
	public void afterDelete(IEntity arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}
	
	public void beforeCreate(IEntity arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		
	}

	public void beforeDelete(IEntity arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}

	public void beforeUpdate(IEntity arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}
	
}