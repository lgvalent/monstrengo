package br.com.orionsoft.financeiro.gerenciador.services;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoSituacao;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.Situacao;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.EntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.QueryService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.UserSession;

/**
 * Este serviço cancela TODOS os lançamentos em aberto de um determinado contrato.  
 * <p>
 * <b>Parâmetros:</b><br>
 * <b>Parâmetros:</b><br>
 * IN_CONTRATO (IEntity<Contrato>) contrato a ser cancelado.<br>
 * IN_USER_SESSION (UserSession) credenciais do usuário que efetuará a ação.<br>
 * IN_DATA_CANCELAMENTO (Calendar) data de cancelamento.<br>
 * IN_DESCRICAO (String) descrição do motivo do cancelamento.<br>
 * <p>
 * <b>Retorno:</b><br>
 * List<IEntity<LancamentoMovimento>> Lista de movimentos gerados para o cancelamento
 * 
 * @author Lucio
 * @version 20150219
 */
public class CancelarLancamentosService extends ServiceBasic {
    public static final String SERVICE_NAME = "CancelarLancamentosService";
    
    public static final String IN_CONTRATO = "contrato";
    public static final String IN_USER_SESSION = "userSession";
    public static final String IN_DATA_CANCELAMENTO = "data";
    public static final String IN_DESCRICAO = "descricao";

    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException {
    	log.debug("::Iniciando a execução do serviço CancelarLancamentosService");

        try {
            log.debug("Preparando os argumentos");
            
            /* 
             * Lê os parâmetros obrigatórios. 
             */
            IEntity<Contrato> inContrato = (IEntity<Contrato>) serviceData.getArgumentList().getProperty(IN_CONTRATO);
            UserSession inUserSession = (UserSession) serviceData.getArgumentList().getProperty(IN_USER_SESSION);
            Calendar inDataCancelamento = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_CANCELAMENTO);
            String inDescricao = (String) serviceData.getArgumentList().getProperty(IN_DESCRICAO);

            /*
             * Lista todos os lançamentos PENDENTES.
             */
            ServiceData sd = new ServiceData(ListarPosicaoContratoService.SERVICE_NAME, serviceData);
            sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_SITUACAO_OPT, Situacao.PENDENTE);
            sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_CONTRATO_OPT, inContrato.getObject());
            this.getServiceManager().execute(sd);
			EntityList<Lancamento> lancamentos = sd.getFirstOutput();
			
            /*
             * Cancela todos os lançamentos.
             */
			for(Lancamento lan: lancamentos.getObjectList()){
				ServiceData sds = new ServiceData(CancelarLancamentoService.SERVICE_NAME, serviceData);
				sds.getArgumentList().setProperty(CancelarLancamentoService.IN_DATA, inDataCancelamento);
				sds.getArgumentList().setProperty(CancelarLancamentoService.IN_DESCRICAO, inDescricao);
				sds.getArgumentList().setProperty(CancelarLancamentoService.IN_LANCAMENTO, lan);
				this.getServiceManager().execute(sds);

				if(sds.getMessageList().isTransactionSuccess()){
					LancamentoMovimento lanMov = sds.getFirstOutput();
					IEntity<LancamentoMovimento> entityLancamentoMovimento = this.getServiceManager().getEntityManager().getEntity(lanMov);

					UtilsAuditorship.auditCreate(this.getServiceManager(), inUserSession,  entityLancamentoMovimento, serviceData);
					serviceData.getOutputData().add(lanMov);
				}else{
					this.addMessages(serviceData, sds.getMessageList());
				}
			}

            /* 
             * Adiconando a mensagem de sucesso 
             */
            this.addInfoMessage(serviceData, "SUCESSO");
		
            log.debug("::Fim da execução do serviço");
        } catch (BusinessException e) {
            log.fatal(e.getErrorList());
            throw new ServiceException(e.getErrorList());
        } catch (Exception e) {
            log.fatal(e.getMessage());
            throw new ServiceException(MessageList.createSingleInternalError(e));
        }
    }

}
