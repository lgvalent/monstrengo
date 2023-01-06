package br.com.orionsoft.monstrengo.crud.services;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;
import br.com.orionsoft.monstrengo.crud.report.entities.ReportParam;

public class OrderCondiction
{
	private long id = IDAO.ENTITY_UNSAVED;
	private boolean active = true;

	private IEntityManager entityMgr;
	private Class entityType;
	private IPropertyMetadata propertyInfo;

	private String propertyPath;
	private String propertyPathLabel;

	public static int ORDER_ASC = 0;
	public static int ORDER_DESC = 1;
	
	private int orderDirection = ORDER_ASC;

	/** Contador manual de ids do objeto.
	 * Utilizado enquanto esta classe não for
	 * mantida pelo mecanismo de persitência e for 
	 * necessária a identificação das instãncias de condições */
	private static long idCounter = 0;
	private long retrieveNextId(){return idCounter++;}
	
	public OrderCondiction(IEntityManager entityManager, Class entityType) throws BusinessException{
		/* Armazena o gerenciador de entidades para usá-lo mais tarde */
		this.entityMgr = entityManager;

		/* Define a entidade atualmente pesquisada */
		this.entityType = entityType;
		
		/* Define a propriedade padrão inicial de pesquisa */
		this.propertyInfo = entityManager.getEntityMetadata(entityType).getPropertyMetadata(IDAO.PROPERTY_ID_NAME);
		
		/* Define um UID Sequencial que identifique esta condição */
		this.id = retrieveNextId();
	}
	
	public OrderCondiction(IEntityManager manager, Class entityType, String propertyName) throws BusinessException{
		this(manager, entityType);

		this.setPropertyPath(propertyName);
	}
	
	public String retrieveHqlExpression(String entityAlias) throws BusinessException{
		String result = entityAlias + "." + propertyPath; 
		if(this.orderDirection == ORDER_ASC)
			result += " ASC";
		else
			result += " DESC";
		
		return result;
	}
	
	public IPropertyMetadata getPropertyInfo() {return propertyInfo;}

	public long getId() {return id;}

	public boolean isActive() {return active;}
	public void setActive(boolean active) {this.active = active;}

	/**
	 * Permite definir a propriedade da condição utilizando 
	 * o nome da propriedade.
	 * A entidade atualmente definida será consultada para
	 * obter a propriedade pelo nome.
	 * Este método aceita caminhos do tipo:
	 * prop1.prop2.prop3.prop4
	 * @return
	 */
	public String getPropertyPath(){return propertyPath;}
	
	public void setPropertyPath(String propertyPath) throws MetadataException, EntityException{
		if(StringUtils.isEmpty(propertyPath))
			this.propertyInfo = null;
		else{
			/* Define o propertyPath. O propertyPathLabel é construído durante o parser */
			this.propertyPath = propertyPath;
			this.propertyPathLabel = "";
			
			/* Inicia a busca das propriedades pela atual entidade da condição */
			Class entity = this.entityType;

			/* Inicia o parser para identificar qual entidade
			 * é a última do caminho e pegar seu tipo */
			String[] props = StringUtils.split(propertyPath,IDAO.PROPERTY_SEPARATOR);
	        for (String prop: props)
	        {
				/* Pega a propriedade atual para verificar seu tipo */
				this.propertyInfo = entityMgr.getEntityMetadata(entity).getPropertyMetadata(prop);

				/* Verifica se a propriedade pode ser manipulada 
				 * pela condição de ordenação. Propriedades calculadas 
				 * não podem ser usadas em ordem
				 */
				if(this.propertyInfo.isCalculated())
					throw new EntityException(MessageList.createSingleInternalError(new Exception("Propriedade calculada não pode ser usada em expressão Hql de ordenação")));

					/* Define o Path label a ser exibido na interface */
				if(this.propertyPathLabel.equals(""))
					this.propertyPathLabel += this.propertyInfo.getLabel();
				else
					this.propertyPathLabel += ReportParam.PROPERTY_PATH_LABEL_SEPARATOR + this.propertyInfo.getLabel();
				
				/* Verifica se não for primitivo obtem o tipo da entidade 
				 * para que na proxima iteração os dados desta entidade seja
				 * buscado e o caminho das propriedades continue sendo percorrido
				 * hierarquicamente */
				if(this.propertyInfo.isEntity()){
					entity = this.propertyInfo.getType();
					
				}else{
					/* Se for primitivo, significa que é a última propriedade do caminho
					 * e já poderá receber um valor assim força um BREAK no for */
					break;
				}
				
				/* Ao terminar este laço o this.propertyInfo apontará para a última propriedade
				 * primitiva do caminho, o this.propertyPath estará definido para ser usado na HQL.
				 */
	        }
		}
	}
	
	public String getPropertyPathLabel() {return propertyPathLabel;}

	public String toString(){
		String result = "";

		result += active?"":"(Desativada)";
		
		if(this.orderDirection == ORDER_ASC)
			result += "« ";
		else
			result += "» ";
		
		result += this.propertyPathLabel;
		
		return result;
	}

	public IEntityMetadata getEntityInfo() throws EntityException {
		return this.entityMgr.getEntityMetadata(entityType);
	}

	/**
	 * Verifica se a atual propriedade já é suportada pela implementação
	 * desta classe. Assim, outras classe podem verifica primeiro se
	 * a propriedade poderá ou não ser suportada 
	 * @param prop Metadados da propriedades
	 */
	public static boolean checkVersionSupport(IPropertyMetadata prop) {
		/* Não permite indexar por coleções */
		if(prop.isCollection())
			return false;
		else
		/* Não exibe propriedades definidas como invisíveis para pesquisa */
		if(!prop.isVisible())
			return false;
		else
		/* Não exibe propriedades calculadas */
		if(prop.isCalculated())
			return false;
		
		return true;
	}

	/** Métodos para facilitar a definição da ordem */
	public boolean isOrderAsc() {return orderDirection==ORDER_ASC;}
	public void setOrderAsc(boolean orderAsc){orderDirection= orderAsc?ORDER_ASC:ORDER_DESC;}

	public boolean isOrderDesc() {return orderDirection==ORDER_DESC;}
	public void setOrderDesc(boolean orderDesc){orderDirection= orderDesc?ORDER_DESC:ORDER_ASC;}

	public int getOrderDirection() {return orderDirection;}
	public void setOrderDirection(int orderDirection) {this.orderDirection = orderDirection;}
}
