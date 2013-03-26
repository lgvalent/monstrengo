package br.com.orionsoft.monstrengo.crud.entity;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

public class PropertyException extends BusinessException
{

    public PropertyException(MessageList errorList)
    {
        super(errorList);
    }

}
