package br.com.orionsoft.financeiro.view.jsf.documento.pagamento;

import java.io.IOException;
import java.text.ParseException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.crud.LabelBean;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;
import br.com.orionsoft.financeiro.documento.pagamento.processes.ImprimirDocumentosPagamentoProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Bean que controla a view de impress�o de documentos
 * 
 * O fluxo das telas � composto por 4 views (visoes) cada uma correspondendo 
 * a um passo do processo. FACES_VIEW_ETIQUETAS apenas redireciona para a 
 * tela de visualiza��o das etiquetas.
 * 
 * Cada String correspondente �s constantes declaradas (FACES_VIEW...) est� 
 * mapeada para uma p�gina no arquivo faces-config.xml
 * 
 * No passo1, obtem-se os parametros necessarios para o processo ImprimirDocumentosPagamentoProcess
 * No passo2, a lista de documentos � mostrada, a partir do bot�o Visualizar do passo1
 * No passo3, se o operador optou por imprimir os documentos, as mensagens relacionadas 
 * a esse processo ser�o mostradas, tendo ainda a op��o de gerar etiquetas
 * No passo4, se o operador optou por gerar etiquetas, as mensagens relacionadas 
 * a esse processo ser�o mostradas, tendo ainda a op��o de imprimir os documentos
 */
@ManagedBean
@SessionScoped
public class ImprimirDocumentosPagamentoBean extends BeanSessionBasic {
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que � ativada para cada view */
	public static final String FACES_VIEW_PASSO_1 = "financeiro/imprimirDocumentosPagamento1";
	public static final String FACES_VIEW_PASSO_2 = "financeiro/imprimirDocumentosPagamento2";
	
    private ImprimirDocumentosPagamentoProcess currentProcess = null;

