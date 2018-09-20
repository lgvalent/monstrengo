/**
 *
 */
package br.com.orionsoft.monstrengo.crud.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.IPropertyValue;
import br.com.orionsoft.monstrengo.crud.entity.PropertyException;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValue;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;

/**
 * @author Lucio
 *
 */
public class Property implements IProperty
{

    private IEntity<?> entityOwner;
    private IPropertyMetadata info;
    private IPropertyValue value;

    public Property(IEntity<?> entityOwner, IPropertyMetadata info)
    {
        this.info = info;
        this.entityOwner = entityOwner;

        this.value = new PropertyValue(this);
    }

    public IPropertyMetadata getInfo(){return info;}
    public void setInfo(IPropertyMetadata info) {this.info = info;}

	public IPropertyValue getValue(){return value;}

    public IEntity getEntityOwner(){return entityOwner;}

    public List<SelectItem> getValuesList() throws PropertyException
    {
      try
      {

        // A lista é criada depois com o tamanho já otimizado
        List<SelectItem> result;

        // Verifica se a propriedade é primitiva para pegar dos metadados
        if(info.isPrimitive())
        {
            /* Ao pegar os metadados */
        	if(info.isEnum()){
                // TODO IMPLEMENTACAO criar uma condicao que pega a lista de valuesList dos metadados e permite seletionar somente entre os nomes dos enumeradores que lá estiverem
        		result = new ArrayList<SelectItem>(info.getType().getEnumConstants().length);
        		for(Object enumValue: info.getType().getEnumConstants())
        			result.add(new SelectItem(new Long(((Enum) enumValue).ordinal()), enumValue.toString()));
        	}
        	else{

        		result = new ArrayList<SelectItem>(info.getValuesList().size());
        		for(Iterator<String> i=info.getValuesList().iterator(); i.hasNext();)
        		{
        			String value = i.next();

        			result.add(new SelectItem(value));
        		}
        	}

            // Verifica se não for requirido insere um elemento em branco para escolha
            // Verifica se a atual propriedade é unitária (one-to-one ou many-to-one), ou seja, não é uma Collection
            if (!info.isRequired() && !info.isCollection())
                result.add(0, new SelectItem("", ""));

        }
        // Se não for primitiva, cria uma lista com as atuais entidades
        // cadastradas no tipo referido
        else
        {
            // TODO IMPLEMENTACAO criar uma condicao HQL que pega uma lista de ids dos metadados e permite seletionar somente entre a lista de entidades identificadas
        	result = entityOwner.getEntityManager().getEntitySelectItems(info.getType(), "");

            // Verifica se não for requidiro insere um elemento em branco para escolha
            // Verifica se a atual propriedade é unitária (one-to-one ou many-to-one), ou seja, não é uma Collection
            if (!info.isRequired() && !info.isCollection())
                result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, ""));
        }

        return result;

      }
      catch (BusinessException e)
      {
          //Adiciona a mensagem local
          e.getErrorList().addAll(MessageList.create(PropertyException.class, "ERROR_GETTING_VALUE_LIST", this.info.getLabel(), this.getEntityOwner().getInfo().getLabel()));
          throw new PropertyException(e.getErrorList());
      }


    }

    public List<SelectItem> getValuesList(String filter) throws PropertyException
    {
      try
      {

        // A lista é criada depois com o tamanho já otimizado
        List<SelectItem> result;

        // Verifica se a propriedade é primitiva para pegar dos metadados
        if(info.isPrimitive())
        {
            /* Ao pegar os metadados */
        	if(info.isEnum()){
                // TODO IMPLEMENTACAO criar uma condicao que pega a lista de valuesList dos metadados e permite seletionar somente entre os nomes dos enumeradores que lá estiverem
        		result = new ArrayList<SelectItem>(info.getType().getEnumConstants().length);
        		for(Object enumValue: info.getType().getEnumConstants())
        			result.add(new SelectItem(new Long(((Enum) enumValue).ordinal()), enumValue.toString()));
        	}
        	else{

        		result = new ArrayList<SelectItem>(info.getValuesList().size());
        		for(Iterator<String> i=info.getValuesList().iterator(); i.hasNext();)
        		{
        			String value = i.next();

        			result.add(new SelectItem(value));
        		}
        	}

            // Verifica se não for requirido insere um elemento em branco para escolha
            // Verifica se a atual propriedade é unitária (one-to-one ou many-to-one), ou seja, não é uma Collection
            if (!info.isRequired() && !info.isCollection())
                result.add(0, new SelectItem("", ""));

        }
        // Se não for primitiva, cria uma lista com as atuais entidades
        // cadastradas no tipo referido
        else
        {
            // TODO IMPLEMENTACAO criar uma condicao HQL que pega uma lista de ids dos metadados e permite seletionar somente entre a lista de entidades identificadas
        	if(filter != null && !filter.isEmpty()){
        		result = entityOwner.getEntityManager().queryEntitySelectItems(info.getType(), filter, 10);
        	}else{
        		result = entityOwner.getEntityManager().getEntitySelectItems(info.getType(), "");
        	}
        	
            // Verifica se não for requidiro insere um elemento em branco para escolha
            // Verifica se a atual propriedade é unitária (one-to-one ou many-to-one), ou seja, não é uma Collection
            if (!info.isRequired() && !info.isCollection())
                result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, ""));
        }

        return result;

      }
      catch (BusinessException e)
      {
          //Adiciona a mensagem local
          e.getErrorList().addAll(MessageList.create(PropertyException.class, "ERROR_GETTING_VALUE_LIST", this.info.getLabel(), this.getEntityOwner().getInfo().getLabel()));
          throw new PropertyException(e.getErrorList());
      }


    }

    @SuppressWarnings("unchecked")
    public List<IEntity<?>> getSelectValues(String filter) throws PropertyException
    {
    	@SuppressWarnings("rawtypes")
    	IEntityList result;
    	try
    	{
    		// Verifica se a propriedade é primitiva para pegar dos metadados
    		if(info.isPrimitive())
    		{
    			throw new RuntimeException("Não é possível obter valores de um propriedade primitiva!");
    		}
    		// Se não for primitiva, cria uma lista com as atuais entidades
    		// cadastradas no tipo referido
    		else
    		{
    			// TODO IMPLEMENTACAO criar uma condicao HQL que pega uma lista de ids dos metadados e permite seletionar somente entre a lista de entidades identificadas
    			result = entityOwner.getEntityManager().queryEntities(info.getType(), filter, null, 50);
    		}

    		return result.getList();

    	}
    	catch (BusinessException e)
    	{
    		//Adiciona a mensagem local
    		e.getErrorList().addAll(MessageList.create(PropertyException.class, "ERROR_GETTING_VALUE_LIST", this.info.getLabel(), this.getEntityOwner().getInfo().getLabel()));
    		throw new PropertyException(e.getErrorList());
    	}
    }
}
