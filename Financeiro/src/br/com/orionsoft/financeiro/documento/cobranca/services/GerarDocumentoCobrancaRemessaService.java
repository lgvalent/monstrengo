package br.com.orionsoft.financeiro.documento.cobranca.services;

import java.io.File;
import java.util.Calendar;

import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este serviço é utilizado para gerar a remessa de um documento.
 * Os dados da remessa são retornados como um OutputStream.
 * 
 * <p>Argumentos:</p>
 * IN_CEDENTE_ID: (Long) id que indica para qual cedente será gerado a remessa.
 * IN_INICIO_PERIODO: (Calendar) Data inicial do periodo.
 * IN_FINAL_PERIODO: (Calendar) Data final do periodo.
 * IN_QUANTIDADE_DIAS_PROTESTO_OPT: (Integer) Indica os dias para protesto em cartório dos títulos da remessa
 * 
 * @version 20060628
 * 
 * @spring.bean id="GerarDocumentoCobrancaRemessaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorDocumentoCobranca" ref="ProvedorDocumentoCobranca"
 * @spring.property name="transactional" value="true"
 */
public class GerarDocumentoCobrancaRemessaService extends DocumentoCobrancaServiceBasic {
	
	public static final String SERVICE_NAME = "GerarDocumentoCobrancaRemessaService";

	public static final String IN_CONVENIO_ID = "convenioId";
	public static final String IN_INICIO_PERIODO = "inicioPeriodo";
	public static final String IN_FINAL_PERIODO = "finalPeriodo";
	public static final String IN_QUANTIDADE_DIAS_PROTESTO_OPT = "quantidadeDiasProtesto";

	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico GerarDocumentoCobrancaRemessaService");
		
		try{
			log.debug("Preparando os argumentos");
			Long inConvenioId = (Long) serviceData.getArgumentList().getProperty(IN_CONVENIO_ID);
			Calendar inInicioPeriodo = (Calendar) serviceData.getArgumentList().getProperty(IN_INICIO_PERIODO);
			Calendar inFinalPeriodo = (Calendar) serviceData.getArgumentList().getProperty(IN_FINAL_PERIODO);
			Integer inQuantidadeDiasProtesto = serviceData.getArgumentList().containsProperty(IN_QUANTIDADE_DIAS_PROTESTO_OPT) ? (Integer)serviceData.getArgumentList().getProperty(IN_QUANTIDADE_DIAS_PROTESTO_OPT) : null;

			IEntity convenio = UtilsCrud.retrieve(this.getServiceManager(), ConvenioCobranca.class, inConvenioId, serviceData);
			
			IGerenciadorDocumentoCobranca gerenciador = this.retrieveGerenciadorPorConvenio((ConvenioCobranca)convenio.getObject());
			
			log.debug("Executando o método de remessa do gerenciador");
			
			File result = gerenciador.gerarRemessa(convenio, inInicioPeriodo, inFinalPeriodo, inQuantidadeDiasProtesto, serviceData);
			
			serviceData.getOutputData().add(result);

			log.debug("Adicionando a mensagem de sucesso");
			this.addInfoMessage(serviceData, "GERAR_REMESSA_SUCESSO", gerenciador.getNome());
			
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
