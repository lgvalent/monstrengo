package br.com.orionsoft.basic.entities.pessoa;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
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
import br.com.orionsoft.monstrengo.mail.services.SendMailService;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

/**
 * Classe que valida entidades do tipo Fisica. <br>
 * 
 *  
 * @author Sergio
 * @spring.bean id="FisicaDvo" init-method="registerDvo"
 * @spring.property name="dvoManager" ref="DvoManager"
 * @spring.property name="capitalizeNames" value="false"
 */
public class FisicaDvo extends DvoBasic<Fisica> {
	
	/** Permite configurar via Spring, se deseja ou não a capitalização
	 * dos nomes automaticamente. Pois em alguns casos isto não é bom.
	 * Mas no início de dados importados, isto é muito útil */
	private boolean capitalizeNames = false;
	
	public boolean isCapitalizeNames() {return capitalizeNames;}
	public void setCapitalizeNames(boolean capitalizeNames) {this.capitalizeNames = capitalizeNames;}

	/***
	 * Metodo que retorna a classe da entidade. 
	 */
	public Class<Fisica> getEntityType() {
		return Fisica.class;
	}
	
	public void afterCreate(IEntity<Fisica> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		/* Lucio 12/12/12: Define o endereço principal na lista de outros endereços */
		Fisica oFisica = entity.getObject();
		oFisica.getEnderecoCorrespondencia().setPessoa(oFisica);
	}
	
	/* Método que verifica o CPF de uma entidade do tipo Fisica antes de alterá-la. 
	 * Ele verifica se existe um CPF para validá-lo, se o documento é obrigatório é evitada a gravação sem o CPF.
	 */
	public void afterUpdate(IEntity<Fisica> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
        DvoException dvoExceptions = new DvoException(new MessageList());
        
        if(this.capitalizeNames){
        	Fisica oFisica = entity.getObject();
        	/* Coloca os nomes em Capitalize e remove espaços em branco desnecessários */
        	oFisica.setApelido(StringUtils.prepareStringField(oFisica.getApelido()));
        	oFisica.setNome(StringUtils.prepareStringField(oFisica.getNome()));
        }
        
		try {
			validarCpf(entity);
		} catch (DvoException e) {
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		} 
		
		try {
			validarDataNascimento(entity);
		} catch (DvoException e) {
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		}
		
		try{
			validarCpfRepetido(entity, serviceData);
		}
		catch(DvoException e){
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		}
		
		try{
			validarRgRepetido(entity, serviceData);
		}
		catch(DvoException e){
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		}
		
		try{
			validarEMail(entity);
		}
		catch(DvoException e){
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		}
		
		if(!dvoExceptions.getErrorList().isEmpty()){
			throw dvoExceptions;
		}
		
	}
	public void afterDelete(IEntity<Fisica> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		// TODO Auto-generated method stub
	}

	public void beforeCreate(IEntity<Fisica> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		// TODO Auto-generated method stub
	}

	public void beforeDelete(IEntity<Fisica> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		// TODO Auto-generated method stub
	}

