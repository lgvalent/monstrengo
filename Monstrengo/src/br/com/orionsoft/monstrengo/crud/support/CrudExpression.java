package br.com.orionsoft.monstrengo.crud.support;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.ExtensiveMoneyBr;
import br.com.orionsoft.monstrengo.core.util.ExtensiveNumberBr;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityCollection;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IEntityMetadata;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * #{Contrato[?].pessoa.nome} 
 * #{Contrato[23].pessoa.nome} 
 * #{Contrato[?].pessoa.telefones[0]}
 * #{now()} 
 * #{now(dd 'de' MMMM 'de' yyyy, 30)}
 * #{extensiveNumber(15)}
 * #{extensiveMoney(15,54)}
 * #{extensiveMoney(Movimento[?].valor)}
 * #{format(Movimento[?].dataVencimento,dd 'de' MMMM 'de' yyyy)}
 * #{format(Movimento[?].valor,R$ %,.2f)}
 * 
 * @author Lucio e Tatiana
 * @version 20070124
 *
 */
public class CrudExpression {
	private static Logger log = Logger.getLogger(CrudExpression.class);
	
	public static final String EXPRESSION_BEGIN = "#{";
	public static final char EXPRESSION_END = '}';
	public static final char EXPRESSION_ID_BEGIN = '[';
	public static final char EXPRESSION_ID_END = ']';
	public static final char EXPRESSION_FUNCTION_BEGIN = '(';
	public static final char EXPRESSION_FUNCTION_END = ')';
	public static final char EXPRESSION_ID_WILDCARD = '?';
	public static final char EXPRESSION_PROPERTY_SEPARATOR = '.';
	private static final char FUNCTION_PARAM_SEPARATOR = ',';
	
	/**
	 * 
	 * 
	 * #{}
	 * 
	 * @param expression
	 * @param mapEntities
	 * @param entityManager
	 * @return
	 * @throws BusinessException
	 */
	public static String expressionToValue(String expression, Map<String, IEntity> mapEntities, IEntityManager entityManager) throws BusinessException{
		String result = "";
		try{
			/* Verificar se a expression inicia com '#{' */
			if (!expression.substring(0, 2).equals(EXPRESSION_BEGIN))
				throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_BEGIN_EXPRESSION", expression));

			/* Verificar se a expression termina com '}'*/
			if(expression.charAt(expression.length()-1) != EXPRESSION_END)
				throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_END_EXPRESSION", expression));
			
			/* Pega o conteudo da  expressão para ser analisada  
			 * por exemplo: ApplicationUser[?].name*/
			String entityOrFunctionName=StringUtils.substring(expression, EXPRESSION_BEGIN.length(), expression.length()-1);
			result = entityOrFunction(entityOrFunctionName, mapEntities, entityManager);
			
		}catch(BusinessException e)
		{
			// "Não foi possível interpretar a expressão <b>{0}</b>. Mapra de entidades fornecido: <b>{1}</b>." 
			e.getErrorList().add(new BusinessMessage(CrudExpression.class, "ERROR_PARSING_EXPRESSION", expression, mapEntities));
			
			throw e;
		}
		
		return result;
		/* Verifica se tem um ? ou um número até o próximo ']'  */
		/* SIM TEM NUMERO */
		/* tranformar entitySimpleName em br.com.orionsoft....... */
		/* Vai no crud com o br.com...... e o id e executa RETRIEVE */
		/* Coloca no mapa a entidade encontradoa com 'SImpleName' + ':' + 'id' */
		/* Manda a entidade I Entitiy o resto do PATH para uma iontrucao percorrer a entidade e dar o valor em getAsString() */
		/* TEM ? */
		/* Busca no mapa uma chave com 'EntitySimpleName' */
		/* NAO: Exception = Entidade nao econtrada */
		/* SIM: Manda a entidade IEntitiy o resto do PATH para uma iontrucao percorrer a entidade e dar o valor em getAsString() */
	}

