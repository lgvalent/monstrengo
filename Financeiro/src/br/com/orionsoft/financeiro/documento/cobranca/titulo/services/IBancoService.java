package br.com.orionsoft.financeiro.documento.cobranca.titulo.services;

import br.com.orionsoft.financeiro.documento.cobranca.titulo.IProvedorBanco;


/**
 *  Esta Interface define as funcionalidades de um banco.
 *  Um banco se encontra registrado em um Provedor de Bancos.
 * 	@version 20060914
 */
public interface IBancoService
{
   public IProvedorBanco getProvedorBanco();
   public void setProvedorBanco(IProvedorBanco provedorBanco);
}
