package br.com.orionsoft.financeiro.view.jsf;

import java.text.ParseException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.process.InserirLancamentoProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.documents.services.ListModelDocumentEntityService;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que controla a view de inserção de um novo lancamento
 * 
 * @author Antonio Alves
 */
@ManagedBean
@SessionScoped
public class InserirLancamentoBean extends BeanSessionBasic implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "inserirLancamentoBean";
	public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/gerenciadorInserirLancamento1?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_2 = "/pages/financeiro/gerenciadorInserirLancamento2?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_3 = "/pages/financeiro/gerenciadorInserirLancamento3?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_4 = "/pages/financeiro/gerenciadorInserirLancamento4?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_5 = "/pages/financeiro/gerenciadorInserirLancamento5?faces-redirect=true";

	public static final String ITEM_NUMERO = "itemNumero";

	private InserirLancamentoProcess process = null;

	/*
	 * Andre 12/06/2008 - Caso o serviço de criação de documento de cobrança lançasse exception, o processo não era interrompido
	 * pois o método doCriarDocumentocobranca é usado pelo Ajax e o tipo de retorno é void, dessa forma não é possível mostrar
	 * o erro na hora da criação do documento, apenas no passo seguinte, que é a visualização dos dados. 
	 */	
	private boolean erroAoCriarDocumentoCobranca = false;  
	private boolean erroAoCriarDocumentoPagamento = false;  

	public void loadParams() throws Exception {
	}

	public void doReset() throws BusinessException, Exception {
	}

	public void doReload() throws BusinessException, Exception {
	}

	public InserirLancamentoProcess getProcess() throws ProcessException {
		if (process == null)
			process = (InserirLancamentoProcess) this.getApplicationBean().getProcessManager().createProcessByName(
					InserirLancamentoProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
		return process;
	}

	public void doAdicionarItem() {
		this.process.criarNovoItem();
	}

	/**
	 * Limpa os dados que foram mantidos pela última execução do processo
	 */
	public void doLimpar() {
		try {
			this.process.finish();
			this.process.start();
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
		}
		
	}

	public String actionInserir() throws ParseException, PropertyValueException, EntityException {
		log.debug("InserirLancamentoBean.actionInserir");
		if (process.runInserirLancamento()) {
			/* Adiciona as mensagens de info no Faces */
			FacesUtils.addInfoMsgs(process.getMessageList());

			// Lucio 26/07/2006 Para manter os últimos parâmetros inseridos
			// process.finish();
			// process = null;

			/* Definir o fluxo de tela de SUCESSO */
			return FACES_VIEW_PASSO_5;
		} else {
			/* Adiciona as mensagens de erro no Faces */
			FacesUtils.addErrorMsgs(process.getMessageList());
			/* Definir o fluxo de tela de SUCESSO */
			return FacesUtils.FACES_VIEW_FAILURE;
		}
	}

	public String actionInicio() throws ParseException {
		log.debug("InserirLancamentoBean.actionInicio");

		/*
		 * Re-Cria o documento para evitar que inserções subsequentes utilizem o
		 * mesmo documento de cobrança
		 */
		doCriarDocumentoCobranca();
		doCriarDocumentoPagamento();

		/* Manda para a tela seleção de documento de cobrança */
		return FACES_VIEW_PASSO_2;

		// Lucio 20060720 - Independente de ter uma data de quitacao o operador
		// poderá gerar um documento de cobrança
		// que deverá ser impresso e possuí uma data de vencimento
		// /* Verifica se a data de quitação foi preenchida */
		// if(StringUtils.isEmpty(process.getDataQuitacao()))
		// /* Data não preenchida, vai direto para confirmação */
		// return "inserirLancamento3";
		// else
		// /* Data preenchida, precisa fornecer qual o tipo de documento */
		// return "inserirLancamento2";
	}

	public void doCriarDocumentoCobranca() {
		log.debug("InserirLancamentoBean.doCriarDocumentoCobranca");
		this.erroAoCriarDocumentoCobranca = !process.runCriarDocumentoCobranca(); //Andre, 12/06/2008
		FacesUtils.addErrorMsgs(process.getMessageList());
	}

	public void doCriarDocumentoPagamento() {
		log.debug("InserirLancamentoBean.doCriarDocumentoPagamento");
		this.erroAoCriarDocumentoPagamento = !process.runCriarDocumentoPagamento(); //Andre, 12/06/2008 
		FacesUtils.addErrorMsgs(process.getMessageList());
	}

	public String actionDocumentoCobrancaCategoria() throws ParseException {
		log.debug("InserirLancamentoBean.actionDocumentoCobrancaCategoria");
		if (this.erroAoCriarDocumentoCobranca){ //Andre, 12/06/2008
			/* Adiciona as mensagens de erro no Faces */
			FacesUtils.addErrorMsgs(process.getMessageList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}else{
			/* Manda direto para a tela de confirmação */
			/*
			 * Andre, 12/06/2008: seria interessante mudar o toString do DocumentoTitulo e de outras
			 * formas de pagamento, pois na tela de confirmação do novo lançamento, não aparecem
			 * informações do tipo do documento de cobrança que está sendo criado (aparece apenas
			 * o toString do contrato).
			 * Seria interessante identificar o tipo do documento de cobrança, seja Título, 
			 * GRCS, etc.
			 */
			return FACES_VIEW_PASSO_3;
		}
		
	}

	public String actionDocumentoPagamentoCategoria() throws ParseException {
		log.debug("InserirLancamentoBean.actionDocumentoPagamentoCategoria");
		if (this.erroAoCriarDocumentoPagamento){ //Andre, 12/06/2008
			/* Adiciona as mensagens de erro no Faces */
			FacesUtils.addErrorMsgs(process.getMessageList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}else{
			/* Manda direto para a tela de confirmação */
			/*
			 * Andre, 12/06/2008: seria interessante mudar o toString do DocumentoTitulo e de outras
			 * formas de pagamento, pois na tela de confirmação do novo lançamento, não aparecem
			 * informações do tipo do documento de cobrança que está sendo criado (aparece apenas
			 * o toString do contrato).
			 * Seria interessante identificar o tipo do documento de cobrança, seja Título, 
			 * GRCS, etc.
			 */
			return FACES_VIEW_PASSO_4;
		}
		
	}

	public String actionRecibo() throws ParseException {
		log.debug("InserirLancamentoBean.actionRecibo");
		/* Manda direto para a tela de confirmação */
		return FACES_VIEW_PASSO_1;
	}

	public List<IProperty> getPropriedadesEditaveisCobranca() {
		try {
			return this.getProcess().retrievePropriedadesEditaveisCobranca();
		} catch (BusinessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return null;
		}
	}

	public boolean isTemPropriedadesEditaveisCobranca() {
		try {
			return this.getProcess().retrievePropriedadesEditaveisCobranca().size() > 0;
		} catch (BusinessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return false;
		}
	}

	public List<IProperty> getPropriedadesEditaveisPagamento() {
		try {
			return this.getProcess().retrievePropriedadesEditaveisPagamento();
		} catch (BusinessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return null;
		}
	}

	public boolean isTemPropriedadesEditaveisPagamento() {
		try {
			return this.getProcess().retrievePropriedadesEditaveisPagamento().size() > 0;
		} catch (BusinessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return false;
		}
	}

	/**
	 * Cria uma lista com os modelos de etiquetas de entidades disponivel para a
	 * entidade atualmente manipulada. O tipo da entidade já deve estar definido
	 * para executar este método, para que ele mostra somente os modelos da
	 * entidade selecionada
	 */
	private List<SelectItem> modelsDocumentEntityBuffer = null;

	public List<SelectItem> getModelsDocumentEntity() {
		try {
			if (modelsDocumentEntityBuffer == null) {
				ServiceData sd = new ServiceData(ListModelDocumentEntityService.SERVICE_NAME, null);
				sd.getArgumentList().setProperty(ListModelDocumentEntityService.IN_ENTITY_TYPE_NAME,
						Lancamento.class.getName());
				sd.getArgumentList().setProperty(ListModelDocumentEntityService.IN_APPLICATION_USER_OPT,
						this.getUserSessionBean().getUserSession().getUser());
				this.getApplicationBean().getProcessManager().getServiceManager().execute(sd);
				modelsDocumentEntityBuffer = sd.getFirstOutput();
			}

			return modelsDocumentEntityBuffer;
		} catch (ServiceException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return null;
		}
	}

	public boolean isHasModelsDocumentEntity() {
		return this.getModelsDocumentEntity().size() > 0;
	}

	/* IRunnableProcessView */
	public String actionStart(){
		return FACES_VIEW_PASSO_1;
	};
	
	public String getViewName() {
		return VIEW_NAME;
	}
	
	@Override
	public String getRunnableEntityProcessName() {
		return InserirLancamentoProcess.PROCESS_NAME;
	}

	@Override
	public String runWithEntity(IEntity<?> entity) {

		try {
			/* Limpa os atuais lançamentos do processo */
			if(this.getProcess().runWithEntity(entity))
				return FACES_VIEW_PASSO_1; 

			FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
			return FacesUtils.FACES_VIEW_FAILURE;
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
	}
}
