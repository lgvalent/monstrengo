package br.com.orionsoft.financeiro.documento.cobranca.processes;

import java.io.File;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

/**
 * Classe de teste para a classe GerarDocumentoCobrancaRemessaProcess.
 * <p>Procedimento:</p>
 * <p>-Setar os valores necessários para a pesquisa de títulos;</p>
 * <p>-Executar o processo;</p>
 * <p>-Obter o arquivo de remessa do processo.</p>
 */
public class GerarDocumentoCobrancaRemessaProcessTestCase extends ProcessBasicTest {
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(GerarDocumentoCobrancaRemessaProcessTestCase.class);
	}
	
	@Test
	public void testExecute() throws BusinessException{
		try{
			GerarDocumentoCobrancaRemessaProcess process = (GerarDocumentoCobrancaRemessaProcess) this.processManager.createProcessByName(GerarDocumentoCobrancaRemessaProcess.PROCESS_NAME, this.getAdminSession());
			
			long cedenteId = 1l;
			Calendar inicioPeriodo = CalendarUtils.getCalendar();
			inicioPeriodo.set(2007, Calendar.DECEMBER, 6);
			Calendar finalPeriodo = CalendarUtils.getCalendar();
			finalPeriodo.set(2007, Calendar.DECEMBER, 8);
			Integer quantidadeDiasProtesto = 10;
			
			process.setConvenioId(cedenteId);
			process.setInicioPeriodo(inicioPeriodo);
			process.setFinalPeriodo(finalPeriodo);
			process.setQuantidadeDiasProtesto(quantidadeDiasProtesto);

			File arquivoRemessa = null;
			if(process.runGerarRemessa()){
				arquivoRemessa = new File(process.getArquivoRemessa(), "/home/andre/Desktop/" + process.getNomeArquivoRemessa());
				System.out.println(arquivoRemessa.length());
				Assert.assertTrue(true);
			}else{
				Assert.assertTrue(false);
			}

			UtilsTest.showMessageList(process.getMessageList());
		}catch (BusinessException be){
			UtilsTest.showMessageList(be.getErrorList());
		}
	}
}