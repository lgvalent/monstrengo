package br.com.orionsoft.financeiro.documento.cobranca.titulo.banco748;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import br.com.orionsoft.financeiro.documento.cobranca.processes.GerarDocumentoCobrancaRemessaProcess;
import br.com.orionsoft.financeiro.documento.cobranca.services.GerarDocumentoCobrancaRemessaService;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.DocumentoTitulo;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.Ocorrencia;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.banco001.Gerenciador001;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class Gerenciador748Test extends ServiceBasicTest {

//	@Test
	public void testCalcularDigitoNossoNumero() {
		fail("Not yet implemented");
	}

//	@Test
	public void testCalcularCampoLivre() throws BusinessException {
		IEntity documento = UtilsCrud.retrieve(this.serviceManager, DocumentoTitulo.class, 2, null);
		Gerenciador001 gerenciador = new Gerenciador001();
		String campoLivre = gerenciador.getCampoLivre(documento);
		System.out.println(campoLivre);
	}

//	@Test
	public void testFormatarNossoNumero() {
		fail("Not yet implemented");
	}

//	@Test
	public void testFormatarAgenciaCedente() {
		fail("Not yet implemented");
	}

//	@Test
	public void testGetCodigoBarras() throws BusinessException {
		IEntity documento = UtilsCrud.retrieve(this.serviceManager, DocumentoTitulo.class, 2, null);
		Gerenciador001 gerenciador = new Gerenciador001();
		String codigoBarras = gerenciador.getCodigoBarras(documento);
		System.out.println(codigoBarras);
	}

//	@Test
	public void testGetLinhaDigitavel() throws BusinessException {
		IEntity documento = UtilsCrud.retrieve(this.serviceManager, DocumentoTitulo.class, 2, null);
		Gerenciador001 gerenciador = new Gerenciador001();
		String linhaDigitavel = gerenciador.getLinhaDigitavel(documento);
		System.out.println(linhaDigitavel);
	}

	@Test
	public void testGerarRemessa() {
		try {
			DocumentoTitulo oBoleto = UtilsCrud.objectRetrieve(serviceManager, DocumentoTitulo.class, 270892, null);
			Ocorrencia oOcorrencia =  UtilsCrud.objectRetrieve(serviceManager, Ocorrencia.class, 1, null);
			
			oBoleto.setUltimaOcorrencia(oOcorrencia);
			
			UtilsCrud.objectUpdate(serviceManager, oBoleto, null);
			
			
	    	ServiceData sdGerarRemessa = new ServiceData(GerarDocumentoCobrancaRemessaService.SERVICE_NAME, null);
	    	sdGerarRemessa.getArgumentList().setProperty(GerarDocumentoCobrancaRemessaService.IN_CONVENIO_ID, 3);
	    	sdGerarRemessa.getArgumentList().setProperty(GerarDocumentoCobrancaRemessaService.IN_INICIO_PERIODO, CalendarUtils.getCalendarFirstMonthDay());
	    	sdGerarRemessa.getArgumentList().setProperty(GerarDocumentoCobrancaRemessaService.IN_FINAL_PERIODO, CalendarUtils.getCalendarLastMonthDay());
	    	
	    	serviceManager.execute(sdGerarRemessa);

	    	File arquivoRemessa = sdGerarRemessa.getFirstOutput();
	    	String nomeArquivoRemessa = arquivoRemessa.getName();
	    	
	    	System.out.println("Nome do arquivo: " + nomeArquivoRemessa + " de " + arquivoRemessa.length() + "bytes.");
	    	UtilsTest.showMessageList(sdGerarRemessa.getMessageList());

			
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Test
	public void testReceberRetorno() {
		fail("Not yet implemented");
	}

}
