package br.com.orionsoft.monstrengo.crud.entity.metadata.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Transient;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.util.AnnotationUtils;
import br.com.orionsoft.monstrengo.core.util.PropertyUtils;
import br.com.orionsoft.monstrengo.crud.documents.entities.ModelDocumentEntity;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDaoManager;
import br.com.orionsoft.monstrengo.crud.entity.metadata.GroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IMetadataHandle;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataHandle;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.CrudOperationType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.EntityType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertiesGroup;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyBooleanType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyCalendarType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyEntityType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyNumericType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyStringType;
import br.com.orionsoft.monstrengo.crud.entity.metadata.xml.templates.PropertyType;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntity;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntityProperty;
import br.com.orionsoft.monstrengo.security.entities.ApplicationEntityPropertyGroup;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * TODO IMPLEMENTAR Definir propriedades dos processos tamb�m como label, hint,
 * description
 * 
 * Este bean � um singleton, e para ser usado, o m�todo setEntityClass(class)
 * deve ser executado. Este m�todo realiza todas as prepara��es de objetos,
 * mapas e listas que servir�o como fonte dos metadados solicitados. Os m�todos
 * getStrEntity(metadataName), getStrProperty(propertyName, metadataName) e
 * getEntityGroups() obtem o metadado solicitado, j� vericando se o metadado
 * est� no banco ou no resourceBundle.
 * 
 * Created on 20/04/2005
 * 
 * @author Lucio
 * @version 20070308
 * 
 * @spring.bean id="MetadataHandleXml"
 * @spring.property name="daoManager" ref="DaoManager"
 */

public class MetadataHandleXmlImpl implements IMetadataHandle {

	/*
	 *  Lucio 20110608: Daqui pra baixo seria a nova estrutura de Metadahandle.
	 *  Atualmente o EntityManager referencia diretamente a classe EntityMetadata, que � uma implementa��o de IEntityMetadata.
	 *  Esta classe, por sua vez, � respons�vel por se auto-construir utilizando o metadahandle passado pra ela.
	 *  O MetadaHandle atual, por sua vez, � respons�vel por pegar os metadados de .properties ou do banco de dados, pois ele possui uma refer�ncia para o DaoManager.
	 *  Assim, a classe EntityMetadata se auto constr�i pegando os metadados do handle os demais metadados, os n�o persist�veis (type, subEntities, etc) ela mesma gera.
	 *  
	 *  NOVA PROPOSTA.
	 *  Seguindo os padr�es e diminuindo o acoplamento, seria interessante dividir o servi�o de obter metadados n�o persist�veis e os persist�veis.
	 *  Mas esta responsabilidade deveria estar nos HANDLES e n�o no EntityMetadata.
	 *  Pra ganhar tempo, vou simplesmente trocar seis por meia d�zia. E fazer o handle pegar
	 *  do .info.xml ao inv�s do .properties.
	 *  
	 *  As linhas que segem s�o prot�tipos da nova proposta e foram mantidas para economizar e guiar futuras implementa��es
	 */
	
	private static final String XSD_SCHEMA_FILE_NAME = "entityMetadata.xsd"; 
	public static final String INFO_XML_FILE_EXTENSION = ".info.xml"; 
	
	private Logger log = LogManager.getLogger(this.getClass());
	
	private EntityType entityType;
	
	public IEntityMetadata retrieveEntityMetadata(Class<?> entityClass) throws MetadataException{
		EntityType entity = prepareEntityMetadataXml(entityClass);
		
		return null;
	}
	
	private EntityType prepareEntityMetadataXml(Class<?> entityClass) throws MetadataException{
		try {
			JAXBContext ctx = JAXBContext.newInstance(EntityType.class);
			Unmarshaller unm = ctx.createUnmarshaller();
			
			/* Define o SCHEMA para for�ar uma valida��o do arquivo e identificar erros do programador */
			SchemaFactory schf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema"); 
			Schema sch = schf.newSchema(new StreamSource(EntityType.class.getResourceAsStream(XSD_SCHEMA_FILE_NAME)));
			unm.setSchema(sch);
			
			/* Este c�digo a princ�pio s� funcionou com o SCHEMA DEFINIDO Sen�o ele n�o busca nada!!! */
			return (EntityType) unm.unmarshal(entityClass.getResourceAsStream(entityClass.getSimpleName() + INFO_XML_FILE_EXTENSION));
		} catch (Exception e) {
			MessageList l = MessageList.create(MetadataHandleXmlImpl.class, "METADATA_NOT_READY", entityClass.getSimpleName());
			l.addAll(MessageList.createSingleInternalError(e));
            throw new MetadataException(l);
		}
	}
	
