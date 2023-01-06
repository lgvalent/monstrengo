package br.com.orionsoft.monstrengo.crud.labels.services;

import java.util.Calendar;

import br.com.orionsoft.monstrengo.crud.labels.services.CreateLabelFromEntityService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.ClassUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabel;
import br.com.orionsoft.monstrengo.crud.labels.entities.AddressLabelGroup;
import br.com.orionsoft.monstrengo.crud.labels.entities.ModelLabelEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.crud.support.DocumentParserCrudExpression;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Serviço de registro de auditoria de alterações Crud.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_USER_SESSION: Instância da atual sessão do usuário.
 * <br> IN_APPLICATION_ENTITY: Entidade de segurança que indica a entidade do sistema que será auditada.
 * <br> IN_ENTITY_ID: Identificador da instância da entidade que será auditada (LONG).
 * <br> IN_DESCRIPTION_STR: Descrição adicional que será registrada na auditoria.
 * 
 * <p><b>Procedimento:</b>
 * <br>Cria um novo registro da auditoria.
 * <br>Preenche os dados do registro.
 * <br>Grava o registro.
 * <br><b>Retorna a nova entidade do registro.</b>
 * 
 * 
 * @spring.bean id="CreateLabelFromEntityService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class CreateLabelFromEntityService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "CreateLabelFromEntityService";
    
    /** Instância do usuário atualmente autenticado no sistema. */
    public static String IN_APPLICATION_USER = "applicationUser";
    /** Modelo de etiqueta para a entidade, a qual fornecerá o que da entidade vai em cada linha da etiqueta */
    public static String IN_MODEL_LABEL_ENTITY = "modelLabelEntity";
    /** Grupo de etiqueta  */
    public static String IN_ADDRESS_LABEL_GROUP_OPT = "addressLabelGroup";
    /** Instância da entidade da qual será estraída as informações */
    public static String IN_ENTITY = "entity";

    @SuppressWarnings({ "unchecked" })
    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execução do serviço CreateLabelFromEntityService");
            // Pega os argumentos
            IEntity<ApplicationUser> inAppUser = (IEntity<ApplicationUser>) serviceData.getArgumentList().getProperty(IN_APPLICATION_USER);
            IEntity<?> inEntity = (IEntity<?>) serviceData.getArgumentList().getProperty(IN_ENTITY);
            IEntity<ModelLabelEntity> inModelLabel = (IEntity<ModelLabelEntity>) serviceData.getArgumentList().getProperty(IN_MODEL_LABEL_ENTITY);
            IEntity<AddressLabelGroup> inAddressLabelGroup = serviceData.getArgumentList().containsProperty(IN_ADDRESS_LABEL_GROUP_OPT)?(IEntity<AddressLabelGroup>) serviceData.getArgumentList().getProperty(IN_ADDRESS_LABEL_GROUP_OPT):null;

            try {
				/* Verifica se a entidade é compativel com o modelo */
				if (!ClassUtils.isAssignable(inEntity.getInfo().getType(), Class.forName(inModelLabel.getObject().getApplicationEntity().getClassName()))){
					throw new ServiceException(MessageList.create(CreateLabelFromEntityService.class, "INCOMPATIBLE_MODEL", inModelLabel, inEntity.getInfo().getType().getSimpleName()));
				}
            } catch (ClassNotFoundException e) {
            	throw new ServiceException(MessageList.createSingleInternalError(e));
            }
            
            // Cria um novo registro
            IEntity<AddressLabel> addressLabel = UtilsCrud.create(this.getServiceManager(), AddressLabel.class, serviceData);

            // Preenche o novo registro
            addressLabel.getProperty(AddressLabel.APPLICATION_USER).getValue().setAsEntity(inAppUser);
            addressLabel.getProperty(AddressLabel.APPLICATION_ENTITY).getValue().setAsEntity(inModelLabel.getProperty(ModelLabelEntity.APPLICATION_ENTITY).getValue().getAsEntity());
            addressLabel.getProperty(AddressLabel.ADDRESS_LABEL_GROUP).getValue().setAsEntity(inAddressLabelGroup);
            addressLabel.getProperty(AddressLabel.OCURRENCY_DATE).getValue().setAsCalendar(Calendar.getInstance());

            String line;
            /* Pega a expressão do modelo de etiqueta */
            line=inModelLabel.getProperty(ModelLabelEntity.LINE1).getValue().getAsString();
            line=replaceClassNameHierachiModel(line, inEntity, inModelLabel);
            /* Compila a expressão para ela virar valor legivel */
            line=DocumentParserCrudExpression.parseString(line,inEntity, this.getServiceManager().getEntityManager());
            /* Armazena o resultado compilado na linha da nova etiqueta */
            addressLabel.getProperty(AddressLabel.LINE1).getValue().setAsString(line);
            
            line=inModelLabel.getProperty(ModelLabelEntity.LINE2).getValue().getAsString();
            line=replaceClassNameHierachiModel(line, inEntity, inModelLabel);
            line=DocumentParserCrudExpression.parseString(line,inEntity, this.getServiceManager().getEntityManager());
            addressLabel.getProperty(AddressLabel.LINE2).getValue().setAsString(line);
            
            line=inModelLabel.getProperty(ModelLabelEntity.LINE3).getValue().getAsString();
            line=replaceClassNameHierachiModel(line, inEntity, inModelLabel);
            line=DocumentParserCrudExpression.parseString(line,inEntity, this.getServiceManager().getEntityManager());
            addressLabel.getProperty(AddressLabel.LINE3).getValue().setAsString(line);
            
            line=inModelLabel.getProperty(ModelLabelEntity.LINE4).getValue().getAsString();
            line=replaceClassNameHierachiModel(line, inEntity, inModelLabel);
            line=DocumentParserCrudExpression.parseString(line,inEntity, this.getServiceManager().getEntityManager());
            addressLabel.getProperty(AddressLabel.LINE4).getValue().setAsString(line);
            
            line=inModelLabel.getProperty(ModelLabelEntity.LINE5).getValue().getAsString();
            line=replaceClassNameHierachiModel(line, inEntity, inModelLabel);
            line=DocumentParserCrudExpression.parseString(line,inEntity, this.getServiceManager().getEntityManager());
            addressLabel.getProperty(AddressLabel.LINE5).getValue().setAsString(line);
            
            // Grava o registro
            UtilsCrud.update(this.getServiceManager(), addressLabel, serviceData);
            
            // Adiciona o registro criado no resultado no serviceData
            serviceData.getOutputData().add(addressLabel);
            
        } 
        catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }
    
    /**
     * O modelo pode ser de uma classe da hierarquia da entidade e não diretamente da classe da entidade.
     * Assim, uma expressão {Pessoa[?].nome} pode ser compiladoa por uma IEntity(Juridica).
     * Para isto, e evitar problema nos compiladores de expressão, vamos substitui a palavra Pessoa para a classe
     * específica da Entidade.
     * @param line
     * @param inEntity
     * @param inModelLabel
     * @return
     */
    private String replaceClassNameHierachiModel(String line, IEntity<?> inEntity,
			IEntity<ModelLabelEntity> inModelLabel) {
		return line.replace(inModelLabel.getObject().getApplicationEntity().getName() + "[?]", inEntity.getObject().getClass().getSimpleName() + "[?]");
	}

	public String getServiceName() {return SERVICE_NAME;}
}