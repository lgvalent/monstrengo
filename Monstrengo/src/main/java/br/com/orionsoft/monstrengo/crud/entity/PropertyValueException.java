package br.com.orionsoft.monstrengo.crud.entity;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

public class PropertyValueException extends BusinessException
{

    public PropertyValueException(MessageList errorList)
    {
        super(errorList);
    }

}
