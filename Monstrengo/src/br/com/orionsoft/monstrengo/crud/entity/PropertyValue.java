package br.com.orionsoft.monstrengo.crud.entity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;

import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntitySet;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.IPropertyValue;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueFormat;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * TODO DOCUMENTAR essa classe
 *
 * @author Lucio
 *
 * TODO IMPLEMENTAR Criar metodos GetAsFormated()
 */
public class PropertyValue implements IPropertyValue
{
	private IProperty propertyOwner;

	private Object oldValue = null;

	private boolean modified = false;

	// Para otimizar o acesso ao nome da propriedade.
	private String propertyName;

	/**
	 * @param propertyOwner
	 *            Indica a propriedade que � manipulada por esta classe.
	 */
	public PropertyValue(IProperty propertyOwner)
	{
		this.propertyOwner = propertyOwner;

		this.propertyName = this.propertyOwner.getInfo().getName();
	}

	/**
	 * Se a propriedade for uma cole��o, ent�o este m�todo analisa se h�
	 * elementos na cole��o, se n�o houver, ele retornar� que a propriedade �
	 * nula. Mesmo que haja um objeto de Collection.
	 */
	public boolean isValueNull()
	{
		try {
			return getPropertyValue() == null
					|| (getPropetyOwner().getInfo().isCollection()
							&& getPropetyOwner().getInfo().isEntity() && getAsEntityCollection()
							.size() == 0)
					|| (getPropetyOwner().getInfo().isCollection()
							&& getPropetyOwner().getInfo().isPrimitive() && getAsPrimitiveCollection()
							.size() == 0);
		} catch (PropertyValueException e) {
			return true;
		}
	}

	public boolean isModified(){return modified;}
	public void setModified(boolean modified) {this.modified = modified;}

	/**
	 * Obtem o valor objeto da propriedade e tenta extrair o ID deste objeto.
	 */
	public Long getId() throws PropertyValueException
	{
		Object obj = getPropertyValue();

		try
		{
			Long result = null;
			/* Verifica se n�o � nulo para retornar null */
			if (obj != null){
				/* Verificando se a propiedade � do tipo Enum para retornar o ordinal da classe Enum */
				if(propertyOwner.getInfo().isEnum()){
					result = new Long(((Enum) obj).ordinal());
				}else
					// Verifica se � uma entidade pra tentar obter o Id dela
					if (propertyOwner.getInfo().isEntity())
					{
						result =  (Long) PropertyUtils.getProperty(obj, IDAO.PROPERTY_ID_NAME);
					}else
						wrongType(IEntity.class);
			}

			return result;

		} catch (PropertyValueException e)
		{
			e.getErrorList().addAll(
					MessageList.create(PropertyValueException.class, "GET_ID",
							propertyName, propertyOwner.getInfo().getType().getSimpleName(),
							propertyOwner.getEntityOwner().getInfo().getLabel()));
			throw e;
		} catch (Exception exception)
		{
			PropertyValueException e = new PropertyValueException(MessageList
					.createSingleInternalError(exception));
			e.getErrorList().addAll(
					MessageList.create(PropertyValueException.class, "GET_ID",
							propertyName, propertyOwner.getInfo().getType().getSimpleName(),
							propertyOwner.getEntityOwner().getInfo().getLabel()));
			throw e;
		}
	}

    /**
     * A cada Id apresentado, o objeto � buscado no DAO respons�vel e armazenado
     * <b>OBSERVA��O</b> Quando estiver dentro de um servi�o e ideal que utilize o m�todo setId(Long, ServiceData)
     * para preservar a sess�o e transa��o do servi�o.
     *
     * @throws PropertyValueException
     *
     */public void setId(Long id) throws PropertyValueException
     {
    	 setId(id, null);
     }

