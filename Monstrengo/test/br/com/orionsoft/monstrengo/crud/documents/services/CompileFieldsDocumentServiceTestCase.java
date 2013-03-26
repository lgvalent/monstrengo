package br.com.orionsoft.monstrengo.crud.documents.services;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.documents.services.CompileFieldsDocumentService;

public class CompileFieldsDocumentServiceTestCase extends ServiceBasicTest
{
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CompileFieldsDocumentServiceTestCase.class);
    }

    @Test
    public void testExecute()
    {
        try{
			Map<String, String> map = new HashMap<String, String>();
//			map.put("Campo 1", "123");
			map.put("Campo 1", "21/01/20099999");
			map.put("Nome da pessoa", "Arthur Lundgren Tecidos S/A");
			map.put("Campo 2", "Valor padrão2");
			
			String source = "Campo 1 @{Campo 1,Valor padrão1} Campo 1 @{Campo 2,Valor padrão2} Campo 1 @{Campo 1} Campo 1 @{Campo 2} Dono do contrato: @{ Nome da pessoa , #{ContratoSindicato[?].pessoa.nome} } ";

            ServiceData serviceData = new ServiceData(CompileFieldsDocumentService.SERVICE_NAME, null);
            serviceData.getArgumentList().setProperty(CompileFieldsDocumentService.IN_DOCUMENT_SOURCE, source);
            serviceData.getArgumentList().setProperty(CompileFieldsDocumentService.IN_DOCUMENT_FIELDS_MAP, map);
            this.serviceManager.execute(serviceData);
            
            System.out.println(serviceData.getFirstOutput());
            
        }
        catch(BusinessException e)
        {
        	UtilsTest.showMessageList(e.getErrorList());
        	Assert.assertTrue(false);
        }
    }

}
