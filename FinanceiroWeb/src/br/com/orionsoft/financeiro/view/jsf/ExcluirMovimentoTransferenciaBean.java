/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.process.ExcluirMovimentoTransferenciaProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Bean que cotrola a view de exclusão de um movimento de transferência.
 * 
 */
@ManagedBean
@SessionScoped
public class ExcluirMovimentoTransferenciaBean extends BeanSessionBasic implements IRunnableProcessView {
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String FACES_VIEW = "/pages/financeiro/gerenciadorExcluirMovimentoTransferencia?faces-redirect=true";
	public static final String VIEW_NAME = "excluirMovimentoTransferenciaBean";

	public static final String URL_PARAM_LANCAMENTO_MOVIMENTO_ID = "lancamentoMovimentoId";

    private ExcluirMovimentoTransferenciaProcess process = null;

    private String justificativa = "";
    
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
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_LANCAMENTO_MOVIMENTO_ID))){
        	IEntity<LancamentoMovimento> lancamentoMovimento = UtilsCrud.retrieve(getProcess().getProcessManager().getServiceManager(), LancamentoMovimento.class, Long.parseLong(super.getRequestParams().get(URL_PARAM_LANCAMENTO_MOVIMENTO_ID).toString()), null);
            
        	/* Passa a entidade para o processo prepara o estorno */
        	this.getProcess().runWithEntity(lancamentoMovimento);
            
            result = true;
        }
        return result;
    }

    public void doReset() throws BusinessException, Exception {
    }

    public void doReload() throws BusinessException, Exception {
    }
    
    public ExcluirMovimentoTransferenciaProcess getProcess() throws ProcessException {
        if (process == null)
            process = (ExcluirMovimentoTransferenciaProcess)this.getApplicationBean().getProcessManager().createProcessByName(ExcluirMovimentoTransferenciaProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
        return process;
    }
    
    public String actionEstornar() {
        try {
			loadParams();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return FACES_VIEW;
    }

    public String doExcluir() throws ProcessException {
        /* Injeta a descrição no processo, mantendo a mesma sempre dentro do bean.
         * Assim, a última descrição sempre será mantida e o processo não */
        getProcess().setJustificativa(this.justificativa);

        if (getProcess().runExcluir()) {
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
    
	/* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}

	public String getRunnableEntityProcessName() {
		return ExcluirMovimentoTransferenciaProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		try {
			this.getProcess().runWithEntity(entity);
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		return FACES_VIEW;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
}
