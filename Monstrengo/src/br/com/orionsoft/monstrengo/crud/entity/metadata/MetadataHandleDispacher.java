package br.com.orionsoft.monstrengo.crud.entity.metadata;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IMetadataHandle;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataHandle;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.MetadataHandleXmlImpl;

/**
 * Esta classe mantem compatibilidade entre os dois tipos de metadados: .properties e .info.xml.
 * 
 * Created on 20/06/2011
 * @author Lucio
 * @version 20110620
 * 
 * @spring.bean id="MetadataHandle"
 * @spring.property name="metadataHandleTxt" ref="MetadataHandleTxt"
 * @spring.property name="metadataHandleXml" ref="MetadataHandleXml"
 */

public class MetadataHandleDispacher implements IMetadataHandle 
{
    protected Logger log = LogManager.getLogger(this.getClass());
    
    
    private MetadataHandle metadataHandleTxt = null;
    private MetadataHandleXmlImpl metadataHandleXml = null;
    private boolean metadataHandleXmlActive = false;
    

    private IMetadataHandle getHandle(){
    	if(this.metadataHandleXmlActive)
    		return this.metadataHandleXml;
    	
    	return this.metadataHandleTxt;
    }
	public void setEntityClass(Class<?> entityClass, boolean defaultMode) 
    {
    	/* Verifica se existe um .properties ou um .info.xml para determinar qual Handle usar */
    	if(entityClass.getResourceAsStream(entityClass.getSimpleName() + MetadataHandleXmlImpl.INFO_XML_FILE_EXTENSION)!=null){
    		this.metadataHandleXmlActive = true;
    		this.metadataHandleXml.setEntityClass(entityClass, defaultMode);
    	}else{
    		this.metadataHandleXmlActive = false;
    		this.metadataHandleTxt.setEntityClass(entityClass, defaultMode);
    	}
    }
    
	
    public Class<?> getEntityClass() {
    	return getHandle().getEntityClass();
    }
    
	
    public void setEntityClass(Class<?> entityClass){
    	setEntityClass(entityClass, false);
    }
	
	
	public MetadataHandle getMetadataHandleTxt() {return metadataHandleTxt;}
	public void setMetadataHandleTxt(MetadataHandle metadataHandleTxt) {this.metadataHandleTxt = metadataHandleTxt;}
	
	public MetadataHandleXmlImpl getMetadataHandleXml() {return metadataHandleXml;}
	public void setMetadataHandleXml(MetadataHandleXmlImpl metadataHandleXml) {this.metadataHandleXml = metadataHandleXml;}

	
	public String getEntityLabel() throws MetadataException {
		return getHandle().getEntityLabel();
	}

	
	public String getEntityHint() throws MetadataException {
		return getHandle().getEntityHint();
	}

	
	public String getEntityDescription() throws MetadataException {
		return getHandle().getEntityDescription();
	}

	
	public String getEntityColorName() throws MetadataException {
		return getHandle().getEntityColorName();
	}

	
	public boolean getEntityCanCreate() throws MetadataException {
		return getHandle().getEntityCanCreate();
	}

	
	public boolean getEntityCanRetrieve() throws MetadataException {
		return getHandle().getEntityCanRetrieve();
	}
    /* Estes métodos permitem a injeção pelo Spring dos HandlesVerdadeiros */
	
	public boolean getEntityCanUpdate() throws MetadataException {
		return getHandle().getEntityCanUpdate();
	}

	
	public boolean getEntityCanDelete() throws MetadataException {
		return getHandle().getEntityCanDelete();
	}

	public boolean getEntityCanQuery() throws MetadataException {
		return getHandle().getEntityCanQuery();
	}

	
	public boolean getEntityRunQueryOnOpen() throws MetadataException {
		return getHandle().getEntityRunQueryOnOpen();
	}

	
	public List<String> getPropertiesInQueryGrid() throws MetadataException {
		return getHandle().getPropertiesInQueryGrid();
	}

	
	public List<IGroupMetadata> getEntityGroups() throws MetadataException {
		return getHandle().getEntityGroups();
	}

	
	public String getPropertyLabel(String propertyName) throws MetadataException {
		return getHandle().getPropertyLabel(propertyName);
	}

	
	public String getPropertyHint(String propertyName) throws MetadataException {
		return getHandle().getPropertyHint(propertyName);
	}

	
	public String getPropertyDescription(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyDescription(propertyName);
	}

	
	public Class<?> getPropertyType(String propertyName) throws MetadataException {
		return getHandle().getPropertyType(propertyName);
	}

	
	public boolean getPropertyRequired(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyRequired(propertyName);
	}

	
	public boolean getPropertyReadOnly(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyReadOnly(propertyName);
	}

	
	public boolean getPropertyCalculated(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyCalculated(propertyName);
	}

	
	public boolean getPropertyVisible(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyVisible(propertyName);
	}

	
	public int getPropertySize(String propertyName) throws MetadataException {
		return getHandle().getPropertySize(propertyName);
	}

	
	public double getPropertyMinimum(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyMinimum(propertyName);	}

	
	public double getPropertyMaximum(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyMaximum(propertyName);	}

	
	public String getPropertyColorName(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyColorName(propertyName);	}

	
	public String getPropertyEditMask(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyEditMask(propertyName);	}

	
	public boolean getPropertyEditShowList(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyEditShowList(propertyName);
		}

	
	public boolean getPropertyIsList(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyIsList(propertyName);	}

	
	public boolean getPropertyIsSet(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyIsSet(propertyName);	}

	
	public boolean getPropertyEditShowEmbedded(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyEditShowEmbedded(propertyName);	}

	
	public boolean getPropertyEmbedded(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyEmbedded(propertyName);	}

	
	public boolean getPropertyAllowSubQuery(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyAllowSubQuery(propertyName);	}

	
	public List<String> getPropertyValuesList(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyValuesList(propertyName);	}

	
	public String getPropertyDisplayFormat(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyDisplayFormat(propertyName);	}

	
	public String getPropertyDefaultValue(String propertyName)
			throws MetadataException {
		return getHandle().getPropertyDefaultValue(propertyName);	}

	
	public int getPropertyIndex(String propertyName) throws MetadataException {
		return getHandle().getPropertyIndex(propertyName);	}

	
	public int getPropertyGroup(String propertyName) throws MetadataException {
		return getHandle().getPropertyGroup(propertyName);	}

	
	public String getPropertyName(String propertyName) throws MetadataException {
		return getHandle().getPropertyName(propertyName);	}

}

