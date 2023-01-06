package br.com.orionsoft.monstrengo.crud.entity.metadata;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.entity.metadata.EntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.GroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IMetadataHandle;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;
import br.com.orionsoft.monstrengo.crud.entity.metadata.PropertyMetadata;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;
import br.com.orionsoft.monstrengo.crud.entity.dao.DAOException;

/**
 * @author Lucio
 * @version 20060109
 */
public class EntityMetadata implements IEntityMetadata
{

    private String label;
    private String hint;
    private String description;
    private String colorName;
    private Map<String, IPropertyMetadata> propertiesMetadata = new HashMap<String, IPropertyMetadata>();
    private List<IGroupMetadata> groups;
    private Class<?> type;
    private boolean canCreate;
    private boolean canRetrieve;
    private boolean canUpdate;
    private boolean canDelete;
    private boolean canQuery;
    private boolean runQueryOnOpen;
    private List<Class<?>> subEntities;
    private List<String> propertiesInQueryGrid;
    
    // Implementação da interface IEntityMetadata
    public String getName(){return type.getName();}

    public String getLabel(){return label;}

    public String getHint(){return hint;}

    public String getDescription(){return description;}

    public IPropertyMetadata getPropertyMetadata(String propertyName) throws MetadataException{
        if (!propertiesMetadata.containsKey(propertyName))
            throw new MetadataException(MessageList.create(EntityException.class, "ERROR_PROPERTY_NOT_FOUND", propertyName, type.getName()));
    
    	return propertiesMetadata.get(propertyName);
    }

    public Class<?> getType(){return type;}

    
    /**
     * <p>Este método permite criar uma instância da classe. 
     * Como a classe implementa uma interface com somente métodos <code>get</code>, este
     * método estático pertencente a própria classe e pode acessar os campos privados. 
     * 
     * @param entityName Nome completo da classe que representa a entidade. Ex.: <code>br.com.orionsoft.basico.cadastro.Endereco</code>
     * @param metadaHandle Classe manipuladora de metadatas correntemente instanciada pela aplicação.
     * @return
     * @throws MetadataException 
     */
	public EntityMetadata(Class<?> entityClass, IEntityManager entityManager,
			boolean defaultMode) throws MetadataException {
		IMetadataHandle metadataHandle = entityManager.getMetadataHandle();

		// Informa o Handle qual classe será pesquisada
		metadataHandle.setEntityClass(entityClass, defaultMode);

		// Prepara os metadados da ENTIDADE
		this.type = entityClass;
		this.label = metadataHandle.getEntityLabel();
		this.hint = metadataHandle.getEntityHint();
		this.description = metadataHandle.getEntityDescription();
		this.colorName = metadataHandle.getEntityColorName();
		this.canCreate = metadataHandle.getEntityCanCreate();
		this.canRetrieve = metadataHandle.getEntityCanRetrieve();
		this.canUpdate = metadataHandle.getEntityCanUpdate();
		this.canDelete = metadataHandle.getEntityCanDelete();
		this.canQuery = metadataHandle.getEntityCanQuery();
		this.runQueryOnOpen = metadataHandle.getEntityRunQueryOnOpen();
		this.propertiesInQueryGrid = metadataHandle.getPropertiesInQueryGrid();

		// Pega os campos declarados da classe
		PropertyDescriptor[] props = PropertyUtils
				.getPropertyDescriptors(entityClass);

		// Prepara os metadados de cada PROPRIEDADE
		for (PropertyDescriptor prop : props) {
			/*
			 * Remove a propriedade class do bean, herdada de Object pelo método
			 * getClass();
			 */
			if(!prop.getName().equals("class")){
				/* Criando os metadados da propriedade */
				IPropertyMetadata propM = new PropertyMetadata(prop,
						metadataHandle, this);

				this.propertiesMetadata.put(propM.getName(), propM);				
			}
		}

		// Re-arranja os índices das propriedades, verificando CONFLITOS e
		// INEXISTENCIA
		PropertyMetadata.arrangePropertiesIndex(this.propertiesMetadata
				.values());

		/*
		 * Prepara os grupos: Lê os grupos dos metadados para já definir suas
		 * propriedades enquanto as propriedades são preparadas e seu grupo é
		 * identificado
		 */
		this.groups = metadataHandle.getEntityGroups();

		// Prepara os GRUPOS de PROPRIEDADES
		for (IPropertyMetadata prop : propertiesMetadata.values()) {
			/* Define em qual grupo a propriedade será adicionada */
			if (prop.getGroup() == IGroupMetadata.GROUP_NOT_DEFINED) {

				/* Procurando se o grupo GROUP_NOT_DEFINED já foi criado */
				IGroupMetadata groupNotDefined = null;
				for (IGroupMetadata group : groups) {
					/*
					 * Verifica se o índice do atual grupo é -1 ou se o nome
					 * dele é nulo. Sendo nulo seu nome, significa que foi
					 * retornado pelo metadata handle um grupo sem nome que
					 * provavelmente estava no banco de dados. Grupos sem nome
					 * são, geralmente, grupos NOT_DEFINED que foram persistidos
					 * no banco com um índice válido.
					 */
					if ((group.getIndex() == IGroupMetadata.GROUP_NOT_DEFINED)
							|| StringUtils.isEmpty(group.getName())) {
						groupNotDefined = group;
						break;
					}
				}

				/*
				 * Verificando se o grupo GROUP_NOT_DEFINED foi encontrado na
				 * busca
				 */
				if (groupNotDefined == null) {
					/* Cria o grupo */
					groupNotDefined = new GroupMetadata(
							IGroupMetadata.GROUP_NOT_DEFINED, "");
					groups.add(groupNotDefined);
				}

				/*
				 * Adicionando a propriedade atual no grupo GROUP_NOT_DEFINED
				 */
				groupNotDefined.getProperties().add(prop);

			} else {
				/* Verifica se o grupo definido na propriedade EXISTE */
				if (prop.getGroup() > (groups.size() - 1)) {
					throw new MetadataException(MessageList.create(
							EntityMetadata.class, "INVALID_GROUP_INDEX",
							prop.getGroup(), prop.getLabel(), this.label));
				}

				/* Grupo definido. Insere ele na lista de propriedades do grupo */
				groups.get(prop.getGroup()).getProperties().add(prop);
			}
		}

		/*
		 * Ordena as propriedades dentro dos grupos e define o índice válido
		 * para o grupo GRUP_NOT_DEFINED. Assim, ele não vai ficar na última
		 * posição da lista com índice = -1 e sim com o índice igual ao da
		 * última posição (a sua)
		 */
		for (IGroupMetadata group : groups) {
			Collections.<IPropertyMetadata> sort(group.getProperties(),
					IPropertyMetadata.COMPARATOR_INDEX);

			/* Verificando se é o último grupo para arrumar seu índice */
			if (group.getIndex() == IGroupMetadata.GROUP_NOT_DEFINED)
				group.setIndex(groups.size() - 1);
		}
		/* Prepara a lista de subEntidades definidas nos metadados */
		try {
			this.subEntities = entityManager.getDaoManager().getSubEntities(
					entityClass);
		} catch (DAOException e) {
			throw new MetadataException(e.getErrorList());
		}

	}

