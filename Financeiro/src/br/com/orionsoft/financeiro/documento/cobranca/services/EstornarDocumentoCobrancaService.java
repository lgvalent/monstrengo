package br.com.orionsoft.financeiro.documento.cobranca.services;

import java.util.Calendar;

import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este servi�o estorna um documento lan�ado, obtendo o gerenciador respons�vel 
 * pelo documento junto ao ProvedorDocumentoCobranca e solicitando o estorno do mesmo para 
 * o gerenciador.
 * 
 * <p>Argumentos:</p>
 * IN_DOCUMENTO: (IEntity) Instancia do documento que ser� estornado.
 * IN_DATA_MOVIMENTO: (Calendar) Data do movimento a ser estornado.
 * IN_MOVIMENTO_ID: (long) Id do movimento a ser estornado.
 * 
 * <p><b>Procedimento:</b>
 * <br>Prepara os argumentos.
 * <br>Obtem o gerenciador.
 * <br>Executa o metodo estornarDocumento.
 * <br>Adicionda mensagem de sucesso.
 * <br><b>N�o retorna nada.</b>
 * 
 * @version 20060615
 * 
 * @spring.bean id="EstornarDocumentoCobrancaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
 * @spring.property name="transactional" value="true"
 */
public class EstornarDocumentoCobrancaService extends DocumentoCobrancaServiceBasic {
	
	public static final String SERVICE_NAME = "EstornarDocumentoCobrancaService";

	public static final String IN_DOCUMENTO = "documento";
	public static final String IN_DATA_MOVIMENTO = "dataMovimento";
	public static final String IN_MOVIMENTO_ID = "inMovimentoId";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execu��o do servico EstornarDocumentoCobrancaService");
		
		try{
			log.debug("Preparando os argumentos");
			IEntity inDocumento = (IEntity) serviceData.getArgumentList().getProperty(IN_DOCUMENTO);
			long inMovimentoId = (Long) serviceData.getArgumentList().getProperty(IN_MOVIMENTO_ID);
			Calendar inDataMovimento = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_MOVIMENTO);
			
			IGerenciadorDocumentoCobranca gerenciador = this.retrieveGerenciadorPorDocumento((DocumentoCobranca)inDocumento.getObject());
			
			log.debug("Executando o m�todo de impress�o avulsa do gerenciador");
			gerenciador.estornarDocumento(inDocumento, inMovimentoId, inDataMovimento, serviceData);
			
			log.debug("Adicionando a mensagem de sucesso");
			this.addInfoMessage(serviceData, "ESTORNO_SUCESSO", inDocumento.toString());
			
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
