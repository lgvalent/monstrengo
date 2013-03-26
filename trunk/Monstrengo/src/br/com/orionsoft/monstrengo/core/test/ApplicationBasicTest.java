package br.com.orionsoft.monstrengo.core.test;

import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ApplicationBasicTest {
	
	public static String APPLICATION_CONTEXT_PATH = "applicationContext.xml";

	protected ApplicationContext ctx;

	@Before
	public void setUp() throws Exception {
		System.out.println("setup ApplicationBasic using file: " + APPLICATION_CONTEXT_PATH);
		ctx = new FileSystemXmlApplicationContext(APPLICATION_CONTEXT_PATH);
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("tearDown ApplicationBasic");
		ctx = null;
	}

}
