package br.com.orionsoft.basico.cadastro.pessoa;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.pessoa.Fisica;
import br.com.orionsoft.basic.entities.pessoa.Juridica;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Esse classe testa o relacionamento entre as classes pessoa, juridica e
 * fisica. Tentando verificar as dificuldades de se lidar com heran�a e n�o
 * associa��o.
 * 
 * @author marcia
 * 
 */
public class PessoaTestCase extends ServiceBasicTest {

    @Test
    public void testRun() {
	try {
	    IEntity juridica = UtilsCrud.create(this.serviceManager,
		    Juridica.class, null);
	    UtilsCrud.update(this.serviceManager, juridica, null);

	    IEntity<Fisica> fisica = UtilsCrud.create(this.serviceManager,
		    Fisica.class, null);
	    UtilsCrud.update(this.serviceManager, fisica, null);

	    IEntityList<Pessoa> pessoas = UtilsCrud.list(this.serviceManager,
		    Pessoa.class, null);

	    Assert.assertTrue(pessoas.size() > 0);

	    Object obj = pessoas.getFirst().getObject();

	    Assert.assertTrue((obj.getClass() == Juridica.class)
		    || (obj.getClass() == Fisica.class));

	    for (IEntity ent : pessoas) {
		if (ent.getProperty("fisica").getValue().getAsBoolean())
		    System.out.println("Fisica:" + ent.getInfo().getLabel()
			    + "=" + ent.getObject().getClass().getSimpleName());
		else
		    System.out.println("Juridica:" + ent.getInfo().getLabel()
			    + "=" + ent.getObject().getClass().getSimpleName());

		for (IProperty prop : ent.getProperties())
		    if (prop.getInfo().isVisible())
			System.out.println(prop.getInfo().getName() + ":"
				+ prop.getValue().getAsString());
	    }

	} catch (BusinessException e) {
	    UtilsTest.showMessageList(e.getErrorList());

	    Assert.assertTrue(false);
	}
    }

}
