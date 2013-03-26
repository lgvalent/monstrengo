package br.com.orionsoft.monstrengo.security.services;

import org.apache.commons.codec.digest.DigestUtils;

import br.com.orionsoft.monstrengo.security.services.ManageSecurityStructureService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.IProcessManager;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.RightCrud;
import br.com.orionsoft.monstrengo.security.entities.RightProcess;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;

/**
 * Serviço para criar ou manutenir os direitos de um operador e do grupo especificado.<br>
 *
 * Antes de iniciar a criação dos direitos o serviço executa outro serviço para
 * verificar atualizações nas estruturas do sistema como novos Módulos, Entidades ou Processos.
 *
 * Esta classe prepara a estrutura básica para a criação de users, groups e rights, definindo
 * as permissões sobre os processos e o CRUD.
 *
 * <p><b>Procedimento:</b><br>
 * Mantem as estruturas básicas do sistema.<br>
 * Busca ou cria o operador solicitado.<br>
 * Busca ou cria o grupo solicitado.<br>
 * Liga o operador ao grupo e cria seus direitos.<br>
 * <b>Não retorna nada;</b><br>
 *
 * @author Lucio 20060427
 * @version 20060501 Lucio
 *
 * @spring.bean id="CreateSecurityStructureService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="transactional" value="true"
 */
public class CreateSecurityStructureService extends ServiceBasic {

	public static String SERVICE_NAME = "CreateSecurityStructureService";

    public static String IN_USER_LOGIN = "userLogin";
    public static String IN_GROUP_NAME = "groupName";
    public static String IN_ALLOW_ALL_BOOL = "allowAll";
    public static String IN_PROCESS_MANAGER = "processManager";
   

    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException
    {
        try
        {
            log.debug("Iniciando a execução do servico CreateSecurityStructureService");

            this.addInfoMessage(serviceData, "SERVICE_START");

            // Pega os argumentos
       		String userLogin = (String) serviceData.getArgumentList().getProperty(IN_USER_LOGIN);

       		String groupName = (String) serviceData.getArgumentList().getProperty(IN_GROUP_NAME);

        	boolean allowAll = false;
        	if(serviceData.getArgumentList().containsProperty(IN_ALLOW_ALL_BOOL))
        		allowAll = (Boolean) serviceData.getArgumentList().getProperty(IN_ALLOW_ALL_BOOL);

            IProcessManager inProcessManager = (IProcessManager) serviceData.getArgumentList().getProperty(IN_PROCESS_MANAGER);

            //cadastra entidades e processos no banco caso não estejam cadastrados
        	ServiceData service = new ServiceData(ManageSecurityStructureService.SERVICE_NAME, serviceData);
        	service.getArgumentList().setProperty(ManageSecurityStructureService.IN_RESTORE_DEFAULT_OPT, true);
        	service.getArgumentList().setProperty(ManageSecurityStructureService.IN_PROCESS_MANAGER, inProcessManager);
        	this.getServiceManager().execute(service);

        	// Buscando o operador e grupo para depois criar seus direitos
            IEntity<ApplicationUser> user = manageUser(userLogin, serviceData);
            IEntity<SecurityGroup> group = manageGroup(groupName, serviceData);

            if(allowAll)
            	this.addInfoMessage(serviceData, "RIGHT_ALLOW_ALL");
            else
            	this.addInfoMessage(serviceData, "RIGHT_NOT_ALLOW_ALL");

            manageRigths(user, group, allowAll, serviceData);
            this.addInfoMessage(serviceData, "RIGHT_SUCCESS");
        }
        catch (BusinessException e)
        {
        	e.printStackTrace();
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }
    }

    private IEntity<ApplicationUser> manageUser(String userLogin, ServiceData ownerSD) throws BusinessException{
    	if(log.isDebugEnabled())
    		log.debug("Procurando se o operador já existe:" + userLogin);

    	this.addInfoMessage(ownerSD, "USER_FIND", userLogin);

    	ServiceData sd = new ServiceData(ListService.SERVICE_NAME, ownerSD);
        sd.getArgumentList().setProperty(ListService.CLASS, ApplicationUser.class);
        sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, ApplicationUser.LOGIN +  "= '" + userLogin + "'");
        this.getServiceManager().execute(sd);

