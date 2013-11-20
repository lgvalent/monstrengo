package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class TransferirServiceTest extends ServiceBasicTest {

	@Test
	public void testExecute() throws BusinessException{
		Calendar data = CalendarUtils.getCalendar(2008, Calendar.JULY, 05);
		BigDecimal valor = DecimalUtils.getBigDecimal(100.00);
		Conta contaOrigem = UtilsCrud.objectRetrieve(this.serviceManager, Conta.class, 1l, null);
		Conta contaDestino = UtilsCrud.objectRetrieve(this.serviceManager, Conta.class, 2l, null);
		LancamentoMovimentoCategoria lancamentoMovimentoCategoria = LancamentoMovimentoCategoria.TRANSFERIDO;
		String descricao = "Inserir Transferencia";
			
		ServiceData sd = new ServiceData(TransferirService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(TransferirService.IN_DATA, data);
		sd.getArgumentList().setProperty(TransferirService.IN_VALOR, valor);
		sd.getArgumentList().setProperty(TransferirService.IN_CONTA_ORIGEN, contaOrigem);
		sd.getArgumentList().setProperty(TransferirService.IN_CONTA_DESTINO, contaDestino);
		sd.getArgumentList().setProperty(TransferirService.IN_DESCRICAO, descricao);
		this.serviceManager.execute(sd);

		LancamentoMovimento lancamentoMovimento = sd.getFirstOutput();
		Assert.assertNotNull(lancamentoMovimento);
		
		LancamentoMovimento lancamentoMovimentoOrigem = UtilsCrud.objectRetrieve(this.serviceManager, LancamentoMovimento.class, lancamentoMovimento.getId(), null);
		Assert.assertEquals(lancamentoMovimentoOrigem.getValor(), valor);
		Assert.assertEquals(lancamentoMovimentoOrigem.getData(), data);
		Assert.assertEquals(lancamentoMovimentoOrigem.getDescricao(), descricao);
		Assert.assertEquals(lancamentoMovimentoOrigem.getLancamentoMovimentoCategoria(), lancamentoMovimentoCategoria);
		Assert.assertEquals(lancamentoMovimentoOrigem.getConta().toString(),contaOrigem.toString());
		
		LancamentoMovimento lancamentoMovimentoDestino = lancamentoMovimentoOrigem.getTransferencia();
		Assert.assertEquals(lancamentoMovimentoDestino.getValor(), valor.negate());
		Assert.assertEquals(lancamentoMovimentoDestino.getData(), data);
		Assert.assertEquals(lancamentoMovimentoDestino.getDescricao(), descricao);
		Assert.assertEquals(lancamentoMovimentoDestino.getLancamentoMovimentoCategoria(), lancamentoMovimentoCategoria);
		Assert.assertEquals(lancamentoMovimentoDestino.getConta().toString(),contaDestino.toString());
	}
}
