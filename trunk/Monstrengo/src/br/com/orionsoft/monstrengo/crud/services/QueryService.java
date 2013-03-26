package br.com.orionsoft.monstrengo.crud.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.com.orionsoft.monstrengo.crud.services.Operator;
import br.com.orionsoft.monstrengo.crud.services.OrderCondiction;
import br.com.orionsoft.monstrengo.crud.services.QueryCondiction;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;

/**
 * Este servi�o, pesquisa uma entidade baseando-se em um
 * filtro fornecido. Inicialmente, este filtro � um conte�do que ser� procurado 
 * em v�rios campos da entidade e subentidade.
 * 
 * <p><b>Argumentos:</b>
 * <br> IN_ENTITY_TYPE: A classe das entidades que ser�o listadas.
 * <br> IN_QUERY_SELECT: Uma string com uma lista ou fun��o de agrega��o 
 *                       para ser usada na cl�usula SELECT da HQL. Se esta op��o for utilizada,
 *                       a lista de entidade retornada ser� nula e o resultado da pesquisa
 *                       ser� armazenado na lista de saida do select (OUT_OBJECT_LIST).
 *                       Esta lista ser� uma lista onde cada item � um vetor que contem 
 *                       as propriedades selecionadas.
 * <br>
 * <br> IN_QUERY_FILTER: Uma string com um conte�do a ser procurado.
 * <br> IN_QUERY_CONDICTIONS: Uma lista do tipo QueryCondictions com cl�usulas de sele��o QueryCondiction.
 * <br> IN_QUERY_HQLWHERE: Uma string com uma express�o HQL j� usando o alias 'entity.' para ser aplicada diretamente na cl�usula WHERE.
 * <br>
 * <br> IN_PARENT_CLASS_OPT: A classe pai que possui algum relacionamento com a classe atual.
 * <br> IN_PARENT_ID_OPT: O id da classe pai que possui algum relacionamento com a classe atual.
 * <br> IN_PARENT_PROPERTY_OPT: A propriedade da classe pai que se relaciona com a classe atual.  
 * <br>
 * <br> IN_ORDER_EXPRESSION_OPT: Uma express�o de ordena��o do tipo "PropertyName ASC, PropertyName DESC".  
 * <br> IN_ORDER_CONDICTIONS_OPT: Uma lista do tipo OrderCondictions com cl�usulas de ordena��o OrderCondiction.  
 * <br>
 * <br> IN_MAX_RESULT_OPT: Um n�mero inteiro que define o n�mero de registros que devem ser retornados pela consulta.  
 * <br> IN_FIRST_RESULT_OPT: Um n�mero inteiro que define o registro inicial da cole��o que ser� retornada pela consulta.  
 *  
 * <p><b>Procedimento:</b>
 * <br>Utilizando o filtro, monta as express�es de busca para todas as propriedades da entidade.
 * <br>Verifica se tem uma entidade pai e monta a jun��o das duas entidades.
 * <br>Aplica as express�es montadas.
 * <br>Realiza a pesquisa.
 * <br>Realiza outra pesquisa para obter o total da cole��o da pesquisa n�o paginada.
 * <br>Converte a cole��o de objetos em uma cole��o de entidades.
 * 
 * <p><b>Retorno:</b>
 * <br>Se nenhum filtro for fornecido ser� retornada uma cole��o vazia.
 * <br><b>OUT_ENTITY_LIST</b>: Retorna a lista de entidades (IEntityList).
 * <br><b>OUT_LIST_SIZE</b>: Retorna a tamanho total da lista n�o paginada (Integer).
 * <br><b>OUT_QUERY_TIME</b>: Retorna o tempo de demora da consulta em milissegundos (Long).
 * <br><b>OUT_SELECT_LIST</b>: Retorna uma lista de objetos resultantes da sele��o personalizada de propriedade pela op��o IN_QUERY_SELECT.<br>
 *                             Se o select tiver somente uma propriedade o resultado � uma List(Object>;<br>
 *                             Se o select tiver mais de uma propriedade o resultado � uma List(Object[]>;<br>
 *                              
 * 
 * @author Lucio
 * @version 20060508
 * 
 */
public class QueryService extends ServiceBasic 
{
    