	private static String entityOrFunction(String expression, Map<String, IEntity> mapEntities, IEntityManager entityManager) throws BusinessException{
		int i=0;

		String entityOrFunctionName="";
		
		/* Pega o nome da entidade ou função */
		while(i<expression.length() && (expression.charAt(i)!=EXPRESSION_ID_BEGIN) && (expression.charAt(i)!=EXPRESSION_FUNCTION_BEGIN)){
			entityOrFunctionName += expression.charAt(i);
			i++;
		}
		
		/* Verifica se saiu do while porque a string acabou sem achar um inicio de ID "[" ou de Funcao "(" */
		if (i==expression.length())
			throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_ENTITY_FUNCTION_NAME_END", expression));
		
		/* Verifica se até aqui achou um nome de Entity */
		if (expression.charAt(i)==EXPRESSION_ID_BEGIN){
			// Bula o _BEGIN 
			i++;

			String entityId="";
			String entityPropertyPath="";

			/* Trata a possível recursivaidade de entityName[entityName[1].id]*/
			int stackChar = 1;
			
			/* Pega o id da entidade */
			while(i<expression.length() && (stackChar > 0)){
				
				/* Se achou mais um inicio incrementa a pilha */
				if (expression.charAt(i)==EXPRESSION_ID_BEGIN)
					stackChar++;
				else
					/* Se achou um final decrementa a pilha até ela chegar a zero */
					if(expression.charAt(i)==EXPRESSION_ID_END)
						stackChar--;
					
				/* Evita de acrescentar no entityId o último ] */
				if(stackChar>0)
					entityId += expression.charAt(i);
				
				i++;
			}
			/* Verifica se o final da expressão foi encontrado sem encontrar o caracter de encerramento, senao dá msg de expressao terminada incorretamente */
			if(i==expression.length())
				throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_ENTITY_ID_END", expression));

			/* Pula o fecha ID "]"*/
			i++;
			
			/* Pega o propertyPath da entidade */
			while(i<expression.length()){
				entityPropertyPath += expression.charAt(i);
				i++;
			}

			return entityToValue(entityOrFunctionName, entityId, entityPropertyPath, mapEntities, entityManager);
			
		} 
		/* Descobriu que é uma função */
		else if (expression.charAt(i)==EXPRESSION_FUNCTION_BEGIN){
			// Bula o _BEGIN 
			i++;

			String functionParams="";
			
			/* Pega o id da entidade */
			while(i<expression.length() && expression.charAt(i)!=EXPRESSION_FUNCTION_END){
				functionParams += expression.charAt(i);
				i++;
			}
			
			/* Verifica se o final da função foi encontrado sem encontrar o caracter de encerramento, senao dá msg de expressao terminada incorretamente */
			if(i==expression.length())
				throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_ENTITY_FUNCTION_END", expression));
			
			return functionToValue(entityOrFunctionName, functionParams, mapEntities, entityManager);
		}

		
		return "";
	}

	
	private static String entityToValue(final String entitySimpleName, String entityId, final String entityPropertyPath, final Map<String, IEntity> mapEntities, IEntityManager entityManager) throws BusinessException{
		String result = "";
		if(entityId.length() == 1 && entityId.charAt(0) == EXPRESSION_ID_WILDCARD){
			/* O Operador NAO forneceu o ID */
			/* Obtem a entidade no Mapa de entidades passado pelo SImpleName da entidade encontrado na expressão */
			IEntity entity = mapEntities.get(entitySimpleName);
			
			/* Verifica se foi encontrada no map a entidade com id coringa */
			if(entity == null)
				// "A expressão referencia a entidade <b>{0}</b>, porém não foi encontrada no mapa de entidades passado nenhuma entidade com este nome. Se a expressão utiliza alguma entidade do tipo Entity[?], lembre-se de forncer uma entidade deste tipo no mapa de entidades." 
				throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_ENTITY_NOTFOUND_MAP",  entitySimpleName));
			
			/* Obtem o valor do caminho da propriedade na entidade encontrada no mapa */
			result =  propertyPathToValue(entity, entityPropertyPath);
			
		}else{
			/* Verifica se o entityId é um id Numérico ou uma outra expressão entityOrFunction */
			if(!NumberUtils.isDigits(entityId)){
				entityId = entityOrFunction(entityId, mapEntities, entityManager);
				
				// "O id <b>{0}</b> fornecido na expressão não é um id numérico válido. Forneça um id numérico válido para entidade que será carregado, ou use o coringa '?' no lugar do id para que o sistema obtenha do mapa de entidades a entidade referenciada." 
//				throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_ENTITY_ID_INVALID", entityId));
			}
			
			/* Monta a chave com simpleName e id*/
			String simpleNameId = entitySimpleName + entityId;

			/* O Operador forneceu o ID e será dado retrieve na entidade */
			/* Verificar se o simpleName + id estão no mapa */
			IEntity entity = mapEntities.get(simpleNameId);

			/* Se SIM, usa do mapa*/
			if(entity != null){
				/* Achou no mapa */
				result = propertyPathToValue(entity, entityPropertyPath);
			}else{
				/* Se NAO, pega o metadados */
				/* Obtem os metadados da entidade pelo SImpleName da entidade encontrado na expressão */
				IEntityMetadata entityMetadata = entityManager.getEntitiesMetadata().get(entitySimpleName);
				
				/* Verifica se o nome da entidade foi encontrado no entityManager */
				if(entityMetadata == null)
					// "Não foi possível localizar uma entidade registrada no sistema com o nome simples de <b>{0}</b>. Verifique se o nome da entidade foi corretamente digitado na expressão." 
					throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_ENTITY_NAME_NOTFOUND", entitySimpleName));
				
				entity = UtilsCrud.retrieve(entityManager.getServiceManager(),
						entityMetadata.getType(),
						Long.parseLong(entityId),
						null);
				
				/* Armazena no mapa a entidade buscada para depois nao precisar 
				 *buscar denovo pois estará no mapa com o id
				 */
				mapEntities.put(simpleNameId, entity);
				
				/* Obtem o valor do caminho da propriedade na entidade encontrada no mapa */
				result = propertyPathToValue(entity, entityPropertyPath);
				
			}
		}
		return result;
	}
	
