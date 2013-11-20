package br.com.orionsoft.financeiro.gerenciador.services;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Test;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabil;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.Operacao;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.utils.UtilsLancamento;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class QuitarLancamentoServiceTest extends ServiceBasicTest {
	private LancamentoMovimento lancamentoMovimento;

	@Test
	public void quitar() throws Exception {
		/*
		 * Cria os parâmetros necessários para o serviço. 
		 */
		Conta conta = UtilsCrud.objectRetrieve(this.serviceManager, Conta.class, 1l, null);
		Contrato contrato = UtilsCrud.objectRetrieve(this.serviceManager, Contrato.class, 1l, null);
		Operacao operacao = UtilsCrud.objectRetrieve(this.serviceManager, Operacao.class, 1l, null);
		CentroCusto centroCusto = UtilsCrud.objectRetrieve(this.serviceManager, CentroCusto.class, 1l, null);
		ItemCusto itemCusto = UtilsCrud.objectRetrieve(this.serviceManager, ItemCusto.class, 1l, null);
		ClassificacaoContabil classificacaoContabil = UtilsCrud.objectRetrieve(this.serviceManager, ClassificacaoContabil.class, 1l, null);

		Calendar data = CalendarUtils.getCalendar(2007, Calendar.SEPTEMBER, 18);
		BigDecimal valor = DecimalUtils.TEN;
		
		Lancamento lancamento = UtilsLancamento.inserir(
				this.serviceManager, 
				conta, 
				contrato, 
				data, 
				data, 
				"", 
				operacao, 
				Transacao.CREDITO,
				centroCusto,
				classificacaoContabil,
				itemCusto,
				valor,
				null, 
				false, 
				null);
		/*
		 * Executa o serviço.
		 */
		ServiceData sd = new ServiceData(QuitarLancamentoService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_CONTA, conta);
		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_DATA, data);
		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_LANCAMENTO, lancamento);
		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_VALOR, valor);
		this.serviceManager.execute(sd);
		
		/*
		 * Recarrega o lançamento para atualização.
		 */
		lancamento = UtilsCrud.objectRetrieve(this.serviceManager, Lancamento.class, lancamento.getId(), null);
		
		lancamentoMovimento = (LancamentoMovimento)sd.getFirstOutput();
		assertEquals(conta.toString(), lancamentoMovimento.getConta().toString());
		assertEquals(data, lancamentoMovimento.getData());
		assertEquals(lancamento.toString(), lancamentoMovimento.getLancamento().toString());
		assertEquals(LancamentoMovimentoCategoria.QUITADO, lancamentoMovimento.getLancamentoMovimentoCategoria());
		assertEquals(valor, lancamentoMovimento.getValor());
		assertEquals(DecimalUtils.getBigDecimal(0), lancamento.getSaldo());
	}

	public LancamentoMovimento getLancamentoMovimento() {
		return lancamentoMovimento;
	}
}
