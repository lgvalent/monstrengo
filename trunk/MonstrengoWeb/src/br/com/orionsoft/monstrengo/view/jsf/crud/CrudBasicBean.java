package br.com.orionsoft.monstrengo.view.jsf.crud;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.IProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.security.services.CheckRightCrudService;
import br.com.orionsoft.monstrengo.security.services.UtilsSecurity;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;


/**
 * Bean que controla a página de criação de uma entidade Crud.
 * Parâmetro de URL necessário: <br>
 * entityType: nome completo da classe da entidade que será criada uma nova instância.
 * 
 * @author Juliana e Tatiana
 * @version 20060213
 *
 */
public abstract class CrudBasicBean extends BeanSessionBasic implements ICrudBasicBean
{
	private static final long serialVersionUID = 1L;

	/** Armazena os processos das entidades que estão atualmente ativas */
    protected Map<String, IProcess> processes = new HashMap<String, IProcess>();
	/** Armazena os direitos já consultados das entidades que estão atualmente ativas */
    protected Map<String, Map<String, Boolean>> rights = new HashMap<String, Map<String, Boolean>>();

    protected IEntity<?> currentEntity=null;
	
	/** 
	 * Faz a validação da entidade corrente antes de restaurar a visão. 
	 */
	public void validateCurrentEntityKey(
			FacesContext facesContext,
			UIComponent component, Object newValue)
			throws BusinessException, Exception{
		
		log.debug("Validando a entidade a ser manipulada pela visão corrente");
        this.prepareCurrentEntity((String) newValue);
	}
	
	/** 
	 * Fornece um link entre o backing Bean e o componente
	 * responsável pelo armazenamento da chave da entidade correntemente
	 * utilizada.<br>
	 * Durante a ação actionCancel() este valor precisa ser invalidado, porque 
	 * senão, na próxima construção da visão o JSF vai considerar que o valor
	 * da view atualmente instanciado e armazenado no componente interno do
	 * JSF HtmlInputHidden pode ser utilizado. Anulando o valor, na 
	 * próxima vez que a visão é renderizada, o valor currentEntityKey é novamente
	 * obtido por getCurrentEntityKey(), então, o processo de verificação de chamada
	 * por link e necessidade de doReload são acionados.  
	 */
	public HtmlInputHidden inputCurrentEntityKey;
    public HtmlInputHidden getInputCurrentEntityKey(){
    	log.debug("getInputCurrentEntityKey");
    	return inputCurrentEntityKey;
    }
	
    public void setInputCurrentEntityKey(HtmlInputHidden inputCurrentEntityKey) throws Exception
	{
    	log.debug("setInputCurrentEntityKey");
    	this.inputCurrentEntityKey = inputCurrentEntityKey;

    	log.debug("Verificando se a requisição atual se originou de um link ou URL");
		if (this.checkLinkRequest()){
			log.debug("Requisição originada de um link. Lendo os atuais parâmetros");
			loadEntityParams(); 
			this.prepareCurrentEntity(prepareCurrentEntityKey());
		}

	}
	
    protected String currentEntityKey="";
	public String getCurrentEntityKey() throws Exception {
		
		log.debug("Verificando se a requisição atual se originou de um link ou URL");
		if (this.checkLinkRequest()){
			log.debug("Requisição originada de um link. Lendo os atuais parâmetros");
			loadEntityParams(); 
			this.prepareCurrentEntity(prepareCurrentEntityKey());
		}

		return currentEntityKey;
	}
	
	public void setCurrentEntityKey(String currentEntityKey){
		log.debug("Definindo a chave atual: " + currentEntityKey);
		this.currentEntityKey = currentEntityKey;
	}

	/** Trata os parâmetros da requisição necessários para o bean */
	private EntityParam entityParam = new EntityParam(this);
	public EntityParam getEntityParam(){return entityParam;}
	
	public void doReload() throws BusinessException, Exception{
        log.debug("Executando doReload");
        this.prepareCurrentEntity(prepareCurrentEntityKey());
	}
	
	/**
     * Carrega os parâmetros pertinente aos Bean da atual transação.   
     * Antes de recarregar os parâmetros, o Bean sofre um reset() para 
     * que os parâmetros atuais sejam limpos e dados processados sejam 
     * descarregados.
     */
    public void loadEntityParams() throws Exception
    {
        log.debug("Lendo parâmetros da entidade do editBean");
        
        // Causa um reset para que os novos parâmetros entrem em ação
        this.doReset();

        /* Solocitando os parâmetros de entidade sejam processados */
        this.getEntityParam().loadParams();
        
    }
  
