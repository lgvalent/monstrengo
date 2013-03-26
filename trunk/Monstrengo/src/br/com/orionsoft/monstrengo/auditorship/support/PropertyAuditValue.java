package br.com.orionsoft.monstrengo.auditorship.support;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;

/**
 * Esta classe manipula a lógica de auditoria de uma entidade.
 * A classe analisa a propriedade de uma entidade para armazenar
 * o valor de comparação que será utilizado no final processo para auditar
 * as alterações ocorridas na entidade.
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
     * Constrói um valor para ser auditado posteriormente.
     * @param prop Propriedades que será auditada.
     * @param process TODO
     * @throws BusinessException
     */
    public PropertyAuditValue(IProperty prop) throws BusinessException
    {
        // Armazena qual a propriedade que será monitorada para a auditoria
        this.property = prop;
      
        // Analisa o valor atual e armazena para posterior comparação e
        // detecção de alterações
        this.oldValue = prepareValue();
      
    }

    private Object prepareValue() throws BusinessException
    {
        Object result = null;
        
        
        /* Se for calculado e somente leitura, então este campo, na verdade, depende totalmente de outro e nunca
         * será alterado, sua mudança na verdade sempre é o resultado da alteração de outra propriedade. Logo,
         * não se deve registrar auditoria desta propriedade  */
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
     * Este método analisa a propriedade atual com outra propriedade 
     * e seu valore anterior e atual e verifica se houve
     * alguma alteração.
     *  
     * @param prop Propriedade da entidade que será analisada
     * @return Valor da propriedade que será usado para comparação ou vazio 
     * @throws BusinessException 
     */
    public String retrieveAuditDescriptionIfChanged() throws BusinessException
    {
    	// Define a classificação básica de um propriedade para a auditoria
    	boolean isCollection = property.getInfo().isCollection();
    	
    	
    	// Constrói o atual valor da propriedade para ser comparado com 
    	// o oldValue construído no construtor da classe (início)
    	Object newValue = prepareValue();
    	
    	// Prepara informações sobre a propriedade que serão montadas
    	// na descrição da auditoria
    	String propName = property.getInfo().getName();
    	String result = propName;
    	
    	// Linha exemplo:
    	// nome='Marcia';numeroPessoas=3;telefones.id=+[1,2]-[3]
    	// Define se o tipo da propriedade não é um número nem id para então receber aspas.  
    	String aspas = "";
    	
    	if (! (property.getInfo().isNumber() || property.getInfo().isEntity()))
    	{
    		aspas = "'";
    	}
    	else 
    	{
    		// Se for entidade, não recebe aspas, mas recebe uma descrição adicional 
    		// no nome da propriedade ".id"
    		if (property.getInfo().isEntity())
    		{
    			result += ".id";
    		}
    	}
    	// Monta o início do resultado: prop= ou prop.id=
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
    			// Os que não estiverem foram removidos
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
    			// Os que não estiverem foram adicionados
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
    				// remove a última vírgula
    				StringUtils.stripEnd(removedValues, ",");
    				result += "-[" + removedValues.length() + "]";
    			}
    			if (addedValues != "")
    			{
    				// remove a última vírgula
    				StringUtils.stripEnd(addedValues, ",");
    				result += "+[" + addedValues + "]";
    			}
    			
    			// Verifica se houve alguma mudança, senão resulta null
    			if ((removedValues == "") && (addedValues == "")) 
    			{
    				result = null;
    			}
    			
    		}
    		// se não for uma lista
    		else
    		{
    			// Verifica se houve alguma mudança e trata se o antigo valor e' nulo
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
   					// Senão, retorna null
   					result = null;
    		}
    		
    		return result;
    	}catch(Exception e){
    		throw new BusinessException(MessageList.createSingleInternalError(e));
    	}
    }
}