package br.com.orionsoft.financeiro.documento.cobranca.services;

import java.util.Calendar;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este serviço é utilizado para quitar um documento.
 * 
 * <p>Argumentos:</p>
 * IN_DOCUMENTO: (IEntity) Instancia do documento que será quitado.
 * IN_CONTA: (IEntity) 
 * IN_VALOR: (BigDecimal) Valor a ser quitado.
 * IN_DATA_MOVIMENTO: (Calendar) Data do movimento a ser quitado.
 * IN_TRANSACAO: (int) Tipo de transação CRÉDITO/DÉBITO
 * 
 * @version 20060622
 * 
 * @spring.bean id="QuitarDocumentoCobrancaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
 * @spring.property name="transactional" value="true"
 */
public class QuitarDocumentoCobrancaService extends DocumentoCobrancaServiceBasic {
	
	public static final String SERVICE_NAME = "QuitarDocumentoCobrancaService";

	public static final String IN_DOCUMENTO = "documento";
	public static final String IN_CONTA = "conta";
	public static final String IN_DATA_MOVIMENTO = "dataMovimento";
	public static final String IN_DATA_COMPENSACAO_OPT = "dataCompensacao";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico QuitarDocumentoCobrancaService");
		
		try{
			log.debug("Preparando os argumentos");
			DocumentoCobranca inDocumento = (DocumentoCobranca) serviceData.getArgumentList().getProperty(IN_DOCUMENTO);
			Conta inConta = (Conta) serviceData.getArgumentList().getProperty(IN_CONTA);
			Calendar inDataMovimento = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_MOVIMENTO);
			Calendar inDataCompensacao = serviceData.getArgumentList().containsProperty(IN_DATA_COMPENSACAO_OPT)?(Calendar) serviceData.getArgumentList().getProperty(IN_DATA_COMPENSACAO_OPT):null;
			
			IGerenciadorDocumentoCobranca gerenciador = this.retrieveGerenciadorPorDocumento(inDocumento);
			if(inDocumento.getId()==IDAO.ENTITY_UNSAVED)
				UtilsCrud.objectUpdate(this.getServiceManager(), inDocumento, serviceData);
			
			log.debug("Executando o método de impressão avulsa do gerenciador");
			gerenciador.quitarDocumento(inDocumento, inConta, inDataMovimento, inDataCompensacao, serviceData);
			
			log.debug("Adicionando a mensagem de sucesso");
			this.addInfoMessage(serviceData, "QUITAR_SUCESSO", inDocumento.toString());
			
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
