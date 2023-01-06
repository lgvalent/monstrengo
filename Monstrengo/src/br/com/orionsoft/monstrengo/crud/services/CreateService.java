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
 * Serviço de criação de uma nova entidade.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_CLASS: A classe da entidade a ser criada.
 * 
 * <p><b>Procedimento:</b>
 * <br>Obtem o Dao da classe solicitada.
 * <br>Solicita ao Dao que crie um novo objeto do mesmo tipo.
 * <br>Converte o objeto em uma entidade.
 * <br>Preence os valores padrões baseado nos metadados da entidade.
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
        log.debug("Iniciando execução do servico CreateService");
        try
        {
            // Pega os argumentos
            Class<?> inEntityType = (Class<?>) serviceData.getArgumentList().getProperty(IN_ENTITY_TYPE);
            Long inEntityCopyId = null;
            
            if(serviceData.getArgumentList().containsProperty(IN_ENTITY_COPY_ID_OPT))
            	inEntityCopyId = (Long) serviceData.getArgumentList().getProperty(IN_ENTITY_COPY_ID_OPT);

            log.debug("Verificando se a entidade é abstrata");
            if(Modifier.isAbstract(inEntityType.getModifiers()))
                throw new ServiceException(MessageList.create(CreateService.class, "ERROR_ABSTRACT_CLASS", inEntityType.getSimpleName()));

//          Cria uma instância do objeto Java pelo DAO responsável
            if (log.isDebugEnabled())
                log.debug("Obtendo o dao correspondente à entidade" + inEntityType);
            
            IDAO<?> dao = this.getServiceManager().getEntityManager().getDaoManager().getDaoByEntity(inEntityType);
            
            log.debug("Criando um objeto da entidade");
            Object object = dao.create();
    
            log.debug("Convertendo o objeto em uma entidade");
            IEntity<?> result = this.getServiceManager().getEntityManager().getEntity(object);
            
            if(log.isDebugEnabled())
            	log.debug("Prepearando a entidade de cópia: " + inEntityType.getName() + "[" + inEntityCopyId +"]");
            /* Prepara uma entidade de cópia caso exista, para se caso não haver um
             * valor padrão, o valor da cópia seja utilizado */
			IEntity<?> entityCopy = inEntityCopyId==null?null:UtilsCrud.retrieve(this.getServiceManager(), inEntityType, inEntityCopyId, serviceData);
            
            
            log.debug("Percorrendo as propriedades da nova entidade para preencher o valor padrão ou de cópia");
            // Obtem o valor padrao de cada propriedade da entidade
            // Ou se tiver uma entidade para copiar, efetua a cópia
            for (IProperty prop : result.getProperties()){
            	
            	/* Não define valor padrão para propriedades Calculadas */
            	if(!prop.getInfo().isCalculated() && !prop.getInfo().isReadOnly()){
            		/* Verifica se existe algum valor padrão definido
            		 * O valor padrão sobrepõe a propriedade isOneToOne para não criar uma entidade */
            		if(!StringUtils.isEmpty(prop.getInfo().getDefaultValue())){
                		/* TODO IMPLEMENTAR Definir funções básica 
                		 *       para valores padrões como:
                		 *       nowCalendar(): para pegar a data atual;
                		 *       nowDate():
                		 *       nowTime():
                		 *       */
            			try{
            				String defaultValue = prop.getInfo().getDefaultValue();
            				Object defaultObject = null;
            				/*Trata valor padrão para propriedades IEntity */
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
            				
            				/* Verifica se o objeto padrão foi tratado para setar
            				 * como AsObject e evitar problemas de formatação 
            				 * com o método AsString */
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
                		/* Não há um valor padrão definido para a propriedade.
                		 * Copia o valor da entidade cópia se esta foi informada.
                		 */
                		if(entityCopy!=null && prop.getValue().isValueNull())
                			prop.getValue().setAsObject(entityCopy.getPropertyValue(prop.getInfo().getName()));

            			/* Verifica se é um relacionamento EditShowEmbedded para já criar a entidade dependente */
            			if(prop.getInfo().isEntity()&&prop.getInfo().isEditShowEmbedded()&&!prop.getInfo().isCollection()){
            				/* Criando a nova entidade */
            				IEntity<?> entityEmbedded = UtilsCrud.create(this.getServiceManager(), prop.getInfo().getType(), serviceData);
            				/* Usando a nova entidade no relacionamento OneToOne */
        					prop.getValue().setAsEntity(entityEmbedded);
            			}
            		
       				/* Default value não caracteriza uma modificação! */
            		prop.getValue().setModified(false);
            	}
            }
            
            // Adiciona o resultado no serviceData
            serviceData.getOutputData().add(result);
        } 
        catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }

}