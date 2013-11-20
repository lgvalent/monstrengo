package br.com.orionsoft.financeiro.gerenciador.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class VerificarLancamentoQuitadoServiceTest extends ServiceBasicTest {

	@Test
	public void testExecute() throws Exception {
		QuitarLancamentoServiceTest test = new QuitarLancamentoServiceTest();
		test.setUp();
		test.quitar();
		test.tearDown();
		Lancamento lancamento = test.getLancamentoMovimento().getLancamento();
		lancamento = UtilsCrud.objectRetrieve(this.serviceManager, Lancamento.class, lancamento.getId(), null);
		
		ServiceData sd = new ServiceData(VerificarLancamentoQuitadoService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(VerificarLancamentoQuitadoService.IN_LANCAMENTO, lancamento);
		this.serviceManager.execute(sd);
		assertEquals(true, sd.getFirstOutput());
	}

}
