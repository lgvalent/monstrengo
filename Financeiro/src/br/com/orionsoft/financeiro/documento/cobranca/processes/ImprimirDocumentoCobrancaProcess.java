/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.documento.cobranca.processes;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ClassUtils;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.services.CalcularVencimentoService;
import br.com.orionsoft.financeiro.documento.cobranca.services.ImprimirDocumentosCobrancaService;
import br.com.orionsoft.financeiro.documento.cobranca.services.ObterLayoutsDisponiveisService;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo re-imprime um documento que já foi gerado.
 * 
 * <p>
 * <b>Procedimentos:</b><br>
 * Definir o identificador da documento a ser reimpresso:
 * <i>setDocumentoId(long)</i><br>
 * Opcionalmente o layout de impressão do documento pode ser alterado, para
 * isto, basta alterar a propriedade layoutId do documento que é obtido pelo
 * método <i>(IEntity) getDocumento()</i>. Uma lista de layouts disponíveis
 * pode ser obtida pelo método <i>(List<SelectItem>) getLayouts()</i>.<br>
 * <br>
 * Executar a impressão por <i>runImprimir()</i>.
 * 
 * @author Lucio
 * @version 20060717
 * 
 * @spring.bean id="ImprimirDocumentoCobrancaProcess" init-method="start"
 *              destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 * 
 */
@ProcessMetadata(label="Imprimir documento de cobrança", hint="Imprime um documento de cobrança", description="Imprime um documento de cobrança com um layout especificado.")
public class ImprimirDocumentoCobrancaProcess extends ProcessBasic implements IRunnableEntityProcess{
	public static final String PROCESS_NAME = "ImprimirDocumentoCobrancaProcess";

	public String getProcessName() {
		return PROCESS_NAME;
	}

	private IEntity<DocumentoCobranca> documento = null;
	private long documentoId = IDAO.ENTITY_UNSAVED;
	private OutputStream outputStream;
	private int printerIndex = PrintUtils.PRINTER_INDEX_NO_PRINT;
	private Boolean zerarValor = false;
	private Boolean zeradoOuRecalculado = false;

	private Boolean recalcularDataVencimento = false;
	private Calendar dataPagamento = CalendarUtils.getCalendar();
	
    private InputStream inputStreamImagem;


	@Override
	public void start() throws ProcessException {
		super.start();
	}
	
	public int getPrinterIndex() {return printerIndex;}
	public void setPrinterIndex(int printerIndex) {this.printerIndex = printerIndex;}
	public List<SelectItem> getPrinterIndexList() {
		return PrintUtils.retrievePrinters();
	}

	public IEntity<DocumentoCobranca> getDocumento() {
		if (documento == null)
			documento = retrieve(DocumentoCobranca.class, documentoId);

		return documento;
	}

	public long getDocumentoId() {
		return documentoId;
	}

	public void setDocumentoId(long documentoId) {
		/*
		 * Não verifica se houve alteração do id e força uma recarga do
		 * documento, pois em caso do operador alterar o vencimento do documento
		 * e solictar a impressão o sistema não carregava o documento, não
		 * imprimindo, assim, as alterações do operador
		 */
		this.documentoId = documentoId;

		/* Anula a atual instância para forçar a recarga */
		this.documento = null;
	}

