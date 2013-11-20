package br.com.orionsoft.basic.entities;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoBasic;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

/**
 * Classe que valida entidades do tipo Contrato. <br>
 * 
 * @author Lucio
 * @spring.bean id="ContratoDvo" init-method="registerDvo"
 * @spring.property name="dvoManager" ref="DvoManager"
 */
public class ContratoDvo extends DvoBasic<Contrato> {

	/**
	 * Metodo que retorna a classe da entidade.
	 */
	public Class<Contrato> getEntityType(){
		return Contrato.class;	
	}
	
	/* Método que verifica o CNPJ de uma entidade do tipo Juridica antes de gravá-la.
	 * Ele verifica se existe um CNPJ para validá-lo, se o documento é obrigatório é evitada a gravação sem o CNPJ.
	 */
	public void afterCreate(IEntity<Contrato> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		 
	}
	
	/* Método que verifica o CPF de uma entidade do tipo Fisica antes de alterá-la. 
	 * Ele verifica se existe um CPF para validá-lo, se o documento é obrigatório é evitada a gravação sem o CPF.
	 */
	public void afterUpdate(IEntity<Contrato> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
        DvoException dvoExceptions = new DvoException(new MessageList());

        try {
			validarCategoriaRepetida(entity, serviceData);
		} catch (DvoException e) {
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		} 
		
		if(!dvoExceptions.getErrorList().isEmpty()){
			throw dvoExceptions;
		}
	}
	
	public void afterDelete(IEntity<Contrato> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}
	
	public void beforeCreate(IEntity<Contrato> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		
	}

	public void beforeDelete(IEntity<Contrato> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}

	public void beforeUpdate(IEntity<Contrato> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}
	
	// Método que verifica se já existe um entidade do tipo Jurídica com o mesmo CNPJ.
	public void validarCategoriaRepetida(IEntity<Contrato> entity, ServiceData serviceDataOwner) throws DvoException, BusinessException {
		if(log.isDebugEnabled())	
			log.debug("Verificando contrato categoria repetida " + entity.toString());

		ServiceData sd = new ServiceData(ListService.SERVICE_NAME, serviceDataOwner);
		sd.getArgumentList().setProperty(ListService.CLASS,Contrato.class);
		/* where entity.pessoa=entity.pessoa and entity.inativo = False and entity.id!=1 */
		sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, IDAO.ENTITY_ALIAS_HQL + "." + Contrato.PESSOA + "= " + entity.getProperty(Contrato.PESSOA).getValue().getAsEntity().getId() + " and " + IDAO.ENTITY_ALIAS_HQL + "." + Contrato.INATIVO + "= FALSE and " + IDAO.ENTITY_ALIAS_HQL + "." + Contrato.CATEGORIA + "= " + entity.getProperty(Contrato.CATEGORIA).getValue().getAsEntity().getId() + " and " + IDAO.ENTITY_ALIAS_HQL + "." + IDAO.PROPERTY_ID_NAME + "!=" + entity.getId());
		this.getDvoManager().getEntityManager().getServiceManager().execute(sd);
		
		IEntityList<Contrato> result = sd.getFirstOutput();
		
		if(!result.isEmpty()){
			IEntity<Contrato> contrato = result.getFirst();
			throw new DvoException(MessageList.create(ContratoDvo.class, "CATEGORIA_REPETIDA", contrato.toString()));
		}
	}
}