/**
 * Criado em 06/08/2005
 */
package br.com.orionsoft.monstrengo.core.exception;

import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;


/**
 * <p>Esta classe � a base para as demais exce��es. 
 * Ela mantem uma lista de erros que podem ter ocorridos e que s�o armazenados
 * na estrutura BusinessMessage. <p>
 * 
 * @see BusinessMessage 
 * @author Lucio
 *
 */
public class BusinessException extends Exception
{
    private MessageList errorList;

    public BusinessException(MessageList errorList)
    {
        super();
        
        this.errorList = errorList;
    }
    
    public MessageList getErrorList()
    {
        return errorList;
    }
    
    public String getMessage()
    {
        if (this.errorList.size() > 0)
            return this.errorList.get(0).getMessage();

        return "";
    }
    
}
