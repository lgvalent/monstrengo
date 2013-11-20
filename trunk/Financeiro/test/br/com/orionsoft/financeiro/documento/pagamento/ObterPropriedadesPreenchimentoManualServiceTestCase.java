package br.com.orionsoft.financeiro.documento.pagamento;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.services.CriarDocumentoCobrancaService;
import br.com.orionsoft.financeiro.documento.cobranca.services.LancarDocumentoCobrancaService;
import br.com.orionsoft.financeiro.documento.pagamento.services.CriarDocumentoPagamentoService;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
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
public class ObterPropriedadesPreenchimentoManualServiceTestCase extends ServiceBasicTest {
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(ObterPropriedadesPreenchimentoManualServiceTestCase.class);
	}
	
	@Test
	public void testExecute() throws BusinessException{
		
		IEntity<Contrato> contrato = UtilsCrud.retrieve(this.serviceManager, Contrato.class, 6598,null);
		IEntity<DocumentoPagamentoCategoria> documentoPagamentoCategoria = UtilsCrud.retrieve(this.serviceManager, DocumentoPagamentoCategoria.class, 1, null);
		
		Calendar dataDocumento = Calendar.getInstance();
		Calendar dataVencimento = Calendar.getInstance();
		
		BigDecimal valorDocumento = new BigDecimal(59.00);
		
		ServiceData sd = new ServiceData(CriarDocumentoPagamentoService.SERVICE_NAME, null);
		sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DOCUMENTO_PAGAMENTO_CATEGORIA, documentoPagamentoCategoria);
		sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_CONTRATO, contrato);
		sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_DOCUMENTO, dataDocumento);
		sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_DATA_VENCIMENTO, dataVencimento);
		sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_VALOR_DOCUMENTO, valorDocumento);
		sd.getArgumentList().setProperty(CriarDocumentoPagamentoService.IN_TRANSACAO, Transacao.CREDITO);
		this.serviceManager.execute(sd);

		IEntity<DocumentoPagamento> documento = sd.getFirstOutput();
		
		UtilsTest.showEntityProperties(documento);
		
				
	}
}