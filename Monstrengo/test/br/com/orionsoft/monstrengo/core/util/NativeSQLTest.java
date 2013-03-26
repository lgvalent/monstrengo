package br.com.orionsoft.monstrengo.core.util;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.NativeSQL;

public class NativeSQLTest extends ServiceBasicTest {
    private Session session;
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.session = serviceManager.getEntityManager().getDaoManager().getSessionFactory().openSession();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    @Test
    public void create() {
    	NativeSQL nativeSQL = new NativeSQL(this.session, "select * from ");
    	assertEquals("select * from ", nativeSQL.getSql());
    }

    @Test
    public void setString() {
        NativeSQL nativeSQL = new NativeSQL(this.session, "where nome = :nome");
        nativeSQL.setString("nome", "Teste");
        assertEquals("where nome = 'Teste'", nativeSQL.getSql());
    }

    @Test
    public void setLong() {
        NativeSQL nativeSQL = new NativeSQL(this.session, "where id = :id");
        nativeSQL.setLong("id", new Long(1));
        assertEquals("where id = 1", nativeSQL.getSql());
    }

    @Test
    public void setCalendar() {
        NativeSQL nativeSQL = new NativeSQL(this.session, "where data = :data");
        Calendar data = CalendarUtils.getCalendar(2006, Calendar.DECEMBER, 25);
        nativeSQL.setCalendar("data", data);
        assertEquals("where data = '2006-12-25'", nativeSQL.getSql());
    }

    @Test
    public void setArrayObject() {
        NativeSQL nativeSQL = new NativeSQL(this.session, "where obj in (:obj)");
        EnumTest[] obj = {EnumTest.ENUM1, EnumTest.ENUM2};
        nativeSQL.setArrayObject("obj", obj);
        assertEquals("where obj in ('ENUM1', 'ENUM2')", nativeSQL.getSql());
    }
    
//    @Test
    public void setId() {
        NativeSQL nativeSQL = new NativeSQL(this.session, "where obj in (:obj)");
        EnumTest[] obj = {EnumTest.ENUM1, EnumTest.ENUM2};
        nativeSQL.setId("obj", obj, EnumTest.class);
        assertEquals("where obj in (1, 2)", nativeSQL.getSql());
    }
    
    @Test
    public void setArrayString() {
        NativeSQL nativeSQL = new NativeSQL(this.session, "where str in (:str)");
        String[] str = {"STR1", "STR2"};
        nativeSQL.setArrayString("str", str);
        assertEquals("where str in (STR1, STR2)", nativeSQL.getSql());
    }
    
    @Test
    public void setArrayLong() {
        NativeSQL nativeSQL = new NativeSQL(this.session, "where id in (:id)");
        Long[] ids = {new Long(1), new Long(2)};
        nativeSQL.setArrayLong("id", ids);
        assertEquals("where id in (1, 2)", nativeSQL.getSql());
    }
    
    @Test
    public void setListLong() {
        NativeSQL nativeSQL = new NativeSQL(this.session, "where id in (:id)");
        List<Long> ids = new ArrayList<Long>(0);
        ids.add(1l);
        ids.add(2l);
        nativeSQL.setListLong("id", ids);
        assertEquals("where id in (1, 2)", nativeSQL.getSql());
    }
    
//    @Test
    public void execute() throws HibernateException, SQLException {
        NativeSQL nativeSQL = new NativeSQL(this.session, "select * from financeiro_conta");
        nativeSQL.executeQuery();
    }
    
    @Test
    public void addOrder() {
    	NativeSQL sql = new NativeSQL(
    			this.session,
    			"select * from financeiro_conta",
    			"where (true)",
    			null,
    			null,
    			null);
    	sql.addOrder("id");
    	assertEquals("select * from financeiro_conta where (true) order by id", sql.getSql());
    	sql.addOrder("nome");
    	assertEquals("select * from financeiro_conta where (true) order by id, nome", sql.getSql());
    }
    
    @Test
    public void addWhere() {
    	NativeSQL nativeSQL = new NativeSQL(
    			this.session, 
    			"select * from financeiro_conta", 
    			"where (true)",
    			"order by nome",
    			null,
    			null);
    	nativeSQL.addWhere("id = 1");
    	assertEquals("select * from financeiro_conta where (true) and id = 1 order by nome", nativeSQL.getSql());

    	nativeSQL = new NativeSQL(
    			this.session, 
    			"select * from financeiro_conta", 
    			null,
    			"order by nome",
    			null,
    			null);
    	nativeSQL.addWhere("(id = :id)");
    	nativeSQL.setInteger("id", 1);
    	nativeSQL.addWhere("(data between '2007-01-01' and '2007-01-31')");
    	assertEquals("select * from financeiro_conta where (id = 1) and (data between '2007-01-01' and '2007-01-31') order by nome", nativeSQL.getSql());
    }
    
    @Test
    public void addGroup() {
    	String select = "select * from financeiro_conta";
    	NativeSQL ns = new NativeSQL(
    			this.session, 
    			select,
    			null,
    			null,
    			null,
    			null);
    	ns.addGroup("nome");
    	assertEquals(select + " group by nome", ns.getSql());
    }
    
    @Test
    public void setGroup() {
    	String select = "select * from financeiro_conta";
    	NativeSQL ns = new NativeSQL(
    			this.session, 
    			select,
    			null,
    			null,
    			null,
    			null);
    	ns.setGroup("nome, categoria");
    	assertEquals(select + " group by nome, categoria", ns.getSql());
    }

}