        IEntityList<ApplicationUser> users = sd.getFirstOutput();
        IEntity<ApplicationUser> user=null;

        if(users.isEmpty()){
        	log.debug("Operador não encontrado. Cadastrando...");
        	this.addInfoMessage(ownerSD, "USER_INSERT");
        	user = UtilsCrud.create(this.getServiceManager(), ApplicationUser.class, ownerSD);
        	user.setPropertyValue(ApplicationUser.NAME, userLogin);
        	user.setPropertyValue(ApplicationUser.LOGIN, userLogin);
        	user.setPropertyValue(ApplicationUser.PASSWORD, DigestUtils.md5Hex(userLogin));
        	
        	UtilsCrud.update(this.getServiceManager(), user,  ownerSD);
        	
        	if(log.isDebugEnabled())
        	   log.debug("Operador " + userLogin + " criado com o id " + user.getId());


        }else{
        	log.debug("Operador já cadastrado. Retornando a referência...");
        	this.addInfoMessage(ownerSD, "USER_FOUND");
            user = users.getFirst();
        }

        return user;
    }

    private IEntity<SecurityGroup> manageGroup(String groupName, ServiceData ownerSD) throws BusinessException{
    	if(log.isDebugEnabled())
    		log.debug("Procurando se o grupo já existe:" + groupName);

    	this.addInfoMessage(ownerSD, "GROUP_FIND", groupName);

    	ServiceData sd = new ServiceData(ListService.SERVICE_NAME, ownerSD);
        sd.getArgumentList().setProperty(ListService.CLASS, SecurityGroup.class);
        sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, SecurityGroup.NAME +  "= '" + groupName + "'");
        this.getServiceManager().execute(sd);

        IEntityList<SecurityGroup> groups = sd.getFirstOutput();
        IEntity<SecurityGroup> group=null;

        if(groups.isEmpty()){
        	log.debug("Grupo não encontrado. Cadastrando...");
        	this.addInfoMessage(ownerSD, "GROUP_INSERT");
        	group = UtilsCrud.create(this.getServiceManager(), SecurityGroup.class, ownerSD);
        	group.setPropertyValue(SecurityGroup.NAME, groupName);
        	UtilsCrud.update(this.getServiceManager(), group, ownerSD);
        	if(log.isDebugEnabled())
        	   log.debug("Grupo " + groupName + " criado com o id " + group.getId());


        }else{
        	log.debug("Grupo já cadastrado. Retornando a referência...");
        	this.addInfoMessage(ownerSD, "GROUP_FOUND");
            group = groups.getFirst();
        }

        return group;
    }

	private void manageRigths(IEntity<ApplicationUser> user, IEntity<SecurityGroup> group, boolean allAllowed, ServiceData ownerSD) throws BusinessException{
    	log.debug("Ligando o operador ao grupo");
    	ApplicationUser userObj = user.getObject();
    	userObj.getSecurityGroups().add(group.getObject());
    	UtilsCrud.update(this.getServiceManager(), user, ownerSD);

    	log.debug("Buscando as entidades que existem no banco");
    	IEntityCollection<ApplicationEntity> entities = UtilsCrud.list(this.getServiceManager(), ApplicationEntity.class, ownerSD);

    	log.debug("Atribuindo os direitos para as entidades. Permitir tudo?" + allAllowed);
    	for (IEntity<ApplicationEntity> ent : entities){
    		/*
    		 * Aqui não está sendo usado DefineRightCrudService porque os direitos de cada
    		 * entidade são adicionados no vetor rights do tipoIEntity
    		 * Isso é feito para que depois de construir os usuários, grupos e direitos
    		 * no Banco de Dados, seja possível apagá-los.
    		 */
        	if(log.isDebugEnabled())
        		log.debug("Verificando se o direito já existe para " + ent.toString());

        	ServiceData sd = new ServiceData(ListService.SERVICE_NAME, ownerSD);
            sd.getArgumentList().setProperty(ListService.CLASS, RightCrud.class);
            sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, RightCrud.SECURITY_GROUP +  "=" + group.getId() + " and " + RightCrud.APPLICATION_ENTITY +  "=" + ent.getId());
            this.getServiceManager().execute(sd);

            IEntityList<RightCrud> rigths = sd.getFirstOutput();

    		IEntity<RightCrud> rightCrud = null;
        	if(rigths.isEmpty()){
        		log.debug("Direito não existente. Cadastrando...");
        		/* Define a ligação do novo direito entre Grupo e Entity */
        		rightCrud = UtilsCrud.create(this.getServiceManager(), RightCrud.class, ownerSD); //direito CRUD
        		rightCrud.setPropertyValue(RightCrud.SECURITY_GROUP, group);
        		rightCrud.setPropertyValue(RightCrud.APPLICATION_ENTITY, ent);
        	}else{
        		log.debug("Direito já existente.");
        		rightCrud = rigths.getFirst();
        	}
        	try {
        		IEntityMetadata entityMetadata =  this.getServiceManager().getEntityManager().getEntityMetadata(Class.forName(ent.getObject().getClassName()));

        		/* Define o direito de acordo com o passado */
        		rightCrud.setPropertyValue(RightCrud.CREATE_ALLOWED, allAllowed && entityMetadata.getCanCreate());
        		rightCrud.setPropertyValue(RightCrud.DELETE_ALLOWED, allAllowed && entityMetadata.getCanDelete());
        		rightCrud.setPropertyValue(RightCrud.RETRIEVE_ALLOWED, allAllowed && entityMetadata.getCanRetrieve());
        		rightCrud.setPropertyValue(RightCrud.UPDATE_ALLOWED, allAllowed && entityMetadata.getCanUpdate());
        	} catch (Exception e) {
        		// TODO Lucio 20120515: Se deu ClassNotFound, a classe já não existe mais. Acho que o ManageSecurityStructureService
        		// deveria remover entidades não mais usadas. Contudo, o sistema pode estar sendo executado com um banco mais completo :(
//        		throw new ServiceException(MessageList.createSingleInternalError(e));
        	}

    		log.debug("Atualizando o direito no banco.");
    		UtilsCrud.update(this.getServiceManager(), rightCrud, ownerSD);

    	}

    	log.debug("Buscando os processos que existem no banco");
    	IEntityCollection<ApplicationProcess> processes = UtilsCrud.list(this.getServiceManager(), ApplicationProcess.class, ownerSD);

    	log.debug("Atribuindo os direitos para os processos. Permitir tudo?" + allAllowed);
    	for (IEntity<ApplicationProcess> proc : processes){
        	if(log.isDebugEnabled())
        		log.debug("Verificando se o direito já existe para " + proc.toString());

        	ServiceData sd = new ServiceData(ListService.SERVICE_NAME, ownerSD);
            sd.getArgumentList().setProperty(ListService.CLASS, RightProcess.class);
            sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, RightProcess.SECURITY_GROUP +  "=" + group.getId() + " and " + RightProcess.APPLICATION_PROCESS +  "=" + proc.getId());
            this.getServiceManager().execute(sd);

            IEntityList<RightProcess> procs = sd.getFirstOutput();

    		IEntity<RightProcess> rightProcess = null;
        	if(procs.isEmpty()){
        		log.debug("Direito não existente. Cadastrando...");
        		/* Define a ligação do novo direito entre Grupo e Entity */
        		rightProcess = UtilsCrud.create(this.getServiceManager(), RightProcess.class, ownerSD);
        		rightProcess.setPropertyValue(RightProcess.SECURITY_GROUP, group);
        		rightProcess.setPropertyValue(RightProcess.APPLICATION_PROCESS, proc);
        	}else{
        		log.debug("Direito já existente.");
        		rightProcess = procs.getFirst();
        	}

    		/* Define o direito de acordo com o passado */
        	rightProcess.setPropertyValue(RightProcess.EXECUTE_ALLOWED, allAllowed);
    		log.debug("Atualizando o direito no banco.");
    		UtilsCrud.update(this.getServiceManager(), rightProcess, ownerSD);
    	}

    }
}
