package br.com.orionsoft.monstrengo.core.test;

import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ApplicationBasicTest {

	protected ApplicationContext ctx;

	@Before
	public void setUp() throws Exception {
		System.out.println("setup ApplicationBasic");
		ctx = new FileSystemXmlApplicationContext("applicationContext.xml");

	}

	@After
	public void tearDown() throws Exception {
		System.out.println("tearDown ApplicationBasic");
		ctx = null;
	}

}
