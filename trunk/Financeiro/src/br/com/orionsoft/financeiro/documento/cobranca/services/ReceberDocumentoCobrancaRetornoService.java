package br.com.orionsoft.financeiro.documento.cobranca.services;

import java.io.InputStream;
import java.util.List;

import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoResultado;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoResultadoSumario;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este serviço é utilizado para receber dados de retorno de um documento.
 * 
 * <p>Argumentos:</p>
 * IN_CONVENIO_COBRANCA_ID: (Long) id que indica para qual convenio será gerado a remessa.
 * IN_DADOS_RETORNO: (InputStream) Dados que serão lidos pelo retorno do gerenciador de documentos.
 * <p>
 * <b>Procedimento:</b><br>
 * <b>Retorna uma classe do tipo DocumentoRetornoResultadoSumario que classifica os totais
 * pelos tipos de ocorrências.</b>
 * 
 * @version 20061006
 * 
 * @spring.bean id="ReceberDocumentoCobrancaRetornoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
 * @spring.property name="transactional" value="true"
 */
public class ReceberDocumentoCobrancaRetornoService extends DocumentoCobrancaServiceBasic {
	
	public static final String SERVICE_NAME = "ReceberDocumentoCobrancaRetornoService";

	public static final String IN_CONVENIO_COBRANCA_ID = "inConvenioCobrancaId";
	public static final String IN_INPUT_STREAM = "inInputStream";

	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico ReceberDocumentoCobrancaRetornoService");
		
		try{
			log.debug("Preparando os argumentos");
			Long inConvenioCobrancaId = (Long) serviceData.getArgumentList().getProperty(IN_CONVENIO_COBRANCA_ID);
			InputStream inInputStream = (InputStream) serviceData.getArgumentList().getProperty(IN_INPUT_STREAM);
			
			IEntity convenioCobranca = UtilsCrud.retrieve(this.getServiceManager(), ConvenioCobranca.class, inConvenioCobrancaId, serviceData);
			
			IGerenciadorDocumentoCobranca gerenciador = this.retrieveGerenciadorPorConvenio((ConvenioCobranca) convenioCobranca.getObject());
			
			log.debug("Executando o método de retorno do gerenciador");
			List<DocumentoRetornoResultado> result = gerenciador.receberRetorno(convenioCobranca, inInputStream, serviceData);
			
			log.debug("Adicionando a mensagem de sucesso");
			this.addInfoMessage(serviceData, "RECEBER_RETORNO_SUCESSO", gerenciador.getNome());
			
			serviceData.getOutputData().add(new DocumentoRetornoResultadoSumario(result));
			
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
