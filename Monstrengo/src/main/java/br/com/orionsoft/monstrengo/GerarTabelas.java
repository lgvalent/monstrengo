package br.com.orionsoft.monstrengo;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.test.DaoBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Esta classe gera o banco de acordo com as classes anotadas no applicationContext.xml.
 * Nos projetos filhos, esta classe poder� ser extendida e os atributos poder�o ser sobrescritos os valores.
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
	 * Este m�todo � utilizado pelas classes filhas para executar uma gera��o personalizada.
	 */
	public void createSchema(boolean generateScript,
							  boolean export,
							  boolean justDrop) {
		AnnotationConfiguration cfg = new AnnotationConfiguration();

		cfg.setProperties(this.daoManager.getSessionFactoryBuilder().getHibernateProperties());
		
		/* Fornece uma conex�o para evitar a presen�a de hibernate.properties */
		BasicDataSource dataSource = this.ctx.getBean(BasicDataSource.class);
		
		cfg.getProperties().setProperty("hibernate.connection.driver_class", dataSource.getDriverClassName());
		cfg.getProperties().setProperty("hibernate.connection.url", dataSource.getUrl());
		cfg.getProperties().setProperty("hibernate.connection.username", dataSource.getUsername());
		cfg.getProperties().setProperty("hibernate.connection.password", dataSource.getPassword());

		for(IDAO<?> dao: this.daoManager.getDaos().values()){
			cfg.addAnnotatedClass(dao.getEntityClass());
		}
		
		SchemaExport schExport = new SchemaExport(cfg);
		/* � necess�rio que o arquivo esteja com edi��o liberada. 
		 * Para isto, acesse o popup menu>Team>Edit */
		schExport.setOutputFile("./schema-export.sql");
		schExport.execute(generateScript, export, justDrop, true);

		if(generateScript){
			/* Reescreve o schema-export.sql para renomear as constraint */
			new RewriteConstraintExportSchema("./schema-export.sql").runRewriteFile();
		}
	}
}
