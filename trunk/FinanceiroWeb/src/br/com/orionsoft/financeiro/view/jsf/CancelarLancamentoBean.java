/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.process.CancelarLancamentoProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Bean que cotrola a view de cancelamento de um lancamento com
 * saldo em aberto.
 * 
 */
@ManagedBean
@SessionScoped
public class CancelarLancamentoBean extends BeanSessionBasic implements IRunnableProcessView {
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String FACES_VIEW_CANCELAR = "/pages/financeiro/gerenciadorCancelarLancamento?faces-redirect=true";
	public static final String VIEW_NAME = "cancelarLancamentoBean";

	public static final String URL_PARAM_LANCAMENTO_ID = "lancamentoId";
    public static final String URL_PARAM_LANCAMENTO_SALDO = "lancamentoSaldo";

    private CancelarLancamentoProcess process = null;
    
	private String descricao = "";

    /**
     * Este método tenta ler os parâmetros da atual requisição.
     * Se ele conseguir ele pega os valores, aplica no processo
     * e retorna true informando que os parâmetros foram encontrados.
     * Assim, os actions poderão decidir se passa para um ou para outro 
     * passo já que os parâmetros já foram fornecidos.
     * @throws BusinessException
     */
    public boolean loadParams() throws BusinessException {
    	boolean result = false;
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_LANCAMENTO_ID))){
        	IEntity<Lancamento> lancamento = UtilsCrud.retrieve(getProcess().getProcessManager().getServiceManager(), Lancamento.class, Long.parseLong(super.getRequestParams().get(URL_PARAM_LANCAMENTO_ID).toString()), null);
            lancamento.setSelected(true);
        	this.getProcess().setLancamento(lancamento);
            result = true;
        }
        return result;
    }

    public void doReset() throws BusinessException, Exception {
    }

    public void doReload() throws BusinessException, Exception {
    }
    
    public CancelarLancamentoProcess getProcess() throws ProcessException {
        if (process == null)
            process = (CancelarLancamentoProcess)this.getApplicationBean().getProcessManager().createProcessByName(CancelarLancamentoProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
        return process;
    }
    
    public String actionCancelar() {
        try {
			loadParams();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return FACES_VIEW_CANCELAR;
    }

    public String doCancelar() throws ProcessException {
        log.debug("InserirMovimentoBean.actionInserir");
        
        /* Injeta a descrição no processo, mantendo a mesma sempre dentro do bean.
         * Assim, a última descrição sempre será mantida e o processo não */
        getProcess().setDescricao(this.descricao);
        if (getProcess().runCancelar()) {
            // Adiciona as mensagens de info no Faces
            FacesUtils.addInfoMsgs(process.getMessageList());
            
            getProcess().finish();
            process = null;
            
            // Definir o fluxo de tela
            return FacesUtils.FACES_VIEW_SUCCESS;
        }else{
            // Adiciona as mensagens de erro no Faces
            FacesUtils.addErrorMsgs(process.getMessageList());
            // Definir o fluxo de tela
            return FacesUtils.FACES_VIEW_FAILURE;
        }
    }

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}
	
    @Override
    public String actionStart() {
		return FACES_VIEW_CANCELAR;
    }
	
	@SuppressWarnings("unchecked")
	public String runWithEntities(IEntityList<?> entities) {
		try {
			/* Limpa os atuais lançamentos do processo */
			this.getProcess().setLancamento(null);
			this.getProcess().getLancamentos().clear();
			for(IEntity<?> entity: entities)
				this.getProcess().getLancamentos().add((IEntity<Lancamento>) entity);
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		return FACES_VIEW_CANCELAR; // O lançamento já está preenchido, vai par ao segujndo passo!
	}

	@Override
	public String getRunnableEntityProcessName() {
		return CancelarLancamentoProcess.PROCESS_NAME;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String runWithEntity(IEntity<?> entity) {
		try {
			/* Limpa os atuais lançamentos do processo */
			this.getProcess().setLancamento(null);
			this.getProcess().getLancamentos().clear();

			this.getProcess().setLancamento((IEntity<Lancamento>) entity);
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		return FACES_VIEW_CANCELAR; // O lançamento já está preenchido, vai par ao segujndo passo!
	}

	
	
}
