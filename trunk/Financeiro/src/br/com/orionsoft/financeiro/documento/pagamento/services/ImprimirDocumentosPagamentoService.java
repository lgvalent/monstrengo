package br.com.orionsoft.financeiro.documento.pagamento.services;


import java.io.OutputStream;
import java.util.List;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoBean;
import br.com.orionsoft.financeiro.documento.pagamento.IGerenciadorDocumentoPagamento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este serviço imprime um Documento solicitando o gerenciador responsável 
 * pelo documento ao ProvedorDocumentoPagamento e solicitando a impressão para 
 * o gerenciador.
 * 
 * <p>Argumentos:</p>
 * IN_DOCUMENTO_OPT: Uma instância IEntity de um documento<br>
 * DOCUMENTO_BEAN_LIST_OPT: Uma lista de DocumentoPagamentoBean<br>
 * IN_OUTPUT_STREAM_OPT: Define um stream para gerar PDF, se estiver null será utilizado o printerIndex<br>
 * PRINTER_INDEX_OPT: O índice da impressora que será utilizada para a impressão do documento<br>
 * IN_INSTRUCOES_ADICIONAIS_OPT: Se um documento IEntity for fornecido o gerenciador de documento, poderá utilizar 
 * estas instruções adicionais para gerar o DocumentoPagamentoBean e consequentemente imprimir estas instruções no documento. 
 * 
 * @version 20060808
 * 
 * @spring.bean id="ImprimirDocumentosPagamentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoPagamento" ref="ProvedorDocumentoPagamento"
 * @spring.property name="transactional" value="true"
 */
public class ImprimirDocumentosPagamentoService extends DocumentoPagamentoServiceBasic {
	
	public static final String SERVICE_NAME = "ImprimirDocumentosPagamentoService";

	public static final String IN_DOCUMENTO_OPT = "documento";
	public static final String IN_DOCUMENTO_BEAN_LIST = "documentoBeanList"; 
	public static final String IN_OUTPUT_STREAM_OPT = "outputStream";
	public static final String IN_PRINTER_INDEX_OPT = "printerIndex";
	public static final String IN_INSTRUCOES_ADICIONAIS_OPT = "instrucoesAdicionais"; 
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	@SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico ImprimirDocumentosPagamentoService");
		
		try{
			log.debug("Preparando os argumentos");
			OutputStream inOutputStream = (serviceData.getArgumentList().containsProperty(IN_OUTPUT_STREAM_OPT)?
					(OutputStream) serviceData.getArgumentList().getProperty(IN_OUTPUT_STREAM_OPT):null);
			int inPrinterIndex = (serviceData.getArgumentList().containsProperty(IN_PRINTER_INDEX_OPT)?
					(Integer) serviceData.getArgumentList().getProperty(IN_PRINTER_INDEX_OPT):PrintUtils.PRINTER_INDEX_NO_PRINT);

			String inInstrucoesAdicionais = (serviceData.getArgumentList().containsProperty(IN_INSTRUCOES_ADICIONAIS_OPT) ?
					(String) serviceData.getArgumentList().getProperty(IN_INSTRUCOES_ADICIONAIS_OPT) : null);
			IEntity<? extends DocumentoPagamento> inDocumento = (serviceData.getArgumentList().containsProperty(IN_DOCUMENTO_OPT) ?
				(IEntity<? extends DocumentoPagamento>) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_OPT) : null);
			List<DocumentoPagamentoBean> inDocumentosBean = (serviceData.getArgumentList().containsProperty(IN_DOCUMENTO_BEAN_LIST) ?
				(List<DocumentoPagamentoBean>) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_BEAN_LIST) : null);

			log.debug("Obtendo junto ao ProvedorDocumentoPagamento o gerenciador responsável");
			IGerenciadorDocumentoPagamento gerenciador=null;
			if(inDocumento!= null)
				gerenciador = this.retrieveGerenciadorPorDocumento((DocumentoPagamento) inDocumento.getObject());
			else if((inDocumentosBean!=null)&&(!inDocumentosBean.isEmpty())){
				DocumentoPagamentoBean bean = inDocumentosBean.get(0); 
				IEntity<? extends DocumentoPagamento> documentoTemp = bean.isTemDocumentoOriginal()?bean.getDocumentoOriginal():UtilsCrud.retrieve(this.getServiceManager(), DocumentoPagamento.class, inDocumentosBean.get(0).getId(), serviceData);
				gerenciador = this.retrieveGerenciadorPorDocumento((DocumentoPagamento) documentoTemp.getObject());
			}else{
				log.debug("Não foi passado um documento ou a lista está vazia");
	            throw new ServiceException(MessageList.create(ImprimirDocumentosPagamentoService.class, "ERRO_NENHUM_DOCUMENTO"));
			}
			
			log.debug("Executando o método de impressão avulsa do gerenciador");
			if(inDocumento!=null)
				gerenciador.imprimirDocumento(inDocumento, inOutputStream, inPrinterIndex, inInstrucoesAdicionais, serviceData);

			log.debug("Executando o método de impressão coletiva do gerenciador");
			if(inDocumentosBean!=null)
				gerenciador.imprimirDocumentos(inDocumentosBean, inOutputStream, inPrinterIndex, serviceData);

			log.debug("Adicionando a mensagem de sucesso");
			this.addInfoMessage(serviceData, "IMPRESSAO_SUCESSO");
			
		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/* O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros. */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			
			/* Indica que o serviço falhou por causa de uma exceção do hibernate. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
		
	}
}