    public static String SERVICE_NAME = "QueryService";
    public static String IN_ENTITY_TYPE = "entityType";
    public static String IN_QUERY_SELECT = "querySelect";
    public static String IN_QUERY_FILTER = "queryFilter";
    public static String IN_QUERY_HQLWHERE = "queryHqlWhere";
    public static String IN_QUERY_CONDICTIONS = "queryConditions";

    public static String IN_PARENT_CLASS_OPT = "parentType";
    public static String IN_PARENT_ID_OPT = "parentId";
    public static String IN_PARENT_PROPERTY_OPT = "parentProperty";
    
    public static String IN_ORDER_EXPRESSION_OPT = "orderExpression";
    public static String IN_ORDER_CONDICTIONS_OPT = "orderCondictions";

    public static String IN_MAX_RESULT_OPT = "maxResult";
    public static String IN_FIRST_RESULT_OPT = "firstResult";

    public static int OUT_ENTITY_LIST = 0;
    public static int OUT_LIST_SIZE = 1;
    public static int OUT_QUERY_TIME = 2;
    public static int OUT_OBJECT_LIST = 3;

    public String getServiceName() {return SERVICE_NAME;}

    @SuppressWarnings("unchecked")
	public void execute(ServiceData serviceData) throws ServiceException
    {
        try
        {
        	log.debug("Iniciando a execu��o do servi�o QueryService");
        	// Obt�m os par�metros
        	Class<?> inEntityType = (Class<?>) serviceData.getArgumentList().getProperty(IN_ENTITY_TYPE);

        	// Obt�m os par�metros OPCIONAIS
    		String inQueryFilter = "";
        	if(serviceData.getArgumentList().containsProperty(IN_QUERY_FILTER))
        		inQueryFilter = (String) serviceData.getArgumentList().getProperty(IN_QUERY_FILTER);

            // Obt�m os par�metros OPCIONAIS
            String inQuerySelect = "";
            if(serviceData.getArgumentList().containsProperty(IN_QUERY_SELECT))
                inQuerySelect = (String) serviceData.getArgumentList().getProperty(IN_QUERY_SELECT);

            String inQueryHqlWhere = "";
            if(serviceData.getArgumentList().containsProperty(IN_QUERY_HQLWHERE))
                inQueryHqlWhere = (String) serviceData.getArgumentList().getProperty(IN_QUERY_HQLWHERE);

            List<QueryCondiction> inQueryCondictions=null;
        	if(serviceData.getArgumentList().containsProperty(IN_QUERY_CONDICTIONS))
            	inQueryCondictions= (List<QueryCondiction>) serviceData.getArgumentList().getProperty(IN_QUERY_CONDICTIONS);

        	Class<?> inParentClass=null;
        	if(serviceData.getArgumentList().containsProperty(IN_PARENT_CLASS_OPT))
            	inParentClass= (Class<?>) serviceData.getArgumentList().getProperty(IN_PARENT_CLASS_OPT);
        	
        	long inParentId=IDAO.ENTITY_UNSAVED;
        	if(serviceData.getArgumentList().containsProperty(IN_PARENT_ID_OPT))
            	inParentId= (Long) serviceData.getArgumentList().getProperty(IN_PARENT_ID_OPT);

        	String inParentProperty=null;
        	if(serviceData.getArgumentList().containsProperty(IN_PARENT_PROPERTY_OPT))
            	inParentProperty= (String) serviceData.getArgumentList().getProperty(IN_PARENT_PROPERTY_OPT);
       	
        	String inOrderExpression="";
        	if(serviceData.getArgumentList().containsProperty(IN_ORDER_EXPRESSION_OPT))
        		inOrderExpression = (String) serviceData.getArgumentList().getProperty(IN_ORDER_EXPRESSION_OPT);

        	List<OrderCondiction> inOrderCondictions=null;
        	if(serviceData.getArgumentList().containsProperty(IN_ORDER_CONDICTIONS_OPT))
            	inOrderCondictions= (List<OrderCondiction>) serviceData.getArgumentList().getProperty(IN_ORDER_CONDICTIONS_OPT);
       	
        	int inMaxResult=-1;
        	if(serviceData.getArgumentList().containsProperty(IN_MAX_RESULT_OPT))
            	inMaxResult = (Integer) serviceData.getArgumentList().getProperty(IN_MAX_RESULT_OPT);
       	
        	int inFirstResult=-1;
        	if(serviceData.getArgumentList().containsProperty(IN_FIRST_RESULT_OPT))
            	inFirstResult = (Integer) serviceData.getArgumentList().getProperty(IN_FIRST_RESULT_OPT);

        	if (log.isDebugEnabled())
        		log.debug("Criando uma consulta HQL para a entidade" + inEntityType);
        	
        	Long outItemsCount = new Long(0);
            Calendar startTime = Calendar.getInstance();

            List<QueryCondiction> condictions=new ArrayList<QueryCondiction>();
        	
        	/* Verifica se foi fornecido um filtro */
        	if(inQueryFilter != null && !StringUtils.isEmpty(inQueryFilter)) {
//        		if(StringUtils.isNumeric(inQueryFilter))
//        			inQueryFilter = "xXXXXXXXXXXXXXXXXXXXXXx";
        		
        		log.debug("Utilizando o filtro informado");
        		/* Percorre a entidade e obtem as clausulas Where de pesquisa das propriedades da entidade */
        		getEntityCondictions(inEntityType, inQueryFilter, condictions);
        	}
        	
        	/* Verifica se foi fornecida uma lista de condi��es */
        	if((inQueryCondictions!=null)&&inQueryCondictions.size()>0) {
        		log.debug("Utilizando as condi��es informadas");
        		condictions.addAll(inQueryCondictions);
        		
        		/* Para ficar assim: filter and|or (expr1 and|or expr2 and|or expr3) */
        		log.debug("Define as parametriza��es dos par�nteses");
        		// TODO CORRIGIR N�o funcionou, gera uma express�o 'and (entity.valor=25) and entity.valor=24)'
        		//inQueryCondictions.get(0).setOpenPar(true);
        		//inQueryCondictions.get(inQueryCondictions.size()-1).setClosePar(true);
        		
        	}

        	/* Tenta realizar a pesquisa se tiver condi��es filtradas ou um Pai definido */
        	String fromExpression = " from " + inEntityType.getName() + " " + IDAO.ENTITY_ALIAS_HQL;
        	String whereExpression = "";
        	
        	/* Verifica se possui um pai para inclu�-lo na clausua FROM 
        	 * Estrutuda da consulta:
        	 * SELECT entity FROM entity, parent WHERE (entity in elements(parent.property) and parent.id=1) and (expr1 or expr2)*/
        	if(inParentClass!=null && inParentId!=IDAO.ENTITY_UNSAVED){
        		fromExpression += ", " + inParentClass.getName() + " parent ";
        		whereExpression += " where (" + IDAO.ENTITY_ALIAS_HQL +" in elements(parent." + inParentProperty + ") and parent.id=" + inParentId + ")";
        	}

        	/* Identifica se a primeira express�o v�lida foi produzida 
        	 * para n�o gerar os blocos iniciais de " where " ou " and (" */
        	boolean firstAlreadyFound = false;
    		/* Utilizado para n�o inserir operadores na primeira express�o e
    		 * inserir operadores AND e OR �o in�cio das demais express�es*/
        	String initOp;
    		
        	/* Utilizando para indicar o in�cio de um par�ntese de condi��es
        	 * que s�o openPar mas que n�o geraram express�o */
        	String initPar="";
        	
        	/* Percorre todas as condi��es, verifica qual � a primeira condi��o que
        	 * n�o resultar� em uma cl�usula vazia e trata a inicializa��o correta 
        	 * dos par�nteses mesmo quando a condi��o n�o gera uma expressao HQL */
        	for(QueryCondiction cond: condictions){
        		
        		/* Verifica se a condi��o est� ativa */
        		if(cond.isActive()){
        			/* Pega a express�o HQL da condi��o */ 
        			String str = cond.retrieveHqlExpression(IDAO.ENTITY_ALIAS_HQL);

        			/* Verifica se � a primeira Express�o, ou seja, n�o precisa de um operador inicial. Exemplo: '(expre)' e n�o 'and (express)' */
        			if(firstAlreadyFound){
        				if(cond.getInitOperator() == QueryCondiction.INIT_AND)
        					initOp = " and " + initPar;
        				else
        					initOp = " or " + initPar;
        			}else{
        				/* Achou uma condi��o v�lida e inicia o bloco logo ap�s o pai */
        				/* Verifica se existem condi��es do pai para iniciar corretamente 
        				 * o bloco WHERE ou continuar o bloco do Pai */
        				if(inParentClass!=null && inParentId!=IDAO.ENTITY_UNSAVED)
        					/* Continua o bloco do PAI: (CONDICOES_PAI) and ( */
        					whereExpression += " and ("  + initPar;
        				else
        					/* Inicia um bloco Where pois n�o h� PAI */
        					whereExpression += " where " + initPar;

        				initOp = "";
        				firstAlreadyFound = true;
        			}

        			whereExpression += initOp + str;

        			/* Depois do primeiro uso limpa o 
        			 * par�ntese inicial */
        			initPar = "";

        		}else{
        			/* Tratar as condi��es com openPar e closePar que n�o geram express�o HQL */
        			if(cond.isOpenPar())
        				/* H� um problema se a primeira condi��o n�o gerar uma express�o e for
        				 * uma openPar. O resultado �: (where expre2 or expr3 ... 
        				 * Assim, se a condi��o n�o gerar uma express�o e o primeiro elemento
        				 * v�lido ainda n�o foi encontrado, o parentese e definido para ser inserido no
        				 * inicio da express�o depois do " where " ou " and (" */
        				if(firstAlreadyFound)
        					whereExpression += "(";
        				else
        					initPar = "(";
        			else
        				if(cond.isClosePar())
        					whereExpression += ")";
        		}
        	}
        	
        	/* TODO CORRIGIR Quando � gerado uma clausula das propriedades da 
        	 * entidade (expr1 OR expr2 OR expr3) e nenhuma condi��o gera 
        	 * uma express�o o resultado final � (), o que � inv�lido */
    		
        	/* Fecha o bloco AND se tiver um pai  (parent) and (condictions) + hqlWhere */
    		if(condictions.size()>0)
    			if(inParentClass!=null && inParentId!=IDAO.ENTITY_UNSAVED)
    				whereExpression += ") ";

        	/* Verifica se foi fornecido uma HQL_WHERE */
        	if(!StringUtils.isEmpty(inQueryHqlWhere)) {
        		/* Verifica se j� foi iniciada um clausua where para decidir se poe um AND ou inicia um WHERE */
        		if((inParentClass!=null && inParentId!=IDAO.ENTITY_UNSAVED) || condictions.size()>0)
        			/* Continua o bloco do PAI: (CONDICOES_PAI) and ( */
        			whereExpression += " and ";
        		else
        			/* Inicia um bloco Where pois n�o h� PAI */
        			whereExpression += " where ";

            	/* Percorre a entidade e obtem as clausulas Where de pesquisa das propriedades da entidade */
        		whereExpression += inQueryHqlWhere;
        	}
        	
        	/* Trata a op��o IN_ORDER_EXPRESSION_OPT */
        	if(!StringUtils.isEmpty(inOrderExpression)){
        		whereExpression += " order by " + inOrderExpression;
        	}

        	if((inOrderCondictions != null) && (!inOrderCondictions.isEmpty())){
            	/* Verifica se h� alguma condi��o marcada, pois se n�o tiver condi��es 
            	 * marcada o from expression n�o pode ser iniciado */
        		boolean hasOrderCondictionChecked = false;
        		for(OrderCondiction orderCondiction: inOrderCondictions)
        			if(orderCondiction.isActive()){hasOrderCondictionChecked = true; break;}

        		if(hasOrderCondictionChecked){
        			if(StringUtils.isEmpty(inOrderExpression))
        				/* Inicia uma nova express�o */
        				whereExpression += " order by ";
        			else
        				/* Continua a express�o j� iniciada */
        				whereExpression += ",";

        			for(OrderCondiction order: inOrderCondictions)
        				if(order.isActive())
        					whereExpression += " " + order.retrieveHqlExpression(IDAO.ENTITY_ALIAS_HQL) + ",";

        			/* Remove a �ltima v�rgula */
        			whereExpression = StringUtils.stripEnd(whereExpression, ",");
            	}
        	}
        	
        	
            /* Valida a cl�usula SELECT */
            String selectExpression;
            if(StringUtils.isEmpty(inQuerySelect))
                selectExpression = IDAO.ENTITY_ALIAS_HQL + fromExpression;
            else{
                /* Por quest�es de compatibilidade � verificado se a cl�usula inQuerySelect veio com 
                 * o comando from definido pelo programador ou ser� necess�rio utilizar a j� criada.
                 * Lembrando que a fromExpression pode conter implicitJoins para o parent. Se ela n�o
                 * for usada aqui e usada a do operador, a pesquisa com Parent n�o montar� uma SQL v�lida */
            	if(inQuerySelect.toLowerCase().contains("from "))
            		selectExpression = inQuerySelect;
            	else
            		selectExpression = inQuerySelect + fromExpression;
            }
            
        	if(log.isDebugEnabled())
        		log.debug("Express�o:" + selectExpression + whereExpression);

        	List outObjectList = null;
        	try {
        		log.debug("Executando a consulta");
        		Query query = serviceData.getCurrentSession().createQuery("select " + selectExpression + whereExpression);
        		
        		/* Verifica se o par�metro maxResult foi passado para aplica-lo*/
        		if(inMaxResult>-1)
        			query.setMaxResults(inMaxResult);
        		/* Verifica se o par�metro firstResult foi passado para aplica-lo*/
        		if(inFirstResult>-1)
        			query.setFirstResult(inFirstResult);
        		
        		outObjectList = query.list();
        		
        		/* Verifica se foi utilizada pagina��o para obter o tamanho total
        		 * da lista n�o paginada */
        		log.debug("Calculando o n�mero de items da pesquisa");
        		if(inMaxResult>-1){
        			/* Quando se pesquisa uma entidade que � uma superclasse, o Hibernate realiza a pesquisa em todas as tabelas
        			 * que representam as subclasses (caso as subclasses estejam definidas para armazenamento em tabelas separadas).
        			 * Exemplo: Pesquisar AuditRegister, faz com que o hibernate pesquise na tabela AuditCrudRegister e AuditProcessRegister.
        			 * Desta forma, o hibernate obtem dois totalizadores (count(*)), um para cada tabela.
        			 * Para saber o total de registros � necess�rio somar os constadores
        			 */ 
        			outItemsCount = 0l;
        			List<Long> itemsCountList = serviceData.getCurrentSession().createQuery("select count(*)" + fromExpression + whereExpression).list();
        			for(Long count: itemsCountList)
        				outItemsCount += count;
        			
        		}else{
        			outItemsCount = new Long(outObjectList.size());
        		}
        		
        	} catch (HibernateException e) {
        		throw new ServiceException(MessageList.createSingleInternalError(e));
        	}
        	
        	if (log.isDebugEnabled() && outObjectList != null)
        		log.debug("Foram encontrados " + outObjectList.size() + " elementos");
        	
            /* Verifica se um select personalizado foi utilizado.
             * Se n�o: Constroi uma lista de entidades
             * Se sim: Deixa a lista de entidades nula e constroi uma lista de objetos */
            IEntityList<?> outEntityList = null;
               if(StringUtils.isEmpty(inQuerySelect))
                   outEntityList =  this.getServiceManager().getEntityManager().getEntityList(outObjectList, inEntityType);
               else
                   outEntityList =  this.getServiceManager().getEntityManager().getEntityList(new ArrayList(0), inEntityType);

            Long outQueryTime = Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis();
        		

        	// Adiciona os resultados no serviceData
    		serviceData.getOutputData().add(OUT_ENTITY_LIST, outEntityList);
    		serviceData.getOutputData().add(OUT_LIST_SIZE, outItemsCount);
            serviceData.getOutputData().add(OUT_QUERY_TIME, outQueryTime);
            serviceData.getOutputData().add(OUT_OBJECT_LIST, outObjectList);
        }
        catch (BusinessException e)
        {
            // O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros.
            throw new ServiceException(e.getErrorList());
        }

    }
    
    
    public void getPropWhere(IPropertyMetadata prop, String filter, List<String> wheres, String propOwnerName, int queryLevel) throws EntityException{
    	/* Soma um ponto para compor corretamente a expressao HQL 'propOwnerName.propName = ABC' */
    	if(!propOwnerName.equals(""))
    		propOwnerName += ".";
    	
    	//Verifica se a propriedade n�o � calculada, pois propriedade calculada 
		//n�o � persistida e n�o � possivel realizar pesquisa por ela.
		if (!prop.isCalculated()){
			// Trata as propriedades primitivas String
			if(prop.isPrimitive()){
				if(prop.isString()){
					/* Limpa alguns caracteres especiais que n�o podem ser fornecidos pelo operador*/
					String strFilter = filter.replace("'", "").replace("\"", "");
					
					/* Trata os asteriscos que podem ser fornecidos pelo operador */
					if(filter.contains("*"))
						wheres.add(propOwnerName + prop.getName() + " like '" + strFilter.replace("*", "%") + "'");
					else
						wheres.add(propOwnerName + prop.getName() + " like '%" + strFilter.replace(" ", "%") + "%'");
				}else
					if(prop.isCalendar()){
						String dateFilter= CalendarUtils.formatToSQLDate(filter);

						/* Se o filtro p�de ser convertido para uma data SQL ele � aplicado ao campo */
						if(!dateFilter.equals(""))
							wheres.add(propOwnerName + prop.getName() + " like '%" + dateFilter + "%'");
					}else
					// Trata as propriedades NUM�RICA INTEIROS int, long
					// Trata as propriedades NUM�RICA double, bigdeciaml
					if(NumberUtils.isNumber(filter.replace(",", "."))||StringUtils.isNumeric(filter)){
						String numberFilter = filter.replace(",", ".");

						if(StringUtils.isNumeric(filter)){
							// Trata as propriedades primitivas Long
							if(prop.isLong())
								wheres.add(propOwnerName + prop.getName() + " = " + filter);
							
							// Trata as propriedades primitivas Integer
							if(prop.isInteger())
								wheres.add(propOwnerName + prop.getName() + " = " + filter);
						}
						
						// Trata as propriedades primitivas Double
						if(prop.isDouble())
							wheres.add(propOwnerName + prop.getName() + " = " + numberFilter);
						
						// Trata as propriedades primitivas BigDecimal
						if(prop.isBigDecimal())
							wheres.add(propOwnerName + prop.getName() + " = " + numberFilter);
					}
			}else
			// Trata as propriedades que s�o entidades
			if(prop.isEntity() && !prop.isCollection())
			{
				if(StringUtils.isNumeric(filter))
					wheres.add(propOwnerName + prop.getName() + ".id = " + filter);

	
				/* Verifica se ainda n�o atingiu a profundidade m�xima 
				 * para continuar a pesquisar recursivamentes nas entidades */
				if(queryLevel >0 )
					/* Desce RECURSIVAMENTE para pesquisar dentro das entidades
					 * relacionadas com a atual entidade */
					getEntityWhere(prop.getType(), filter, wheres, propOwnerName + prop.getName(), queryLevel--);
			}
		}
   	
    }
    
