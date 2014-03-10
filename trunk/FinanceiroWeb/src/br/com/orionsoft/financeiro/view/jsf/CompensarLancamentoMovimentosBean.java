/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.process.CompensarLancamentoMovimentosProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que cotrola a view de estorno de um lancamento.
 * 
 * @jsf.bean name="CompensarLancamentoMovimentosBean" scope="session"
 * @jsf.navigation from="*" result="CompensarLancamentoMovimentos" to="/pages/financeiro/gerenciadorCompensarLancamentoMovimentos.jsp" 
 */
@ManagedBean
@SessionScoped
public class CompensarLancamentoMovimentosBean extends BeanSessionBasic implements IRunnableProcessView {
	/** Define a view JSF que é ativada para cada view */
	public static final String FACES_VIEW = "/pages/financeiro/gerenciadorCompensarLancamentoMovimentos?faces-redirect=true";
	public static final String VIEW_NAME = "compensarLancamentoMovimentosBean";

	public static final String URL_PARAM_LANCAMENTO_MOVIMENTO_ID = "lancamentoMovimentoId";

    private CompensarLancamentoMovimentosProcess process = null;
    
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
    
    public CompensarLancamentoMovimentosProcess getProcess() throws ProcessException {
        if (process == null)
            process = (CompensarLancamentoMovimentosProcess)this.getApplicationBean().getProcessManager().createProcessByName(CompensarLancamentoMovimentosProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
        return process;
    }
    
    public String doCompensar() throws ProcessException {
        if (getProcess().runCompensar()) {
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

    @Override
    public String actionStart() {
    	try {
			loadParams();
			return FACES_VIEW;
		} catch (BusinessException e) {
			e.printStackTrace();
			FacesUtils.addMsg(e.getMessage());
			return FacesUtils.FACES_VIEW_FAILURE;
		}

    }

    public String getRunnableEntityProcessName() {
		return CompensarLancamentoMovimentosProcess.PROCESS_NAME;
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

	@SuppressWarnings("unchecked")
	public String runWithEntities(IEntityCollection<?> entities) {
		try {
			this.getProcess().getLancamentoMovimentos().clear();
			for(IEntity<?> entity: entities)
				this.getProcess().getLancamentoMovimentos().add((IEntity<LancamentoMovimento>) entity);
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		return FACES_VIEW; // O lançamento já está preenchido, vai par ao segujndo passo!
	}
}
