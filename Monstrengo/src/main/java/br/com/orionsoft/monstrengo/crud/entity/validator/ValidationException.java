/**
 * 
 */
package br.com.orionsoft.monstrengo.crud.entity.validator;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

/**
 * @author Lucio
 *
 */
@SuppressWarnings("serial")
public class ValidationException extends BusinessException
{

    public ValidationException(MessageList errorList)
    {
        super(errorList);
    }

}
