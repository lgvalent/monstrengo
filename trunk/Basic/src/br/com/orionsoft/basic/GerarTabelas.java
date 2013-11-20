package br.com.orionsoft.basic;

import org.junit.Test;

public class GerarTabelas extends br.com.orionsoft.monstrengo.GerarTabelas{

	@Test
	public void createSchema() {
		super.createSchema(true, false, false);
	}

}
