package br.com.orionsoft.monstrengo.crud.support;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

/**
 * 
 * @author estagio
 */
public class DocumentParserFields {

	public static final String FIELD_EXPRESSION_BEGIN = "@{";
	public static final char FIELD_EXPRESSION_PARAM_SEPARATOR = ',';
	public static final char FIELD_EXPRESSION_END = '}';
	
	/* Esta constante é utilizada para tratar expressões encadeadas que estão
	 * dentro das expressões @{} e que também podem utilizar o símbolo '}'
	 * para encerrar a expressão */
	public static final char FIELD_EXPRESSION_BALANCE_END = '{';
	
	/**
	 * Este método analisa o documento em busca de campos que são 
	 * identificados pela expressão:
	 * @{fieldName1, defaultValue}
	 * @param documentSource
	 * @return Retorna um mapa com os nomes dos campos encontrados. 
	 * Se o mapa estiver vazio é porque nenhum campo foi encontrado
	 * @throws BusinessException
	 */
	public static Map<String, String> findFields(final String documentSource) throws BusinessException{
		/* Uses a SortedMap implementation to keep entry order */
		Map<String, String> mapFields = new LinkedHashMap<String, String>();

		try{
			int i=0;
			int expBeginLength = FIELD_EXPRESSION_BEGIN.length();
			
			/* Percorre toda a string de entrada */ 
			while(i<documentSource.length()){
				
				//"Ola sr #{Contrato[?].nome}#{}, tudo bem."
				while((i+expBeginLength)<documentSource.length() && !(StringUtils.substring(documentSource, i, i+expBeginLength).equals(FIELD_EXPRESSION_BEGIN))){
					i++;
				}

				/* Verifica se o while anterior parou porque achou o inicio da expressão */
				if(StringUtils.substring(documentSource, i, i+expBeginLength).equals(FIELD_EXPRESSION_BEGIN)){
					
					String fieldName = "";
					String fieldDefaultValue = "";
					i += expBeginLength;
					
					/* Guarda a expressao */
					while (i<documentSource.length() && (documentSource.charAt(i) != FIELD_EXPRESSION_PARAM_SEPARATOR) && (documentSource.charAt(i) != FIELD_EXPRESSION_END)){ 
						fieldName += documentSource.charAt(i);
						i++;
					}

					/* Verifica se o while anterior parou porque achou o separador de parâmetros */
					if (documentSource.charAt(i) == FIELD_EXPRESSION_PARAM_SEPARATOR){
						/* Pula o separador */
						i++; 
						
						/* Começa a pegar o valor padrão */
						/* Usada para fazer o balanceamento de {} em expressões encadeada @{fieldName, #{Entity[?].property}}*/
						int balanceEnd = 0; 

						/* Pega o valor padrão controlando a presença de subExpressões que podem estar no valor padrão e que usem os mesmos
						 * símbolos de { e } */
						while (i<documentSource.length() && (documentSource.charAt(i) != FIELD_EXPRESSION_END || balanceEnd != 0)){ 
							
							/* Verifica se o caractere corrente é um { para evitar que ao encontrar o outro }
							 * este parser pare de percorrer e não encontre o verdadeiro { da expressão */
							if(documentSource.charAt(i) == FIELD_EXPRESSION_BALANCE_END) 
								balanceEnd++;
							else
								if(documentSource.charAt(i) == FIELD_EXPRESSION_END) 
									balanceEnd--;

							fieldDefaultValue += documentSource.charAt(i);
							i++;
						}

						
						/* Pula o ultimo caractere } ou final de string */
						i++; 

					}else
						if (documentSource.charAt(i) == FIELD_EXPRESSION_END){
							/* Não faz nada, tudo terminou normal*/
							/* Pula o end */
							i++; 
						}else					
							/* Verifica se o while anterior parou porque achou o final da expressão */
							if (i==documentSource.length()){
								throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_END_EXPRESSION", fieldName));
							}
					
					/* Trata os espaços em branco nas laterais */
					fieldName = StringUtils.strip(fieldName);
					fieldDefaultValue = StringUtils.strip(fieldDefaultValue);

					/* Verifica se o campo já está no mapa, para não substitui o primeiro
					 * valor padrão encontrado */
					if(!mapFields.containsKey(fieldName))
						/* Adiciono no mapa o campo que foi encontrado */
						mapFields.put(fieldName, fieldDefaultValue);
				}
				else
				/* Verifica se o while anterior parou porque não seria possivel iniciar uma expressao com o restante de caractere da string */
				if ((i+expBeginLength)>=documentSource.length()){ 
//					result.append(documentSource.charAt(i));
					i++;
				}
				
			}
			
			
		}catch(BusinessException e)
		{
			// "Não foi possível executar o parsing da string <b>{0}</b>." 
			e.getErrorList().add(new BusinessMessage(DocumentParserFields.class, "ERROR_PARSING_STRING", documentSource));
			throw e;
		}
		
		return mapFields;
	}
	
	

