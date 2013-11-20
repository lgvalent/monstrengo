package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.orionsoft.financeiro.documento.cobranca.IProvedorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;

/**
 * <p>Este provedor centraliza todos os gerenciadores de documentos disponíveis 
 * no sistema. Os gerenciadores, quando instanciados, recebem uma referencia 
 * deste provedor e automaticamente se registram nele. Assim, este provedor vai
 * ficar conhecendo cada gerenciador instanciado.
 * 
 * Tudo isto é controlado pelo SPRING<p>
 *
 * @author Lucio
 * @spring.bean id="ProvedorBanco"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
*/
public class ProvedorBanco implements IProvedorBanco
{
	public static final String PROVEDOR_NOME = "ProvedorBanco"; 
	
	protected final Logger log = Logger.getLogger(this.getClass());
	
	private IProvedorDocumentoCobranca provedorDocumentoCobranca;

	private Map<String, IGerenciadorBanco> gerenciadores = null;
	
	public void init() {
		if(gerenciadores != null)
			throw new RuntimeException("ProvedorBanco já iniciado anteriormente. O método init() não pode ser executado.");
		
		gerenciadores = new HashMap<String, IGerenciadorBanco>();
		
		/* Prepara as entidades que implementam IProvedorBanco */
		for (Class<? extends IGerenciadorBanco> klazz: this.getProvedorDocumentoCobranca().getServiceManager().getApplication().findModulesClasses(IGerenciadorBanco.class)){
			try {
				log.info("Registrando Provedor de Banco: " + klazz.getSimpleName());
				IGerenciadorBanco gerenciador = (IGerenciadorBanco) klazz.newInstance();
				gerenciador.setProvedorBanco(this);
				/* Solicita ao gerenciador que ele se registre. */
				gerenciador.registrarGerenciador();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void registrarGerenciador(IGerenciadorBanco gerenciador) {
		//coloca o gerenciador no mapa de gerenciadores
		gerenciadores.put(gerenciador.getCodigo(), gerenciador);
	}

	public IGerenciadorBanco retrieveGerenciadorBanco(String codigoBanco) throws BusinessException {
		// Verifica no mapa se tem algum gerenciador com este nome
		// SE SIM: Retorna o gerenciador
		if(gerenciadores.containsKey(codigoBanco))
			return gerenciadores.get(codigoBanco);
		
		//SE NAO: Levanta um erro de GERENCIADOR_NAO_REGISTRADO
		throw new BusinessException(MessageList.create(ProvedorBanco.class,"GERENCIADOR_NAO_ENCONTRADO", codigoBanco));
	}

	public IProvedorDocumentoCobranca getProvedorDocumentoCobranca() {return this.provedorDocumentoCobranca;}
	public void setProvedorDocumentoCobranca(IProvedorDocumentoCobranca provedorDocumentoCobranca) {this.provedorDocumentoCobranca = provedorDocumentoCobranca;}
}
