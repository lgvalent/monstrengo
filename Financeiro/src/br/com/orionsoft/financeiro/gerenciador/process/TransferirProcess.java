package br.com.orionsoft.financeiro.gerenciador.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.services.TransferirService;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * @author Antonio Alves
 * @since 05/09/2007
 * @version Lucio 20120818: Tornou-se um IRunnable
 *
 */
@ProcessMetadata(label = "Transferir valores entre contas", hint = "Realiza uma movimentação de transferência entre as contas do financeiro", description = "Informe a conta de origem, a conta de destino, o valor a ser transferido entre as contas e uma descrição sobre a transferência.")
public class TransferirProcess extends ProcessBasic implements IRunnableEntityProcess {
	public static final String PROCESS_NAME = "TransferirProcess";

	private IEntity<LancamentoMovimento> lancamentoMovimentoOrigem;
	
	private IEntity<Conta> contaOrigem;
	private IEntity<Conta> contaDestino;
	private Calendar data = CalendarUtils.getCalendar();
	private BigDecimal valor = BigDecimal.ZERO;
	private String descricao;

	public String getProcessName() {
		return PROCESS_NAME; 
	}
	
	@Override
	public void start() throws ProcessException {
		super.start();
		try {
			contaOrigem = UtilsCrud.create(this.getProcessManager().getServiceManager(), Conta.class, null);
			contaDestino = UtilsCrud.create(this.getProcessManager().getServiceManager(), Conta.class, null);
		} catch (BusinessException e) {
			throw new ProcessException(e.getErrorList());
		}
	}

	public boolean runTransferir() {
		super.beforeRun();
		try {
			if (this.contaDestino.getId() == this.contaOrigem.getId())
				throw new BusinessException(MessageList.create(TransferirProcess.class, "CONTAS_IGUAIS"));
			
			if (valor.signum() != 1)
				throw new BusinessException(MessageList.create(TransferirProcess.class, "VALOR_INVALIDO"));
			
			ServiceData sd = new ServiceData(TransferirService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(TransferirService.IN_CONTA_ORIGEN, contaOrigem.getObject());
			sd.getArgumentList().setProperty(TransferirService.IN_CONTA_DESTINO, contaDestino.getObject());
			sd.getArgumentList().setProperty(TransferirService.IN_DATA, data);
			sd.getArgumentList().setProperty(TransferirService.IN_VALOR, this.valor);
			sd.getArgumentList().setProperty(TransferirService.IN_DESCRICAO, descricao);
			this.getProcessManager().getServiceManager().execute(sd);
			LancamentoMovimento lancamentoMovimento = sd.getFirstOutput();
			
			this.lancamentoMovimentoOrigem = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(), LancamentoMovimento.class, lancamentoMovimento.getId(), null);
			
			/* Lucio 20120919: Registra na auditoria */
			UtilsAuditorship.auditCreate(this.getProcessManager().getServiceManager(), getUserSession(), lancamentoMovimentoOrigem, null);
			UtilsAuditorship.auditCreate(this.getProcessManager().getServiceManager(), getUserSession(), (IEntity<?>) lancamentoMovimentoOrigem.getPropertyValue(LancamentoMovimento.TRANSFERENCIA), null);

			this.getMessageList().add(TransferirProcess.class, "SUCESSO", DecimalUtils.formatBigDecimal(this.valor), contaOrigem.toString(), contaDestino.toString());

			return true;
		} catch (ServiceException e) {
    		this.getMessageList().addAll(e.getErrorList());
    		return false;
		} catch (BusinessException e) {
    		this.getMessageList().addAll(e.getErrorList());
    		return false;
		}
	}
	
    /**
     * Constrói uma lista de Contas
     * 
     * @return
     * @throws BusinessException
     */
    public List<SelectItem> getListConta() throws BusinessException {
        IEntityList<Conta> entityList = UtilsCrud.list(this.getProcessManager().getServiceManager(), Conta.class, null);
        List<SelectItem> result = new ArrayList<SelectItem>(entityList.getSize());
        for (IEntity<Conta> entity : entityList) {
            result.add(new SelectItem(entity.getId(), entity.getPropertyValue(Conta.NOME).toString()));
        }
        return result;
    }
    
	public IEntity<LancamentoMovimento> getLancamentoMovimentoOrigem() {
		return lancamentoMovimentoOrigem;
	}

	public IEntity<Conta> getContaOrigem() {
		return contaOrigem;
	}

	public void setContaOrigem(IEntity<Conta> contaOrigem) {
		this.contaOrigem = contaOrigem;
	}

	public IEntity<Conta> getContaDestino() {
		return contaDestino;
	}

	public void setContaDestino(IEntity<Conta> contaDestino) {
		this.contaDestino = contaDestino;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/*
	 * ==========================================================================
	 * IRunnableEntityProcess
	 * ==========================================================================
	 */
	@SuppressWarnings("unchecked")
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();

		boolean result = false;

		try{
			/* Verifica se a entidade é compatível */
			if (entity.getInfo().getType() == Conta.class) {
				this.contaOrigem = (IEntity<Conta>) entity;
	
			} else 
			if (entity.getInfo().getType() == Lancamento.class) {
				
				this.contaOrigem = (IEntity<Conta>) entity.getPropertyValue(Lancamento.CONTA_PREVISTA);
				this.valor = entity.getPropertyValue(Lancamento.VALOR);
			} else 
			if (entity.getInfo().getType() == LancamentoMovimento.class) {
	
				this.contaOrigem = (IEntity<Conta>) entity.getPropertyValue(LancamentoMovimento.CONTA);
				this.valor = entity.getPropertyValue(LancamentoMovimento.VALOR_TOTAL);
			} else {
				this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class,"ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
			}
			
			this.valor = this.valor.abs();
		}catch(BusinessException e){
			this.getMessageList().add(e.getErrorList());
		}
		
		return result;
	}
}