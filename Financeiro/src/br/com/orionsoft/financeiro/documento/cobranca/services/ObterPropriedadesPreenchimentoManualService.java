package br.com.orionsoft.financeiro.documento.cobranca.services;

import java.util.ArrayList;
import java.util.List;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;

/**
 * Este servi�o utiliza o Provedor de documentos
 * para obter da inst�ncia de um documento, a lista de
 * propriedades que dever�o ser exibidas e preenchidas
 * pelo operador na interface.
 * 
 * <p>Argumentos:
 * IN_DOCUMENTO : Inst�ncia IEntity de um Documento do financeiro

 * <p>Procedimento:
 * <b>Retorna uma lista de propriedades (List<IProperty>) j� ligadas com sua entidade
 * e que disponibiliza uma interface para altera��o dos dados</b>
 * 
 * @version 20060627
 * 
 * @spring.bean id="ObterPropriedadesPreenchimentoManualService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
 */
public class ObterPropriedadesPreenchimentoManualService extends DocumentoCobrancaServiceBasic {
	
	public static final String SERVICE_NAME = "ObterPropriedadesPreenchimentoManualService";
	public static final String IN_DOCUMENTO = "documento";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execu��o do servico ObterPropriedadesPreenchimentoManualService");
		
		try{
			log.debug("Preparando os argumentos");
			IEntity inDocumento = (IEntity) serviceData.getArgumentList().getProperty(IN_DOCUMENTO);
			
			List<IProperty> result = null; 

			/* Verifica se o documento passado � nulo ent�o retorna uma lista vazia */
			if(inDocumento == null)
				result = new ArrayList<IProperty>();
			else{
				IGerenciadorDocumentoCobranca gerenciador = this.retrieveGerenciadorPorDocumento((DocumentoCobranca) inDocumento.getObject());

				// Verifica se o Gerenciador possui campos de preenchimento manual
				if(gerenciador.isPreenchimentoManual())
					// Cria a lista de propriedades
					result = gerenciador.retrievePropriedadesPreenchimentoManual(inDocumento);
				else
					// Cria uma lista vazia 
					result = new ArrayList<IProperty>(0);
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
