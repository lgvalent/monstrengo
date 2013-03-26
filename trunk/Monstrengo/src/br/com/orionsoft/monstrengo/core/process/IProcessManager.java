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
 * Gerencia todos os processos que s�o instanciados pelo usu�rio.
 * Poder� haver controle para que quando a aplica��o for terminada, este
 * gerenciador possa finalizar (abortar) os processos atualmente instanciados.
 * 
 * @author Lucio
 *
 */
public interface IProcessManager extends IManager {

    /**
     * Obtem uma nova inst�ncia do Processo com o nome fornecido.
     * Os beans processos n�o s�o <i>singleton</i>.
     * @param processName Nome do servi�o procurado.
     * @param userSession 
     */
    public abstract IProcess createProcessByName(String processName, UserSession userSession) throws ProcessException;

	public abstract IServiceManager getServiceManager();
    public abstract void setServiceManager(IServiceManager serviceManager);

    /**
     * Verifica se o atual usu�rio possui direitos de execu��o
     * sobre o processo
     * @param processName Nome identificador do processo.
     * @param userSession Sess�o do operador atualmente autenticado.
     * @return Se o operador tiver direito de execu��o ser� retornado true sen�o false.
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
	 * @param entityClass classe de entidade com a qual se deseja pesquisar os processos compat�veis 
	 * @return
	 * @throws ProcessException
	 */
    public List<IRunnableEntityProcessController> getRunnableProcessesControllers(Class<?> entityClass) throws ProcessException;
	
	/**
	 * Obtem uma lista com a entrada de processos que podem ser executados a partir de um determinado tipo
	 * de entidade.<br>
	 * Os {@link IRunnableEntityProcess} fornecem uma lista de tipos de entidades com as quais
	 * eles s�o compat�veis. Esta lista de entidades � mantida dentro do controlador e � construida dinamicamente.<br>
	 * Caso um controlador de processo indique que n�o � poss�vel executar o processo neste momento,
	 * ({@link IRunnableEntityProcessController.canRunWithEntity}) a entidade do processo retornada n�o estar� com isSelected() definido.<br>  
	 * @param entity Entidade com a qual se deseja pesquisar os processos compat�veis 
	 * @param userSession Sess�o do operador para verificar a quais processos ele tem direito de acessar
	 * @return
	 * @throws ProcessException
	 */
    public List<RunnableProcessEntry> getRunnableProcessesEntry(IEntity<?> entity, UserSession userSession) throws ProcessException;
	
    /**
     * Registra uma inst�ncia de um RunnableEntityProcessController que possibilita ao gerenciador de processo
     * controlar quantos processos possuem esta caracter�stica de RunnableEntity.
     */
    public void registerController(IRunnableEntityProcessController controller) throws ProcessException;
    
    /**
     * Revome uma inst�ncia do tipo RunnableEntityProcessController da lista de Controladores ativos.
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