package br.com.orionsoft.financeiro.gerenciador.services;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.services.ListarItemCustoService.Coluna;
import br.com.orionsoft.financeiro.gerenciador.services.ListarItemCustoService.QueryItemCusto;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;

public class ListarItemCustoServiceTestCase extends ServiceBasicTest {

	@Test
	public void test() throws ServiceException {
        Calendar dataLancamentoInicial = CalendarUtils.getCalendar(2008, Calendar.MAY, 1);
        Calendar dataLancamentoFinal = CalendarUtils.getCalendar(2008, Calendar.AUGUST, 31);
        String centroCustoIdList = "1, 2";
        int[] colunas = {
        		Coluna.CONTA.ordinal()
//        		Coluna.DATA.ordinal(),
//        		Coluna.CENTRO_CUSTO.ordinal()
        		};

        ServiceData sd = new ServiceData(ListarItemCustoService.SERVICE_NAME, null);
        sd.getArgumentList().setProperty(ListarItemCustoService.IN_DATA_LANCAMENTO_INICIAL, dataLancamentoInicial);
        sd.getArgumentList().setProperty(ListarItemCustoService.IN_DATA_LANCAMENTO_FINAL, dataLancamentoFinal);
        sd.getArgumentList().setProperty(ListarItemCustoService.IN_CENTRO_CUSTO_ID_LIST_OPT, centroCustoIdList);
        sd.getArgumentList().setProperty(ListarItemCustoService.IN_COLUNAS_OPT, colunas);
        this.serviceManager.execute(sd);

        List<QueryItemCusto> list = sd.getFirstOutput();
        assertFalse(list.isEmpty());
        
        for (QueryItemCusto itemCusto : list) {
            System.out.println(
                    CalendarUtils.formatDate(itemCusto.getData()) + " | " +
                    itemCusto.getCentroCusto() + " | " +
                    itemCusto.getConta() + " | " + 
                    DecimalUtils.formatBigDecimal(itemCusto.getValorTotal()) + " | " +
                    itemCusto.getItemCusto()
                    );
        }
    }
    
}
