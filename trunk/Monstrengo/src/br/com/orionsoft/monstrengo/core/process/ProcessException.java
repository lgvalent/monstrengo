package br.com.orionsoft.monstrengo.core.process;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

public class ProcessException extends BusinessException
{

    public ProcessException(MessageList errorList)
    {
        super(errorList);
    }

}
