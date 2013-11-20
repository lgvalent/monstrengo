package br.com.orionsoft.financeiro.documento.cobranca.services;

import br.com.orionsoft.financeiro.documento.cobranca.IProvedorDocumentoCobranca;


/**
 *  Esta Interface define as funcionalidades de um documento.
 *  Um documento se encontra registrado em um Provedor de Documentos.
 * 	@version 20060602
 */
public interface IDocumentoCobrancaService
{
   public IProvedorDocumentoCobranca getProvedorDocumentoCobranca();
   public void setProvedorDocumentoCobranca(IProvedorDocumentoCobranca provedor);
}
