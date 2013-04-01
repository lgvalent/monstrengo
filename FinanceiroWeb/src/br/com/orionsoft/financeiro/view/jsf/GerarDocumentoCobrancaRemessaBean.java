/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.view.jsf;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.FileCopyUtils;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;
import br.com.orionsoft.financeiro.documento.cobranca.processes.GerarDocumentoCobrancaRemessaProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Bean que controla a view para gerar um arquivo de remessa para
 * um determinado convênio e determinado período; 
 *
 */
@ManagedBean
@SessionScoped
public class GerarDocumentoCobrancaRemessaBean extends BeanSessionBasic implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "gerarDocumentoCobrancaRemessaBean";
	public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/documentoCobrancaGerarRemessa1?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_2 = "/pages/financeiro/documentoCobrancaGerarRemessa2?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_3 = "/pages/financeiro/documentoCobrancaGerarRemessa3?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_4 = "/pages/financeiro/documentoCobrancaGerarRemessa4?faces-redirect=true";

	private GerarDocumentoCobrancaRemessaProcess process = null;
    
    public GerarDocumentoCobrancaRemessaProcess getProcess() {
    	try {
    		if (process == null)
    			process = (GerarDocumentoCobrancaRemessaProcess)this.getApplicationBean().getProcessManager().createProcessByName(GerarDocumentoCobrancaRemessaProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
    	} catch (ProcessException e) {
    		e.printStackTrace();
    	}
        return process;
    }
    
    public String actionStart(){
    	return FACES_VIEW_PASSO_1;
    }
    
    public String actionVisualizar() throws ParseException {
    	log.debug("GerarDocumentoRemessaBean.actionVisualizar");
    	if (this.process.isQuantidadeDiasProtestoValida()){
        	log.debug("GerarDocumentoRemessaBean.isQtde:"+ FACES_VIEW_PASSO_2);
    		return FACES_VIEW_PASSO_2;
    	}else{
    		FacesUtils.addInfoMsg("A quantidade de dias para protesto deve conter no máximo duas casas decimais.");
        	log.debug("GerarDocumentoRemessaBean.return?");
    		return "";
    	}
    	
    }

    public String actionGerarRemessa() throws ParseException {
    	log.debug("GerarDocumentoRemessaBean.actionGerarRemessa");

    	if (process.runGerarRemessa()) {
        	/* Adiciona as mensagens de info no Faces */
        	FacesUtils.addInfoMsgs(process.getMessageList());
            return FACES_VIEW_PASSO_3;
        }else{
            /* Adiciona as mensagens de erro no Faces */
            FacesUtils.addErrorMsgs(process.getMessageList());
            return FacesUtils.FACES_VIEW_FAILURE;
        }
        
    }
    
    public String actionRelatorio() throws ParseException {
    	FacesUtils.addInfoMsgs(process.getMessageList());
    	return FacesUtils.FACES_VIEW_SUCCESS;
    }
    
    /* 
     * Executa o download do arquivo de remessa  
     * Esse método é executado após o actionGerarRemessa, 
     * faz parte da view FACES_VIEW_PASSO_3
	 */
	public void actionDownloadRemessa() throws BusinessException{
		
		// Prepara o outPutStream
		try {
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			response.setContentType("text-content"); //vinculo com um documento texto
			
			/* Verifica se o relatório possui um nome se nao tiver, lançar um erro indicando */
			String nomeArquivoRemessa = process.getNomeArquivoRemessa();
			if(StringUtils.isEmpty(nomeArquivoRemessa)){}
				//TODO erro erro erro erro erro erro erro erro erro erro 

			//põe em cache o nome do arquivo e sua extensão
			response.setHeader("Content-Disposition", "attachment;filename=\"" + nomeArquivoRemessa);
			
			ServletOutputStream out = response.getOutputStream();
			//TODO ler do precess.getArquivoRemessa() para o out
			
			FileInputStream input = new FileInputStream(process.getArquivoRemessa());
			
			//copia o conteúdo do arquivo do 1o parametro para o arquivo do 2o parametro 
			FileCopyUtils.copy(input, out);
			
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();			
			
		} catch (IOException e) {
			throw new BusinessException(MessageList.createSingleInternalError(e));
		} catch (Exception e) {
			throw new BusinessException(MessageList.createSingleInternalError(e));
		}
		
	}
	
	public void loadParams() throws Exception { }
	
	public void doReset() throws BusinessException, Exception { }
	
	public void doReload() throws BusinessException, Exception { }
	
	/* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}

	public String getRunnableEntityProcessName() {
		return GerarDocumentoCobrancaRemessaProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		this.getProcess().runWithEntity(entity);
		return FACES_VIEW_PASSO_1;
	}
}
