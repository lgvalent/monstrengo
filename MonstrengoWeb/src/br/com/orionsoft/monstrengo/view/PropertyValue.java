package br.com.orionsoft.monstrengo.view;

import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * 
 * Created on 24/05/2005
 * @author Marcia
 */
public class PropertyValue
{
    private Object value;
    
    public long getObjectId() throws Exception
    {
        // Pega a propriedade 'id' da subClasse para saber a qual objeto se refere o campo
        return (Long) PropertyUtils.getProperty(value, IDAO.PROPERTY_ID_NAME);
    }
    
    public String getAsString(){return value.toString();};
    public int getAsInteger(){return 0;};
    public double getAsDouble(){return 0;};
    public long getAsLong(){return 0;};
    public Date getAsDate(){return null;};
}