     /**
      * Este m�todo � compat�vel com propriedades que respondam isEntity() ou isEnum().<br>
      * A cada Id apresentado, o objeto � buscado no DAO respons�vel e armazenado
      * Este m�todo utiliza o encanamento de sess�o e transa��o definidos pelo seviceDataOwner
      * assim, de dentro de servi�os, este m�todo pode ser utilizado sem que o objeto definido
      * crie outra sess�o diferente da sess�o na qual o servi�o est� sendo executado.<br>
      * Quando a propriedade for do tipo ENUM ent�o o id esperado � o ordinal do tipo.
      * Uma lista com o ordinal e o to String dispon�vel � gerada pela classe Property.getValuesList();
      *
      * @throws PropertyValueException
      *
      */public void setId(Long id, ServiceData serviceDataOwner) throws PropertyValueException
     {
    	  try
    	  {
    		  // Varifica se o id � nullo
    		  if (id==null || id==IDAO.ENTITY_UNSAVED)
    			  setPropertyValue(null);
    		  else
    		  {
    			  /* Verificando se a propiedade � do tipo Enum para tratar o id recebido como o ordinal da classe Enum e n�o ir buscar no banco */
    			  if(propertyOwner.getInfo().isEnum()){
    				  setAsObject(propertyOwner.getInfo().getType().getEnumConstants()[new Long(id).intValue()]);
    			  }else
    				  if(propertyOwner.getInfo().isEntity()){
    					  /* Busca o objeto correspondente com o id no banco de dados */
    					  IEntity<?> ent = UtilsCrud.retrieve(this.getPropetyOwner().getEntityOwner().getEntityManager().getServiceManager(),
    							  this.propertyOwner.getInfo().getType(),
    							  id,
    							  serviceDataOwner);
    					  setAsEntity(ent);
    				  }else
    					  wrongType(IEntity.class);

    		  }

    	  }
    	  catch(BusinessException e)
    	  {
    		  e.getErrorList().addAll(MessageList.create(PropertyValueException.class, "SET_ID",  Long.toString(id), propertyName, propertyOwner.getInfo().getType().getSimpleName(), propertyOwner.getEntityOwner().getInfo().getLabel()));
    		  throw new PropertyValueException(e.getErrorList());
    	  }
    	  catch(Exception e)
          {
       	   PropertyValueException e1 = new PropertyValueException(MessageList.createSingleInternalError(e));
       	   e1.getErrorList().addAll(MessageList.create(PropertyValueException.class, "SET_ID",  Long.toString(id), propertyName, propertyOwner.getInfo().getType().getSimpleName(), propertyOwner.getEntityOwner().getInfo().getLabel()));
              throw e1;
          }
     }

  	/**
  	 * Obtem a lista de valores dos id's dos objetos que est�o na lista.<br>
       * Este m�todo � �til para que a pr�pria interface exiba uma lista
       * de op��o de entidades e defina na lista atual as entidades que foram
       * marcadas. 
  	 */
  	public Long[] getIds() throws BusinessException
  	{
  		/* Verificando se a propiedade � do tipo Collection para suportar uma lista de Ids */
		if(!propertyOwner.getInfo().isCollection())
			wrongType(Collection.class);

		Long[] result = null;

		try {
	  		/* Verificando se a propiedade � do tipo Enum para tratar os ids recebidos como o ordinal da classe Enum e n�o ir buscar no banco */
			if(propertyOwner.getInfo().isEnum()){
		  		Collection<Object> primitiveCollection =  this.getAsPrimitiveCollection();

		  		result = new Long[primitiveCollection.size()];
				int i = 0;

				for(Object item: primitiveCollection){
					result[i] = new Long(((Enum) item).ordinal());
					i++;
				}
			}
			else
		  		/* Verificando se a propiedade � do tipo Collection Enum para tratar os ids recebidos como o ordinal da classe Enum e n�o ir buscar no banco */
				if(propertyOwner.getInfo().isEntity())
					result = this.getAsEntityCollection().getIds();

  			return result;
  			
  		} catch (Exception e) {
  			throw new BusinessException(MessageList.createSingleInternalError(e));
  		}
  	}

  	/**
  	 * Este m�todo permite acrescentar uma lista de ids (de entidade ou enum) de uma s� vez na cole��o.<br>
  	 * <b>OBSERVA��O</b> Quando estiver dentro de um servi�o � ideal que utilize o m�todo setIds(Long[], ServiceData)
  	 * para preservar a sess�o e transa��o do servi�o.
  	 *  
  	 * @throws PropertyValueException 
  	 * 
  	 */
  	public void setIds(Long[] ids) throws BusinessException
  	{
  		setIds(ids, null);
  	}
  	
