package br.com.orionsoft.monstrengo.view;

import java.util.Date;

import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Classe que armazena valores dinâmicos de uma propriedade
 * 
 * Created on 01/04/2005
 * @author Marcia
 */
public class EntityPropertyValue
{
    
    private IServiceManager app; // Utilizado para validar o id e os valores atraves dos servicos da aplicação
    private EntityPropertyInfo info;
    
    private Object object;
    private long id = IDAO.ENTITY_UNSAVED;
    private long oldId = IDAO.ENTITY_UNSAVED;

    /////////////////////////////////////////////////////////////////////////////
    // Propriedades de Edição
    /////////////////////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////
    // CONSTRUTOR
    // ///////////////////////////////////////////////////////////
    public EntityPropertyValue(IServiceManager app,  EntityPropertyInfo entityPropertyInfo)
    {
        this.app = app;
        this.info = entityPropertyInfo;
    }

    // Esta propriedade não pôde se chamar null ou empty, dava conflito com o
    // JSF
    /* (non-Javadoc)
     * @see br.com.orionsoft.monstrengo.view.IPropertyValue#isValueNull()
     */
    public boolean isValueNull(){return (object == null) || (object.toString().equals(""));}
    /* (non-Javadoc)
     * @see br.com.orionsoft.monstrengo.view.IPropertyValue#modified()
     */
    public boolean modified(){return id!=oldId;}

    /* (non-Javadoc)
     * @see br.com.orionsoft.monstrengo.view.IPropertyValue#getId()
     */
    public long getId(){return id;}
    /* (non-Javadoc)
     * @see br.com.orionsoft.monstrengo.view.IPropertyValue#setId(long)
     */
    public void setId(long id) throws Exception
    {
        // Verifica se o Id foi alterado
        if (this.id != id)
        {
            // Busca a nova entidade com o id passado
//            ICrudService service = (ICrudService) app.getServiceByName(ICrudService.SERVICE_NAME);
            
            // Atualiza o Object
//            this.object = service.retrieve(id, info.getType());

            // Verifica se é o primeiro povoamento id
            if (this.oldId == IDAO.ENTITY_UNSAVED)
            {
                // 1ª ALTERAÇÂO
                // Armazena o passado como ATUAL E ANTERIOR
                this.oldId = id;
                this.id = id;
            }
            else
            {
                // ALTERAÇÂO
                // Armazena o anterior e altera o atual
                this.oldId = this.id;
                this.id = id;
            }
        }
        
    }
    
    /* (non-Javadoc)
     * @see br.com.orionsoft.monstrengo.view.IPropertyValue#getValue()
     */
    public String getValue()
    {
        if (object == null)
            return "";

        return object.toString();
    }
    /* (non-Javadoc)
     * @see br.com.orionsoft.monstrengo.view.IPropertyValue#setValue(java.lang.String)
     */
    public void setValue(String valor)
    {
        if (info.isString()){object = valor;}else
        if (info.isInteger()){object = Integer.parseInt(valor);}else
        if (info.isFloat()){object = Float.parseFloat(valor);}else
        if (info.isDate()){object = Date.parse(valor);}
        if (info.isBoolean()){object = Boolean.parseBoolean(valor);}
        
//        public boolean isDate(){return (type == Date.class);}
//        public boolean isFloat(){return (type == Float.class);}
//        public boolean isInteger(){return (type == Integer.class) || (type == int.class)
//                    || (type == long.class);}
//        public boolean isString(){return (type == String.class);}
//        public boolean isList(){return (typeFromFile || (type == Collection.class));}
//        public boolean isSubClass(){return (!(isDate() || isFloat() || isInteger() 
    }
    
    /* (non-Javadoc)
     * @see br.com.orionsoft.monstrengo.view.IPropertyValue#setAsBoolean(boolean)
     */
    public void setAsBoolean(boolean valor){object = valor;}
    /* (non-Javadoc)
     * @see br.com.orionsoft.monstrengo.view.IPropertyValue#isAsBoolean()
     */
    public boolean isAsBoolean(){return (Boolean) object;}
    
    /* (non-Javadoc)
     * @see br.com.orionsoft.monstrengo.view.IPropertyValue#getObject()
     */
    public Object getObject() {return object;}
    /* (non-Javadoc)
     * @see br.com.orionsoft.monstrengo.view.IPropertyValue#setObject(java.lang.Object)
     */
    public void setObject(Object object) {this.object = object;}

    /* (non-Javadoc)
     * @see br.com.orionsoft.monstrengo.view.IPropertyValue#getInfo()
     */
    public EntityPropertyInfo getInfo() {
        return info;
    }
 
}
