package br.com.orionsoft.monstrengo.core.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.service.ValueObject;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

/**
 * Esta classe é usada para manter uma lista de propriedades com seus valores.
 * Métodos <code>setProperty()</code> e <code>getProperty()</code> são usados para acessar as propriedades.  
 * @author Lucio
 *
 */public class ValueObject
{
    private Map<String, Object> properties = new HashMap<String, Object>();
    
    public void setProperty(String name, Object value)
    {
        properties.put(name, value);
    }
    
    public Object getProperty(String name) throws ServiceException
    {
        if (!containsProperty(name))
            throw new ServiceException(MessageList.create(ServiceException.class, "PROPERTY_NOT_FOUND", name));
        
        return properties.get(name);
    }
    
    public boolean containsProperty(String name)
    {
        
        return properties.containsKey(name);
    }
    
    public Map<String, Object> getProperties()
    {
        return properties;
    }

    /**
     * Permite reutilizar os argumentos de um serviço para outro,
     * sem ter que ficar passando argumento por argumento.
     * 
     * @author antonio
     * @since 20060911
     * @param valueObject Lista de argumentos origem que serão copiados
     */
    public void addAll(ValueObject valueObject) {
        for (Entry<String, Object> entry: valueObject.getProperties().entrySet()){
            this.properties.put(entry.getKey(), entry.getValue());
        }
    }
}
