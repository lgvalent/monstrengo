package br.com.orionsoft.financeiro.gerenciador.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

public class EstornarLancamentoMovimentoServiceTest extends ServiceBasicTest {

	@Test
	public void testExecute() throws Exception{
		QuitarLancamentoServiceTest test = new QuitarLancamentoServiceTest();
		test.setUp();
		test.quitar();
		test.tearDown();
		LancamentoMovimento lancamentoMovimento = test.getLancamentoMovimento();

		ServiceData sd = new ServiceData(EstornarLancamentoMovimentoService.SERVICE_NAME,null);
		sd.getArgumentList().setProperty(EstornarLancamentoMovimentoService.IN_LANCAMENTO_MOVIMENTO, lancamentoMovimento);
		sd.getArgumentList().setProperty(EstornarLancamentoMovimentoService.IN_DATA, CalendarUtils.getCalendar());
		sd.getArgumentList().setProperty(EstornarLancamentoMovimentoService.IN_DESCRICAO, "Teste de estorno.");
		this.serviceManager.execute(sd);
		
		LancamentoMovimento lancamentoMovimentoEstornado = sd.getFirstOutput();
		
		Assert.assertEquals(LancamentoMovimentoCategoria.QUITADO_ESTORNADO, lancamentoMovimentoEstornado.getLancamentoMovimentoCategoria());
	}

}
