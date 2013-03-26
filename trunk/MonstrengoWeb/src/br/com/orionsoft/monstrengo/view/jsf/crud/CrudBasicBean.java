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
 * Bean que controla a p�gina de cria��o de uma entidade Crud.
 * Par�metro de URL necess�rio: <br>
 * entityType: nome completo da classe da entidade que ser� criada uma nova inst�ncia.
 * 
 * @author Juliana e Tatiana
 * @version 20060213
 *
 */
public abstract class CrudBasicBean extends BeanSessionBasic implements ICrudBasicBean
{
	private static final long serialVersionUID = 1L;

	/** Armazena os processos das entidades que est�o atualmente ativas */
    protected Map<String, IProcess> processes = new HashMap<String, IProcess>();
	/** Armazena os direitos j� consultados das entidades que est�o atualmente ativas */
    protected Map<String, Map<String, Boolean>> rights = new HashMap<String, Map<String, Boolean>>();

    protected IEntity<?> currentEntity=null;
	
	/** 
	 * Faz a valida��o da entidade corrente antes de restaurar a vis�o. 
	 */
	public void validateCurrentEntityKey(
			FacesContext facesContext,
			UIComponent component, Object newValue)
			throws BusinessException, Exception{
		
		log.debug("Validando a entidade a ser manipulada pela vis�o corrente");
        this.prepareCurrentEntity((String) newValue);
	}
	
	/** 
	 * Fornece um link entre o backing Bean e o componente
	 * respons�vel pelo armazenamento da chave da entidade correntemente
	 * utilizada.<br>
	 * Durante a a��o actionCancel() este valor precisa ser invalidado, porque 
	 * sen�o, na pr�xima constru��o da vis�o o JSF vai considerar que o valor
	 * da view atualmente instanciado e armazenado no componente interno do
	 * JSF HtmlInputHidden pode ser utilizado. Anulando o valor, na 
	 * pr�xima vez que a vis�o � renderizada, o valor currentEntityKey � novamente
	 * obtido por getCurrentEntityKey(), ent�o, o processo de verifica��o de chamada
	 * por link e necessidade de doReload s�o acionados.  
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

    	log.debug("Verificando se a requisi��o atual se originou de um link ou URL");
		if (this.checkLinkRequest()){
			log.debug("Requisi��o originada de um link. Lendo os atuais par�metros");
			loadEntityParams(); 
			this.prepareCurrentEntity(prepareCurrentEntityKey());
		}

	}
	
    protected String currentEntityKey="";
	public String getCurrentEntityKey() throws Exception {
		
		log.debug("Verificando se a requisi��o atual se originou de um link ou URL");
		if (this.checkLinkRequest()){
			log.debug("Requisi��o originada de um link. Lendo os atuais par�metros");
			loadEntityParams(); 
			this.prepareCurrentEntity(prepareCurrentEntityKey());
		}

		return currentEntityKey;
	}
	
	public void setCurrentEntityKey(String currentEntityKey){
		log.debug("Definindo a chave atual: " + currentEntityKey);
		this.currentEntityKey = currentEntityKey;
	}

	/** Trata os par�metros da requisi��o necess�rios para o bean */
	private EntityParam entityParam = new EntityParam(this);
	public EntityParam getEntityParam(){return entityParam;}
	
	public void doReload() throws BusinessException, Exception{
        log.debug("Executando doReload");
        this.prepareCurrentEntity(prepareCurrentEntityKey());
	}
	
	/**
     * Carrega os par�metros pertinente aos Bean da atual transa��o.   
     * Antes de recarregar os par�metros, o Bean sofre um reset() para 
     * que os par�metros atuais sejam limpos e dados processados sejam 
     * descarregados.
     */
    public void loadEntityParams() throws Exception
    {
        log.debug("Lendo par�metros da entidade do editBean");
        
        // Causa um reset para que os novos par�metros entrem em a��o
        this.doReset();

        /* Solocitando os par�metros de entidade sejam processados */
        this.getEntityParam().loadParams();
        
    }
  
    public void doReset() throws BusinessException, Exception{
        // Limpa os atuais par�metros
    }
	
    /**
     * Obtem a chave da entidade que est� currentemente sendo manipulada pelo bean
     * @return
     */
    public IEntity<?> getCurrentEntity() {
		return currentEntity;
	}

	/**
	 * Este m�todo implementa as atividades b�sicas de prepara��o dos direitos
	 * CRUD que o atual operador possui sobre a entidade que ser� manipulada 
	 * pela vis�o.<br>
	 * Todas as classes descendentes devem, <b>antes de qualquer c�digo</b> na implementa��o
	 * do seu m�todo prepareCurrentEntity(), executar super.prepareCurrentEntity().
	 */
	public void prepareCurrentEntity(String currentEntityKey) throws BusinessException, Exception{
		if(log.isDebugEnabled())
			log.debug("prepareCurrentEntity:" + currentEntityKey);

		this.setCurrentEntityKey(currentEntityKey);
		
		this.prepareCrudRights(currentEntityKey);
		
        /* Prepara o reposit�rio de par�metro para a atual entidade */
        this.prepareCurrentParams(currentEntityKey);
	}
	
