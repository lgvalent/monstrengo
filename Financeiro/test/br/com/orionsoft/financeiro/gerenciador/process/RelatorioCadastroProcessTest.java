package br.com.orionsoft.financeiro.gerenciador.process;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCadastroService;
import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCadastroService.TipoRelatorio;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

public class RelatorioCadastroProcessTest extends ProcessBasicTest{
	private RelatorioCadastroProcess process = null;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		process = (RelatorioCadastroProcess) processManager.createProcessByName(RelatorioCadastroProcess.PROCESS_NAME, this.getAdminSession());
	}

//	@After
//	public void tearDown() throws Exception {
//	super.tearDown();
//	}


	@Test
	public void testExecute() throws IOException {

		try {
			File file = new File("/tmp/relatorio_cadastro.pdf");
			OutputStream stream = new FileOutputStream(file);
			
//			process.setAgrupamento(RelatorioCadastroService.Agrupamento.CNAE);
			process.setCampoList(new Integer[]{0,1,2,3,4,5,6,7,8,9,10});
			process.setCnaeId(1l);
			process.setCpfCnpj("75317701000158");
			process.setDataComecoAtividadeInicio(CalendarUtils.getCalendar("01/01/1990"));
			process.setDataComecoAtividadeFim(CalendarUtils.getCalendar("31/12/2005"));
			process.setDataContratoInicio(CalendarUtils.getCalendar("01/01/1990"));
			process.setDataContratoFim(CalendarUtils.getCalendar("31/12/2005"));
			process.setDataContratoRescisaoInicio(null);
			process.setDataContratoRescisaoFim(null);
			process.setDataLancamentoFim(null);
			process.setDataLancamentoInicio(null);
			process.setDataLancamentoRecebimentoFim(null);
			process.setDataLancamentoRecebimentoInicio(null);
			process.setDataLancamentoVencimentoFim(null);
			process.setDataTerminoAtividadeFim(null);
			process.setDataTerminoAtividadeInicio(null);
			process.setEscritorioContabilId(7l);
			process.setIncluirDatasCadastro(false);
			process.setIncluirInativos(false);
			process.setItemCustoList(new Long[]{new Long(0),new Long(1),new Long(2)});
			process.setMunicipioId(null);
//			process.setOrdenacao(RelatorioCadastroService.Ordenacao.APELIDO.ordinal());
			process.setOrdenacao(new Integer(0));
			process.setRepresentanteId(null);
			process.setTipoEstabelecimento(RelatorioCadastroService.TipoEstabelecimento.FILIAL.ordinal());
			process.setTipoRelatorio(TipoRelatorio.LISTAGEM.ordinal());
			process.setIncluirMovimentacoes(true);
			process.setContratoCategoriaId(1l);
			process.setOutputStream(stream);
			process.execute();
			process.finish();
			process = null;
		} catch (ProcessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertTrue(true);
	}

}
