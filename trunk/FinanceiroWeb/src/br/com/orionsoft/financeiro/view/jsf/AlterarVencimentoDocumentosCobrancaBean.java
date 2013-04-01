package br.com.orionsoft.financeiro.view.jsf;

import java.text.ParseException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.processes.AlterarVencimentoDocumentosCobrancaProcess;
import br.com.orionsoft.financeiro.view.jsf.documento.cobranca.ImprimirDocumentosCobrancaBean;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Bean que cotrola a view de alteração de documentos de cobrança.
 * 
 * @jsf.bean name="alterarVencimentoDocumentosCobrancaBean" scope="session"
 * @jsf.navigation from="*" result="alterarVencimentoDocumentosCobranca" to="/pages/financeiro/gerenciadorAlterarVencimentoDocumentosCobranca.jsp" 
 */
@ManagedBean
@SessionScoped
public class AlterarVencimentoDocumentosCobrancaBean extends BeanSessionBasic implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "alterarVencimentoDocumentosCobrancaBean";
	public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/gerenciadorAlterarVencimentoDocumentosCobranca1?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_2 = "/pages/financeiro/gerenciadorAlterarVencimentoDocumentosCobranca2?faces-redirect=true";

	public static final String URL_PARAM_DOCUMENTO_ID = "documentoId";

    private AlterarVencimentoDocumentosCobrancaProcess process = null;
    
    /** Este bean permite imprimir os documentos alterados de uma só vez */
    @ManagedProperty("#{imprimirDocumentosCobrancaBean}")
    private ImprimirDocumentosCobrancaBean imprimirDocumentosCobrancaBean;

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
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_DOCUMENTO_ID))){
        	IEntity<DocumentoCobranca> documento = UtilsCrud.retrieve(getProcess().getProcessManager().getServiceManager(), DocumentoCobranca.class, Long.parseLong(super.getRequestParams().get(URL_PARAM_DOCUMENTO_ID).toString()), null);
            documento.setSelected(true);
        	this.getProcess().runWithEntity(documento);
            result = true;
        }
        return result;
    }

    public void doReset() throws BusinessException, Exception {
    }

    public void doReload() throws BusinessException, Exception {
    }
    
    public  AlterarVencimentoDocumentosCobrancaProcess getProcess() throws ProcessException {
        if (process == null)
            process = (AlterarVencimentoDocumentosCobrancaProcess) this.getApplicationBean().getProcessManager().createProcessByName(AlterarVencimentoDocumentosCobrancaProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
        return process;
    }
    
    public String actionAlterar() throws ProcessException {
        log.debug("AlterarVencimentoDocumentosCobrancaBean.actionAlterar");
        
        if (getProcess().runAlterar()) {
            // Adiciona as mensagens de info no Faces
            FacesUtils.addInfoMsgs(process.getMessageList());
            
            // Definir o fluxo de tela
            return FACES_VIEW_PASSO_2;
        }else{
            // Adiciona as mensagens de erro no Faces
            FacesUtils.addErrorMsgs(process.getMessageList());
            // Definir o fluxo de tela
            return FacesUtils.FACES_VIEW_FAILURE;
        }
    }

    /**
     * Permite ir para a tela de impressão dos documentos
     * @throws EntityException 
     */
    public String actionImprimir() throws ParseException, ProcessException, EntityException {
        log.debug("AlterarVencimentoDocumentosCobrancaBean.actionImprimir");
        return imprimirDocumentosCobrancaBean.runWithEntities(this.getProcess().getDocumentos());
    }
    
	/* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}

	public String getRunnableEntityProcessName() {
		return AlterarVencimentoDocumentosCobrancaProcess.PROCESS_NAME;
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
	
	@SuppressWarnings("unchecked")
	public String runWithEntities(IEntityList<?> entities) {
		try {
			this.getProcess().getDocumentos().clear();
			for(IEntity<?> entity: entities)
				this.getProcess().getDocumentos().add((IEntity<DocumentoCobranca>) entity);
		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}
		return FACES_VIEW_PASSO_1;
	}

	public ImprimirDocumentosCobrancaBean getImprimirDocumentosCobrancaBean() {return imprimirDocumentosCobrancaBean;}
	public void setImprimirDocumentosCobrancaBean(ImprimirDocumentosCobrancaBean imprimirDocumentosCobrancaBean) {this.imprimirDocumentosCobrancaBean = imprimirDocumentosCobrancaBean;}
}
