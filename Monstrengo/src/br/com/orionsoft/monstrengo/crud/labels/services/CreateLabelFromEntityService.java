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
 * Servi�o de registro de auditoria de altera��es Crud.
 * 
 * <p><b>Argumento:</b>
 * <br> IN_USER_SESSION: Inst�ncia da atual sess�o do usu�rio.
 * <br> IN_APPLICATION_ENTITY: Entidade de seguran�a que indica a entidade do sistema que ser� auditada.
 * <br> IN_ENTITY_ID: Identificador da inst�ncia da entidade que ser� auditada (LONG).
 * <br> IN_DESCRIPTION_STR: Descri��o adicional que ser� registrada na auditoria.
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
    
    /** Inst�ncia do usu�rio atualmente autenticado no sistema. */
    public static String IN_APPLICATION_USER = "applicationUser";
    /** Modelo de etiqueta para a entidade, a qual fornecer� o que da entidade vai em cada linha da etiqueta */
    public static String IN_MODEL_LABEL_ENTITY = "modelLabelEntity";
    /** Grupo de etiqueta  */
    public static String IN_ADDRESS_LABEL_GROUP_OPT = "addressLabelGroup";
    /** Inst�ncia da entidade da qual ser� estra�da as informa��es */
    public static String IN_ENTITY = "entity";

    @SuppressWarnings({ "unchecked" })
    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execu��o do servi�o CreateLabelFromEntityService");
            // Pega os argumentos
            IEntity<ApplicationUser> inAppUser = (IEntity<ApplicationUser>) serviceData.getArgumentList().getProperty(IN_APPLICATION_USER);
            IEntity<?> inEntity = (IEntity<?>) serviceData.getArgumentList().getProperty(IN_ENTITY);
            IEntity<ModelLabelEntity> inModelLabel = (IEntity<ModelLabelEntity>) serviceData.getArgumentList().getProperty(IN_MODEL_LABEL_ENTITY);
            IEntity<AddressLabelGroup> inAddressLabelGroup = serviceData.getArgumentList().containsProperty(IN_ADDRESS_LABEL_GROUP_OPT)?(IEntity<AddressLabelGroup>) serviceData.getArgumentList().getProperty(IN_ADDRESS_LABEL_GROUP_OPT):null;

            try {
				/* Verifica se a entidade � compativel com o modelo */
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
            /* Pega a express�o do modelo de etiqueta */
            line=inModelLabel.getProperty(ModelLabelEntity.LINE1).getValue().getAsString();
            line=replaceClassNameHierachiModel(line, inEntity, inModelLabel);
            /* Compila a express�o para ela virar valor legivel */
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
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
        }
    }
    
    /**
     * O modelo pode ser de uma classe da hierarquia da entidade e n�o diretamente da classe da entidade.
     * Assim, uma express�o {Pessoa[?].nome} pode ser compiladoa por uma IEntity(Juridica).
     * Para isto, e evitar problema nos compiladores de express�o, vamos substitui a palavra Pessoa para a classe
     * espec�fica da Entidade.
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