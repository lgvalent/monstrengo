package br.com.orionsoft.financeiro.gerenciador.services;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCobrancaService.QueryRelatorioCobranca;
import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCobrancaService.RelatorioCobrancaModelo;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Serviço que lista informações financeiras para o Relatório de Cobrança.
 * 
 * <p>
 * <b>Argumento:</b><br>
 * 
 * <p>
 * <b>Procedimento:</b>
 * 
 * @author Antonio Alves
 * @version 20070430
 * 
 * @spring.bean id="RelatorioCobrancaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ImprimirRelatorioCobrancaService extends ServiceBasic {
	public static final String SERVICE_NAME = "ImprimirRelatorioCobrancaService";

	public static final String IN_QUERY_RELATORIO_COBRANCA = "queryRelatorioCobranca";
	public static final String IN_OUTPUT_STREAM = "outputSteam";
	public static final String IN_RELATORIO_COBRANCA_MODELO = "relatorioCobrancaModelo";
	public static final String IN_DATA_PAGAMENTO_OPT = "dataPagamento";

	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("Iniciando a execução do serviço RelatorioCobrancaService");
		log.debug("Preparando os argumentos.");

		/*
		 * Parâmetros obrigatórios
		 */
		List<QueryRelatorioCobranca> list = (List<QueryRelatorioCobranca>) serviceData.getArgumentList().getProperty(IN_QUERY_RELATORIO_COBRANCA);
		OutputStream inOutputStream = (OutputStream) serviceData.getArgumentList().getProperty(IN_OUTPUT_STREAM);
		RelatorioCobrancaModelo inRelatorioCobrancaModelo = (RelatorioCobrancaModelo) serviceData.getArgumentList().getProperty(IN_RELATORIO_COBRANCA_MODELO);
		Calendar inDataPagamento = (serviceData.getArgumentList().containsProperty(IN_DATA_PAGAMENTO_OPT) ?
				(Calendar) serviceData.getArgumentList().getProperty(IN_DATA_PAGAMENTO_OPT) : CalendarUtils.getCalendar());

		List<QueryRelatorioCobranca> listChecked = new ArrayList<QueryRelatorioCobranca>(list.size());
		for(QueryRelatorioCobranca bean: list)
			if(bean.isChecked())
				listChecked.add(bean);
		
		try{

			log.debug("Compilando o relatório.");
			Map<String, String> parametros = new HashMap<String, String>();
			parametros.put("Memo1", "Impresso em " + CalendarUtils.formatDateTime(Calendar.getInstance()));
			parametros.put("Memo2", "Valores calculados para pagamento até " + CalendarUtils.formatDate(inDataPagamento));
			parametros.put("Memo3", "");

			JasperReport relatorio = null;
			if(inRelatorioCobrancaModelo == RelatorioCobrancaModelo.RETRATO)
				relatorio = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioCobrancaRetrato.jrxml"));
			else
				relatorio = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioCobrancaPaisagem.jrxml"));

			if (inOutputStream != null) {
				log.debug("Imprimindo o relatório.");
				JasperPrint print = JasperFillManager.fillReport(relatorio, parametros, new JRBeanCollectionDataSource(listChecked));
				JasperExportManager.exportReportToPdfStream(print, inOutputStream);
			}

		} catch (JRException e) {
			/* Indica que o serviço falhou por causa de uma exceção do Jasper. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

}
