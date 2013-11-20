package br.com.orionsoft.financeiro;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

public class GerarListaEntidades extends ServiceBasicTest {
	
	public class DaoSortComparator implements Comparator<IDAO>{
		public int compare(IDAO o1, IDAO o2) {
			return o1.getEntityClassName().compareTo(o2.getEntityClassName());
		}
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testGetAllDAOs() throws BusinessException {
		List<IDAO> daoList = new ArrayList<IDAO>();
		daoList.addAll(this.serviceManager.getEntityManager().getDaoManager().getDaos().values());
		Collections.sort(daoList, new DaoSortComparator());
		/* para spring-beans.xml */
		System.out.println("\nspring-beans.xml");
		for(IDAO dao:daoList){	
			System.out.println("<value>" + dao.getEntityClassName() + "</value>");
		}

		/* para GerarTabelas.java */
		System.out.println("\nGerarTabelas.java");
		for(IDAO dao:daoList){	
			System.out.println("cfg.addAnnotatedClass(" + dao.getEntityClassName() + ".class);");
		}
	}

}
