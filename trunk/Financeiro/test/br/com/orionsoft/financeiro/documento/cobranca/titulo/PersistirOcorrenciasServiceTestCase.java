package br.com.orionsoft.financeiro.documento.cobranca.titulo;


import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.financeiro.documento.cobranca.titulo.services.PersistirOcorrenciasService;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;

public class PersistirOcorrenciasServiceTestCase extends ServiceBasicTest {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PersistirOcorrenciasServiceTestCase.class);
    }
    
    @Test
    public void testProcess() {
        
        try {
            /*
             * Persiste todas as ocorrências que estão declaradas nas constantes da classe Ocorrencia.java
             * @see Ocorrencia.java
             */
            ServiceData sd = new ServiceData(PersistirOcorrenciasService.SERVICE_NAME, null);
            this.serviceManager.execute(sd);

        	Assert.assertTrue(sd.getMessageList().isTransactionSuccess());
        } catch(ServiceException e) {
            e.printStackTrace();
            UtilsTest.showMessageList(e.getErrorList());
        	Assert.assertTrue(true);
        }
    }

}