	/**
	 * 
	 * @param documentSource
	 * @param fieldsValues
	 * @return Retorna o documento com os campos já substituidos pelos seus respectivos valores
	 * @throws BusinessException
	 */
	public static String replaceFields(final String documentSource, final Map<String,String> fieldsValues) throws BusinessException{
		StringBuffer result = new StringBuffer();

		try{
			int i=0;
			int expBeginLength = FIELD_EXPRESSION_BEGIN.length();
			
			/* Percorre toda a string de entrada */ 
			while(i<documentSource.length()){
				
				//"Ola sr #{Contrato[?].nome}#{}, tudo bem."
				while((i+expBeginLength)<documentSource.length() && !(StringUtils.substring(documentSource, i, i+expBeginLength).equals(FIELD_EXPRESSION_BEGIN))){
					result.append(documentSource.charAt(i));
					i++;
				}

				/* Verifica se o while anterior parou porque achou o inicio da expressão */
				if(StringUtils.substring(documentSource, i, i+expBeginLength).equals(FIELD_EXPRESSION_BEGIN)){
					
					String fieldName = "";
					i += expBeginLength;
					
					
					/* Guarda a expressao */
					while (i<documentSource.length() && (documentSource.charAt(i) != FIELD_EXPRESSION_PARAM_SEPARATOR) && (documentSource.charAt(i) != FIELD_EXPRESSION_END)){ 
						fieldName += documentSource.charAt(i);
						i++;
					}

					/* Verifica se o while anterior parou porque achou o separador de parâmetros */
					if (documentSource.charAt(i) == FIELD_EXPRESSION_PARAM_SEPARATOR){
						/* Pula o separador */
						i++; 
						
						/* Começa a pegar o valor padrão */
						/* Usada para fazer o balanceamento de {} em expressões encadeada @{fieldName, #{Entity[?].property}}*/
						int balanceEnd = 0; 

						/* Pega o valor padrão controlando a presença de subExpressões que podem estar no valor padrão e que usem os mesmos
						 * símbolos de { e } */
						while (i<documentSource.length() && (documentSource.charAt(i) != FIELD_EXPRESSION_END || balanceEnd != 0)){ 
							
							/* Verifica se o caractere corrente é um { para evitar que ao encontrar o outro }
							 * este parser pare de percorrer e não encontre o verdadeiro { da expressão */
							if(documentSource.charAt(i) == FIELD_EXPRESSION_BALANCE_END) 
								balanceEnd++;
							else
								if(documentSource.charAt(i) == FIELD_EXPRESSION_END) 
									balanceEnd--;

							i++;
						}

						
						/* Pula o ultimo caractere } ou final de string */
						i++; 

					}else
						if (documentSource.charAt(i) == FIELD_EXPRESSION_END){
							/* Não faz nada, tudo terminou normal*/
							/* Pula o end */
							i++; 
						}else					
							/* Verifica se o while anterior parou porque achou o final da expressão */
							if (i==documentSource.length()){
								throw new BusinessException(MessageList.create(CrudExpression.class, "ERROR_END_EXPRESSION", fieldName));
							}

					/* Trata os espaços em branco nas laterais */
					fieldName = StringUtils.strip(fieldName);
					
					/* Adiciono no mapa o campo que foi encontrado */
					result.append(fieldsValues.get(fieldName));

				}
				else
				/* Verifica se o while anterior parou porque não seria possivel iniciar uma expressao com o restante de caractere da string */
				if ((i+expBeginLength)>=documentSource.length()){ 
					result.append(documentSource.charAt(i));
					i++;
				}
				
			}
			
			
		}catch(BusinessException e)
		{
			// "Não foi possível executar o parsing da string <b>{0}</b>." 
			e.getErrorList().add(new BusinessMessage(DocumentParserFields.class, "ERROR_PARSING_STRING", documentSource));
			throw e;
		}
		
		return result.toString();
	}
	
	
//	private static void mainFindFields(String[] args){
//		try
//		{
//			for(Entry entry: DocumentParserFields.findFields("@{oi,value} oi @{ei,#{oi}}").entrySet())
//			    System.out.println("TESTE: " + entry.getKey() + "=" + entry.getValue());
//		} catch (BusinessException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	
//	public static String oi(String str){
//		str = "oioi";
//		
//		return "oi";
//		
//	}
//	
//	public static void main(String[] args){
//		String str = null;
//		System.out.println(oi(str));
//		System.out.println(str);
//		
//		
//		Map<String, String> map2 = new HashMap();
//		map2.put("oi", "Olá");
//		map2.put("ei", "Mundo");
//
//	    List<Entry> entry = new ArrayList<Entry>();
//	    entry.addAll(map2.entrySet());
//
//		for(Entry ent: entry){
//			ent.setValue("novo");
//		}
//
//        System.out.println("TESTE: " + map2);
//		
//		try
//		{
//			String documentSource = "@{oi,value} oi @{ei,#{oi}}";
//			Map<String, String> map = DocumentParserFields.findFields(documentSource);
//			
//			map.put("oi", "Olá");
////			map.put("ei", "Mundo");
//			
//			System.out.println(DocumentParserFields.replaceFields(documentSource, map)); 
//		} catch (BusinessException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
