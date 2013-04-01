package br.com.orionsoft.financeiro.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.process.TransferirProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * 
 * @author Antonio Alves
 */
@ManagedBean
@SessionScoped
public class TransferirBean extends BeanSessionBasic  implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	public static final String URL_PARAM_LANCAMENTO_MOVIMENTO_ID = "lancamentoMovimentoId";

	public static final String VIEW_NAME = "transferirBean";
	public static final String FACES_VIEW_TRANSFERIR = "/pages/financeiro/gerenciadorTransferir?faces-redirect=true";

	private TransferirProcess process = null;
	
	public void doReload() throws BusinessException, Exception {
	}

	public void doReset() throws BusinessException, Exception {
	}

	public TransferirProcess getProcess() throws ProcessException, EntityException {
		if (process == null) {
			process = (TransferirProcess) this.getApplicationBean().getProcessManager().createProcessByName(
					TransferirProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
		}
		return process;
	}
	
    public boolean loadParams() throws BusinessException {
    	boolean result = false;
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_LANCAMENTO_MOVIMENTO_ID))){
        	IEntity<LancamentoMovimento> lancamentoMovimento = UtilsCrud.retrieve(getProcess().getProcessManager().getServiceManager(), LancamentoMovimento.class, Long.parseLong(super.getRequestParams().get(URL_PARAM_LANCAMENTO_MOVIMENTO_ID).toString()), null);
            this.getProcess().setContaOrigem(lancamentoMovimento.getProperty(LancamentoMovimento.CONTA).getValue().<Conta>getAsEntity());
            this.getProcess().setValor(lancamentoMovimento.getProperty(LancamentoMovimento.VALOR).getValue().getAsBigDecimal());
            result = true;
        }

        return result;
    }

	public String actionInicio() throws BusinessException {
		log.debug("TranferirBean.actionInicio");
		
		if (process != null) {
        	this.getProcess().finish();
        	process = null;
		}
		
		this.loadParams();

		return FACES_VIEW_TRANSFERIR;
	}

	public String actionTransferir() throws ProcessException, EntityException {
		if (getProcess().runTransferir()) {
			FacesUtils.addInfoMsgs(getProcess().getMessageList());
		} else {
			FacesUtils.addErrorMsgs(getProcess().getMessageList());
		}
		return ""; // Mesma tela
	}
	
	/* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}
	
	@Override
	public String actionStart() {
		return FACES_VIEW_TRANSFERIR;
	}

	public String getRunnableEntityProcessName() {
		return TransferirProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		try {
			this.getProcess().runWithEntity(entity);
		} catch (BusinessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		return FACES_VIEW_TRANSFERIR;
	}
	
}
