/**
 * Criado em 04/08/2005
 */
package br.com.orionsoft.monstrengo.crud.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.orionsoft.monstrengo.crud.entity.BusinessEntity;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.GroupProperty;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;
import br.com.orionsoft.monstrengo.crud.entity.IGroupProperties;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.Property;
import br.com.orionsoft.monstrengo.crud.entity.PropertyException;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * <p>
 * Esta classe reúne as funções básicas comuns à todas as entidades do sistema.
 * Todas as entidades de negócio devem descender desta classe. Contudo, para
 * haver uma ligação da classe de negócio com seus metadados o método
 * <code>init()</code> deve ser executado.
 * <p>
 *
 * @author Lucio
 * @version 20060413
 */
public class BusinessEntity<T> implements IEntity<T> {
	private Object object;

	private Map<String, IProperty> properties;

	private IEntityMetadata info;

	private IEntityManager entityManager;

	private boolean selected = true; // Uma entidade vem por padrão selecionada. Isto pode causar problema nos processos que permite que o operador marque entidade para determiando processo

	public BusinessEntity(Object object, IEntityMetadata entityMetadata, IEntityManager entityManager)
			throws EntityException {
		/* Verifica se o objeto é do mesmo tipo da entidade */
		if (!entityMetadata.getType().isAssignableFrom(object.getClass()))
			throw new EntityException(MessageList.create(BusinessEntity.class, "INCOMPATIBLE_TYPE", object.getClass(),
					entityMetadata.getType()));

		// Armazena o manipulador de serviço
		this.object = object;

		// Armazena a estrutura de metadados da classe
		this.info = entityMetadata;

		// Armazena o gerenciador de entidades que criou este objeto
		this.entityManager = entityManager;

		// Prepara cada propriedade da entidade como uma IPropety
		properties = new HashMap<String, IProperty>(entityMetadata.getPropertiesMetadata().size());
		for (IPropertyMetadata info : entityMetadata.getPropertiesMetadata().values()) {
			// Cria uma nova propriedade para ser ser coloca na lista
			IProperty prop = new Property(this, info);

			properties.put(info.getName(), prop);
		}

	}

	// Implementação da interface IEntity
	public IProperty getProperty(String propertyName) throws EntityException {
		try {
			checkPropertyName(propertyName);
			return properties.get(propertyName);

		} catch (EntityException e) {
			// Adiciona a mensagem local
			e.getErrorList().addAll(
					MessageList.create(EntityException.class, "ERROR_GETTING_PROPERTY", propertyName, getInfo()
							.getLabel()));
			throw new EntityException(e.getErrorList());
		}

	}

	public <E> E getPropertyValue(String propertyName) throws EntityException {
		try {
			checkPropertyName(propertyName);

			return properties.get(propertyName).getValue().getAsObject();

		} catch (BusinessException e) {
			// Adiciona a mensagem local
			e.getErrorList().addAll(
					MessageList.create(EntityException.class, "ERROR_GETTING_PROPERTY_VALUE", propertyName, getInfo()
							.getLabel()));
			throw new EntityException(e.getErrorList());
		}
	}

	public void setPropertyValue(String propertyName, Object value) throws EntityException {
		try {
			checkPropertyName(propertyName);

			properties.get(propertyName).getValue().setAsObject(value);
		} catch (BusinessException e) {
			e.getErrorList().addAll(
					MessageList.create(EntityException.class, "ERROR_GETTING_PROPERTY_VALUE", propertyName, getInfo()
							.getLabel()));
			throw new EntityException(e.getErrorList());
		}

	}

	/*
	 * Esse array ira retornar todas as propriedades da entidade ordenadas pelo
	 * index.
	 */
	private IProperty[] propertiesArray = null;

	/**
	 * Este método retorna todas as propriedades da entidade de forma ordenada,
	 * baseando-se no index do metadado de cada propriedade.
	 */
	public IProperty[] getProperties() {
		if (propertiesArray == null) {
			/* Cria uma lista ordenada com as propriedades */
			propertiesArray = new IProperty[properties.size()];

			for (IProperty prop : properties.values()) {
				/* Grava a prop na posição do indice. */
				propertiesArray[prop.getInfo().getIndex()] = prop;
			}
		}

		return this.propertiesArray;
	}

	/**
	 * Este método retorna todas as propriedades da entidade de forma ordenada,
	 * baseando-se no metadado propertiesInQueryGrid().
	 */
	IProperty[] propertiesInQueryGridArray = null;
	public IProperty[] getPropertiesInQueryGrid() {
		if (propertiesInQueryGridArray == null) {
			/* Cria uma lista InQueryGrid com as propriedades */
			propertiesInQueryGridArray = new IProperty[info.getPropertiesInQueryGridSize()];

			int i = 0;
			for (IPropertyMetadata prop : info.getPropertiesInQueryGrid()) {
				/* Grava a prop na posição do indice. */
				propertiesInQueryGridArray[i++] = this.getPropertiesMap().get(prop.getName());
			}
		}

		return this.propertiesInQueryGridArray;
	}

	@SuppressWarnings("unchecked")
	public T getObject() {
		return (T) this.object;
	}

	public IEntityMetadata getInfo() {
		return info;
	}