	/**
	 * #{now()}
	 * @param functionName
	 * @param functionParams
	 * @return
	 * @throws BusinessException 
	 */
	private static String functionToValue(final String functionName, final String functionParams, Map<String, IEntity> mapEntities, IEntityManager entityManager) throws BusinessException{
		String result = "";
		String[] params = StringUtils.split(functionParams,FUNCTION_PARAM_SEPARATOR);

		/*
		 * Permite:
		 * abs(#{Entity[?].bigDecimal})
		 * abs(number)
		 */
		if(functionName.equals("abs")){
			String param=functionParams;
			
			/* Verifica se o parâmetro OBRIGATORIO da função BigDecimal não é vazio */
			if(functionParams == null || functionParams.equals("")){
				throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_FUNCTION_PARAM_REQUIRED", "abs", "expression #{} or number"));
			}else{
				/* Verifica se ele não é numérico, ou seja, deve ser uma crudExpression
				 * e obtém a entidade  #{extensiveMoney(Movimento[?].valor)} */
				if(!NumberUtils.isNumber(functionParams.replace(".","").replace(",","."))){
					param = entityOrFunction(functionParams, mapEntities, entityManager);
				}
				/* Verifica se na entidade achou algum valor */
				if (!param.equals("")){
					/* Obtém o extenso a partir do parâmetro recebido ou encontrado */
					result = param.replace("-","").replace("+","");
				}
			}
		}else
		/*
		 * Permite:
		 * now()
		 * now(30)
		 * now(dd/MM/yyyy)
		 * now(',' dd 'de' MM 'de' yyyy, -30)
		 * NÂO ACEITA --> now(30, dd/MM/yyyy)
		 */
		if(functionName.equals("now")){
			int incDays = 0;
			String dateFormat = CalendarUtils.defaultFormat;
			Calendar currDate = Calendar.getInstance();
			
			if(params.length>0)
				if(!StringUtils.isEmpty(params[0]))
					if(NumberUtils.isNumber(params[0]))
						incDays = Integer.parseInt(params[0].trim());
					else
						dateFormat = params[0];
			
			if(params.length>1)
				incDays = Integer.parseInt(params[1].trim());
			
			currDate.add(Calendar.DATE, incDays);
			
			result = CalendarUtils.formatDate(dateFormat, currDate);
		}else{
			
			/*
			 * Permite:
			 * #{extensiveNumber(1)} => "Um" 
			 * #{extensiveNumber(12,50)} => "Doze vírgula cinquenta" 
			 * #{extensiveMoney(2,50)} => "Dois reais e cinquenta centavos" 
			 * #{extensiveMoney(1)} => "Um real" 
			 * #{extensiveMoney(Movimento[?].valor)} => "O valor por extenso" 
			 */
			if(functionName.equals("extensiveNumber")){
				String param=functionParams;
				
				/* Verifica se o parâmetro OBRIGATORIO da função extensoReal não é vazio */
				if(functionParams == null || functionParams.equals("")){
					throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_FUNCTION_PARAM_REQUIRED", "extensiveNumber", "expression #{} or number"));
				}else{
					/* Verifica se ele não é numérico, ou seja, deve ser uma crudExpression
					 * e obtém a entidade  #{extensiveMoney(Movimento[?].valor)} */
					if(!NumberUtils.isNumber(functionParams.replace(".","").replace(",","."))){
						param = entityOrFunction(functionParams, mapEntities, entityManager);
					}
					/* Verifica se na entidade achou algum valor */
					if (!param.equals("")){
						/* Obtém o extenso a partir do parâmetro recebido ou encontrado */
						result = ExtensiveNumberBr.getExtenso(new BigDecimal(param.replace(".","").replace(",",".")));
					}
				}
			}
			else
				if(functionName.equals("extensiveMoney")){
					String param=functionParams;
					
					/* Verifica se o parâmetro OBRIGATORIO da função extensoReal não é vazio */
					if(functionParams == null || functionParams.equals("")){
						throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_FUNCTION_PARAM_REQUIRED", "extensiveMoney", "expression #{} or number"));
					}else{
						/* Verifica se ele não é numérico, ou seja, deve ser uma crudExpression
						 * e obtém a entidade  #{extensiveMoney(Movimento[?].valor)} */
						if(!NumberUtils.isNumber(functionParams.replace(".","").replace(",","."))){
							param = entityOrFunction(functionParams, mapEntities, entityManager);
						}
						/* Verifica se na entidade achou algum valor */
						if (!param.equals("")){
							/* Obtém o extenso a partir do parâmetro recebido ou encontrado */
							result = ExtensiveMoneyBr.getExtenso(new BigDecimal(param.replace(".","").replace(",",".")));
						}
					}
				} 
				else
					/* format(propertPath, mask)*/
					if(functionName.equals("format")){
						String propertyPath=functionParams.substring(0,functionParams.indexOf(FUNCTION_PARAM_SEPARATOR));
						String mask=functionParams.substring(functionParams.indexOf(FUNCTION_PARAM_SEPARATOR)+1, functionParams.length());
						String propertyValue="";
						/* Verifica se o conteudo da função extensoReal não é vazio, e se
						 * ele não é numérico, obtém a entidade */
						if(!functionParams.equals("") && (!NumberUtils.isNumber(StringUtils.replaceChars(functionParams, ",", ".")))){
							propertyValue = entityOrFunction(propertyPath, mapEntities, entityManager);
						} 
						
						/* Verifica que tipo de dado foi retornado
						 * para decidir qual função de formatação utilizar */
						if(propertyValue.indexOf('/')>-1){
							try
							{
								result = CalendarUtils.formatDate(mask, CalendarUtils.parseCalendar(propertyValue));
							} catch (ParseException e)
							{
								BusinessMessage myMsg = new BusinessMessage(CrudExpression.class, "ERROR_INVALID_FUNCTION_PARAM", functionParams, functionName);
								result = myMsg.getMessage();
								if(log.isInfoEnabled()){
									log.info(myMsg.getMessage());
									
//									e.getMessage();
//									log.info(new BusinessMessage(CrudExpression.class, "ERROR_INVALID_FUNCTION_PARAM", functionParams, functionName));
								
								}
							}
						}
						else{
							String numberValue = StringUtils.replaceChars(propertyValue, ",", ".");
							if (NumberUtils.isNumber(numberValue)){
								result = String.format(mask, new Float (numberValue));
							}else{
								BusinessMessage myMsg = new BusinessMessage(CrudExpression.class, "ERROR_INVALID_FUNCTION_PARAM", functionParams, functionName);
								result = myMsg.getMessage();
								if(log.isInfoEnabled()){
									log.info(myMsg.getMessage());
								}
							}
						}
					} 
					else
						throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_FUNCTION_NOT_IMPLEMENTED", functionName));
		}

		
		return result.toString();
	}

