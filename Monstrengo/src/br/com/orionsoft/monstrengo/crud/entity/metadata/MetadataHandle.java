package br.com.orionsoft.monstrengo.crud.entity.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.persistence.Embedded;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.crud.entity.metadata.GroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IMetadataHandle;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataHandle;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.util.AnnotationUtils;
import br.com.orionsoft.monstrengo.core.util.PropertyUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDaoManager;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntityProperty;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntityPropertyGroup;

/**
 * TODO IMPLEMENTAR Definir propriedades dos processos também como label, hint, description

 * TODO OTIMIZAR Fazer refactoring com a classe EntityInfo para evitar
 * múltiplas preparações do ResourceBundle a cada leitura de UMA
 * propriedade. 
 * Tentar utilizar XML para armazenar as informações.
 * 
 * Este bean é um singleton, e para ser usado, o método setEntityClass(class) deve
 * ser executado. Este método realiza todas as preparações de objetos, mapas e listas que servirão como fonte
 * dos metadados solicitados.
 * Os métodos getStrEntity(metadataName), getStrProperty(propertyName, metadataName) e getEntityGroups() obtem o metadado solicitado, já vericando se o metadado está no banco ou
 * no resourceBundle. 
 * 
 * Created on 20/04/2005
 * @author Lucio
 * @version 20070308
 * 
 * @spring.bean id="MetadataHandleTxt"
 * @spring.property name="daoManager" ref="DaoManager"
 */

public class MetadataHandle implements IMetadataHandle 
{
    protected Logger log = LogManager.getLogger(this.getClass());
    
    // CONSTANTES
    private static final String INDEX = "index";
    private static final String GROUP = "group";
    private static final String NAME = "name";
    private static final String LABEL = "label";
    private static final String HINT = "hint";
    private static final String DESCRIPTION = "description";
    private static final String TYPE = "type";
    private static final String SIZE = "size";
    private static final String COLORNAME = "colorName";
    private static final String DISPLAYFORMAT = "displayFormat";
    private static final String DEFAULTVALUE = "defaultValue";
    private static final String EDITMASK = "editMask";
    private static final String CANCREATE = "canCreate";
    private static final String CANRETRIEVE = "canRetrieve";
    private static final String CANUPDATE = "canUpdate";
    private static final String CANDELETE = "canDelete";
    private static final String CANQUERY = "canQuery";
    private static final String RUN_QUERY_ON_OPEN = "runQueryOnOpen";
    @Deprecated
    private static final String SUB_ENTITIES = "subEntities";

    private static final String VISIBLE = "visible";
    private static final String REQUIRED = "required";
    private static final String READONLY = "readOnly";  
    private static final String CALCULATED = "calculated";
    private static final String MINIMUM = "minimum";
    private static final String MAXIMUM = "maximum";
    private static final String EDITSHOWLIST = "editShowList";
    private static final String ISLIST = "isList";
    private static final String ISSET = "isSet";
    private static final String ALLOW_SUB_QUERY = "allowSubQuery";
    private static final String VALUESLIST = "valuesList";

    @Deprecated
    private static final String IS_ONE_TO_ONE = "oneToOne";
    @Deprecated
    private static final String IS_ONE_TO_MANY = "oneToMany";
    
    private Class<?> entityClass;
    private String entityName;
    private ApplicationEntity oApplicationEntity;
    private boolean defaultMode=false;
    
    private String bundleName;
    private static String DEFAULT_BUNDLE_NAME = MetadataHandle.class.getPackage().getName() + ".MetadataDefaults";
    private ResourceBundle resourceBundle;
    private ResourceBundle defaultResourceBundle;
    private boolean ready=false;
    private Map<String, ApplicationEntityProperty> applicationEntityPropertiesMap =  new HashMap<String, ApplicationEntityProperty>();
    private Map applicationEntityMetadataNameMap =  new HashMap();
    private Map applicationEntityPropertyMetadataNameMap =  new HashMap();
    
    private IDaoManager daoManager;

    private boolean checkReady() throws MetadataException
    {
        if (!ready)
            throw new MetadataException(MessageList.create(MetadataException.class, "METADATA_NOT_READY", entityName));
            
        return ready;
    }
    
    public Class<?> getEntityClass() {
    	return this.entityClass;
    }
    
    public void setEntityClass(Class<?> entityClass){
    	setEntityClass(entityClass, false);
    }

