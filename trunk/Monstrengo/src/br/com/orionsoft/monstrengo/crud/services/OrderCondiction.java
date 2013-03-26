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
	 * Utilizado enquanto esta classe n�o for
	 * mantida pelo mecanismo de persit�ncia e for 
	 * necess�ria a identifica��o das inst�ncias de condi��es */
	private static long idCounter = 0;
	private long retrieveNextId(){return idCounter++;}
	
	public OrderCondiction(IEntityManager entityManager, Class entityType) throws BusinessException{
		/* Armazena o gerenciador de entidades para us�-lo mais tarde */
		this.entityMgr = entityManager;

		/* Define a entidade atualmente pesquisada */
		this.entityType = entityType;
		
		/* Define a propriedade padr�o inicial de pesquisa */
		this.propertyInfo = entityManager.getEntityMetadata(entityType).getPropertyMetadata(IDAO.PROPERTY_ID_NAME);
		
		/* Define um UID Sequencial que identifique esta condi��o */
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
	 * Permite definir a propriedade da condi��o utilizando 
	 * o nome da propriedade.
	 * A entidade atualmente definida ser� consultada para
	 * obter a propriedade pelo nome.
	 * Este m�todo aceita caminhos do tipo:
	 * prop1.prop2.prop3.prop4
	 * @return
	 */
	public String getPropertyPath(){return propertyPath;}
	
	public void setPropertyPath(String propertyPath) throws MetadataException, EntityException{
		if(StringUtils.isEmpty(propertyPath))
			this.propertyInfo = null;
		else{
			/* Define o propertyPath. O propertyPathLabel � constru�do durante o parser */
			this.propertyPath = propertyPath;
			this.propertyPathLabel = "";
			
			/* Inicia a busca das propriedades pela atual entidade da condi��o */
			Class entity = this.entityType;

			/* Inicia o parser para identificar qual entidade
			 * � a �ltima do caminho e pegar seu tipo */
			String[] props = StringUtils.split(propertyPath,IDAO.PROPERTY_SEPARATOR);
	        for (String prop: props)
	        {
				/* Pega a propriedade atual para verificar seu tipo */
				this.propertyInfo = entityMgr.getEntityMetadata(entity).getPropertyMetadata(prop);

				/* Verifica se a propriedade pode ser manipulada 
				 * pela condi��o de ordena��o. Propriedades calculadas 
				 * n�o podem ser usadas em ordem
				 */
				if(this.propertyInfo.isCalculated())
					throw new EntityException(MessageList.createSingleInternalError(new Exception("Propriedade calculada n�o pode ser usada em express�o Hql de ordena��o")));

					/* Define o Path label a ser exibido na interface */
				if(this.propertyPathLabel.equals(""))
					this.propertyPathLabel += this.propertyInfo.getLabel();
				else
					this.propertyPathLabel += ReportParam.PROPERTY_PATH_LABEL_SEPARATOR + this.propertyInfo.getLabel();
				
				/* Verifica se n�o for primitivo obtem o tipo da entidade 
				 * para que na proxima itera��o os dados desta entidade seja
				 * buscado e o caminho das propriedades continue sendo percorrido
				 * hierarquicamente */
				if(this.propertyInfo.isEntity()){
					entity = this.propertyInfo.getType();
					
				}else{
					/* Se for primitivo, significa que � a �ltima propriedade do caminho
					 * e j� poder� receber um valor assim for�a um BREAK no for */
					break;
				}
				
				/* Ao terminar este la�o o this.propertyInfo apontar� para a �ltima propriedade
				 * primitiva do caminho, o this.propertyPath estar� definido para ser usado na HQL.
				 */
	        }
		}
	}
	
	public String getPropertyPathLabel() {return propertyPathLabel;}

	public String toString(){
		String result = "";

		result += active?"":"(Desativada)";
		
		if(this.orderDirection == ORDER_ASC)
			result += "� ";
		else
			result += "� ";
		
		result += this.propertyPathLabel;
		
		return result;
	}

	public IEntityMetadata getEntityInfo() throws EntityException {
		return this.entityMgr.getEntityMetadata(entityType);
	}

	/**
	 * Verifica se a atual propriedade j� � suportada pela implementa��o
	 * desta classe. Assim, outras classe podem verifica primeiro se
	 * a propriedade poder� ou n�o ser suportada 
	 * @param prop Metadados da propriedades
	 */
	public static boolean checkVersionSupport(IPropertyMetadata prop) {
		/* N�o permite indexar por cole��es */
		if(prop.isCollection())
			return false;
		else
		/* N�o exibe propriedades definidas como invis�veis para pesquisa */
		if(!prop.isVisible())
			return false;
		else
		/* N�o exibe propriedades calculadas */
		if(prop.isCalculated())
			return false;
		
		return true;
	}

	/** M�todos para facilitar a defini��o da ordem */
	public boolean isOrderAsc() {return orderDirection==ORDER_ASC;}
	public void setOrderAsc(boolean orderAsc){orderDirection= orderAsc?ORDER_ASC:ORDER_DESC;}

	public boolean isOrderDesc() {return orderDirection==ORDER_DESC;}
	public void setOrderDesc(boolean orderDesc){orderDirection= orderDesc?ORDER_DESC:ORDER_ASC;}

	public int getOrderDirection() {return orderDirection;}
	public void setOrderDirection(int orderDirection) {this.orderDirection = orderDirection;}
}
