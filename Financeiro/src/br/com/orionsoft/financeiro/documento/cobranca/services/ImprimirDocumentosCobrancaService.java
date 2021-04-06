package br.com.orionsoft.financeiro.documento.cobranca.services;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.internet.MimeBodyPart;

import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaBean;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.core.util.StringUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.crud.support.DocumentParserCrudExpression;
import br.com.orionsoft.monstrengo.mail.services.SendMailService;

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
	public static final String IN_ENVIAR_EMAIL_OPT = "enviarEMail";
	public static final String IN_DOWNLOAD_PDF_ZIP_OPT = "downloadPdfZip";
	public static final String IN_OUTPUT_STREAM_OPT = "outputStream";
	public static final String IN_PRINTER_INDEX_OPT = "printerIndex";
	public static final String IN_INSTRUCOES_ADICIONAIS_OPT = "instrucoesAdicionais"; 
	public static final String IN_INPUT_STREAM_IMAGEM_OPT = "inInputStreamImagem";


	public String getServiceName() {
		return SERVICE_NAME;
	}

	@SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico ImprimirDocumentosCobrancaService");

		try{
			log.debug("Preparando os argumentos");
			Boolean inEnviarEMail = (serviceData.getArgumentList()
					.containsProperty(IN_ENVIAR_EMAIL_OPT) ? (Boolean) serviceData
							.getArgumentList().getProperty(IN_ENVIAR_EMAIL_OPT)
							: false);

			Boolean inDownloadPdfZip = (serviceData.getArgumentList()
					.containsProperty(IN_DOWNLOAD_PDF_ZIP_OPT) ? (Boolean) serviceData
							.getArgumentList().getProperty(IN_DOWNLOAD_PDF_ZIP_OPT)
							: false);

			OutputStream inOutputStream = (serviceData.getArgumentList()
					.containsProperty(IN_OUTPUT_STREAM_OPT) ? (OutputStream) serviceData
							.getArgumentList().getProperty(IN_OUTPUT_STREAM_OPT) : null);

			int inPrinterIndex = (serviceData.getArgumentList()
					.containsProperty(IN_PRINTER_INDEX_OPT) ? (Integer) serviceData
							.getArgumentList().getProperty(IN_PRINTER_INDEX_OPT)
							: PrintUtils.PRINTER_INDEX_NO_PRINT);

			String inInstrucoesAdicionais = (serviceData.getArgumentList()
					.containsProperty(IN_INSTRUCOES_ADICIONAIS_OPT) ? (String) serviceData
							.getArgumentList()
							.getProperty(IN_INSTRUCOES_ADICIONAIS_OPT) : "");

			IEntity<? extends DocumentoCobranca> inDocumento = (serviceData.getArgumentList()
					.containsProperty(IN_DOCUMENTO_OPT) ? (IEntity<? extends DocumentoCobranca>) serviceData
							.getArgumentList().getProperty(IN_DOCUMENTO_OPT) : null);
			List<DocumentoCobrancaBean> inDocumentosBean = (serviceData
					.getArgumentList().containsProperty(IN_DOCUMENTO_BEAN_LIST) ? (List<DocumentoCobrancaBean>) serviceData
							.getArgumentList().getProperty(IN_DOCUMENTO_BEAN_LIST)
							: null);
			InputStream inInputStreamImagem = (serviceData.getArgumentList()
					.containsProperty(IN_INPUT_STREAM_IMAGEM_OPT) ? (InputStream) serviceData
							.getArgumentList().getProperty(IN_INPUT_STREAM_IMAGEM_OPT)
							: null);

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
			if(inDocumento!=null){
				if(inEnviarEMail){
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					gerenciador.imprimirDocumento(inDocumento, outputStream, PrintUtils.PRINTER_INDEX_NO_PRINT, inInstrucoesAdicionais, inInputStreamImagem, serviceData);
					enviarEMail(outputStream, inDocumento, serviceData);
				}else
					gerenciador.imprimirDocumento(inDocumento, inOutputStream, inPrinterIndex, inInstrucoesAdicionais, inInputStreamImagem, serviceData);
			}

			log.debug("Executando o método de impressão coletiva do gerenciador");
			if(inDocumentosBean!=null){
				if(inEnviarEMail){
					for(DocumentoCobrancaBean bean: inDocumentosBean){
						if(bean.isChecked()) {
							List<DocumentoCobrancaBean> docUnitarioBean = new ArrayList<DocumentoCobrancaBean>(1);
							docUnitarioBean.add(bean);
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							gerenciador.imprimirDocumentos(docUnitarioBean, outputStream, PrintUtils.PRINTER_INDEX_NO_PRINT, inInputStreamImagem, serviceData);
							
							try {
								enviarEMail(outputStream, bean.getDocumentoOriginal(), serviceData);								
							} catch (BusinessException e) {
								IEntity<? extends DocumentoCobranca> doc = bean.getDocumentoOriginal();
								this.addInfoMessage(serviceData, "ERRO_ENVIAR_EMAIL", doc.getObject().getContrato(), e.getMessage());
							}
						}
					}
				}else if(inDownloadPdfZip){
					ZipOutputStream zos = new ZipOutputStream(inOutputStream);
					for(DocumentoCobrancaBean bean: inDocumentosBean){
						if(bean.isChecked()) {
							List<DocumentoCobrancaBean> docUnitarioBean = new ArrayList<DocumentoCobrancaBean>(1);
							docUnitarioBean.add(bean);
							String fileName = bean.getPessoa() + "(" + bean.getDocumento() + ") - " + bean.getId() + ".pdf";
							ZipEntry zipEntry = new ZipEntry(fileName);
							zos.putNextEntry(zipEntry);
							gerenciador.imprimirDocumentos(docUnitarioBean, zos, PrintUtils.PRINTER_INDEX_NO_PRINT, inInputStreamImagem, serviceData);
							zos.closeEntry();
						}
					}
					zos.close();
				}else
					gerenciador.imprimirDocumentos(inDocumentosBean, inOutputStream, inPrinterIndex, inInputStreamImagem, serviceData);
			}

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

	private void enviarEMail(ByteArrayOutputStream pdfOutputStream, IEntity<? extends DocumentoCobranca> documento, ServiceData serviceData) throws BusinessException{
		/* TODO Lucio 20181029	Se der erro ao enviar um email, tem que ver como tratar transacionalmente!!! Os emails errados vão retornar como MSG para o e-mail origem */
		if(documento.getObject().getDocumentoCobrancaCategoria().getContaEMail() == null)
			throw new ServiceException(MessageList.create(ImprimirDocumentosCobrancaService.class, "CONTA_EMAIL_NAO_DEFINIDA", documento.getObject().getDocumentoCobrancaCategoria(), documento.getObject().getDocumentoCobrancaCategoria().getId()));

		if(StringUtils.isBlank(documento.getObject().getDocumentoCobrancaCategoria().getMensagemEMail()))
			throw new ServiceException(MessageList.create(ImprimirDocumentosCobrancaService.class, "MENSAGEM_EMAIL_NAO_DEFINIDA", documento.getObject().getDocumentoCobrancaCategoria(), documento.getObject().getDocumentoCobrancaCategoria().getId()));

		String mensagemHtml = "";
		if (!documento.getProperty(DocumentoCobranca.DOCUMENTO_COBRANCA_CATEGORIA).getValue().isValueNull())
			mensagemHtml = DocumentParserCrudExpression.parseString(documento.getObject().getDocumentoCobrancaCategoria().getMensagemEMail(), documento, this.getServiceManager().getEntityManager());

		String email = documento.getObject().getDocumentoCobrancaCategoria().getContaEMail().getSenderMail();
		String assunto = "[Empresa com E-Mail inválido ou inexistente] Documento de Cobrança: " + documento.toString();
		if(StringUtils.isNotBlank(documento.getObject().getContrato().getPessoa().getEmail())){
			String emailTemp = documento.getObject().getContrato().getPessoa().getEmail().split(";")[0];
			if(SendMailService.validateEMail(emailTemp)){
				email = emailTemp;
				assunto = "Documento de Cobrança: " + documento.toString();
			}
		}

		ServiceData service = new ServiceData(SendMailService.SERVICE_NAME, serviceData);
		service.getArgumentList().setProperty(SendMailService.IN_EMAIL_ACCOUNT_OPT, documento.getObject().getDocumentoCobrancaCategoria().getContaEMail());
		service.getArgumentList().setProperty(SendMailService.IN_MESSAGE, mensagemHtml);
		service.getArgumentList().setProperty(SendMailService.IN_RECIPIENT_OPT, email);
		service.getArgumentList().setProperty(SendMailService.IN_SUBJECT, assunto);

		// Test MimeBodyPart attachment
		MimeBodyPart bodyPart = new MimeBodyPart();
		try {
			List<MimeBodyPart> bodyList = new ArrayList<MimeBodyPart>();

			bodyPart.setFileName("Boleto.pdf");
			bodyPart.setDisposition(MimeBodyPart.ATTACHMENT);
			bodyPart.setContent(pdfOutputStream.toByteArray(), "application/pdf");
			bodyList.add(bodyPart);

			service.getArgumentList().setProperty(SendMailService.IN_MIME_BODY_PART_LIST_OPT, bodyList);

			this.getServiceManager().execute(service);
		} catch (Exception e) {
			log.fatal(e.getMessage());

			/* Indica que o serviço falhou por causa de uma exceção do hibernate. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}
}
