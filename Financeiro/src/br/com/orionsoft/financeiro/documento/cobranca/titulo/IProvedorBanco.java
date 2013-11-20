package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import br.com.orionsoft.financeiro.documento.cobranca.IProvedorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;

/**
 * Esta Interface define as funcionalidades de um provedor de Banco.
 * Este provedor terá registrado todos os gerenciadores de banco do sistema,
 * onde cada gerenciador é responsável por gerar remessa, receber retorno, formatar
 * título, calcular nosso número, calcular linha digitável e campo livre que são
 * particulares para cada banco.
 * 
 * @version 20060630
 *
 */
public interface IProvedorBanco
{
   /** Permite que os serviços obtenha um gerenciador de banco de acordo com 
    * o nome do gerenciador passado 
    * @param nomeGerenciador
    * @return
    * @throws BusinessException 
    */
	public IGerenciadorBanco retrieveGerenciadorBanco(String codigoBanco) throws BusinessException;
   
	/**
	 * Este método é utilizado pelos gerenciadores para se registrarem no provedor
	 * e ficarem disponível para o sistema
	 * @param gerenciador
	 */
	public void registrarGerenciador(IGerenciadorBanco gerenciador);
	
    /** Provê acesso para o Provedor de Documento e desta forma
     * toda a arquitetura básica pode ser acessada, como: Gerenciador de Serviços, 
     * EntityManager e etc.
     */
	public IProvedorDocumentoCobranca getProvedorDocumentoCobranca();
    public void setProvedorDocumentoCobranca(IProvedorDocumentoCobranca provedorDocumentoCobranca);

    /** Inicializa o provedor do banco para que ele busque os gerenciadores de banco */
    public void init();
}
