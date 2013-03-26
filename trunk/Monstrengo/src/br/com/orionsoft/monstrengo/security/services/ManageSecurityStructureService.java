package br.com.orionsoft.monstrengo.security.services;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.IProcessManager;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.services.GetCrudEntitiesService;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntityProperty;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntityPropertyGroup;
import br.com.orionsoft.monstrengo.security.entities.ApplicationModule;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;
import br.com.orionsoft.monstrengo.security.processes.OverwritePasswordProcess;

/**
 * Serviço para cadastrar todas as entidades de segurança da aplicação correntemente
 * instanciada.<br>
 * Ele também atualiza o label das entidades de acordo com a atual definição nos metadados.<br>
 * Cria os registros no banco de todos os módulos (framework, basic, financeiro, etc), 
 * bem como suas entidades e processos.
 * Este serviço é excencial para que a aplicação funcione corretamente.
 *  
 * <p><b>Procedimento:</b><br>
 * Mantem as entidades Crud disponíveis no serviço GetCrudEntitiesService.<br>
 * Mantem os processos disponíveis no serviço GetAllProcessService.<br>
 * <b>Não retorna nada;</b><br>
 * 
 * @author Lucio 2005/10/21
 * @version 2005/10/21
 * 
 */
public class ManageSecurityStructureService extends ServiceBasic 
{
    public static String SERVICE_NAME = "ManageSecurityStructureService";
    public static String IN_RESTORE_DEFAULT_OPT = "restoreDefault";
    public static String IN_PROCESS_MANAGER = "processManager";
    
    public String getServiceName() {
        return SERVICE_NAME;
    }
    
    /** Injeta a referência do processManager atual, pois este desempenha o papel de RunnableProcessEntityController
     * que mantém um registro de todos os controllers de processos que implementam a interface IRunnableProcessEntity
     */

