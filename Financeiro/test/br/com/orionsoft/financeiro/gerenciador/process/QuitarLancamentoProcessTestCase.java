package br.com.orionsoft.financeiro.gerenciador.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.financeiro.gerenciador.services.InserirLancamentoServiceTest;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class QuitarLancamentoProcessTestCase extends ProcessBasicTest {
	QuitarLancamentoProcess process;

    public void setUp() throws Exception {
        super.setUp();
        process = (QuitarLancamentoProcess) this.processManager.createProcessByName(QuitarLancamentoProcess.PROCESS_NAME, this.getAdminSession());
    }

   public void tearDown() throws Exception {
        super.tearDown();
        process = null;
    }
   
   @Test
   public void calcularMultaJuros() throws Exception {
	    IEntity<Conta> conta = UtilsCrud.retrieve(this.processManager.getServiceManager(), Conta.class, 1l, null);
	    Calendar dataQuitacao = CalendarUtils.getCalendar(2008, Calendar.FEBRUARY, 15);
		BigDecimal valor = DecimalUtils.TEN;

		InserirLancamentoServiceTest test = new InserirLancamentoServiceTest();
	    test.setUp();
	    test.inserir();
	    test.tearDown();
	    Lancamento lancamento = test.getLancamento();

		/*
		 * Executa o processo.
		 */
    	process.setDataQuitacao(dataQuitacao);
    	process.runWithEntity(this.processManager.getServiceManager().getEntityManager().getEntity(lancamento));
    	process.getContratos().get(0).getMovimentos().getFirst().setPropertyValue(LancamentoMovimento.CONTA, conta);
    	process.getContratos().get(0).getMovimentos().getFirst().setPropertyValue(LancamentoMovimento.VALOR, valor);
    	process.runCalcularMultaJuros();
    	
    	System.out.println("Valor: "+process.getContratos().get(0).getValor());
    	System.out.println("Multa: "+process.getContratos().get(0).getMulta());
    	System.out.println("Juros: "+process.getContratos().get(0).getJuros());
    	System.out.println("Total: "+process.getContratos().get(0).getTotal());
   }

//   @Test 
   public void testExecute() throws Exception {
	    IEntity<Conta> conta = UtilsCrud.retrieve(this.processManager.getServiceManager(), Conta.class, 1l, null);
	    Calendar dataQuitacao = CalendarUtils.getCalendar(2007, Calendar.SEPTEMBER, 18);
//		Contrato contrato = UtilsCrud.objectRetrieve(this.processManager.getServiceManager(), Contrato.class, 1l, null);
		BigDecimal valor = DecimalUtils.TEN;
		Calendar data = CalendarUtils.getCalendar(2007, Calendar.SEPTEMBER, 18);
//		DocumentoPagamentoCategoria documentoPagamentoCategoria = UtilsCrud.objectRetrieve(this.processManager.getServiceManager(), DocumentoPagamentoCategoria.class, 1l, null);

		InserirLancamentoServiceTest test = new InserirLancamentoServiceTest();
	    test.setUp();
	    test.inserir();
	    test.tearDown();
	    Lancamento lancamento = test.getLancamento();

		/*
		 * Executa o processo.
		 */
    	process.setDataQuitacao(dataQuitacao);
    	process.runWithEntity(this.processManager.getServiceManager().getEntityManager().getEntity(lancamento));
    	process.getContratos().get(0).getMovimentos().getFirst().setPropertyValue(LancamentoMovimento.CONTA, conta);
    	process.getContratos().get(0).getMovimentos().getFirst().setPropertyValue(LancamentoMovimento.VALOR, valor);
    	process.runCalcularMultaJuros();
    	
    	assertTrue(process.runQuitar());
    	lancamento = UtilsCrud.objectRetrieve(this.processManager.getServiceManager(), Lancamento.class, lancamento.getId(), null);
    	assertEquals(DecimalUtils.ZERO, lancamento.getSaldo());
    	    	
    	LancamentoMovimento lancamentoMovimento = lancamento.getLancamentoMovimentos().get(0);    	
    	assertEquals(valor, lancamentoMovimento.getValor());
    	assertEquals(data, lancamentoMovimento.getData());
    	assertEquals(LancamentoMovimentoCategoria.QUITADO, lancamentoMovimento.getLancamentoMovimentoCategoria());
    	assertEquals(conta.toString(), lancamentoMovimento.getConta().toString());
    	assertEquals(lancamento.toString(), lancamentoMovimento.getLancamento().toString());
    	
//    	DocumentoPagamento documento = lancamentoMovimento.getDocumentoPagamento();
//    	assertEquals(contrato.toString(), documento.getContrato().toString());
//    	assertEquals(dataQuitacao, documento.getData());
//    	assertEquals(valor, documento.getValor());
    }

}
