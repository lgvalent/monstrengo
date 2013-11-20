package br.com.orionsoft.financeiro.gerenciador.process;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.Ordem;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.QueryLancamento;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.Situacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class ListarLancamentoProcessTestCase extends ProcessBasicTest{
	ListarLancamentoProcess process;

    public void setUp() throws Exception {
        super.setUp();
        process = (ListarLancamentoProcess) this.processManager.createProcessByName(ListarLancamentoProcess.PROCESS_NAME, this.getAdminSession());
    }

   public void tearDown() throws Exception {
        super.tearDown();
        process = null;
    }

   @Test 
   public void testExecute() throws BusinessException, ParseException {
	    Long[] contaList = new Long[1];
	    contaList[0] = 3l;
		Long contrato = 1l;
		Calendar dataInicial = CalendarUtils.getCalendar(2010, Calendar.JANUARY, 01);
		Calendar dataFinal = CalendarUtils.getCalendar(2010, Calendar.DECEMBER, 31);
		Long centroCusto = 1l;
		String itemCustoList = "";
		Situacao situacao = Situacao.TODOS;
		Integer[] transacao = {Transacao.CREDITO.ordinal(), Transacao.DEBITO.ordinal()};
	   
//		process.setContaList(contaList);
//		process.setContrato(contrato);
//		process.setCpfCnpj("04175308000129");
		process.setDataFinal(dataFinal);
		process.setDataInicial(dataInicial);
//		process.setCentroCusto(centroCusto);
//		process.setItemCustoList(itemCustoList);
		process.setOrdem(Ordem.DATA.ordinal());
		process.setSituacao(situacao.ordinal());
//		process.setTransacao(transacao);
		Assert.assertTrue(process.runListar());
		
		for (QueryLancamento lancamento : process.getQueryLancamentoList()) {
			System.out.println(lancamento.getId());
		}
   }


}
