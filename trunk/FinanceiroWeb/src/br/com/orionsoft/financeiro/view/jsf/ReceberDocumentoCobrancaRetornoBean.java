/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.view.jsf;

import java.io.IOException;
import java.text.ParseException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;
import br.com.orionsoft.financeiro.documento.cobranca.processes.ReceberDocumentoCobrancaRetornoProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Bean que controla a view para receber um arquivo de retorno para um determinado cedente; 
 */
@ManagedBean
@SessionScoped
public class ReceberDocumentoCobrancaRetornoBean extends BeanSessionBasic  implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "receberDocumentoCobrancaRetornoBean";
	public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/documentoCobrancaReceberRetorno1?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_2 = "/pages/financeiro/documentoCobrancaReceberRetorno2?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_3 = "/pages/financeiro/documentoCobrancaReceberRetorno3?faces-redirect=true";

	//  será feito o upload de um arquivoRetorno de retorno
    private UploadedFile arquivoRetorno;
	private ReceberDocumentoCobrancaRetornoProcess process = null;
    
    public ReceberDocumentoCobrancaRetornoProcess getProcess() {
    	try {
    		if (process == null)
    			process = (ReceberDocumentoCobrancaRetornoProcess)this.getApplicationBean().getProcessManager().createProcessByName(ReceberDocumentoCobrancaRetornoProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
    	} catch (ProcessException e) {
    		e.printStackTrace();
    	}
        return process;
    }
    
    public String actionStart() {
    	return FACES_VIEW_PASSO_1;
	}
    
    /**
     * Evitar ter que colocar os componentes dentro de um <form enctype="multipart/form-data">
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {  
        FacesMessage msg = new FacesMessage("Arquivo " + event.getFile().getFileName() + " carregado com sucesso.");  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
        this.arquivoRetorno = event.getFile();
    } 
    /**
     * Para o upload é usado o MyFaces, que usa um objeto do tipo UploadedFile.
     * O Bean após obter esse objeto, transforma-o em um File através do FileCopyUtils, 
     * também do MyFaces, e então seta o Bean com o objeto correto (um File).
     */
    public String actionVisualizar() throws IOException {
    	log.debug("RETORNO " + arquivoRetorno.getFileName().toUpperCase());
    	
		process.setInputStream(arquivoRetorno.getInputstream());
		process.setNomeRetorno(arquivoRetorno.getFileName());
		
    	return FACES_VIEW_PASSO_2;
    }
    
    public String actionReceberRetorno() throws ParseException {
    	log.debug("ReceberDocumentoCobrancaRetornoBean.actionReceberRetorno");
		
    	if (process.runReceberRetorno()) {
        	/* Adiciona as mensagens de info no Faces */
        	FacesUtils.addInfoMsgs(process.getMessageList());
            return FACES_VIEW_PASSO_3;
        }else{
            /* Adiciona as mensagens de erro no Faces */
            FacesUtils.addErrorMsgs(process.getMessageList());
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        
    }
    
    public UploadedFile getArquivoRetorno() { return arquivoRetorno; }
    public void setArquivoRetorno(UploadedFile arquivo) { this.arquivoRetorno = arquivo; }
    
	public void loadParams() throws Exception { }
	
	public void doReset() throws BusinessException, Exception { }
	
	public void doReload() throws BusinessException, Exception { }
	
	/* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}

	public String getRunnableEntityProcessName() {
		return ReceberDocumentoCobrancaRetornoProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		this.getProcess().runWithEntity(entity);
		return FACES_VIEW_PASSO_1;
	}
}
