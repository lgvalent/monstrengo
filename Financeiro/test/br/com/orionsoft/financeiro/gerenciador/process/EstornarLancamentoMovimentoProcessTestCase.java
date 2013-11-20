package br.com.orionsoft.financeiro.gerenciador.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.services.CalcularSaldoAbertoLancamentoService;
import br.com.orionsoft.financeiro.gerenciador.services.QuitarLancamentoServiceTest;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class EstornarLancamentoMovimentoProcessTestCase extends ProcessBasicTest {
	EstornarLancamentoMovimentoProcess process;

    public void setUp() throws Exception {
        super.setUp();
        process = (EstornarLancamentoMovimentoProcess) this.processManager.createProcessByName(EstornarLancamentoMovimentoProcess.PROCESS_NAME, this.getAdminSession());
    }

   public void tearDown() throws Exception {
        super.tearDown();
        process = null;
    }

   @Test 
   public void testExecute() throws Exception {
	   BigDecimal valor = DecimalUtils.getBigDecimal(30.00);
//	   Calendar data = CalendarUtils.getCalendar(2007, Calendar.JULY, 30);
//	   IEntity conta = UtilsCrud.retrieve(this.processManager.getServiceManager(), Conta.class, 1l, null);

	   QuitarLancamentoServiceTest test = new QuitarLancamentoServiceTest();
	   test.setUp();
	   test.quitar();
	   test.tearDown();
	   LancamentoMovimento lancamentoMovimento = test.getLancamentoMovimento();
	   
	   /*
	    * Calcula o saldo do lançamento quitado.
	    */
	   Lancamento lancamento = UtilsCrud.objectRetrieve(this.processManager.getServiceManager(), Lancamento.class, lancamentoMovimento.getLancamento().getId(), null);
	   ServiceData sd = new ServiceData(CalcularSaldoAbertoLancamentoService.SERVICE_NAME, null);
	   sd.getArgumentList().setProperty(CalcularSaldoAbertoLancamentoService.IN_LANCAMENTO, lancamento);
	   this.processManager.getServiceManager().execute(sd);
	   assertEquals(DecimalUtils.ZERO, sd.getFirstOutput());
	   
	   process.setLancamentoMovimento(this.processManager.getServiceManager().getEntityManager().<LancamentoMovimento>getEntity(lancamentoMovimento));
	   assertTrue(process.runEstornar());

	   /*
	    * Calcula o saldo do lançamento estornado.
	    */
	   lancamento = UtilsCrud.objectRetrieve(this.processManager.getServiceManager(), Lancamento.class, lancamentoMovimento.getLancamento().getId(), null);
	   sd = new ServiceData(CalcularSaldoAbertoLancamentoService.SERVICE_NAME, null);
	   sd.getArgumentList().setProperty(CalcularSaldoAbertoLancamentoService.IN_LANCAMENTO, lancamento);
	   this.processManager.getServiceManager().execute(sd);
	   assertEquals(valor, sd.getFirstOutput());
	   
//	   lancamentoMovimento = UtilsCrud.objectRetrieve(this.processManager.getServiceManager(), LancamentoMovimento.class, process.getLancamentoMovimento().getId(), null);

	   assertEquals(valor, lancamentoMovimento.getLancamento().getSaldo());
//	   assertEquals(valor, lancamentoMovimento.getValor());
//	   assertEquals(data, lancamentoMovimento.getData());
//	   assertEquals(LancamentoMovimentoCategoria.QUITADO_ESTORNADO, lancamentoMovimento.getLancamentoMovimentoCategoria());
//	   assertEquals(conta.toString(), lancamentoMovimento.getConta().toString());
   }
}