	public void setEntityClass(Class<?> entityClass, boolean defaultMode) 
    {
    	/* Limpa o mapa que armazena os metadados que foram encontrados persistidos no
    	 * banco de dados */
    	applicationEntityPropertiesMap.clear();

    	this.entityClass = entityClass;
        this.entityName = entityClass.getSimpleName();
        this.defaultMode = defaultMode;
     
        /* BANCO */
        try
        {
        	/* Busca no banco o objeto de metadados persistido referente  a entidade que está sendo solicitada
        	 * Obs.: O MetadataHandle encontra-se numa camada inferior ao EntityManger, logo, ele não pode
        	 * usar os recursos de IEntity para buscar objetos e usa , entao, busca em baixo nivel pelo DAO */
        	IDAO dao = daoManager.getDaoByEntity(ApplicationEntity.class);
        	List list = dao.getList(IDAO.ENTITY_ALIAS_HQL + "." + ApplicationEntity.CLASS_NAME +  "='" + entityClass.getName() + "'");

        	/* Verifica se achou o objeto */
        	if (!list.isEmpty()){
        		/* Define o objeto que será utilizado pelo Handle para obter os metadados solicitadas */
        		this.oApplicationEntity = (ApplicationEntity) list.get(0);
        		
        		/* Prepara os mapas contendo os metadados que podem ser recuperados do banco.
        		 * Útil para descobrir se um metadataName solicitado está no banco ou deve ser buscado no resourceBundle */
        		try{
        			this.applicationEntityMetadataNameMap = org.apache.commons.beanutils.BeanUtils.describe(oApplicationEntity);
        			this.applicationEntityPropertyMetadataNameMap = org.apache.commons.beanutils.BeanUtils.describe(new ApplicationEntityProperty());
        		} catch (Exception e){
        			throw new MetadataException(MessageList.createSingleInternalError(e));
        		}
        		
        		/* Prepara o mapa que contem todas as propriedades da entidade com os metadados que estão persistido
        		 * no banco. */ 
        		List<ApplicationEntityProperty> listProp = oApplicationEntity.getApplicationEntityProperty();
                for (ApplicationEntityProperty prop: listProp)
                {
                	applicationEntityPropertiesMap.put(prop.getName(), prop);
                }
        	}
        	else {
                if (log.isDebugEnabled())
                    log.debug("Os metadados da entidade "+ this.entityName +" não foram encontrados no banco");
            	oApplicationEntity = null;
        	}
        } catch (BusinessException e1)
        {
        	if (log.isDebugEnabled())
        		log.debug("Houve um erro ao executar o serviço: "+ e1.getMessage());
        	oApplicationEntity = null;
        }

        /* BUNDLE */
        bundleName = entityClass.getPackage().getName() + "." + entityClass.getSimpleName();
        
        try{
            resourceBundle = ResourceBundle.getBundle(bundleName);
        }
        catch(MissingResourceException e)
        {   
            if (log.isDebugEnabled())
                log.debug("O arquivo de propriedades da classe "+ this.entityName +" não foi encontrado");
            resourceBundle = null;
        }

        /* BUNDLE DEFAULT */
        /*
         * Se não encontrar o arquivo .properties relacionado à classe, 
         * usa o arquivo default (MetadataDefaults.properties)
         */
        try{
            defaultResourceBundle = ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME);
        }
        catch(MissingResourceException e)
        {   
            if (log.isDebugEnabled())
                log.debug("O arquivo de propriedades default não foi encontrado");
                defaultResourceBundle = null;
        }

