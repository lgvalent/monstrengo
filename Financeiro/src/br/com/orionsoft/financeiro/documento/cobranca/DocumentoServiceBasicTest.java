package br.com.orionsoft.financeiro.documento.cobranca;

import br.com.orionsoft.financeiro.documento.cobranca.titulo.ProvedorBanco;
import br.com.orionsoft.monstrengo.core.test.ApplicationBasicTest;


public class DocumentoServiceBasicTest extends ApplicationBasicTest
{

    protected ProvedorDocumentoCobranca provedorDocumentoCobranca;
    protected ProvedorBanco provedorBanco;

//    public static void main(String[] args)
//    {
//        junit.textui.TestRunner.run(DocumentoServiceBasicTest.class);
//    }

    public void setUp() throws Exception
    {
    	super.setUp();
    	provedorDocumentoCobranca = (ProvedorDocumentoCobranca)ctx.getBean("ProvedorDocumentoCobranca");
    	provedorBanco = (ProvedorBanco)ctx.getBean("ProvedorBanco");
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
        provedorDocumentoCobranca=null;
        provedorBanco=null;
    }
    
}
