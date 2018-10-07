package br.com.orionsoft.monstrengo.crud.entity.metadata;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

public class MetadataException extends BusinessException
{
	private static final long serialVersionUID = 1L;

	public MetadataException(MessageList errorList)
    {
        super(errorList);
    }

}