    public void execute(ServiceData serviceData) throws ServiceException 
    {
        try
        {
            log.debug("Iniciando a execução do servico ManagerSecurityStructureService");
            
            Boolean inRestoreDefault = false;
            if(serviceData.getArgumentList().containsProperty(IN_RESTORE_DEFAULT_OPT))
            	inRestoreDefault = (Boolean) serviceData.getArgumentList().getProperty(IN_RESTORE_DEFAULT_OPT);
            IProcessManager inProcessManager = (IProcessManager) serviceData.getArgumentList().getProperty(IN_PROCESS_MANAGER);
            
            manageEntitiesSecurity(serviceData, inRestoreDefault);
            
            manageProcessSecurity(serviceData, inProcessManager);
            
        } 
        catch (BusinessException e)
        {
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }

    private void manageEntitiesSecurity(ServiceData serviceData, boolean restoreDefault) throws BusinessException
    {
    	log.debug("Registrando as entidades crud do Sistema");
        
    	//Obtendo a lista de entidades do Sistema
        ServiceData sd = new ServiceData(GetCrudEntitiesService.SERVICE_NAME, serviceData);
        
        this.getServiceManager().execute(sd);
        
        List<IEntityMetadata> entityList = sd.getFirstOutput();
        
        for (IEntityMetadata entityInfo : entityList){
            // Verifica se o módulo não está cadastrado
            //para cada entidade, verificar o modulo correspondente
            IEntity<ApplicationModule> module = manageModuleSecurity(entityInfo.getType(), serviceData);
            
            if (log.isDebugEnabled())
                log.debug("Verificando se a entidade " + entityInfo.getLabel() + " já está cadastrada");
            
            sd = new ServiceData(ListService.SERVICE_NAME, serviceData);
            //preenche propriedades do serviço
            sd.getArgumentList().setProperty(ListService.CLASS, ApplicationEntity.class);
            //processClass.getSimpleName - pega o nome do processo pra gravar no banco
            sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, ApplicationEntity.CLASS_NAME +  "= '" + entityInfo.getType().getName() + "'");
            
            //executa o serviço
            this.getServiceManager().execute(sd);
            
            //verificando se foi encontrado alguma entidade cadastrado        	
            //o item 0 possui uma EntityList
            IEntityList<ApplicationEntity> enl = sd.getFirstOutput();
            if (enl.isEmpty()){
                if (log.isDebugEnabled())
                    log.debug("Entidade " + entityInfo.getLabel() + " não encontrada, cadastrando...");
                
                //não encontrou, cadastra entidade
                IEntity<ApplicationEntity> entity = UtilsCrud.create(this.getServiceManager(), ApplicationEntity.class, serviceData);
                
                fillApplicationEntity(entity, module, entityInfo);
                
                for(IPropertyMetadata prop: entityInfo.getPropertiesMetadata().values()){
                	IEntity<ApplicationEntityProperty> property = UtilsCrud.create(this.getServiceManager(), ApplicationEntityProperty.class, serviceData);

                	fillApplicationEntityProperty(property, entity, prop);
                	
                	UtilsCrud.update(this.getServiceManager(), property, serviceData);
                }
                
                
                for(IGroupMetadata groupMetadata: entityInfo.getGroups()){
                    IEntity<ApplicationEntityPropertyGroup> group = UtilsCrud.create(this.getServiceManager(), ApplicationEntityPropertyGroup.class, serviceData);

                	fillApplicationEntityPropertyGroup(group, entity, groupMetadata);

                    /* Liga o grupo atual à entidade */
                	entity.getProperty(ApplicationEntity.APPLICATION_ENTITY_PROPERTY_GROUP).getValue().<ApplicationEntityPropertyGroup>getAsEntityList().add(group); 

                	UtilsCrud.update(this.getServiceManager(), group, serviceData);
                }
                
                //executa o serviço
                UtilsCrud.update(this.getServiceManager(), entity, serviceData);

                if (log.isDebugEnabled())
                    log.debug("Entidade " + entityInfo.getLabel() + " cadastrada com sucesso");
                
            }
            else  //nao está vazio
            {
                if (log.isDebugEnabled())
                    log.debug("Entidade " + entityInfo.getLabel() + " ja cadastrada. Atualizando metadados...");

                //encontrou, atualiza o label
                IEntity<ApplicationEntity> entity = enl.getFirst();

                IEntityMetadata entityInfoDefault =  this.getServiceManager().getEntityManager().getEntityMetadataDefaults(entityInfo.getType());
//                UtilsTest.showEntityProperties(entityInfoDefault);

                /* se tiver habilitado o restoreDefault preenche as propriedades da entidade
                 * detectar as propriedades q estao no metadados e q nao estao no banco. adiciona no banco
                 * detectar as propriedades q nao estao no metadados e q estao no banco. deletar no banco
                 * detectar os grupos das propriedades q estao no metadados e q nao estao no banco. adiciona no banco
                 * detectar os grupos das propriedades q nao estao no metadados e q estao no banco. deletar no banco
                 */


                if(restoreDefault){

                    fillApplicationEntity(entity, module, entityInfoDefault);

                }

                /* Lucio 20110721: Pega as propriedades no banco pesquisando diretamente na tabela da
                 * entidade, pois ApplicationEntity não vê propriedades e grupos */
                IEntityList<ApplicationEntityPropertyGroup> entityGroups = UtilsCrud.list(this.getServiceManager(), ApplicationEntityPropertyGroup.class, "entity.applicationEntity.id = " + entity.getId(), serviceData);
                IEntityList<ApplicationEntityProperty> entityProperties = UtilsCrud.list(this.getServiceManager(), ApplicationEntityProperty.class, "entity.applicationEntity.id = " + entity.getId(), serviceData);
                
                //detectar se as propriedades da entidade que estao no metadados estão no banco
                //se encontrar e restoreDefault=true, atualizar propriedades
                //se nao encontrar, criar a propriedade no banco
                for(IPropertyMetadata prop: entityInfoDefault.getPropertiesMetadata().values()){
                    boolean found = false;
                	for(IEntity<ApplicationEntityProperty> entProp: entityProperties){ 

                		if(StringUtils.equals(prop.getName(), entProp.getProperty(ApplicationEntityProperty.NAME).getValue().getAsString())){
                			found=true;  
                			
                			if(restoreDefault){
                				
                				fillApplicationEntityProperty(entProp, entity, prop);
                				
                				UtilsCrud.update(this.getServiceManager(), entProp, serviceData);

                			}
                			break;
                		}
                	}
                	if(!found){
                		IEntity<ApplicationEntityProperty> property = UtilsCrud.create(this.getServiceManager(), ApplicationEntityProperty.class, serviceData);

        				fillApplicationEntityProperty(property, entity, prop);

        				entityProperties.add(property);
        				
        				UtilsCrud.update(this.getServiceManager(), property, serviceData);
                	}
                }

                //detectar se os grupos das propriedades da entidade q estao no metadados estão no banco
                //se encontrar e restoreDefault=true, atualizar os grupos
                //se nao encontrar, criar o grupo no banco
                for(IGroupMetadata groupMetadata: entityInfoDefault.getGroups()){
                    boolean found = false;
                	for(IEntity<ApplicationEntityPropertyGroup> entGroup: entityGroups){ 

                		if(StringUtils.equals(groupMetadata.getName(), entGroup.getProperty(ApplicationEntityProperty.NAME).getValue().getAsString())){
                			found=true;  
                			
                			if(restoreDefault){
                                /* Encontrou no banco, restaurando os valores padrões */
                            	fillApplicationEntityPropertyGroup(entGroup, entity, groupMetadata);

                            	UtilsCrud.update(this.getServiceManager(), entGroup, serviceData);
                			}
                			break;
                		}
                	}
                	if(!found){
                		IEntity<ApplicationEntityPropertyGroup> group = UtilsCrud.create(this.getServiceManager(), ApplicationEntityPropertyGroup.class, serviceData);

                    	fillApplicationEntityPropertyGroup(group, entity, groupMetadata);

                    	entityGroups.add(group); 
                    	
                    	UtilsCrud.update(this.getServiceManager(), group, serviceData);
                	}
                }
                
                
                //detectar se as propriedades da entidade q estao no banco estão no metadados
                //se nao encontrar, deletar a propriedade no banco
                for(Iterator<IEntity<ApplicationEntityProperty>> it = entityProperties.iterator(); it.hasNext();){ 
                	IEntity<ApplicationEntityProperty> entProp = it.next();
                	
                    boolean found = false;
                	for(IPropertyMetadata prop: entityInfo.getPropertiesMetadata().values()){
                		if(StringUtils.equals(prop.getName(), entProp.getProperty(ApplicationEntityProperty.NAME).getValue().getAsString())){
                			found=true;  
                           	break;
                		}
                	}
                	if(!found){
                		it.remove();
                		
                		entityProperties.remove(entProp);
                		
                		UtilsCrud.delete(this.getServiceManager(), entProp, serviceData);
                	}
                }
                
                //detectar se os grupos das propriedades da entidade q estao no banco estão no metadados
                //se nao encontrar, deletar o grupo da propriedade no banco
                for(Iterator<IEntity<ApplicationEntityPropertyGroup>> it = entityGroups.iterator(); it.hasNext();){ 
                	IEntity<ApplicationEntityPropertyGroup> entGroup = it.next();

                    boolean found = false;
                	for(IGroupMetadata groupMetadata: entityInfo.getGroups()){

                		if(StringUtils.equals(groupMetadata.getName(), entGroup.getProperty(ApplicationEntityProperty.NAME).getValue().getAsString())){
                			found=true;  
                			break;
                		}
                	}
                	if(!found){
                		
                		it.remove();

                		entityGroups.remove(entGroup);
                		
                		UtilsCrud.delete(this.getServiceManager(), entGroup, serviceData);
                	}
                }
                
            	//executa o serviço
            	UtilsCrud.update(this.getServiceManager(), entity, serviceData);

            	if (log.isDebugEnabled())
                    log.debug("Entidade " + entityInfo.getLabel() + "atualizada com sucesso");
            }

        }
        
    }

