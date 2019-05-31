package br.com.orionsoft.financeiro.gerenciador.process;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ClassUtils;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.services.ListarLancamentoService.Situacao;
import br.com.orionsoft.financeiro.gerenciador.services.ListarPosicaoContratoService;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.process.ProcessParamEntity;
import br.com.orionsoft.monstrengo.core.process.ProcessParamEntityList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.documents.entities.ModelDocumentEntity;
import br.com.orionsoft.monstrengo.crud.entity.EntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * 
 * Primeiro preencher o cpfCnpj:setCpfCnpj(String)
 * Depois executar o runValidarCpfCnpj()
 * e Depois executar o runListar();
 * @author Antonio Alves
 * @since 14/09/2007
 */
@ProcessMetadata(label="Listar posição do Cliente", hint="Lista os todos os lançamentos de um cliente, indicando a atual situação de cada lançamento", description="Possibilita verificar todos os lançamentos lançados para um determinado cliente, filtrando por perídodo de lançamento e por situação (Pendente, Vencido ou Quitado). A partir deste processo é possível acessar os lançamentos para realizar quitação, baixa ou estorno, e ainda, imprimir 2ª via de documentos e recibos.")
public class ListarPosicaoContratoProcess extends ProcessBasic implements IRunnableEntityProcess{
	public static final String PROCESS_NAME = "ListarPosicaoContratoProcess";
	
	private ProcessParamEntity<Pessoa> paramPessoa = new ProcessParamEntity<Pessoa>(Pessoa.class, false, this);
	private Boolean incluirFiliais = false;

	private ProcessParamEntityList<ItemCusto> paramItemCusto = new ProcessParamEntityList<ItemCusto>(ItemCusto.class, false, this);
	private Boolean itemCustoListNot = false;
	
	private ProcessParamEntity<ModelDocumentEntity> paramModelDocument = new ProcessParamEntity<ModelDocumentEntity>(ModelDocumentEntity.class, false, this);

	private EntityList<Lancamento> lancamentos = null;
	
//	private String cpfCnpj;
	private Calendar dataInicial;
	private Calendar dataFinal;
	private int situacao;
	private IEntityList<Conta> contas;
	
	@Override
	public void start() throws ProcessException {
		super.start();
		this.situacao = Situacao.TODOS.ordinal();
		this.dataInicial = CalendarUtils.getCalendar();
		this.dataFinal = CalendarUtils.getCalendar();
		/* Altera a data inicial para o primeiro dia do mês e a data final para o ultimo dia do mês */
		this.dataInicial.set(Calendar.DATE, this.dataInicial.getActualMinimum(Calendar.DATE));
		this.dataFinal.set(Calendar.DATE, this.dataFinal.getActualMaximum(Calendar.DATE));
		try {
			this.contas = this.getListConta();
		} catch (BusinessException e) {
			super.getMessageList().add(e.getErrorList());
		}
		
		this.paramModelDocument.setStaticHqlWhereFilter("("+ModelDocumentEntity.APPLICATION_ENTITY + " is null)");
	}
	
	public List<SelectItem> getListSituacao() {
        List<SelectItem> result = new ArrayList<SelectItem>(4);
        result.add(new SelectItem(Situacao.TODOS.ordinal(), Situacao.TODOS.getNome()));
        result.add(new SelectItem(Situacao.PENDENTE.ordinal(), Situacao.PENDENTE.getNome()));
        result.add(new SelectItem(Situacao.VENCIDO.ordinal(), Situacao.VENCIDO.getNome()));
        result.add(new SelectItem(Situacao.QUITADO.ordinal(), Situacao.QUITADO.getNome()));
        return result;
	}
	
	private IEntityList<Conta> getListConta() throws BusinessException {
		return UtilsCrud.list(this.getProcessManager().getServiceManager(), Conta.class, " entity.id in (SELECT c.id FROM Conta c inner join c.applicationUsers as user where user.id=" + this.getUserSession().getUser().getId() + ")  order by entity.nome", null);
	}
	
	/**
	 * @version Lucio 20110404: Capacidade de reconhecer cpfCpnj incompleto. Para listar matriz e filiais
	 * @return
	 */
//	public boolean runValidarCfpCnpj() {
//		super.beforeRun();
//		try {
//			if(!StringUtils.isBlank(cpfCnpj)){
//				IEntityList<Pessoa> list = UtilsCrud.list(this.getProcessManager().getServiceManager(), Pessoa.class, "entity." + Pessoa.DOCUMENTO + " like '"+cpfCnpj+"%'", null);
//				if (list.size() > 0) {
//					this.pessoa.setValue(list.getFirst());
//					return true;
//				}
//			}
//				
//			/* Retorna uma pessoa VAZIA */
//			this.pessoa.setValue(null);
//			
//			return true;
//		} catch (BusinessException e) {
//			/* Armazenando a lista de erros */
//			this.getMessageList().addAll(e.getErrorList());
//			return false;
//		}
//	}

