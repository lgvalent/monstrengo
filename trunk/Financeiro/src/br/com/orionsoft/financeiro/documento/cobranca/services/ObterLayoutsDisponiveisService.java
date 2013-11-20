package br.com.orionsoft.financeiro.documento.cobranca.services;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este serviço utiliza o Provedor de documentos
 * para obter uma lista de layouts de impressão
 * disponíveis para o documento que foi passado.
 * 
 * <p>Argumentos:
 * IN_DOCUMENTO : Instância IEntity de um Documento do financeiro

 * <p>Procedimento:
 * <b>Retorna uma lista de layouts com id e nome (List<SelectItem>)</b>
 * 
 * @version 20061130
 * 
 * @spring.bean id="ObterLayoutsDisponiveisService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
 */
public class ObterLayoutsDisponiveisService extends DocumentoCobrancaServiceBasic {
	
	public static final String SERVICE_NAME = "ObterLayoutsDisponiveisService";
	public static final String IN_DOCUMENTO = "documento";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico ObterLayoutsDisponiveisService");
		
		try{
			log.debug("Preparando os argumentos");
			IEntity inDocumento = (IEntity) serviceData.getArgumentList().getProperty(IN_DOCUMENTO);
			
			List<SelectItem> result = null; 

			/* Verifica se o documento passado é nulo então retorna uma lista vazia */
			if(inDocumento == null)
				result = new ArrayList<SelectItem>();
			else{
				IGerenciadorDocumentoCobranca gerenciador = this.retrieveGerenciadorPorDocumento((DocumentoCobranca) inDocumento.getObject());

				// Cria a lista de layouts de acordo com o gerenciador controlador do tipo de documento
				result = gerenciador.getLayouts();
			}
			
			// retorna a entidade Documento que o gerenciador criou;			
			log.debug("::Fim da execução do serviço");
			serviceData.getOutputData().add(result);
			
		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/* O Serviço não precisa adicionar mensagem local. O Manager já indica qual srv falhou e os parâmetros. */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			
			/* Indica que o serviço falhou por causa de uma exceção do hibernate. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
		
	}
}
