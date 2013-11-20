package br.com.orionsoft.financeiro.documento.cobranca.titulo.services;

import java.util.Calendar;
import java.io.File;

import br.com.orionsoft.financeiro.documento.cobranca.titulo.IGerenciadorBanco;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Este serviço é utilizado para gerar a remessa de determinado gerenciador de bancos
 * 
 * <p>Argumentos:</p>
 * IN_CEDENTE: (IEntity) entidade que indica para qual cedente será gerado o arquivo de remessa.
 * IN_INICIO_PERIODO: (Calendar) Data de início de um período 
 * IN_FINAL_PERIODO: (Calendar) Data final do período
 * IN_QUANTIDADE_DIAS_PROTESTO_OPT: (Integer) Indica os dias para protesto em cartório dos títulos da remessa
 * 
 * @version 20060914
 * 
 * @spring.bean id="GerarBancoRemessaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="provedorBanco" ref="ProvedorBanco"
 * @spring.property name="transactional" value="true"
 */
public class GerarBancoRemessaService extends BancoServiceBasic {
	
	public static final String SERVICE_NAME = "GerarBancoRemessaService";

	public static final String IN_CEDENTE = "cedente";
	public static final String IN_INICIO_PERIODO = "inicioPeriodo";
	public static final String IN_FINAL_PERIODO = "finalPeriodo";
	public static final String IN_QUANTIDADE_DIAS_PROTESTO_OPT = "quantidadeDiasProtesto";
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando execução do servico GerarBancoRemessaService");
		
		try{
			log.debug("Preparando os argumentos");
			IEntity inCedente = (IEntity)serviceData.getArgumentList().getProperty(IN_CEDENTE);
			Calendar inInicioPeriodo = (Calendar) serviceData.getArgumentList().getProperty(IN_INICIO_PERIODO);
			Calendar inFinalPeriodo = (Calendar) serviceData.getArgumentList().getProperty(IN_FINAL_PERIODO);
			Integer inQuantidadeDiasProtesto = serviceData.getArgumentList().containsProperty(IN_QUANTIDADE_DIAS_PROTESTO_OPT) ? (Integer)serviceData.getArgumentList().getProperty(IN_QUANTIDADE_DIAS_PROTESTO_OPT) : null;
			
			IGerenciadorBanco gerenciadorBanco = this.retrieveGerenciadorBanco(inCedente);
			
			log.debug("Executando o método de remessa avulsa do gerenciador");
			File result = gerenciadorBanco.gerarRemessa(inCedente, inInicioPeriodo, inFinalPeriodo, inQuantidadeDiasProtesto, serviceData);
			
			serviceData.getOutputData().add(result);
			
			log.debug("Adicionando a mensagem de sucesso");
			this.addInfoMessage(serviceData, "GERAR_REMESSA_SUCESSO", gerenciadorBanco.getNome());
			
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
