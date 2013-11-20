package br.com.orionsoft.financeiro.gerenciador.process;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.Situacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.QueryService;

public class ListarPosicaoContratoProcessTest extends ProcessBasicTest {
	private ListarPosicaoContratoProcess process;
	
	@Override
	public void setUp() throws Exception, BusinessException {
		super.setUp();
		process = (ListarPosicaoContratoProcess) this.processManager.createProcessByName(ListarPosicaoContratoProcess.PROCESS_NAME, this.getAdminSession());
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		process = null;
	}

	@Test
	public void testRunListar() throws BusinessException {
		ServiceData sd = new ServiceData(QueryService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, Lancamento.class);
		sd.getArgumentList().setProperty(QueryService.IN_MAX_RESULT_OPT, 40);
		sd.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT, "id DESC");
		this.processManager.getServiceManager().execute(sd);
		
		IEntityList<Lancamento> lancamentos = sd.getFirstOutput();
		Lancamento oLancamento = lancamentos.getFirst().getObject();
		IEntity<Contrato> contrato = lancamentos.getFirst().getPropertyValue(Lancamento.CONTRATO);
		IEntity<Pessoa> pessoa = contrato.getPropertyValue(Contrato.PESSOA);
		Calendar dataInicial = CalendarUtils.getCalendar(oLancamento.getDataVencimento().get(Calendar.YEAR), Calendar.JANUARY, 01);
		Calendar dataFinal = CalendarUtils.getCalendar(oLancamento.getDataVencimento().get(Calendar.YEAR), Calendar.DECEMBER, 31);
		Situacao situacao = Situacao.PENDENTE;
		
		process.getParamPessoa().setValue(pessoa);
		process.setDataInicial(dataInicial);
		process.setDataFinal(dataFinal);
		process.setSituacao(situacao.ordinal());
		assertTrue(process.runListar());
		
		System.out.println("==> "+process.getLancamentos().size());
		for (IEntity<Lancamento> lancamento : process.getLancamentos()) {
			System.out.println(lancamento.getId());
		}
	}

}
