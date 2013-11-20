/*
 * Created on 31/03/2006 by antonio
 */
package br.com.orionsoft.financeiro.gerenciador.process;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ClassUtils;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.ContratoCategoria;
import br.com.orionsoft.basic.entities.endereco.Municipio;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.basic.entities.pessoa.Representante;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.services.RelatorioRecebimentoService;
import br.com.orionsoft.monstrengo.core.annotations.ProcessMetadata;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.process.IRunnableEntityProcess;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Este processo lista as pendências dos contratos para cobrança.
 * 
 * <p><b>Procedimentos:</b>
 * 
 * @author Antônio 20070424
 * @version 20070424
 *
 * @spring.bean id="RelatorioRecebimentoProcess" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */
@ProcessMetadata(label="Relatório de recebimento", hint="Gera um relatório com os lançamentos recebimento, seu vencimento, sua data de recebimento e seu valor", description="Gera um relatório com os lançamentos recebidos no período, seu vencimento, sua data de recebimento e seu valor.")
public class RelatorioRecebimentoProcess extends ProcessBasic implements IRunnableEntityProcess{
    public static final String PROCESS_NAME = "RelatorioRecebimentoProcess";
    
	private String itemCustoIdList = null;
	private Boolean itemCustoNot = false;
	private String escritorioContabilIdList = null;
	private Long categoriaContratoId = IDAO.ENTITY_UNSAVED;
	private Long municipioId = IDAO.ENTITY_UNSAVED;
	private Boolean notMunicipio = false;
	private Long contaId = IDAO.ENTITY_UNSAVED;
	private Long contratoRepresentanteId = IDAO.ENTITY_UNSAVED;
	private int tipoContrato = 0;
//	private Calendar dataLancamentoInicial = CalendarUtils.getCalendar();
//	private Calendar dataLancamentoFinal = CalendarUtils.getCalendar();
	private Calendar dataVencimentoInicial = CalendarUtils.getCalendar();
	private Calendar dataVencimentoFinal = CalendarUtils.getCalendar();
	private Calendar dataRecebimentoInicial = CalendarUtils.getCalendar();
	private Calendar dataRecebimentoFinal = CalendarUtils.getCalendar();
	private Integer quantidadeItensInicial = null;
	private Integer quantidadeItensFinal = null;
	private String cpfCnpj = null;
	private OutputStream outputStream = null;
    
	@Override
	public void start() throws ProcessException {
		super.start();
//		dataLancamentoInicial.set(Calendar.DATE, dataLancamentoInicial.getActualMinimum(Calendar.DATE));
		dataVencimentoInicial.set(Calendar.DATE, dataVencimentoInicial.getActualMinimum(Calendar.DATE));
		dataRecebimentoInicial.set(Calendar.DATE, dataRecebimentoInicial.getActualMinimum(Calendar.DATE));
	}
	
	public void execute() {
		log.debug("Iniciando RelatorioRecebimentoProcess...");
		try {
			ServiceData sd = new ServiceData(RelatorioRecebimentoService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_ITEM_CUSTO_ID_LIST, this.itemCustoIdList);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_ITEM_CUSTO_NOT, this.itemCustoNot);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_ESCRITORIO_CONTABIL_ID_LIST, this.escritorioContabilIdList);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_CATEGORIA_CONTRATO_ID, this.categoriaContratoId);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_TIPO_CONTRATO, this.tipoContrato);