    private void manageProcessSecurity(ServiceData serviceData, IProcessManager processManager)  throws BusinessException
    {
        log.debug("Registrando os processos do Sistema");
        // Obtendo a lista de processos do sistema
        
        for (Class<?> processClass : processManager.getAllProcessesClasses()){
            // Verifica se o módulo não está cadastrado
            
            //para cada processo, verificar o modulo correspondente
            IEntity<ApplicationModule> module = manageModuleSecurity(processClass, serviceData);

            // Obtem o nome simples do processo
            String processName = processClass.getSimpleName();
            
            //verifica se o processo esta cadastrado
            ServiceData sd = new ServiceData(ListService.SERVICE_NAME, serviceData);
            
            //preenche propriedades do serviço
            sd.getArgumentList().setProperty(ListService.CLASS, ApplicationProcess.class);
            //processClass.getSimpleName - pega o nome do processo pra gravar no banco
            sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, ApplicationProcess.NAME +  "= '" + processName + "'");
            
            //executa o serviço
            this.getServiceManager().execute(sd);
            
            //verificando se foi encontrado algum processo cadastrado        	
            //o item 0 possui uma EntityList
            IEntityList<ApplicationProcess> enl = sd.getFirstOutput();
            IEntity<ApplicationProcess> process; 
            
            if (enl.size() == 0){
                if (log.isDebugEnabled())
                    log.debug("Processo " + processClass +" não encontrado, cadastrando.");
                //não encontrou, cadastrar o processo
                process = UtilsCrud.create(this.getServiceManager(), ApplicationProcess.class, serviceData);


                if (log.isDebugEnabled())
                	log.debug("Processo " + processClass + "cadastrada com sucesso");
            }
            else
            {
                //encontrou, pega a primeira da lista
            	process = enl.getFirst();
            	
                if (log.isDebugEnabled())
                    log.debug("Processo " + processClass + "ja cadastrado");
            }
            

            //preenche os dados do processo
            process.getProperty(ApplicationProcess.NAME).getValue().setAsString(processName);
            
            // Se tiver a anotação com os metadados, pega os demais dados primitivos e descritivos 
            if(processClass.isAnnotationPresent(ProcessMetadata.class)){
            	ProcessMetadata annotation = (ProcessMetadata) processClass.getAnnotation(ProcessMetadata.class);
            	process.getProperty(ApplicationProcess.LABEL).getValue().setAsString(annotation.label()); 
            	process.getProperty(ApplicationProcess.HINT).getValue().setAsString(annotation.hint()); 
            	process.getProperty(ApplicationProcess.DESCRIPTION).getValue().setAsString(annotation.description());
            }

            process.getProperty(ApplicationProcess.APPLICATION_MODULE).getValue().setAsEntity(module); //processo exige uma propriedade modulo
            
            //executa o serviço
            UtilsCrud.update(this.getServiceManager(), process, serviceData);

        }
    }
    
    private IEntity<ApplicationModule> manageModuleSecurity(Class<?> klazz, ServiceData serviceData) throws BusinessException 
    {
    	/* Obtem o nome do módulo de uma class */
    	String moduleName = this.getServiceManager().getApplication().extractModuleName(klazz);
    	
        log.debug("Obtendo a lista de módulos cadastrados do sistema.");
        // Verifica se o módulo não está cadastrado
        ServiceData sd = new ServiceData(ListService.SERVICE_NAME, serviceData);
        //preenche propriedades do serviço
        sd.getArgumentList().setProperty(ListService.CLASS, ApplicationModule.class);
        sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, ApplicationModule.NAME +  "='" + moduleName + "'");
        
        //executa o serviço
        this.getServiceManager().execute(sd);
        
        if (log.isDebugEnabled())
            log.debug("Verificando se o modulo " + moduleName + "ja foi cadastrado");
        
        //verificando se foi encontrado algum módulo cadastrado        	
        IEntityList<ApplicationModule> enl = sd.getFirstOutput();
        if (enl.isEmpty()){

            if (log.isDebugEnabled())
                log.debug("Modulo " + moduleName + "não encontrado, cadastrando...");

            //não encontrou, cadastrar o módulo
            IEntity<ApplicationModule> module = UtilsCrud.create(this.getServiceManager(), ApplicationModule.class, serviceData);

            //preenche os dados do módulo
            module.getProperty(ApplicationModule.NAME).getValue().setAsString(moduleName);
            
            //Grava o novo módulo
            UtilsCrud.update(this.getServiceManager(), module, serviceData);

            if (log.isDebugEnabled())
                log.debug("Modulo " + moduleName + "cadastrado com sucesso");
            
            return module;
        }
        
        if (log.isDebugEnabled())
            log.debug("Modulo " + moduleName + "já cadastrado");

        //se encontrado
        return enl.get(0); //pegar o primeiro da lista, que é uma entity
        
    }

    private void fillApplicationEntity(IEntity<ApplicationEntity> applicationEntity, IEntity<ApplicationModule> applicationModule, IEntityMetadata entityInfo) throws BusinessException{
    	//preenche os dados da entidade
        applicationEntity.getProperty(ApplicationEntity.APPLICATION_MODULE).getValue().setAsEntity(applicationModule); //processo exige uma propriedade modulo
        applicationEntity.getProperty(ApplicationEntity.COLOR_NAME).getValue().setAsString(entityInfo.getColorName()); 
        applicationEntity.getProperty(ApplicationEntity.DESCRIPTION).getValue().setAsString(entityInfo.getDescription()); 
        applicationEntity.getProperty(ApplicationEntity.CLASS_NAME).getValue().setAsString(entityInfo.getType().getName()); 
        applicationEntity.getProperty(ApplicationEntity.HINT).getValue().setAsString(entityInfo.getHint()); 
        applicationEntity.getProperty(ApplicationEntity.LABEL).getValue().setAsString(entityInfo.getLabel());
        applicationEntity.getProperty(ApplicationEntity.NAME).getValue().setAsString(entityInfo.getType().getSimpleName());
        applicationEntity.getProperty(ApplicationEntity.RUN_QUERY_ON_OPEN).getValue().setAsBoolean(entityInfo.getRunQueryOnOpen()); 

    }
    
    private void fillApplicationEntityProperty(IEntity<ApplicationEntityProperty> applicationEntityProperty, IEntity<ApplicationEntity> applicationEntity, IPropertyMetadata propertyInfo) throws BusinessException{
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.ALLOW_SUB_QUERY).getValue().setAsBoolean(propertyInfo.isAllowSubQuery());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.APPLICATION_ENTITY).getValue().setAsEntity(applicationEntity);
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.COLOR_NAME).getValue().setAsString(propertyInfo.getColorName());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.DEFAULT_VALUE).getValue().setAsString(propertyInfo.getDefaultValue());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.DESCRIPTION).getValue().setAsString(propertyInfo.getDescription());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.DISPLAY_FORMAT).getValue().setAsString(propertyInfo.getDisplayFormat());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.EDIT_MASK).getValue().setAsString(propertyInfo.getEditMask());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.EDIT_SHOW_LIST).getValue().setAsBoolean(propertyInfo.isEditShowList());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.HINT).getValue().setAsString(propertyInfo.getHint());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.INDEX_PROPERTY).getValue().setAsInteger(propertyInfo.getIndex());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.INDEX_GROUP).getValue().setAsInteger(propertyInfo.getGroup());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.LABEL).getValue().setAsString(propertyInfo.getLabel());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.MAXIMUM).getValue().setAsDouble(propertyInfo.getMaximum());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.MINIMUM).getValue().setAsDouble(propertyInfo.getMinimum());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.NAME).getValue().setAsString(propertyInfo.getName());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.READ_ONLY).getValue().setAsBoolean(propertyInfo.isReadOnly());
    	applicationEntityProperty.getProperty(ApplicationEntityProperty.REQUIRED).getValue().setAsBoolean(propertyInfo.isRequired());

        List<String> valuesList = propertyInfo.getValuesList();
        String string="";
        for(String str: valuesList){
        	string+=str + ",";
        }
        string=StringUtils.stripEnd(string, ",");
        
        applicationEntityProperty.getProperty(ApplicationEntityProperty.VALUES_LIST).getValue().setAsString(string);
        applicationEntityProperty.getProperty(ApplicationEntityProperty.VISIBLE).getValue().setAsBoolean(propertyInfo.isVisible());
    	applicationEntity.getProperty(ApplicationEntity.APPLICATION_ENTITY_PROPERTY).getValue().<ApplicationEntityProperty>getAsEntityList().add(applicationEntityProperty); 

    }

    private void fillApplicationEntityPropertyGroup(IEntity<ApplicationEntityPropertyGroup> applicationEntityPropertyGroup, IEntity<ApplicationEntity> applicationEntity, IGroupMetadata groupInfo) throws BusinessException{
    	applicationEntityPropertyGroup.getProperty(ApplicationEntityPropertyGroup.APPLICATION_ENTITY).getValue().setAsEntity(applicationEntity);
    	applicationEntityPropertyGroup.getProperty(ApplicationEntityPropertyGroup.COLOR_NAME).getValue().setAsString(groupInfo.getColorName());
    	applicationEntityPropertyGroup.getProperty(ApplicationEntityPropertyGroup.DESCRIPTION).getValue().setAsString(groupInfo.getDescription());
    	applicationEntityPropertyGroup.getProperty(ApplicationEntityPropertyGroup.HINT).getValue().setAsString(groupInfo.getHint());
    	applicationEntityPropertyGroup.getProperty(ApplicationEntityPropertyGroup.INDEX_GROUP).getValue().setAsInteger(groupInfo.getIndex());
    	applicationEntityPropertyGroup.getProperty(ApplicationEntityPropertyGroup.LABEL).getValue().setAsString(groupInfo.getLabel());
    	applicationEntityPropertyGroup.getProperty(ApplicationEntityPropertyGroup.NAME).getValue().setAsString(groupInfo.getName());
    	
    }    
    public static void main(String[] args)
	{
		if(OverwritePasswordProcess.class.isAssignableFrom(IRunnableEntityProcess.class))
			System.out.println("okk");
		else
			System.out.println("NOK");

		if(OverwritePasswordProcess.class.equals(IRunnableEntityProcess.class))
			System.out.println("okk");
		else
			System.out.println("NOK");

		if(IRunnableEntityProcess.class.isAssignableFrom(OverwritePasswordProcess.class))
			System.out.println("okk");
		else
			System.out.println("NOK");
	}
}