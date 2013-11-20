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
import br.com.orionsoft.financeiro.gerenciador.process.EstornarLancamentoMovimentoProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Bean que cotrola a view de estorno de um lancamento.
 * 
 * @jsf.bean name="estornarLancamentoMovimentoBean" scope="session"
 * @jsf.navigation from="*" result="estornarLancamentoMovimento" to="/pages/financeiro/gerenciadorEstornarLancamentoMovimento.jsp" 
 */
@ManagedBean
@SessionScoped
public class EstornarLancamentoMovimentoBean extends BeanSessionBasic implements IRunnableProcessView {
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que � ativada para cada view */
	public static final String FACES_VIEW_ESTORNAR = "/pages/financeiro/gerenciadorEstornarLancamentoMovimento?faces-redirect=true";
	public static final String VIEW_NAME = "estornarLancamentoMovimentoBean";

	public static final String URL_PARAM_LANCAMENTO_MOVIMENTO_ID = "lancamentoMovimentoId";

    private EstornarLancamentoMovimentoProcess process = null;

    private String descricao = "";
    
    /**
     * Este m�todo tenta ler os par�metros da atual requisi��o.
     * Se ele conseguir ele pega os valores, aplica no processo
     * e retorna true informando que os par�metros foram encontrados.
     * Assim, os actions poder�o decidir se passa para um ou para outro 
     * passo j� que os par�metros j� foram fornecidos.
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
    
    public EstornarLancamentoMovimentoProcess getProcess() throws ProcessException {
        if (process == null)
            process = (EstornarLancamentoMovimentoProcess)this.getApplicationBean().getProcessManager().createProcessByName(EstornarLancamentoMovimentoProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
        return process;
    }
    
    public String actionEstornar() {
        try {
			loadParams();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return FACES_VIEW_ESTORNAR;
    }

    public String doEstornar() throws ProcessException {
        /* Injeta a descri��o no processo, mantendo a mesma sempre dentro do bean.
         * Assim, a �ltima descri��o sempre ser� mantida e o processo n�o */
        getProcess().setDescricao(this.descricao);

        if (getProcess().runEstornar()) {
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
		return FACES_VIEW_ESTORNAR;
    }

	public String getRunnableEntityProcessName() {
		return EstornarLancamentoMovimentoProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		try {
			this.getProcess().runWithEntity(entity);
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		return FACES_VIEW_ESTORNAR;
	}

}
