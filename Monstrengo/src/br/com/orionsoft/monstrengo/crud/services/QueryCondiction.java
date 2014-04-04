package br.com.orionsoft.monstrengo.crud.services;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.crud.services.Operator;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;
import br.com.orionsoft.monstrengo.crud.report.entities.ReportParam;

public class QueryCondiction
{
	private Logger log = Logger.getLogger(this.getClass());
	
	private long id = IDAO.ENTITY_UNSAVED;
	private boolean active = true;
	private boolean readOnly = false;

	public static int INIT_AND = 1;
	public static int INIT_OR = 2;
	
	private IEntityManager entityManager;
	private Class<?> entityType;
	private IPropertyMetadata propertyInfo;

	private String propertyPath;
	private String propertyPathLabel;

	private int initOperator = INIT_AND;
	private boolean openPar = false;
	private Operator operator = new Operator(Operator.EQUAL); /* � necess�rio inicializar um operador v�lido para n�o dar nullpointer no getOperatorId()*/
	private String value1 = "";
	private String value2 = "";
	private boolean closePar = false;

	/** Contador manual de ids do objeto.
	 * Utilizado enquanto esta classe n�o for
	 * mantida pelo mecanismo de persit�ncia e for 
	 * necess�ria a identifica��o das inst�ncias de condi��es */
	private static long idCounter = 0;
	private long retrieveNextId(){return idCounter++;}
	
	public QueryCondiction(IEntityManager entityManager, Class<?> entityType) throws BusinessException{
		/* Armazena o gerenciador de entidades para us�-lo mais tarde */
		this.entityManager = entityManager;

		/* Prepara os metadados da entidade */
		this.entityType = entityType;
		
		/* Define a propriedade padr�o inicial de pesquisa */
		this.setPropertyPath(IDAO.PROPERTY_ID_NAME);
		
		/* Define um UID Sequencial que identifique esta condi��o */
		this.id = retrieveNextId();
	}
	
	public QueryCondiction(IEntityManager manager, Class<?> entityType, String propertyName, int operatorId, String value1, String value2) throws BusinessException{
		this(manager, entityType);

		this.setPropertyPath(propertyName);
		this.setOperatorId(operatorId);
		this.value1 = value1;
		this.value2 = value2;
	}
	
	
	
	public boolean isReadOnly() {return readOnly;}
	public void setReadOnly(boolean readOnly) {this.readOnly = readOnly;}

	public int getInitOperator(){return initOperator;}
	public void setInitOperator(int initOperator){this.initOperator = initOperator;}

	public boolean isClosePar(){return closePar;}
	public void setClosePar(boolean closePar){this.closePar = closePar;}
	
	public boolean isOpenPar(){return openPar;}
	public void setOpenPar(boolean openPar){this.openPar = openPar;}
	
	public Operator getOperator(){return operator;}
	public void setOperator(Operator operator){this.operator = operator;}
	
	public IPropertyMetadata getPropertyInfo() {return propertyInfo;}

	public String getValue1(){return value1;}
	public void setValue1(String value1){this.value1 = value1;}

	public boolean getValue1AsBoolean(){return value1.equals("true");}
	public void setValue1AsBoolean(boolean value1){this.value1 = value1?"true":"false";}

	public String getValue2(){return value2;}
	public void setValue2(String value2){this.value2 = value2;}
	
	public boolean getValue2AsBoolean(){return value1.equals("true");}
	public void setValue2AsBoolean(boolean value1){this.value1 = value1?"true":"false";}

	public int getOperatorId(){return this.operator.getId();}
	public void setOperatorId(int id){this.operator = new Operator(id);}
	
	public long getId() {return id;}

	public boolean isActive() {return active;}
	public void setActive(boolean active) {		this.active = active;	}