	public static void main(String[] args) {
		try {
			EntityType entityType = new MetadataHandleXmlImpl().prepareEntityMetadataXml(ModelDocumentEntity.class);
			
//			showEntityType(entityType);

			MetadataHandleXmlImpl m = new MetadataHandleXmlImpl();
			m.setEntityClass(ModelDocumentEntity.class);
		} catch (MetadataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void showEntityType(EntityType entity) {
		System.out.println("=Entity:" + entity.getName() + "=========================");
		System.out.println(" className:" + entity.getClassName());
		System.out.println(" label:" + entity.getLabel());
		System.out.println(" hint:" + entity.getHint());
		System.out.println(" description:" + entity.getDescription());
		System.out.println(" crudOperations:" + entity.getCrudOperations());
		System.out.println(" propertiesInQueryGrid:" + entity.getPropertiesInQueryGrid());
		for(PropertiesGroup g: entity.getGroup()){
			System.out.println(" -Group:" + g.getName() + "-----------------------");
			System.out.println("  label:" + g.getLabel());
			System.out.println("  hint:" + g.getHint());
			System.out.println("  description:" + g.getDescription());
			for(PropertyType p: g.getProperty()){
				System.out.println("  --Property:" + p.getName());
				System.out.println("       label:" + p.getLabel());
				System.out.println("        hint:" + p.getHint());
				System.out.println("      descr.:" + p.getDescription());
				System.out.println("       color:" + p.getColorName());
				System.out.println("     default:" + p.getDefaultValue());
				System.out.println("      format:" + p.getDisplayFormat());
				System.out.println("        mask:" + p.getEditMask());
				System.out.println("     v. list:" + p.getValuesList());
				System.out.println("  calculated:" + p.isCalculated());
				System.out.println("    readOnly:" + p.isReadOnly());
				System.out.println("    required:" + p.isRequired());
				System.out.println("     visible:" + p.isVisible());
				
				if(p instanceof PropertyNumericType){
					PropertyNumericType pn = (PropertyNumericType) p;
					System.out.println("     maximum:" + pn.getMaximum());
					System.out.println("     minimum:" + pn.getMinimum());
				}
						
				if(p instanceof PropertyBooleanType){
					PropertyBooleanType pb = (PropertyBooleanType) p;
				}
						
				if(p instanceof PropertyCalendarType){
					PropertyCalendarType pc = (PropertyCalendarType) p;
				}
						
				if(p instanceof PropertyStringType){
					PropertyStringType ps = (PropertyStringType) p;
				}
						
				if(p instanceof PropertyEntityType){
					PropertyEntityType pe = (PropertyEntityType) p;
					System.out.println("   allowSubQ:" + pe.isAllowSubQuery());
					System.out.println("   edit emb.:" + pe.isEditShowEmbedded());
					System.out.println("   edit list:" + pe.isEditShowList());
				}
						
			}
			

		}
		
		System.out.println("===================================================");
	}

	
	/**
	 * Este m�todo � uma ideia de que o Handle deve retornar um IentityMetadata e n�o ser chamado TODA hora pra
	 * pegar indicidualmente um metadado. Assim, d� pra fazer algumas otimiza��es. E fica, tipo, no padr�o
	 * factory, para fabricar os metadados!!! Mas para isto, precisa alterar outras coisas
	 * que precisam ser analisadas no EntityManager e onde � usado o MetadataHandle
	 * @param entity
	 * @return
	 */
	private IEntityMetadata buildEntityMetadata(EntityType entity){
//		IEntityMetadata entityMetadata = new IEntityMetadata() {
//			
//		};
		
		return null;
	}

	/**
	 * Usando JAXBContext
	 * @param args
	 * @throws JAXBException
	 * @throws SAXException 
	 */
	public static void main__(String[] args) throws Exception{
		System.out.println(ApplicationUser.class.getSuperclass());
		
		long l = System.currentTimeMillis();
		JAXBContext ctx = JAXBContext.newInstance(EntityType.class);
		Unmarshaller unm = ctx.createUnmarshaller();

		SchemaFactory schf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema"); 
		Schema sch = schf.newSchema(new StreamSource(EntityType.class.getResourceAsStream("entityMetadata.xsd")));
		unm.setSchema(sch);

		/* Este c�digo s� funciona com o SCHEMA DEFINIDO Sen�o ele n�o busca nada!!! */
		EntityType ent = (EntityType) unm.unmarshal(EntityType.class.getResourceAsStream("ApplicationUser.info.xml"));
		System.out.println(ent.getGroup().get(0).getName());
		
////		/* Este c�digo funciona sem o SCHEMA */
//		JAXBElement<Entity> ent = unm.unmarshal(new StreamSource(Entity.class.getResourceAsStream("ApplicationUser.info.xml")), Entity.class);
//		System.out.println(ent.getValue().getGroup().get(0).getName());
		
		ctx.createMarshaller().marshal(ent, new File("/home/lucio/test.xml"));
		System.out.println(System.currentTimeMillis() - l);
		
		for(Package p : Package.getPackages())
			System.out.println(p.getName());

	}

	public static void main_(String[] args) throws SAXException, IOException,
			ParserConfigurationException, MetadataException {
		MetadataHandleXmlImpl m = new MetadataHandleXmlImpl();
		m.setEntityClass(ApplicationUser.class);
		System.out.println(m.getEntityDescription());
		System.out.println(m.getEntityLabel());
		System.out.println(m.getEntityHint());
		System.out.println(m.getEntityGroups().get(0).getName());
		System.out.println(m.getEntityGroups().get(0).getProperties().get(0).getDefaultValue());
		
		
		InputStream is = ApplicationUser.class
				.getResourceAsStream("ApplicationUser.info.xml");
		// InputStream is =
		// ClassLoader.getSystemClassLoader().getResourceAsStream(ApplicationUser.class.getName());
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(is);
		doc.getDocumentElement().normalize();
		Node entity = doc.getElementsByTagName("entity").item(0);
		System.out.println("==Entity===================================");
		NamedNodeMap atts = entity.getAttributes();
		for(int i=0; i < atts.getLength();i++){
			System.out.println(atts.item(i).getNodeName() + "=" + atts.item(i).getNodeValue());
		}
		
		NodeList groups = doc.getElementsByTagName("group");
		for(int g=0; g<groups.getLength(); g++){
			System.out.println("  Group===================================");
			NamedNodeMap attss = groups.item(g).getAttributes();
			for(int i=0; i < attss.getLength();i++){
				System.out.println(attss.item(i).getNodeName() + "=" + attss.item(i).getNodeValue());
			}
			System.out.println("    Property===================================");
			NamedNodeMap attsss = groups.item(g).getNextSibling().getAttributes();
			for(int i=0; i < attsss.getLength();i++){
				System.out.println(attss.item(i).getNodeName() + "====" + attss.item(i).getNodeValue());
			}
		}
	}


	// Lucio 20110608: Daqui pra baixo segue o c�digo seis por meia d�zia
	
	// CONSTANTES
	private static final String INDEX = "index";
	private static final String GROUP = "group";
	private static final String NAME = "name";
	private static final String LABEL = "label";
	private static final String HINT = "hint";
	private static final String DESCRIPTION = "description";
	private static final String OVERRIDE_TYPE = "overrideType";
//	private static final String SIZE = "size";
	private static final String COLORNAME = "colorName";
	private static final String DISPLAYFORMAT = "displayFormat";
	private static final String DEFAULTVALUE = "defaultValue";
	private static final String EDITMASK = "editMask";
	private static final String CRUD_OPERATIONS = "crudOperations";
	private static final String RUN_QUERY_ON_OPEN = "runQueryOnOpen";

	private static final String VISIBLE = "visible";
	private static final String REQUIRED = "required";
	private static final String READONLY = "readOnly";
	private static final String CALCULATED = "calculated";
	private static final String MINIMUM = "minimum";
	private static final String MAXIMUM = "maximum";
	private static final String EDITSHOWLIST = "editShowList";
	private static final String EDITSHOWEMBEDDED = "editShowEmbedded";
//	private static final String ISLIST = "isList";
//	private static final String ISSET = "isSet";
	private static final String ALLOW_SUB_QUERY = "allowSubQuery";
	private static final String VALUESLIST = "valuesList";

//	private static final String IS_ONE_TO_ONE = "oneToOne";
//	private static final String IS_ONE_TO_MANY = "oneToMany";

	private Class entityClass;
	private String entityName;
	private ApplicationEntity oApplicationEntity;
	private boolean defaultMode = false;

	private static String XML_DOCUMENT_NAME_FOR_DEFAULTS_VALUES = "MetadataDefaults"
			+ INFO_XML_FILE_EXTENSION;
	private static String DEFAULT_BUNDLE_NAME = MetadataHandle.class
			.getPackage().getName() + ".MetadataDefaults";
	private ResourceBundle defaultResourceBundle;
	private boolean ready = false;
	private Map<String, ApplicationEntityProperty> applicationEntityPropertiesMap = new HashMap<String, ApplicationEntityProperty>();
	private Map<String, PropertyType> xmlPropertiesMap = new HashMap<String, PropertyType>();
	private Map applicationEntityMetadataNameMap = new HashMap();
	private Map applicationEntityPropertyMetadataNameMap = new HashMap();


	private IDaoManager daoManager;

	private boolean checkReady() throws MetadataException {
		if (!ready)
			throw new MetadataException(MessageList.create(
					MetadataException.class, "METADATA_NOT_READY", entityName));

		return ready;
	}

	public Class getEntityClass() {
		return this.entityClass;
	}
	public void setEntityClass(Class entityClass) {
		setEntityClass(entityClass, false);
	}

	public void setEntityClass(Class entityClass, boolean defaultMode) {
		/*
		 * Limpa o mapa que armazena os metadados que foram encontrados
		 * persistidos no banco de dados e no XML
		 */
		applicationEntityPropertiesMap.clear();
		xmlPropertiesMap.clear();
		
		this.entityClass = entityClass;
		this.entityName = entityClass.getSimpleName();
		this.defaultMode = defaultMode;

		/* BANCO */
		try {
			/*
			 * Busca no banco o objeto de metadados persistido referente a
			 * entidade que est� sendo solicitada Obs.: O MetadataHandle
			 * encontra-se numa camada inferior ao EntityManger, logo, ele n�o
			 * pode usar os recursos de IEntity para buscar objetos e usa ,
			 * entao, busca em baixo nivel pelo DAO
			 */
			List list = null;
			if(daoManager != null){
				IDAO dao = daoManager.getDaoByEntity(ApplicationEntity.class);
				list = dao.getList(IDAO.ENTITY_ALIAS_HQL + "."
						+ ApplicationEntity.CLASS_NAME + "='"
						+ entityClass.getName() + "'");
			}

			/* Verifica se achou o objeto */
			if (list!=null && !list.isEmpty()) {
				/* Define o objeto que ser� utilizado pelo Handle para obter os
				 * metadados solicitadas */
				this.oApplicationEntity = (ApplicationEntity) list.get(0);

				/*
				 * Prepara os mapas contendo os metadados que podem ser
				 * recuperados do banco. �til para descobrir se um metadataName
				 * solicitado est� no banco ou deve ser buscado no
				 * resourceBundle
				 */
				try {
					this.applicationEntityMetadataNameMap = org.apache.commons.beanutils.BeanUtils
							.describe(oApplicationEntity);
					this.applicationEntityPropertyMetadataNameMap = org.apache.commons.beanutils.BeanUtils
							.describe(new ApplicationEntityProperty());
				} catch (Exception e) {
					throw new MetadataException(
							MessageList.createSingleInternalError(e));
				}

				/*
				 * Prepara o mapa que contem todas as propriedades da entidade
				 * com os metadados que est�o persistido no banco.
				 */
				List<ApplicationEntityProperty> listProp = oApplicationEntity
						.getApplicationEntityProperty();
				for (ApplicationEntityProperty prop : listProp) {
					applicationEntityPropertiesMap.put(prop.getName(), prop);
				}
			} else {
				if (log.isDebugEnabled())
					log.debug("Os metadados da entidade " + this.entityName
							+ " n�o foram encontrados no banco");
				oApplicationEntity = null;
			}
		} catch (BusinessException e1) {
			if (log.isDebugEnabled())
				log.debug("Houve um erro ao executar o servi�o: "
						+ e1.getMessage());
			oApplicationEntity = null;
		}

		/* XML */

		try {
			this.entityType = prepareEntityMetadataXml(entityClass);
			
			/* Prepara o mapa com todos as propriedades, pois no XML elas est�o hierarquicamente dentro de seus respectivos grupos */
			for(PropertiesGroup g: entityType.getGroup()){
				for(PropertyType p: g.getProperty()){
					this.xmlPropertiesMap.put(p.getName(), p);
				}
			}
		} catch (Exception e) {
			if (log.isDebugEnabled())
				log.debug("O arquivo de propriedades da classe "
						+ this.entityName
						+ " n�o foi processado. Verifique se no pacote da classe existe um arquivo '"
						+ this.entityName + INFO_XML_FILE_EXTENSION + "'");
			this.entityType = null;
			e.printStackTrace();
		}

		/* BUNDLE DEFAULT */
		/*
		 * Se n�o encontrar o arquivo .properties relacionado � classe, usa o
		 * arquivo default (MetadataDefaults.properties)
		 */
		// try{
		// InputStream is =
		// MetadataHandleXmlImpl.class.getResourceAsStream(XML_DOCUMENT_NAME_FOR_DEFAULTS_VALUES);
		// this.xmlDocumentForDefaultsValues =
		// DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
		// this.xmlDocumentForDefaultsValues.getDocumentElement().normalize();
		// }
		// catch(Exception e)
		// {
		// if (log.isDebugEnabled())
		// log.debug("O arquivo de propriedades default n�o foi encontrado. Verifique se existe o arquivo: "
		// + MetadataHandleXmlImpl.class.getPackage().getName() + "." +
		// XML_DOCUMENT_NAME_FOR_DEFAULTS_VALUES);
		// this.xmlDocumentForDefaultsValues = null;
		// e.printStackTrace();
		// }
		try {
			defaultResourceBundle = ResourceBundle
					.getBundle(DEFAULT_BUNDLE_NAME);
		} catch (MissingResourceException e) {
			if (log.isDebugEnabled())
				log.debug("O arquivo de propriedades default n�o foi encontrado");
			defaultResourceBundle = null;
		}

		this.ready = true;
	}

	/**
	 * getPropertyStr(propName, metadataName) getPropertyStr(name, .label){
	 * entity.getApplicationEntityProperties()
	 * 
	 * }
	 * 
	 * @param metadataName
	 * @return
	 * @throws MissingResourceException
	 * @throws MetadataException
	 * @throws EntityException
	 * @throws PropertyValueException
	 */
	private String getStrEntity(String metadataName)
			throws MissingResourceException, MetadataException {
		checkReady();

		try {
			Object value = null;
			/*
			 * Verifica se o metadado solicitado � persistido e se foi definido
			 * no banco. Metadados .canCreate e etc n�o s�o persistidos
			 */
			/* BANCO */
			if (applicationEntityMetadataNameMap.containsKey(metadataName))
				if (oApplicationEntity != null)
					value = org.apache.commons.beanutils.PropertyUtils
							.getProperty(oApplicationEntity, metadataName);

			if (!defaultMode && value != null)
				return value.toString();
			else
			/* BUNDLE */
			if (this.entityType != null) {
				/* Pega a Raiz do XML e tenta pegar o atributo solicitado, sen�o retorna NULL */
				value = PropertyUtils.getPropertyValue(this.entityType, metadataName);
				if(value != null)
					return value.toString();
			}
		} catch (MissingResourceException e) {
			throw e;
		} catch (Exception e) {
			throw new MetadataException(
					MessageList.createSingleInternalError(e));
		}
		throw new MissingResourceException("", "", "");
	}

	/**
	 * getPropertyStr(propName, metadataName) getPropertyStr(name, .label){
	 * entity.getApplicationEntityProperties()
	 * 
	 * }
	 * 
	 * @param propertyName
	 * @return
	 * @throws MissingResourceException
	 * @throws MetadataException
	 */
	private String getStrProperty(String propertyName, String metadataName)
			throws MissingResourceException, MetadataException {
		checkReady();

		/*
		 * Gambi!!! O nome do metadado .index no banco � indexProperty, porque
		 * 'index' � uma palavra reservada para os bancos de dados. Assim, no
		 * banco, o metadado 'index' � chamado de 'indexProperty'. Para manter
		 * esta correspond�ncia, sempre que o metadado 'index' � chamado, estas
		 * linhas a baixo os converte em 'indexProperty': Tati e Lucio
		 * 11/05/2007
		 */
		String dbMetadataName = metadataName;
		if (metadataName.equals(INDEX))
			dbMetadataName = ApplicationEntityProperty.INDEX_PROPERTY;

		/* BANCO */
		/*
		 * Verifica se o propertyName esta no banco Se nao � defaultMode Se o
		 * metadataName � um metadado persistido pelo banco Metadados .type e
		 * etc n�o s�o persistidos
		 */
		if (!defaultMode
				&& applicationEntityPropertiesMap.containsKey(propertyName)
				&& applicationEntityPropertyMetadataNameMap
						.containsKey(dbMetadataName)) {
			// verifica o valor: se nao � nulo, return valor
			Object value = null;
			try {
				/* Obtem a propriedade que tem os metadados */
				ApplicationEntityProperty prop = applicationEntityPropertiesMap
						.get(propertyName);
				/* Obtem o valor do metadado na propriedade persistida */
				value = org.apache.commons.beanutils.PropertyUtils.getProperty(
						prop, dbMetadataName);
			} catch (Exception e) {
				throw new MetadataException(
						MessageList.createSingleInternalError(e));
			}

			if (value != null)
				return value.toString();
			else
				throw new MissingResourceException("", "", "");

		} else
		/* XML */
		if (this.entityType != null) {
			/* Pega a propriedade no mapa montado durante a prepara��o da classe */
			if(this.xmlPropertiesMap.containsKey(propertyName)){
				
				/* Busca o atributo para tentar pegar o metadados */
				String value;
				try {
					value = PropertyUtils.getPropertyValue(this.xmlPropertiesMap.get(propertyName), metadataName).toString();
					if(value != null)
						return value;
				} catch (Exception e) {
					
				}
			}
		}
		throw new MissingResourceException("", "", "");
	}

	private Class<?> propertyType(String propertyName) {
		try {
			return PropertyUtils.getPropertyType(entityClass, propertyName);

		} catch (Exception e) {
			e.printStackTrace();
			return Class.class;
		}
	}

	/**
	 * Este m�todo obtem do arquivo de valores padr�es dos metadados
	 * (MetadataDefault.properties) um valor padr�o para uma determinada chave
	 * solicitada.<br>
	 * Exemplo: Valores padr�es para EditMask. Nome_Simples_Classe + "." +
	 * EDITMASK BigDecimal.editMask=%,.2f
	 * 
	 * @param keyName
	 * @return
	 */
	private String getDefaultStr(String propertyName, String metadataName) {
		try {
			if (defaultResourceBundle != null) {
				/* Retira poss�veis espa�os em branco */
				String value = defaultResourceBundle.getString(propertyName
						+ "." + metadataName);
				value = StringUtils.stripEnd(value, " ");
				return value;
			}
			/* Se n�o achou o default retorna um valor vazio */
			return "";
		} catch (MissingResourceException e) {
			/* Se n�o achou o default retorna um valor vazio */
			return "";
		}
	}

	public String getPropertyLabel(String propertyName)
			throws MetadataException {
		try {
			return getStrProperty(propertyName, LABEL);
		} catch (MissingResourceException e) {
			// Caso n�o encontre a Key procurada no arquivo de propriedade da
			// classe
			// utiliza o nome da Key com a primeira letra em mai�sculo. Assim,
			// propriedades
			// como Class.nome, Class.id que n�o se encontre no arquivo, s�o
			// 'deduzidas'.
			return StringUtils.capitalize(propertyName);
		}
	}

	public String getPropertyHint(String propertyName) throws MetadataException {
		try {
			return getStrProperty(propertyName, HINT);
		} catch (MissingResourceException e) {
			return this.getPropertyLabel(propertyName);
		}
	}

	public String getPropertyDescription(String propertyName)
			throws MetadataException {
		try {
			return getStrProperty(propertyName, DESCRIPTION);
		} catch (MissingResourceException e) {
			return "";
		}
	}

	public Class getPropertyType(String propertyName) throws MetadataException {
		try {
			/* Procura o OVERRIDE_TYPE nos metadados */
			String value = getStrProperty(propertyName, OVERRIDE_TYPE);
            return Class.forName(value);
		} catch (Exception e) {
			/* N�o faz nada aqui, deixa o m�todo continuar */
		}

		try {
			/* Procura um generics em cole��es */
			if(getPropertyIsList(propertyName)||getPropertyIsSet(propertyName)){
				return (Class<?>) PropertyUtils.getPropertyGenericsDeclaration(entityClass, propertyName).getActualTypeArguments()[0];
			}
			return PropertyUtils.getPropertyType(entityClass, propertyName);
		} catch (NullPointerException e) {
			// Se n�o encontrou o tipo declarado no arquivo resulta em nulo
			return e.getClass();
		}
	}

	public boolean getPropertyRequired(String propertyName)
			throws MetadataException {
		try {
			String value = getStrProperty(propertyName, REQUIRED);
			return value.equals("true");
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return false;
		}
	}

	public boolean getPropertyReadOnly(String propertyName)
			throws MetadataException {
		/* Propriedade id sempre � READ-ONLY */
		if(propertyName.equals(IDAO.PROPERTY_ID_NAME))
			return true;
		
		try {
			String value = getStrProperty(propertyName, READONLY);
			return value.equals("true");
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return false;
		}
	}

	public boolean getPropertyCalculated(String propertyName)
			throws MetadataException {
		/* Propriedade transient sempre � calculado */
		Transient anno = AnnotationUtils.findAnnotation(Transient.class, this.entityClass, propertyName);
		if(anno!=null)
			return true;

		try {
			String value = getStrProperty(propertyName, CALCULATED);
			return value.equals("true");
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return false;
		}
	}

	public boolean getPropertyVisible(String propertyName)
			throws MetadataException {
		try {
			String value = getStrProperty(propertyName, VISIBLE);
			return value.equals("true");
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return true;
		}
	}

	
	public int getPropertySize(String propertyName) throws MetadataException {
		Column col = AnnotationUtils.findAnnotation(Column.class, this.entityClass, propertyName);
		if(col!=null)
			return col.length();
		else{
			/* Lucio 20110621 Size n�o � aplic�vel a todas as propriedades 
			 * Logo sempre dava erro de busca ao buscar size pra uma propriedade
			 * do tipo inteiro ou entity */
//			// Se n�o encontrou a propriedade declarada retorna a padr�o
//			String value = getDefaultStr(propertyType(propertyName).getSimpleName(),
//			"size");
//			if(value.isEmpty())
				return 0;
//			return Integer.parseInt(value);
		}
	}

	public double getPropertyMinimum(String propertyName)
			throws MetadataException {
		String value = null;
		try {
			value = getStrProperty(propertyName, MINIMUM);
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			throw new MetadataException(MessageList.create(
					MetadataException.class, "INVALID_METADATA", value,
					propertyName, MINIMUM, entityName));
		} catch (MissingResourceException e) {
			/* TODO Colocar no MetadataDefaults.properties os valores padr�es */
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return 0.0;
		}
	}

	public double getPropertyMaximum(String propertyName)
			throws MetadataException {
		String value = null;
		try {
			value = getStrProperty(propertyName, MAXIMUM);
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			throw new MetadataException(MessageList.create(
					MetadataException.class, "INVALID_METADATA", value,
					propertyName, MAXIMUM, entityName));
		} catch (MissingResourceException e) {
			/* TODO Colocar no MetadataDefaults.properties os valores padr�es */
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return 999999.0;
		}
	}

	public String getPropertyColorName(String propertyName)
			throws MetadataException {
		try {
			String value = getStrProperty(propertyName, COLORNAME);
			return value;
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return "";
		}
	}

	public String getPropertyEditMask(String propertyName)
			throws MetadataException {
		try {
			String value = getStrProperty(propertyName, EDITMASK);
			return value;
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return getDefaultStr(propertyType(propertyName).getSimpleName(),
					EDITMASK);
		}
	}

	public boolean getPropertyEditShowList(String propertyName)
			throws MetadataException {
		try {
			String editShowList = getStrProperty(propertyName, EDITSHOWLIST);

			return editShowList.equals("true");
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return false;
		}
	}

	public boolean getPropertyIsList(String propertyName)
			throws MetadataException {
			return List.class.isAssignableFrom(PropertyUtils.getPropertyType(this.entityClass, propertyName));
	}

	public boolean getPropertyIsSet(String propertyName)
			throws MetadataException {
		return Set.class.isAssignableFrom(PropertyUtils.getPropertyType(this.entityClass, propertyName));
	}

//	@Override
    public boolean getPropertyEmbedded(String propertyName)
    		throws MetadataException {
    	return AnnotationUtils.findAnnotation(Embedded.class, this.entityClass, propertyName) != null;
    }

//	@Override
	public boolean getPropertyEditShowEmbedded(String propertyName)
			throws MetadataException {
		/* Verifica se a propriedade � @Embedded para for�ar o EditShowEmbedded */
		Embedded anno = AnnotationUtils.findAnnotation(Embedded.class, this.entityClass, propertyName);
		if(anno!=null)
			return true;

		try {
			
			String value = getStrProperty(propertyName, EDITSHOWEMBEDDED);

			return value.equals("true");
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return false;
		}
	}
	
	public boolean getPropertyAllowSubQuery(String propertyName)
			throws MetadataException {
		try {
			String value = getStrProperty(propertyName, ALLOW_SUB_QUERY);

			return value.equals("true");
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return false;
		}
	}

	/**
	 * Busca a lista que dever� ser mostrada na edi��o.
	 * 
	 * @param propertyName
	 *            = propriedade a ser pesquisada
	 * @return lista de elementos
	 */
	public List<String> getPropertyValuesList(String propertyName)
			throws MetadataException {
		try {
			String editList = getStrProperty(propertyName, VALUESLIST);
			/*
			 * Dada uma string, converte-a num array de string considerando que
			 * os elementos est�o separados por v�rgula.
			 */
			String[] list = StringUtils.split(editList, ",");
			List<String> lista = new ArrayList<String>();

			// Coloca os demais item
			for (String str : list) {
				lista.add(str);
			}
			return lista;
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return new ArrayList<String>(0);
		}
	}

	public String getPropertyDisplayFormat(String propertyName)
			throws MetadataException {
		try {
			String value = getStrProperty(propertyName, DISPLAYFORMAT);
			return value;
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return getDefaultStr(propertyType(propertyName).getSimpleName(),
					DISPLAYFORMAT);
		}
	}

	public String getPropertyDefaultValue(String propertyName)
			throws MetadataException {
		try {
			String value = getStrProperty(propertyName, DEFAULTVALUE);
			return value;
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return getDefaultStr(propertyType(propertyName).getSimpleName(),
					DEFAULTVALUE);
		}
	}

	public int getPropertyIndex(String propertyName) throws MetadataException {
		int i = 0;
		for(PropertiesGroup g: this.entityType.getGroup()){
			for(PropertyType p : g.getProperty()){
				if(p.getName().equals(propertyName)){
					return i;
				}
				i++;
			}
		}
		return -1;
//		String value = null;
//		try {
//			value = getStrProperty(propertyName, INDEX);
//			return Integer.parseInt(value);
//		} catch (NumberFormatException e) {
//			throw new MetadataException(MessageList.create(
//					MetadataException.class, "INVALID_METADATA", value,
//					propertyName, INDEX, entityName));
//		} catch (MissingResourceException e) {
//			/* TODO Colocar no MetadataDefaults.properties os valores padr�es */
//			// Se n�o encontrou a propriedade declarada retorna a padr�o
//			return -1;
//		}
	}

	/** TODO Est� percorrendo o grupo inteiro para achar a propriedade dentro do grupo
	 * isto � extremamente CARO!!!!!
	 * Mudar todo o contexto de GROUP e GROUP_INDEX das propriedades, j� que o XML
	 * imprime uma hierarquia. O problema � no banco de dados, pois os metadados 
	 * tamb�m est�o no banco.
	 * Tem que alterar a estrutura e colocar tudo em ordem agora, com a nova estrutura do XML
	 * J� que foi separada as propriedades vol�teis em XML e as n�o vol�teis em Annotations de JPA (@Column(length=20))
	 * 
	 */
	public int getPropertyGroup(String propertyName) throws MetadataException {
		int i = 0;
		for(PropertiesGroup g: this.entityType.getGroup()){
			for(PropertyType p : g.getProperty()){
				if(p.getName().equals(propertyName)){
					return i;
				}
			}
			i++;
		}

		return IGroupMetadata.GROUP_NOT_DEFINED;
		// Se n�o encontrou a propriedade declarada retorna a padr�o
	}

	public String getPropertyName(String propertyName) throws MetadataException {
		try {
			String value = getStrProperty(propertyName, NAME);
			return value;
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return propertyName;
		}
	}

	public String getEntityLabel() throws MetadataException {
		try {
			String value = getStrEntity(LABEL);
			return value;
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return entityName;
		}
	}

	public String getEntityHint() throws MetadataException {
		try {
			String value = getStrEntity(HINT);
			return value;
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return "";
		}
	}

	public String getEntityDescription() throws MetadataException {
		try {
			String value = getStrEntity(DESCRIPTION);
			return value;
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return "";
		}
	}

	public String getEntityColorName() throws MetadataException {
		try {
			String value = getStrEntity(COLORNAME);
			return value;
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return "";
		}
	}

	/**
	 * Indica se a entidade � do tipo canCreate
	 * 
	 * @return true se a entidade for canCreate, false caso contr�rio
	 * @throws MetadataException
	 */
	public boolean getEntityCanCreate() throws MetadataException {
		try {
			return getStrEntity(CRUD_OPERATIONS).contains(CrudOperationType.CREATE.name());
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return false;
		}
	}

	/**
	 * Indica se a entidade � do tipo runQueryOnOpen
	 * 
	 * @return true se a entidade for runQueryOnOpen, false caso contr�rio
	 * @throws MetadataException
	 */
	public boolean getEntityRunQueryOnOpen() throws MetadataException {
		try {
			String value = getStrEntity(RUN_QUERY_ON_OPEN);
			return Boolean.parseBoolean(value);
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return false;
		}
	}

	/**
	 * Indica se a entidade � do tipo canRetrieve
	 * 
	 * @return true se a entidade for canRetrieve, false caso contr�rio
	 * @throws MetadataException
	 */
	public boolean getEntityCanRetrieve() throws MetadataException {
		try {
			return getStrEntity(CRUD_OPERATIONS).contains(CrudOperationType.RETRIEVE.name());
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return false;
		}
	}

	/**
	 * Indica se a entidade � do tipo canUpdate
	 * 
	 * @return true se a entidade for canUpdate, false caso contr�rio
	 * @throws MetadataException
	 */
	public boolean getEntityCanUpdate() throws MetadataException {
		try {
			return getStrEntity(CRUD_OPERATIONS).contains(CrudOperationType.UPDATE.name());
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return false;
		}
	}

	/**
	 * Indica se a entidade � do tipo canDelete
	 * 
	 * @return true se a entidade for canDelete, false caso contr�rio
	 * @throws MetadataException
	 */
	public boolean getEntityCanDelete() throws MetadataException {
		try {
			return getStrEntity(CRUD_OPERATIONS).contains(CrudOperationType.DELETE.name());
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return false;
		}
	}

	/**
	 * Indica se a entidade � do tipo canQuery
	 * 
	 * @return true se a entidade for canQuery, false caso contr�rio
	 * @throws MetadataException
	 */
	public boolean getEntityCanQuery() throws MetadataException {
		try {
			return getStrEntity(CRUD_OPERATIONS).contains(CrudOperationType.QUERY.name());
		} catch (MissingResourceException e) {
			// Se n�o encontrou a propriedade declarada retorna a padr�o
			return false;
		}
	}

	/**
	 * Indica se a entidade � do tipo canDelete
	 * 
	 * @return true se a entidade for canDelete, false caso contr�rio
	 * @throws MetadataException
	 */
	
	public List<String> getPropertiesInQueryGrid() throws MetadataException {
		List<String> result = new ArrayList<String>(this.entityType.getPropertiesInQueryGrid().size());
		for(PropertyType p: this.entityType.getPropertiesInQueryGrid()){
			result.add(p.getName());
		}
		return result;
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

			/* Ordena os grupos pelo �ndice, pois como � uma cole��o persistida por SET, ele
			 * n�o possui uma ordem deterministica. E o mecanismo de persistencia pode
			 * recuperar a cole��o em qualquer ordem */
			Collections.<IGroupMetadata>sort(groups, IGroupMetadata.COMPARATOR_INDEX);
		}
		
		/* XML */
		if (this.entityType != null){
			/* Pega todos os grupos do XML*/
			int i = 0;
			for(PropertiesGroup g: this.entityType.getGroup()){
				/* Retira poss�veis espa�os em branco */
				GroupMetadata group = new GroupMetadata(i++, g.getName());
				group.setLabel(g.getLabel());
				group.setHint(g.getHint());
				group.setDescription(g.getDescription());
				group.setColorName(g.getColorName());
				/* Verifica se o grupo j� foi definido no BANCO */
				if(!groups.contains(group))
					/* Cria e adiciona o grupo na lista de resultados */
					groups.add(group);
			}
		}

		/* BUNDLE */
//		boolean getNext = true;
//		int i = 0;
//		do{
//			try{
//				if (xmlDocument != null){
//					/* Retira poss�veis espa�os em branco */
//					String groupName = xmlDocument.getString(GROUP + "." + i + "." + NAME);
//					groupName = StringUtils.stripEnd(groupName, " ");
//
//					GroupMetadata group = new GroupMetadata(i, groupName);
//
//					/* Verifica se o grupo j� foi definido no BANCO */
//					if(!groups.contains(group))
//						/* Cria e adiciona o grupo na lista de resultados */
//						groups.add(group);
//				}else
//					getNext=false;
//				
//				/* Incrementa para tentar buscar o pr�ximo grupo */
//				i++;
//			}catch (MissingResourceException e){
//				getNext = false;
//			}
//		}while(getNext);
		
		return groups;
	}

	public void setDaoManager(IDaoManager serviceManager) {
		this.daoManager = serviceManager;
	}

	public IDaoManager getDaoManager() {
		return this.daoManager;
	}

}