	public boolean runListar() {
		super.beforeRun();
		try {
			ServiceData sd = new ServiceData(ListarPosicaoContratoService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_CONTA_LIST_OPT, contas);
			
//			/* Somente se o cpf estiver preenchido observa ele e não a pessoa */
//			if (StringUtils.isNotBlank(this.cpfCnpj))
//				/*Lucio 20100603: Remove possíveis pontos e caracteres de máscara */
//				sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_DOCUMENTO_OPT, br.com.orionsoft.monstrengo.core.util.StringUtils.removeNonNumeric(this.cpfCnpj));
//			else
			if (!this.paramPessoa.isNull() && (this.paramPessoa.getValue().getId()!=IDAO.ENTITY_UNSAVED)){
				if(this.incluirFiliais)
					sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_DOCUMENTO_OPT, this.paramPessoa.getValue().getObject().getDocumento().substring(0, 9));
				else
					sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_PESSOA_OPT, this.paramPessoa.getValue().getObject());
			}
			sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_DATA_INICIAL_OPT, dataInicial);
			sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_DATA_FINAL_OPT, dataFinal);
			sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_SITUACAO_OPT, Situacao.values()[situacao]);
			sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_ITEM_CUSTO_IDS_OPT, this.paramItemCusto.getValue().getIds());
			sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_ITEM_CUSTO_LIST_NOT_OPT, itemCustoListNot);
			sd.getArgumentList().setProperty(ListarPosicaoContratoService.IN_APPLICATION_USER_OPT, this.getUserSession().getUser().getObject());
			this.getProcessManager().getServiceManager().execute(sd);
			
			this.lancamentos = sd.getFirstOutput();

			/* Pegas as mensagens do serviço */
            this.getMessageList().add(sd.getMessageList());
            
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

	public Calendar getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Calendar dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Calendar getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Calendar dataFinal) {
		this.dataFinal = dataFinal;
	}

	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}

	public EntityList<Lancamento> getLancamentos() {
		return lancamentos;
	}

	public ProcessParamEntity<Pessoa> getParamPessoa() {
		return paramPessoa;
	}
	
	public ProcessParamEntityList<ItemCusto> getParamItemCusto() {
		return paramItemCusto;
	}

	public ProcessParamEntity<ModelDocumentEntity> getParamModelDocument() {
		return paramModelDocument;
	}

	/*==============================================================================
	 * IRunnableEntityProcess	
	 *==============================================================================*/
	@SuppressWarnings("unchecked")
	public boolean runWithEntity(IEntity<?> entity) {
		super.beforeRun();

		boolean result = false;

		/* Verifica se a entidade é compatível */
		/* Verifica se a entidade passada eh um DocumentoCobranca ou pertence eh descendente */
		if (ClassUtils.isAssignable(entity.getInfo().getType(), Contrato.class)) {
			try {
				Contrato oContrato = (Contrato) entity.getObject();
//				this.cpfCnpj = oContrato.getPessoa().getDocumento();
				this.paramPessoa.setValue((IEntity<Pessoa>) entity.getPropertyValue(Contrato.PESSOA));

				/* Alguns dados poderao ser inicializados aqui */
				this.situacao = Situacao.TODOS.ordinal();

				/* Executa a listagem com os parâmetros definidos acima */
				result = this.runListar();
			} catch (BusinessException e) {
				this.getMessageList().addAll(e.getErrorList());
			}
		}else
		if (ClassUtils.isAssignable(entity.getInfo().getType(), Pessoa.class)) {
			Pessoa oPessoa = (Pessoa) entity.getObject();
//			this.cpfCnpj = oPessoa.getDocumento();
			this.paramPessoa.setValue((IEntity<Pessoa>) entity);

			/* Alguns dados poderao ser inicializados aqui */
			this.situacao = Situacao.TODOS.ordinal();

			/* Executa a listagem com os parâmetros definidos acima */
			result = this.runListar();
		}else
		{
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}

		return result;
	}

	public Boolean getItemCustoListNot() {
		return itemCustoListNot;
	}

	public void setItemCustoListNot(Boolean itemCustoListNot) {
		this.itemCustoListNot = itemCustoListNot;
	}

	public Boolean getIncluirFiliais() {
		return incluirFiliais;
	}

	public void setIncluirFiliais(Boolean incluirFiliais) {
		this.incluirFiliais = incluirFiliais;
	}

	public IEntityList<Conta> getContas() {
		return contas;
	}

	public void setContas(IEntityList<Conta> contaList) {
		this.contas = contaList;
	}
}