    public int getPropertiesSize()
    {
       return propertiesMetadata.size();    
    }
    
    public IPropertyMetadata[] getProperties()
    {
        // Cria uma lista ordenada com as propriedades
        // Obs.: Nem todas propriedades tem um índice definido.
        // Ver: PropertyMetadata.arrangePropertiesIndex()
        IPropertyMetadata[] props = new IPropertyMetadata[propertiesMetadata.size()];

        for(IPropertyMetadata prop: propertiesMetadata.values())
        {
            props[prop.getIndex()] = prop;
        }
        
        return props;
    }
    
    public int getPropertiesInQueryGridSize()
    {
       return getPropertiesInQueryGrid().length;    
    }
    
    IPropertyMetadata[] propertiesInQueryGridBuffer = null;
    public IPropertyMetadata[] getPropertiesInQueryGrid()
    {
    	if(propertiesInQueryGridBuffer != null)
    		return propertiesInQueryGridBuffer;
        
        
    	/* Verifica se tem itens definidos nos metadados */
    	if(this.propertiesInQueryGrid.size()!=0){
    		/* Cria uma lista de propriedades de acordo
    		 * com o tamanho dos nomes encontrados nos
    		 * metadados
    		 */
    		propertiesInQueryGridBuffer = new IPropertyMetadata[this.propertiesInQueryGrid.size()];
    		int index = 0;

    		/* Adiciona no vetor somente as do inQueryGrid */
    		for(String propName: this.propertiesInQueryGrid){
    			propertiesInQueryGridBuffer[index] = this.propertiesMetadata.get(propName);
    			index++;
    		}
    	}else{
            /* Obtem a lista ordenada de todas as propriedades da 
             * entidade visiveis e invisiveis 
             */
            IPropertyMetadata[] props = getProperties();
            
            /* Verifica quantas propriedades são visiveis */
            int length = 0;
            for(IPropertyMetadata prop: props){ 
                if(prop.isVisible()) length++;
            }
            
            /* Cria a lista interna que conterá somente as visíveis */
            propertiesInQueryGridBuffer = new IPropertyMetadata[length];
            int index = 0;
            
            /* Adiciona no vetor somente as visíveis */
            for(IPropertyMetadata prop: props){
                if(prop.isVisible()){
                	propertiesInQueryGridBuffer[index] = prop;
                    index++;
                }
            }
    	}
        
        return propertiesInQueryGridBuffer;
    }

    public Map<String, IPropertyMetadata> getPropertiesMetadata()
    {
        return propertiesMetadata;
    }

    public boolean isAbstract()
    {
        return Modifier.isAbstract(type.getModifiers());
    }

    public boolean getCanCreate()
    {
        return this.canCreate;
    }
    
    public boolean getCanRetrieve()
    {
        return this.canRetrieve;
    }
    
    public boolean getCanUpdate()
    {
        return this.canUpdate;
    }
    
    public boolean getCanDelete()
    {
        return this.canDelete;
    }

    public boolean getCanQuery()
    {
        return this.canQuery;
    }

    public boolean getRunQueryOnOpen()
    {
        return this.runQueryOnOpen;
    }

	public List<IGroupMetadata> getGroups() {
		return this.groups;
	}

	public List<Class<?>> getSubEntities()
	{
		return subEntities;
	}

	public boolean isHasSubEntities()
	{
		return subEntities != null && subEntities.size()>0;
	}

	public String getColorName() {
		return colorName;
	}

}
