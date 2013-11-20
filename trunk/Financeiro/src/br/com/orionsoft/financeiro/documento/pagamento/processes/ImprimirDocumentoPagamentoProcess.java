/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.documento.pagamento.processes;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ClassUtils;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.services.ImprimirDocumentosPagamentoService;
import br.com.orionsoft.financeiro.documento.pagamento.services.ObterLayoutsDisponiveisDocumentoPagamentoService;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo imprime um documento que já foi gerado.
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
 * @version 20081201
 * 
 * @spring.bean id="ImprimirDocumentoPagamentoProcess" init-method="start"
 *              destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 * 
 */
@ProcessMetadata(label="Imprimir documento de pagamento", hint="Imprime um documento de pagamento", description="Imprime um documento de pagmento com um layout especificado.")
public class ImprimirDocumentoPagamentoProcess extends ProcessBasic implements IRunnableEntityProcess{
	public static final String PROCESS_NAME = "ImprimirDocumentoPagamentoProcess";

	public String getProcessName() {
		return PROCESS_NAME;
	}

	private IEntity<? extends DocumentoPagamento> documento = null;
	private long documentoId = IDAO.ENTITY_UNSAVED;
	private OutputStream outputStream;

    private int printerIndex = PrintUtils.PRINTER_INDEX_NO_PRINT;

	public int getPrinterIndex() {return printerIndex;}
	public void setPrinterIndex(int printerIndex) {this.printerIndex = printerIndex;}
	public List<SelectItem> getPrinterIndexList() {
		return PrintUtils.retrievePrinters();
	}

	public IEntity<? extends DocumentoPagamento> getDocumento() {
		if (documento == null)
			documento = retrieve(DocumentoPagamento.class, documentoId);

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

	private IEntity<DocumentoPagamento> retrieve(Class<DocumentoPagamento> class1, long id) {
		IEntity<DocumentoPagamento> result = null;
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
		    IEntity<? extends DocumentoPagamento> doc= getDocumento();
			
			/* Imprime o documento */
			ServiceData sd = new ServiceData(ImprimirDocumentosPagamentoService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(ImprimirDocumentosPagamentoService.IN_DOCUMENTO_OPT, doc);
			/* Coloca como descrição adicional a descrição do movimento */
			if(doc.getObject().getLancamentos().size()>0){
				sd.getArgumentList().setProperty(
						ImprimirDocumentosPagamentoService.IN_INSTRUCOES_ADICIONAIS_OPT,
						getDocumento().getProperty(DocumentoPagamento.LANCAMENTOS).getValue().getAsEntityCollection()
						.getFirst().getProperty(Lancamento.DESCRICAO).getValue().getAsString());
			}
			sd.getArgumentList().setProperty(ImprimirDocumentosPagamentoService.IN_OUTPUT_STREAM_OPT, this.outputStream);
			sd.getArgumentList().setProperty(ImprimirDocumentosPagamentoService.IN_PRINTER_INDEX_OPT, this.printerIndex);
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

	public List<SelectItem> getLayouts() {
		IEntity<? extends DocumentoPagamento> documento = getDocumento();

		if (documento != null) {
			ServiceData sd = new ServiceData(ObterLayoutsDisponiveisDocumentoPagamentoService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(ObterLayoutsDisponiveisDocumentoPagamentoService.IN_DOCUMENTO, documento);

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

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	/* IRunnableEntityProcess */
	public boolean runWithEntity(IEntity entity) {
		super.beforeRun();
		boolean result = false;
		/* Verifica se a entidade passada eh um DocumentoPagamento ou pertence eh descendente */
		if (ClassUtils.isAssignable(entity.getInfo().getType(), DocumentoPagamento.class)) {
			//org.apache.commons.lang.ClassUtils.is
			this.documento = entity;

			result = true;
		} else
			if (entity.getInfo().getType() == Lancamento.class) {
				try{
					this.documento = entity.getProperty(Lancamento.DOCUMENTO_PAGAMENTO).getValue().getAsEntity();

					result = true;
				} catch (BusinessException e) {
					this.getMessageList().addAll(e.getErrorList());
				}
			} else 
				if (entity.getInfo().getType() == LancamentoMovimento.class) {
					try{
						this.documento = entity.getProperty(LancamentoMovimento.DOCUMENTO_PAGAMENTO).getValue().getAsEntity();

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
