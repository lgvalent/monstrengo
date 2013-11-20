package br.com.orionsoft.financeiro.gerenciador.services;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimentoCategoria;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Servi�o que estorna um lancamentoMovimentoservice
 * 
 * <p>
 * <b>Argumento:</b><br>
 * 
 * <p>
 * <b>Procedimento:</b>
 * 
 * @author Lucio Valentin
 * @version 20090522
 * 
 * @spring.bean id="ExcluirMovimentoTransferenciaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class ExcluirMovimentoTransferenciaService extends ServiceBasic {
	public static final String SERVICE_NAME = "ExcluirMovimentoTransferenciaService";

	public static final Integer OUT_LANCAMENTO_MOVIMENTO_ID_1 = 0;
	public static final Integer OUT_LANCAMENTO_MOVIMENTO_ID_2 = 1;

	public static final String IN_LANCAMENTO_MOVIMENTO = "lancamentoMovimento";

	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("::Iniciando a execu��o do servi�o EstornarLancamentoMovimentoService");
		try {
			LancamentoMovimento inLancamentoMovimento1 = (LancamentoMovimento) serviceData.getArgumentList().getProperty(IN_LANCAMENTO_MOVIMENTO);
			LancamentoMovimento inLancamentoMovimento2 = inLancamentoMovimento1.getTransferencia();
			
			/* Verifica se � de transfer�ncia mesmo */
			if(inLancamentoMovimento1.getLancamentoMovimentoCategoria() != LancamentoMovimentoCategoria.TRANSFERIDO){
				throw new ServiceException(MessageList.create(ExcluirMovimentoTransferenciaService.class, "ERRO_CATEGORIA_MOVIMENTO_INCORRETA"));
			}
			
			/* Verifica se � o movimento possui a refer�ncia para outra transfer�ncia */
			if(inLancamentoMovimento2 == null){
				throw new ServiceException(MessageList.create(ExcluirMovimentoTransferenciaService.class, "ERRO_MOVIMENTO_TRANSFERENCIA"));
			}

			UtilsCrud.objectDelete(this.getServiceManager(), inLancamentoMovimento1, serviceData);
			UtilsCrud.objectDelete(this.getServiceManager(), inLancamentoMovimento2, serviceData);
			
			/* Retorna os ID dos objetos exclu�dos para poss�vel registro na auditoria */
			serviceData.getOutputData().add(inLancamentoMovimento1.getId());
			serviceData.getOutputData().add(inLancamentoMovimento2.getId());
		
            /* 
             * Adiconando a mensagem de sucesso 
             */
            this.addInfoMessage(serviceData, "SUCESSO");
		
		} catch (BusinessException e) {
			log.fatal(e.getErrorList());
			/*
			 * O Servi�o n�o precisa adicionar mensagem local. O Manager j�
			 * indica qual srv falhou e os par�metros.
			 */
			throw new ServiceException(e.getErrorList());
		} catch (Exception e) {
			log.fatal(e.getMessage());
			/*
			 * Indica que o servi�o falhou por causa de uma exce��o do
			 * hibernate.
			 */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

}
