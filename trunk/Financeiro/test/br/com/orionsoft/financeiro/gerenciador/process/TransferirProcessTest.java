package br.com.orionsoft.financeiro.gerenciador.process;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class TransferirProcessTest extends ProcessBasicTest {
	private TransferirProcess process;
	
	@Before
	public void setUp() throws Exception ,BusinessException {
		super.setUp();
		process = (TransferirProcess) this.processManager.createProcessByName(TransferirProcess.PROCESS_NAME, this.getAdminSession());
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		process = null;
	}

	@Test
	public void testTransferir() throws BusinessException {
		IEntity contaOrigem = UtilsCrud.retrieve(this.processManager.getServiceManager(), Conta.class, 1l, null);
		IEntity contaDestino = UtilsCrud.retrieve(this.processManager.getServiceManager(), Conta.class, 2l, null);;
		Calendar data = CalendarUtils.getCalendar();
		BigDecimal valor = DecimalUtils.TEN;
		String descricao = "TransferirProcessTest";
		
		process.setContaOrigem(contaOrigem);
		process.setContaDestino(contaDestino);
		process.setData(data);
		process.setValor(valor);
		process.setDescricao(descricao);
		IEntity entity = null;
		if (process.runTransferir()) {
			entity = process.getLancamentoMovimentoOrigem();
		}
		UtilsTest.showMessageList(process.getMessageList());
		assertNotNull(entity);
	}

}
