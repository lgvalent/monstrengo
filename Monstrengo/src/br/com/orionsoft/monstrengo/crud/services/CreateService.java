package br.com.orionsoft.monstrengo.crud.services;

import java.lang.reflect.Modifier;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.services.CreateService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Servi�o de cria��o de uma nova entidade.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_CLASS: A classe da entidade a ser criada.
 * 
 * <p><b>Procedimento:</b>
 * <br>Obtem o Dao da classe solicitada.
 * <br>Solicita ao Dao que crie um novo objeto do mesmo tipo.
 * <br>Converte o objeto em uma entidade.
 * <br>Preence os valores padr�es baseado nos metadados da entidade.
 * <br><b>Retorna uma nova entidade;</b>
 * 
 * 
 * @author Lucio
 * @version 2005/09/28
 * 
 */
public class CreateService extends ServiceBasic {
    
    public static String SERVICE_NAME = "CreateService";
    public static String IN_ENTITY_TYPE = "classObj";
    public static String IN_ENTITY_COPY_ID_OPT = "entityCopyId";
    
    public String getServiceName() {return SERVICE_NAME;}

    public void execute(ServiceData serviceData) throws ServiceException 
    {
        log.debug("Iniciando execu��o do servico CreateService");
        try
        {
            // Pega os argumentos
            Class<?> inEntityType = (Class<?>) serviceData.getArgumentList().getProperty(IN_ENTITY_TYPE);
            Long inEntityCopyId = null;
            
            if(serviceData.getArgumentList().containsProperty(IN_ENTITY_COPY_ID_OPT))
            	inEntityCopyId = (Long) serviceData.getArgumentList().getProperty(IN_ENTITY_COPY_ID_OPT);

            log.debug("Verificando se a entidade � abstrata");
            if(Modifier.isAbstract(inEntityType.getModifiers()))
                throw new ServiceException(MessageList.create(CreateService.class, "ERROR_ABSTRACT_CLASS", inEntityType.getSimpleName()));

//          Cria uma inst�ncia do objeto Java pelo DAO respons�vel
            if (log.isDebugEnabled())
                log.debug("Obtendo o dao correspondente � entidade" + inEntityType);
            
            IDAO<?> dao = this.getServiceManager().getEntityManager().getDaoManager().getDaoByEntity(inEntityType);
            
            log.debug("Criando um objeto da entidade");
            Object object = dao.create();
    
            log.debug("Convertendo o objeto em uma entidade");
            IEntity<?> result = this.getServiceManager().getEntityManager().getEntity(object);
            
            if(log.isDebugEnabled())
            	log.debug("Prepearando a entidade de c�pia: " + inEntityType.getName() + "[" + inEntityCopyId +"]");
            /* Prepara uma entidade de c�pia caso exista, para se caso n�o haver um
             * valor padr�o, o valor da c�pia seja utilizado */
			IEntity<?> entityCopy = inEntityCopyId==null?null:UtilsCrud.retrieve(this.getServiceManager(), inEntityType, inEntityCopyId, serviceData);
            
            
            log.debug("Percorrendo as propriedades da nova entidade para preencher o valor padr�o ou de c�pia");
            // Obtem o valor padrao de cada propriedade da entidade
            // Ou se tiver uma entidade para copiar, efetua a c�pia
            for (IProperty prop : result.getProperties()){
            	
            	/* N�o define valor padr�o para propriedades Calculadas */
            	if(!prop.getInfo().isCalculated() && !prop.getInfo().isReadOnly()){
            		/* Verifica se existe algum valor padr�o definido
            		 * O valor padr�o sobrep�e a propriedade isOneToOne para n�o criar uma entidade */
            		if(!StringUtils.isEmpty(prop.getInfo().getDefaultValue())){
                		/* TODO IMPLEMENTAR Definir fun��es b�sica 
                		 *       para valores padr�es como:
                		 *       nowCalendar(): para pegar a data atual;
                		 *       nowDate():
                		 *       nowTime():
                		 *       */
            			try{
            				String defaultValue = prop.getInfo().getDefaultValue();
            				Object defaultObject = null;
            				/*Trata valor padr�o para propriedades IEntity */
            				if(prop.getInfo().isEntity()){
            					try{
            						long entityId = Long.parseLong(defaultValue);
            						/* Verifica se o id fornecido nos metadados foi encontrado */
            						IEntityList<?> list = UtilsCrud.list(this.getServiceManager(), prop.getInfo().getType(), IDAO.ENTITY_ALIAS_HQL + "." + IDAO.PROPERTY_ID_NAME + "=" + entityId, serviceData);
            						if(list.isEmpty())
            							this.addInfoMessage(serviceData, "DEFAULT_VALUE_ID_NOT_FOUND", entityId, prop.getInfo().getName(), prop.getEntityOwner().getInfo().getLabel());
            						else
            						  defaultObject = list.getFirst();
            					}catch(RuntimeException e){
            						throw new PropertyValueException(MessageList.createSingleInternalError(e));
            					}catch(BusinessException e){
            						throw new PropertyValueException(e.getErrorList());
            					}
            					
            				}else{
            					if(defaultValue.equals("now()"))
            						defaultObject = Calendar.getInstance();
            					
            					if(defaultValue.equals("nowDate()"))
            						defaultObject = CalendarUtils.getCalendar();
            					
            					if(defaultValue.equals("nowTime()"))
            						defaultObject = CalendarUtils.getCalendarTime();
            				}
            				
            				/* Verifica se o objeto padr�o foi tratado para setar
            				 * como AsObject e evitar problemas de formata��o 
            				 * com o m�todo AsString */
            				if(defaultObject != null)
            					prop.getValue().setAsObject(defaultObject);
            				else
            					prop.getValue().setAsString(defaultValue);
            				
            			}catch(PropertyValueException e){
            				PropertyValueException ex = new PropertyValueException(e.getErrorList());
            				ex.getErrorList().add(CreateService.class, "ERROR_GETTING_DEFAULT", prop.getInfo().getLabel(), prop.getEntityOwner().getInfo().getLabel(), prop.getInfo().getDefaultValue());
            				throw ex;
            			}

            		}else
                		/* N�o h� um valor padr�o definido para a propriedade.
                		 * Copia o valor da entidade c�pia se esta foi informada.
                		 */
                		if(entityCopy!=null && prop.getValue().isValueNull())
                			prop.getValue().setAsObject(entityCopy.getPropertyValue(prop.getInfo().getName()));

            			/* Verifica se � um relacionamento EditShowEmbedded para j� criar a entidade dependente */
            			if(prop.getInfo().isEntity()&&prop.getInfo().isEditShowEmbedded()&&!prop.getInfo().isCollection()){
            				/* Criando a nova entidade */
            				IEntity<?> entityEmbedded = UtilsCrud.create(this.getServiceManager(), prop.getInfo().getType(), serviceData);
            				/* Usando a nova entidade no relacionamento OneToOne */
        					prop.getValue().setAsEntity(entityEmbedded);
            			}
            		
       				/* Default value n�o caracteriza uma modifica��o! */
            		prop.getValue().setModified(false);
            	}
            }
            
            // Adiciona o resultado no serviceData
            serviceData.getOutputData().add(result);
        } 
        catch (BusinessException e)
        {
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
        }
    }

}