package br.com.orionsoft.monstrengo.auditorship.support;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

public class AuditorshipSupportException extends BusinessException
{

    public AuditorshipSupportException(MessageList errorList)
    {
        super(errorList);
    }

}
