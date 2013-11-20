package br.com.orionsoft.financeiro.documento.pagamento;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

public class DocumentoPagamentoException extends BusinessException
{

	private static final long serialVersionUID = 1L;

	public DocumentoPagamentoException(MessageList errorList)
	{
		super(errorList);
	}

}
