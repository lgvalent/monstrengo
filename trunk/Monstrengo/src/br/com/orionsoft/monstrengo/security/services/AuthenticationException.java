package br.com.orionsoft.monstrengo.security.services;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

@SuppressWarnings("serial")
public class AuthenticationException extends BusinessException
{

    public AuthenticationException(MessageList errorList)
    {
        super(errorList);
    }

}
