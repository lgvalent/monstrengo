package br.com.orionsoft.financeiro.gerenciador.process;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.financeiro.gerenciador.services.InserirLancamentoServiceTest;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class CancelarLancamentoProcessTestCase extends ProcessBasicTest{
	CancelarLancamentoProcess process;

    public void setUp() throws Exception {
        super.setUp();
        process = (CancelarLancamentoProcess) this.processManager.createProcessByName(CancelarLancamentoProcess.PROCESS_NAME, this.getAdminSession());
    }

   public void tearDown() throws Exception {
        super.tearDown();
        process = null;
    }

   @Test 
   public void testExecute() throws Exception {
	   Calendar dataCancelamento = CalendarUtils.getCalendar(2007, Calendar.JULY, 05);
	   String descricao = "CancelarLancamentoMovimentoServiceTest";
	   BigDecimal valor = DecimalUtils.getBigDecimal(30.00);
	   Calendar data = CalendarUtils.getCalendar(2007, Calendar.JULY, 05);
	   
	   InserirLancamentoServiceTest inserirLancamentoServiceTest = new InserirLancamentoServiceTest();
	   inserirLancamentoServiceTest.setUp();
	   inserirLancamentoServiceTest.inserir();
	   inserirLancamentoServiceTest.tearDown();
	   Lancamento lancamento = inserirLancamentoServiceTest.getLancamento();
	   
	   process.setData(dataCancelamento);
	   process.setDescricao(descricao);
	   process.setLancamento(this.processManager.getServiceManager().getEntityManager().<Lancamento>getEntity(lancamento));
	   Assert.assertTrue(process.runCancelar());
	   
	   lancamento = UtilsCrud.objectRetrieve(this.processManager.getServiceManager(), Lancamento.class, lancamento.getId(), null);
	   Assert.assertEquals(DecimalUtils.getBigDecimal(0.0), lancamento.getSaldo());
	   
	   LancamentoMovimento lancamentoMovimento = lancamento.getLancamentoMovimentos().get(0);
	   Assert.assertEquals(lancamentoMovimento.getValor(), valor);
	   Assert.assertEquals(lancamentoMovimento.getData(), data);
	   Assert.assertEquals(lancamentoMovimento.getLancamentoMovimentoCategoria(), LancamentoMovimentoCategoria.CANCELADO);
	   Assert.assertEquals(lancamentoMovimento.getLancamento().toString(), lancamento.toString());
   }
}
