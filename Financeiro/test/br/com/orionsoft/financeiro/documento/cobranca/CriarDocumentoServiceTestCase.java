package br.com.orionsoft.financeiro.documento.cobranca;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.cobranca.services.CriarDocumentoCobrancaService;
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
public class CriarDocumentoServiceTestCase extends ServiceBasicTest {
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(CriarDocumentoServiceTestCase.class);
	}
	
	@Test
	public void testExecute() throws BusinessException{
		
		IEntityList list = UtilsCrud.list(this.serviceManager, Contrato.class, null);
		IEntity contrato = list.getFirst();
		
		IEntity documentoCobrancaCategoria = UtilsCrud.retrieve(this.serviceManager, DocumentoCobrancaCategoria.class, 1, null);
		
		Calendar dataDocumento = Calendar.getInstance();
		Calendar dataVencimento = Calendar.getInstance();
		
		BigDecimal valorDocumento = new BigDecimal(59.00);

		System.out.println("\n ###############################");
		System.out.println("\nContrato: " + contrato.getObject());
		System.out.println("\nConvenio da forma de pagamento: " + documentoCobrancaCategoria.getPropertyValue(DocumentoCobrancaCategoria.CONVENIO_COBRANCA));
		System.out.println("\nData do Documento: " + dataDocumento);
		System.out.println("\nData de Vencimento: " + dataVencimento);
		System.out.println("\nValor documento: " + valorDocumento);
		
		
		try{
			
			ServiceData sdCriarDocumento = new ServiceData(CriarDocumentoCobrancaService.SERVICE_NAME, null);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_CONTRATO, contrato);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DOCUMENTO_COBRANCA_CATEGORIA, documentoCobrancaCategoria);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_DOCUMENTO, dataDocumento);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_DATA_VENCIMENTO, dataVencimento);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_VALOR_DOCUMENTO, valorDocumento);
			sdCriarDocumento.getArgumentList().setProperty(CriarDocumentoCobrancaService.IN_TRANSACAO, Transacao.CREDITO);
			
			System.out.println("::Iniciando CriarDocumentoServiceTestCase");
			this.serviceManager.execute(sdCriarDocumento);
			
			IEntity doc = (IEntity)sdCriarDocumento.getFirstOutput();

			ServiceData sdLancarDocumento = new ServiceData(LancarDocumentoCobrancaService.SERVICE_NAME, null);
			sdLancarDocumento.getArgumentList().setProperty(LancarDocumentoCobrancaService.IN_DOCUMENTO, doc.getObject());
			
			System.out.println("::Iniciando CriarDocumentoServiceTestCase");
			this.serviceManager.execute(sdLancarDocumento);

			UtilsTest.showEntityProperties(doc);

//			DocumentoDinheiro d = (DocumentoPDinheiro)doc.getObject();
//			
//			System.out.println("\nDocumento Dinheiro: " + d);
//			System.out.println("Valor: " + d.getValorDocumento());
//			System.out.println(doc.getProperty(Documento.CONTRATO).getValue().isValueNull());
//			
//			assertFalse(doc.getProperty(Documento.VALOR_DOCUMENTO).getValue().isValueNull());
//			UtilsTest.showEntityProperties(doc);
			//UtilsTest.showEntityProperties((IEntity) doc.getPropertyValue(Documento.CONVENIO));
			//UtilsTest.showEntityProperties((IEntity) doc.getPropertyValue(Documento.GRUPOS));
			//UtilsTest.showEntityProperties((IEntity) doc.getPropertyValue(Documento.FORMA_PAGAMENTO));
			//UtilsTest.showEntityProperties((IEntity) doc.getPropertyValue(Documento.DATA_DOCUMENTO));
			//UtilsTest.showEntityProperties((IEntity) doc.getPropertyValue(Documento.DATA_VENCIMENTO));
			//UtilsTest.showEntityProperties((IEntity) doc.getPropertyValue(Documento.VALOR_DOCUMENTO));
			
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