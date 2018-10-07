package br.com.orionsoft.monstrengo.crud.entity;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

public class EntityException extends BusinessException
{

    public EntityException(MessageList errorList)
    {
        super(errorList);
    }

}
