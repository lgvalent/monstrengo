package br.com.orionsoft.financeiro.documento.pagamento.services;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.IGerenciadorDocumentoPagamento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este servi�o utiliza o Provedor de documentos
 * para obter uma lista de layouts de impress�o
 * dispon�veis para o documento que foi passado.
 * 
 * <p>Argumentos:
 * IN_DOCUMENTO : Inst�ncia IEntity de um Documento do financeiro

 * <p>Procedimento:
 * <b>Retorna uma lista de layouts com id e nome (List<SelectItem>)</b>
 * 
 * @version 20061130
 * 
 * @spring.bean id="ObterLayoutsDisponiveisDocumentoPagamentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoPagamento" ref="ProvedorDocumentoPagamento"
 */
public class ObterLayoutsDisponiveisDocumentoPagamentoService extends DocumentoPagamentoServiceBasic {
	
	public static final String SERVICE_NAME = "ObterLayoutsDisponiveisDocumentoPagamentoService";
	public static final String IN_DOCUMENTO = "documento";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execu��o do servico ObterLayoutsDisponiveisDocumentoPagamentoService");
		
		try{
			log.debug("Preparando os argumentos");
			IEntity<? extends DocumentoPagamento> inDocumento = (IEntity<? extends DocumentoPagamento>) serviceData.getArgumentList().getProperty(IN_DOCUMENTO);
			
			List<SelectItem> result = null; 

			/* Verifica se o documento passado � nulo ent�o retorna uma lista vazia */
			if(inDocumento == null)
				result = new ArrayList<SelectItem>();
			else{
				IGerenciadorDocumentoPagamento gerenciador = this.retrieveGerenciadorPorDocumento(inDocumento.getObject());

				// Cria a lista de layouts de acordo com o gerenciador controlador do tipo de documento
				result = gerenciador.getLayouts();
			}
			
			// retorna a entidade Documento que o gerenciador criou;			
			log.debug("::Fim da execu��o do servi�o");
			serviceData.getOutputData().add(result);
			
		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/* O Servi�o n�o precisa adicionar mensagem local. O Manager j� indica qual srv falhou e os par�metros. */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			
			/* Indica que o servi�o falhou por causa de uma exce��o do hibernate. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
		
	}
}