    public void getEntityWhere(Class<?> classEnt, String filter, List<String> wheres, String propOwnerName, int queryLevel) throws EntityException{
    	/* Verifica se a propriedade atual possui uma propriedade pai.
    	 * Exemplo: Pessoa.enderecoCorrespondencia aponta para uma classe do tipo Endereco, logo, 
    	 * ao pesquisar a classe pessoa, � possivel pesquisar tamb�m as propriedades da entidade
    	 * Endereco relacionada ao objeto Pessoa. Assim, a propriedade enderecoCorrespondencia ser� 
    	 * a propOwner da entidade Endereco, isto porque, na composi��o da HQL � necess�rio construir
    	 * uma express�o assim: entity.enderecoCorrespondencia.nome like "" */
    	
    
    	// Obtem os metadados da entidade
		IEntityMetadata md = this.getServiceManager().getEntityManager().getEntityMetadata(classEnt);
		
		// Prepara as condi��es para todas as propriedades da classe
		for(IPropertyMetadata prop: md.getProperties()){
			//Verifica se a propriedade n�o � calculada, pois propriedade calculada 
			//n�o � persistida e n�o � possivel realizar pesquisa por ela.
			getPropWhere(prop, filter, wheres, propOwnerName, queryLevel);
		}
    	
    }
    
