package br.com.orionsoft.financeiro.documento.cobranca;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;

/**
 * Esta Interface define as funcionalidades de um provedor de documento.
 * Este provedor ter� registrado todos os gerenciadores de documentos do sistema,
 * onde cada gerenciador manipula um tipo de documento diferente.
 * 
 * @version 20060601
 *
 */
public interface IProvedorDocumentoCobranca
{
   /** Permite que os servi�os obtenha um gerenciador de documentos de acordo com 
    * o nome do gerenciador passado 
    * @param nomeGerenciador
    * @return
    * @throws BusinessException 
    */
	public IGerenciadorDocumentoCobranca retrieveGerenciadorDocumentoCobranca(String nomeGerenciador) throws BusinessException;
   
	public void registrarGerenciador(IGerenciadorDocumentoCobranca gerenciador);
	
    /** Prov� acesso para o gerenciador de servi�os acessar toda a estrutura 
     * da arquitetura (ServiceManager, EntityManager, ProcessManager)
     */
	public IServiceManager getServiceManager();
    public void setServiceManager(IServiceManager serviceManager);
    
    public void init();
}
