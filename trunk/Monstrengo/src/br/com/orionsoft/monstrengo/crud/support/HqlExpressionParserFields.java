package br.com.orionsoft.monstrengo.crud.support;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

/**
 * @{type, name, value}
 * @author estagio
 */
public class HqlExpressionParserFields {
	
	public static final String FIELD_EXPRESSION_BEGIN = "@{";
	public static final char FIELD_EXPRESSION_PARAM_SEPARATOR = ',';
	public static final char FIELD_EXPRESSION_END = '}';
	
	public static class HqlExpressionField{
		private Class type;
		private String name;
		private String value;
		
		public HqlExpressionField(Class type, String name, String value){
			this.type = type;
			this.name = name;
			this.value = value;
		}
		public String getName(){return name;}
//		public void setName(String name){this.name = name;}
		
		public Class getType(){return type;}
//		public void setType(Class type){this.type = type;}
		
		public String getValue(){return value;}
		public void setValue(String value){this.value = value;}
	}
	
	/**
	 * Este m�todo analisa o documento em busca de campos que s�o 
	 * identificados pela express�o:
	 * @{fieldType, fieldName, defaultValue}
	 * @param expressionSource
	 * @return Retorna um mapa com os nomes dos campos encontrados. 
	 * Se o mapa estiver vazio � porque nenhum campo foi encontrado
	 * @throws BusinessException
	 */
	public static Map<String, HqlExpressionField> findFields(final String expressionSource) throws BusinessException{
		Map<String, HqlExpressionField> mapFields = new HashMap<String, HqlExpressionField>();
		
		if (expressionSource != null){
			try{
				int i=0;
				int expBeginLength = FIELD_EXPRESSION_BEGIN.length();

				/* Percorre toda a string de entrada */ 
				while(i<expressionSource.length()){

					//"Ola sr #{Contrato[?].nome}#{}, tudo bem."
					while((i+expBeginLength)<expressionSource.length() && !(StringUtils.substring(expressionSource, i, i+expBeginLength).equals(FIELD_EXPRESSION_BEGIN))){
						i++;
					}

					/* Verifica se o while anterior parou porque achou o inicio da express�o */
					if(StringUtils.substring(expressionSource, i, i+expBeginLength).equals(FIELD_EXPRESSION_BEGIN)){

						String fieldTypeName = "";
						String fieldName = "";
						String fieldDefaultValue = "";
						i += expBeginLength;

						/* Guarda o tipo */
						while (i<expressionSource.length() && (expressionSource.charAt(i) != FIELD_EXPRESSION_PARAM_SEPARATOR) && (expressionSource.charAt(i) != FIELD_EXPRESSION_END)){ 
							fieldTypeName += expressionSource.charAt(i);
							i++;
						}

						/* Verifica se o while anterior parou porque achou o separador de par�metros */
						if (expressionSource.charAt(i) == FIELD_EXPRESSION_PARAM_SEPARATOR){
							/* Pula o separador */
							i++; 

							/* Guarda o nome */
							while (i<expressionSource.length() && (expressionSource.charAt(i) != FIELD_EXPRESSION_PARAM_SEPARATOR) && (expressionSource.charAt(i) != FIELD_EXPRESSION_END)){ 
								fieldName += expressionSource.charAt(i);
								i++;
							}

							/* Verifica se o while anterior parou porque achou o separador de par�metros */
							if (expressionSource.charAt(i) == FIELD_EXPRESSION_PARAM_SEPARATOR){
								/* Pula o separador */
								i++; 

								/* Pega o valor padr�o controlando a presen�a de subExpress�es que podem estar no valor padr�o e que usem os mesmos
								 * s�mbolos de { e } */
								while (i<expressionSource.length() && expressionSource.charAt(i) != FIELD_EXPRESSION_END){ 

									fieldDefaultValue += expressionSource.charAt(i);
									i++;
								}


								/* Pula o ultimo caractere } ou final de string */
								i++; 
							}
						}else
							if (expressionSource.charAt(i) == FIELD_EXPRESSION_END){
								/* N�o faz nada, tudo terminou normal*/
								/* Pula o end */
								i++; 
							}else					
								/* Verifica se o while anterior parou porque achou o final da express�o */
								if (i==expressionSource.length()){
									throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_END_EXPRESSION", fieldName));
								}

						/* Trata os espa�os em branco nas laterais */
						fieldTypeName = StringUtils.strip(fieldTypeName);
						fieldName = StringUtils.strip(fieldName);
						fieldDefaultValue = StringUtils.strip(fieldDefaultValue);

						/*
						 * Verifica o tipo do dado do campo a ser entrado 
						 * -Number
						 * -String
						 * -Calendar
						 * */
						Class fieldType = null;
						if(fieldTypeName.equals("Number"))
							fieldType = Double.class;
						else
							if(fieldTypeName.equals("String"))
								fieldType = String.class;
							else
								if(fieldTypeName.equals("Calendar"))
									fieldType = Calendar.class;


						/* Verifica se o campo j� est� no mapa, para n�o substitui o primeiro
						 * valor padr�o encontrado */
						if(!mapFields.containsKey(fieldName))
							/* Adiciono no mapa o campo que foi encontrado */
							mapFields.put(fieldName, new HqlExpressionField(fieldType, fieldName, fieldDefaultValue));

					}else
						/* Verifica se o while anterior parou porque n�o seria possivel iniciar uma expressao com o restante de caractere da string */
						if ((i+expBeginLength)>=expressionSource.length()){ 
//							result.append(documentSource.charAt(i));
							i++;
						}


				}


			}catch(BusinessException e)
			{
				// "N�o foi poss�vel executar o parsing da string <b>{0}</b>." 
				e.getErrorList().add(new BusinessMessage(HqlExpressionParserFields.class, "ERROR_PARSING_STRING", expressionSource));
				throw e;
			}
		}		
		return mapFields;
	}
	
	
	
