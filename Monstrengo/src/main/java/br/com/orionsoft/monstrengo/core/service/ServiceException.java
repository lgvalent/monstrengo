package br.com.orionsoft.monstrengo.core.service;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

public class ServiceException extends BusinessException
{

	private static final long serialVersionUID = 1L;

	public ServiceException(MessageList errorList)
    {
        super(errorList);
    }

}
