package br.com.orionsoft.financeiro.gerenciador.services;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.services.ImprimirCartaCobrancaService.CartaCobrancaModelo;
import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCobrancaService.QueryRelatorioCobranca;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

public class ImprimirCartaCobrancaServiceTest extends ServiceBasicTest {

	@Test
	public void testExecute() throws FileNotFoundException, ServiceException {
		Calendar dataLancamentoInicial = CalendarUtils.getCalendar(2008, Calendar.JANUARY, 1);
		Calendar dataLancamentoFinal = CalendarUtils.getCalendar(2008, Calendar.NOVEMBER, 30);
		Calendar dataVencimentoInicial = CalendarUtils.getCalendar(2008, Calendar.JANUARY, 1);
		Calendar dataVencimentoFinal = CalendarUtils.getCalendar(2008, Calendar.NOVEMBER, 30);
		
		ServiceData sd = new ServiceData(RelatorioCobrancaService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_CPF_CNPJ_OPT, null);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_CATEGORIA_CONTRATO_ID, IDAO.ENTITY_UNSAVED);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_VENCIMENTO_INICIAL, dataVencimentoInicial);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_VENCIMENTO_FINAL, dataVencimentoFinal);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_LANCAMENTO_INICIAL, dataLancamentoInicial);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_LANCAMENTO_FINAL, dataLancamentoFinal);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_ITEM_CUSTO_ID_LIST, "4");
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_QUANTIDADE_ITENS_INICIAL, null);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_QUANTIDADE_ITENS_FINAL, null);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_TIPO_CONTRATO, RelatorioCobrancaService.TIPO_CONTRATO_ATIVOS);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_MUNICIPIO_ID_OPT, 9l);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_OUTPUT_STREAM, null);
		if (this.serviceManager == null)
			System.out.println("null");
		this.serviceManager.execute(sd);
        List<QueryRelatorioCobranca> list = sd.getFirstOutput();
		
		CartaCobrancaModelo modelo = CartaCobrancaModelo.PADRAO;
		OutputStream out = new FileOutputStream("/home/antonio/carta_cobranca.pdf");
		
		sd = new ServiceData(ImprimirCartaCobrancaService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(ImprimirCartaCobrancaService.IN_MODELO_CARTA_COBRANCA, modelo);
		sd.getArgumentList().setProperty(ImprimirCartaCobrancaService.IN_QUERY_RELATORIO_COBRANCA, list);
		sd.getArgumentList().setProperty(ImprimirCartaCobrancaService.IN_OUTPUT_STREAM, out);
		this.serviceManager.execute(sd);
		
	}

}
