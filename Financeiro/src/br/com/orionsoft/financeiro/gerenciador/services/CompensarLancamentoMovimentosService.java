package br.com.orionsoft.financeiro.gerenciador.services;

import java.util.Calendar;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este servi�o marca a data de compensa��o em v�rios LancamentoMovimento
 * <p>
 * <b>Par�metros:</b><br>
 * IN_MOVIMENTOS (IEntity) o movimentos que ser�o compensados.<br>
 * IN_DATA_COMPENSACAO (Calendar)<br>
 * <p>
 * 
 * @author Lucio
 * @version 20131008
 */
public class CompensarLancamentoMovimentosService extends ServiceBasic {
    public static final String SERVICE_NAME = "CompensarLancamentoMovimentosService";
    
    public static final String IN_MOVIMENTOS = "movimentos";
    public static final String IN_DATA_COMPENSACAO = "dataCompensacao";
    
    public String getServiceName() {
        return SERVICE_NAME;
    }

    public void execute(ServiceData serviceData) throws ServiceException {
    	log.debug("::Iniciando a execu��o do servi�o CompensarLancamentoMovimentosService");

        try {
            /* L� os par�metros obrigat�rios. */
            @SuppressWarnings("unchecked")
			IEntityList<LancamentoMovimento> inMovimentos = (IEntityList<LancamentoMovimento>) serviceData.getArgumentList().getProperty(IN_MOVIMENTOS);
            Calendar inDataCompensacao = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_COMPENSACAO);
            
            /* Para cada movimento chama o QuitarLancamentoService */
            for(IEntity<LancamentoMovimento> mov: inMovimentos){
            	if(mov.isSelected()){
            		LancamentoMovimento oMov = mov.getObject();
            		if(oMov.isCompensado()){
            			serviceData.getMessageList().add(BusinessMessage.TYPE_INFO, CompensarLancamentoMovimentosService.class, "JA_COMPENSADO", mov);
            		}else{
            			oMov.setDataCompensacao(inDataCompensacao);
            			UtilsCrud.update(this.getServiceManager(), mov, serviceData);
            			serviceData.getMessageList().add(BusinessMessage.TYPE_INFO, CompensarLancamentoMovimentosService.class, "COMPENSACAO_OK", mov);
            		}
            	}
            }
            log.debug("::Fim da execu��o do servi�o");
        } catch (BusinessException e) {
            log.fatal(e.getErrorList());
            throw new ServiceException(e.getErrorList());
        } catch (Exception e) {
            log.fatal(e.getMessage());
            throw new ServiceException(MessageList.createSingleInternalError(e));
        }
    }

}