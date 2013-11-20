package br.com.orionsoft.basico.cadastro.endereco;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.endereco.Municipio;
import br.com.orionsoft.basic.entities.endereco.Uf;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Esse classe testa o relacionamento entre as classes user e group, verificando
 * se com a exclusão de um group, implica na exclusão do relacionamento com user 
 * na tabela de relacionamentos
 * @author marcia
 *
 */
public class UfTestCase extends ServiceBasicTest
{

	@Test
	public void testRun()
    {
        try
        {
//            IEntityList users = UtilsCrud.list(this.serviceManager, ApplicationUser.class, null);
//            
//            assertTrue(users.size()>0);
//
            // Criando um UF
//            IEntity uf = UtilsCrud.create(this.serviceManager, Uf.class, null);
//            uf.setPropertyValue(Uf.NOME, "marcia");
//            uf.setPropertyValue(Uf.SIGLA, "ma");
//            UtilsCrud.update(this.serviceManager, uf, null);
            
            // Criando e setando duas instâncias de Municipio
            IEntity municipio1 = UtilsCrud.create(this.serviceManager, Municipio.class, null);
            municipio1.setPropertyValue(Municipio.NOME, "Maringá");
            municipio1.setPropertyValue(Municipio.UF, Uf.PR);
            UtilsCrud.update(this.serviceManager, municipio1, null);
            
            IEntity municipio2 = UtilsCrud.create(this.serviceManager, Municipio.class, null);
            municipio2.setPropertyValue(Municipio.NOME, "Sarandi");
            municipio2.setPropertyValue(Municipio.UF, Uf.PR);
            UtilsCrud.update(this.serviceManager, municipio2, null);


            // Excluindo UF para verificar se os Municipios somem
            //UtilsCrud.delete(this.serviceManager, uf);
            
        } catch (BusinessException e)
        {
        	Assert.assertTrue(false);
        }
    }

}
