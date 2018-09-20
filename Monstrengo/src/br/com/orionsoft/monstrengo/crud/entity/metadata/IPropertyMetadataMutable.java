/**
 *
 */
package br.com.orionsoft.monstrengo.crud.entity.metadata;

import java.util.List;

/**
 * @author Lucio
 * @version 20180920
 *
 */
public interface IPropertyMetadataMutable extends IPropertyMetadata, Cloneable
{

	public void setEntity(IEntityMetadata entity);

	public void setIndex(int index);

	public void setGroup(int group);

	public void setName(String name);

	public void setLabel(String label);

	public void setHint(String hint);

	public void setDescription(String description);

	public void setType(Class<?> type);
	
	public void setColorName(String colorName);

	public void setDisplayFormat(String displayFormat);
	
	public void setEditMask(String editMask);

	public void setSize(int size);
	
	public void setMaximum(double maximum);

	public void setMinimum(double minimum);
	
	public void setReadOnly(boolean readOnly);

	public void setCalculated(boolean calculated);

	public void setVisible(boolean visible);
	
	public void setRequired(boolean required);

	public void setValuesList(List<String> valuesList);
	
	public void setEditShowList(boolean editShowList);

	public void setList(boolean isList);
	
	public void setSet(boolean isSet);

	public void setDefaultValue(String defaultValue);
	
	public void setEmbedded(boolean embedded);
	
	public void setAllowSubQuery(boolean allowSubQuery);
	
	public IPropertyMetadataMutable clone() throws CloneNotSupportedException; 
}
