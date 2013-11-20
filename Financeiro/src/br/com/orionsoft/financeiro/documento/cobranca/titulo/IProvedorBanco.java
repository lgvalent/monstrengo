package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import br.com.orionsoft.financeiro.documento.cobranca.IProvedorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;

/**
 * Esta Interface define as funcionalidades de um provedor de Banco.
 * Este provedor ter� registrado todos os gerenciadores de banco do sistema,
 * onde cada gerenciador � respons�vel por gerar remessa, receber retorno, formatar
 * t�tulo, calcular nosso n�mero, calcular linha digit�vel e campo livre que s�o
 * particulares para cada banco.
 * 
 * @version 20060630
 *
 */
public interface IProvedorBanco
{
   /** Permite que os servi�os obtenha um gerenciador de banco de acordo com 
    * o nome do gerenciador passado 
    * @param nomeGerenciador
    * @return
    * @throws BusinessException 
    */
	public IGerenciadorBanco retrieveGerenciadorBanco(String codigoBanco) throws BusinessException;
   
	/**
	 * Este m�todo � utilizado pelos gerenciadores para se registrarem no provedor
	 * e ficarem dispon�vel para o sistema
	 * @param gerenciador
	 */
	public void registrarGerenciador(IGerenciadorBanco gerenciador);
	
    /** Prov� acesso para o Provedor de Documento e desta forma
     * toda a arquitetura b�sica pode ser acessada, como: Gerenciador de Servi�os, 
     * EntityManager e etc.
     */
	public IProvedorDocumentoCobranca getProvedorDocumentoCobranca();
    public void setProvedorDocumentoCobranca(IProvedorDocumentoCobranca provedorDocumentoCobranca);

    /** Inicializa o provedor do banco para que ele busque os gerenciadores de banco */
    public void init();
}
