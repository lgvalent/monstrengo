package br.com.orionsoft.monstrengo.core.exception;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;


public class GeneralException  extends BusinessException{

    public GeneralException(MessageList errorList)
    {
        super(errorList);
        
    }
    
}
