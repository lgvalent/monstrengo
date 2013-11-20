package br.com.orionsoft.financeiro.documento.cobranca.services;

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.IProvedorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceException;


/**
 * <p>Este classe implementa as funcionalidades b�sicas de qualquer
 * servi�o que venha a utilizar o provedor de documento e seus gerenciadores
 * (cheque, visa, boleto, etc)
 *
 * @author Lucio
 *
 */
public abstract class DocumentoCobrancaServiceBasic extends ServiceBasic implements IDocumentoCobrancaService
{
	/**
	 * Mantem uma inst�ncia est�tica do provedor para todos os servi�os IDocumentoCobrancaService
	 */
	private static IProvedorDocumentoCobranca provedorDocumentoCobranca = null;

    @SuppressWarnings("unchecked")
	public void registerService() throws Exception {
    	super.registerService();
    	
    	if(provedorDocumentoCobranca == null){
    		boolean singletonFlag = false;
    		/* Prepara as entidades que implementam IService */
    		for (Class<? extends IProvedorDocumentoCobranca> klazz: this.getServiceManager().getApplication().findModulesClasses(IProvedorDocumentoCobranca.class)){
    			log.info("Registrando Provedor de Documento de Cobran�a: " + klazz.getSimpleName());
    			try {
    				if(singletonFlag)
    					throw new RuntimeException("Somente um provedor de documento � suportado. Provedor registrado: " + provedorDocumentoCobranca.getClass().getName() + ". Provedor que est� tentando ser registrado:" + klazz.getName());

    				provedorDocumentoCobranca = (IProvedorDocumentoCobranca) klazz.newInstance();
    				provedorDocumentoCobranca.setServiceManager(this.getServiceManager());
    				/* Inicializa o provedor para ele buscar  */
    				provedorDocumentoCobranca.init();
    				
    				singletonFlag = true;
    			} catch (Exception e) {
    				throw new RuntimeException(e);
    			}
    		}
    		
    	}
    }
	
	@SuppressWarnings("static-access")
	public IProvedorDocumentoCobranca getProvedorDocumentoCobranca() {return this.provedorDocumentoCobranca;}
	@SuppressWarnings("static-access")
	public void setProvedorDocumentoCobranca(IProvedorDocumentoCobranca provedor) {this.provedorDocumentoCobranca = provedor;}
	
	
	/**
	 * Obtem o gerenciador de documento de um determinado documento. 
	 * @param documento
	 * @return
	 * @throws BusinessException 
	 */
	public IGerenciadorDocumentoCobranca retrieveGerenciadorPorDocumento(DocumentoCobranca documento) throws BusinessException{
		log.debug("Obtendo o nome do Gerenciador de Documento respons�vel pelo(s) documento(s) passado(s)");
		return this.retrieveGerenciadorPorConvenio(documento.getConvenioCobranca());
	}

	/**
	 * Obtem o gerenciador de documento de um determinado documento. 
	 * @param documento
	 * @return
	 * @throws BusinessException 
	 */
	public IGerenciadorDocumentoCobranca retrieveGerenciadorPorConvenio(ConvenioCobranca convenio) throws BusinessException{
		log.debug("Obtendo o nome do Gerenciador de Documento respons�vel pelo(s) documento(s) passado(s)");
		String nomeGerenciadorDocumento = null;
		if(convenio!= null)
			nomeGerenciadorDocumento = convenio.getNomeGerenciadorDocumento();
		
		log.debug("Verificando se foi poss�vel achar o gerenciador respons�vel");
		if(StringUtils.isEmpty(nomeGerenciadorDocumento))
			throw new ServiceException(MessageList.create(DocumentoCobrancaServiceBasic.class,
					  "GERENCIADOR_NAO_DEFINIDO", convenio));
		

		log.debug("Obtendo junto ao ProvedorDocumentoCobranca o gerenciador respons�vel");
		return this.getProvedorDocumentoCobranca().retrieveGerenciadorDocumentoCobranca(nomeGerenciadorDocumento);
	}
 }
