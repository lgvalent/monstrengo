package br.com.orionsoft.monstrengo;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.test.DaoBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Esta classe gera o banco de acordo com as classes anotadas no applicationContext.xml.
 * Nos projetos filhos, esta classe poderá ser extendida e os atributos poderão ser sobrescritos os valores.
 * @author lucio 
 */
public class GerarTabelas extends DaoBasicTest{
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void createSchema() {
		createSchema(true, false, false);
	}

	/**
	 * Este método é utilizado pelas classes filhas para executar uma geração personalizada.
	 */
	public void createSchema(boolean generateScript,
							  boolean export,
							  boolean justDrop) {
		AnnotationConfiguration cfg = new AnnotationConfiguration();

		cfg.setProperties(this.daoManager.getConfiguration().getProperties());
		
		for(IDAO<?> dao: this.daoManager.getDaos().values()){
			cfg.addAnnotatedClass(dao.getEntityClass());
		}
		
		SchemaExport schExport = new SchemaExport(cfg);
		/* É necessário que o arquivo esteja com edição liberada. 
		 * Para isto, acesse o popup menu>Team>Edit */
		schExport.setOutputFile("./schema-export.sql");
		schExport.execute(generateScript, export, justDrop, true);

		if(generateScript){
			/* Reescreve o schema-export.sql para renomear as constraint */
			new RewriteConstraintExportSchema("./schema-export.sql").runRewriteFile();
		}
	}
}
