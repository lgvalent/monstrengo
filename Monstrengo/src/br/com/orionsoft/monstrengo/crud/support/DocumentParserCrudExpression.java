package br.com.orionsoft.monstrengo.crud.support;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.crud.support.CrudExpression;
import br.com.orionsoft.monstrengo.crud.support.DocumentParserCrudExpression;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityManager;

/**
 * 
 * @author estagio
 */
public class DocumentParserCrudExpression {
	
	/**
	 * Este m�todo usado para compilar express�es CRUDs de entidades que n�o utilizam entidades din�micas,
	 * ou seja, cujo o modelo de documentos n�o est�o vinculados a entidades
	 * @param str
	 * @param entityManager
	 * @return
	 * @throws BusinessException
	 */
	public static String parseString(final String str, final IEntityManager entityManager) throws BusinessException{
		Map<String, IEntity> mapEntities = new HashMap<String, IEntity>(1);
		
		return parseString(str, mapEntities, entityManager);
	}
	
	/**
	 * Este m�todo usado para compilar express�es CRUDs que referenciam uma entidade din�mica.
	 * @param str
	 * @param sourceEntity
	 * @param entityManager
	 * @return
	 * @throws BusinessException
	 */
	public static String parseString(final String str, final IEntity sourceEntity, final IEntityManager entityManager) throws BusinessException{
		Map<String, IEntity> mapEntities = new HashMap<String, IEntity>(1);
		
		mapEntities.put(sourceEntity.getInfo().getType().getSimpleName(), sourceEntity);
		
		return parseString(str, mapEntities, entityManager);
	}
	
	/**
	 * Este m�todo usado para compilar express�es CRUDs que referenciam mais de uma entidade din�mica.
	 * As entidades din�micamente referenciadas na express�o ser�o buscadas no mapEntities.
	 * @param str
	 * @param mapEntities
	 * @param entityManager
	 * @return
	 * @throws BusinessException
	 */
	public static String parseString(final String str, final Map<String, IEntity> mapEntities, final IEntityManager entityManager) throws BusinessException{
		StringBuffer result = new StringBuffer();
		try{
			int i=0;
			int expBeginLength = CrudExpression.EXPRESSION_BEGIN.length()-1;
			
			/* Percorre toda a string de entrada */ 
			while(i<str.length()){
				
				//"Ola sr #{Contrato[?].nome}#{}, tudo bem."
				while((i+expBeginLength)<str.length() && !(StringUtils.substring(str, i, i+expBeginLength+1).equals(CrudExpression.EXPRESSION_BEGIN))){
					result.append(str.charAt(i));
					i++;
				}
				
				/* Verifica se o while anterior parou porque achou o inicio da express�o */
				if(StringUtils.substring(str, i, i+expBeginLength+1).equals(CrudExpression.EXPRESSION_BEGIN)){
					String expression = "";
					
					/* Guarda a expressao */
					while (i<str.length() && (str.charAt(i) != CrudExpression.EXPRESSION_END)){ 
						expression += str.charAt(i);
						i++;
					}
					
					/* Verifica se o while anterior parou porque achou o final da express�o */
					if (i<str.length()){
						/* Adiciona o '}' na expressao */
						expression += str.charAt(i);
						i++; 
					}
					
					/* Obtem o valor a partir da expressao e adiciona em result */
					result.append(CrudExpression.expressionToValue(expression, mapEntities, entityManager));
				}
				else
				/* Verifica se o while anterior parou porque n�o seria possivel iniciar uma expressoa com o restante de caractere da string */
				if ((i+expBeginLength)>=str.length()){ 
					result.append(str.charAt(i));
					i++;
				}
				
			}
			
			
		}catch(BusinessException e)
		{
			// "N�o foi poss�vel executar o parsing da string <b>{0}</b>." 
			e.getErrorList().add(new BusinessMessage(DocumentParserCrudExpression.class, "ERROR_PARSING_STRING", str));
			throw e;
		}
		
		return result.toString();
	}
	
	/**
	 * Este m�todo analisa streams de entrada de textos planos. 
	 * Textos planos s�o todos arquivos que podem ser abertos por
	 * editores de texto e continuam legiveis, como html, xml, txt.
	 *  
	 */
	public static OutputStream parseDocument(InputStream plainTextStream, Map<String, IEntity> mapEntities, IEntityManager entityManager) throws BusinessException{
		OutputStream result = new ByteArrayOutputStream();
		
		DataInputStream input = new DataInputStream(plainTextStream);
		
		try{
			String line;
			
			/* Percorre todo stream de entrada */
			while((line = input.readLine()) != null){
				/* Escreve o byte de sa�da */
				result.write(parseString(line, mapEntities, entityManager).getBytes());
				/* Recoloca a quebra de linha */
				result.write("\n".getBytes());
			}
			
		}catch(IOException e){
			BusinessException be = new BusinessException(MessageList.createSingleInternalError(e));
			// "N�o foi poss�vel executar o parsing da string <b>{0}</b>." 
			be.getErrorList().add(new BusinessMessage(DocumentParserCrudExpression.class, "ERROR_PARSING_DOCUMENT", plainTextStream));
			throw be;
		}catch(BusinessException e){
			// "N�o foi poss�vel executar o parsing da string <b>{0}</b>." 
			e.getErrorList().add(new BusinessMessage(DocumentParserCrudExpression.class, "ERROR_PARSING_DOCUMENT", plainTextStream));
			throw e;
		}
		
		return result;
	}
}
