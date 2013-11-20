package br.com.orionsoft.financeiro.documento.cobranca;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

public class DocumentoCobrancaException extends BusinessException
{

	private static final long serialVersionUID = 1L;

	public DocumentoCobrancaException(MessageList errorList)
	{
		super(errorList);
	}

}
