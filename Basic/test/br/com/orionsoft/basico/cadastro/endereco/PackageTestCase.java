package br.com.orionsoft.basico.cadastro.endereco;

import junit.framework.TestCase;

/**
 * Esse classe testa o relacionamento entre as classes user e group, verificando
 * se com a exclusão de um group, implica na exclusão do relacionamento com user 
 * na tabela de relacionamentos
 * @author marcia
 *
 */
public class PackageTestCase extends TestCase
{

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(PackageTestCase.class);
    }
    
    public void testRun()
    {
        Package pk = Package.getPackage("br");
//            Package pk = this.getClass().getPackage();
            
            for(Package pct: pk.getPackages())
            {
                System.out.println(pct.getName());
            }

            System.out.println(pk.getName());
            
    }

}
