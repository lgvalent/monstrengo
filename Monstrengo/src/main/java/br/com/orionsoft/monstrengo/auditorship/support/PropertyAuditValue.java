package br.com.orionsoft.monstrengo.auditorship.support;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;

/**
 * Esta classe manipula a l�gica de auditoria de uma entidade.
 * A classe analisa a propriedade de uma entidade para armazenar
 * o valor de compara��o que ser� utilizado no final processo para auditar
 * as altera��es ocorridas na entidade.
 * 
 * @author Lucio 2005/11/25
 *
 */
public class PropertyAuditValue
{
    private IProperty property;
    
    private Object oldValue=null;
    
    private static final String OLD_VALUE_NEW_VALUE_DIVIDER = "->";

    /**
     * Constr�i um valor para ser auditado posteriormente.
     * @param prop Propriedades que ser� auditada.
     * @param process TODO
     * @throws BusinessException
     */
    public PropertyAuditValue(IProperty prop) throws BusinessException
    {
        // Armazena qual a propriedade que ser� monitorada para a auditoria
        this.property = prop;
      
        // Analisa o valor atual e armazena para posterior compara��o e
        // detec��o de altera��es
        this.oldValue = prepareValue();
      
    }

    private Object prepareValue() throws BusinessException
    {
        Object result = null;
        
        
        /* Se for calculado e somente leitura, ent�o este campo, na verdade, depende totalmente de outro e nunca
         * ser� alterado, sua mudan�a na verdade sempre � o resultado da altera��o de outra propriedade. Logo,
         * n�o se deve registrar auditoria desta propriedade  */
        if (property.getInfo().isCalculated()&&property.getInfo().isReadOnly()){
        	result = null;
        }
        else
        	if (property.getInfo().isCollection())
        	{
        		if (property.getInfo().isPrimitive())
        		{
        			result = property.getValue().getAsPrimitiveCollection();
        		}
        		else
        		{
        			Collection<Object> coll = new ArrayList<Object>();
        			
        			for (IEntity ent: property.getValue().getAsEntityCollection())
        				coll.add(ent.getId());
        			
        			result = coll;
        		}
        	}
        	else
        	{
        		if (property.getInfo().isPrimitive())
        			result = property.getValue().getAsString();
        		else
        			result = property.getValue().getId();
        	}
        
        return result;
    }

    /**
     * Este m�todo analisa a propriedade atual com outra propriedade 
     * e seu valore anterior e atual e verifica se houve
     * alguma altera��o.
     *  
     * @param prop Propriedade da entidade que ser� analisada
     * @return Valor da propriedade que ser� usado para compara��o ou vazio 
     * @throws BusinessException 
     */
    public String retrieveAuditDescriptionIfChanged() throws BusinessException
    {
    	// Define a classifica��o b�sica de um propriedade para a auditoria
    	boolean isCollection = property.getInfo().isCollection();
    	
    	
    	// Constr�i o atual valor da propriedade para ser comparado com 
    	// o oldValue constru�do no construtor da classe (in�cio)
    	Object newValue = prepareValue();
    	
    	// Prepara informa��es sobre a propriedade que ser�o montadas
    	// na descri��o da auditoria
    	String propName = property.getInfo().getName();
    	String result = propName;
    	
    	// Linha exemplo:
    	// nome='Marcia';numeroPessoas=3;telefones.id=+[1,2]-[3]
    	// Define se o tipo da propriedade n�o � um n�mero nem id para ent�o receber aspas.  
    	String aspas = "";
    	
    	if (! (property.getInfo().isNumber() || property.getInfo().isEntity()))
    	{
    		aspas = "'";
    	}
    	else 
    	{
    		// Se for entidade, n�o recebe aspas, mas recebe uma descri��o adicional 
    		// no nome da propriedade ".id"
    		if (property.getInfo().isEntity())
    		{
    			result += ".id";
    		}
    	}
    	// Monta o in�cio do resultado: prop= ou prop.id=
    	result += "=";
    	try{
    		
    		if (isCollection)
    		{
    			// Pega as listas e trata se o antigo valor e' nulo 
    			Collection oldCollection = oldValue!=null?(Collection) oldValue:new ArrayList(1);
    			Collection newCollection = (Collection) newValue;
    			String removedValues = "";
    			String addedValues = "";
    			
    			// Verifica os valores removidos
    			// Pega a lista velha e busca cada item na lista nova
    			// Os que n�o estiverem foram removidos
    			for(Object obj: oldCollection)
    			{
    				// agrupa os valores removidos
    				if(!newCollection.contains(obj))
    				{
    					removedValues += aspas + obj.toString() + aspas + ",";
    				}
    			}   
    			
    			// Verifica os valores adicionados
    			// Pega a lista nova e busca cada item na lista velha
    			// Os que n�o estiverem foram adicionados
    			for(Object obj: newCollection)
    			{
    				// agrupa os valores adicionados
    				if(!oldCollection.contains(obj))
    				{
    					addedValues += aspas + obj.toString() + aspas + ",";
    				}
    			}
    			
    			// Monta a String resultado
    			if (removedValues != "")
    			{
    				// remove a �ltima v�rgula
    				StringUtils.stripEnd(removedValues, ",");
    				result += "-[" + removedValues.length() + "]";
    			}
    			if (addedValues != "")
    			{
    				// remove a �ltima v�rgula
    				StringUtils.stripEnd(addedValues, ",");
    				result += "+[" + addedValues + "]";
    			}
    			
    			// Verifica se houve alguma mudan�a, sen�o resulta null
    			if ((removedValues == "") && (addedValues == "")) 
    			{
    				result = null;
    			}
    			
    		}
    		// se n�o for uma lista
    		else
    		{
    			// Verifica se houve alguma mudan�a e trata se o antigo valor e' nulo
    			if (oldValue==null && newValue!=null)
    			{
    				/* Registrando o valor anterior */
    				result += aspas + aspas;
    				
    				/* Registrando o valor atual */
    				result += OLD_VALUE_NEW_VALUE_DIVIDER + aspas + newValue + aspas;
    				
    			}
    			else
      			if (oldValue!=null && newValue==null)
       			{
   					result += aspas + oldValue + aspas;

   					result += OLD_VALUE_NEW_VALUE_DIVIDER + aspas + aspas;

       			}
       			else
           			if (oldValue!=null && newValue!=null &&!oldValue.equals(newValue))
   				{
   					result += aspas + oldValue + aspas;

   					result += OLD_VALUE_NEW_VALUE_DIVIDER + aspas +  newValue + aspas;

   				}
   				else
   					// Sen�o, retorna null
   					result = null;
    		}
    		
    		return result;
    	}catch(Exception e){
    		throw new BusinessException(MessageList.createSingleInternalError(e));
    	}
    }
}