	/**
	 * Este flag foi criado para permitir identificar quando o operador tentou inserir um id inválido.<br>
	 * No entanto o id -1 continua indicando que se trata de uma nova entidade.<br>
	 * Criado um flag idErro para indicar ao ToString() que houve um id inválido e que é para gerar um toString();  
	 */
	private boolean idError = false;
	public void setId(long id) throws EntityException {
		try {
			/* Se for um id vazio, então cria um objeto vazio */
			if (id == 0 || id == IDAO.ENTITY_UNSAVED) {
				this.object = UtilsCrud.create(this.getEntityManager().getServiceManager(), this.info.getType(), null).getObject();
			} else if (id != this.getId()) {
				IEntity<T> entity = (IEntity<T>) UtilsCrud.retrieve(this.getEntityManager().getServiceManager(), this.info.getType(), id, null);
				this.object = entity.getObject();
			}
			
			/* Solicita para cada propriedade da entidade
			 * para limpar possíveis BUFFERS.
			 * Isto acontecia com as propertyValue.entityList que eram 
			 * bufferizadas e ao trocar o Id do objeto, os buffers do antigo
			 * continuava e as novas operações eram executadas sobre os
			 * buffers, mas na hora de update, o novo objeto era gravado 
			 * sem as alterações realizadas */
			for (IProperty prop : properties.values()) {
				// Cria uma nova propriedade para ser ser coloca na lista
				prop.getValue().flush();
			}
			
			/* Indica que não possui problema no id */
			idError = false;
		} catch (BusinessException e) {
			/*
			 * Caso ocorra algum erro, então anula a stual instância
			 * da entidade, criando uma nova vazia e propaga o erro
			 */
			try {
				this.object = UtilsCrud.create(this.getEntityManager().getServiceManager(), this.info.getType(), null).getObject();
			} catch (BusinessException e1) {
				/* Se ocorre um erro neste momento adiciona a mensagem a lista que será propagada logo a seguir */
				e.getErrorList().addAll(e1.getErrorList());
			}

			/* Indica que possui problema no id */
			idError = true;
			e.getErrorList().addAll(MessageList.create(EntityException.class, "ERROR_SETTING_ID", info.getLabel()));
			throw new EntityException(e.getErrorList());
		} catch (Exception e) {
			throw new EntityException(MessageList.createSingleInternalError(e));
		}
	}

	public long getId(){
		try {
			return (Long) getProperty(IDAO.PROPERTY_ID_NAME).getValue().getAsObject();
		} catch (BusinessException e) {
			e.getErrorList().addAll(MessageList.create(EntityException.class, "ERROR_GETTING_ID", info.getLabel()));
			
			/* Lucio: 20070913 - Dispara um runtime exception caso ocorra algum erro ao pegar o id da entidade. Uma
			 * vez que todas as entidades OBRIGATORIAMENTE devem ter este ID, um erro neste local
			 * só poderá ser consequencia de bug que deverá ser resolvido.
			 * A exceção não foi jogada pra cima para facilitar os métodos que simplesmente
			 * precisam do IEntity.getId() sem necessitar de tratar exceção.*/
			throw new RuntimeException(e.getMessage());
		}
	}

	public IEntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Com o novo recurso de setId(), o operador pode
	 * fornecer um Id inexistente. Com isto, é importante
	 * informar que o Id não existe. Para isto, utiliza-se
	 * este método toString().
	 */
	public String toString() {
		if (idError)
			return MessageList.create(EntityException.class, "UNDEFINED_ID").get(0).getMessage();

		return this.object.toString();
	}

	private void checkPropertyName(String propertyName) throws EntityException {
		if (!properties.containsKey(propertyName))
			throw new EntityException(MessageList.create(EntityException.class, "ERROR_PROPERTY_NOT_FOUND",
					propertyName, getInfo().getLabel()));

	}

	public Map<String, IProperty> getPropertiesMap() {
		return properties;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	IGroupProperties[] groupsProperties = null;

	public IGroupProperties[] getGroupsProperties() throws EntityException {
		/* Verifica se o buffer de grupos já não está pronto */
		if (groupsProperties == null) {
			/* Criando o o buffer de grupos */
			groupsProperties = new IGroupProperties[this.getInfo().getGroups().size()];

			/*
			 * Verificando os grupos existentes nos metadados e criando os
			 * grupos de propriedades
			 */
			for (IGroupMetadata group : this.getInfo().getGroups()) {
				/* Criando o grupo */
				IProperty[] properties = new IProperty[group.getProperties().size()];

				/* Adicionando as propriedades do grupo */
				int i = 0;
				for (IPropertyMetadata propInfo : group.getProperties()) {
					properties[i++] = this.getProperty(propInfo.getName());
				}
				
				/* Adicionando ele na lista de grupos */
				IGroupProperties propGroup = new GroupProperty(group, properties);
				groupsProperties[propGroup.getInfo().getIndex()] = propGroup;
			}
		}
		return groupsProperties;
	}

	/**
	 * Compara se uma entidade é igual a outra
	 */
	public boolean equals(Object arg0) {
		try {
			if((arg0 != null) &&  (arg0 instanceof BusinessEntity) && this.getInfo().getType().equals(((IEntity<?>)arg0).getInfo().getType()) && 
					(this.getId() == ((IEntity<?>)arg0).getId()))
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<IEntity<T>> find(String queryString) throws EntityException {
    	@SuppressWarnings("rawtypes")
    	IEntityList result;
    	try
    	{
  			// TODO IMPLEMENTACAO criar uma condicao HQL que pega uma lista de ids dos metadados e permite seletionar somente entre a lista de entidades identificadas
   			result = this.entityManager.queryEntities(info.getType(), queryString, null, 50);

    		return result.getList();

    	}
    	catch (BusinessException e)
    	{
    		//Adiciona a mensagem local
    		e.getErrorList().addAll(MessageList.create(PropertyException.class, "ERROR_GETTING_VALUE_LIST", this.info.getLabel(), this.getInfo().getLabel()));
    		throw new EntityException(e.getErrorList());
    	}
	}


}