//			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_DATA_LANCAMENTO_INICIAL, this.dataLancamentoInicial);
//			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_DATA_LANCAMENTO_FINAL, this.dataLancamentoFinal);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_DATA_VENCIMENTO_INICIAL, this.dataVencimentoInicial);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_DATA_VENCIMENTO_FINAL, this.dataVencimentoFinal);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_DATA_RECEBIMENTO_INICIAL, this.dataRecebimentoInicial);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_DATA_RECEBIMENTO_FINAL, this.dataRecebimentoFinal);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_QUANTIDADE_ITENS_INICIAL, this.quantidadeItensInicial);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_QUANTIDADE_ITENS_FINAL, this.quantidadeItensFinal);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_CPF_CNPJ, this.cpfCnpj);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_MUNICIPIO_ID_OPT, this.municipioId);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_NOT_MUNICIPIO_OPT, this.notMunicipio);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_CONTA_ID_OPT, this.contaId);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_CONTRATO_REPRESENTANTE_ID_OPT, this.contratoRepresentanteId);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_OUTPUT_STREAM, this.outputStream);
			sd.getArgumentList().setProperty(RelatorioRecebimentoService.IN_APPLICATION_USER_OPT, this.getUserSession().getUser().getObject());
			this.getProcessManager().getServiceManager().execute(sd);
			sd.getFirstOutput();

		} catch (BusinessException e) {
			/* Armazenando a lista de erros */
			this.getMessageList().addAll(e.getErrorList());
		}
	}

	/**
     * Constrói uma lista de Categorias de Contratos para ser usada
     * na exibição da página JSF.
     * @return List<SelectItem>
     * @throws BusinessException 
     */
    public List<SelectItem> getListCategoriaContrato() throws BusinessException {
        List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ContratoCategoria.class, "");
        result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos)"));
        return result;
    }

    public List<SelectItem> getListMunicipio() throws BusinessException {
        List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(Municipio.class, "");
        result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos)"));
        return result;
    }

    public List<SelectItem> getListConta() throws BusinessException {
		List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(Conta.class, " entity.id in (SELECT c.id FROM Conta c inner join c.applicationUsers as user where user.id=" + this.getUserSession().getUser().getId() + ")");
        result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todas)"));
        return result;
    }

    public List<SelectItem> getListContratoRepresentante() throws BusinessException {
        List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(Representante.class, "");
        result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos)"));
        return result;
    }
    
    public List<SelectItem> getListTipoContrato() {
        List<SelectItem> result = new ArrayList<SelectItem>(3);
        result.add(new SelectItem(RelatorioRecebimentoService.TipoContrato.TODOS.ordinal(), RelatorioRecebimentoService.TipoContrato.TODOS.getNome()));
        result.add(new SelectItem(RelatorioRecebimentoService.TipoContrato.ATIVOS.ordinal(), RelatorioRecebimentoService.TipoContrato.ATIVOS.getNome()));
        result.add(new SelectItem(RelatorioRecebimentoService.TipoContrato.INATIVOS.ordinal(), RelatorioRecebimentoService.TipoContrato.INATIVOS.getNome()));
        return result;
    }

	public String getProcessName() {
		return PROCESS_NAME;
	}

	public String getItemCustoIdList() {
		return itemCustoIdList;
	}

	public void setItemCustoIdList(String itemCustoIdList) {
		this.itemCustoIdList = itemCustoIdList;
	}

	public Long getCategoriaContratoId() {
		return categoriaContratoId;
	}

	public void setCategoriaContratoId(Long categoriaContratoId) {
		this.categoriaContratoId = categoriaContratoId;
	}

	public int getTipoContrato() {
		return tipoContrato;
	}

	public void setTipoContrato(int tipoContrato) {
		this.tipoContrato = tipoContrato;
	}

	public Integer getQuantidadeItensInicial() {
		return quantidadeItensInicial;
	}

	public void setQuantidadeItensInicial(Integer quantidadeItensInicial) {
		this.quantidadeItensInicial = quantidadeItensInicial;
	}

	public Integer getQuantidadeItensFinal() {
		return quantidadeItensFinal;
	}

	public void setQuantidadeItensFinal(Integer quantidadeItensFinal) {
		this.quantidadeItensFinal = quantidadeItensFinal;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

//	public Calendar getDataLancamentoFinal() {
//		return dataLancamentoFinal;
//	}
//
//	public void setDataLancamentoFinal(Calendar dataLancamentoFinal) {
//		this.dataLancamentoFinal = dataLancamentoFinal;
//	}
//
//	public Calendar getDataLancamentoInicial() {
//		return dataLancamentoInicial;
//	}
//
//	public void setDataLancamentoInicial(Calendar dataLancamentoInicial) {
//		this.dataLancamentoInicial = dataLancamentoInicial;
//	}

	public Calendar getDataVencimentoFinal() {
		return dataVencimentoFinal;
	}

	public void setDataVencimentoFinal(Calendar dataVencimentoFinal) {
		this.dataVencimentoFinal = dataVencimentoFinal;
	}

	public Calendar getDataVencimentoInicial() {
		return dataVencimentoInicial;
	}

	public void setDataVencimentoInicial(Calendar dataVencimentoInicial) {
		this.dataVencimentoInicial = dataVencimentoInicial;
	}

	public Long getContratoRepresentanteId() {
		return contratoRepresentanteId;
	}

	public void setContratoRepresentanteId(Long contratoRepresentanteId) {
		this.contratoRepresentanteId = contratoRepresentanteId;
	}

	public Long getMunicipioId() {
		return municipioId;
	}

	public void setMunicipioId(Long municipioId) {
		this.municipioId = municipioId;
	}

	public Long getContaId() {
		return contaId;
	}

	public void setContaId(Long contaId) {
		this.contaId = contaId;
	}

	public Calendar getDataRecebimentoFinal() {
		return dataRecebimentoFinal;
	}

	public void setDataRecebimentoFinal(Calendar dataRecebimentoFinal) {
		this.dataRecebimentoFinal = dataRecebimentoFinal;
	}

	public Calendar getDataRecebimentoInicial() {
		return dataRecebimentoInicial;
	}

	public void setDataRecebimentoInicial(Calendar dataRecebimentoInicial) {
		this.dataRecebimentoInicial = dataRecebimentoInicial;
	}

	public Boolean getNotMunicipio() {
		return notMunicipio;
	}

	public void setNotMunicipio(Boolean notMunicipio) {
		this.notMunicipio = notMunicipio;
	}

	/*==============================================================================
	 * IRunnableEntityProcess	
	 *==============================================================================*/
	
	public boolean runWithEntity(IEntity<?> entity) {
        super.beforeRun();

        boolean result = false;
		
		/* Verifica se a entidade é compatível */
		/* Verifica se a entidade passada eh um DocumentoCobranca ou pertence eh descendente */
		if (ClassUtils.isAssignable(entity.getInfo().getType(), Contrato.class)) {
			Contrato oContrato = (Contrato) entity.getObject();
			this.cpfCnpj = oContrato.getPessoa().getDocumento();

			/* Alguns dados poderao ser inicializados aqui */
			this.categoriaContratoId = IDAO.ENTITY_UNSAVED;
			
			/* Define as datas de vencimento e recebimento amplas */
			Calendar dataInicial = CalendarUtils.getCalendar(1900,Calendar.JANUARY,1);
			Calendar dataAtual = CalendarUtils.getCalendar();
			
			this.dataVencimentoInicial = (Calendar) dataInicial.clone(); 
			this.dataVencimentoFinal = (Calendar) dataAtual.clone();
				
			this.dataRecebimentoInicial = (Calendar) dataInicial.clone();
			this.dataRecebimentoFinal = (Calendar) dataAtual.clone();

			/* Não executa nada, pois o processo gera um PDF e é importante
			 * que o operador defina as propriedades do relatório antes de gerar o PDF */
			result = true;
		}else
		if (ClassUtils.isAssignable(entity.getInfo().getType(), Pessoa.class)) {
			Pessoa oPessoa = (Pessoa) entity.getObject();
			this.cpfCnpj = oPessoa.getDocumento();

			/* Alguns dados poderao ser inicializados aqui */
			this.categoriaContratoId = IDAO.ENTITY_UNSAVED;

			/* Define as datas de vencimento e recebimento amplas */
			Calendar dataInicial = CalendarUtils.getCalendar(1900,Calendar.JANUARY,1);
			Calendar dataAtual = CalendarUtils.getCalendar();
			
			this.dataVencimentoInicial = (Calendar) dataInicial.clone(); 
			this.dataVencimentoFinal = (Calendar) dataAtual.clone();
				
			this.dataRecebimentoInicial = (Calendar) dataInicial.clone();
			this.dataRecebimentoFinal = (Calendar) dataAtual.clone();

			/* Não executa nada, pois o processo gera um PDF e é importante
			 * que o operador defina as propriedades do relatório antes de gerar o PDF */
			result = true;
		}else
		{
			this.getMessageList().add(new BusinessMessage(IRunnableEntityProcess.class, "ENTITY_NOT_COMPATIBLE", PROCESS_NAME, entity.getInfo().getType().getName()));
		}
		
		return result;
	}

	public String getEscritorioContabilIdList() {
		return escritorioContabilIdList;
	}

	public void setEscritorioContabilIdList(String escritorioContabilIdList) {
		this.escritorioContabilIdList = escritorioContabilIdList;
	}

	public Boolean getItemCustoNot() {
		return itemCustoNot;
	}

	public void setItemCustoNot(Boolean itemCustoNot) {
		this.itemCustoNot = itemCustoNot;
	}

}
