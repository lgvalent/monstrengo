package br.com.orionsoft.financeiro.gerenciador.services;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class CancelarLancamentoServiceTest extends ServiceBasicTest {

	@Test
	public void testExecute() throws Exception  {
//		/*
//		 * Insere um lançamento para poder quitar.
//		 */
//		Calendar data = CalendarUtils.getCalendar(05, Calendar.JULY, 2007);
//		Calendar dataVencimento = CalendarUtils.getCalendar(30, Calendar.JULY, 2007);
//		String descricao = "InserirLancamentoServiceTest";		
//		Integer transacao = Lancamento.TRANSACAO_CREDITO;
//		BigDecimal valor = DecimalUtils.getBigDecimal(1000.00);
//		
//		IEntity centroCusto = UtilsCrud.retrieve(this.serviceManager, CentroCusto.class, 1l, null);
//		IEntity classificacaoContabil = UtilsCrud.retrieve(this.serviceManager, ClassificacaoContabil.class, 1l, null);
//		IEntity contaPrevista = UtilsCrud.retrieve(this.serviceManager, Conta.class, 1l, null);
//		IEntity operacao = UtilsCrud.retrieve(this.serviceManager, Operacao.class, 1l, null);
//		IEntity contrato = UtilsCrud.retrieve(this.serviceManager, Contrato.class, 1l, null);
//		IEntityList itemCustoList = UtilsCrud.list(this.serviceManager, ItemCusto.class, null); 
//		
//		ServiceData sdLancamento = new ServiceData(InserirLancamentoService.SERVICE_NAME, null);
//		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_CENTRO_CUSTO, centroCusto);
//		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_CLASSIFICACAO_CONTABIL_OPT, classificacaoContabil);
//		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_CONTA_PREVISTA_OPT, contaPrevista);
//		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_CONTRATO, contrato);
//		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DATA, data);
//		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DATA_VENCIMENTO, dataVencimento);
//		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_DESCRICAO_OPT, descricao);
//		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_ITEM_CUSTO_LIST, itemCustoList);
//		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_OPERACAO, operacao);
//		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_TRANSACAO, transacao);
//		sdLancamento.getArgumentList().setProperty(InserirLancamentoService.IN_VALOR, valor);
//		this.serviceManager.execute(sdLancamento);
//		IEntity lancamento = sdLancamento.getFirstOutput();
		
		InserirLancamentoServiceTest inserirLancamentoServiceTest = new InserirLancamentoServiceTest();
		inserirLancamentoServiceTest.setUp();
		inserirLancamentoServiceTest.inserir();
		inserirLancamentoServiceTest.tearDown();
		Lancamento lancamento = inserirLancamentoServiceTest.getLancamento();
		
		/*
		 * Cria os parâmetros necessários para o serviço. 
		 */
		Calendar dataCancelamento = CalendarUtils.getCalendar(1, Calendar.JANUARY, 2007);
		
		/*
		 * Executa o serviço.
		 */
		ServiceData sd = new ServiceData(CancelarLancamentoService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(CancelarLancamentoService.IN_DATA, dataCancelamento);
		sd.getArgumentList().setProperty(CancelarLancamentoService.IN_LANCAMENTO, lancamento);
		sd.getArgumentList().setProperty(CancelarLancamentoService.IN_DESCRICAO, "Cancelado");
		this.serviceManager.execute(sd);
		
		lancamento = UtilsCrud.objectRetrieve(this.serviceManager, Lancamento.class, lancamento.getId(), null);
		
		assertEquals(DecimalUtils.ZERO, lancamento.getSaldo());
	}

}