	/**
	 * Este m�todo permite que antes de alguma a��o, o cache de processos ativos seja
	 * consultado e um determinado processo seja removido para iniciar uma
	 * nova inst�ncia.<br>
	 * Este m�todo foi implementado porque ao entrar em uma tela de edi��o, o processo
	 * fica em cache com a entidade. O operador podia abrir outros processos e realizar 
	 * alguma altera��o na entidade. No entanto, ao retornar para a tela de
	 * edi��o, a mesma exibia a entidade que estava em cache e n�o a nova entidade
	 * que foi gravada no banco por outro processo.<br>
	 * Agora � poss�vel consultar e limpar este cache for�ando a recarga da entidade antes de uma
	 * determinada opera��o..
	 */
	public void removeFromCache(String currentEntityKey){
		if(log.isDebugEnabled())
			log.debug("Localizando e removendo do cache o processo:" + currentEntityKey);

        /* Verifica se h� algum processo ativo para a chave passada */
        if(processes.containsKey(currentEntityKey)){
        	log.debug("Uma inst�ncia do processo foi encontrada. Removendo-o do cache e finalizando-o.");
        	try {
				processes.remove(currentEntityKey).finish();
			} catch (ProcessException e) {
				throw new RuntimeException(e.getMessage());
			}
    	}
	}
	
	/**
	 * Este m�todo implementa as atividades b�sicas de cancelamento do processo
	 * atual da vis�o. Os dados b�sicos s�o limpos. A vis�o seguinte � sugerida para "close".<br>
	 * Todas as classes descendentes devem, <b>depois de qualquer c�digo</b> na implementa��o
	 * do seu m�todo actionCancel(), executar <b>return super.actionCancel()</b>.
	 */
    public String actionCancel() throws Exception{
        log.debug("::Iniciando actionCancel");
    	
        /* TODO CORRIGIR por ser uma a��o imediata o actionCancel nao
    	 * popula o componente currentEntityKey na segunda vez consecutiva 
    	 * em que ele � executado. Assim, o processo n�o � finalizado nem 
    	 * retirado da lista e fica ativo. Tentar ver como for�ar o FACES
    	 * a popular o valor ou pelo menos o submmitedVAlue para poder
    	 * identificar a entidade corrente e finalizar seu processo */
        log.debug("Finalizando o processo e removendo da lista de processo ativos e direitos ativos");
        rights.remove(currentEntityKey);
    	IProcess proc = processes.remove(currentEntityKey);
		/* Verifica se o processo foi encontrado, pois pode acontecer da sess�o ser invalidada,
		 * os processos distru�dos e o operador posteriormente apertar cancel. */
    	if(proc!=null)
			proc.finish();
		else{
	    	log.debug("ERRO: Processo n�o encontrado para ser finalizado: " + currentEntityKey);
	    	FacesUtils.addErrorMsg("ERRO: Processo n�o encontrado para ser finalizado: " + currentEntityKey);
		}
		
    	proc=null;

    	/* Quando h� v�rias chamadas por link de uma vis�o e a �ltima a��o da vis�o foi uma
         * a��o com immediate=true, o JSF n�o recebe nenhuma a��o e 
         * tende a renderizar sempre a mesma vis�o sem validar os par�metros da requisi��o
         * atual. Anulando um valor de um componente da vis�o durante a actionCancel, que 
         * � definida como immediate=true, o JSF ser� for�ado a processar os par�metros
         * da pr�xima requisi��o.*/
    	log.debug("Anulando o atual valor do componente interno do JSF: currentEntityKey");
    	if(inputCurrentEntityKey!=null)
    		inputCurrentEntityKey.setValue(null);
    	currentEntityKey = "";
		currentEntity = null;
	    
//    	return "";
    	return FacesUtils.FACES_VIEW_CLOSE;
    }
	
	
    // Direitos de a��es CRUD dentro desta vis�o
    private boolean canCreate = false;
    private boolean canRetrieve = false;
    private boolean canUpdate = false;
    private boolean canDelete = false;
	
    /** 
     * Verifica se o user tem permiss�o de acionar os comandos CRUD
     * que ser�o renderizados nesta vis�o de listagem  
     * @throws ClassNotFoundException 
     */
    private void prepareCrudRights(String currentEntityKey) throws BusinessException, ClassNotFoundException{
   	 	
    	log.debug("Verificando se os direitos j� est�o preparados para a entidade corrente");
    	Map<String, Boolean> right;
    	if(rights.containsKey(currentEntityKey)){
        	log.debug("Utilizando o direitos j� ativo");
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
	
    /** Armazena os par�metros relacionados a atual vis�o e as entidades que est�o atualmente ativas */
    protected Map<String, Map<String, String>> params = new HashMap<String, Map<String, String>>();
    protected Map<String, String> currentParams = null;
    
    /** 
     * Prepara o reposit�rio para a atual vis�o.
     * As vis�es poder�o armazenar os par�metros recebidos das
     * requisi��es nesta estrutura, para posterior recupera��o. 
     */
    private void prepareCurrentParams(String currentEntityKey){
   	 	
    	log.debug("Preparando o reposit�rio de par�metros da vis�o");
    	if(params.containsKey(currentEntityKey)){
        	log.debug("Utilizando reposit�rio j� preparado");
        	currentParams = params.get(currentEntityKey);
    	}else{
        	log.debug("Reposit�rio n�o encontrado. Criando um novo para a atual vis�o");
    		currentParams = new HashMap<String,String>();
    		params.put(currentEntityKey, currentParams);
    	}
    }

	public Map<String, String> getCurrentParams() {return currentParams;}
	
	
	/***************************************************
	 * CONTROLE DE ETIQUETAS INTEGRADO AOS BEANS CRUD 
	 ***************************************************/
	/**
	 * Esta refer�ncia permite que os Beans do CRUD enviem mensagens ao controlador
	 * da vis�o de etiquetas. COmo por exemplo, ap�s um Bean Crud inserir uma etiqueta
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