        this.ready = true;
    }
    
    /**
     * getPropertyStr(propName, metadataName)
     * getPropertyStr(name, .label){
     *     entity.getApplicationEntityProperties()
     *  
     *  }
     * @param metadataName
     * @return
     * @throws MissingResourceException
     * @throws MetadataException
     * @throws EntityException 
     * @throws PropertyValueException 
     */
    private String getStrEntity(String metadataName) throws MissingResourceException, MetadataException
    {
        checkReady();
        
        try
        {
        	Object value = null;
        	/* Verifica se o metadado solicitado é persistido e se foi definido no banco.
        	 * Metadados .canCreate e etc não são persistidos */
            /* BANCO */
        	if(applicationEntityMetadataNameMap.containsKey(metadataName))
        		if(oApplicationEntity != null)
        			value = org.apache.commons.beanutils.PropertyUtils.getProperty(oApplicationEntity, metadataName);
        		
			if(!defaultMode && value != null)
        		return value.toString();
        	else
                /* BUNDLE */
        		if (resourceBundle != null){
        			metadataName = "." + metadataName;
        			/* Retira possíveis espaços em branco */
        			String valueStr = resourceBundle.getString(metadataName);
        			value = StringUtils.stripEnd(valueStr, " ");
        			return valueStr;
        		}
        } catch (MissingResourceException e){
        	throw e;
        } catch (Exception e){
        	throw new MetadataException(MessageList.createSingleInternalError(e));
        }
    	throw new MissingResourceException("","","");
    }

    /**
     * getPropertyStr(propName, metadataName)
     * getPropertyStr(name, .label){
     *     entity.getApplicationEntityProperties()
     *  
     *  }
     * @param propertyName
     * @return
     * @throws MissingResourceException
     * @throws MetadataException
     */
    private String getStrProperty(String propertyName, String metadataName) throws MissingResourceException, MetadataException
    {
        checkReady();
        
        /* Gambi!!! O nome do metadado .index no banco é indexProperty, porque 'index' é uma palavra
         * reservada para os bancos de dados. Assim, no banco, o metadado 'index' é chamado de 'indexProperty'.
         * Para manter esta correspondência, sempre que o metadado 'index' é chamado, estas linhas a baixo
         * os converte em 'indexProperty': Tati e Lucio 11/05/2007 */
        String dbMetadataName = metadataName;
        if(metadataName.equals(INDEX)) dbMetadataName = ApplicationEntityProperty.INDEX_PROPERTY;

        /* BANCO */
        /* Verifica se o propertyName esta no banco
         * Se nao é defaultMode 
         * Se o metadataName é um metadado persistido pelo banco
   	     * Metadados .type e etc não são persistidos */
        if(!defaultMode && applicationEntityPropertiesMap.containsKey(propertyName) && applicationEntityPropertyMetadataNameMap.containsKey(dbMetadataName)){
        	//verifica o valor: se nao é nulo, return valor
    		Object value = null;
        	try
			{
				/* Obtem a propriedade que tem os metadados */
        		ApplicationEntityProperty prop = applicationEntityPropertiesMap.get(propertyName);
				/* Obtem o valor do metadado na propriedade persistida */
	        	value = org.apache.commons.beanutils.PropertyUtils.getProperty(prop, dbMetadataName);
	        } catch (Exception e)
			{
				throw new MetadataException(MessageList.createSingleInternalError(e));
			}
        		
			if(value != null)
				return value.toString();
			else
				throw new MissingResourceException("","","");

        }else
            /* BUNDLE */
        	if (resourceBundle != null){
        		/* Retira possíveis espaços em branco */
        		String value = resourceBundle.getString(propertyName + "." + metadataName);
        		value = StringUtils.stripEnd(value, " ");
        		return value;
        	}
        throw new MissingResourceException("","","");
    }

    private Class<?> propertyType(String propertyName){
        try
        {
            return PropertyUtils.getPropertyType(entityClass, propertyName);
            
        } catch (Exception e)
        {
            e.printStackTrace();
            return Class.class;
        }
    }
    
    
    /**
     * Este método obtem do arquivo de valores padrões 
     * dos metadados (MetadataDefault.properties) um valor
     * padrão para uma determinada chave solicitada.<br>
     * Exemplo: Valores padrões para EditMask.
     * Nome_Simples_Classe + "." + EDITMASK
     * BigDecimal.editMask=%,.2f
     * 
     * @param keyName
     * @return
     */
    private String getDefaultStr(String propertyName, String metadataName)
    {
        try
        {
            if (defaultResourceBundle != null){
                /* Retira possíveis espaços em branco */
                String value = defaultResourceBundle.getString(propertyName + "." + metadataName);
                value = StringUtils.stripEnd(value, " ");
                return value;
            }
            /* Se não achou o default retorna um valor vazio */
            return "";
        }
        catch (MissingResourceException e)
        { 
            /* Se não achou o default retorna um valor vazio */
            return "";
        }
    }
    
    public String getPropertyLabel(String propertyName) throws MetadataException
    {
        try
        {
            return getStrProperty(propertyName , LABEL);
        }
        catch (MissingResourceException e)
        { 
            // Caso não encontre a Key procurada no arquivo de propriedade da classe
            // utiliza o nome da Key com a primeira letra em maiúsculo. Assim, propriedades
            // como Class.nome, Class.id que não se encontre no arquivo, são 'deduzidas'.
            return StringUtils.capitalize(propertyName);
        }
    }
    
    public String getPropertyHint(String propertyName) throws MetadataException
    {
        try
        {
            return getStrProperty(propertyName , HINT);
        }
        catch (MissingResourceException e)
        {
            return this.getPropertyLabel(propertyName);    
        }
    }
    
    public String getPropertyDescription(String propertyName) throws MetadataException
    {
        try
        {
            return getStrProperty(propertyName , DESCRIPTION);
        }
        catch (MissingResourceException e)
        {
            return "";
        }
    }
    
    public Class<?> getPropertyType(String propertyName) throws MetadataException
    {
        try
        {
            String value = getStrProperty(propertyName , TYPE);
            return Class.forName(value);
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou o tipo declarado no arquivo resulta em nulo
            return null;
        }
        catch (ClassNotFoundException e)
        {
            // Se deu um erro de procura de classe na VM retorna a classe de erro
            return e.getClass();
        }
    }

    public boolean getPropertyRequired(String propertyName) throws MetadataException
    {
        try
        {
            String value = getStrProperty(propertyName , REQUIRED);
            return value.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }
    
    public boolean getPropertyReadOnly(String propertyName) throws MetadataException
    {
        try
        {
            String value = getStrProperty(propertyName , READONLY);
            return value.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }    
    
    public boolean getPropertyCalculated(String propertyName) throws MetadataException
    {
        try
        {
            String value = getStrProperty(propertyName , CALCULATED);
            return value.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }    
    
    public boolean getPropertyVisible(String propertyName) throws MetadataException
    {
        try
        {
            String value = getStrProperty(propertyName , VISIBLE);
            return value.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return true;
        }
    }    
    
    public int getPropertySize(String propertyName) throws MetadataException
    {
    	String value=null;
    	try
        {
            value = getStrProperty(propertyName , SIZE);
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
        	throw new MetadataException(MessageList.create(MetadataException.class, "INVALID_METADATA", value, propertyName, SIZE, entityName));
        }
        catch (MissingResourceException e)
        {
            /* TODO Colocar no MetadataDefaults.properties os valores padrões */
        	// Se não encontrou a propriedade declarada retorna a padrão
            return 40;
        }
    }
    
    public double getPropertyMinimum(String propertyName) throws MetadataException
    {
    	String value=null;
    	try
        {
            value = getStrProperty(propertyName , MINIMUM);
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e)
        {
        	throw new MetadataException(MessageList.create(MetadataException.class, "INVALID_METADATA", value, propertyName, MINIMUM, entityName));
        }
        catch (MissingResourceException e)
        {
            /* TODO Colocar no MetadataDefaults.properties os valores padrões */
        	// Se não encontrou a propriedade declarada retorna a padrão
            return 0.0;
        }
    }

    public double getPropertyMaximum(String propertyName) throws MetadataException
    {
    	String value=null;
    	try
        {
            value = getStrProperty(propertyName , MAXIMUM);
            return Double.parseDouble(value);
        }
        catch (NumberFormatException e)
        {
        	throw new MetadataException(MessageList.create(MetadataException.class, "INVALID_METADATA", value, propertyName, MAXIMUM, entityName));
        }
        catch (MissingResourceException e)
        {
            /* TODO Colocar no MetadataDefaults.properties os valores padrões */
        	// Se não encontrou a propriedade declarada retorna a padrão
            return 999999.0;
        }
    }

    public String getPropertyColorName(String propertyName) throws MetadataException
    {
        try
        {
            String value = getStrProperty(propertyName , COLORNAME);
            return value;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return "";
        }
    }

    public String getPropertyEditMask(String propertyName) throws MetadataException
    {
        try
        {
            String value = getStrProperty(propertyName , EDITMASK);
            return value;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return getDefaultStr(propertyType(propertyName).getSimpleName(), EDITMASK);
        }
    }
    
    public boolean getPropertyEditShowList(String propertyName) throws MetadataException
    {
        try
        {
            String editShowList = getStrProperty(propertyName , EDITSHOWLIST);
            
            return editShowList.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }
    
    public boolean getPropertyIsList(String propertyName) throws MetadataException
    {
        try
        {
            String value = getStrProperty(propertyName , ISLIST);
            
            return value.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }

    public boolean getPropertyIsSet(String propertyName) throws MetadataException
    {
        try
        {
            String value = getStrProperty(propertyName , ISSET);
            
            return value.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }

//    @Override
    public boolean getPropertyEditShowEmbedded(String propertyName)
    		throws MetadataException {
    	return this.getPropertyIsOneToMany(propertyName) || this.getPropertyIsOneToOne(propertyName);
    }
    
//    @Override
    public boolean getPropertyEmbedded(String propertyName)
    		throws MetadataException {
    	return AnnotationUtils.findAnnotation(Embedded.class, this.entityClass, propertyName) != null;
    }
    
    @Deprecated
    private boolean getPropertyIsOneToOne(String propertyName) throws MetadataException
    {
        try
        {
            String value = getStrProperty(propertyName , IS_ONE_TO_ONE);
            
            return value.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }

    @Deprecated
    private boolean getPropertyIsOneToMany(String propertyName) throws MetadataException
    {
        try
        {
            String value = getStrProperty(propertyName , IS_ONE_TO_MANY);
            
            return value.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }

    public boolean getPropertyAllowSubQuery(String propertyName) throws MetadataException
    {
        try
        {
            String value = getStrProperty(propertyName , ALLOW_SUB_QUERY);
            
            return value.equals("true");
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }

    /**
     * Busca a lista que deverá ser mostrada na edição. 
     * @param propertyName = propriedade a ser pesquisada
     * @return lista de elementos
     */
    public List<String> getPropertyValuesList(String propertyName) throws MetadataException
    {
        try
        {
            String editList = getStrProperty(propertyName , VALUESLIST);
            /*
             * Dada uma string, converte-a num array de string considerando que 
             * os elementos estão separados por vírgula.
             */
            String[] list = StringUtils.split(editList,",");
            List<String> lista = new ArrayList<String>();
            
            // Coloca os demais item
            for (String str: list)
            {
                lista.add(str);
            }
            return lista;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return new ArrayList<String>(0);
        }
    }

    public String getPropertyDisplayFormat(String propertyName) throws MetadataException 
    {
        try
        {
            String value = getStrProperty(propertyName , DISPLAYFORMAT);
            return value;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return getDefaultStr(propertyType(propertyName).getSimpleName(), DISPLAYFORMAT);
        }
    }
    
    public String getPropertyDefaultValue(String propertyName) throws MetadataException 
    {
        try
        {
            String value = getStrProperty(propertyName , DEFAULTVALUE);
            return value;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return getDefaultStr(propertyType(propertyName).getSimpleName(), DEFAULTVALUE);
        }
    }
    public int getPropertyIndex(String propertyName) throws MetadataException 
    {
    	String value=null;
    	try
        {
            value = getStrProperty(propertyName , INDEX);
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
        	throw new MetadataException(MessageList.create(MetadataException.class, "INVALID_METADATA", value, propertyName, INDEX, entityName));
        }
        catch (MissingResourceException e)
        {
            /* TODO Colocar no MetadataDefaults.properties os valores padrões */
            // Se não encontrou a propriedade declarada retorna a padrão
            return -1;
        }
    }
    
    public int getPropertyGroup(String propertyName) throws MetadataException 
    {
    	String value=null;
    	try
        {
            value = getStrProperty(propertyName , GROUP);
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
        	throw new MetadataException(MessageList.create(MetadataException.class, "INVALID_METADATA", value, propertyName, GROUP, entityName));
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return IGroupMetadata.GROUP_NOT_DEFINED;
        }
    }
    
    public String getPropertyName(String propertyName) throws MetadataException 
    {
        try
        {
            String value = getStrProperty(propertyName , NAME);
            return value;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return propertyName;
        }
    }

    public String getEntityLabel() throws MetadataException
    {
        try
        {
            String value = getStrEntity(LABEL);
            return value;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return entityName;
        }
    }

    public String getEntityHint() throws MetadataException
    {
        try
        {
            String value = getStrEntity(HINT);
            return value;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return "";
        }
    }

    public String getEntityDescription() throws MetadataException
    {
        try
        {
            String value = getStrEntity(DESCRIPTION);
            return value;
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return "";
        }
    }

    public String getEntityColorName() throws MetadataException
    {
            // Não implementado para .properties
            return "";
    }

    /**
     * Indica se a entidade é do tipo canCreate
     * @return true se a entidade for canCreate, false caso contrário
     * @throws MetadataException
     */
    public boolean getEntityCanCreate() throws MetadataException
    {
        try
        {
            String value = getStrEntity(CANCREATE);
            return Boolean.parseBoolean(value);
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }
    
    /**
     * Indica se a entidade é do tipo runQueryOnOpen
     * @return true se a entidade for runQueryOnOpen, false caso contrário
     * @throws MetadataException
     */
    public boolean getEntityRunQueryOnOpen() throws MetadataException
    {
        try
        {
            String value = getStrEntity(RUN_QUERY_ON_OPEN);
            return Boolean.parseBoolean(value);
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }
    
//    @Override
    public List<String> getPropertiesInQueryGrid() throws MetadataException {
    	return new ArrayList<String>(0);
    }
    
    /**
     * Indica se a entidade é do tipo canRetrieve
     * @return true se a entidade for canRetrieve, false caso contrário
     * @throws MetadataException
     */
    public boolean getEntityCanRetrieve() throws MetadataException
    {
        try
        {
            String value = getStrEntity(CANRETRIEVE);
            return Boolean.parseBoolean(value);
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }
    
    /**
     * Indica se a entidade é do tipo canUpdate
     * @return true se a entidade for canUpdate, false caso contrário
     * @throws MetadataException
     */
    public boolean getEntityCanUpdate() throws MetadataException
    {
        try
        {
            String value = getStrEntity(CANUPDATE);
            return Boolean.parseBoolean(value);
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }
    /**
     * Indica se a entidade é do tipo canDelete
     * @return true se a entidade for canDelete, false caso contrário
     * @throws MetadataException
     */
    public boolean getEntityCanDelete() throws MetadataException
    {
        try
        {
            String value = getStrEntity(CANDELETE);
            return Boolean.parseBoolean(value);
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return false;
        }
    }

    /**
     * Indica se a entidade é do tipo canQuery
     * @return true se a entidade for canQuery, false caso contrário
     * @throws MetadataException
     */
    public boolean getEntityCanQuery() throws MetadataException
    {
        try
        {
            String value = getStrEntity(CANQUERY);
            return Boolean.parseBoolean(value);
        }
        catch (MissingResourceException e)
        {
            // Se não encontrou a propriedade declarada retorna a padrão
            return getEntityCanRetrieve();
        }
    }

	public List<IGroupMetadata> getEntityGroups() throws MetadataException {
		List<IGroupMetadata> groups = new ArrayList<IGroupMetadata>();

		/* BANCO */
		if(!defaultMode && (oApplicationEntity != null)){
			List<ApplicationEntityPropertyGroup> groupList = oApplicationEntity.getApplicationEntityPropertyGroup();
			for (ApplicationEntityPropertyGroup g: groupList)
			{
				GroupMetadata group = new GroupMetadata(g.getIndexGroup(), g.getName());
				group.setLabel(g.getLabel());
				group.setHint(g.getHint());
				group.setDescription(g.getDescription());
				group.setColorName(g.getColorName());
				groups.add(group);	
			}

			/* Ordena os grupos pelo índice, pois como é uma coleção persistida por SET, ele
			 * não possui uma ordem deterministica. E o mecanismo de persistencia pode
			 * recuperar a coleção em qualquer ordem */
			Collections.<IGroupMetadata>sort(groups, IGroupMetadata.COMPARATOR_INDEX);
		}
		/* BUNDLE */
		boolean getNext = true;
		int i = 0;
		do{
			try{
				if (resourceBundle != null){
					/* Retira possíveis espaços em branco */
					String groupName = resourceBundle.getString(GROUP + "." + i + "." + NAME);
					groupName = StringUtils.stripEnd(groupName, " ");

					GroupMetadata group = new GroupMetadata(i, groupName);
					group.setLabel(groupName);
//					group.setHint(g.getHint());
//					group.setDescription(g.getDescription());
//					group.setColorName(g.getColorName());

					/* Verifica se o grupo já foi definido no BANCO */
					if(!groups.contains(group))
						/* Cria e adiciona o grupo na lista de resultados */
						groups.add(group);
				}else
					getNext=false;
				
				/* Incrementa para tentar buscar o próximo grupo */
				i++;
			}catch (MissingResourceException e){
				getNext = false;
			}
		}while(getNext);
		
		return groups;
	}
	
    public void setDaoManager(IDaoManager serviceManager){this.daoManager = serviceManager;}
    public IDaoManager getDaoManager(){return this.daoManager;}

}

