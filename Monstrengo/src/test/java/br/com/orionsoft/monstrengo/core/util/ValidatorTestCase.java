package br.com.orionsoft.monstrengo.core.util;

import junit.framework.TestCase;
import br.com.orionsoft.monstrengo.core.util.ValidatorUtils;

public class ValidatorTestCase extends TestCase
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ValidatorTestCase.class);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /*
     * Test method for 'br.com.orionsoft.monstrengo.core.util.ValidatorUtils.dvCPF(String)'
     */
    public void testValidarCPF()
    {
        String cpf = "36925814755";
        assertTrue(ValidatorUtils.validarCPF(cpf));
        
        cpf = "36925814758";
        assertFalse(ValidatorUtils.validarCPF(cpf));
    }

    /*
     * Test method for 'br.com.orionsoft.monstrengo.core.util.ValidatorUtils.dvCPF(String)'
     */
    public void testDvCPF()
    {
        String cpf = "934210006";
        String dv = ValidatorUtils.dvCPF(cpf);
        //assertEquals(dv, "97");
//        System.out.println("DV gerado: " + dv);
//        System.out.println("CPF: " + (cpf + dv));
        
        cpf = "369258147";
        dv = ValidatorUtils.dvCPF(cpf);
        System.out.println("DV CPF: " + dv);
        System.out.println("CPF: " + (cpf + dv));
    }

    /*
     * Test method for 'br.com.orionsoft.monstrengo.core.util.ValidatorUtils.dvCNPJ(String)'
     */
    public void testDvCNPJ()
    {
//        String cnpj = "041753080001";
//        String dv = ValidatorUtils.dvCNPJ(cnpj);
//        assertEquals(dv, "29");
//        System.out.println("DV CNPJ: " + dv);
//        System.out.println("CNPJ: " + (cnpj + dv));
        
        String cnpj = "123456780001";
        String dv = ValidatorUtils.dvCNPJ(cnpj);
        //assertEquals(dv, "81");
        System.out.println("DV CNPJ: " + dv);
        System.out.println("CNPJ: " + (cnpj + dv));
        
//        cnpj = "032754730001";
//        dv = ValidatorUtils.dvCNPJ(cnpj);
//        System.out.println("CNPJ gerado para " + cnpj + ": " + dv);
        
    }

}
