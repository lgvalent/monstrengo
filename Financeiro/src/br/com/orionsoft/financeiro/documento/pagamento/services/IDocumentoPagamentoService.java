package br.com.orionsoft.financeiro.documento.pagamento.services;

import br.com.orionsoft.financeiro.documento.pagamento.IProvedorDocumentoPagamento;

/**
 * Esta Interface define as funcionalidades de um documento. Um documento se
 * encontra registrado em um Provedor de Documentos.
 * 
 * @version 20060602
 */
public interface IDocumentoPagamentoService {
	public IProvedorDocumentoPagamento getProvedorDocumentoPagamento();

	public void setProvedorDocumentoPagamento(IProvedorDocumentoPagamento provedor);
}