    public void doReset() throws BusinessException, Exception{
        // Limpa os atuais parâmetros
    }
	
    /**
     * Obtem a chave da entidade que está currentemente sendo manipulada pelo bean
     * @return
     */
    public IEntity<?> getCurrentEntity() {
		return currentEntity;
	}

	/**
	 * Este método implementa as atividades básicas de preparação dos direitos
	 * CRUD que o atual operador possui sobre a entidade que será manipulada 
	 * pela visão.<br>
	 * Todas as classes descendentes devem, <b>antes de qualquer código</b> na implementação
	 * do seu método prepareCurrentEntity(), executar super.prepareCurrentEntity().
	 */
	public void prepareCurrentEntity(String currentEntityKey) throws BusinessException, Exception{
		if(log.isDebugEnabled())
			log.debug("prepareCurrentEntity:" + currentEntityKey);

		this.setCurrentEntityKey(currentEntityKey);
		
		this.prepareCrudRights(currentEntityKey);
		
        /* Prepara o repositório de parâmetro para a atual entidade */
        this.prepareCurrentParams(currentEntityKey);
	}
	
	/**
	 * Este método permite que antes de alguma ação, o cache de processos ativos seja
	 * consultado e um determinado processo seja removido para iniciar uma
	 * nova instância.<br>
	 * Este método foi implementado porque ao entrar em uma tela de edição, o processo
	 * fica em cache com a entidade. O operador podia abrir outros processos e realizar 
	 * alguma alteração na entidade. No entanto, ao retornar para a tela de
	 * edição, a mesma exibia a entidade que estava em cache e não a nova entidade
	 * que foi gravada no banco por outro processo.<br>
	 * Agora é possível consultar e limpar este cache forçando a recarga da entidade antes de uma
	 * determinada operação..
	 */
	public void removeFromCache(String currentEntityKey){
		if(log.isDebugEnabled())
			log.debug("Localizando e removendo do cache o processo:" + currentEntityKey);

        /* Verifica se há algum processo ativo para a chave passada */
        if(processes.containsKey(currentEntityKey)){
        	log.debug("Uma instância do processo foi encontrada. Removendo-o do cache e finalizando-o.");
        	try {
				processes.remove(currentEntityKey).finish();
			} catch (ProcessException e) {
				throw new RuntimeException(e.getMessage());
			}
    	}
	}
	
	/**
	 * Este método implementa as atividades básicas de cancelamento do processo
	 * atual da visão. Os dados básicos são limpos. A visão seguinte é sugerida para "close".<br>
	 * Todas as classes descendentes devem, <b>depois de qualquer código</b> na implementação
	 * do seu método actionCancel(), executar <b>return super.actionCancel()</b>.
	 */
    public String actionCancel() throws Exception{
        log.debug("::Iniciando actionCancel");
    	
        /* TODO CORRIGIR por ser uma ação imediata o actionCancel nao
    	 * popula o componente currentEntityKey na segunda vez consecutiva 
    	 * em que ele é executado. Assim, o processo não é finalizado nem 
    	 * retirado da lista e fica ativo. Tentar ver como forçar o FACES
    	 * a popular o valor ou pelo menos o submmitedVAlue para poder
    	 * identificar a entidade corrente e finalizar seu processo */
        log.debug("Finalizando o processo e removendo da lista de processo ativos e direitos ativos");
        rights.remove(currentEntityKey);
    	IProcess proc = processes.remove(currentEntityKey);
		/* Verifica se o processo foi encontrado, pois pode acontecer da sessão ser invalidada,
		 * os processos distruídos e o operador posteriormente apertar cancel. */
    	if(proc!=null)
			proc.finish();
		else{
	    	log.debug("ERRO: Processo não encontrado para ser finalizado: " + currentEntityKey);
	    	FacesUtils.addErrorMsg("ERRO: Processo não encontrado para ser finalizado: " + currentEntityKey);
		}
		
    	proc=null;

    	/* Quando há várias chamadas por link de uma visão e a última ação da visão foi uma
         * ação com immediate=true, o JSF não recebe nenhuma ação e 
         * tende a renderizar sempre a mesma visão sem validar os parâmetros da requisição
         * atual. Anulando um valor de um componente da visão durante a actionCancel, que 
         * é definida como immediate=true, o JSF será forçado a processar os parâmetros
         * da próxima requisição.*/
    	log.debug("Anulando o atual valor do componente interno do JSF: currentEntityKey");
    	if(inputCurrentEntityKey!=null)
    		inputCurrentEntityKey.setValue(null);
    	currentEntityKey = "";
		currentEntity = null;
	    
//    	return "";
    	return FacesUtils.FACES_VIEW_CLOSE;
    }
	
	
    // Direitos de ações CRUD dentro desta visão
    private boolean canCreate = false;
    private boolean canRetrieve = false;
    private boolean canUpdate = false;
    private boolean canDelete = false;
	
