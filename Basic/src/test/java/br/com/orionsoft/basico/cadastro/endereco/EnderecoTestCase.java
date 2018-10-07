package br.com.orionsoft.basico.cadastro.endereco;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.endereco.Endereco;
import br.com.orionsoft.basic.entities.endereco.Telefone;
import br.com.orionsoft.basic.entities.pessoa.Juridica;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
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
public class EnderecoTestCase extends ServiceBasicTest
{

	@Test
	public void testRun()
    {
        try
        {
//            IEntityList users = UtilsCrud.list(this.serviceManager, ApplicationUser.class, null);
//            
//            Assert.assertTrue(users.size()>0);
//
            // Criando um Endereço
            IEntity end = UtilsCrud.create(this.serviceManager, Endereco.class, null);
            end.setPropertyValue(Endereco.COMPLEMENTO, "marcia");
            end.setPropertyValue(Endereco.NUMERO, "0123");
            end.setPropertyValue(Endereco.CAIXA_POSTAL, "cx postal");
            UtilsCrud.update(this.serviceManager, end, null);

            Assert.assertNotNull(end.getObject());
            
            // Criando uma pessoa fisica
            IEntity pj = UtilsCrud.create(this.serviceManager, Juridica.class, null);
            pj.setPropertyValue(Pessoa.NOME, "marcia");
            pj.setPropertyValue(Pessoa.ENDERECO_CORRESPONDENCIA, end);
            pj.getProperty(Pessoa.ENDERECOS).getValue().getAsEntityCollection().add(end);
            System.out.println("----->>>>" + pj.getProperty(Pessoa.ENDERECOS).getValue().getAsEntityCollection().size());
            System.out.println("ENDEREçO:" + end.getObject());
            Pessoa p = (Pessoa) pj.getObject();
            List col = p.getEnderecos();
            System.out.println("----->>>>>" + col.size());
//            col.add(end.getObject());
            System.out.println("----->>>>>" + col.size());
            p.setEnderecos(col);
//            end.getProperty(Endereco.PESSOAS).getValue().getAsEntitySet().add(pj);
            
//            UtilsCrud.update(this.serviceManager, end, null);
            
            
//            IEntitySet entitySet = new EntitySet(col, new EntityMetadata(Endereco.class, this.serviceManager.getEntityManager().getMetadataHandle()), this.serviceManager.getEntityManager());
//            pj.getProperty(Pessoa.ENDERECOS).getValue().setAsEntitySet(entitySet);
            Assert.assertTrue(p.getEnderecos().size() > 0);
            Assert.assertTrue(pj.getProperty(Pessoa.ENDERECOS).getValue().getAsEntityCollection().size() > 0);

            UtilsCrud.update(this.serviceManager, pj, null);
            
            pj = UtilsCrud.retrieve(this.serviceManager, Juridica.class, pj.getId(), null);
            
            Assert.assertTrue(pj.getProperty(Pessoa.ENDERECOS).getValue().getAsEntitySet().size() > 0);

            // Excluindo UF para verificar se os Municipios somem
            //UtilsCrud.delete(this.serviceManager, uf);
            
        } catch (BusinessException e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
    
	@Test
    public void testTelefone()
    {
        try
        {
//            IEntityList users = UtilsCrud.list(this.serviceManager, ApplicationUser.class, null);
//            
//            Assert.assertTrue(users.size()>0);
//
            // Criando um Endereço
            IEntity tel = UtilsCrud.create(this.serviceManager, Telefone.class, null);
            tel.setPropertyValue(Telefone.DDD, "ddd");
            tel.setPropertyValue(Telefone.NUMERO, "num");
            tel.setPropertyValue(Telefone.RAMAL, "ramal");
            UtilsCrud.update(this.serviceManager, tel, null);

            Assert.assertNotNull(tel.getObject());
            
            // Criando uma pessoa fisica
            IEntity pj = UtilsCrud.create(this.serviceManager, Juridica.class, null);
            pj.setPropertyValue(Pessoa.NOME, "marcia");
            pj.getProperty(Pessoa.TELEFONES).getValue().getAsEntityCollection().add(tel);
            System.out.println("----->>>>" + pj.getProperty(Pessoa.TELEFONES).getValue().getAsEntityCollection().size());
            System.out.println("TELEFONE:" + tel.getObject());
            Pessoa p = (Pessoa) pj.getObject();
            Set col = p.getTelefones();
            System.out.println("----->>>>>" + col.size());
            col.add(tel.getObject());
            System.out.println("----->>>>>" + col.size());
            p.setTelefones(col);
            
//            IEntitySet entitySet = new EntitySet(col, new EntityMetadata(Endereco.class, this.serviceManager.getEntityManager().getMetadataHandle()), this.serviceManager.getEntityManager());
//            pj.getProperty(Pessoa.ENDERECOS).getValue().setAsEntitySet(entitySet);
            Assert.assertTrue(p.getTelefones().size() > 0);
            Assert.assertTrue(pj.getProperty(Pessoa.TELEFONES).getValue().getAsEntityCollection().size() > 0);

            UtilsCrud.update(this.serviceManager, pj, null);
            
            pj = UtilsCrud.retrieve(this.serviceManager, Juridica.class, pj.getId(), null);
            
            Assert.assertTrue(pj.getProperty(Pessoa.TELEFONES).getValue().getAsEntitySet().size() > 0);

            // Excluindo UF para verificar se os Municipios somem
            //UtilsCrud.delete(this.serviceManager, uf);
            
        } catch (BusinessException e)
        {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }

}
