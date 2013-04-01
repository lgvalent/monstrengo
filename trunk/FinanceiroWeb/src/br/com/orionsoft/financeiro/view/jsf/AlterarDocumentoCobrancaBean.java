package br.com.orionsoft.financeiro.view.jsf;

import java.text.ParseException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;
import br.com.orionsoft.financeiro.documento.cobranca.processes.AlterarDocumentoCobrancaProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;

/**
 * Bean que controla a view para alterar a forma de pagamento de um grupo.
 * @author Andre 20070216
 * @version 20080820 Lucio
 */
@ManagedBean
@SessionScoped
public class AlterarDocumentoCobrancaBean extends BeanSessionBasic implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	public static String URL_PARAM_LANCAMENTO_ID = "lancamentoId";
	
	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "alterarDocumentoCobrancaBean";
    public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/gerenciadorAlterarDocumentoCobranca1?faces-redirect=true";
    public static final String FACES_VIEW_PASSO_2 = "/pages/financeiro/gerenciadorAlterarDocumentoCobranca2?faces-redirect=true";
    public static final String FACES_VIEW_PASSO_3 = "/pages/financeiro/gerenciadorAlterarDocumentoCobranca3?faces-redirect=true";

	private AlterarDocumentoCobrancaProcess process = null;

    public void doReset() throws BusinessException{
    	if (process != null)
    		process = null;
    }

    public void doReload() throws BusinessException, Exception {
    }

    public AlterarDocumentoCobrancaProcess getProcess() throws ProcessException {
        if (process == null)
            process = (AlterarDocumentoCobrancaProcess)this.getApplicationBean().getProcessManager().createProcessByName(AlterarDocumentoCobrancaProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
        return process;
    }

    public String doConfirmar() throws ParseException {
        log.debug("AlterarDocumentoCobrancaBean.doConfirmar");
        if (process.runAlterarCategoria()) {
            /* Adiciona as mensagens de info no Faces */
            FacesUtils.addInfoMsgs(process.getMessageList());

            /* Definir o fluxo de tela de SUCESSO */
            return FACES_VIEW_PASSO_3;
        }else{
            /* Adiciona as mensagens de erro no Faces */
            FacesUtils.addErrorMsgs(process.getMessageList());
            
            /* Definir o fluxo de tela de SUCESSO */
            return FacesUtils.FACES_VIEW_FAILURE;
        }
    }

    public String doVisualizar() throws BusinessException{
       log.debug("MudarDocumentoCobrancaBean.doVisulizar");
        
       /* Manda para a tela confirmar alterações */
       return FACES_VIEW_PASSO_2;
    }
    
    public String actionAlterarDocumento(long lancamentoId){
        log.debug("AlterarDocumentoCobrancaBean.actionAlterarDocumento");
        
       	try {
       		/* Limpa o processo anterior */
       		this.doReset();
			this.getProcess().setLancamentoId(lancamentoId);
		} catch (BusinessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}

        /* Definir o fluxo de tela de SUCESSO */
        return FACES_VIEW_PASSO_1;
    }
    
    /**
     * Este metodo faz a conclusão do processo. 
     * Quando chamado pela interface, fecha a tela corrente.
     */
    public String doConcluir() {
    	if (process != null) {
    		try {
				process.finish();
			} catch (ProcessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		process = null;
    	}
    	return FacesUtils.FACES_VIEW_CLOSE;
    }

    public List<IProperty> getPropriedadesEditaveis() {
        try {
            return this.getProcess().retrievePropriedadesEditaveis();
        } catch (BusinessException e) {
            FacesUtils.addErrorMsgs(e.getErrorList());
            return null;
        }
    }

    public boolean isTemPropriedadesEditaveis() {
        try {
            return this.getProcess().retrievePropriedadesEditaveis().size() > 0;
        } catch (BusinessException e) {
            FacesUtils.addErrorMsgs(e.getErrorList());
            return false;
        }
    }
    
	/* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}

	public String getRunnableEntityProcessName() {
		return AlterarDocumentoCobrancaProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		try {
			this.getProcess().runWithEntity(entity);
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		return FACES_VIEW_PASSO_1;
	}

}