	public static PropertyPathReturn propertyPathToProperty(IEntity entity, String propertyPath) throws BusinessException{
		try{
			// Para cada propriedade selecionada
			IProperty property = null;
			IEntity pathEntity = null;
			int collectionIndex=-1;
			
			if(StringUtils.isNotEmpty(propertyPath)){
				/* Inicia a busca das propriedades pela atual entidade da condição */
				pathEntity = entity;
				
				/* Inicia o parser para identificar qual entidade
				 * é a última do caminho e pegar seu valor */
				/* pessoa.enderecoCorrespondencia.logadouro.nome */
				String[] props = StringUtils.split(propertyPath,EXPRESSION_PROPERTY_SEPARATOR);
				for (String prop: props){
					
					int collectionIndexBegin = prop.indexOf(EXPRESSION_ID_BEGIN);
					int collectionIndexEnd = prop.indexOf(EXPRESSION_ID_END);
					collectionIndex=-1; // Reinicia o índice para evitar sobreposição de valor por várias coleções em um mesmo propertyPath: contrato.pessoa.socios[0].fisica.telefones[1] 
					
					/* 2o. Verifica se no final do propertypath tem um [0] com o indice
					 * do elemento de uma propriedade do tipo collection */
					if(collectionIndexBegin>-1 && collectionIndexEnd>-1){
						collectionIndex = Integer.parseInt(prop.substring(collectionIndexBegin+1, collectionIndexEnd)); 
						prop = prop.substring(0, collectionIndexBegin);
					}
					
					/* 1o. Verifica se a propriedade existe para evitar THROWS e estourar a pilar*/
					if(!pathEntity.getPropertiesMap().containsKey(prop)){
						/* Dá uma mensagem silenciosa na saida de informação para indicar ao programador que o property path está incorreto, caso
						 * ele não tenha desejado esta situação */
						if(log.isInfoEnabled()){
							log.info(new BusinessMessage(CrudExpression.class, "ERROR_PARSING_PROPERTY_PATH", prop, pathEntity.getInfo().getType().getName()).getMessage());
						}
						
						return null;
					}
					
					/* Pega a propriedade atual para verificar seu tipo */
					property = pathEntity.getProperty(prop);
					
					/* Verifica se não for primitivo obtem o tipo da entidade 
					 * para que na proxima iteração os dados desta entidade seja
					 * buscado e o caminho das propriedades continue sendo percorrido
					 * hierarquicamente */
					if(property.getInfo().isEntity()){
						if(property.getInfo().isCollection()){
							if(collectionIndex>-1){
								if(property.getInfo().isList()){
									IEntityList entityList = property.getValue().getAsEntityList();
									/* Verifica se é possivel aplicar o índice fornecido na coleção */
									if((entityList != null)&&(!entityList.isEmpty())&&(entityList.size()>collectionIndex))
										/* Pega a entidade da coleção pelo índice */
										pathEntity = entityList.get(collectionIndex);
									else
										return null;
								}
								else{
									IEntityCollection entityColletion = property.getValue().getAsEntityCollection();
									/* Verifica se é possivel aplicar o índice fornecido na coleção */
									if((entityColletion  != null)&&(!entityColletion .isEmpty())&&(entityColletion.size()>collectionIndex))
										/* Pega a entidade da coleção pelo índice */
										pathEntity = (IEntity) entityColletion.getArray()[collectionIndex];
									else
										return null;
								}
							}
							
						}else{
							/* A propriedade atual é uma entidade, então pega a entidade
							 * para continuar a percorrer o propetyPath */
							pathEntity = property.getValue().getAsEntity();
							
							/* Verifica se o valor da propriedade que é uma entidade é NULO. Assim,
							 * não há entidade para continuar a percorre o path, então pára o for
							 * e deixar o return usar o result vazio */
							if(pathEntity == null)
								break;
						}
					}else{
						if(property.getInfo().isCollection())
							if(collectionIndex>-1)
								if(property.getInfo().isList()){
									List primitiveList = property.getValue().getAsPrimitiveList();
									/* Verifica se é possivel aplicar o índice fornecido na coleção */
									if((primitiveList != null)&&(!primitiveList.isEmpty())&&(primitiveList.size()>collectionIndex))
										/* Pega a entidade da coleção pelo índice */
										return new PropertyPathReturn(property, primitiveList.get(collectionIndex).toString());
									else
										return null;
								}
								else{
									Collection primitiveCollection = property.getValue().getAsPrimitiveCollection();
									/* Verifica se é possivel aplicar o índice fornecido na coleção */
									if((primitiveCollection != null)&&(!primitiveCollection.isEmpty())&&(primitiveCollection.size()>collectionIndex))
										/* Pega a entidade da coleção pelo índice */
										return new PropertyPathReturn(property, primitiveCollection.toArray()[collectionIndex].toString());
									else
										return null;
								}
						
						break;
					}
					
				}
				
				/* Terminou de percorrer o Path.
				 * Verifica se a última propriedade do path é uma coleção e 
				 * um indice foi passado, assim, uma entidade da coleção foi selecionada
				 * e armazenada na variavel pathEntity, logo, ela somente deve ser
				 * impressa */
				if(property.getInfo().isCollection()&& (collectionIndex>-1))
					return new PropertyPathReturn(property, pathEntity.toString());
				
				/* então pega o valor da popriedade encontrada */
				return new PropertyPathReturn(property, property.getValue().getAsString());
			}
			
			return null;
			
		}catch(BusinessException e){
			// "Erro ao tentar obter o valor da propriedade <b>{0}</b> da entidade {1}. Verifique se o caminho da propriedade está correto." 
			e.getErrorList().add(new BusinessMessage(CrudExpression.class, "ERROR_PARSING_PROPERTY_PATH", propertyPath, entity));
			throw e;
		}
		
	}
	
	
	/**
	 * Esta classe é utilizada para que o método propertyPathToProperty retorne a
	 * propriedade interpretada e o valor obtido.
	 * A propriedade pode ser uma coleção. Neste caso, o índice, se fornecido,
	 * já foi analisado e o valor toString() do índice já foi retornado na
	 * propriedade value.
	 * @author estagio
	 *
	 */public static class PropertyPathReturn{
		private String value;
		private IProperty property;
		
		public PropertyPathReturn(IProperty prop, String value){
			this.property = prop;
			this.value = value;
		}
		
		public IProperty getProperty(){return property;}
		
		public String getValue(){return value;}
	}
	
	
	public static String propertyPathToValue(IEntity entity, String propertyPath) throws BusinessException{
		PropertyPathReturn result = propertyPathToProperty(entity, propertyPath);
		if(result != null)
			return result.getValue();
		
		return "";
	}

//	public static String propertyPathToValue(IEntity entity, String propertyPath) throws BusinessException{
//		try{
//			// Para cada propriedade selecionada
//			IProperty property = null;
//			IEntity pathEntity = null;
//			int collectionIndex=-1; 
//			
//			if(StringUtils.isNotEmpty(propertyPath)){
//				/* Inicia a busca das propriedades pela atual entidade da condição */
//				pathEntity = entity;
//				
//				/* Inicia o parser para identificar qual entidade
//				 * é a última do caminho e pegar seu valor */
//				/* pessoa.enderecoCorrespondencia.logadouro.nome */
//				String[] props = StringUtils.split(propertyPath,EXPRESSION_PROPERTY_SEPARATOR);
//				for (String prop: props){
//
//					int collectionIndexBegin = prop.indexOf(EXPRESSION_ID_BEGIN);
//					int collectionIndexEnd = prop.indexOf(EXPRESSION_ID_END);
//					collectionIndex=-1; // Reinicia o índice para evitar sobreposição de valor por várias coleções em um mesmo propertyPath: contrato.pessoa.socios[0].fisica.telefones[1] 
//					
//					if(collectionIndexBegin>-1 && collectionIndexEnd>-1){
//						collectionIndex = Integer.parseInt(prop.substring(collectionIndexBegin+1, collectionIndexEnd)); 
//						prop = prop.substring(0, collectionIndexBegin);
//					}
//					
//					/* Verifica se a propriedade existe para evitar THROWS e estourar a pilar*/
//					if(!pathEntity.getPropertiesMap().containsKey(prop)){
//						/* Dá uma mensagem silenciosa na saida de informação para indicar ao programador que o property path está incorreto, caso
//						 * ele não tenha desejado esta situação */
//						if(log.isInfoEnabled()){
//							log.info(new BusinessMessage(CrudExpression.class, "ERROR_PARSING_PROPERTY_PATH", prop, pathEntity.getInfo().getType().getName()).getMessage());
//						}
//							
//						return "";
//					}
//
//					/* Pega a propriedade atual para verificar seu tipo */
//					property = pathEntity.getProperty(prop);
//					
//					
//					/* Verifica se não for primitivo obtem o tipo da entidade 
//					 * para que na proxima iteração os dados desta entidade seja
//					 * buscado e o caminho das propriedades continue sendo percorrido
//					 * hierarquicamente */
//					if(property.getInfo().isEntity()){
//						if(property.getInfo().isCollection()){
//							if(collectionIndex>-1){
//								if(property.getInfo().isList()){
//									IEntityList entityList = property.getValue().getAsEntityList();
//									/* Verifica se é possivel aplicar o índice fornecido na coleção */
//									if((entityList != null)&&(!entityList.isEmpty())&&(entityList.size()>collectionIndex))
//										/* Pega a entidade da coleção pelo índice */
//										pathEntity = entityList.get(collectionIndex);
//									else
//										return "";
//								}
//								else{
//									IEntityCollection entityColletion = property.getValue().getAsEntityCollection();
//									/* Verifica se é possivel aplicar o índice fornecido na coleção */
//									if((entityColletion  != null)&&(!entityColletion .isEmpty())&&(entityColletion.size()>collectionIndex))
//										/* Pega a entidade da coleção pelo índice */
//										pathEntity = (IEntity) entityColletion.getArray()[collectionIndex];
//									else
//										return "";
//								}
//							}
//									
//						}else{
//							/* A propriedade atual é uma entidade, então pega a entidade
//							 * para continuar a percorrer o propetyPath */
//							pathEntity = property.getValue().getAsEntity();
//
//							/* Verifica se o valor da propriedade que é uma entidade é NULO. Assim,
//							 * não há entidade para continuar a percorre o path, então pára o for
//							 * e deixar o return usar o result vazio */
//							if(pathEntity == null)
//								break;
//						}
//					}else{
//						if(property.getInfo().isCollection())
//							if(collectionIndex>-1)
//								if(property.getInfo().isList()){
//									List primitiveList = property.getValue().getAsPrimitiveList();
//									/* Verifica se é possivel aplicar o índice fornecido na coleção */
//									if((primitiveList != null)&&(!primitiveList.isEmpty())&&(primitiveList.size()>collectionIndex))
//										/* Pega a entidade da coleção pelo índice */
//										return primitiveList.get(collectionIndex).toString();
//									else
//										return "";
//								}
//								else{
//									Collection primitiveCollection = property.getValue().getAsPrimitiveCollection();
//									/* Verifica se é possivel aplicar o índice fornecido na coleção */
//									if((primitiveCollection != null)&&(!primitiveCollection.isEmpty())&&(primitiveCollection.size()>collectionIndex))
//										/* Pega a entidade da coleção pelo índice */
//										return primitiveCollection.toArray()[collectionIndex].toString();
//									else
//										return "";
//								}
//						
//						break;
//					}
//
//				}
//
//				/* Terminou de percorrer o Path.
//				 * Verifica se a última propriedade do path é uma coleção e 
//				 * um indice foi passado, assim, uma entidade da coleção foi selecionada
//				 * e armazenada na variavel pathEntity, logo, ela somente deve ser
//				 * impressa */
//				if(property.getInfo().isCollection()&& (collectionIndex>-1))
//					return pathEntity.toString();
//				
//				/* então pega o valor da popriedade encontrada */
//				return property.getValue().getAsString();
//			}
//			
//			return "";
//			
//		}catch(BusinessException e){
//			// "Erro ao tentar obter o valor da propriedade <b>{0}</b> da entidade {1}. Verifique se o caminho da propriedade está correto." 
//			e.getErrorList().add(new BusinessMessage(CrudExpression.class, "ERROR_PARSING_PROPERTY_PATH", propertyPath, entity));
//			throw e;
//		}
//		
//	}
}