	private IEntity<DocumentoCobranca> retrieve(Class<DocumentoCobranca> class1, long id) {
		IEntity<DocumentoCobranca> result = null;
		try {
			result = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(), class1, id, null);
		} catch (BusinessException e) {
			new RuntimeException(e.getMessage());
		}
		return result;
	}

	public boolean runImprimir() {
		super.beforeRun();
		
		try {
			/* Lucio 20121014: Verifica se a opção e zerarValor e recalcular está marcada
			 * para sempre pegar o documento fresco sem alterações em valores e 
			 * vencimentos */
			if(zeradoOuRecalculado){
				this.documento = null;
				this.zeradoOuRecalculado = false;
			}
			
			IEntity<DocumentoCobranca> documentoLocal = getDocumento();
			
			log.debug("Verificando se foi solicitado para zerar o valor");
			if (this.zerarValor){
				this.zeradoOuRecalculado = true; // Indica que o documento original foi alterado e deverá ser recarregado no próximo run
				documentoLocal.getObject().setValor(BigDecimal.ZERO);
			}

			String descricaoAdicionalDataPagamento = "";
			if(this.recalcularDataVencimento){
				this.zeradoOuRecalculado = true; // Indica que o documento original foi alterado e deverá ser recarregado no próximo run
				descricaoAdicionalDataPagamento = "NÃO RECEBER ESTE DOCUMENTO APÓS " + CalendarUtils.formatDate(this.dataPagamento) + ".";
				/* Verifica se o documento está pendente e vencido para então
				 * gerar um documento com data de vencimento, multa e juros recalculados */
				if((CalendarUtils.diffDay(documentoLocal.getObject().getDataVencimento(), this.dataPagamento) < 0)){

					ServiceData sd = new ServiceData(CalcularVencimentoService.SERVICE_NAME, null);
					sd.getArgumentList().setProperty(CalcularVencimentoService.IN_DOCUMENTO, documentoLocal);
					sd.getArgumentList().setProperty(CalcularVencimentoService.IN_DATA_PAGAMENTO, this.dataPagamento);

					this.getProcessManager().getServiceManager().execute(sd);
				}
			}

			/* Imprime o documento */
			ServiceData sd = new ServiceData(ImprimirDocumentosCobrancaService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_DOCUMENTO_OPT, documentoLocal);
			/* Lucio 03092009: Agora o sistema grava a Titulo.instrucao3 que é impressa */
			/* Lucio 20120821: Coloca instrução adicional da data nova de pagamento */
			sd.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_INSTRUCOES_ADICIONAIS_OPT, descricaoAdicionalDataPagamento);
			sd.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_OUTPUT_STREAM_OPT, this.outputStream);
			sd.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_PRINTER_INDEX_OPT, this.printerIndex);
			sd.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_INPUT_STREAM_IMAGEM_OPT, this.inputStreamImagem);
			
			this.getProcessManager().getServiceManager().execute(sd);

			this.getMessageList().addAll(sd.getMessageList());

			if (sd.getMessageList().isTransactionSuccess()) {
				return true;
			}
			return false;

		} catch (BusinessException e) {
			/* Armazenando a lista de erros */
			this.getMessageList().addAll(e.getErrorList());
			return false;
		}
	}

//	public List<SelectItem> getPrintersIndex() {
//		return PrintUtils.retrievePrinters();
//	}

	public List<SelectItem> getLayouts() {
		IEntity<DocumentoCobranca> documento = getDocumento();

		if (documento != null) {
			ServiceData sd = new ServiceData(ObterLayoutsDisponiveisService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(ObterLayoutsDisponiveisService.IN_DOCUMENTO, documento);

			try {
				this.getProcessManager().getServiceManager().execute(sd);
			} catch (ServiceException e) {
				this.getMessageList().clear();
				this.getMessageList().addAll(sd.getMessageList());
			}

			return sd.getFirstOutput();
		}

		/*
		 * Se deu algum erro ou não conseguiu executar o serviço, retorna uma
		 * lista vazia se o documento e inválido
		 */
		return new ArrayList<SelectItem>(0);

	}

	public OutputStream getOutputStream() {return outputStream;}
	public void setOutputStream(OutputStream outputStream) {this.outputStream = outputStream;}
	
	public Boolean getRecalcularDataVencimento() {return recalcularDataVencimento;}
	public void setRecalcularDataVencimento(Boolean recalcularDataVencimento) {this.recalcularDataVencimento = recalcularDataVencimento;}

	public Calendar getDataPagamento() {return dataPagamento;}
	public void setDataPagamento(Calendar dataPagamento) {this.dataPagamento = dataPagamento;}
	
	public Boolean getZerarValor() {return zerarValor;}
	public void setZerarValor(Boolean zerarValor) {this.zerarValor = zerarValor;}
	
	public InputStream getInputStreamImagem() {return inputStreamImagem;}
	public void setInputStreamImagem(InputStream inputStreamImagem) {this.inputStreamImagem = inputStreamImagem;}

	/* IRunnableEntityProcess */
	@SuppressWarnings("unchecked")
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();
		boolean result = false;
		/* Verifica se a entidade passada eh um DocumentoCobranca ou pertence eh descendente */
		if (ClassUtils.isAssignable(entity.getInfo().getType(), DocumentoCobranca.class)) {
			//org.apache.commons.lang.ClassUtils.is
			this.documento = (IEntity<DocumentoCobranca>) entity;
			this.documentoId = entity.getId();
			
			result = true;
		} else
		if (entity.getInfo().getType() == Lancamento.class) {
			try{
				this.documento = entity.getProperty(Lancamento.DOCUMENTO_COBRANCA).getValue().getAsEntity();
				this.documentoId = entity.getId();

				result = true;
			} catch (BusinessException e) {
				this.getMessageList().addAll(e.getErrorList());
			}
		} else {
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}
		return result;
	}

}
