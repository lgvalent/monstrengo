/**
 * 
 */
package br.com.orionsoft.monstrengo.crud.entity;

import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IPropertyValue;
import br.com.orionsoft.monstrengo.crud.entity.PropertyException;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;

/**
 * @author Lucio
 *
 */
public interface IProperty
{
    public IEntity<?> getEntityOwner();
    public IPropertyMetadata getInfo();
    public IPropertyValue getValue();

    public List<SelectItem> getValuesList() throws PropertyException;
    /**
     * Pesquisa no banco possíveis valores filtrados
     * pela string de filtro. Se a propriedade é do tipo entidade, pesquisa
     * as entidades possíveis, se vor valor primitivo, está indefinido o 
     * comportamento
     * @param filter
     * @return
     * @throws PropertyException
     */
    public List<SelectItem> getValuesList(String filter) throws PropertyException;
	public abstract List<IEntity<?>> getSelectValues(String filter)
			throws PropertyException;
}
