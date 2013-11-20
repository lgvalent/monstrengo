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
 * Este processo realiza a quitação de um lancamento. Ele localiza o documento ligado
 * ao lancamento para definir um documento de quitação, se não tiver nenhum documento
 * vinculado ele cria um documento de quitação
 * 
 * <p>
 * <b>Procedimentos:</b><br>
 * Primeiramente deve-se definir o id do lancamento a ser quitaqo:
 * <i>setLancamentoId(long)</i><br>
 * Definir o identificador da conta onde será lançado movimento de quitação:
 * <i>setContaId(long)</i><br>
 * Definir a dataQuitacao de quitação : <i>setDataQuitacao(Calendar)</i><br>
 * Definir o valor da quitação: <i>setValor(BigDecimal)</i><br>
 * <br>
 * Verificar se a entidade pode ser criada: <i>boolean mayCreate()</i> <br>
 * Obter a entidade por <i>(IEntity) retrieveEntity()</i>.
 * <li>Realizar edições pela interface com o usuário. <br>
 * Gravar as alterações por <i>runUpdate()</i>.
 * 
 * @author JUAN GARAY III
 * @version 20070725
 */
@ProcessMetadata(label="Estornar movimento", hint="Estorna movimento de baixa ou quitação", description="O estorno de um movimento desfaz a operação realizada sobre o lançamento. Desta forma, uma lançamento quitado ou cancelado voltará a sua situação de pendentes. Todos movimentos ficam registrados para manter o histórico.")
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

            /* Pegas as mensagens do serviço */
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
