package br.com.orionsoft.monstrengo.core.test;

import java.util.ArrayList;
import java.util.List;

import br.com.orionsoft.monstrengo.core.test.BasicStructureRight;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.IProcessManager;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationProcess;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;
import br.com.orionsoft.monstrengo.security.entities.RightCrud;
import br.com.orionsoft.monstrengo.security.entities.RightProcess;
import br.com.orionsoft.monstrengo.security.entities.SecurityGroup;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
import br.com.orionsoft.monstrengo.security.processes.AuthenticateProcess;
import br.com.orionsoft.monstrengo.security.services.ManageSecurityStructureService;

/**
 * Esta classe prepara a estrutura básica para a criação de users, groups e rights, definindo 
 * as permissões sobre os processos e o CRUD.
 * <p>São criados 2 usuários, 2 grupos e 2 direitos. O primeiro usuário (user1) pertence ao grupo1 e 
 * tem todos os direitos <code>true</code> (administrador); o segundo usuário (user2) pertence ao
 * grupo2 e não possui direito algum <code>(false)</code>.
 * 
 * @author estagio
 */
public class BasicStructureRight {
	/*
	 * São criadas 3 listas, onde são armazenados as entidades do tipo user, group e right criadas,
	 * para posterior eliminação das mesmas no Banco de Dados
	 */
	private static List<IEntity<?>> users = new ArrayList<IEntity<?>>();
    private static List<IEntity<?>> groups = new ArrayList<IEntity<?>>();
    private static List<IEntity<?>> rights = new ArrayList<IEntity<?>>();
    
        
    public static final String USER_1 = "admin";
    public static final String USER_2 = "user";
    
    public static final String GROUP_1 = "admin";
    public static final String GROUP_2 = "user";
    