	public void beforeUpdate(IEntity<Fisica> entity, UserSession userSession, ServiceData serviceDataOwner) throws DvoException, BusinessException  {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Método que verifica o CPF de uma entidade do tipo Fisica antes de criá-la
	 * Ele verifica se existe um CPF para validá-lo, se o documento é obrigatório é evitada a gravação sem o CPF.
	 */
	public void validarCpf(IEntity<Fisica> entity) throws DvoException, BusinessException{
		/* Verifica se tem um cpf para validar */
		try {
			if(entity.getProperty(Fisica.DOCUMENTO).getValue().isValueNull()){
				if(entity.getProperty(Fisica.DOCUMENTO).getInfo().isRequired())
					/* Não tem CPF, verifica se o CPF é obrigatório para evitar que a entidade seja gravada sem CPF */
			    	throw new DvoException(MessageList.create(FisicaDvo.class, "CPF_REQUERIDO"));
			}else
				/* Lucio 20090316: Permite a digitação de CPF 000.000.000-00 (Sandra Rodrigues) */
				if(entity.getPropertyValue(Fisica.DOCUMENTO).equals("00000000000"))
					log.debug("CPF VALIDO!");
				else
					/* Tem um cpf digitado, verifica se ele é válido */
					if(!ValidatorUtils.validarCPF(entity.getProperty(Fisica.DOCUMENTO).getValue().getAsObject().toString()))
				    	throw new DvoException(MessageList.create(FisicaDvo.class, "CPF_INVALIDO", entity.getPropertyValue(Fisica.DOCUMENTO)));
					else
						log.debug("CPF VALIDO!");
				
		} catch (PropertyValueException e) {
			throw new BusinessException(e.getErrorList());
		} catch (EntityException e) {
			throw new BusinessException(e.getErrorList());
		}
	}
	/***
	 * Método que verifica se a Data de Nascimento é maior que a Data Corrente, caso seja 
	 * uma mensagem será enviada para o usuário.
	 */
	public void validarDataNascimento(IEntity<Fisica> entity) throws DvoException, BusinessException {
		try{
			/*
			 * Verifica se tem uma data de nascimento para validar
			 * nao eh nulo!!!
			 */
			if(!entity.getProperty(Fisica.DATA_INICIAL).getValue().isValueNull()){
				if(CalendarUtils.diffDay(entity.getProperty(Fisica.DATA_INICIAL).getValue().getAsCalendar(), CalendarUtils.getCalendar()) > 0 ){
					throw new DvoException(MessageList.create(Fisica.class, "DATA_INCORRETA"));
				}	
				else{
					log.debug("Data de Nascimento OK !");
				}
			}
		}
		catch(PropertyValueException e) {
			throw new BusinessException(e.getErrorList());
		}
		catch(EntityException e) {
			throw new BusinessException(e.getErrorList());
		}
		
	}
	
	/***
	 * Método que verifica se os são válidos.
	 */
	public void validarEMail(IEntity<Fisica> entity) throws DvoException, BusinessException {
		try{
			String emails = entity.getProperty(Fisica.EMAIL).getValue().getAsString();
			if(!StringUtils.isBlank(emails)){
				for(String email: emails.split(";"))
					if(!SendMailService.validateEMail(email))
						throw new DvoException(MessageList.create(FisicaDvo.class, "EMAIL_INVALIDO", email));
			}
		}
		catch(PropertyValueException e) {
			throw new BusinessException(e.getErrorList());
		}
		catch(EntityException e) {
			throw new BusinessException(e.getErrorList());
		}
		
	}
	
	/**
	 * Método que verifica se já existe uma entidade do tipo Física com o mesmo CPF.
	 * 
	 * Alterado por Andre em 20071026
	 * Caso o CPF já exista, verifica se a pessao está utilizando-o como CPF de Responsável; caso 
	 * esteja, permite que a pessoa se cadastre utilizando o CPF repetido.
	 */
	public void validarCpfRepetido(IEntity<Fisica> entity, ServiceData serviceDataOwner) throws DvoException, BusinessException {
		
		if(log.isDebugEnabled())
			log.debug("Verificando CPF repetido:" + entity.getPropertyValue(Fisica.DOCUMENTO));

		/* Lucio 20090316: Permite a digitação de CPF 000.000.000-00 (Sandra Rodrigues) */
		if(entity.getPropertyValue(Fisica.DOCUMENTO).equals("00000000000"))
			return;

		/* TODO ATIMIZAR */
		ServiceData sd = new ServiceData(ListService.SERVICE_NAME, serviceDataOwner);
		sd.getArgumentList().setProperty(ListService.CLASS, Fisica.class);
		/* where entity.documento='00011122234' and entity.id!=1 */
		sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, IDAO.ENTITY_ALIAS_HQL + "." + Fisica.DOCUMENTO + "= '" + entity.getPropertyValue(Fisica.DOCUMENTO) + "' and " + IDAO.ENTITY_ALIAS_HQL + "." + IDAO.PROPERTY_ID_NAME + "!=" + entity.getId());
		this.getDvoManager().getEntityManager().getServiceManager().execute(sd);

		IEntityList<Fisica> result = sd.getFirstOutput();

		/*
		 * Se a lista retornada contém elementos, verifica se:
		 * - a pessoa que está se cadastrando está usando o CPF de seu responsável (isUsaResponsavelCpf);
		 * - se não usa CPF de responsável, verifica se o CPF já está sendo usado como responsável, ou seja, 
		 * a pessoa é a dona do CPF e existe outra pessoa no sistema sob sua responsabilidade.
		 * 
		 * Caso nenhuma das opções acima seja válida, não permite o cadastramento repetido de CPF. 
		 */ 
		if(!result.isEmpty()){
			Fisica novoCadastro = entity.getObject();
			//se não utiliza CPF de Responsável, verifica se todos os CPF cadastrados com o mesmo número são de dependentes
			if (!novoCadastro.isUsaResponsavelCpf()){
				boolean cpfRepetido = false;

				Fisica oFisica = result.getFirst().getObject();
				for (IEntity<Fisica> eFisica : result){
					oFisica = eFisica.getObject();

					//se encontrar pelo menos um que não seja dependente, evita que o CPF seja gravado 
					if (!oFisica.isUsaResponsavelCpf()){
						cpfRepetido = true;
						break;
					}

				}

				if (cpfRepetido){
					if(log.isDebugEnabled()){
						log.debug("Pessoa já cadastrada com o CPF" + entity.getPropertyValue(Fisica.DOCUMENTO));
					}

					throw new DvoException(MessageList.create(FisicaDvo.class, "CPF_REPETIDO", entity.getPropertyValue(Fisica.DOCUMENTO), oFisica.getId() + ":" + oFisica.toString() ));
				}
			}
		}
	}

