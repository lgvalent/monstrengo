package br.com.orionsoft.financeiro.gerenciador.services;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import javax.swing.text.MaskFormatter;

import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.services.RelatorioRecebimentoService.QueryRelatorioRecebimento;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

public class RelatorioRecebimentoServiceTest extends ServiceBasicTest {

	public void test() throws ParseException {
		MaskFormatter mf = new MaskFormatter("(##)####-####");
		mf.setValueContainsLiteralCharacters(false);
		mf.setAllowsInvalid(true);
		System.out.println(mf.valueToString("4432638001"));
	}
	
	@Test
	public void testExecute() throws Exception {
		File file = new File("/home/antonio/relatorio_recebimento.pdf");
		OutputStream stream = new FileOutputStream(file);
		
//		IEntity municipio = UtilsCrud.retrieve(this.serviceManager, Municipio.class, 1l, null);
//		IEntity representante = UtilsCrud.retrieve(this.serviceManager, Representante.class, 3l, null);
//		IEntity conta = UtilsCrud.retrieve(this.serviceManager, Conta.class, 4l, null);
		Calendar dataLancamentoInicial = CalendarUtils.getCalendar(2009, Calendar.APRIL, 1);
		Calendar dataLancamentoFinal = CalendarUtils.getCalendar(2009, Calendar.APRIL, 30);
		String cpfCnpj = "";
		String itemCusto = "3";
		
		ServiceData sd = new ServiceData(RelatorioRecebimentoService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_CATEGORIA_CONTRATO_ID, IDAO.ENTITY_UNSAVED);
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_CPF_CNPJ, cpfCnpj);
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_DATA_VENCIMENTO_INICIAL, dataLancamentoInicial);
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_DATA_VENCIMENTO_FINAL, dataLancamentoFinal);
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_DATA_RECEBIMENTO_INICIAL, dataLancamentoInicial);
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_DATA_RECEBIMENTO_FINAL, dataLancamentoFinal);
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_ITEM_CUSTO_ID_LIST, itemCusto);
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_ITEM_CUSTO_NOT, true);
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_QUANTIDADE_ITENS_INICIAL, null);
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_QUANTIDADE_ITENS_FINAL, null);
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_TIPO_CONTRATO, RelatorioRecebimentoService.TipoContrato.TODOS.ordinal());
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_MUNICIPIO_ID_OPT, 1l);
//		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_CONTA_ID_OPT, conta.getId());
//		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_CONTRATO_REPRESENTANTE_ID_OPT, representante.getId());
		sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_OUTPUT_STREAM, stream);
		this.serviceManager.execute(sd);
		
        List<QueryRelatorioRecebimento> list = sd.getFirstOutput();
        assertFalse(list.isEmpty());
	}

}
