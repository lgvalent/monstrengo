package br.com.orionsoft.financeiro.view.jsf.documento.cobranca;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.model.UploadedFile;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaBean;
import br.com.orionsoft.financeiro.documento.cobranca.processes.ImprimirDocumentosCobrancaProcess;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.bean.IRunnableProcessView;
import br.com.orionsoft.monstrengo.view.jsf.crud.LabelBean;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que controla a view de impressão de documentos
 * 
 * O fluxo das telas é composto por 4 views (visoes) cada uma correspondendo 
 * a um passo do processo. FACES_VIEW_ETIQUETAS apenas redireciona para a 
 * tela de visualização das etiquetas.
 * 
 * Cada String correspondente às constantes declaradas (FACES_VIEW...) está 
 * mapeada para uma página no arquivo faces-config.xml
 * 
 * No passo1, obtem-se os parametros necessarios para o processo ImprimirDocumentosCobrancaProcess
 * No passo2, a lista de documentos é mostrada, a partir do botão Visualizar do passo1
 * No passo3, se o operador optou por imprimir os documentos, as mensagens relacionadas 
 * a esse processo serão mostradas, tendo ainda a opção de gerar etiquetas
 * No passo4, se o operador optou por gerar etiquetas, as mensagens relacionadas 
 * a esse processo serão mostradas, tendo ainda a opção de imprimir os documentos
 * 
 */
@ManagedBean
@SessionScoped
public class ImprimirDocumentosCobrancaBean extends BeanSessionBasic implements IRunnableProcessView{
	private static final long serialVersionUID = 1L;

	/** Define a view JSF que é ativada para cada view */
	public static final String VIEW_NAME = "imprimirDocumentosCobrancaBean";
	public static final String FACES_VIEW_PASSO_1 = "/pages/financeiro/documentoCobrancaImprimir1?faces-redirect=true";
	public static final String FACES_VIEW_PASSO_2 = "/pages/financeiro/documentoCobrancaImprimir2?faces-redirect=true";

	public static final String URL_PARAM_NOME_GERENCIADOR_DOCUMENTO = "nomeGerenciadorDocumento";

	//  será feito o upload de um arquivoRetorno de retorno
	private UploadedFile arquivoImagem;
	private ImprimirDocumentosCobrancaProcess currentProcess = null;

	public ImprimirDocumentosCobrancaProcess getCurrentProcess() {

		if (currentProcess == null) {
			try {
				currentProcess = (ImprimirDocumentosCobrancaProcess) this.getApplicationBean().getProcessManager().createProcessByName(ImprimirDocumentosCobrancaProcess.PROCESS_NAME, this.getUserSessionBean().getUserSession());
			} catch (ProcessException e) {
				FacesUtils.addErrorMsgs(e.getErrorList());
			}
		}
		return currentProcess;
	}

	public String actionStart() {
		loadParams();

		return FACES_VIEW_PASSO_1;
	}

	/**
	 * Verifica se veio um parametro de filtro de nomeGerenciadorDocumento para listar somente
	 * documentos de um determinado gerenciador (GRCS, Titulos, etc)
	 */
	public void loadParams(){
		if (FacesUtils.isNotNull(super.getRequestParams().get(URL_PARAM_NOME_GERENCIADOR_DOCUMENTO))){ 
			String nomeGerenciadorDocumento = super.getRequestParams().get(URL_PARAM_NOME_GERENCIADOR_DOCUMENTO).toString();

			this.getCurrentProcess().setNomeGerenciadorDocumento(nomeGerenciadorDocumento);
		}
	}

