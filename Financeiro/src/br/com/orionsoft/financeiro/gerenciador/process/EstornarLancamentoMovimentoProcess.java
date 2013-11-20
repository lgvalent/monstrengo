package br.com.orionsoft.financeiro.gerenciador.process;

import java.util.Calendar;

import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoMovimento;
import br.com.orionsoft.financeiro.gerenciador.services.EstornarLancamentoMovimentoService;
import br.com.orionsoft.monstrengo.auditorship.services.UtilsAuditorship;
import br.com.orionsoft.monstrengo.auditorship.support.EntityAuditValue;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Este processo realiza a quita��o de um lancamento. Ele localiza o documento ligado
 * ao lancamento para definir um documento de quita��o, se n�o tiver nenhum documento
 * vinculado ele cria um documento de quita��o
 * 
 * <p>
 * <b>Procedimentos:</b><br>
 * Primeiramente deve-se definir o id do lancamento a ser quitaqo:
 * <i>setLancamentoId(long)</i><br>
 * Definir o identificador da conta onde ser� lan�ado movimento de quita��o:
 * <i>setContaId(long)</i><br>
 * Definir a dataQuitacao de quita��o : <i>setDataQuitacao(Calendar)</i><br>
 * Definir o valor da quita��o: <i>setValor(BigDecimal)</i><br>
 * <br>
 * Verificar se a entidade pode ser criada: <i>boolean mayCreate()</i> <br>
 * Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * <li>Realizar edi��es pela interface com o usu�rio. <br>
 * Gravar as altera��es por <i>runUpdate()</i>.
 * 
 * @author JUAN GARAY III
 * @version 20070725
 */
@ProcessMetadata(label="Estornar movimento", hint="Estorna movimento de baixa ou quita��o", description="O estorno de um movimento desfaz a opera��o realizada sobre o lan�amento. Desta forma, uma lan�amento quitado ou cancelado voltar� a sua situa��o de pendentes. Todos movimentos ficam registrados para manter o hist�rico.")
public class EstornarLancamentoMovimentoProcess extends ProcessBasic implements IRunnableEntityProcess {
	public static final String PROCESS_NAME="EstornarLancamentoMovimentoProcess";
	
	private IEntity<LancamentoMovimento> lancamentoMovimento = null;
	private Calendar data = CalendarUtils.getCalendar();
	private String descricao = "";
	
	public boolean runEstornar() {
        try {
        	EntityAuditValue entityAuditValue = new EntityAuditValue(this.lancamentoMovimento);
        	
            ServiceData sd = new ServiceData(EstornarLancamentoMovimentoService.SERVICE_NAME, null);
            sd.getArgumentList().setProperty(EstornarLancamentoMovimentoService.IN_LANCAMENTO_MOVIMENTO, this.lancamentoMovimento.getObject());
            sd.getArgumentList().setProperty(EstornarLancamentoMovimentoService.IN_DATA, this.data);
            sd.getArgumentList().setProperty(EstornarLancamentoMovimentoService.IN_DESCRICAO, this.descricao);
            this.getProcessManager().getServiceManager().execute(sd);

            /* Pegas as mensagens do servi�o */
            this.getMessageList().add(sd.getMessageList());
            
			if (sd.getMessageList().isTransactionSuccess()) {
				IEntity<LancamentoMovimento> lancamentoMovimentoEntity = UtilsCrud.retrieve(this.getProcessManager().getServiceManager(), LancamentoMovimento.class, sd.<LancamentoMovimento>getFirstOutput().getId(), null);
				UtilsAuditorship.auditCreate(this.getProcessManager().getServiceManager(), this.getUserSession(), lancamentoMovimentoEntity, null);
				UtilsAuditorship.auditUpdate(this.getProcessManager().getServiceManager(), this.getUserSession(), entityAuditValue, null);
			}
			
            return true;

        } catch (BusinessException e) {
            /* Armazenando a lista de erros */
            this.getMessageList().addAll(e.getErrorList());
            return false;
        }
    }
	
	public String getProcessName() {
		return PROCESS_NAME;
	}

	public IEntity<LancamentoMovimento> getLancamentoMovimento() {
		return lancamentoMovimento;
	}

	public void setLancamentoMovimento(IEntity<LancamentoMovimento> lancamentoMovimento) {
		this.lancamentoMovimento = lancamentoMovimento;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@SuppressWarnings("unchecked")
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();
		boolean result = false;
		if (entity.getInfo().getType() == LancamentoMovimento.class) {
			this.lancamentoMovimento = (IEntity<LancamentoMovimento>) entity;
			result = true;
		} else {
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}
		return result;
	}

	
}
