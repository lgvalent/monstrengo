package br.com.orionsoft.financeiro.documento.cobranca.services;


import java.io.OutputStream;
import java.util.List;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaBean;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este serviço imprime um Documento solicitando o gerenciador responsável 
 * pelo documento ao ProvedorDocumentoCobranca e solicitando a impressão para 
 * o gerenciador.
 * 
 * <p>Argumentos:</p>
 * IN_DOCUMENTO_OPT: Uma instância IEntity de um documento<br>
 * DOCUMENTO_BEAN_LIST_OPT: Uma lista de DocumentoCobrancaBean<br>
 * IN_OUTPUT_STREAM_OPT: Define um stream para gerar PDF, se estiver null será utilizado o printerIndex<br>
 * PRINTER_INDEX_OPT: O índice da impressora que será utilizada para a impressão do documento<br>
 * IN_INSTRUCOES_ADICIONAIS_OPT: Se um documento IEntity for fornecido o gerenciador de documento, poderá utilizar 
 * estas instruções adicionais para gerar o DocumentoCobrancaBean e consequentemente imprimir estas instruções no documento. 
 * 
 * @version 20060808
 * 
 * @spring.bean id="ImprimirDocumentosCobrancaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
 * @spring.property name="transactional" value="true"
 */
public class ImprimirDocumentosCobrancaService extends DocumentoCobrancaServiceBasic {
	
	public static final String SERVICE_NAME = "ImprimirDocumentosCobrancaService";

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
		log.debug("::Iniciando execução do servico ImprimirDocumentosCobrancaService");
		
		try{
			log.debug("Preparando os argumentos");
			OutputStream inOutputStream = (serviceData.getArgumentList().containsProperty(IN_OUTPUT_STREAM_OPT)?
					                      (OutputStream) serviceData.getArgumentList().getProperty(IN_OUTPUT_STREAM_OPT):null);
			int inPrinterIndex = (serviceData.getArgumentList().containsProperty(IN_PRINTER_INDEX_OPT)?
                    			 (Integer) serviceData.getArgumentList().getProperty(IN_PRINTER_INDEX_OPT):PrintUtils.PRINTER_INDEX_NO_PRINT);

			String inInstrucoesAdicionais = (serviceData.getArgumentList().containsProperty(IN_INSTRUCOES_ADICIONAIS_OPT) ?
					(String) serviceData.getArgumentList().getProperty(IN_INSTRUCOES_ADICIONAIS_OPT) : "");
			IEntity inDocumento = (serviceData.getArgumentList().containsProperty(IN_DOCUMENTO_OPT) ?
				(IEntity) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_OPT) : null);
			List<DocumentoCobrancaBean> inDocumentosBean = (serviceData.getArgumentList().containsProperty(IN_DOCUMENTO_BEAN_LIST) ?
				(List<DocumentoCobrancaBean>) serviceData.getArgumentList().getProperty(IN_DOCUMENTO_BEAN_LIST) : null);
			
			/* Lucio 20110517 Identifica se tem documentos de diferentes tipos para lançar uma exceção */
			if(inDocumentosBean != null){
				String gerenciadorAnterior = null;
				for(DocumentoCobrancaBean bean: inDocumentosBean){
					if(bean.isChecked()){
						IEntity<? extends DocumentoCobranca> documentoTemp = bean.isTemDocumentoOriginal()?bean.getDocumentoOriginal():UtilsCrud.retrieve(this.getServiceManager(), DocumentoCobranca.class, bean.getId(), serviceData);
						String gerenciadorAtual =  documentoTemp.getObject().getConvenioCobranca().getNomeGerenciadorDocumento();
						
						if((gerenciadorAnterior != null) && !(gerenciadorAnterior.equals(gerenciadorAtual))){
							log.debug("Documentos de diferentes gerenciadores foram selecionados ");
				            throw new ServiceException(MessageList.create(ImprimirDocumentosCobrancaService.class, "ERRO_DIFERENTES_GERENCIADORES"));
							
						}else{
							gerenciadorAnterior = gerenciadorAtual;
						}
					}
				}
			}
			

			log.debug("Obtendo junto ao ProvedorDocumentoCobranca o gerenciador responsável");
			/* TODO Se um elemento seleciona nao for do mesmo tipo do primeiro elemento da lista, dará um erro
			 * pois o gerenciador nao é o mesmo. E também, o primeiro elemento pode não estar selecionado, mesmo assim o 
			 * codigo abaixo tenta pegar o primeiro selecionado, senão o segundo, senão o terceiro 
			 * Lucio 20110517 Seria interessante agrupar os documentos dos mesmos gerenciadores e gerar um único PDF
			 * No entanto, se passar o inOutputStrem para vários gerenciadores, os dados são gravados, mas o 
			 * Reader só lerá o início do arquivo. Ou seja, os bytes dos demais documentos estão no arquivo, mas não são lidos 
			 * Teria que fazer um PDFJoin, mas não sei como fazer. Outra coisa, se uma impressora estiver selecionada,
			 * não há este problema de PDF, aí poderia enviar para cada gerenciador.
			 * Ahhh, no entanto, a ordem de impressão ficaria diferente da ordem da lista, e se etiquetas forem
			 * geradas, a ordem não vai bater */
			IGerenciadorDocumentoCobranca gerenciador=null;
			if(inDocumento!= null)
				gerenciador = this.retrieveGerenciadorPorDocumento((DocumentoCobranca) inDocumento.getObject());
			else if((inDocumentosBean!=null)&&(!inDocumentosBean.isEmpty())){
				DocumentoCobrancaBean bean = inDocumentosBean.get(0).isChecked()?inDocumentosBean.get(0):inDocumentosBean.get(1).isChecked()?inDocumentosBean.get(1):inDocumentosBean.get(2); 
				IEntity documentoTemp = bean.isTemDocumentoOriginal()?bean.getDocumentoOriginal():UtilsCrud.retrieve(this.getServiceManager(), DocumentoCobranca.class, bean.getId(), serviceData);
				gerenciador = this.retrieveGerenciadorPorDocumento((DocumentoCobranca) documentoTemp.getObject());
			}else{
				log.debug("Não foi passado um documento ou a lista está vazia");
	            throw new ServiceException(MessageList.create(ImprimirDocumentosCobrancaService.class, "ERRO_NENHUM_DOCUMENTO"));
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