	/**
	 * 
	 * @param expressionSource
	 * @param fieldsValues
	 * @return Retorna o documento com os campos j� substituidos pelos seus respectivos valores
	 * @throws BusinessException
	 */
	public static String replaceFields(final String expressionSource, final Map<String,HqlExpressionField> fieldsValues) throws BusinessException{
		StringBuffer result = new StringBuffer();
		
		try{
			int i=0;
			int expBeginLength = FIELD_EXPRESSION_BEGIN.length();
			
			/* Percorre toda a string de entrada */ 
			while(i<expressionSource.length()){
				
				//"Ola sr #{Contrato[?].nome}#{}, tudo bem."
				while((i+expBeginLength)<expressionSource.length() && !(StringUtils.substring(expressionSource, i, i+expBeginLength).equals(FIELD_EXPRESSION_BEGIN))){
					result.append(expressionSource.charAt(i));
					i++;
				}
				
				/* Verifica se o while anterior parou porque achou o inicio da express�o */
				if(StringUtils.substring(expressionSource, i, i+expBeginLength).equals(FIELD_EXPRESSION_BEGIN)){
					
					String fieldName = "";
					i += expBeginLength;
					
					
					/* Guarda a expressao */
					while (i<expressionSource.length() && (expressionSource.charAt(i) != FIELD_EXPRESSION_PARAM_SEPARATOR) && (expressionSource.charAt(i) != FIELD_EXPRESSION_END)){ 
//						fieldName += documentSource.charAt(i);
						i++;
					}
					
					/* Verifica se o while anterior parou porque achou o separador de par�metros */
					if (expressionSource.charAt(i) == FIELD_EXPRESSION_PARAM_SEPARATOR){
						/* Pula o separador */
						i++; 
						
						/* Guarda a expressao */
						while (i<expressionSource.length() && (expressionSource.charAt(i) != FIELD_EXPRESSION_PARAM_SEPARATOR) && (expressionSource.charAt(i) != FIELD_EXPRESSION_END)){ 
							fieldName += expressionSource.charAt(i);
							i++;
						}
						
						/* Verifica se o while anterior parou porque achou o separador de par�metros */
						if (expressionSource.charAt(i) == FIELD_EXPRESSION_PARAM_SEPARATOR){
							/* Pula o separador */
							i++; 
							
							/* Pega o valor padr�o controlando a presen�a de subExpress�es que podem estar no valor padr�o e que usem os mesmos
							 * s�mbolos de { e } */
							while (i<expressionSource.length() && expressionSource.charAt(i) != FIELD_EXPRESSION_END){ 
								
								i++;
							}
							
							
							/* Pula o ultimo caractere } ou final de string */
							i++;
						}
						
					}else
						if (expressionSource.charAt(i) == FIELD_EXPRESSION_END){
							/* N�o faz nada, tudo terminou normal*/
							/* Pula o end */
							i++; 
						}else					
							/* Verifica se o while anterior parou porque achou o final da express�o */
							if (i==expressionSource.length()){
								throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_END_EXPRESSION", fieldName));
							}
					
					/* Trata os espa�os em branco nas laterais */
					fieldName = StringUtils.strip(fieldName);

					String fieldValue;
					HqlExpressionField fieldInfo = fieldsValues.get(fieldName);
					
					/* Se o tipo for num�rico */
					if(fieldInfo.getType() == Double.class)
						fieldValue = fieldInfo.getValue().replace(".", "").replace(",","."); 
					/* Se o tipo for Calendar */
					if(fieldInfo.getType() == Calendar.class)
						fieldValue = CalendarUtils.formatToSQLDate(fieldInfo.getValue()); 
					else
						/* Se o tipo for String */
						fieldValue = fieldInfo.getValue(); 
					
					/* Adiciono no mapa o campo que foi encontrado */
					System.out.println("FIELD VALUE: " + fieldValue);
					result.append(fieldValue);
					
				}else
				
				/* Verifica se o while anterior parou porque n�o seria possivel iniciar uma expressao com o restante de caractere da string */
				if ((i+expBeginLength)>=expressionSource.length()){ 
					result.append(expressionSource.charAt(i));
					i++;
				}
				
			}
			
			
		}catch(BusinessException e)
		{
			// "N�o foi poss�vel executar o parsing da string <b>{0}</b>." 
			e.getErrorList().add(new BusinessMessage(HqlExpressionParserFields.class, "ERROR_PARSING_STRING", expressionSource));
			throw e;
		}
		
		return result.toString();
	}
	
	
//	private static void mainFindFields(String[] args){
//	try
//	{
//	for(Entry entry: DocumentParserFields.findFields("@{oi,value} oi @{ei,#{oi}}").entrySet())
//	System.out.println("TESTE: " + entry.getKey() + "=" + entry.getValue());
//	} catch (BusinessException e)
//	{
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//	}
//	}
	
	
//	public static String oi(String str){
//	str = "oioi";
//	
//	return "oi";
//	
//	}
//	
//	public static void main(String[] args){
//	String str = null;
//	System.out.println(oi(str));
//	System.out.println(str);
//	
//	
//	Map<String, String> map2 = new HashMap();
//	map2.put("oi", "Ol�");
//	map2.put("ei", "Mundo");
//	
//	List<Entry> entry = new ArrayList<Entry>();
//	entry.addAll(map2.entrySet());
//	
//	for(Entry ent: entry){
//	ent.setValue("novo");
//	}
//	
//	System.out.println("TESTE: " + map2);
//	
//	try
//	{
//	String documentSource = "@{oi,value} oi @{ei,#{oi}}";
//	Map<String, String> map = DocumentParserFields.findFields(documentSource);
//	
//	map.put("oi", "Ol�");
////	map.put("ei", "Mundo");
//	
//	System.out.println(DocumentParserFields.replaceFields(documentSource, map)); 
//	} catch (BusinessException e)
//	{
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//	}
//	}
}