  	public void setIds(Long[] ids, ServiceData serviceDataOwner) throws BusinessException
  	{
  		/* Verificando se a propiedade � do tipo Collection para suportar uma lista de Ids */
		if(!propertyOwner.getInfo().isCollection())
			wrongType(Collection.class);
			
  		/* Verificando se a propiedade � do tipo Enum para tratar os ids recebidos como o ordinal da classe Enum e n�o ir buscar no banco */
		if(propertyOwner.getInfo().isEnum()){
	  		Collection<Object> primitiveCollection =  this.getAsPrimitiveCollection();

	  		/* Limpa as atuais entidades da lista para adicionar as entidades referenciadas pelos ids */ 
	  		primitiveCollection.clear();

			for(Long id:ids)
			  primitiveCollection.add(propertyOwner.getInfo().getType().getEnumConstants()[new Long(id).intValue()]);
		}else
	  		/* Verificando se a propiedade � do tipo Collection Enum para tratar os ids recebidos como o ordinal da classe Enum e n�o ir buscar no banco */
			if(propertyOwner.getInfo().isEntity())
				this.getAsEntityCollection().setIds(ids, serviceDataOwner);
  	}
  	
	public void setAsBoolean(Boolean value) throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isBoolean())
			wrongType(Boolean.class);

		setPropertyValue(value);

	}

	public Boolean getAsBoolean() throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isBoolean())
			wrongType(Boolean.class);

		return (Boolean) getPropertyValue();
	}

	public void setAsInteger(Integer value) throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isInteger())
			wrongType(Integer.class);

		setPropertyValue(value);
	}

	public Integer getAsInteger() throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isInteger())
			wrongType(Integer.class);

		return (Integer) getPropertyValue();
	}

	public void setAsLong(Long value) throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isLong())
			wrongType(Long.class);

		setPropertyValue(value);
	}

	public Long getAsLong() throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isLong())
			wrongType(Long.class);

		return (Long) getPropertyValue();
	}

	public void setAsDouble(Double value) throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isDouble())
			wrongType(Double.class);

		setPropertyValue(value);
	}

	public Double getAsDouble() throws PropertyValueException
	{

		if (!propertyOwner.getInfo().isNumber())
			wrongType(Double.class);

		if (isValueNull())
			return null;

		if (propertyOwner.getInfo().isDouble())
			return (Double) getPropertyValue();

		if (propertyOwner.getInfo().isBigDecimal())
			return getAsBigDecimal().doubleValue();

		if (propertyOwner.getInfo().isFloat())
			return getAsFloat().doubleValue();

		if (propertyOwner.getInfo().isLong())
			return getAsLong().doubleValue();

		if (propertyOwner.getInfo().isInteger())
			return getAsInteger().doubleValue();

		return null;
	}

	public void setAsFloat(Float value) throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isFloat())
			wrongType(Float.class);

		setPropertyValue(value);
	}

	public Float getAsFloat() throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isFloat())
			wrongType(Float.class);

		return (Float) getPropertyValue();
	}

	public void setAsBigDecimal(BigDecimal value) throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isBigDecimal())
			wrongType(BigDecimal.class);

		setPropertyValue(value);
	}

	public BigDecimal getAsBigDecimal() throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isBigDecimal())
			wrongType(BigDecimal.class);

		return (BigDecimal) getPropertyValue();
	}

	public void setAsString(String value) throws PropertyValueException
	{
		try
		{
			PropertyValueFormat.stringToValue(this, value);
		} catch (PropertyValueException e)
		{
			/* Adiciona a mensagem de falha da atividade local */
			e.getErrorList().addAll(
					MessageList
							.create(PropertyValueException.class,
									"SET_AS_STRING", propertyName,
									propertyOwner.getInfo().getType()
											.getSimpleName(), propertyOwner
											.getEntityOwner().getInfo()
											.getLabel()));
			throw e;
		}

	}

	public String getAsString() throws PropertyValueException
	{
		try
		{
			return PropertyValueFormat.valueToString(this);
		} catch (PropertyValueException e)
		{
			/* Adiciona a mensagem de falha da atividade local */
			e.getErrorList().addAll(
					MessageList
							.create(PropertyValueException.class,
									"GET_AS_STRING", propertyName,
									propertyOwner.getInfo().getType()
											.getSimpleName(), propertyOwner
											.getEntityOwner().getInfo()
											.getLabel()));
			throw e;
		}
	}

	public String getAsFormated() throws PropertyValueException
	{
		Object result = getPropertyValue();
		// Verifica se o valor � nulo para retornar uma String vazia
		if (result == null)
			return "";

		if (propertyOwner.getInfo().isString())
			return result.toString();
		else if (propertyOwner.getInfo().isCalendar())
			return new SimpleDateFormat("dd/MM/yyyy")
					.format(((Calendar) result).getTime());
		else if (propertyOwner.getInfo().isBigDecimal())
		{
			// Retira os separadores de milhar
			String temp = String.format("%,.2f", result);
			// Convert o ponto temporariamente para um sinal especial
			temp.replace(".", "_");
			// Convert virgula em ponto decimanl
			temp = temp.replace(",", ".");
			// No lugar onde esta o ponto coloca a v?rgula
			temp.replace("_", ",");

			return temp;
		} else if (propertyOwner.getInfo().isBoolean())
			return ((Boolean) result) ? "sim" : "nao";
		else if (propertyOwner.getInfo().isDouble())
			return ((Double) result).toString();
		else if (propertyOwner.getInfo().isLong())
			return ((Long) result).toString();
		else
			return result.toString();

	}

	public void setAsCalendar(Calendar value) throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isCalendar())
			wrongType(Calendar.class);

		setPropertyValue(value);

	}

	public Calendar getAsCalendar() throws PropertyValueException
	{
		if (!propertyOwner.getInfo().isCalendar())
			wrongType(Calendar.class);

		return (Calendar) getPropertyValue();
	}

	/**
	 * Este m�todo retorna a propriedade da entidade como um objeto. O tipo do
	 * objeto retornado � equivalente ao tipo declarado no metadados, por�m,
	 * tipos n�o primitivos s�o encapsulados em classes do tipo IEntity<?> e
	 * IEntityList
	 *
	 * @throws PropertyValueException
	 *
	 * @see IEntity
	 * @see IEntityList
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAsObject() throws PropertyValueException
	{
		try
		{
			T result = null;
			// 1 - � Primitivo
			if (propertyOwner.getInfo().isPrimitive())
				result = getPropertyValue();
			else
			// 2 - � Cole��o
			if (propertyOwner.getInfo().isCollection())
			{
				// 2.1 Lista de subclasse : Cria List<IEntity>
				result = (T) getAsEntityCollection();
			} else
			{
				// 2.2 - � Subclasse : Encapsula em IEntity
				result = (T) getAsEntity();
			}

			return result;
		} catch (PropertyValueException e)
		{
			e.getErrorList().addAll(
					MessageList
							.create(PropertyValueException.class,
									"GET_AS_OBJECT", propertyName,
									propertyOwner.getInfo().getType()
											.getSimpleName(), propertyOwner
											.getEntityOwner().getInfo()
											.getLabel()));
			throw e;
		}
	}

	public void setAsObject(Object object) throws PropertyValueException
    {
        try
        {
            // 1 - � Primitivo
            if (propertyOwner.getInfo().isPrimitive())
                setPropertyValue(object);
            else
                // 2 - � Entidade
            	if(object==null){
                    setPropertyValue(null);
            	}
            	else if (propertyOwner.getInfo().isCollection())
                {
                	// 2.1 � Subclasse cole��o: Pega Collection<Object> para
					// persistir
            		setAsEntityCollection((IEntityCollection)object);
                }
                else{
                    // 2.2 - � Subclasse �nica: Desencapsula a IEntity
                    setPropertyValue(((IEntity)object).getObject());
                }

        } catch (Exception exception)
        {
            PropertyValueException e = new PropertyValueException(MessageList.createSingleInternalError(exception));
            e.getErrorList().addAll(MessageList.create(PropertyValueException.class, "SET_AS_OBJECT", propertyName, propertyOwner.getInfo().getType().getSimpleName(), propertyOwner.getEntityOwner().getInfo().getLabel()));
            throw e;
        }
    }
	
	private IEntity<?> asEntityCache = null; 
	@SuppressWarnings("unchecked")
	public <T> IEntity<T> getAsEntity() throws PropertyValueException {
		if (!(propertyOwner.getInfo().isEntity() && !propertyOwner.getInfo().isList()))
			wrongType(IEntity.class);

		try{
			/* Verifica se um getAsEntity j� foi chamada para evitar sucessivas transforma��es */
			if(asEntityCache == null){

				// se o valor da propriedade � nullo retorna null e n�o
				// uma entidade vazia, pois isto seria uma nova entidade
				Object value = getPropertyValue();
				if (value == null)
					asEntityCache = null;
				else
					asEntityCache = this.getPropetyOwner().getEntityOwner().getEntityManager().getEntity(value);
			}
			return (IEntity<T>) asEntityCache;
		} catch (EntityException e){
			e.getErrorList().addAll(
					MessageList
					.create(PropertyValueException.class,
							"GET_AS_ENTITY", propertyName,
							propertyOwner.getInfo().getType()
							.getSimpleName(), propertyOwner
							.getEntityOwner().getInfo()
							.getLabel()));
			throw new PropertyValueException(e.getErrorList());
		}
	}

	public void setAsEntity(IEntity<?> entity) throws PropertyValueException{
		if (!(propertyOwner.getInfo().isEntity() && !propertyOwner.getInfo().isList()))
			wrongType(IEntity.class);

		if (entity == null)
			setPropertyValue(null);
		else
			setPropertyValue(entity.getObject());

		/* Limpa o cache para o pr�ximo getAsEntity */
		this.asEntityCache = entity;
	}

	private IEntityList<?> asEntityListCache = null;

	@SuppressWarnings("unchecked")
	public <T> IEntityList<T> getAsEntityList() throws PropertyValueException
	{
		if (!(propertyOwner.getInfo().isEntity() && propertyOwner.getInfo()
				.isList()))
			wrongType(IEntityList.class);

		try	{
			/* Prepara a lista caso j� n�o tenha sido preparada */
			if (asEntityListCache == null)
				asEntityListCache = this.getPropetyOwner().getEntityOwner()
						.getEntityManager().<T>getEntityList(
								(List<T>) getPropertyValue(),
								(Class<T>) getPropetyOwner().getInfo().getType());

			return (IEntityList<T>) asEntityListCache;
		} catch (EntityException e)	{
			e.getErrorList().addAll(
					MessageList
							.create(PropertyValueException.class,
									"GET_AS_ENTITY_LIST", propertyName,
									propertyOwner.getInfo().getType()
											.getSimpleName(), propertyOwner
											.getEntityOwner().getInfo()
											.getLabel()));
			throw new PropertyValueException(e.getErrorList());
		}
	}

	public void setAsEntityList(IEntityList<?> entityList)throws PropertyValueException	{
		if (!(propertyOwner.getInfo().isEntity() && propertyOwner.getInfo()
				.isList()))
			wrongType(IEntityList.class);

		// Verifica se a lista � do mesmo tipo da atual propriedade
		if (propertyOwner.getInfo().getType() != entityList.getInfo().getType())
			wrongType(entityList.getInfo().getType());

		if (entityList == null)
			setPropertyValue(null);
		else
		{
			/* Atualiza o buffer local */
			this.asEntityListCache = entityList;
			/* Grava a nova lista no objeto */
			setPropertyValue(entityList.getObjectList());
		}
	}

	private IEntitySet<?> asEntitySetCache = null;

	@SuppressWarnings("unchecked")
	public <T> IEntitySet<T> getAsEntitySet() throws PropertyValueException {
		if (!(propertyOwner.getInfo().isEntity() && propertyOwner.getInfo()
				.isSet()))
			wrongType(IEntitySet.class);

		try
		{
			/* Prepara a lista caso j� n�o tenha sido preparada */
			if (asEntitySetCache == null)
				asEntitySetCache = this.getPropetyOwner().getEntityOwner()
						.getEntityManager().getEntitySet(
								(Set<T>) getPropertyValue(),
								(Class<T>) getPropetyOwner().getInfo().getType());

			return (IEntitySet<T>) asEntitySetCache;
		} catch (EntityException e)
		{
			e.getErrorList().addAll(
					MessageList
							.create(PropertyValueException.class,
									"GET_AS_ENTITY_SET", propertyName,
									propertyOwner.getInfo().getType()
											.getSimpleName(), propertyOwner
											.getEntityOwner().getInfo()
											.getLabel()));
			throw new PropertyValueException(e.getErrorList());
		}
	}

	public void setAsEntitySet(IEntitySet<?> entitySet)
			throws PropertyValueException
	{
		if (!(propertyOwner.getInfo().isEntity() && propertyOwner.getInfo()
				.isSet()))
			wrongType(IEntitySet.class);

		// Verifica se a lista � do mesmo tipo da atual propriedade
		if (propertyOwner.getInfo().getType() != entitySet.getInfo().getType())
			wrongType(entitySet.getInfo().getType());

		if (entitySet == null)
			setPropertyValue(null);
		else
		{
			/* Atualiza o buffer local */
			this.asEntitySetCache = entitySet;
			/* Grava a nova lista no objeto */
			setPropertyValue(entitySet.getObjectSet());
		}
	}

	@SuppressWarnings("unchecked")
	public IEntityCollection<?> getAsEntityCollection()
			throws PropertyValueException
	{
		if (!(propertyOwner.getInfo().isEntity() && (propertyOwner.getInfo()
				.isList() || propertyOwner.getInfo().isSet())))
			wrongType(IEntityCollection.class);

		try
		{
			if (propertyOwner.getInfo().isList())
				return getAsEntityList();
			else if (propertyOwner.getInfo().isSet())
				return getAsEntitySet();
			else
				return null;

		} catch (PropertyValueException e)
		{
			e.getErrorList().addAll(
					MessageList
							.create(PropertyValueException.class,
									"GET_AS_ENTITY_COLLECTION", propertyName,
									propertyOwner.getInfo().getType()
											.getSimpleName(), propertyOwner
											.getEntityOwner().getInfo()
											.getLabel()));
			throw new PropertyValueException(e.getErrorList());
		}
	}

	public void  setAsEntityCollection(IEntityCollection<?> entityCollection)
			throws PropertyValueException
	{
		if (!(propertyOwner.getInfo().isEntity() && (propertyOwner.getInfo()
				.isList() || propertyOwner.getInfo().isSet())))
			wrongType(IEntityCollection.class);

		try
		{
			if (propertyOwner.getInfo().isList())
				setAsEntityList((IEntityList<?>) entityCollection);
			else if (propertyOwner.getInfo().isSet())
				setAsEntitySet((IEntitySet<?>) entityCollection);

		} catch (PropertyValueException e)
		{
			e.getErrorList().addAll(
					MessageList
							.create(PropertyValueException.class,
									"GET_AS_ENTITY_COLLECTION", propertyName,
									propertyOwner.getInfo().getType()
											.getSimpleName(), propertyOwner
											.getEntityOwner().getInfo()
											.getLabel()));
			throw new PropertyValueException(e.getErrorList());
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getAsPrimitiveList() throws PropertyValueException
	{
		if (!(propertyOwner.getInfo().isPrimitive() && propertyOwner.getInfo()
				.isList()))
			wrongType(List.class);

		try
		{
			return (List<T>) getPropertyValue();
		} catch (Exception exc)
		{
			PropertyValueException e = new PropertyValueException(MessageList
					.createSingleInternalError(exc));
			e.getErrorList().addAll(
					MessageList
							.create(PropertyValueException.class,
									"GET_AS_PRIMITIVE_LIST", propertyName,
									propertyOwner.getInfo().getType()
											.getSimpleName(), propertyOwner
											.getEntityOwner().getInfo()
											.getLabel()));
			throw e;
		}
	}

	public void setAsPrimitiveList(List<?> primitiveList)
			throws PropertyValueException
	{
		if (!(propertyOwner.getInfo().isPrimitive() && propertyOwner.getInfo()
				.isList()))
			wrongType(List.class);

		setPropertyValue(primitiveList);
	}

	public <T> Set<T>  getAsPrimitiveSet() throws PropertyValueException
	{
		if (!(propertyOwner.getInfo().isPrimitive() && propertyOwner.getInfo()
				.isSet()))
			wrongType(Set.class);

		try
		{
			return (Set<T>) getPropertyValue();
		} catch (Exception exc)
		{
			PropertyValueException e = new PropertyValueException(MessageList
					.createSingleInternalError(exc));
			e.getErrorList().addAll(
					MessageList
							.create(PropertyValueException.class,
									"GET_AS_PRIMITIVE_SET", propertyName,
									propertyOwner.getInfo().getType()
											.getSimpleName(), propertyOwner
											.getEntityOwner().getInfo()
											.getLabel()));
			throw e;
		}
	}

	public void setAsPrimitiveSet(Set<?> primitiveSet)
			throws PropertyValueException
	{
		if (!(propertyOwner.getInfo().isPrimitive() && propertyOwner.getInfo()
				.isSet()))
			wrongType(Set.class);

		setPropertyValue(primitiveSet);
	}

	public <T> Collection<T>  getAsPrimitiveCollection() throws PropertyValueException
	{
		if (!(propertyOwner.getInfo().isPrimitive() && (propertyOwner.getInfo()
				.isList() || propertyOwner.getInfo().isSet())))
			wrongType(Collection.class);

		try
		{
			if (propertyOwner.getInfo().isList())
				return getAsPrimitiveList();
			else if (propertyOwner.getInfo().isSet())
				return getAsPrimitiveSet();
			else
				return null;

		} catch (PropertyValueException e)
		{
			e.getErrorList().addAll(
					MessageList
							.create(PropertyValueException.class,
									"GET_AS_PRIMITIVE_COLLECTION",
									propertyName, propertyOwner.getInfo()
											.getType().getSimpleName(),
									propertyOwner.getEntityOwner().getInfo()
											.getLabel()));
			throw new PropertyValueException(e.getErrorList());
		}
	}

	public Object getOldValue()
	{
		return oldValue;
	}

	@SuppressWarnings("unchecked")
	private <T> T getPropertyValue() throws PropertyValueException
	{
		try
		{
			return (T) PropertyUtils.getProperty(propertyOwner.getEntityOwner()
					.getObject(), propertyName);

		} catch (Exception e)
		{
			throw new PropertyValueException(MessageList
					.createSingleInternalError(e));
		}

	}

	private void setPropertyValue(Object object) throws PropertyValueException
	{
		try
		{
			Object old = getPropertyValue();
			/*
			 * Uma matriz de incidencia que reprensenta a �rvore de decis�o para
			 * definir ou n�o o valor da propriedade.
			 *
			 * old old_null obj set set obj_null set !set
			 *
			 */
			if (((object == null) && (old != null))
					|| ((object != null) && (!object.equals(old))))
			{

				PropertyUtils.setProperty(propertyOwner.getEntityOwner()
						.getObject(), propertyName, object);

				// Verifica se � a primeira altera��o para guardar o Old
				// que representa o valor persistido.
				if (!modified)
				{
					oldValue = old;

					// Indica que foi modificado valor no objeto origem
					modified = true;
				}
			}

		} catch (Exception e)
		{
			throw new PropertyValueException(MessageList
					.createSingleInternalError(e));
		}
	}

	public IProperty getPropetyOwner()
	{
		return propertyOwner;
	}

	private void wrongType(Class<?> classType) throws PropertyValueException
	{
		throw new PropertyValueException(MessageList.create(
				PropertyValueException.class, "GET_AS_WRONG_TYPE",
				propertyName,
				propertyOwner.getInfo().getType().getSimpleName(),
				propertyOwner.getEntityOwner().getInfo().getLabel(), classType
						.getSimpleName()));
	}
	
	/** Diz para a propriedade limpar os seus buffers pois pode haver um novo objeto
	 * na entidade */
	public void flush(){
		this.asEntityListCache = null;
		this.asEntitySetCache = null;
		this.asEntityCache = null;
		this.oldValue = null;
	}

	/**
	 * !Descri��o na interface
	 * @author lucio 20100411
	 * @throws PropertyValueException 
	 */
	public boolean restoreOldValue() throws PropertyValueException {
		if(this.isModified()){
			this.setPropertyValue(this.getOldValue());
			this.modified = false;
			return true;
		}
		return false;
	}

}
