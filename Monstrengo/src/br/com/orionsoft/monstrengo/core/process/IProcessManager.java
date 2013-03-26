package br.com.orionsoft.monstrengo.core.process;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcessController;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.RunnableProcessEntry;
import br.com.orionsoft.monstrengo.core.IManager;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

/**
 * Gerencia todos os processos que são instanciados pelo usuário.
 * Poderá haver controle para que quando a aplicação for terminada, este
 * gerenciador possa finalizar (abortar) os processos atualmente instanciados.
 * 
 * @author Lucio
 *
 */
public interface IProcessManager extends IManager {

    /**
     * Obtem uma nova instância do Processo com o nome fornecido.
     * Os beans processos não são <i>singleton</i>.
     * @param processName Nome do serviço procurado.
     * @param userSession 
     */
    public abstract IProcess createProcessByName(String processName, UserSession userSession) throws ProcessException;

	public abstract IServiceManager getServiceManager();
    public abstract void setServiceManager(IServiceManager serviceManager);

    /**
     * Verifica se o atual usuário possui direitos de execução
     * sobre o processo
     * @param processName Nome identificador do processo.
     * @param userSession Sessão do operador atualmente autenticado.
     * @return Se o operador tiver direito de execução será retornado true senão false.
     * @throws ProcessException
     */
    public abstract boolean mayExecuteProcess(String processName, UserSession userSession) throws ProcessException;
    
	/**
	 * Obtem uma lista com as classes dos processos atualmente registrados no Manager.<br>
	 * @return
	 * @throws ProcessException
	 */
    public Collection<Class<? extends IProcess>> getAllProcessesClasses() throws ProcessException;
	
	/**
	 * Obtem uma lista com os controladores dos processos que podem ser executados a partir de uma
	 * determinada classe de entidade.<br>
	 * @param entityClass classe de entidade com a qual se deseja pesquisar os processos compatíveis 
	 * @return
	 * @throws ProcessException
	 */
    public List<IRunnableEntityProcessController> getRunnableProcessesControllers(Class<?> entityClass) throws ProcessException;
	
	/**
	 * Obtem uma lista com a entrada de processos que podem ser executados a partir de um determinado tipo
	 * de entidade.<br>
	 * Os {@link IRunnableEntityProcess} fornecem uma lista de tipos de entidades com as quais
	 * eles são compatíveis. Esta lista de entidades é mantida dentro do controlador e é construida dinamicamente.<br>
	 * Caso um controlador de processo indique que não é possível executar o processo neste momento,
	 * ({@link IRunnableEntityProcessController.canRunWithEntity}) a entidade do processo retornada não estará com isSelected() definido.<br>  
	 * @param entity Entidade com a qual se deseja pesquisar os processos compatíveis 
	 * @param userSession Sessão do operador para verificar a quais processos ele tem direito de acessar
	 * @return
	 * @throws ProcessException
	 */
    public List<RunnableProcessEntry> getRunnableProcessesEntry(IEntity<?> entity, UserSession userSession) throws ProcessException;
	
    /**
     * Registra uma instância de um RunnableEntityProcessController que possibilita ao gerenciador de processo
     * controlar quantos processos possuem esta característica de RunnableEntity.
     */
    public void registerController(IRunnableEntityProcessController controller) throws ProcessException;
    
    /**
     * Revome uma instância do tipo RunnableEntityProcessController da lista de Controladores ativos.
     */
    public void unregisterController(IRunnableEntityProcessController controller) throws ProcessException;    
    
    /**
     * Obtem a lista de controllers que foram registrados pelo ProcessManager
     * 
     * @return
     * @throws ProcessException
     */
    public Map<String, IRunnableEntityProcessController> getControllers() throws ProcessException;
    
    
    public IRunnableEntityProcessController getRunnableController(String processName) throws ProcessException;
    
    
    
}