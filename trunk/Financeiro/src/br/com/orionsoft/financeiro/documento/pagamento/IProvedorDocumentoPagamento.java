package br.com.orionsoft.financeiro.documento.pagamento;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;

/**
 * Esta Interface define as funcionalidades de um provedor de documento.
 * Este provedor terá registrado todos os gerenciadores de documentos do sistema,
 * onde cada gerenciador manipula um tipo de documento diferente.
 * 
 * @version 20060601
 *
 */
public interface IProvedorDocumentoPagamento
{
   /** Permite que os serviços obtenha um gerenciador de documentos de acordo com 
    * o nome do gerenciador passado 
    * @param nomeGerenciador
    * @return
    * @throws BusinessException 
    */
	public IGerenciadorDocumentoPagamento retrieveGerenciadorDocumentoPagamento(String nomeGerenciador) throws BusinessException;
   
	public void registrarGerenciador(IGerenciadorDocumentoPagamento gerenciador);
	
    /** Provê acesso para o gerenciador de serviços acessar toda a estrutura 
     * da arquitetura (ServiceManager, EntityManager, ProcessManager)
     */
	public IServiceManager getServiceManager();
    public void setServiceManager(IServiceManager serviceManager);
    
    public void init();
}
