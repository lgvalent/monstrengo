package br.com.orionsoft.financeiro.documento.cobranca;

import java.io.FileOutputStream;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.services.ImprimirDocumentosPagamentoService;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Classe de teste para a classe CriarDocumentoService.
 * <p>Procedimento:</p>
 * <p>-Setar os valores necessarios para criar o documento</p>
 * <p>-Mostrar os valores do documento criado</p>
 */
public class ImprimirDocumentoPagamentoServiceTestCase extends ServiceBasicTest {

	@Test
	public void testExecute() throws BusinessException{
		
		try{
		IEntity documento  = UtilsCrud.retrieve(this.serviceManager, DocumentoPagamento.class, 185680, null);
		FileOutputStream file = new FileOutputStream("/home/orion/Desktop/documentoPagamento.pdf");
		
		UtilsTest.showEntityProperties(documento);
		
			ServiceData sdImprimirDocumento = new ServiceData(ImprimirDocumentosPagamentoService.SERVICE_NAME, null);
			sdImprimirDocumento.getArgumentList().setProperty(ImprimirDocumentosPagamentoService.IN_DOCUMENTO_OPT, documento);
			sdImprimirDocumento.getArgumentList().setProperty(ImprimirDocumentosPagamentoService.IN_OUTPUT_STREAM_OPT, file);
			
			System.out.println("::Iniciando ImprimirDocumentoServiceTestCase");
			this.serviceManager.execute(sdImprimirDocumento);
			
		}catch (ServiceException se){
			UtilsTest.showMessageList(se.getErrorList());
			Assert.assertTrue(false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Assert.assertTrue(false);
		}
		
	}
}