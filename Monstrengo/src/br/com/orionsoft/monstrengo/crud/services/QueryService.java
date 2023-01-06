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
 * Este serviço, pesquisa uma entidade baseando-se em um
 * filtro fornecido. Inicialmente, este filtro é um conteúdo que será procurado 
 * em vários campos da entidade e subentidade.
 * 
 * <p><b>Argumentos:</b>
 * <br> IN_ENTITY_TYPE: A classe das entidades que serão listadas.
 * <br> IN_QUERY_SELECT: Uma string com uma lista ou função de agregação 
 *                       para ser usada na cláusula SELECT da HQL. Se esta opção for utilizada,
 *                       a lista de entidade retornada será nula e o resultado da pesquisa
 *                       será armazenado na lista de saida do select (OUT_OBJECT_LIST).
 *                       Esta lista será uma lista onde cada item é um vetor que contem 
 *                       as propriedades selecionadas.
 * <br>
 * <br> IN_QUERY_FILTER: Uma string com um conteúdo a ser procurado.
 * <br> IN_QUERY_CONDICTIONS: Uma lista do tipo QueryCondictions com cláusulas de seleção QueryCondiction.
 * <br> IN_QUERY_HQLWHERE: Uma string com uma expressão HQL já usando o alias 'entity.' para ser aplicada diretamente na cláusula WHERE.
 * <br>
 * <br> IN_PARENT_CLASS_OPT: A classe pai que possui algum relacionamento com a classe atual.
 * <br> IN_PARENT_ID_OPT: O id da classe pai que possui algum relacionamento com a classe atual.
 * <br> IN_PARENT_PROPERTY_OPT: A propriedade da classe pai que se relaciona com a classe atual.  
 * <br>
 * <br> IN_ORDER_EXPRESSION_OPT: Uma expressão de ordenação do tipo "PropertyName ASC, PropertyName DESC".  
 * <br> IN_ORDER_CONDICTIONS_OPT: Uma lista do tipo OrderCondictions com cláusulas de ordenação OrderCondiction.  
 * <br>
 * <br> IN_MAX_RESULT_OPT: Um número inteiro que define o número de registros que devem ser retornados pela consulta.  
 * <br> IN_FIRST_RESULT_OPT: Um número inteiro que define o registro inicial da coleção que será retornada pela consulta.  
 *  
 * <p><b>Procedimento:</b>
 * <br>Utilizando o filtro, monta as expressões de busca para todas as propriedades da entidade.
 * <br>Verifica se tem uma entidade pai e monta a junção das duas entidades.
 * <br>Aplica as expressões montadas.
 * <br>Realiza a pesquisa.
 * <br>Realiza outra pesquisa para obter o total da coleção da pesquisa não paginada.
 * <br>Converte a coleção de objetos em uma coleção de entidades.
 * 
 * <p><b>Retorno:</b>
 * <br>Se nenhum filtro for fornecido será retornada uma coleção vazia.
 * <br><b>OUT_ENTITY_LIST</b>: Retorna a lista de entidades (IEntityList).
 * <br><b>OUT_LIST_SIZE</b>: Retorna a tamanho total da lista não paginada (Integer).
 * <br><b>OUT_QUERY_TIME</b>: Retorna o tempo de demora da consulta em milissegundos (Long).
 * <br><b>OUT_SELECT_LIST</b>: Retorna uma lista de objetos resultantes da seleção personalizada de propriedade pela opção IN_QUERY_SELECT.<br>
 *                             Se o select tiver somente uma propriedade o resultado é uma List(Object>;<br>
 *                             Se o select tiver mais de uma propriedade o resultado é uma List(Object[]>;<br>
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
        	log.debug("Iniciando a execução do serviço QueryService");
        	// Obtém os parâmetros
        	Class<?> inEntityType = (Class<?>) serviceData.getArgumentList().getProperty(IN_ENTITY_TYPE);

        	// Obtém os parâmetros OPCIONAIS
    		String inQueryFilter = "";
        	if(serviceData.getArgumentList().containsProperty(IN_QUERY_FILTER))
        		inQueryFilter = (String) serviceData.getArgumentList().getProperty(IN_QUERY_FILTER);

            // Obtém os parâmetros OPCIONAIS
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
        	
        	/* Verifica se foi fornecida uma lista de condições */
        	if((inQueryCondictions!=null)&&inQueryCondictions.size()>0) {
        		log.debug("Utilizando as condições informadas");
        		condictions.addAll(inQueryCondictions);
        		
        		/* Para ficar assim: filter and|or (expr1 and|or expr2 and|or expr3) */
        		log.debug("Define as parametrizações dos parênteses");
        		// TODO CORRIGIR Não funcionou, gera uma expressão 'and (entity.valor=25) and entity.valor=24)'
        		//inQueryCondictions.get(0).setOpenPar(true);
        		//inQueryCondictions.get(inQueryCondictions.size()-1).setClosePar(true);
        		
        	}

        	/* Tenta realizar a pesquisa se tiver condições filtradas ou um Pai definido */
        	String fromExpression = " from " + inEntityType.getName() + " " + IDAO.ENTITY_ALIAS_HQL;
        	String whereExpression = "";
        	
        	/* Verifica se possui um pai para incluí-lo na clausua FROM 
        	 * Estrutuda da consulta:
        	 * SELECT entity FROM entity, parent WHERE (entity in elements(parent.property) and parent.id=1) and (expr1 or expr2)*/
        	if(inParentClass!=null && inParentId!=IDAO.ENTITY_UNSAVED){
        		fromExpression += ", " + inParentClass.getName() + " parent ";
        		whereExpression += " where (" + IDAO.ENTITY_ALIAS_HQL +" in elements(parent." + inParentProperty + ") and parent.id=" + inParentId + ")";
        	}

        	/* Identifica se a primeira expressão válida foi produzida 
        	 * para não gerar os blocos iniciais de " where " ou " and (" */
        	boolean firstAlreadyFound = false;
    		/* Utilizado para não inserir operadores na primeira expressão e
    		 * inserir operadores AND e OR ño início das demais expressões*/
        	String initOp;
    		
        	/* Utilizando para indicar o início de um parêntese de condições
        	 * que são openPar mas que não geraram expressão */
        	String initPar="";
        	
        	/* Percorre todas as condições, verifica qual é a primeira condição que
        	 * não resultará em uma cláusula vazia e trata a inicialização correta 
        	 * dos parênteses mesmo quando a condição não gera uma expressao HQL */
        	for(QueryCondiction cond: condictions){
        		
        		/* Verifica se a condição está ativa */
        		if(cond.isActive()){
        			/* Pega a expressão HQL da condição */ 
        			String str = cond.retrieveHqlExpression(IDAO.ENTITY_ALIAS_HQL);

        			/* Verifica se é a primeira Expressão, ou seja, não precisa de um operador inicial. Exemplo: '(expre)' e não 'and (express)' */
        			if(firstAlreadyFound){
        				if(cond.getInitOperator() == QueryCondiction.INIT_AND)
        					initOp = " and " + initPar;
        				else
        					initOp = " or " + initPar;
        			}else{
        				/* Achou uma condição válida e inicia o bloco logo após o pai */
        				/* Verifica se existem condições do pai para iniciar corretamente 
        				 * o bloco WHERE ou continuar o bloco do Pai */
        				if(inParentClass!=null && inParentId!=IDAO.ENTITY_UNSAVED)
        					/* Continua o bloco do PAI: (CONDICOES_PAI) and ( */
        					whereExpression += " and ("  + initPar;
        				else
        					/* Inicia um bloco Where pois não há PAI */
        					whereExpression += " where " + initPar;

        				initOp = "";
        				firstAlreadyFound = true;
        			}

        			whereExpression += initOp + str;

        			/* Depois do primeiro uso limpa o 
        			 * parêntese inicial */
        			initPar = "";

        		}else{
        			/* Tratar as condições com openPar e closePar que não geram expressão HQL */
        			if(cond.isOpenPar())
        				/* Há um problema se a primeira condição não gerar uma expressão e for
        				 * uma openPar. O resultado é: (where expre2 or expr3 ... 
        				 * Assim, se a condição não gerar uma expressão e o primeiro elemento
        				 * válido ainda não foi encontrado, o parentese e definido para ser inserido no
        				 * inicio da expressão depois do " where " ou " and (" */
        				if(firstAlreadyFound)
        					whereExpression += "(";
        				else
        					initPar = "(";
        			else
        				if(cond.isClosePar())
        					whereExpression += ")";
        		}
        	}
        	
        	/* TODO CORRIGIR Quando é gerado uma clausula das propriedades da 
        	 * entidade (expr1 OR expr2 OR expr3) e nenhuma condição gera 
        	 * uma expressão o resultado final é (), o que é inválido */
    		
        	/* Fecha o bloco AND se tiver um pai  (parent) and (condictions) + hqlWhere */
    		if(condictions.size()>0)
    			if(inParentClass!=null && inParentId!=IDAO.ENTITY_UNSAVED)
    				whereExpression += ") ";

        	/* Verifica se foi fornecido uma HQL_WHERE */
        	if(!StringUtils.isEmpty(inQueryHqlWhere)) {
        		/* Verifica se já foi iniciada um clausua where para decidir se poe um AND ou inicia um WHERE */
        		if((inParentClass!=null && inParentId!=IDAO.ENTITY_UNSAVED) || condictions.size()>0)
        			/* Continua o bloco do PAI: (CONDICOES_PAI) and ( */
        			whereExpression += " and ";
        		else
        			/* Inicia um bloco Where pois não há PAI */
        			whereExpression += " where ";

            	/* Percorre a entidade e obtem as clausulas Where de pesquisa das propriedades da entidade */
        		whereExpression += inQueryHqlWhere;
        	}
        	
        	/* Trata a opção IN_ORDER_EXPRESSION_OPT */
        	if(!StringUtils.isEmpty(inOrderExpression)){
        		whereExpression += " order by " + inOrderExpression;
        	}

        	if((inOrderCondictions != null) && (!inOrderCondictions.isEmpty())){
            	/* Verifica se há alguma condição marcada, pois se não tiver condições 
            	 * marcada o from expression não pode ser iniciado */
        		boolean hasOrderCondictionChecked = false;
        		for(OrderCondiction orderCondiction: inOrderCondictions)
        			if(orderCondiction.isActive()){hasOrderCondictionChecked = true; break;}

        		if(hasOrderCondictionChecked){
        			if(StringUtils.isEmpty(inOrderExpression))
        				/* Inicia uma nova expressão */
        				whereExpression += " order by ";
        			else
        				/* Continua a expressão já iniciada */
        				whereExpression += ",";

        			for(OrderCondiction order: inOrderCondictions)
        				if(order.isActive())
        					whereExpression += " " + order.retrieveHqlExpression(IDAO.ENTITY_ALIAS_HQL) + ",";

        			/* Remove a última vírgula */
        			whereExpression = StringUtils.stripEnd(whereExpression, ",");
            	}
        	}
        	
        	
            /* Valida a cláusula SELECT */
            String selectExpression;
            if(StringUtils.isEmpty(inQuerySelect))
                selectExpression = IDAO.ENTITY_ALIAS_HQL + fromExpression;
            else{
                /* Por questões de compatibilidade é verificado se a cláusula inQuerySelect veio com 
                 * o comando from definido pelo programador ou será necessário utilizar a já criada.
                 * Lembrando que a fromExpression pode conter implicitJoins para o parent. Se ela não
                 * for usada aqui e usada a do operador, a pesquisa com Parent não montará uma SQL válida */
            	if(inQuerySelect.toLowerCase().contains("from "))
            		selectExpression = inQuerySelect;
            	else
            		selectExpression = inQuerySelect + fromExpression;
            }
            
        	if(log.isDebugEnabled())
        		log.debug("Expressão:" + selectExpression + whereExpression);

        	List outObjectList = null;
        	try {
        		log.debug("Executando a consulta");
        		Query query = serviceData.getCurrentSession().createQuery("select " + selectExpression + whereExpression);
        		
        		/* Verifica se o parâmetro maxResult foi passado para aplica-lo*/
        		if(inMaxResult>-1)
        			query.setMaxResults(inMaxResult);
        		/* Verifica se o parâmetro firstResult foi passado para aplica-lo*/
        		if(inFirstResult>-1)
        			query.setFirstResult(inFirstResult);
        		
        		outObjectList = query.list();
        		
        		/* Verifica se foi utilizada paginação para obter o tamanho total
        		 * da lista não paginada */
        		log.debug("Calculando o número de items da pesquisa");
        		if(inMaxResult>-1){
        			/* Quando se pesquisa uma entidade que é uma superclasse, o Hibernate realiza a pesquisa em todas as tabelas
        			 * que representam as subclasses (caso as subclasses estejam definidas para armazenamento em tabelas separadas).
        			 * Exemplo: Pesquisar AuditRegister, faz com que o hibernate pesquise na tabela AuditCrudRegister e AuditProcessRegister.
        			 * Desta forma, o hibernate obtem dois totalizadores (count(*)), um para cada tabela.
        			 * Para saber o total de registros é necessário somar os constadores
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
             * Se não: Constroi uma lista de entidades
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
            // O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros.
            throw new ServiceException(e.getErrorList());
        }

    }
    
    
    public void getPropWhere(IPropertyMetadata prop, String filter, List<String> wheres, String propOwnerName, int queryLevel) throws EntityException{
    	/* Soma um ponto para compor corretamente a expressao HQL 'propOwnerName.propName = ABC' */
    	if(!propOwnerName.equals(""))
    		propOwnerName += ".";
    	
    	//Verifica se a propriedade não é calculada, pois propriedade calculada 
		//não é persistida e não é possivel realizar pesquisa por ela.
		if (!prop.isCalculated()){
			// Trata as propriedades primitivas String
			if(prop.isPrimitive()){
				if(prop.isString()){
					/* Limpa alguns caracteres especiais que não podem ser fornecidos pelo operador*/
					String strFilter = filter.replace("'", "").replace("\"", "");
					
					/* Trata os asteriscos que podem ser fornecidos pelo operador */
					if(filter.contains("*"))
						wheres.add(propOwnerName + prop.getName() + " like '" + strFilter.replace("*", "%") + "'");
					else
						wheres.add(propOwnerName + prop.getName() + " like '%" + strFilter.replace(" ", "%") + "%'");
				}else
					if(prop.isCalendar()){
						String dateFilter= CalendarUtils.formatToSQLDate(filter);

						/* Se o filtro pôde ser convertido para uma data SQL ele é aplicado ao campo */
						if(!dateFilter.equals(""))
							wheres.add(propOwnerName + prop.getName() + " like '%" + dateFilter + "%'");
					}else
					// Trata as propriedades NUMÉRICA INTEIROS int, long
					// Trata as propriedades NUMÉRICA double, bigdeciaml
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
			// Trata as propriedades que são entidades
			if(prop.isEntity() && !prop.isCollection())
			{
				if(StringUtils.isNumeric(filter))
					wheres.add(propOwnerName + prop.getName() + ".id = " + filter);

	
				/* Verifica se ainda não atingiu a profundidade máxima 
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
    	 * ao pesquisar a classe pessoa, é possivel pesquisar também as propriedades da entidade
    	 * Endereco relacionada ao objeto Pessoa. Assim, a propriedade enderecoCorrespondencia será 
    	 * a propOwner da entidade Endereco, isto porque, na composição da HQL é necessário construir
    	 * uma expressão assim: entity.enderecoCorrespondencia.nome like "" */
    	
    
    	// Obtem os metadados da entidade
		IEntityMetadata md = this.getServiceManager().getEntityManager().getEntityMetadata(classEnt);
		
		// Prepara as condições para todas as propriedades da classe
		for(IPropertyMetadata prop: md.getProperties()){
			//Verifica se a propriedade não é calculada, pois propriedade calculada 
			//não é persistida e não é possivel realizar pesquisa por ela.
			getPropWhere(prop, filter, wheres, propOwnerName, queryLevel);
		}
    	
    }
    
    private void getEntityCondictions(Class<?> classEnt, String filter, List<QueryCondiction> condictions) throws BusinessException{
    	// Obtem os metadados da entidade
		IEntityMetadata md = this.getServiceManager().getEntityManager().getEntityMetadata(classEnt);
		
		//marca a condiction como sendo a primeira
		boolean isFirst = true;
		QueryCondiction cond = null;
		// Prepara as condições para todas as propriedades da classe
		for(IPropertyMetadata prop: md.getProperties()){
			/* Verifica se a propriedade poderá ser interpetrada pela Condiction */
			if(QueryCondiction.checkVersionSupport(prop) && QueryCondiction.checkValueSupport(prop, filter)){
				cond = new QueryCondiction(this.getServiceManager().getEntityManager(), classEnt);
				
				/* Verificando se a coleçãoFechando o parêntese do último elemento da coleção */
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
							/* Numérico ou boolean */
							cond.setOperatorId(Operator.EQUAL);
				}else
					/* do tipo Entity */
					cond.setOperatorId(Operator.EQUAL);
				
				cond.setValue1(filter);
				
				condictions.add(cond);
				
			}

			/* Trata um primeiro nível para acessar as propriedades string 
			 * TODO IMPLEMENTAR Recursividade para percorrer todas as propriedades que tiverem definidas como
			 * isAllowSubQuery */
			if(prop.isEntity() && QueryCondiction.checkVersionSupport(prop) && prop.isAllowSubQuery()){
				IEntityMetadata mdProp = this.getServiceManager().getEntityManager().getEntityMetadata(prop.getType());
				
				// Prepara as condições para todas as propriedades da classe
				for(IPropertyMetadata subProp: mdProp.getProperties()){
					/* Verifica se a propriedade poderá ser interpetrada pela Condiction */
					if(subProp.isString() && QueryCondiction.checkVersionSupport(subProp) && QueryCondiction.checkValueSupport(subProp, filter)){
						cond = new QueryCondiction(this.getServiceManager().getEntityManager(), classEnt);
						
						/* Verificando se a coleçãoFechando o parêntese do último elemento da coleção */
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
		
		/* Verificando se tem items na coleção que foi 
		 * criado pelo 'for' acima. Senão cria uma clausula nula
		 * para que nenhum registro seja exibido */
		if(cond != null){
    		/* Fechando o parêntese do último elemento da coleção */
			cond.setClosePar(true);
		}else{
			/* Cria um condição nula que forçará um resultado vazio caso o filtro não seja aplicado a nenhum propriedade
			 * da entidade. Isto porque, em uma entidade que possui somente campos numericos e o operador digita uma string, 
			 * a pesquisa não aplica a condição string a nenhum campo e o sistema retora todos os registros, pois
			 * nenhum filto foi aplicado. Assim, é criado este filtro do tipo OR FALSE=TRUE  
			 */
			cond = new QueryCondiction(this.getServiceManager().getEntityManager(), classEnt, IDAO.PROPERTY_ID_NAME, Operator.EQUAL, IDAO.ENTITY_UNSAVED+"", "" );
			condictions.add(cond);
		}
		
    }
}