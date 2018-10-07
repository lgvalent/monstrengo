package br.com.orionsoft.monstrengo.security.services;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

public class UtilsSecurityException extends BusinessException
{

    public UtilsSecurityException(MessageList errorList)
    {
        super(errorList);
    }

}
