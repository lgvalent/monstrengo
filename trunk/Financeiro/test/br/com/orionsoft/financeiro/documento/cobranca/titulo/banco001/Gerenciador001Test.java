package br.com.orionsoft.financeiro.documento.cobranca.titulo.banco001;

import static org.junit.Assert.fail;

import org.junit.Test;

import br.com.orionsoft.financeiro.documento.cobranca.titulo.DocumentoTitulo;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class Gerenciador001Test extends ServiceBasicTest {

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

	@Test
	public void testGetLinhaDigitavel() throws BusinessException {
		IEntity documento = UtilsCrud.retrieve(this.serviceManager, DocumentoTitulo.class, 2, null);
		Gerenciador001 gerenciador = new Gerenciador001();
		String linhaDigitavel = gerenciador.getLinhaDigitavel(documento);
		System.out.println(linhaDigitavel);
	}

//	@Test
	public void testGerarRemessa() {
		fail("Not yet implemented");
	}

//	@Test
	public void testReceberRetorno() {
		fail("Not yet implemented");
	}

}
