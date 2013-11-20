package br.com.orionsoft.financeiro.documento.pagamento.services;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.financeiro.documento.pagamento.ConvenioPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.IGerenciadorDocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.IProvedorDocumentoPagamento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceException;

/**
 * <p>
 * Este classe implementa as funcionalidades básicas de qualquer serviço que
 * venha a utilizar o provedor de documento e seus gerenciadores (cheque, visa,
 * boleto, etc)
 * 
 * @author Lucio
 * 
 */
public abstract class DocumentoPagamentoServiceBasic extends ServiceBasic implements IDocumentoPagamentoService {

	private static IProvedorDocumentoPagamento provedorDocumentoPagamento = null;

    @SuppressWarnings("unchecked")
	public void registerService() throws Exception {
    	super.registerService();
    	
    	if(provedorDocumentoPagamento == null){
    		boolean singletonFlag = false;
    		/* Prepara as entidades que implementam IService */
    		for (Class<? extends IProvedorDocumentoPagamento> klazz: this.getServiceManager().getApplication().findModulesClasses(IProvedorDocumentoPagamento.class)){
    			log.info("Registrando Provedor de Documento de Pagamento: " + klazz.getSimpleName());
    			try {
       				if(singletonFlag)
    					throw new RuntimeException("Somente um provedor de documento é suportado. Provedor registrado: " + provedorDocumentoPagamento.getClass().getName() + ". Provedor que está tentando ser registrado:" + klazz.getName());

       				provedorDocumentoPagamento = (IProvedorDocumentoPagamento) klazz.newInstance();
       				provedorDocumentoPagamento.setServiceManager(this.getServiceManager());
    				/* Inicializa o provedor para ele buscar  */
       				provedorDocumentoPagamento.init();
    				
    				singletonFlag = true;
    			} catch (Exception e) {
    				throw new RuntimeException(e);
    			}
    		}
    		
    	}
    }

	@SuppressWarnings("static-access")
	public IProvedorDocumentoPagamento getProvedorDocumentoPagamento() {
		return this.provedorDocumentoPagamento;
	}

	@SuppressWarnings("static-access")
	public void setProvedorDocumentoPagamento(IProvedorDocumentoPagamento provedor) {
		this.provedorDocumentoPagamento = provedor;
	}

	/**
	 * Obtem o gerenciador de documento de um determinado documento.
	 * 
	 * @param documento
	 * @return
	 * @throws BusinessException
	 */
	public IGerenciadorDocumentoPagamento retrieveGerenciadorPorDocumento(DocumentoPagamento documento) throws BusinessException {
		log.debug("Obtendo o nome do Gerenciador de Documento responsável pelo(s) documento(s) passado(s)");
		return this.retrieveGerenciadorPorConvenio(documento.getConvenioPagamento());
	}

	/**
	 * Obtem o gerenciador de documento de um determinado documento.
	 * 
	 * @param documento
	 * @return
	 * @throws BusinessException
	 */
	public IGerenciadorDocumentoPagamento retrieveGerenciadorPorConvenio(ConvenioPagamento convenio) throws BusinessException {
		log.debug("Obtendo o nome do Gerenciador de Documento responsável pelo(s) documento(s) passado(s)");
		String nomeGerenciadorDocumento = null;
		if (convenio != null)
			nomeGerenciadorDocumento = convenio.getNomeGerenciadorDocumento();

		log.debug("Verificando se foi possível achar o gerenciador responsável");
		if (StringUtils.isEmpty(nomeGerenciadorDocumento))
			throw new ServiceException(MessageList.create(DocumentoPagamentoServiceBasic.class,
					"GERENCIADOR_NAO_DEFINIDO", convenio));

		log.debug("Obtendo junto ao ProvedorDocumentoCobranca o gerenciador responsável");
		return this.getProvedorDocumentoPagamento().retrieveGerenciadorDocumentoPagamento(nomeGerenciadorDocumento);
	}
}
