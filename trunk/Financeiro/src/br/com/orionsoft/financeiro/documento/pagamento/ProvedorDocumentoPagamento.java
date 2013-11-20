package br.com.orionsoft.financeiro.documento.pagamento;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;

/**
 * <p>Este provedor centraliza todos os gerenciadores de documentos disponíveis 
 * no sistema. Os gerenciadores, quando instanciados, recebem uma referencia 
 * deste provedor e automaticamente se registram nele. Assim, este provedor vai
 * ficar conhecendo cada gerenciador instanciado.
 * 
 * Tudo isto é controlado pelo SPRING<p>
 *
 * @author Lucio
 * @spring.bean id="ProvedorDocumentoPagamento"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ProvedorDocumentoPagamento implements IProvedorDocumentoPagamento
{
	public static final String PROVEDOR_NOME = "ProvedorDocumentoCobranca"; 
	
    protected Logger log = LogManager.getLogger(getClass());

	private IServiceManager serviceManager;

	private Map<String, IGerenciadorDocumentoPagamento> gerenciadores = null;
	
	/**
	 * Este método cria a lista de Gerenciadores e busca todas as 
	 * A lista de DAOs auxilia o restante da arquitetura a saber quantas entidades são mantidas, ou seja,
	 * quantas entidades são CRUD
	 */
	@SuppressWarnings("unchecked")
	public void init(){
		if(gerenciadores != null)
			throw new RuntimeException("ProvedorDocumentoCobranca já iniciado anteriormente. O método init() não pode ser executado.");
		
		gerenciadores = new HashMap<String, IGerenciadorDocumentoPagamento>();
		
		/* Prepara as entidades que implementam IService */
		for (Class<? extends IGerenciadorDocumentoPagamento> klazz: this.getServiceManager().getApplication().findModulesClasses(IGerenciadorDocumentoPagamento.class)){
			try {
				log.info("Registrando Gerenciador de Documento de Pagamento: " + klazz.getSimpleName());
				IGerenciadorDocumentoPagamento gerenciador = (IGerenciadorDocumentoPagamento) klazz.newInstance();
				gerenciador.setProvedorDocumentoPagamento(this);
				/* Solicita ao gerenciador que ele se registre. */
				gerenciador.registrarGerenciador();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public IGerenciadorDocumentoPagamento retrieveGerenciadorDocumentoPagamento(String nomeGerenciador) throws BusinessException{
		// Verifica no mapa se tem algum gerenciador com este nome
		// SE SIM: Retorna o gerenciador
		if(gerenciadores.containsKey(nomeGerenciador))
			return gerenciadores.get(nomeGerenciador);
		
		//SE NAO: Levanta um erro de GERENCIADOR_NAO_REGISTRADO
		throw new BusinessException(MessageList.create(ProvedorDocumentoPagamento.class,"GERENCIADOR_NAO_ENCONTRADO", nomeGerenciador));
	}

	public void registrarGerenciador(IGerenciadorDocumentoPagamento gerenciador) {
		//coloca o gerenciador no mapa de gerenciadores
		gerenciadores.put(gerenciador.getNome(), gerenciador);
	}

	public IServiceManager getServiceManager() {return serviceManager;}
	public void setServiceManager(IServiceManager serviceManager) {this.serviceManager = serviceManager;}
}
