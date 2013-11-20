


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import br.com.orionsoft.financeiro.contabilidade.services.ExportarMovimentoContabilService;
import br.com.orionsoft.financeiro.contabilidade.services.QueryLancamentoContabil;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class EntityTest extends ServiceBasicTest {
	
	@Test
	public void testExecute() throws BusinessException{
		
		Calendar dataInicial = CalendarUtils.getCalendar(2012, Calendar.NOVEMBER, 1);
		Calendar dataFinal = CalendarUtils.getCalendar(2012, Calendar.NOVEMBER, 1);
		
//		IEntity<Lancamento> entity = UtilsCrud.create(this.serviceManager, Lancamento.class, null);
		IEntity<Lancamento> entity = UtilsCrud.retrieve(this.serviceManager, Lancamento.class, 312421, null);
	 	
		System.out.println("Data alterada:" + entity.getProperty(Lancamento.DATA_VENCIMENTO).getValue().isModified());
		entity.getProperty(Lancamento.DATA_VENCIMENTO).getValue().setAsString("28/11/2013");
		System.out.println("Data alterada:" + entity.getProperty(Lancamento.DATA_VENCIMENTO).getValue().isModified());
		
	}
}
