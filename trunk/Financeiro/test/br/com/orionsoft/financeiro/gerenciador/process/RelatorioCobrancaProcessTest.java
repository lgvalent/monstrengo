package br.com.orionsoft.financeiro.gerenciador.process;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.services.ImprimirCartaCobrancaService.CartaCobrancaModelo;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

public class RelatorioCobrancaProcessTest extends ProcessBasicTest{
	private RelatorioCobrancaProcess process = null;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		process = (RelatorioCobrancaProcess) processManager.createProcessByName(RelatorioCobrancaProcess.PROCESS_NAME, this.getAdminSession());
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testImprimirCartaCobranca() throws IOException {
		OutputStream outputStream = new FileOutputStream("./carta_cobranca.pdf");
		
		process.setCartaCobrancaModelo(CartaCobrancaModelo.PADRAO.ordinal());
		process.getParamPessoa().setValue((IEntity<Pessoa>) process.getParamPessoa().getList("Alarmes").get(0));
		process.setDataLancamentoInicial(CalendarUtils.getCalendar(2009, Calendar.APRIL, 1));
		process.setDataLancamentoFinal(CalendarUtils.getCalendar(2012, Calendar.APRIL, 30));
		process.setDataVencimentoInicial(CalendarUtils.getCalendar(2009, Calendar.APRIL, 1));
		process.setDataVencimentoFinal(CalendarUtils.getCalendar(2012, Calendar.APRIL, 30));

		IEntity<ItemCusto> itemCusto = (IEntity<ItemCusto>) process.getParamItemCusto().getList("Contribui").get(0);
		process.getParamItemCusto().getValue().add(itemCusto);
		
		process.setOutputStream(outputStream);
		
		UtilsTest.showMessageList(process.getMessageList());
		Assert.assertTrue(process.runImprimirCartaCobranca());

		outputStream.flush();
		outputStream.close();
	}

//	@Test
	public void testExecute() throws IOException {
		OutputStream outputStream = new FileOutputStream("./relatorio_cobranca.pdf");
		process.getParamPessoa().setValue((IEntity<Pessoa>) process.getParamPessoa().getList("Alarmes").get(0));
		process.setDataLancamentoInicial(CalendarUtils.getCalendar(2009, Calendar.APRIL, 1));
		process.setDataLancamentoFinal(CalendarUtils.getCalendar(2009, Calendar.APRIL, 30));
		process.setDataVencimentoInicial(CalendarUtils.getCalendar(2009, Calendar.APRIL, 1));
		process.setDataVencimentoFinal(CalendarUtils.getCalendar(2009, Calendar.APRIL, 30));
		process.setOutputStream(outputStream);
		process.runGerarPdf();
		outputStream.flush();
		outputStream.close();
	}

	public void testGetListContratoCategoria() throws BusinessException {
		List<SelectItem> list = process.getListCategoriaContrato();
		for (SelectItem item : list) {
			System.out.println(item.getValue() + " " + item.getLabel());
		}
	}

	public void _testGetListTipoContrato() {
		List<SelectItem> list = process.getListTipoContrato();
		for (SelectItem item : list) {
			System.out.println(item.getValue() + " " + item.getLabel());
		}
	}

}
