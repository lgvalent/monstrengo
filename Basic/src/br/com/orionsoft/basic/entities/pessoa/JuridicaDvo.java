package br.com.orionsoft.basic.entities.pessoa;



import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.StringUtils;
import br.com.orionsoft.monstrengo.core.util.ValidatorUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoBasic;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
/**
 * Classe que valida entidades do tipo Juridica. <br>
 * 
 * @author Sergio
 * @spring.bean id="JuridicaDvo" init-method="registerDvo"
 * @spring.property name="dvoManager" ref="DvoManager"
 * @spring.property name="capitalizeNames" value="false"
 * 
 */
public class JuridicaDvo extends DvoBasic<Juridica> {

	/** Permite configurar via Spring, se deseja ou n�o a capitaliza��o
	 * dos nomes automaticamente. Pois em alguns casos isto n�o � bom.
	 * Mas no in�cio de dados importados, isto � muito �til */
	private boolean capitalizeNames = false;

	public boolean isCapitalizeNames() {return capitalizeNames;}
	public void setCapitalizeNames(boolean capitalizeNames) {this.capitalizeNames = capitalizeNames;}

	/**
	 * Metodo que retorna a classe da entidade.
	 */
	public Class<Juridica> getEntityType(){
		return Juridica.class;	
	}
	
	/* M�todo que verifica o CNPJ de uma entidade do tipo Juridica antes de grav�-la.
	 * Ele verifica se existe um CNPJ para valid�-lo, se o documento � obrigat�rio � evitada a grava��o sem o CNPJ.
	 */
	public void afterCreate(IEntity<Juridica> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		/* Lucio 12/12/12: Define o endere�o principal na lista de outros endere�os */
		Juridica oJuridica = entity.getObject();
		oJuridica.getEnderecoCorrespondencia().setPessoa(oJuridica);
	}
	
	/* M�todo que verifica o CPF de uma entidade do tipo Fisica antes de alter�-la. 
	 * Ele verifica se existe um CPF para valid�-lo, se o documento � obrigat�rio � evitada a grava��o sem o CPF.
	 */
	public void afterUpdate(IEntity<Juridica> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
        DvoException dvoExceptions = new DvoException(new MessageList());
        Juridica oJuridica = entity.getObject();
        if(this.capitalizeNames){

        	/* Coloca os nomes em Capitalize e remove espa�os em branco desnecess�rios */
        	oJuridica.setApelido(StringUtils.prepareStringField(oJuridica.getApelido())); 
        	oJuridica.setNome(StringUtils.prepareStringField(oJuridica.getNome()));
        }else{
        	/* Remove espa�os em branco desnecess�rios */
        	oJuridica.setApelido(org.apache.commons.lang.StringUtils.strip(oJuridica.getApelido())); 
        	oJuridica.setNome(org.apache.commons.lang.StringUtils.strip(oJuridica.getNome()));
        }
        
        try {
			validarCnpj(entity);
		} catch (DvoException e) {
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		} 
		
		try {
			validarCnpjRepetido(entity, serviceData);
		} catch (DvoException e) {
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		}
		
		if(!dvoExceptions.getErrorList().isEmpty()){
			throw dvoExceptions;
		}
	}
	
	public void afterDelete(IEntity<Juridica> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}
	
	public void beforeCreate(IEntity<Juridica> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		
	}

	public void beforeDelete(IEntity<Juridica> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}

	public void beforeUpdate(IEntity<Juridica> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * M�todo que verifica o CNPJ de uma entidade recebendo como argumento um objeto do tipo Juridica.
	 * Ele verifica se o atributo CNPJ objeto do tipo Juridica � obrigat�rio, se for � evitada a grava��o/altera��o do mesmo.
	 */
	private void validarCnpj(IEntity<Juridica> entity) throws DvoException, BusinessException{
		try{
			/* Lucio 20090330: Permite a digita��o de CNPJ 00.000.000/0000-00 (Sandra Rodrigues) */
			if(entity.getPropertyValue(Juridica.DOCUMENTO).equals("00000000000000"))
				return;

			/*
			 * Verifica se existe um CNPJ para validar.
			 */
			if(entity.getProperty(Juridica.DOCUMENTO).getValue().isValueNull()){
				/*
				 * Verifica se o CNPJ � requerido, evitando que uma entidade do tipo Juridica seja gravada/alterada sem o documento.
				 */
				if(entity.getProperty(Juridica.DOCUMENTO).getInfo().isRequired()){
					throw new DvoException(MessageList.create(JuridicaDvo.class, "CNPJ_REQUERIDO"));
				}
			}
			else{
				/*
				 * Caso o CNPJ n�o seja v�lido, uma mensagem ser� enviada.
				 */
				if(!ValidatorUtils.validarCNPJ(entity.getProperty(Juridica.DOCUMENTO).getValue().getAsObject().toString())){
					throw new DvoException(MessageList.create(JuridicaDvo.class, "CNPJ_INVALIDO", entity.getPropertyValue(Juridica.DOCUMENTO)));
					
				}
			}
				
		}catch(PropertyValueException e){
			throw new BusinessException(e.getErrorList());
			
		}
		catch(EntityException e){
		    throw new BusinessException(e.getErrorList());
		}    
	} 
	
	// M�todo que verifica se j� existe um entidade do tipo Jur�dica com o mesmo CNPJ.
	public void validarCnpjRepetido(IEntity<Juridica> entity, ServiceData serviceDataOwner) throws DvoException, BusinessException {
		if(log.isDebugEnabled())	
			log.debug("Verificando CNPJ repetido " + entity.getPropertyValue(Juridica.DOCUMENTO));

		/* Lucio 20090330: Permite a digita��o de CNPJ 00.000.000/0000-00 (Sandra Rodrigues) */
		if(entity.getPropertyValue(Juridica.DOCUMENTO).equals("00000000000000"))
			return;

		ServiceData sd = new ServiceData(ListService.SERVICE_NAME, serviceDataOwner);
		sd.getArgumentList().setProperty(ListService.CLASS, Juridica.class);
		/* where entity.documento='00011122234' and entity.id!=1 */
		sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, IDAO.ENTITY_ALIAS_HQL + "." + Juridica.DOCUMENTO + "= '" + entity.getPropertyValue(Juridica.DOCUMENTO) + "' and " + IDAO.ENTITY_ALIAS_HQL + "." + IDAO.PROPERTY_ID_NAME + "!=" + entity.getId());
		this.getDvoManager().getEntityManager().getServiceManager().execute(sd);
		
		IEntityList<Juridica> result = sd.getFirstOutput();
		
		if(!result.isEmpty()){
			IEntity<Juridica> juridica = result.getFirst();
			throw new DvoException(MessageList.create(JuridicaDvo.class, "CNPJ_REPETIDO", entity.getPropertyValue(Juridica.DOCUMENTO), juridica.getId() + ":" + juridica.toString()));
		}
	}
}