	/**
	 * Método que verifica se já existe uma entidade do tipo Física com o mesmo RG.
	 * 
	 * Criado por Lucio em 20080113
	 */
	public void validarRgRepetido(IEntity<Fisica> entity, ServiceData serviceDataOwner) throws DvoException, BusinessException {
		Fisica oFisica = entity.getObject();

		//TODO - otimizar este código
		
		if(log.isDebugEnabled())
			log.debug("Verificando RG repetido:" + entity.getPropertyValue(Fisica.RG_NUMERO));
			
		/*  */
		ServiceData sd = new ServiceData(ListService.SERVICE_NAME, serviceDataOwner);
		sd.getArgumentList().setProperty(ListService.CLASS, Fisica.class);
		/* where entity.rg='00011122234' and entity.id!=1 and entity.rgUfExpediro='PR'*/
		if(oFisica.getRgUfExpedidor() == null)
			sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, IDAO.ENTITY_ALIAS_HQL + "." + Fisica.RG_NUMERO + "= '" + oFisica.getRgNumero() + "' and " + IDAO.ENTITY_ALIAS_HQL + "." + IDAO.PROPERTY_ID_NAME + "!=" + oFisica.getId() + " and " + IDAO.ENTITY_ALIAS_HQL + "." + Fisica.RG_UF_EXPEDIDOR + " is null");
		else
			sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, IDAO.ENTITY_ALIAS_HQL + "." + Fisica.RG_NUMERO + "= '" + oFisica.getRgNumero() + "' and " + IDAO.ENTITY_ALIAS_HQL + "." + IDAO.PROPERTY_ID_NAME + "!=" + oFisica.getId() + " and " + IDAO.ENTITY_ALIAS_HQL + "." + Fisica.RG_UF_EXPEDIDOR + " = '" + oFisica.getRgUfExpedidor().name() + "'");
		this.getDvoManager().getEntityManager().getServiceManager().execute(sd);
		
		IEntityList<Fisica> result = sd.getFirstOutput();
		
		/*
		 * Se a lista retornada contém elementos:
		 */ 
		if(!result.isEmpty()){
			if(log.isDebugEnabled()){
				log.debug("Pessoa já cadastrada com o RG" + entity.getPropertyValue(Fisica.DOCUMENTO));
			}
			
			throw new DvoException(MessageList.create(FisicaDvo.class, "RG_REPETIDO", entity.getPropertyValue(Fisica.RG_NUMERO), oFisica.getId() + ":" + oFisica.toString() ));

		}
	}
	
	
}
