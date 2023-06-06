/*
 * Created on 21/08/2006 by antonio
 */
package br.com.orionsoft.financeiro.gerenciador.services;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeBodyPart;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaBean;
import br.com.orionsoft.financeiro.documento.cobranca.services.ImprimirDocumentosCobrancaService;
import br.com.orionsoft.financeiro.gerenciador.entities.CartaCobrancaBean;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCobrancaService.QueryRelatorioCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.core.util.PrintUtils;
import br.com.orionsoft.monstrengo.core.util.StringUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.crud.support.DocumentParserCrudExpression;
import br.com.orionsoft.monstrengo.mail.entities.EmailAccount;
import br.com.orionsoft.monstrengo.mail.services.SendMailService;

/**
 * Servi�o que imprime cartas de cobran�a.
 * 
 * <p>
 * <b>Argumento:</b><br>
 * 
 * <p>
 * <b>Procedimento:</b>
 * 
 * @author antonio
 * @version 20060821
 * 
 * @spring.bean id="ImprimirCartaCobrancaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ImprimirCartaCobrancaService extends ServiceBasic {
	public enum CartaCobrancaModelo {
		PADRAO("Padr�o", "CartaCobranca.jrxml", ".pdf"),
		PADRAO_SEM_VALOR("Padr�o sem valor", "CartaCobrancaSemValor.jrxml", ".pdf"),
		REVERSAO("Revers�o", "CartaCobrancaReversao.jrxml", ".pdf"), 
		PARCELAS("Parcelas", "CartaCobrancaParcelas.jrxml", ".pdf"),
		EMAIL("E-Mail", "CartaCobrancaEMail.jrxml", ".pdf"),
		CSV_1("Mala direta por item", "", ".csv");

		private String nome;
		private String arquivo;
		private String extensaoSaida;

		private CartaCobrancaModelo(String nome, String arquivo, String extensaoSaida) {
			this.nome = nome;
			this.arquivo = arquivo;
			this.extensaoSaida = extensaoSaida;
		}

		public String getNome() {
			return nome;
		}

		public String getArquivo() {
			return arquivo;
		}

		public String getExtensaoSaida() {
			return extensaoSaida;
		}
	}

	public static final String SERVICE_NAME = "ImprimirCartaCobrancaService";

	public static final String IN_QUERY_RELATORIO_COBRANCA = "queryRelatorioCobranca";
	public static final String IN_MODELO_CARTA_COBRANCA = "modeloCartaCobranca";
	public static final String IN_OUTPUT_STREAM = "outputSteam";

	public static final String IN_ENVIAR_EMAIL_OPT = "enviarEmail";
	public static final String IN_CONTA_EMAIL_OPT = "contaEmail";
	public static final String IN_MENSAGAEM_EMAIL_OPT = "mensagemEmail";

	@SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando a execu��o do servi�o ImprimirCartaCobrancaService");
		try {
			log.debug("Preparando os argumentos");
			/* Obrigat�rios */
			List<QueryRelatorioCobranca> cobrancaList = (List<QueryRelatorioCobranca>) serviceData.getArgumentList().getProperty(IN_QUERY_RELATORIO_COBRANCA);
			CartaCobrancaModelo modelo = (CartaCobrancaModelo) serviceData.getArgumentList().getProperty(IN_MODELO_CARTA_COBRANCA);
			OutputStream inOutputStream = (OutputStream) serviceData.getArgumentList().getProperty(IN_OUTPUT_STREAM);

			Boolean inEnviarEMail = (serviceData.getArgumentList().containsProperty(IN_ENVIAR_EMAIL_OPT) ? (Boolean) serviceData.getArgumentList().getProperty(IN_ENVIAR_EMAIL_OPT): false);
			EmailAccount inEmailAccount = (serviceData.getArgumentList().containsProperty(IN_CONTA_EMAIL_OPT) ? (EmailAccount) serviceData.getArgumentList().getProperty(IN_CONTA_EMAIL_OPT): null);
			String inMensagemEMail = (serviceData.getArgumentList().containsProperty(IN_MENSAGAEM_EMAIL_OPT) ? (String) serviceData.getArgumentList().getProperty(IN_MENSAGAEM_EMAIL_OPT): "");

			if(modelo == CartaCobrancaModelo.CSV_1){
				if(inEnviarEMail)
					throw new ServiceException(MessageList.createSingleInternalError(new Exception("O modelo n�o � compat�vel para ser enviado por E-Mail. Selecione outro modelo!")));

				PrintWriter pw = new PrintWriter(new OutputStreamWriter(inOutputStream, "ISO-8859-1"));
				pw.println("cpf/cnpj;nome;telefone;endere�o;total geral;id do 1� item;descri��o do 1� item;valor do 1� item;...");
				StringBuffer linha = null;

				/* Cria um mapa com os items dispon�veis em TODAS AS EMPRESAS para
				 * conseguir sempre formar um registro padr�o mesmo que a empresa
				 * n�o possua aquele item. No entanto, n�o deve aparecer ZERO, mas sim
				 * VAZIO, pois em uma mala direta, o item nem dever� aparecer */
				HashMap<Long, String> itemsMap = new HashMap<Long, String>();
				for(QueryRelatorioCobranca query: cobrancaList){
					if(query.isChecked() && !itemsMap.containsKey(query.getIdItemCusto())){
						String descricao = ((ItemCusto)UtilsCrud.objectRetrieve(this.getServiceManager(), ItemCusto.class, query.getIdItemCusto(), serviceData.getServiceDataOwner())).getNome();
						itemsMap.put(query.getIdItemCusto(), descricao);
					}
				}

				Iterator<QueryRelatorioCobranca> it = cobrancaList.iterator();
				HashMap<Long, BigDecimal> map = null;
				String documentoCorrente = "";
				do{
					QueryRelatorioCobranca query = it.next();
					if(query.isChecked()) {


						if(!documentoCorrente.equals(query.getDocumento())){
							/* Verifica se houve troca do cara para gravar o atual conteudo do mapa */
							if(!documentoCorrente.equals("")){
								// Escreve os itens do map na linha e preprara outro cara
								// Escreve o total primeiro, pois nem todos caras ter�o todos os itens
								// !!! CODIGO REPETIDO ABAIXO
								BigDecimal total = BigDecimal.ZERO;
								StringBuffer sub = new StringBuffer();
								for(Long id: map.keySet()){
									if(map.get(id)==null){
										sub.append(";");
										sub.append(";");
										sub.append(";");
									}else{
										sub.append(id);
										sub.append(";");
										sub.append(itemsMap.get(id));
										sub.append(";");
										sub.append(DecimalUtils.formatBigDecimal(map.get(id)));
										sub.append(";");
										total = total.add(map.get(id));
									}
								}
								linha.append(DecimalUtils.formatBigDecimal(total));
								linha.append(";");
								linha.append(sub.toString());
								pw.println(linha.toString());
							}

							// Prepara a nova entidade
							linha = new StringBuffer();

							linha.append(query.getDocumento());
							linha.append(";");
							linha.append(query.getNome());
							linha.append(";");
							linha.append(query.getTelefone());
							linha.append(";");
							linha.append(query.getEndereco());
							linha.append(";");

							// Indica quem � o cara corrente
							documentoCorrente = query.getDocumento();
							// Prepara o mapa do cara j� com os itens pr� analisados
							map = new HashMap<Long, BigDecimal>();
							for(Long id: itemsMap.keySet()){
								map.put(id, null);
							}
						}

						// Soma os itens no map
						// Busca no mapa o item atual
						if(map.get(query.getIdItemCusto())!=null){
							// Adiciona ao existente
							map.put(query.getIdItemCusto(), map.get(query.getIdItemCusto()).add(query.getValorCorrigido()));
						}else{
							// Adiciona o primeiro
							map.put(query.getIdItemCusto(), query.getValorCorrigido());
						}

						if(!it.hasNext()){
							// Escreve os itens do map na linha e preprara outro cara
							// Escreve o total primeiro, pois nem todos caras ter�o todos os itens
							// !!! CODIGO REPETIDO ACIMA
							BigDecimal total = BigDecimal.ZERO;
							StringBuffer sub = new StringBuffer();
							for(Long id: map.keySet()){
								if(map.get(id)==null){
									sub.append(";");
									sub.append(";");
									sub.append(";");
								}else{
									sub.append(id);
									sub.append(";");
									sub.append(itemsMap.get(id));
									sub.append(";");
									sub.append(DecimalUtils.formatBigDecimal(map.get(id)));
									sub.append(";");
									total = total.add(map.get(id));
								}
							}
							linha.append(DecimalUtils.formatBigDecimal(total));
							linha.append(";");
							linha.append(sub.toString());
							pw.println(linha.toString());
						}
					}
				}while(it.hasNext());

				/* Descarrega os buffers */
				pw.flush();

			} else {
				Map<String, String> parametros = new HashMap<String, String>();
				parametros.put("data", "Maring�, "+ SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG).format(Calendar.getInstance().getTime()));

				List<CartaCobrancaBean> beans = new ArrayList<CartaCobrancaBean>(cobrancaList.size());
				for (QueryRelatorioCobranca query : cobrancaList) {
					CartaCobrancaBean bean;
					/* Lucio 20121206: beans com valores nulos tamb�m s�o exibidos */
					if (query.isChecked() && ((query.getValorOriginal() == null) || (query.getValorOriginal().signum() == 1))) {
						bean = new CartaCobrancaBean(
								query.getNome(),
								query.getDocumento(),
								"",
								"",
								"",
								query.getEmail(),
								CalendarUtils.formatDate(query.getData()),
								query.getDescricao(),
								CalendarUtils.formatDate(query.getDataVencimento()), 
								DecimalUtils.formatBigDecimal(query.getValorOriginal()));
						beans.add(bean);
					}
				}

				log.debug("Compilando o relat�rio.");
				JasperReport relatorio = JasperCompileManager.compileReport(getClass().getResourceAsStream(modelo.getArquivo()));

				if(inEnviarEMail){
					CartaCobrancaBean lastBean = null; beans.add(new CartaCobrancaBean("", "00000000000", "", "", "", "", "", "", "", "")); // Insere um �ltimo elemento sinalizador de fim de lista que n�o ser� impresso!

					List<CartaCobrancaBean> cartasBeans = new ArrayList<CartaCobrancaBean>();
					for(CartaCobrancaBean bean: beans){
						if(lastBean != null && !bean.getDocumento().equals(lastBean.getDocumento())){
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							log.debug("Imprimindo o relat�rio.");
							JasperPrint print = JasperFillManager.fillReport(relatorio, parametros, new JRBeanCollectionDataSource(cartasBeans));
							JasperExportManager.exportReportToPdfStream(print, outputStream);

							log.debug("Enviando E-Mail com o PDF do relat�rio.");
							enviarEMail(outputStream, lastBean, inEmailAccount, inMensagemEMail, serviceData);

							cartasBeans.clear();
						}
						cartasBeans.add(bean);
						lastBean = bean;
					}
				}else{
					log.debug("Executando o m�todo de impress�o coletiva do gerenciador");
					log.debug("Imprimindo o relat�rio.");
					JasperPrint print = JasperFillManager.fillReport(relatorio, parametros, new JRBeanCollectionDataSource(beans));

					log.debug("Gerando o PDF do relat�rio.");
					JasperExportManager.exportReportToPdfStream(print, inOutputStream);
				}
			}


		} catch (Exception e) {
			log.fatal(e.getMessage());
			/*
			 * Indica que o servi�o falhou por causa de uma exce��o do
			 * hibernate.
			 */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}

	private void enviarEMail(ByteArrayOutputStream pdfOutputStream, CartaCobrancaBean bean, EmailAccount emailAccount, String mensagemEmail, ServiceData serviceData) throws BusinessException{
		/* TODO Lucio 20181029	Se der erro ao enviar um email, tem que ver como tratar transacionalmente!!! Os emails errados v�o retornar como MSG para o e-mail origem */
		if(emailAccount == null)
			throw new ServiceException(MessageList.create(ImprimirCartaCobrancaService.class, "CONTA_EMAIL_NAO_DEFINIDA"));

		if(StringUtils.isBlank(mensagemEmail))
			throw new ServiceException(MessageList.create(ImprimirDocumentosCobrancaService.class, "MENSAGEM_EMAIL_NAO_DEFINIDA"));

		String email = emailAccount.getSenderMail();
		String assunto = "[Empresa com E-Mail inv�lido ou inexistente] Cobran�a: " + bean.toString();
		if(StringUtils.isNotBlank(bean.getEmail())){
			String emailTemp = bean.getEmail().split(";")[0];
			if(SendMailService.validateEMail(emailTemp)){
				email = emailTemp;
				assunto = "Rela��o de t�tulos vencidos para " + bean.toString();
			}
		}

		ServiceData service = new ServiceData(SendMailService.SERVICE_NAME, serviceData);
		service.getArgumentList().setProperty(SendMailService.IN_EMAIL_ACCOUNT_OPT, emailAccount);
		service.getArgumentList().setProperty(SendMailService.IN_MESSAGE, mensagemEmail);
		service.getArgumentList().setProperty(SendMailService.IN_RECIPIENT_OPT, email);
		service.getArgumentList().setProperty(SendMailService.IN_SUBJECT, assunto);

		// Test MimeBodyPart attachment
		MimeBodyPart bodyPart = new MimeBodyPart();
		try {
			List<MimeBodyPart> bodyList = new ArrayList<MimeBodyPart>();

			bodyPart.setFileName("Carta de cobran�a.pdf");
			bodyPart.setDisposition(MimeBodyPart.ATTACHMENT);
			bodyPart.setContent(pdfOutputStream.toByteArray(), "application/pdf");
			bodyList.add(bodyPart);

			service.getArgumentList().setProperty(SendMailService.IN_MIME_BODY_PART_LIST_OPT, bodyList);

			this.getServiceManager().execute(service);
		} catch (Exception e) {
			log.fatal(e.getMessage());

			/* Indica que o servi�o falhou por causa de uma exce��o do hibernate. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}
	public String getServiceName() {
		return SERVICE_NAME;
	}

}
