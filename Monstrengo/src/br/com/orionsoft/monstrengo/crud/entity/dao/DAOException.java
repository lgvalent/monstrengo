package br.com.orionsoft.monstrengo.crud.entity.dao;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

public class DAOException extends BusinessException
{
	private static final long serialVersionUID = 1L;

	public DAOException(MessageList errorList)
    {
        super(errorList);
    }

}
