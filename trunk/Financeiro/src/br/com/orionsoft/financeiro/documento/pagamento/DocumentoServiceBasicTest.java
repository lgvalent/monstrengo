package br.com.orionsoft.financeiro.documento.pagamento;

import br.com.orionsoft.financeiro.documento.cobranca.titulo.ProvedorBanco;
import br.com.orionsoft.monstrengo.core.test.ApplicationBasicTest;


public class DocumentoServiceBasicTest extends ApplicationBasicTest
{

    protected ProvedorDocumentoPagamento provedorDocumentoPagamento;
    protected ProvedorBanco provedorBanco;

//    public static void main(String[] args)
//    {
//        junit.textui.TestRunner.run(DocumentoServiceBasicTest.class);
//    }

    public void setUp() throws Exception
    {
    	super.setUp();
    	provedorDocumentoPagamento = (ProvedorDocumentoPagamento)ctx.getBean("ProvedorDocumentoCobranca");
    	provedorBanco = (ProvedorBanco)ctx.getBean("ProvedorBanco");
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
        provedorDocumentoPagamento=null;
        provedorBanco=null;
    }
    
}
