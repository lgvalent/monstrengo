package br.com.orionsoft.financeiro.gerenciador.services;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;

/**
 * Este serviço quita vários Lancamentos obtendo os valores de Juros e multa diretamente
 * de movimentos temporários fornecidos.
 * Os movimentos já vêm vinculados aos lançamentos que eles desejam quitar.
 * Inclusive com a conta e demais valores prontos.
 * Inclusive o DocumentoPagamento que deve ser utilizado.  
 * <p>
 * <b>Parâmetros:</b><br>
 * IN_MOVIMENTOS (IEntity) o movimentos que serão quitados.<br>
 * IN_SUBSTITUIR_VALOR_OPT (Boolean) indica se o valor que está no Movimento deverá SOBREPOR o valor do lançamento.<br>
 * <p>
 * <b>Retorno:</b><br>
 * IEntityList Lista dos movimentos REAIS gerados para os lançamentos
 * 
 * @author Lucio
 * @version 20110328
 * 
 * @spring.bean id="QuitarLancamentosService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 * @spring.property name="transactional" value="true"
 */
public class QuitarLancamentosService extends ServiceBasic {
    public static final String SERVICE_NAME = "QuitarLancamentosService";
    
    public static final String IN_MOVIMENTOS = "movimentos";
    public static final String IN_SUBSTITUIR_VALOR_OPT = "substituirValor";
    
    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException {
    	log.debug("::Iniciando a execução do serviço QuitarLancamentoService");

        try {
            /* Lê os parâmetros obrigatórios. */
            IEntityList<LancamentoMovimento> inMovimentos = (IEntityList<LancamentoMovimento>) serviceData.getArgumentList().getProperty(IN_MOVIMENTOS);

            boolean inSubstituirValor = serviceData.getArgumentList().containsProperty(IN_SUBSTITUIR_VALOR_OPT)?(Boolean) serviceData.getArgumentList().getProperty(IN_SUBSTITUIR_VALOR_OPT):false;
            
            /* Prepara a lista para armazenar os resultados do QuitarLancamentoService */
            IEntityList<LancamentoMovimento> outMovimentos = this.getServiceManager().getEntityManager().getEntityList(null, LancamentoMovimento.class);
            
            /* Para cada movimento chama o QuitarLancamentoService */
            for(IEntity<LancamentoMovimento> mov: inMovimentos){
            	if(mov.isSelected()){
            		LancamentoMovimento oMov = mov.getObject();
            		ServiceData sd = new ServiceData(QuitarLancamentoService.SERVICE_NAME, serviceData);
            		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_CONTA, oMov.getConta());
            		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_DATA, oMov.getData());
            		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_DATA_COMPENSACAO_OPT, oMov.getDataCompensacao());
            		if (oMov.getDocumentoPagamento() != null)
            			sd.getArgumentList().setProperty(QuitarLancamentoService.IN_DOCUMENTO_PAGAMENTO_OPT, oMov.getDocumentoPagamento());
            		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_LANCAMENTO, oMov.getLancamento());
            		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_VALOR, oMov.getValor());
            		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_JUROS_OPT, oMov.getJuros());
            		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_MULTA_OPT, oMov.getMulta());
            		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_DESCONTO_OPT, oMov.getDesconto());
            		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_ACRESCIMO_OPT, oMov.getAcrescimo());
            		sd.getArgumentList().setProperty(QuitarLancamentoService.IN_SUBSTITUIR_VALOR_OPT, inSubstituirValor);
            		this.getServiceManager().execute(sd);
            		IEntity<LancamentoMovimento> movimentoInserido = this.getServiceManager().getEntityManager().getEntity(sd.getFirstOutput());
            		outMovimentos.add(movimentoInserido);
            	}
            	
            	serviceData.getOutputData().add(outMovimentos);
            }
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