    public ImprimirDocumentosPagamentoProcess getCurrentProcess() {
		
		if (currentProcess == null) {
			try {
				currentProcess = (ImprimirDocumentosPagamentoProcess) this.getApplicationBean().getProcessManager().createProcessByName(ImprimirDocumentosPagamentoProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
			} catch (ProcessException e) {
				FacesUtils.addErrorMsgs(e.getErrorList());
			}
		}
    	return currentProcess;
	}

    /**
     * Este m�todo retorna uma String para o FacesUtils 
     * redirecionar para a proxima tela, onde estar� a visualiza��o
     *
     */
    public String doVisualizar(){
    	/* Os par�metros s�o inseridos diretamente no processo
    	 * pelo Faces */
    	String result = null;
    	if(getCurrentProcess().runVisualizar()){
			FacesUtils.addInfoMsgs(getCurrentProcess().getMessageList());
   			result = FACES_VIEW_PASSO_2;
    	}
    	else{
    		FacesUtils.addErrorMsgs(getCurrentProcess().getMessageList());
    		result = FACES_VIEW_PASSO_1;
    	}
    	
    	return result;
    }

    public String doVisualizarEtiquetas(){
    	return LabelBean.FACES_VIEW_LABELS;
    }
    
    /**
     * Esta a��o gera o PDF do documento
     * @return
     * @throws ParseException
     * @throws BusinessException
     */
    public void doDownload() throws ParseException, BusinessException {
        log.debug("ImprimirDocumentosPagamentoBean.actionImprimir");

       	try {
       		/* Define o outputStream */
       		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
       		response.setContentType("pdf-content");
       		response.setHeader("Content-Disposition", "attachment;filename=\"" + prepareFileName() + ".pdf\"");

       		ServletOutputStream out = response.getOutputStream();
       		
       		/* Define o outputStream que receber� o arquivo pdf do relat�rio
       		 * E anula o �ndice da impressora */
       		this.getCurrentProcess().setOutputStream(out);
       		this.getCurrentProcess().setPrinterIndex(PrintUtils.PRINTER_INDEX_NO_PRINT);

       		if (this.getCurrentProcess().runImprimir()){
       			/* Adiciona as mensagens de info no Faces */
       			FacesUtils.addInfoMsgs(this.getCurrentProcess().getMessageList());

       		}else{
       			/* Adiciona as mensagens de erro no Faces */
       			FacesUtils.addErrorMsgs(this.getCurrentProcess().getMessageList());
       			response.setContentType("text/html");
           		response.setHeader("Content-Disposition", "filename=\"error.html\"");

       			out.print(this.getCurrentProcess().getMessageList().get(0).getMessage());
       		}
			out.flush();
			out.close();
			FacesContext.getCurrentInstance().responseComplete();
       	} catch (IOException e) {
       		FacesUtils.addErrorMsg(e.getMessage());
		}
    }
    
    /**
     * Esta a��o imprime o documento na impressora definida no printerIndex
     * atual do processo
     * @return
     * @throws ParseException
     * @throws BusinessException
     */
    public void doImprimir() throws BusinessException {
    	log.debug("ImprimirDocumentosPagamentoBean.doImprimir");

    	/* O printerIndex � injetado diretamente pelo JSF
    	 * E anula o outputStream */
    	//this.getProcess().setPrinterIndex(printerIndex);
   		this.getCurrentProcess().setOutputStream(null);

    	if (this.getCurrentProcess().runImprimir()){
    		/* Adiciona as mensagens de info no Faces */
    		FacesUtils.addInfoMsgs(this.getCurrentProcess().getMessageList());

    	}else{
    		/* Adiciona as mensagens de erro no Faces */
    		FacesUtils.addErrorMsgs(this.getCurrentProcess().getMessageList());
    	}
    }    
    
    private String prepareFileName() {
    	String result = "Doc ";
    	
    	/* Vencimento !!!!!!!!! */
    	result += CalendarUtils.formatDate(getCurrentProcess().getDataDocumentoDe()) + " a " + CalendarUtils.formatDate(getCurrentProcess().getDataDocumentoAte());
    	result += " - Vencimento " + CalendarUtils.formatDate(getCurrentProcess().getDataVencimentoDe()) + " a " + CalendarUtils.formatDate(getCurrentProcess().getDataVencimentoAte());
    	
    	/* Se tiver CNPJ simplesmente exibe o CNPJ no nome do PDF */
    	if(StringUtils.isNotEmpty(getCurrentProcess().getCpfCnpj()))
    		result += "-" + getCurrentProcess().getCpfCnpj();
    	else{
        	/* Sen�o exibe as informa��es definidas no filtro e o n�mero de guias que cont�m o arquivo */
    		if(getCurrentProcess().getEscritorioContabilId() != null && getCurrentProcess().getEscritorioContabilId().compareTo(IDAO.ENTITY_UNSAVED)==1)
        		result += " - Escrit�rio " + getCurrentProcess().getEscritorioContabilId();
    		if(getCurrentProcess().getMunicipioId() != null && getCurrentProcess().getMunicipioId().compareTo(IDAO.ENTITY_UNSAVED)==1)
        		result += " - Munic�pio " + getCurrentProcess().getMunicipioId();
    		if(getCurrentProcess().getExcluirMunicipio() != null && getCurrentProcess().getExcluirMunicipio())
        		result += " (Omitir)";
    		result += " - " + getCurrentProcess().getSize() + " guia(s)";
    	}
    	
    	/* Substitui a / pelo - no nome do arquivo */
    	return result.replace("/", "-");
    }
    
    public void doGerarEtiquetas() {
    	if (getCurrentProcess().runGerarEtiquetas()) {
    		/* Definir as mensagens a serem exibidas na pagina */
    		FacesUtils.addInfoMsg("");//O menuLayout mostra a primeira mensagem contida no messagesBean
    		FacesUtils.addInfoMsg("<hr>");
    		FacesUtils.addInfoMsgs(getCurrentProcess().getMessageList());
    	} else {
    		FacesUtils.addErrorMsg("Ocorreram erros durante a gera��o de etiqueta.");
    		FacesUtils.addErrorMsgs(getCurrentProcess().getMessageList());
    		/* Definir o fluxo de tela de FAILURE */
    	}    
    }
    
    /**
     * Este metodo faz a conclus�o do processo. 
     * Quando chamado pela interface, fecha a tela corrente.
     */
    public String doConcluir() {
    	if (currentProcess != null) {
    		try {
				currentProcess.finish();
			} catch (ProcessException e) {
				FacesUtils.addErrorMsgs(e.getErrorList());
			}
    		currentProcess = null;
    	}
    	return FacesUtils.FACES_VIEW_CLOSE;
    }
    
	public void doReset() throws BusinessException, Exception
	{
	}

	public void doReload() throws BusinessException, Exception
	{
	}

}
