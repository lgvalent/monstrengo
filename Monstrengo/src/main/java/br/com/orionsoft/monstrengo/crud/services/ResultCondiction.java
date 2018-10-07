package br.com.orionsoft.monstrengo.crud.services;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.report.entities.ReportParam;

public class ResultCondiction
{
	private long id = IDAO.ENTITY_UNSAVED;
	private boolean visible = true;

	private IPropertyMetadata propertyInfo;
	private String propertyPath;
	private String propertyPathLabel;
	
	private Integer resultIndex;

	/** Contador manual de ids do objeto.
	 * Utilizado enquanto esta classe n�o for
	 * mantida pelo mecanismo de persit�ncia e for 
	 * necess�ria a identifica��o das inst�ncias de condi��es */
	private static long idCounter = 0;
	private long retrieveNextId(){return idCounter++;}
	
	public ResultCondiction(IPropertyMetadata propertyInfo, String propertyPath, String propertyPathLabel, boolean visible){
		this.propertyInfo = propertyInfo;
		this.propertyPath = propertyPath;
		this.propertyPathLabel = propertyPathLabel;
		this.visible = visible;
		
		/* Define um UID Sequencial que identifique esta condi��o */
		this.id = retrieveNextId();
	}
	
	public long getId() {return id;}

	public boolean isVisible() {return visible;}
	public void setVisible(boolean visible){this.visible = visible;}

	public Integer getResultIndex(){return resultIndex;}
	public void setResultIndex(Integer resultIndex){this.resultIndex = resultIndex;}

	public IPropertyMetadata getPropertyInfo() {return propertyInfo;}

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
	
	public String getPropertyPathLabel() {return propertyPathLabel;}

	/**
	 * Retorna somente o nome da �ltima propriedade do caminho
	 * @return
	 */
	public String getPropertyLabel() {
		/* Pega o �ndice do �ltimo separador*/
		int lastPropertyBegin = this.propertyPathLabel.lastIndexOf(ReportParam.PROPERTY_PATH_LABEL_SEPARATOR);
		
		/* Se o separador n�o existe, ent�o o propertyPath referencia uma propriedade direta da entidade.
		 * Assim define o in�cio da c�pia para o �ndice */
		if(lastPropertyBegin==-1)
			lastPropertyBegin = 0;
		else
			/* Copia removendo o s�mbolo de separa��o */
			lastPropertyBegin += ReportParam.PROPERTY_PATH_LABEL_SEPARATOR.length();
		
		return StringUtils.substring(this.propertyPathLabel, lastPropertyBegin);
	}

	public String toString(){
		String result = visible?"[x]":"[ ]";
		
		result += this.propertyPathLabel;
		
		return result;
	}

	/**
	 * Verifica se a atual propriedade j� � suportada pela implementa��o
	 * desta classe. Assim, outras classe podem verifica primeiro se
	 * a propriedade poder� ou n�o ser suportada 
	 * @param prop Metadados da propriedades
	 */
	public static boolean checkVersionSupport(IPropertyMetadata prop) {
		/* N�o exibe propriedades definidas como invis�veis para pesquisa */
		if(!prop.isVisible())
			return false;
		else
			return true;
	}

}