    /** 
     * Verifica se o user tem permissão de acionar os comandos CRUD
     * que serão renderizados nesta visão de listagem  
     * @throws ClassNotFoundException 
     */
    private void prepareCrudRights(String currentEntityKey) throws BusinessException, ClassNotFoundException{
   	 	
    	log.debug("Verificando se os direitos já estão preparados para a entidade corrente");
    	Map<String, Boolean> right;
    	if(rights.containsKey(currentEntityKey)){
        	log.debug("Utilizando o direitos já ativo");
        	right = rights.get(currentEntityKey);
    	}else{
        	log.debug("Preparando os direitos");
        	right = UtilsSecurity.checkRightCrud(this.getApplicationBean().getProcessManager().getServiceManager(), Class.forName(entityParam.getTypeName()), this.getUserSessionBean().getUserSession(), null);
           	rights.put(this.currentEntityKey, right);
        }
   	 	
   	 	this.canCreate = right.get(CheckRightCrudService.CAN_CREATE);
   	 	this.canRetrieve = right.get(CheckRightCrudService.CAN_RETRIEVE);
   	 	this.canUpdate = right.get(CheckRightCrudService.CAN_UPDATE);
   	 	this.canDelete = right.get(CheckRightCrudService.CAN_DELETE);
    }
    
    public boolean isCanCreate(){return canCreate;}
	public void setCanCreate(boolean canCreate){this.canCreate = canCreate;}
	
	public boolean isCanRetrieve(){return canRetrieve;}
	public void setCanRetrieve(boolean canRetrieve){this.canRetrieve = canRetrieve;}
	
	public boolean isCanUpdate(){return canUpdate;}
	public void setCanUpdate(boolean canUpdate){this.canUpdate = canUpdate;}
	
	public boolean isCanDelete(){return canDelete;}
	public void setCanDelete(boolean canDelete){this.canDelete = canDelete;}
	
    /** Armazena os parâmetros relacionados a atual visão e as entidades que estão atualmente ativas */
    protected Map<String, Map<String, String>> params = new HashMap<String, Map<String, String>>();
    protected Map<String, String> currentParams = null;
    
    /** 
     * Prepara o repositório para a atual visão.
     * As visões poderão armazenar os parãmetros recebidos das
     * requisições nesta estrutura, para posterior recuperação. 
     */
    private void prepareCurrentParams(String currentEntityKey){
   	 	
    	log.debug("Preparando o repositório de parãmetros da visão");
    	if(params.containsKey(currentEntityKey)){
        	log.debug("Utilizando repositório já preparado");
        	currentParams = params.get(currentEntityKey);
    	}else{
        	log.debug("Repositório não encontrado. Criando um novo para a atual visão");
    		currentParams = new HashMap<String,String>();
    		params.put(currentEntityKey, currentParams);
    	}
    }

	public Map<String, String> getCurrentParams() {return currentParams;}
	
	
	/***************************************************
	 * CONTROLE DE ETIQUETAS INTEGRADO AOS BEANS CRUD 
	 ***************************************************/
	/**
	 * Esta referência permite que os Beans do CRUD enviem mensagens ao controlador
	 * da visão de etiquetas. COmo por exemplo, após um Bean Crud inserir uma etiqueta
	 * ele avisar ao labelBean que deve recarregar a lista e evitar que o operador veja a
	 * lista desatualizada
	 * 
	 * @author estagio
	 * @since 20061221
	 */
	@ManagedProperty(value="#{labelBean}")
	private LabelBean labelBean;
	public LabelBean getLabelBean(){return labelBean;}
	public void setLabelBean(LabelBean labelBean){this.labelBean = labelBean;}
	
	
	
}