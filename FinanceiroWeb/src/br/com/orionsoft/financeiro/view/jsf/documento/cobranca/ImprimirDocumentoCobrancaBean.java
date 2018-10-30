/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.view.jsf.documento.cobranca;

import java.io.IOException;
import java.text.ParseException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.UploadedFile;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.processes.ImprimirDocumentoCobrancaProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Bean que cotrola a view de impressão de documento de cobrança
 * @author lucio
 */
@ManagedBean
@SessionScoped
public class ImprimirDocumentoCobrancaBean extends BeanSessionBasic implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	public static final String URL_PARAM_DOCUMENTO_ID = "documentoId";

    /** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "imprimirDocumentoCobrancaBean";
    public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/documentoCobrancaImprimir?faces-redirect=true";
    
    private UploadedFile arquivoImagem;
    private ImprimirDocumentoCobrancaProcess process = null;
    
    public void loadParams() throws BusinessException {
        if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_DOCUMENTO_ID))){ 
        	IEntity<? extends DocumentoCobranca> documento = UtilsCrud.retrieve(getProcess().getProcessManager().getServiceManager(), DocumentoCobranca.class, Long.parseLong(super.getRequestParams().get(URL_PARAM_DOCUMENTO_ID).toString()), null);
        	
        	/* Solicita ao processo que prepare a sua execução com base na entidade documento fornecida */
    	    this.getProcess().runWithEntity(documento);
        }
    }

    public void doReset() throws BusinessException, Exception {
    }

    public void doReload() throws BusinessException, Exception {
    }
    
    public ImprimirDocumentoCobrancaProcess getProcess() throws ProcessException {
        if (process == null)
            process = (ImprimirDocumentoCobrancaProcess)this.getApplicationBean().getProcessManager().createProcessByName(ImprimirDocumentoCobrancaProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
        return process;
    }
    
    /**
     * Para a execução desta ação um parâmetro documentoId deve ser
     * passado na requisição
     * @return
     * @throws ParseException
     * @throws BusinessException
     */
    public String actionPrepararDocumento() throws ParseException, BusinessException {
        log.debug("ImprimirDocumentoCobrancaBean.actionPrepararDocumento");

        /* Pega por parâmetro de URL o grupo e o valor sugerido */
       	this.loadParams();

        /* Definir o fluxo de tela de SUCESSO */
        return FACES_VIEW_PASSO_1;
    }

    /**
     * Esta ação gera o PDF do documento
     * @return
     * @throws ParseException
     * @throws BusinessException
     */
    public void doDownload() throws ParseException, BusinessException {
        log.debug("ImprimirDocumentoCobrancaBean.actionImprimir");

        /* Permite acionar esta action de tela passando somente o documentoId pela URL */
       	this.loadParams();
       	
       	doDownload(this.getProcess().getDocumento());
    }
    /**
     * Esta ação gera o PDF do documento
     * @return
     * @throws ParseException
     * @throws BusinessException
     */
    public void doDownload(IEntity<? extends DocumentoCobranca> documento) throws ParseException, BusinessException {
        log.debug("ImprimirDocumentoCobrancaBean.actionImprimir(IEntity)");

       	try {
       		/* Define o outputStream */
       		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
   			response.setContentType("pdf-content");
       		String fileName = "DocumentoCobranca-"+ documento.getId();
       		response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + ".pdf\"");

       		ServletOutputStream out = response.getOutputStream();
       		
       		/* Define o outputStream que receberá o arquivo pdf do relatório
       		 * E anula o índice da impressora */
      		this.getProcess().setEnviarEMail(false);
       		this.getProcess().setOutputStream(out);
       		this.getProcess().setPrinterIndex(PrintUtils.PRINTER_INDEX_NO_PRINT);
       		this.getProcess().setInputStreamImagem(arquivoImagem==null?null:arquivoImagem.getInputstream());
       		this.getProcess().runWithEntity(documento);

       		if (this.getProcess().runImprimir()){
       			/* Adiciona as mensagens de info no Faces */
       			FacesUtils.addInfoMsgs(this.getProcess().getMessageList());

       		}else{
       			/* Adiciona as mensagens de erro no Faces */
       			FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
       			response.setContentType("text/html");
           		response.setHeader("Content-Disposition", "filename=\"error.html\"");

       			out.print(this.getProcess().getMessageList().get(0).getMessage());
       		}
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();
       	} catch (IOException e) {
       		FacesUtils.addErrorMsg(e.getMessage());
		}
    }

    public void doEnviarEMail() throws BusinessException {
    	log.debug("ImprimirDocumentoCobrancaBean.doEnviarEMail");
    	
    	/* Permite acionar esta action de tela passando somente o documentoId pela URL */
    	this.loadParams();
    	
    	/* Passa o documento lido pelo loadParams e injetado diretamente processo*/
    	doEnviarEMail(this.getProcess().getDocumento());
    	
    }
    
    /**
     * Esta ação gera o PDF do documento
     * @return
     * @throws ParseException
     * @throws BusinessException
     */
    public void doEnviarEMail(IEntity<? extends DocumentoCobranca> documento) throws BusinessException {
        log.debug("ImprimirDocumentoCobrancaBean.doEnviarEMail(IEntity)");

       	try {
       		/* Informa ao processo para enviar e-mail */
      		this.getProcess().setEnviarEMail(true);
       		this.getProcess().setOutputStream(null);
       		this.getProcess().setPrinterIndex(PrintUtils.PRINTER_INDEX_NO_PRINT);
       		this.getProcess().setInputStreamImagem(arquivoImagem==null?null:arquivoImagem.getInputstream());
       		this.getProcess().runWithEntity(documento);

       		if (this.getProcess().runImprimir()){
       			/* Adiciona as mensagens de info no Faces */
       			FacesUtils.addInfoMsgs(this.getProcess().getMessageList());

    		}else{
    			/* Adiciona as mensagens de erro no Faces */
    			FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
       		}
       	} catch (IOException e) {
       		FacesUtils.addErrorMsg(e.getMessage());
		}
    }
    
    /**
     * Esta ação imprime o documento na impressora definida no printerIndex
     * atual do processo
     * @return
     * @throws ParseException
     * @throws BusinessException
     */
    public void doImprimir() throws BusinessException {
    	log.debug("ImprimirDocumentoCobrancaBean.doImprimir");

    	/* Permite acionar esta action de tela passando somente o documentoId pela URL */
    	this.loadParams();
    	
    	/* Passa o documento lido pelo loadParams e injetado diretamente processo*/
    	doImprimir(this.getProcess().getDocumento());

    }
    
    /**
     * Esta ação imprime o documento na impressora definida no printerIndex
     * atual do processo
     * @return
     * @throws ParseException
     * @throws BusinessException
     */
    public void doImprimir(IEntity<? extends DocumentoCobranca> documento) throws BusinessException {
    	log.debug("ImprimirDocumentoCobrancaBean.doImprimir(IEntity)");

    	try{
    		/* O printerIndex é injetado diretamente pelo JSF
    		 * E anula o outputStream */
    		//this.getProcess().setPrinterIndex(printerIndex);
      		this.getProcess().setEnviarEMail(false);
    		this.getProcess().setOutputStream(null);
    		this.getProcess().setInputStreamImagem(arquivoImagem==null?null:arquivoImagem.getInputstream());
    		this.getProcess().runWithEntity(documento);

    		if (this.getProcess().runImprimir()){
    			/* Adiciona as mensagens de info no Faces */
    			FacesUtils.addInfoMsgs(this.getProcess().getMessageList());

    		}else{
    			/* Adiciona as mensagens de erro no Faces */
    			FacesUtils.addErrorMsgs(this.getProcess().getMessageList());
    		}
    	} catch (IOException e) {
    		FacesUtils.addErrorMsg(e.getMessage());
    	}

    }
    

    
    public UploadedFile getArquivoImagem() {
		return arquivoImagem;
	}

	public void setArquivoImagem(UploadedFile arquivoImagem) {
		this.arquivoImagem = arquivoImagem;
	}

	/* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}

	public String getRunnableEntityProcessName() {
		return ImprimirDocumentoCobrancaProcess.PROCESS_NAME;
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
	
	public String runWithEntities(IEntityCollection<?> entities) {
		return FacesUtils.FACES_VIEW_FAILURE;
	}

}
