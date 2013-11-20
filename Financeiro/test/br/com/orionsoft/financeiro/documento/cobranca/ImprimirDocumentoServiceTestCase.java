package br.com.orionsoft.financeiro.documento.cobranca;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.cobranca.services.ImprimirDocumentosCobrancaService;import br.com.orionsoft.financeiro.documento.cobranca.services.ImprimirDocumentosCobrancaService;

import br.com.orionsoft.financeiro.documento.cobranca.services.LancarDocumentoCobrancaService;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Classe de teste para a classe CriarDocumentoService.
 * <p>Procedimento:</p>
 * <p>-Setar os valores necessarios para criar o documento</p>
 * <p>-Mostrar os valores do documento criado</p>
 */
public class ImprimirDocumentoServiceTestCase extends ServiceBasicTest {
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(ImprimirDocumentoServiceTestCase.class);
	}
	
	@Test
	public void testExecute() throws BusinessException{
		
		try{
		IEntity documento  = UtilsCrud.retrieve(this.serviceManager, DocumentoCobranca.class, 173210, null);
		FileOutputStream file = new FileOutputStream("/home/orion/Desktop/documento.pdf");
		
		UtilsTest.showEntityProperties(documento);
		
			ServiceData sdImprimirDocumento = new ServiceData(ImprimirDocumentosCobrancaService.SERVICE_NAME, null);
			sdImprimirDocumento.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_DOCUMENTO_OPT, documento);
			sdImprimirDocumento.getArgumentList().setProperty(ImprimirDocumentosCobrancaService.IN_OUTPUT_STREAM_OPT, file);
			
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