    public static UserSession userSession1 = null;
    public static UserSession userSession2 = null;
    
    
    /**
     * Cria 2 usuários que fazem parte de 2 grupos e tem 2 direitos diferentes
     * user1 - pertence group1 - tem todos os direitos
     * user2 - pertence group2 - não possui direito algum
     */
    public static void createRights(IProcessManager processManager){
        try{
        	IServiceManager serviceManager = processManager.getServiceManager();

        	//cadastra entidades e processos no banco caso não estejam cadastrados
        	ServiceData service = new ServiceData(ManageSecurityStructureService.SERVICE_NAME, null);
        	service.getArgumentList().setProperty(ManageSecurityStructureService.IN_PROCESS_MANAGER, processManager);
        	serviceManager.execute(service);        	
        	
        	//Criando 2 usuários, 2 grupos e 2 direitos
        	//user1 pertencerá ao grupo1 e terá todos os direitos (administrador)	
//        	System.out.println("user1");
        	IEntity<?> user1 = UtilsCrud.create(serviceManager, ApplicationUser.class, null);
        	user1.setPropertyValue(ApplicationUser.LOGIN, USER_1);
        	user1.setPropertyValue(ApplicationUser.PASSWORD, USER_1);
        	UtilsCrud.update(serviceManager, user1, null);
        	users.add(user1);
//        	System.out.println("ID - user1 - " + user1.getId());
        	
        	//user2 pertencerá ao grupo2 e não terá direitos de acesso (tudo negado) 
//        	System.out.println("user2");
        	IEntity<?> user2 = UtilsCrud.create(serviceManager, ApplicationUser.class, null);
        	user2.setPropertyValue(ApplicationUser.LOGIN, USER_2);
        	user2.setPropertyValue(ApplicationUser.PASSWORD, USER_2);
        	UtilsCrud.update(serviceManager, user2, null);
        	users.add(user2);
//        	System.out.println("ID - user2 - " + user2.getId());
        	
        	//Criando grupos e associando aos usuários
//        	System.out.println("group1");
        	IEntity<?> group1 = UtilsCrud.create(serviceManager, SecurityGroup.class, null);
        	group1.setPropertyValue(SecurityGroup.NAME, GROUP_1);
        	UtilsCrud.update(serviceManager, group1, null);
        	group1 = UtilsCrud.retrieve(serviceManager, SecurityGroup.class, group1.getId(), null);
        	group1.getProperty(SecurityGroup.USERS).getValue().getAsEntityCollection().add((IEntity<Object>) user1);
        	UtilsCrud.update(serviceManager, group1, null);
        	groups.add(group1);
        	
//        	System.out.println("group2");
        	IEntity<?> group2 = UtilsCrud.create(serviceManager, SecurityGroup.class, null);
        	group2.setPropertyValue(SecurityGroup.NAME, GROUP_2);
        	UtilsCrud.update(serviceManager, group2, null);
        	group2 = UtilsCrud.retrieve(serviceManager, SecurityGroup.class, group2.getId(), null);
        	group2.getProperty(SecurityGroup.USERS).getValue().getAsEntityCollection().add((IEntity<Object>) user2);
        	UtilsCrud.update(serviceManager, group2, null);
        	groups.add(group2);
        	
   	    	//Buscando as entidades e os processos que existem no banco
//        	System.out.println("ServiceData");
        	ServiceData sdEntity = new ServiceData(ListService.SERVICE_NAME, null);
        	sdEntity.getArgumentList().setProperty(ListService.CLASS, ApplicationEntity.class);
        	serviceManager.execute(sdEntity);
        	IEntityCollection<ApplicationEntity> entities = sdEntity.getFirstOutput();
        	
        	ServiceData sdProcess = new ServiceData(ListService.SERVICE_NAME, null);
        	sdProcess.getArgumentList().setProperty(ListService.CLASS, ApplicationProcess.class);
        	serviceManager.execute(sdProcess);
        	IEntityCollection<ApplicationProcess> processes = sdProcess.getFirstOutput();
        	
//        	System.out.println("Right Entity");
        	//Definindo direito CRUD - user1 / user2
        	for (IEntity ent : entities){
        		//user1 - group1
        		/*
        		 * Aqui não está sendo usado DefineRightCrudService porque os direitos de cada 
        		 * entidade são adicionados no vetor rights do tipoIEntity
        		 * Isso é feito para que depois de construir os usuários, grupos e direitos
        		 * no Banco de Dados, seja possível apagá-los. 
        		 */
        		IEntity rightCrud1 = UtilsCrud.create(serviceManager, RightCrud.class, null); //direito CRUD
        		rightCrud1.setPropertyValue(RightCrud.SECURITY_GROUP, group1);
        		rightCrud1.setPropertyValue(RightCrud.CREATE_ALLOWED, true);
        		rightCrud1.setPropertyValue(RightCrud.DELETE_ALLOWED, true);
        		rightCrud1.setPropertyValue(RightCrud.RETRIEVE_ALLOWED, true);
        		rightCrud1.setPropertyValue(RightCrud.UPDATE_ALLOWED, true);
        		rightCrud1.setPropertyValue(RightCrud.APPLICATION_ENTITY, ent);
        		UtilsCrud.update(serviceManager, rightCrud1, null);
        		rights.add(rightCrud1);
        		
        		//user2 - group2
        		IEntity rightCrud2 = UtilsCrud.create(serviceManager, RightCrud.class, null); //direito CRUD
        		rightCrud2.setPropertyValue(RightCrud.SECURITY_GROUP, group2);
        		rightCrud2.setPropertyValue(RightCrud.CREATE_ALLOWED, false);
        		rightCrud2.setPropertyValue(RightCrud.DELETE_ALLOWED, false);
        		rightCrud2.setPropertyValue(RightCrud.RETRIEVE_ALLOWED, false);
        		rightCrud2.setPropertyValue(RightCrud.UPDATE_ALLOWED, false);
        		rightCrud2.setPropertyValue(RightCrud.APPLICATION_ENTITY, ent);
        		UtilsCrud.update(serviceManager, rightCrud2, null);
        		rights.add(rightCrud2);
        		
//        		IEntity testRight1 = UtilsCrud.retrieve(serviceManager, RightCrud.class, rightCrud1.getId());
//        		System.out.println("user1 - CREATE - TRUE - " + testRight1.getProperty(RightCrud.CREATE_ALLOWED).getValue().getAsBoolean());
//        		System.out.println("user1 - RETRIEVE - TRUE - " + testRight1.getProperty(RightCrud.RETRIEVE_ALLOWED).getValue().getAsBoolean());
//        		System.out.println("user1 - UPDATE - TRUE - " + testRight1.getProperty(RightCrud.UPDATE_ALLOWED).getValue().getAsBoolean());
//        		System.out.println("user1 - DELETE - TRUE - " + testRight1.getProperty(RightCrud.DELETE_ALLOWED).getValue().getAsBoolean());
//        		
//        		IEntity testRight2 = UtilsCrud.retrieve(serviceManager, RightCrud.class, rightCrud2.getId());
//        		System.out.println("user2 - CREATE - FALSE - " + testRight2.getProperty(RightCrud.CREATE_ALLOWED).getValue().getAsBoolean());
//        		System.out.println("user2 - RETRIEVE - FALSE - " + testRight2.getProperty(RightCrud.RETRIEVE_ALLOWED).getValue().getAsBoolean());
//        		System.out.println("user2 - UPDATE - FALSE - " + testRight2.getProperty(RightCrud.UPDATE_ALLOWED).getValue().getAsBoolean());
//        		System.out.println("user2 - DELETE - FALSE - " + testRight2.getProperty(RightCrud.DELETE_ALLOWED).getValue().getAsBoolean());

        	}
        	
//        	System.out.println();
//        	System.out.println("Right ProcessMetadata");
        	//Definindo direito ProcessMetadata - user1 / user2
        	for (IEntity proc : processes){
        		//user1 - group1
        		IEntity rightProcess1 = UtilsCrud.create(serviceManager, RightProcess.class, null);
        		rightProcess1.setPropertyValue(RightProcess.SECURITY_GROUP, group1);
        		rightProcess1.setPropertyValue(RightProcess.EXECUTE_ALLOWED, true);
        		rightProcess1.setPropertyValue(RightProcess.APPLICATION_PROCESS, proc);
        		UtilsCrud.update(serviceManager, rightProcess1, null);
        		rights.add(rightProcess1);
        		
        		//user2 - group2
        		IEntity rightProcess2 = UtilsCrud.create(serviceManager, RightProcess.class, null);
        		rightProcess2.setPropertyValue(RightProcess.SECURITY_GROUP, group2);
        		rightProcess2.setPropertyValue(RightProcess.EXECUTE_ALLOWED, false);
        		rightProcess2.setPropertyValue(RightProcess.APPLICATION_PROCESS, proc);
        		UtilsCrud.update(serviceManager, rightProcess2, null);
        		rights.add(rightProcess2);
        		
//        		IEntity testRight1 = UtilsCrud.retrieve(serviceManager, RightProcess.class, rightProcess1.getId());
//        		System.out.println("user1 - EXECUTE - TRUE - " + testRight1.getProperty(RightProcess.EXECUTE_ALLOWED).getValue().getAsBoolean());
//        		
//        		IEntity testRight2 = UtilsCrud.retrieve(serviceManager, RightProcess.class, rightProcess2.getId());
//        		System.out.println("user2 - EXECUTE - FALSE - " + testRight2.getProperty(RightProcess.EXECUTE_ALLOWED).getValue().getAsBoolean());
        	}
        	
        	//autenticando usuários user1 e user2
        	authenticateUser(processManager);	

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private static void authenticateUser(IProcessManager processManager){
    	try{
    		//autenticando usuários criados
//    		System.out.println(":Obtendo um Id válido para fornecer ao processo.");
    		ServiceData sd = new ServiceData(ListService.SERVICE_NAME, null);
    		sd.getArgumentList().setProperty(ListService.CLASS, ApplicationUser.class);
    		processManager.getServiceManager().execute(sd);
    		
    		//autenticando usuario (user1)
//    		System.out.println("Autenticando usuario user1");
    		AuthenticateProcess authProc = (AuthenticateProcess) processManager.createProcessByName(AuthenticateProcess.PROCESS_NAME, null);
    		authProc.setLogin(BasicStructureRight.USER_1);
    		authProc.setPassword(BasicStructureRight.USER_1);
    		if (authProc.runAuthenticate()){
    			userSession1 = authProc.getUserSession();	
    		}else{
    			throw new BusinessException(authProc.getMessageList());
    		}
    		
    		//autenticando usuario (user2)
//    		System.out.println("Autenticando usuario user2");
    		AuthenticateProcess authProc2 = (AuthenticateProcess) processManager.createProcessByName(AuthenticateProcess.PROCESS_NAME, null);
    		authProc2.setLogin(BasicStructureRight.USER_2);
    		authProc2.setPassword(BasicStructureRight.USER_2);
    		if (authProc2.runAuthenticate()){
    			userSession2 = authProc2.getUserSession();	
    		}else{
    			throw new BusinessException(authProc2.getMessageList());
    		}
    		
    	}catch (BusinessException e){
            UtilsTest.showMessageList(e.getErrorList());
        }
    }
    
    /**
     * Apaga os elementos inseridos para teste no Banco de Dados.
     * <br>Deve ser apagado na ordem inversa ao que foi criado devido às dependencias, 
     * no caso, como foram criados na ordem: user, group, right, são apagados na ordem: right, group, user.
     */
    public static void destroyRigths(IProcessManager processManager){
            try{
            	IServiceManager serviceManager = processManager.getServiceManager();
            	
            	//Deletando os direitos
//            	System.out.println("Deletando " + rights.size() + " Rights");
            	for (int i = 0; i < rights.size(); i++){
            		UtilsCrud.delete(serviceManager, rights.get(i), null);
            	}
            	
            	//Deletando os grupos
//            	System.out.println("Deletando " + groups.size() + " Groups");
            	for (int i = 0; i < groups.size(); i++){
            		UtilsCrud.delete(serviceManager, groups.get(i), null);
            	}
            	
            	//Deletando os usuários
//            	System.out.println("Deletando " + users.size() + " Users");
            	for (int i = 0; i < users.size(); i++){
            		UtilsCrud.delete(serviceManager, users.get(i), null);	
            	}
            	
            }catch(Exception e){
            	e.printStackTrace();
            }
            
        }
}
