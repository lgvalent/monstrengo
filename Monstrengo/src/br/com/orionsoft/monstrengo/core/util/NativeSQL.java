package br.com.orionsoft.monstrengo.core.util;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import br.com.orionsoft.monstrengo.core.util.CalendarUtils;


public class NativeSQL {
    private Session session;
    private String sql;
    private String select;
    private String where;
    private String group;
    private String order;
    private String having;

    public NativeSQL(Session session, String sql) {
        super();
        this.session = session;
        this.sql = sql;
    }
    
    public NativeSQL(Session session, String select, String where, String having, String group, String order) {
        super();
        this.session = session;
        this.select = select;
        this.where = where;
        this.having = having;
        this.group = group;
        this.order = order;
        this.mountSql();
    }
    
    private void mountSql() {
        this.sql = "";
        if (this.select != null)
        	this.sql = this.sql.concat(this.select);
        if (this.where != null)
        	this.sql = this.sql.concat(" ").concat(this.where);
        if (this.group != null)
        	this.sql = this.sql.concat(" ").concat(this.group);
        if (this.having != null)
        	this.sql = this.sql.concat(" ").concat(this.having);
        if (this.order != null)
        	this.sql = this.sql.concat(" ").concat(this.order);
    }
    
    public void setGroup(String value) {
    	this.group = "group by ".concat(value);
    	this.mountSql();
    }
    
    public void addGroup(String value) {
    	if (this.group == null)
    		this.group = "group by ";
    	else
    		this.group = this.group.concat(", ");
    	this.group = this.group.concat(value);
    	this.mountSql();
    }
    
    public void addWhere(String value) {
    	if (this.where == null)
    		this.where = "where ";
    	else 
    		this.where = this.where.concat(" and ");
    	this.where = this.where.concat(value);
    	this.mountSql();
    }
    
    public void addOrder(String value) {
    	if (this.order == null)
    		this.order = "order by ";
    	else 
    		this.order = this.order.concat(", ");
    	this.order = this.order.concat(value);
    	this.mountSql();
    }
    
    public void setString(String parameter, String value) {
        parameter = ":".concat(parameter);
        value = "'".concat(value).concat("'");
        this.where = StringUtils.replace(this.where, parameter, value);
        this.sql = StringUtils.replace(this.sql, parameter, value);
    }
    
    public void setParameter(String parameter, String value) {
        parameter = ":".concat(parameter);
        this.where = StringUtils.replace(this.where, parameter, value);
        this.sql = StringUtils.replace(this.sql, parameter, value);
    }
    
    public void setSelect(String parameter, String field, String table) {
        parameter = ":".concat(parameter);
        String value = "select ".concat(field).concat(" from ").concat(table);
        this.select = StringUtils.replace(this.select, parameter, value);
        this.sql = StringUtils.replace(this.sql, parameter, value);
    }
    
    public void setSelectFields(String parameter, String fields) {
        parameter = ":".concat(parameter);
        this.select = StringUtils.replace(this.select, parameter, fields);
        this.sql = StringUtils.replace(this.sql, parameter, fields);
    }
    
    public void setInteger(String parameter, Integer value) {
        parameter = ":".concat(parameter);
        this.where = StringUtils.replace(this.where, parameter, value.toString());
        this.sql = StringUtils.replace(this.sql, parameter, value.toString());
    }
    
    public void setLong(String parameter, Long value) {
        parameter = ":".concat(parameter);
        this.where = StringUtils.replace(this.where, parameter, value.toString());
        this.sql = StringUtils.replace(this.sql, parameter, value.toString());
    }
    
    public void setCalendar(String parameter, Calendar value) {
        parameter = ":".concat(parameter);
        String date = "'".concat(CalendarUtils.formatToSQLDate(value).concat("'"));
        this.where = StringUtils.replace(this.where, parameter, date);
        this.sql = StringUtils.replace(this.sql, parameter, date);
    }
    
    public void setId(String parameter, Object[] value, Class<?> klass) {
        parameter = ":".concat(parameter);
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            if (i > 0)
                values.append(", ");
            Long id = null;
            try {
            	Object object = value[i];
            	klass.cast(object);
				Method method = object.getClass().getDeclaredMethod("getId", klass);
				id = (Long)method.invoke(null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("==> ID: "+id);
            values.append(value[i].toString());
        }
        this.where = StringUtils.replace(this.where, parameter, values.toString());
        this.sql = StringUtils.replace(this.sql, parameter, values.toString());
    }
    
    public void setArrayObject(String parameter, Object[] value) {
        parameter = ":".concat(parameter);
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            if (i > 0)
                values.append(", ");
            values.append("'"+value[i].toString()+"'");
        }
        this.where = StringUtils.replace(this.where, parameter, values.toString());
        this.sql = StringUtils.replace(this.sql, parameter, values.toString());
    }
    
    public void setArrayString(String parameter, String[] value) {
        parameter = ":".concat(parameter);
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            if (i > 0)
                values.append(", ");
            values.append(value[i].toString());
        }
        this.where = StringUtils.replace(this.where, parameter, values.toString());
        this.sql = StringUtils.replace(this.sql, parameter, values.toString());
    }
    
    public void setArrayInteger(String parameter, Integer[] value) {
        parameter = ":".concat(parameter);
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            if (i > 0)
                values.append(", ");
            values.append(value[i].toString());
        }
        this.where = StringUtils.replace(this.where, parameter, values.toString());
        this.sql = StringUtils.replace(this.sql, parameter, values.toString());
    }
    
    public void setArrayLong(String parameter, Long[] value) {
        parameter = ":".concat(parameter);
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            if (i > 0)
                values.append(", ");
            values.append(value[i].toString());
        }
        this.where = StringUtils.replace(this.where, parameter, values.toString());
        this.sql = StringUtils.replace(this.sql, parameter, values.toString());
    }
    
    public void setListLong(String parameter, List<Long> value) {
        parameter = ":".concat(parameter);
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < value.size(); i++) {
            if (i > 0)
                values.append(", ");
            values.append(value.get(i).toString());
        }
        this.where = StringUtils.replace(this.where, parameter, values.toString());
        this.sql = StringUtils.replace(this.sql, parameter, values.toString());
    }
    
    public String getSql() {
        return this.sql;
    }
    
    public ResultSet executeQuery() throws HibernateException, SQLException {
        Statement statement = this.session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
//        if (this.sql == null) {
//        	this.sql = "";
//        	this.sql.concat(this.select).concat(this.where).concat(this.order).concat(this.group).concat(this.having);
//        }
        statement.executeQuery(this.sql);
        ResultSet rs = statement.getResultSet();
        return rs;
    }
    
    public ResultSet executeUpdate() throws HibernateException, SQLException {
        Statement statement = this.session.connection().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.executeUpdate(this.sql);
        ResultSet rs = statement.getResultSet();
        return rs;
    }
    
}
