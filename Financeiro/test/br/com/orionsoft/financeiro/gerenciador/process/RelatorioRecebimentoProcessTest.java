package br.com.orionsoft.financeiro.gerenciador.process;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;

public class RelatorioRecebimentoProcessTest extends ProcessBasicTest {
	private RelatorioRecebimentoProcess process = null;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		process = (RelatorioRecebimentoProcess) processManager.createProcessByName(RelatorioRecebimentoProcess.PROCESS_NAME, this.getAdminSession());
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		process = null;
	}

	@Test
	public void testExecute() throws FileNotFoundException {
		OutputStream stream = new FileOutputStream("/home/antonio/relatorio_recebimento.pdf");
		process.setCpfCnpj("04175308000129");
		process.setOutputStream(stream);
		process.execute();
		assertTrue(true);
	}

}
