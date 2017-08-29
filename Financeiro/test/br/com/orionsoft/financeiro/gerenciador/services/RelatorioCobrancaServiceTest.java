package br.com.orionsoft.financeiro.gerenciador.services;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import javax.swing.text.MaskFormatter;

import net.sf.jasperreports.engine.JRException;

import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCobrancaService.QueryRelatorioCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

public class RelatorioCobrancaServiceTest extends ServiceBasicTest {

	@Test
	public void test() throws ParseException {
		MaskFormatter mf = new MaskFormatter("(**)****-****");
		mf.setValueContainsLiteralCharacters(false);
		mf.setAllowsInvalid(true);
		System.out.println(mf.valueToString("4432638001"));
	}
	
//	@Test
	public void testExecute() throws JRException, FileNotFoundException, BusinessException {
		File file = new File("/home/antonio/relatorio_cobranca.pdf");
		OutputStream stream = new FileOutputStream(file);
		Calendar dataLancamentoInicial = CalendarUtils.getCalendar(2000, Calendar.JANUARY, 1);
		Calendar dataLancamentoFinal = CalendarUtils.getCalendar(2010, Calendar.OCTOBER, 31);
//		Calendar dataVencimentoInicial = CalendarUtils.getCalendar(2009, Calendar.FEBRUARY, 1);
//		Calendar dataVencimentoFinal = CalendarUtils.getCalendar(2009, Calendar.APRIL, 30);
		Calendar dataPagamento = CalendarUtils.getCalendar(2010, Calendar.NOVEMBER, 30);
		String itemCusto = "";	
//		String cnpj = "89848543026990";
//		String cnpj = "03356567000193";
//		String cnpj = "78924693000104";
		String cnpj = "07943424000139"; // Alarmes e Serviços Astorga Ltda.
		String cnae = "comercio";
		
		ServiceData sd = new ServiceData(RelatorioCobrancaService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_RELATORIO_COBRANCA_MODELO, RelatorioCobrancaService.RelatorioCobrancaModelo.RETRATO);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_CATEGORIA_CONTRATO_ID, IDAO.ENTITY_UNSAVED);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_CPF_CNPJ_OPT, cnpj);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_ITEM_CUSTO_ID_LIST, itemCusto);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_ITEM_CUSTO_NOT, false);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_VENCIMENTO_INICIAL, dataLancamentoInicial);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_VENCIMENTO_FINAL, dataLancamentoFinal);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_LANCAMENTO_INICIAL, dataLancamentoInicial);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_LANCAMENTO_FINAL, dataLancamentoFinal);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_DATA_PAGAMENTO_OPT, dataPagamento);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_QUANTIDADE_ITENS_INICIAL, null);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_QUANTIDADE_ITENS_FINAL, null);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_OMITIR_VALORES, false);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_TIPO_CONTRATO, RelatorioCobrancaService.TIPO_CONTRATO_TODOS);
//		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_ESCRITORIO_CONTABIL_ID_LIST, escritorio);
//		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_MUNICIPIO_ID_OPT, 1l);
//		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_NOT_MUNICIPIO_OPT, true);
//		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_CNAE_DESCRICAO_OPT, cnae);
		sd.getArgumentList().setProperty(RelatorioCobrancaService.IN_OUTPUT_STREAM, stream);
		this.serviceManager.execute(sd);
		
        List<QueryRelatorioCobranca> list = sd.getFirstOutput();
        assertFalse(list.isEmpty());
	}

}
