/*
 * Created on 20/02/2005
 */
package br.com.orionsoft.monstrengo.core.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IProcessManager;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcessController;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.ProcessManager;
import br.com.orionsoft.monstrengo.core.process.RunnableProcessEntry;
import br.com.orionsoft.monstrengo.core.IApplication;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.util.ClassUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;

/**
 * @author Marcia
 * @spring.bean id="ProcessManager"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="aplication" ref="Aplication"
 */
public class ProcessManager implements IProcessManager {
    public static final String MANAGER_NAME = "ProcessManager";
    
    protected Logger log = LogManager.getLogger(getClass());

    private Map<String, IRunnableEntityProcessController> controllers;

    private Map<String, Class<? extends IProcess>> processesClasses;
    
    private IServiceManager serviceManager;
    
    private long pidCount=0;
    
	private IApplication application;

	public IApplication getApplication() {return application;}
	public void setApplication(IApplication application) {this.application = application;}
	
	/**
	 * Este método cria a lista de DAOs e busca todas as entidades anotadas no sistema
	 * para criar um dao manipulador para esta entidade.
	 * A lista de DAOs auxilia o restante da arquitetura a saber quantas entidades são mantidas, ou seja,
	 * quantas entidades são CRUD
	 */
	public void init(){
		if(controllers != null)
			throw new RuntimeException("ProcessManager já inciado anteriormente. O método init() não pode ser executado.");
		
		controllers = new HashMap<String , IRunnableEntityProcessController>();
		
		/* Prepara as entidades que implementam IRunnableEntityProcess */
		for (Class<? extends IRunnableEntityProcessController> klazz : this.getApplication().findModulesClasses(IRunnableEntityProcessController.class)){
			log.info("Registrando controlador: " + klazz.getSimpleName());
			try {
				IRunnableEntityProcessController controller = (IRunnableEntityProcessController) klazz.newInstance();
				controller.setProcessManager(this);
				this.registerController(controller);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		processesClasses = new HashMap<String, Class<? extends IProcess>>();
		
		/* Prepara as entidades que implementam IProcess */
		for (Class<? extends IProcess> klazz : this.getApplication().findModulesClasses(IProcess.class)){
			log.info("Registrando processo: " + klazz.getSimpleName());
			try {
				Class<? extends IProcess> processClass = (Class<? extends IProcess>) klazz;
				processesClasses.put(processClass.getSimpleName(), processClass);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	public Collection<Class<? extends IProcess>> getAllProcessesClasses()throws ProcessException {
		return this.processesClasses.values();
	}

    public IProcess createProcessByName(String processName, UserSession userSession) throws ProcessException{
    	try
        {
        	// Verifica se o operador possui direitos de executar o atual processo
    		if(!mayExecuteProcess(processName, userSession))
    			throw new ProcessException(MessageList.create(ProcessException.class, "EXECUTE_DENIED", userSession.getUserLogin(), processName));
    		
    		// Obtem uma nova instancia do processo
            ProcessBasic result = (ProcessBasic) processesClasses.get(processName).newInstance();
            result.setProcessManager(this);
            
            // Define o PID
            result.setPid(pidCount++);

            // Define o ID persistido
            result.getProcessInfo().setId(UtilsSecurity.retrieveProcessId(this.serviceManager, result.getProcessName(), null));

            // Define a sessão do usuário
            result.setUserSession(userSession);
            
            //Starta o processo
            result.start();

            return result;
        }
        catch(BusinessException e)
        {
            e.getErrorList().add(new BusinessMessage(ProcessException.class, "ERROR_CREATING_PROCESS", processName));
            
            throw new ProcessException(e.getErrorList());
        }
        catch(Exception e)
        {
           
            throw new ProcessException(MessageList.createSingleInternalError(e));
        }
    }

    public IServiceManager getServiceManager()
    {
        return serviceManager;
    }

    public void setServiceManager(IServiceManager serviceManager)
    {
        this.serviceManager = serviceManager;
    }

	public boolean mayExecuteProcess(String processName, UserSession userSession) throws ProcessException
	{
		/* Verifica se tem uma sessão, pois existem processos que são executados antes que uma
		 * sessão de operador esteja autenticada, como por exemplo: AuthenticateProcess*/
		if(userSession == null)
			return true;
		
		try
		{
			return UtilsSecurity.checkRightProcess(this.getServiceManager(), processName, userSession, null);
		} catch (BusinessException e)
		{
			// Converte a exceção
			throw new ProcessException(e.getErrorList());
		}
	}
    
	/**
	 * @see {@link IProcessManager.getRunnableProcessesControllers()}
	 */
	private Map<Class<?>, List<IRunnableEntityProcessController>> runnableControllerForClassesBuffer = new HashMap<Class<?>, List<IRunnableEntityProcessController>>();
	public List<IRunnableEntityProcessController> getRunnableProcessesControllers(Class<?> entityClass) throws ProcessException {
		List<IRunnableEntityProcessController> result;

		/* Primeiro busca no buffer os controladores pra classe */
		result = runnableControllerForClassesBuffer.get(entityClass);
		if(result != null){
			return result;
		}
		
		/* Não achou no buffer, então realiza a busca completa e bufferiza posteriormente */
		result = new ArrayList<IRunnableEntityProcessController>();	

		List<Class<?>> ancestorClasses = ClassUtils.getAllHierarchy(entityClass);
		
		/* Percorre todos os controladores, depois todas as classes de cada controlador e depois
		 * todas as classes da hierarquia da entidade atual. Se algo cruzar, o controlador é apto para a entidade 
		 * e uma entrada de processo é criada para exibir os dados sobre o processo que pode ser disparado */
		for(IRunnableEntityProcessController controller: controllers.values()){
			boolean controllerCompatible = false;
			for(Class<?> controllerClass: controller.getRunnableEntities()){
				for(Class<?> entityClass1: ancestorClasses){
					if(entityClass1 == controllerClass){
						result.add(controller);
						/* Evita procurar compatibilidade com outras classe deste mesmo controlador */
						controllerCompatible = true;
						break;
					}
				}
				if(controllerCompatible)
					break;
			}
		}

		/* Bufferiza o atual resultado */
		runnableControllerForClassesBuffer.put(entityClass, result);
		return result;
	}
	/**
	 * @see {@link IProcessManager.getRunnableProcesses()}
	 */
	public List<RunnableProcessEntry> getRunnableProcessesEntry(IEntity<?> entity, UserSession userSession) throws ProcessException
	{
		try {
			List<RunnableProcessEntry> result = new ArrayList<RunnableProcessEntry>();

			for(IRunnableEntityProcessController controller: getRunnableProcessesControllers(entity.getInfo().getType())){
				/* Verifica se o operador possui direito de acesso ao processo */
				if(UtilsSecurity.checkRightProcess(this.getServiceManager(), controller.getProcessClass().getSimpleName(), userSession, null)){
					/* Ainda verifica se o processo poderá ser executado com os atuais dados da entidade */
					RunnableProcessEntry entry = new RunnableProcessEntry(controller.getProcessClass());

					entry.setDisabled(!controller.canRunWithEntity(entity));
					if(entry.isDisabled())
						entry.setMessage(controller.getMessage());
					result.add(entry);
				}
			}

			return result;

		} catch (BusinessException e)
		{
			// Converte a exceção
			throw new ProcessException(e.getErrorList());
		}
	}
    
	/**
     * Recebe como parametro uma instancia do tipo IRunnableEntityProcessController e procura no mapa se ela 
     * se encontra no mapa e tenta registrar este controller
     * @param controller
     */
	public void registerController(IRunnableEntityProcessController controller) throws ProcessException {
		final String processNameKey = controller.getProcessClass().getSimpleName();
			
		IRunnableEntityProcessController controllerFound = controllers.get(processNameKey);
		if (controllerFound != null){
			throw new ProcessException(MessageList.create(ProcessManager.class , "DUPLICATED_CONTROLLER" , controller.getClass().getName(), controllerFound.getClass().getName(), processNameKey));
		}
		
		// adiciona o controller no mapa controllers
		controllers.put(processNameKey, controller);
	}
	
	/**
	 * Recebe como parametro uma instancia do tipo IRunnableEntityProcessController e tenta remover essa instância.
	 */
	public void unregisterController(IRunnableEntityProcessController controller) throws ProcessException {
		final String processName = controller.getProcessClass().getSimpleName();
		
		if (!controllers.containsKey(processName)) {
			throw new ProcessException(MessageList.create(ProcessManager.class, "CONTROLLER_NOT_FOUND", processName));
			
		}
		// remove o controller do mapa controllers
		controllers.remove(processName);
	}

	/**
	 * @see {link IProcessManager.getControllers()}
	 */
	public Map<String, IRunnableEntityProcessController> getControllers() throws ProcessException {
		return Collections.unmodifiableMap(controllers);
	}
	
	/**
	 * @see {link IProcessManager.getRunnableController}
	 */
	public IRunnableEntityProcessController getRunnableController(String processName) throws ProcessException{
		if (!controllers.containsKey(processName))
			throw new ProcessException(MessageList.create(ProcessManager.class, "CONTROLLER_NOT_FOUND", processName));
			
		return controllers.get(processName);
	}
	
}