	@SuppressWarnings("unchecked")
	public String getValue1Description() throws BusinessException{
		String result = "";
		/* Prepara a descri��o dos valores atuais das condi��es */
		/* Verifica se � ENUM */
		if(this.getPropertyInfo().isEnum()){
			Class c = this.getPropertyInfo().getType();
			result = Enum.valueOf(c, this.getValue1()).toString();
		}else
		/* Verifica se a propriedade � do tipo Entidade */
		if(this.getPropertyInfo().isEntity()){
			/* Verifica se foram definidos os value 1 e 2 para preencher suas
			 * descri��es com os dados da entidade */
			if(!StringUtils.isEmpty(this.getValue1())){
				result = UtilsCrud.retrieve(this.entityManager.getServiceManager(), 
						this.getPropertyInfo().getType(),
						Long.parseLong(this.getValue1()),
						null).toString();
			}
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public String getValue2Description() throws BusinessException{
		String result = "";
		/* Prepara a descri��o dos valores atuais das condi��es */
		/* Verifica se � ENUM */
		if(this.getPropertyInfo().isEnum()){
			Class c = this.getPropertyInfo().getType();
			result = Enum.valueOf(c, this.getValue2()).toString();
		}else
		/* Verifica se a propriedade � do tipo Entidade */
		if(this.getPropertyInfo().isEntity()){
			/* Verifica se foram definidos os value 1 e 2 para preencher suas
			 * descri��es com os dados da entidade */
			if(!StringUtils.isEmpty(this.getValue2())){
				result = UtilsCrud.retrieve(this.entityManager.getServiceManager(), 
						this.getPropertyInfo().getType(),
						Long.parseLong(this.getValue2()),
						null).toString();
			}
		}
		
		return result;
	}

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
			
			/* Prepara o flag que indica a presen�a de uma propriedade Entity Calculada 
			 * que � utilizado na montagem da HQL */
			this.calculatedFlag = false;

			/* Inicia a busca das propriedades pela atual entidade da condi��o */
			Class<?> entity = this.entityType;

			/* Inicia o parser para identificar qual entidade
			 * � a �ltima do caminho e pegar seu tipo */
			String[] props = StringUtils.split(propertyPath,IDAO.PROPERTY_SEPARATOR);
	        for (String prop: props)
	        {
				/* Pega a propriedade atual para verificar seu tipo */
				this.propertyInfo = entityManager.getEntityMetadata(entity).getPropertyMetadata(prop);

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
					
					/* Ativa o flag Calculated que ser� utilizado na montagem da HQL.
					 * Assim, a fun��o de montagem ir� identificar que h� propriedades
					 * calculadas no caminho e que deve deixar a aplica��o da express�p 
					 * do operador para ser 'impressa' no meio da fun��o de tratamento do caminho 
					 * Ex: (entity.prop1 in (select propCalculatedType from pCT as pCT where ... OPERADOR ...)*/
					if(this.propertyInfo.isCalculated())
						this.calculatedFlag |= true; 
				}else{
					/* Se for primitivo, significa que � a �ltima propriedade do caminho
					 * e j� poder� receber um valor assim for�a um BREAK no for */
					break;
				}
				
				/* Ao terminar este la�o o this.propertyInfo apontar� para a �ltima propriedade
				 * primitiva do caminho, o this.propertyPath estar� definido para ser usado na HQL.
				 * E o flag de presen�a de propriedade calculada no path est�ra ativo ou inativo
				 */
	        }
		}
	}

	public String getPropertyPathLabel(){return propertyPathLabel;}

	public String toString(){
		String result = "";
		String description = "";
		
		result += active?"":"(Desativada)";
		
		if(this.initOperator == INIT_AND)
			result += "e ";
		else if(this.initOperator == INIT_OR)
			result += "ou ";

		if(this.openPar)
			result += "( ";
		
		result += this.propertyPathLabel + " " + this.operator.getLabel();
		
		if(!StringUtils.isEmpty(this.value1)){
		  result += " '" + this.value1 + "'";
			try{description = this.getValue1Description();}catch(Exception e){
				description = e.getMessage();}
		  if(!StringUtils.isEmpty(description))
			  result += " - " + description;
		}
		if(!StringUtils.isEmpty(this.value2)){
			result += " e '" + this.value2 + "'";
			
			try{description = this.getValue2Description();}catch(Exception e){
				description = e.getMessage();}
			if(!StringUtils.isEmpty(description))
				result += " - " + description;
		}
		
		if(this.closePar)
			result += " )";
		return result;
	}

	public IEntityMetadata getEntityInfo() throws EntityException {
		return this.entityManager.getEntityMetadata(entityType);
	}

	/**
	 * Indica se durante a an�lise do propertyPath foi detectado que h�
	 * propriedades calculadas para serem tratadas
	 */
	private boolean calculatedFlag = false;
	
	public String retrieveHqlExpression(String entityAlias) throws BusinessException{
		if(log.isDebugEnabled()){
			log.debug("Iniciando retrieveHqlExpression()");
			log.debug("entity:" + entityType.getName());
			log.debug("entityAlias:" + entityAlias);
			log.debug("id:" + id);
			log.debug("active:" + active);
			log.debug("iniOperator:" + initOperator);
			log.debug("openPar:" + openPar);
			log.debug("closePar:" + closePar);
			log.debug("propertyInfo:" + propertyInfo.getName());
			log.debug("operator:" + operator.getLabel());
			log.debug("value1:" + value1);
			log.debug("value2:" + value2);
		}
		
		/* VERIFICA SE A CONDI��O J� � SUPORTADA PELA ATUAL IMPLEMENTA��O */
		if(!checkVersionSupport(this.propertyInfo)){
			log.debug("Propriedade n�o suporta pela atual implementa��o");
			return "";
		}
		
		
		/* Inicia a lista de propriedades que ser� utilizada
		 * para criar a cl�usula WHERE e tratar cole��es e
		 * propriedades calculadas de especializa��o do tipo asMyClass
		 */
		String[] props = StringUtils.split(propertyPath,".");
		
		/* OBTEM CLAUSULA WHERE */
		String result = funcaoRosa(entityAlias, this.entityType, props, 0, this.calculatedFlag);
		
		/* APLICAR A PARENTIZA��O DEFINIDA */
		if(this.openPar) result = "(" + result;
		if(this.closePar) result += ")";
		
		if(log.isDebugEnabled())
			log.debug("Express�o:" + result);
		
//		System.out.println("Express�o:" + result);
		
		return result;
	}
	
	/**
	 * Trata a aplica��o dos operadores e do operador SOME() para conjunto
	 * definindo de ser� composta uma expressao:
	 * - prop1.prop2.prop3 op value;
	 * Onde prop2 � uma cole��o.
	 * - entity IN( SELECT entity_ FROM this.entityType.simpleName entity_ INNER JOIN entity_prop2 entities WHERE entities +  funcaoAzul(prop3  op value)
	 * Exemplo de uso:
	 * - applicationUser.groups.name = 'admin'
	 * - entity IN (SELEC entity_ FROM ApplicationUser entity_ INNER JOIN entity_.groups entities WHERE entities.nome = 'admin');
	 *   
	 * @param entityAlias Apelido que ser� aplicado na parte da express�o gerada 
	 * em cada itera��o. Por exemplo: 'entity', '.prop1', '.prop2', etc 
	 * @param entityType Tipo da entidade da propriedade atual que � utilizado
	 * para obter os metadados da propriedade atual no Gerenciador de Entidades
	 * @param props Vetor com os nomes das proriedades que comp�e a express�o
	 * separada por pontos do tipo 'prop1.prop2.prop3'
	 * @param propIndex �ndice da propriedade atualmente em an�lise
	 * @param calculatedFlag Indica se na express�o existe alguma propriedades
	 * que � do tipo IEntity e � calculada para que sej� constru�da a express�o
	 * IN(SELECT FROM WHERE) que permite traduzir um campo calculado em uma Hql
	 * @return Esta fun��o � recursiva e retorna a express�o construida recursivamente
	 */
	private String funcaoRosa(String entityAlias, Class<?> entityType, String[] props, int propIndex, boolean calculatedFlag) throws BusinessException {
        IPropertyMetadata prop=null;
		/* Verifica se existe a propriedade atual,
		 * ou seja, se n�o � a �ltima chamda � esta fun��o para somente 
		 * colocar o operador e valor*/
		if(propIndex < props.length)
			/*SIM: Define flagHierarquia = FALSE; */
			/* Obtem os metadados da propriedade ATUAL */
			prop = this.entityManager.getEntityMetadata(entityType).getPropertyMetadata(props[propIndex]);		

	    /* Verifica se o indice da propriedade atual j� passou da �ltima propriedade
	     * para desativar o tratamento de propriedades calculadas*/
		if(propIndex == props.length){
			/*SIM: Define flagHierarquia = FALSE; */
			calculatedFlag = false;
		}

		/* Verifica o flag de h op valueierarquia? */
		if(calculatedFlag)
			/* ATIVO: O operador � usado dentro de AZUL    hierarquia(expr OP expr)*/
	        return entityAlias + funcaoAzul(prop, props, propIndex, calculatedFlag);
		/* INATIVO: Nao tem hierarquia, logo a expressao � simples  expr1 OP expr1 */
		else{
			/* Verifica se entityAlias � a �ltima propriedade e n�o tem mais */
			if(prop==null)
				/* SIM: return `entityAlias`+ LARANJA +VERMELHO */
				return entityAlias + funcaoLaranja(); 
			else/* NAO: return `entityAlias`+ LARANJA +VERMELHO */
				/* Verifica se a �ltima propriedade � uma cole��o? */
				if((prop !=null) && prop.isEntity() && prop.isCollection())
				/* SIM: return VERMELHO+LARANJA+`SOME(`+`entityAlias`+AZUL(entityType, props[],indiceAtual) +`)` */
				return entityAlias  + " IN (SELECT entity_ FROM " + this.entityType.getSimpleName() + " entity_ INNER JOIN entity_." + props[propIndex] + " entities WHERE entities" + funcaoAzul(prop, props, propIndex+1, calculatedFlag) + ")";
			else
				/* NAO: return `entityAlias`+ AZUL(entityType, props[], indice)+LARANJA +VERMELHO */
				return entityAlias + funcaoAzul(prop, props, propIndex, calculatedFlag); 
		}
	}

	private String funcaoAzul(IPropertyMetadata propInfo, String[] props, int propIndex, boolean calculatedFlag) throws BusinessException{
		/* TODO CORRIGIR Estah sendo gerar HQL do tipo prop=1=1 ou prop=true=true */
		/* J� passou do ultimo elemento? */
		if(propIndex == props.length)
			/* SIM: return ""; Finaliza a recursao retornando VAZIO */
			return "";
		else
			/* NAO: A propriedade � do tipo ENTIDADE e CALCULADA? */
			if(propInfo.isEntity() && propInfo.isCalculated())
				/*	SIM: retorna ` in(	SELECT simpleName() 
				 *				FROM  simpleName() as simpleName()
				 *				WHERE  SOME(simpleName, entityType, propPath[], ++indiceAtual)
				 *			)`
				 */
				return " IN(SELECT " + propInfo.getType().getSimpleName() + 
				       "\n FROM " + propInfo.getType().getSimpleName() + " AS " + propInfo.getType().getSimpleName() +
				       "\n WHERE " + funcaoRosa(propInfo.getType().getSimpleName(),  propInfo.getType(), props, ++propIndex, calculatedFlag) +
				       "\n )"; 
			else
			 	/* NAO: SOME(`.` + propPath[indiceAtual], entityType, propPath[], ++indiceAtual) */
				return funcaoRosa("." + props[propIndex],  propInfo.getType(), props, ++propIndex, calculatedFlag);
	}

	private String funcaoVermelho(String valueIn) {
		/* TRATA O VALOR ENTRADO DE ACORDO COM O TIPO QUE A PROPRIEDADE ESPERA */
		/* Limpa alguns caracteres especiais que n�o podem ser fornecidos pelo operador*/
		valueIn = valueIn.replace("'", "").replace("\"", "");
		
		// Trata as propriedades primitivas String
		if(propertyInfo.isPrimitive()){
			if(propertyInfo.isString() || propertyInfo.isEnum())
				/* Coloca o valor de String entre aspas simples */
				return "'" + valueIn + "'"; 
			else
				if(propertyInfo.isBoolean())
					return StringUtils.trim(valueIn);
				else
					if(propertyInfo.isCalendar()){
						valueIn = CalendarUtils.formatToSQLDate(valueIn);
						
						/* Verifica se o valor passado p�de ser convertido em uma data SQL v�lida */
						if(StringUtils.isEmpty(valueIn)){
							log.debug("Valor informado n�o � uma valor v�lido para calend�rio. Pegando a data atual");
							valueIn = CalendarUtils.formatDate(CalendarUtils.getCalendar());
						}
						
						return "'" + valueIn + "'";
					}else{
						/* Provavelmente seja um tipo num�rico */
						
						/* Remove o separador de milhar (.) */
						valueIn = valueIn.replace(".", "");
						
						/* Converte o ponto flutuande brasileiro (,) por (.) */
						valueIn = valueIn.replace(",", ".");
						
						/* Verifica se o valor n�o � num�rico e n�o pode ser aplicado � propriedade
						 * a fun��o retorna vazio */
						if(!StringUtils.isEmpty(valueIn))
							if(!(NumberUtils.isNumber(valueIn)||(StringUtils.isNumeric(valueIn)))){
								log.debug("Value1 informado n�o � num�rico para ser aplicado");
								return "0";
							}
						
						return valueIn;
					}
		}else
			/* N�o � uma propriedade primitiva, � do tipo IEntity 
			 * Trata o valor que pode ter sido entrado como um Id 
			 * de uma entidade ou para comapara com uma cole��o de entidade 
			 */ 
			if(propertyInfo.isEntity()){
				// Se for uma entidade verifica se o o valor pode ser um id
				valueIn = StringUtils.trim(valueIn);
				
				if(!StringUtils.isEmpty(valueIn)) {
					if (!valueIn.toLowerCase().contains("from") && !valueIn.contains(",") && (!(NumberUtils.isNumber(valueIn)||(StringUtils.isNumeric(valueIn))))){
						log.debug("Valor informado n�o � num�rico para ser aplicado");
						return "-1";
					}
				}
				
				return valueIn;
			}else
				return valueIn;
	}

	private String funcaoLaranja() {
		/* Trata incialmente o value1 mais usado e deixa para que o operador between
		 * trate o value2 */
		String strValue = funcaoVermelho(this.value1); 

		/* APLICAR O OPERADOR SELECIONADO */
		/* 'entity.propName' OPERADOR VALUE1 [AND VALUE2] */
		
		switch(operator.getId()){
		case Operator.EQUAL:
			return "=" + strValue;
		case Operator.NOT_EQUAL:
			return "!=" + strValue;
		case Operator.MORE_THEN:
			return ">" + strValue;
		case Operator.MORE_EQUAL:
			return ">=" + strValue;
		case Operator.LESS_THEN:
			return "<" + strValue;
		case Operator.LESS_EQUAL:
			return "<=" + strValue;
		case Operator.LIKE:
			/* Trata os asteriscos que podem ser fornecidos pelo operador */
			if(strValue.contains("*"))
				strValue = strValue.replace("*", "%");
			else
				/* Remove as aspas caso for do tipo String para colocar o operador % e as aspas corretamente */
				strValue = "'%" + strValue.replace(" ", "%").replace("'", "") + "%'";
			
			
			return " like " + strValue;
		case Operator.NOT_LIKE:
			/* Trata os asteriscos que podem ser fornecidos pelo operador */
			if(strValue.contains("*"))
				strValue = strValue.replace("*", "%");
			else
				/* Remove as aspas caso for do tipo String para colocar o operador % e as aspas corretamente */
				strValue = "'%" + strValue.replace(" ", "%").replace("'", "") + "%'";
			
			return " not like " + funcaoVermelho(this.value1);
		case Operator.NULL:
			return " is null";
		case Operator.NOT_NULL:
			return " is not null";
		case Operator.BETWEEN:
			return " between " + strValue + " and " + funcaoVermelho(this.value2);
		case Operator.NOT_BETWEEN:
			return " not between " + strValue + " and " + funcaoVermelho(this.value2);
		case Operator.IN:
			return " in (" + this.value1 + ")"; // Lucio 20140404: N�o trata o valor, pois deve ser uma lista separada por ',' 
		case Operator.NOT_IN:
			return " not in (" + this.value1 + ")"; // Lucio 20140404: N�o trata o valor, pois deve ser uma lista separada por ','
		default:
			throw new RuntimeException("Nenhum operador foi definido para o Id:" + operator.getId());
		}
	}

	/**
	 * Verifica se a atual propriedade j� � suportada pela implementa��o
	 * desta classe. Assim, outras classe podem verifica primeiro se
	 * a propriedade poder� ou n�o ser suportada 
	 * @param prop Metadados da propriedades
	 */
	public static boolean checkVersionSupport(IPropertyMetadata prop) {
		/* TODO IMPLEMENTAR N�o permite consulta em propriedades cole��o, porque a Condiction.java ainda n�o trata Collection */
		/* Cole��es ainda n�o s�o suportadas porque o operador SOME() soh 
		 * se aplica � cole��es indexadas */
		if(prop.isCollection())
			return false;
		else
		/* N�o exibe propriedades definidas como invis�veis para pesquisa */
		if(!prop.isVisible())
			return false;
		else
		/* N�o exibe propriedades primitivas calculadas */
		if(prop.isPrimitive() && prop.isCalculated())
			return false;
		
		return true;
	}

	/**
	 * Verifica se a valor fornecido poder� ser aplicado � propriedade
	 * evitando assim que a propriedade seja inserida
	 * @param prop Metadados da propriedades
	 * @param value Valor entrado
	 */
	public static boolean checkValueSupport(IPropertyMetadata prop, String valueIn) {
		/* Limpa alguns caracteres especiais que n�o podem ser fornecidos pelo operador*/
		valueIn = valueIn.replace("'", "").replace("\"", "");
		
		// Trata as propriedades primitivas String
		if(prop.isPrimitive()){
			if(prop.isString() || prop.isEnum()){
				/* Verifica se o valor � num�rico e n�o deve ser aplicado � propriedade
				 * do tipo string */
				if(!StringUtils.isEmpty(valueIn))
					if((NumberUtils.isNumber(valueIn)||(StringUtils.isNumeric(valueIn))) && !prop.isHasEditMask())
						return false;

				/* Coloca o valor de String entre aspas simples */
				return true; 
			}else
				if(prop.isBoolean())
					return valueIn.equals("true") || valueIn.equals("false") ;
				else
					if(prop.isCalendar()){
						valueIn = CalendarUtils.formatToSQLDate(valueIn);
						
//						System.out.println(valueIn);
						/* Verifica se o valor passado p�de ser convertido em uma data SQL v�lida */
						return !StringUtils.isEmpty(valueIn);
					}else{
						/* Remove o separador de milhar (.) */
						valueIn = valueIn.replace(".", "");
						
						/* Converte o ponto flutuande brasileiro (,) por (.) */
						valueIn = valueIn.replace(",", ".");
						
						/* Verifica se o valor n�o � num�rico e n�o pode ser aplicado � propriedade
						 * a fun��o retorna vazio
						 * Lucio 27092007: Verifica se o numero inicia com ZERO pois n�o pode ser um ID. O Hibernate d� um erro quanto busca alguma coisa do tipo entity.id=074565454  
						 */
						if(!StringUtils.isEmpty(valueIn) && (valueIn.charAt(0)!='0') && (valueIn.length() < 9))
							if(NumberUtils.isNumber(valueIn)||(StringUtils.isNumeric(valueIn))){
								return true;
							}
						
						return false;
					}
		}else
			/* N�o � uma propriedade primitiva, � do tipo IEntity 
			 * Trata o valor que pode ter sido entrado como um Id 
			 * de uma entidade ou para comapara com uma cole��o de entidade 
			 */ 
			if(prop.isEntity()){
//				valueIn = StringUtils.trim(valueIn); Lucio 27092007: Pra que tirar espalo em branco, se o operador digitou � porque nao deve swer um id mesmo
				
				/* Se for uma entidade verifica se o o valor pode ser um id
				 * Lucio 27092007: Verifica se o numero inicia com ZERO pois n�o pode ser um ID. O Hibernate d� um erro quanto busca alguma coisa do tipo entity.id=074565454  
				 */
				if(!StringUtils.isEmpty(valueIn) && (valueIn.charAt(0)!='0') && (valueIn.length() < 9))
					if(NumberUtils.isNumber(valueIn)||(StringUtils.isNumeric(valueIn))){
						return true;
					}
				
				return false;
			}else
				/* Desconhecido */
				return false;
	}

}