    private void getEntityCondictions(Class<?> classEnt, String filter, List<QueryCondiction> condictions) throws BusinessException{
    	// Obtem os metadados da entidade
		IEntityMetadata md = this.getServiceManager().getEntityManager().getEntityMetadata(classEnt);
		
		//marca a condiction como sendo a primeira
		boolean isFirst = true;
		QueryCondiction cond = null;
		// Prepara as condi��es para todas as propriedades da classe
		for(IPropertyMetadata prop: md.getProperties()){
			/* Verifica se a propriedade poder� ser interpetrada pela Condiction */
			if(QueryCondiction.checkVersionSupport(prop) && QueryCondiction.checkValueSupport(prop, filter)){
				cond = new QueryCondiction(this.getServiceManager().getEntityManager(), classEnt);
				
				/* Verificando se a cole��oFechando o par�ntese do �ltimo elemento da cole��o */
				if(isFirst){
					cond.setOpenPar(isFirst);
					isFirst = false;
				}
				cond.setInitOperator(QueryCondiction.INIT_OR);
				cond.setPropertyPath(prop.getName());
				
				// Trata as propriedades primitivas String
				if(prop.isPrimitive()){
					if(prop.isString())
						cond.setOperatorId(Operator.LIKE);
					else
						if(prop.isCalendar())
							cond.setOperatorId(Operator.LIKE);
						else
							/* Num�rico ou boolean */
							cond.setOperatorId(Operator.EQUAL);
				}else
					/* do tipo Entity */
					cond.setOperatorId(Operator.EQUAL);
				
				cond.setValue1(filter);
				
				condictions.add(cond);
				
			}

			/* Trata um primeiro n�vel para acessar as propriedades string 
			 * TODO IMPLEMENTAR Recursividade para percorrer todas as propriedades que tiverem definidas como
			 * isAllowSubQuery */
			if(prop.isEntity() && QueryCondiction.checkVersionSupport(prop) && prop.isAllowSubQuery()){
				IEntityMetadata mdProp = this.getServiceManager().getEntityManager().getEntityMetadata(prop.getType());
				
				// Prepara as condi��es para todas as propriedades da classe
				for(IPropertyMetadata subProp: mdProp.getProperties()){
					/* Verifica se a propriedade poder� ser interpetrada pela Condiction */
					if(subProp.isString() && QueryCondiction.checkVersionSupport(subProp) && QueryCondiction.checkValueSupport(subProp, filter)){
						cond = new QueryCondiction(this.getServiceManager().getEntityManager(), classEnt);
						
						/* Verificando se a cole��oFechando o par�ntese do �ltimo elemento da cole��o */
						if(isFirst){
							cond.setOpenPar(isFirst);
							isFirst = false;
						}
						cond.setInitOperator(QueryCondiction.INIT_OR);
						cond.setPropertyPath(prop.getName()+ "." + subProp.getName());
						
						// Trata as propriedades primitivas String
						cond.setOperatorId(Operator.LIKE);
				
						cond.setValue1(filter);
						
						condictions.add(cond);
					}
				}
			}
			
		}
		
		/* Verificando se tem items na cole��o que foi 
		 * criado pelo 'for' acima. Sen�o cria uma clausula nula
		 * para que nenhum registro seja exibido */
		if(cond != null){
    		/* Fechando o par�ntese do �ltimo elemento da cole��o */
			cond.setClosePar(true);
		}else{
			/* Cria um condi��o nula que for�ar� um resultado vazio caso o filtro n�o seja aplicado a nenhum propriedade
			 * da entidade. Isto porque, em uma entidade que possui somente campos numericos e o operador digita uma string, 
			 * a pesquisa n�o aplica a condi��o string a nenhum campo e o sistema retora todos os registros, pois
			 * nenhum filto foi aplicado. Assim, � criado este filtro do tipo OR FALSE=TRUE  
			 */
			cond = new QueryCondiction(this.getServiceManager().getEntityManager(), classEnt, IDAO.PROPERTY_ID_NAME, Operator.EQUAL, IDAO.ENTITY_UNSAVED+"", "" );
			condictions.add(cond);
		}
		
    }
}