	/**
	 * Este método retorna uma String para o FacesUtils 
	 * redirecionar para a proxima tela, onde estará a visualização
	 *
	 */
	public String doVisualizar(){
		/* Os parâmetros são inseridos diretamente no processo
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
	 * Esta ação gera o PDF do documento
	 * @return
	 * @throws ParseException
	 * @throws BusinessException
	 */
	public void doDownload() throws ParseException, BusinessException {
		log.debug("ImprimirDocumentoCobrancaBean.doDownload");

		/* Permite acionar esta action de tela passando somente o documentoId pela URL */
		this.loadParams();

		try {
			/* Define o outputStream */
			HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
			if(this.currentProcess.getDownloadPdfZip()) {
				response.setContentType("application/zip");
				response.setHeader("Content-Disposition", "attachment;filename=\"" + prepareFileName() + ".zip\"");
			}else {
				response.setContentType("pdf-content");
				response.setHeader("Content-Disposition", "attachment;filename=\"" + prepareFileName() + ".pdf\"");
			}

			ServletOutputStream out = response.getOutputStream();

			/* Define o outputStream que receberá o arquivo pdf do relatório
			 * E anula o índice da impressora */
			this.getCurrentProcess().setEnviarEMail(false);
			this.getCurrentProcess().setOutputStream(out);
			this.getCurrentProcess().setPrinterIndex(PrintUtils.PRINTER_INDEX_NO_PRINT);
			this.getCurrentProcess().setInputStreamImagem(arquivoImagem==null?null:arquivoImagem.getInputstream());

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
	 * Efetua o download do documento em PDF separados em ZIP
	 */
	public void doDownloadPdfZip() {
		try {
			/* Informa ao processo para enviar e-mail */
			this.getCurrentProcess().setEnviarEMail(false);
			this.getCurrentProcess().setDownloadPdfZip(true);
			this.doDownload();
		} catch (Exception e) {
			FacesUtils.addErrorMsg(e.getMessage());
		}

	}

	/**
	 * Efetua o download do documento em PDF
	 */
	public void doEnviarEMail() {
		try {
			/* Informa ao processo para enviar e-mail */
			this.getCurrentProcess().setEnviarEMail(true);
			this.getCurrentProcess().setOutputStream(null);
			this.getCurrentProcess().setPrinterIndex(PrintUtils.PRINTER_INDEX_NO_PRINT);
			this.getCurrentProcess().setInputStreamImagem(arquivoImagem==null?null:arquivoImagem.getInputstream());

			if (this.getCurrentProcess().runImprimir()){
				/* Adiciona as mensagens de info no Faces */
				FacesUtils.addInfoMsgs(this.getCurrentProcess().getMessageList());
			}else{
				/* Adiciona as mensagens de erro no Faces */
				FacesUtils.addErrorMsgs(this.getCurrentProcess().getMessageList());
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

		try{
			/* O printerIndex é injetado diretamente pelo JSF
			 * E anula o outputStream */
			//this.getProcess().setPrinterIndex(printerIndex);
			this.getCurrentProcess().setEnviarEMail(false);
			this.getCurrentProcess().setOutputStream(null);
			this.getCurrentProcess().setInputStreamImagem(arquivoImagem==null?null:arquivoImagem.getInputstream());

			if (this.getCurrentProcess().runImprimir()){
				/* Adiciona as mensagens de info no Faces */
				FacesUtils.addInfoMsgs(this.getCurrentProcess().getMessageList());

			}else{
				/* Adiciona as mensagens de erro no Faces */
				FacesUtils.addErrorMsgs(this.getCurrentProcess().getMessageList());
			}
		} catch (IOException e) {
			FacesUtils.addErrorMsg(e.getMessage());
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
			/* Senão exibe as informações definidas no filtro e o número de guias que contém o arquivo */
			if(!getCurrentProcess().getParamEscritorioContabil().isNull())
				result += " - Escritório " + getCurrentProcess().getParamEscritorioContabil().getValue();
			if(!getCurrentProcess().getParamMunicipio().isNull())
				result += " - Município " + getCurrentProcess().getParamMunicipio().getValue();
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
			FacesUtils.addErrorMsg("Ocorreram erros durante a geração de etiqueta.");
			FacesUtils.addErrorMsgs(getCurrentProcess().getMessageList());
			/* Definir o fluxo de tela de FAILURE */
		}    
	}

	/**
	 * Este metodo faz a conclusão do processo. 
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

	public UploadedFile getArquivoImagem() {return arquivoImagem;}
	public void setArquivoImagem(UploadedFile arquivoImagem) {this.arquivoImagem = arquivoImagem;}



	@SuppressWarnings("unchecked")
	public String runWithEntities(IEntityCollection<?> entities) {
		List<DocumentoCobrancaBean> docs = new ArrayList<DocumentoCobrancaBean>(entities.getSize());

		/* Converte a lista de IEntity em beans */
		for(IEntity<?> entitiy: entities){
			if (ClassUtils.isAssignable(entities.getInfo().getType(), DocumentoCobranca.class)){
				docs.add(new DocumentoCobrancaBean((IEntity<? extends DocumentoCobranca>) entitiy, ""));
			}
			else if (ClassUtils.isAssignable(entities.getInfo().getType(), Lancamento.class)){
				IEntity<Lancamento> lan = (IEntity<Lancamento>) entitiy;
				/* verifica se tem documento de cobrança neste lançamento */
				try {
					entitiy = (IEntity<?>) lan.getPropertyValue(Lancamento.DOCUMENTO_COBRANCA);
				} catch (EntityException e) {
					FacesUtils.addErrorMsgs(e.getErrorList());
					return FacesUtils.FACES_VIEW_FAILURE;
				}
				if(entitiy != null)
					docs.add(new DocumentoCobrancaBean((IEntity<? extends DocumentoCobranca>) entitiy, ""));
			}
		}

		/* Envia os beans para o processo de impressão de documentos */
		this.getCurrentProcess().setBeanList(docs);

		return FACES_VIEW_PASSO_2;
	}

	/* IRunnableProcessView */
	public String getViewName() {
		return VIEW_NAME;
	}

	public String getRunnableEntityProcessName() {
		return ImprimirDocumentosCobrancaProcess.PROCESS_NAME;
	}

	public String runWithEntity(IEntity<?> entity) {
		if (!this.getCurrentProcess().runWithEntity(entity)){
			FacesUtils.addErrorMsgs(this.getCurrentProcess().getMessageList());
			return FacesUtils.FACES_VIEW_FAILURE;
		}

		return FACES_VIEW_PASSO_1;
	}


}
