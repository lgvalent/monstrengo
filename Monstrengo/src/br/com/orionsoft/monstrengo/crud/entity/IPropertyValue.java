
package br.com.orionsoft.monstrengo.crud.entity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntitySet;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;

/**
 * TODO DOCUMENTAR essa interface
 * @author marcia
 *
 */
public interface IPropertyValue {

    public IProperty getPropetyOwner();
    
    // Esta propriedade não pôde se chamar null ou empty, dá conflito com o JSF
    public boolean isValueNull();
    public boolean isModified();
    public void setModified(boolean value);
    
    public Object getOldValue();
    
    public Long getId() throws PropertyValueException;
    public void setId(Long id) throws PropertyValueException;
    public void setId(Long id, ServiceData serviceDataOwner) throws PropertyValueException;

    /**
  	 * Este método permite acrescentar uma lista de ids (de entidade ou enum) de uma só vez na coleção.<br>
  	 * <b>OBSERVAÇÃO</b> Quando estiver dentro de um serviço é ideal que utilize o método setIds(Long[], ServiceData)
  	 * para preservar a sessão e transação do serviço.<br>
  	 * Muito útil para gerar uma lista de seleção com id<=>valor e utilizar os métodos de preenchimento
  	 * e seleção de interfaces.
  	 *  
  	 * @throws PropertyValueException 
  	 * 
  	 */
  	public void setIds(Long[] ids) throws BusinessException;
  	public void setIds(Long[] ids, ServiceData serviceDataOwner) throws BusinessException;
  	public Long[] getIds() throws BusinessException;

    public void setAsBoolean(Boolean value) throws PropertyValueException;
    public Boolean getAsBoolean() throws PropertyValueException;

    public void setAsInteger(Integer value) throws PropertyValueException;
    public Integer getAsInteger() throws PropertyValueException;
    
    public void setAsLong(Long value) throws PropertyValueException;
    public Long getAsLong() throws PropertyValueException;

    public void setAsDouble(Double value) throws PropertyValueException;
    public Double getAsDouble() throws PropertyValueException;

    public void setAsFloat(Float value) throws PropertyValueException;
    public Float getAsFloat() throws PropertyValueException;

    public void setAsBigDecimal(BigDecimal value) throws PropertyValueException;
    public BigDecimal getAsBigDecimal() throws PropertyValueException;

    public String getAsFormated() throws PropertyValueException;

    public String getAsString() throws PropertyValueException;
    public void setAsString(String value) throws PropertyValueException;

    public Calendar getAsCalendar() throws PropertyValueException;
    public void setAsCalendar(Calendar value) throws PropertyValueException;

    public <T> T getAsObject() throws PropertyValueException;
    public void setAsObject(Object object) throws PropertyValueException;

    public <T> IEntity<T> getAsEntity() throws PropertyValueException;
    public void setAsEntity(IEntity<?> entity) throws PropertyValueException;
    
    public <T> IEntityList<T> getAsEntityList() throws PropertyValueException;
    public void setAsEntityList(IEntityList<?> entityList) throws PropertyValueException;

    public <T> IEntitySet<T> getAsEntitySet() throws PropertyValueException;
    public void setAsEntitySet(IEntitySet<?> entitySet) throws PropertyValueException;

    public <T> IEntityCollection<T> getAsEntityCollection() throws PropertyValueException;
    public void setAsEntityCollection(IEntityCollection<?> entityCollection) throws PropertyValueException;

    public <T> List<T> getAsPrimitiveList() throws PropertyValueException;
    public void setAsPrimitiveList(List<?> primitiveList) throws PropertyValueException;

    public <T> Set<T> getAsPrimitiveSet() throws PropertyValueException;
    public void setAsPrimitiveSet(Set<?> primitiveSet) throws PropertyValueException;

    public <T> Collection<T> getAsPrimitiveCollection() throws PropertyValueException;

    /** 
     * Diz para a propriedade limpar os seus buffers pois pode haver um novo objeto
	 * na entidade. Isto é utilizando principalmente quando a entidade é trocada
	 * pelo método IEntity.setId(lLong). E evita que o novo objeto trabalhe com 
	 * antigos dados.
	 * @since 20071018
	 * @author lucio 20071018
	 */
	public void flush();
	
	/**
	 * Restaura o valor anterior a modificação se o mesmo recebeu um novo valor.
	 * @return True se houve uma restauração e False se a propriedade não tinha sido modificada.
	 * @author lucio 20100411
	 */
	public boolean restoreOldValue() throws PropertyValueException;

}