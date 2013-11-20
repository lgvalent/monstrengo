package br.com.orionsoft.financeiro.documento.cobranca;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

import br.com.orionsoft.financeiro.documento.cobranca.services.ReceberDocumentoCobrancaRetornoService;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoResultado;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoResultadoSumario;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;

/**
 * Classe de teste para a classe CriarDocumentoService.
 * <p>Procedimento:</p>
 * <p>-Setar os valores necessarios para criar o documento</p>
 * <p>-Mostrar os valores do documento criado</p>
 */
public class ReceberDocumentoCobrancaRetornoServiceTestCase extends ServiceBasicTest {
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(ReceberDocumentoCobrancaRetornoServiceTestCase.class);
	}
	
	@Test
	public void testExecute() throws BusinessException{
		
		File tempFile = null; 
		
//		tempFile = new File("/home/lucio/Desktop/IEDCBRBI.RET");
		tempFile = new File("/home/andre/retorno.ret");

		
		try{
			ServiceData sdReceberRetorno = new ServiceData(ReceberDocumentoCobrancaRetornoService.SERVICE_NAME, null);
			sdReceberRetorno.getArgumentList().setProperty(ReceberDocumentoCobrancaRetornoService.IN_CONVENIO_COBRANCA_ID, new Long(2));
//			sdReceberRetorno.getArgumentList().setProperty(ReceberDocumentoCobrancaRetornoService.IN_DADOS_RETORNO, tempFile);
			
			System.out.println("::Iniciando ReceberRetornoServiceTestCase");
			this.serviceManager.execute(sdReceberRetorno);
			
			DocumentoRetornoResultadoSumario sumario = sdReceberRetorno.getFirstOutput();

			UtilsTest.showMessageList(sdReceberRetorno.getMessageList());
			
			for(DocumentoRetornoResultado result: sumario.getNumeroDocumentoNaoEncontradoList())
				System.out.println(result.getNumeroDocumento() + "->" + result.getStatus());
			for(DocumentoRetornoResultado result: sumario.getDocumentoJaLiquidadoList())
				System.out.println(result.getNumeroDocumento() + "->" + result.getStatus());
			UtilsTest.showMessageList(sdReceberRetorno.getMessageList());
			Assert.assertTrue(true);
		}catch (ServiceException se){
			UtilsTest.showMessageList(se.getErrorList());
			Assert.assertTrue(false);
		}
		catch (Exception e){
			e.printStackTrace();
			Assert.assertTrue(false);
		}
		
	}
}