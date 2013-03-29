
package br.com.orionsoft.monstrengo.crud.entity.metadata;

import java.util.List;

import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;



public interface IMetadataHandle {
    public String getEntityLabel() throws MetadataException;

    public String getEntityHint() throws MetadataException;

    public String getEntityDescription() throws MetadataException;

    public String getEntityColorName() throws MetadataException;

    public boolean getEntityCanCreate() throws MetadataException;
    
    public boolean getEntityCanRetrieve() throws MetadataException;
    
    public boolean getEntityCanUpdate() throws MetadataException;
    
    public boolean getEntityCanDelete() throws MetadataException;

    public boolean getEntityRunQueryOnOpen() throws MetadataException;

    public List<String> getPropertiesInQueryGrid() throws MetadataException;

    /**
     *  
     * @since 20060413
     */
    public List<IGroupMetadata> getEntityGroups() throws MetadataException;
    
    public String getPropertyLabel(String propertyName) throws MetadataException;

    public String getPropertyHint(String propertyName) throws MetadataException;

    public String getPropertyDescription(String propertyName) throws MetadataException;

    public Class<?> getPropertyType(String propertyName) throws MetadataException;

    public boolean getPropertyRequired(String propertyName) throws MetadataException;

    public boolean getPropertyReadOnly(String propertyName) throws MetadataException;
    
    public boolean getPropertyCalculated(String propertyName) throws MetadataException;

    public boolean getPropertyVisible(String propertyName) throws MetadataException;

    public boolean getPropertyHtml(String propertyName) throws MetadataException;

    public int getPropertySize(String propertyName) throws MetadataException;

    public double getPropertyMinimum(String propertyName) throws MetadataException;

    public double getPropertyMaximum(String propertyName) throws MetadataException;

    public String getPropertyColorName(String propertyName) throws MetadataException;

    public String getPropertyEditMask(String propertyName) throws MetadataException;

    public boolean getPropertyEditShowList(String propertyName) throws MetadataException;

    public boolean getPropertyIsList(String propertyName) throws MetadataException;

    public boolean getPropertyIsSet(String propertyName) throws MetadataException;

    /**
     * @see IPropertyMetadata.isEditShowEmbedded();
     */
    public boolean getPropertyEditShowEmbedded(String propertyName) throws MetadataException;
    /**
     * @see IPropertyMetadata.isEmbedded();
     */
    public boolean getPropertyEmbedded(String propertyName) throws MetadataException;
    /**
     * @see IPropertyMetadata.isAllowSubQuery();
     */
    public boolean getPropertyAllowSubQuery(String propertyName) throws MetadataException;

    /**
     * Busca a lista que deverá ser mostrada na edição. 
     * @param propertyName = propriedade a ser pesquisada
     * @return lista de elementos
     */
    public List<String> getPropertyValuesList(String propertyName) throws MetadataException;

    public String getPropertyDisplayFormat(String propertyName) throws MetadataException;
    
    public String getPropertyDefaultValue(String propertyName) throws MetadataException;
    
    public int getPropertyIndex(String propertyName) throws MetadataException;

    
    /**
     * Obtem o índice do grupo ao qual esta propriedade pertence   
     * @since 20060413
     */
    public int getPropertyGroup(String propertyName) throws MetadataException;

    public String getPropertyName(String propertyName) throws MetadataException;

    /**
     * Define para o gerenciador de metadados preparar os metadados de uma determinada entidade.
     * Este método não permite escolher entre pegar o valor padrão ou do banco. Ele utiliza um algoritmo 
     * para buscar no banco e se não achar, busca no .properties
     * @param entityClass
     * @throws MetadataException
     */
    public void setEntityClass(Class<?> entityClass) throws MetadataException;
    
    /**
     * Define para o gerenciador de metadados preparar os metadados de uma determinada entidade e possibilita
     * indicar o modo de operação.
     * Este método permite escolher entre pegar o valor padrão SEMPRE ou não.
     * @param entityClass
     * @throws MetadataException
     */
    public void setEntityClass(Class<?> entityClass, boolean defaultMode) throws MetadataException;
    public Class<?> getEntityClass();
    
    

}