package br.com.orionsoft.financeiro.gerenciador.process;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.basic.entities.ContratoCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCadastroService;
import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCadastroService.TipoEstabelecimento;
import br.com.orionsoft.financeiro.gerenciador.services.RelatorioCadastroService.TipoRelatorio;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Processo para formar a tela de relatorio geral.
 *  
 * @author Juliana
 * @spring.bean id="RelatorioCadastroProcess" init-method="start" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 */
public class RelatorioCadastroProcess extends ProcessBasic{

	public static final String PROCESS_NAME ="RelatorioCadastroProcess";
	/* Foi comentado pois as datas nem sempre são utilizadas no relatorio*/
//	public void start() throws ProcessException {
//		super.start();
//		
//		if(incluirDatasCadastro){
//			this.dataContratoInicio.set(Calendar.DATE, dataContratoInicio.getActualMinimum(Calendar.DATE));
//			/* Altera a data para o ultimo dia do mes */
//			this.dataContratoFim.set(Calendar.DATE, this.dataContratoFim.getActualMaximum(Calendar.DATE));
//
//			this.dataContratoRescisaoInicio.set(Calendar.DATE, dataContratoRescisaoInicio.getActualMinimum(Calendar.DATE));
//			this.dataContratoRescisaoFim.set(Calendar.DATE, this.dataContratoRescisaoFim.getActualMaximum(Calendar.DATE));
//
//			this.dataComecoAtividadeInicio.set(Calendar.DATE, dataComecoAtividadeInicio.getActualMinimum(Calendar.DATE));
//			this.dataComecoAtividadeFim.set(Calendar.DATE, this.dataComecoAtividadeFim.getActualMaximum(Calendar.DATE));
//
//			this.dataTerminoAtividadeInicio.set(Calendar.DATE, dataTerminoAtividadeInicio.getActualMinimum(Calendar.DATE));
//			this.dataTerminoAtividadeFim.set(Calendar.DATE, this.dataTerminoAtividadeFim.getActualMaximum(Calendar.DATE));
//
//			/* Filtro por datas do financeiro (lancamento)*/
//			this.dataLancamentoInicio.set(Calendar.DATE, dataLancamentoInicio.getActualMinimum(Calendar.DATE));
//			this.dataLancamentoFim.set(Calendar.DATE, this.dataLancamentoFim.getActualMaximum(Calendar.DATE));
//
//			this.dataLancamentoVencimentoInicio.set(Calendar.DATE, dataLancamentoVencimentoInicio.getActualMinimum(Calendar.DATE));
//			this.dataLancamentoVencimentoFim.set(Calendar.DATE, this.dataLancamentoVencimentoFim.getActualMaximum(Calendar.DATE));
//
//			this.dataLancamentoRecebimentoInicio.set(Calendar.DATE, dataLancamentoRecebimentoInicio.getActualMinimum(Calendar.DATE));
//			this.dataLancamentoRecebimentoFim.set(Calendar.DATE, this.dataLancamentoRecebimentoFim.getActualMaximum(Calendar.DATE));
//		}
//	}
	
	/*Variaveis obtidas pela tela do relatorio*/
	private String cpfCnpj;
	private boolean incluirInativos;
	/* Filtro por datas do cadastro (contrato e pessoa)*/
	private Calendar dataContratoInicio = CalendarUtils.getCalendar();
	private Calendar dataContratoFim = CalendarUtils.getCalendar();
	private Calendar dataContratoRescisaoInicio = CalendarUtils.getCalendar();
	private Calendar dataContratoRescisaoFim = CalendarUtils.getCalendar();
	private Calendar dataComecoAtividadeInicio = CalendarUtils.getCalendar();
	private Calendar dataComecoAtividadeFim = CalendarUtils.getCalendar();
	private Calendar dataTerminoAtividadeInicio = CalendarUtils.getCalendar();
	private Calendar dataTerminoAtividadeFim = CalendarUtils.getCalendar();
	/* Filtro por datas do financeiro (lancamento)*/
	private Calendar dataLancamentoInicio = CalendarUtils.getCalendar();
	private Calendar dataLancamentoFim = CalendarUtils.getCalendar();
	private Calendar dataLancamentoVencimentoInicio = CalendarUtils.getCalendar();
	private Calendar dataLancamentoVencimentoFim = CalendarUtils.getCalendar();
	private Calendar dataLancamentoRecebimentoInicio = CalendarUtils.getCalendar();
	private Calendar dataLancamentoRecebimentoFim = CalendarUtils.getCalendar();
	private Long escritorioContabilId;
	private Long cnaeId;
	private Long representanteId;
	private Long municipioId;
	private Integer tipoEstabelecimento;
	private Integer ordenacao;
	private Integer agrupamento;
	private Integer tipoRelatorio = TipoRelatorio.LISTAGEM.ordinal();
	private Long[] itemCustoList;
	private Integer[] campoList; //Indica quais campos foram selecionados (ver enum Campo em RelatorioCadastroService)
	private boolean incluirMovimentacoes;
	private Long contratoCategoriaId;
	private OutputStream outputStream = null;
	
	/* Flags que indicam que a pesquisa sera feita para itens diferentes dos selecionados,
	 * ou seja, exceto os selecionados*/
	private boolean notEscritorioContabilId;
	private boolean notCnaeId;
	private boolean notRepresentanteId;
	private boolean notMunicipioId;
	private boolean notTipoEstabelecimento;
	private boolean notContratoCategoriaId;
	
	/* Vari�veis que indicam quais datas ser�o inclu�das na consulta.
	 * Inicialmente, todas as vari�veis ser�o desabilitadas a receberem valores,
	 * o que evita erros de passagem de parametros atrav�s do JSF, pois os campos datas
	 * do .jsp esperam receber o valor Calendar.time e geram o erro:
	 * 'Error setting property 'time' in bean of type null' 
	 * Isto foi feito porque nem sempre todas as datas s�o requeridas na consulta para
	 * gerar o relatorio*/
	private boolean incluirDatasCadastro; //para mostrar o campo na tela e tamb�m para indicar que alguma data � usada como filtro na consulta
	private boolean incluirDataContrato;
	private boolean incluirDataContratoRescisao;
	private boolean incluirDataComecoAtividade;
	private boolean incluirDataTerminoAtividade;
	private boolean incluirDataLancamento;
	private boolean incluirDataLancamentoRecebimento;
	private boolean incluirDataLancamentoVencimento;
	
	public String getProcessName() {
		return PROCESS_NAME;
	}

	/* Executa o servico*/
	public void execute(){
		try {
			log.debug("Iniciando RelatorioCadastroProcess...");
			ServiceData sd = new ServiceData(RelatorioCadastroService.SERVICE_NAME, null);
			/* TODO: Foi decidido que o agrupamento na consulta fica por enquanto sem ser tratado,
			 * uma vez que a clausula group by influencia a clausula order by*/
//			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_AGRUPAMENTO, Agrupamento.values()[this.agrupamento]);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_COMECO_ATIVIDADE_INICIO, CalendarUtils.formatDate(this.dataComecoAtividadeInicio));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_COMECO_ATIVIDADE_FIM, CalendarUtils.formatDate(this.dataComecoAtividadeFim));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_CONTRATO_INICIO, CalendarUtils.formatDate(this.dataContratoInicio));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_CONTRATO_FIM, CalendarUtils.formatDate(this.dataContratoFim));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_CONTRATO_RESCISAO_INICIO, CalendarUtils.formatDate(this.dataContratoRescisaoInicio));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_CONTRATO_RESCISAO_FIM, CalendarUtils.formatDate(this.dataContratoRescisaoFim));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_LANCAMENTO_INICIO, CalendarUtils.formatDate(this.dataLancamentoInicio));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_LANCAMENTO_FIM, CalendarUtils.formatDate(this.dataLancamentoFim));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_LANCAMENTO_RECEBIMENTO_INICIO, CalendarUtils.formatDate(this.dataLancamentoRecebimentoInicio));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_LANCAMENTO_RECEBIMENTO_FIM, CalendarUtils.formatDate(this.dataLancamentoRecebimentoFim));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_LANCAMENTO_VENCIMENTO_INICIO, CalendarUtils.formatDate(this.dataLancamentoVencimentoInicio));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_LANCAMENTO_VENCIMENTO_FIM, CalendarUtils.formatDate(this.dataLancamentoVencimentoFim));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_TERMINO_ATIVIDADE_INICIO, CalendarUtils.formatDate(this.dataTerminoAtividadeInicio));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_DATA_TERMINO_ATIVIDADE_FIM, CalendarUtils.formatDate(this.dataTerminoAtividadeFim));
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_CAMPO_LIST, this.campoList);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_CNAE_ID, this.cnaeId);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_CONTRATO_CATEGORIA_ID, this.contratoCategoriaId);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_CPF_CNPJ, this.cpfCnpj);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_ESCRITORIO_CONTABIL_ID, this.escritorioContabilId);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_INATIVOS, this.incluirInativos);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_MOVIMENTACOES, this.incluirMovimentacoes);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_ITEM_CUSTO_ID_LIST, this.itemCustoList);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_MUNICIPIO_ID, this.municipioId);

			/* (01/04/09)A ordenacao passa a ser pelo campo contido do enum Campo, pois a ordenação pode ser
			 * feita apenas pelos elementos que foram selecionados para serem exibidos */
//			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_ORDENACAO, Ordenacao.values()[this.ordenacao]);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_ORDENACAO, this.ordenacao);
			/* Ainda ha somente um tipo de relatorio, sendo irrelevante ter esta opcao na tela do usuario
			 * Para não causar erro de nullPointer no servico, a variavel tipoRelatorio foi inicializada com o tipo LISTAGEM*/
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_TIPO_RELATORIO, TipoRelatorio.values()[this.tipoRelatorio]);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_REPRESENTANTE_ID, this.representanteId);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_TIPO_ESTABELECIMENTO, TipoEstabelecimento.values()[this.tipoEstabelecimento]);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_OUTPUT_STREAM, this.outputStream);
			
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_NOT_CNAE_ID, this.notCnaeId);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_NOT_CONTRATO_CATEGORIA_ID, this.notContratoCategoriaId);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_NOT_ESCRITORIO_CONTABIL_ID, this.notEscritorioContabilId);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_NOT_MUNICIPIO_ID, this.notMunicipioId);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_NOT_REPRESENTANTE_ID, this.notRepresentanteId);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_NOT_TIPO_ESTABELECIMENTO_ID, this.notTipoEstabelecimento);
					
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATAS_CADASTRO, this.incluirDatasCadastro);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_CONTRATO, this.incluirDataContrato);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_COMECO_ATIVIDADE, this.incluirDataComecoAtividade);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_TERMINO_ATIVIDADE, this.incluirDataTerminoAtividade);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_CONTRATO_RESCISAO, this.incluirDataContratoRescisao);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_LANCAMENTO, this.incluirDataLancamento);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_LANCAMENTO_RECEBIMENTO, this.incluirDataLancamentoRecebimento);
			sd.getArgumentList().setProperty(RelatorioCadastroService.IN_INCLUIR_DATA_LANCAMENTO_VENCIMENTO, this.incluirDataLancamentoVencimento);
			
			this.getProcessManager().getServiceManager().execute(sd);
			sd.getFirstOutput();

		} catch (BusinessException e) {
			/* Armazenando a lista de erros */
			this.getMessageList().addAll(e.getErrorList());
		}

	}
	
	/**
	 * SelectItem para mostrar as opcoes de tipo de estabelecimento
	 */
	public List<SelectItem> getListTipoEstabelecimento(){
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(RelatorioCadastroService.TipoEstabelecimento.TODOS.ordinal(), RelatorioCadastroService.TipoEstabelecimento.TODOS.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.TipoEstabelecimento.PRINCIPAL.ordinal(), RelatorioCadastroService.TipoEstabelecimento.PRINCIPAL.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.TipoEstabelecimento.FILIAL.ordinal(), RelatorioCadastroService.TipoEstabelecimento.FILIAL.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.TipoEstabelecimento.UNICO.ordinal(), RelatorioCadastroService.TipoEstabelecimento.UNICO.getNome()));
		return result;
	}
	
	/**
	 * SelectItem para mostrar as opcoes para a ordenacao. Usado somente as constantes das 
	 * propriedades de cada classe, mas todos os atributos podem ser obtidos 
	 * atraves da navegacao pelas propriedades de Contrato
	 * Ex: Contrato.Pessoa.asJuridica.nome
	 */
	
	/* (01/04/09)A ordenacao passa a ser pelo campo contido do enum Campo, pois a ordenação pode ser
	 * feita apenas pelos elementos que podem ser selecionados para serem exibidos no relatorio*/
	public List<SelectItem> getListOrdenacao() throws BusinessException{
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(RelatorioCadastroService.Campo.RAZAO_SOCIAL.ordinal(), RelatorioCadastroService.Campo.RAZAO_SOCIAL.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.NOME_FANTASIA.ordinal(), RelatorioCadastroService.Campo.NOME_FANTASIA.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.CPF_CNPJ.ordinal(), RelatorioCadastroService.Campo.CPF_CNPJ.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.LOGRADOURO.ordinal(), RelatorioCadastroService.Campo.LOGRADOURO.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.NUMERO.ordinal(), RelatorioCadastroService.Campo.NUMERO.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.BAIRRO.ordinal(), RelatorioCadastroService.Campo.BAIRRO.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.CEP.ordinal(), RelatorioCadastroService.Campo.CEP.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.CAIXA_POSTAL.ordinal(), RelatorioCadastroService.Campo.CAIXA_POSTAL.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.COMPLEMENTO.ordinal(), RelatorioCadastroService.Campo.COMPLEMENTO.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.MUNICIPIO.ordinal(), RelatorioCadastroService.Campo.MUNICIPIO.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.TELEFONE.ordinal(), RelatorioCadastroService.Campo.TELEFONE.getNome()));
		return result;
	}
//	public List<SelectItem> getListOrdenacao() throws BusinessException{
//		List<SelectItem> result = new ArrayList<SelectItem>();
//		result.add(new SelectItem(RelatorioCadastroService.Ordenacao.NOME.ordinal(), RelatorioCadastroService.Ordenacao.NOME.getNome()));
//		result.add(new SelectItem(RelatorioCadastroService.Ordenacao.APELIDO.ordinal(), RelatorioCadastroService.Ordenacao.APELIDO.getNome()));
//		result.add(new SelectItem(RelatorioCadastroService.Ordenacao.ENDERECO.ordinal(), RelatorioCadastroService.Ordenacao.ENDERECO.getNome()));
//		result.add(new SelectItem(RelatorioCadastroService.Ordenacao.TIPO_ESTABELECIMENTO.ordinal(), RelatorioCadastroService.Ordenacao.TIPO_ESTABELECIMENTO.getNome()));
//		result.add(new SelectItem(RelatorioCadastroService.Ordenacao.ESCRITORIO_CONTABIL.ordinal(), RelatorioCadastroService.Ordenacao.ESCRITORIO_CONTABIL.getNome()));
//		result.add(new SelectItem(RelatorioCadastroService.Ordenacao.CNAE.ordinal(), RelatorioCadastroService.Ordenacao.CNAE.getNome()));
//		result.add(new SelectItem(RelatorioCadastroService.Ordenacao.DOCUMENTO.ordinal(), RelatorioCadastroService.Ordenacao.DOCUMENTO.getNome()));
//		result.add(new SelectItem(RelatorioCadastroService.Ordenacao.MUNICIPIO.ordinal(), RelatorioCadastroService.Ordenacao.MUNICIPIO.getNome()));
//		result.add(new SelectItem(RelatorioCadastroService.Ordenacao.BAIRRO.ordinal(), RelatorioCadastroService.Ordenacao.BAIRRO.getNome()));
//		result.add(new SelectItem(RelatorioCadastroService.Ordenacao.CEP.ordinal(), RelatorioCadastroService.Ordenacao.CEP.getNome()));
//		result.add(new SelectItem(RelatorioCadastroService.Ordenacao.REPRESENTANTE.ordinal(), RelatorioCadastroService.Ordenacao.REPRESENTANTE.getNome()));
//		return result;
//	}
	
	
	
	/**
	 * SelectItem para mostrar as opcoes para o agrupamento. Usado somente as constantes das 
	 * propriedades de cada classe, mas todos os atributos podem ser obtidos 
	 * atraves da navegacao pelas propriedades de Contrato
	 * Ex: Contrato.Pessoa.asJuridica.escritorioContabil
	 */
	public List<SelectItem> getListAgrupamento(){
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(RelatorioCadastroService.Agrupamento.ESCRITORIO_CONTABIL.ordinal(), RelatorioCadastroService.Agrupamento.ESCRITORIO_CONTABIL.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Agrupamento.CNAE.ordinal(), RelatorioCadastroService.Agrupamento.CNAE.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Agrupamento.MUNICIPIO.ordinal(), RelatorioCadastroService.Agrupamento.MUNICIPIO.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Agrupamento.REPRESENTANTE.ordinal(), RelatorioCadastroService.Agrupamento.REPRESENTANTE.getNome()));
		
		return result;
	}

	/**
	 * SelectItem para mostrar as opcoes para tipo de relatorio.
	 */
	public List<SelectItem> getListTipoRelatorio(){
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(RelatorioCadastroService.TipoRelatorio.LISTAGEM.ordinal(), RelatorioCadastroService.TipoRelatorio.LISTAGEM.getNome()));
		
		return result;
	}

	/**
	 * SelectItem para mostrar as opcoes de item de custo
	 */
	public List<SelectItem> getListItemCusto(){
		try {
			return this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ItemCusto.class, "");
		} catch (EntityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * SelectItem para mostrar as opcoes de categoria de contrato
	 * @throws BusinessException 
	 */
    public List<SelectItem> getListContratoCategoria() throws BusinessException {
        List<SelectItem> result = this.getProcessManager().getServiceManager().getEntityManager().getEntitySelectItems(ContratoCategoria.class, "");
        result.add(0, new SelectItem(IDAO.ENTITY_UNSAVED, "(Todos)"));
        return result;
    }

	/**
	 * SelectItem para mostrar as opcoes para tipo de relatorio.
	 */
	public List<SelectItem> getListCampo(){
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(RelatorioCadastroService.Campo.RAZAO_SOCIAL.ordinal(), RelatorioCadastroService.Campo.RAZAO_SOCIAL.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.NOME_FANTASIA.ordinal(), RelatorioCadastroService.Campo.NOME_FANTASIA.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.CPF_CNPJ.ordinal(), RelatorioCadastroService.Campo.CPF_CNPJ.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.LOGRADOURO.ordinal(), RelatorioCadastroService.Campo.LOGRADOURO.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.NUMERO.ordinal(), RelatorioCadastroService.Campo.NUMERO.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.BAIRRO.ordinal(), RelatorioCadastroService.Campo.BAIRRO.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.CEP.ordinal(), RelatorioCadastroService.Campo.CEP.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.CAIXA_POSTAL.ordinal(), RelatorioCadastroService.Campo.CAIXA_POSTAL.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.COMPLEMENTO.ordinal(), RelatorioCadastroService.Campo.COMPLEMENTO.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.MUNICIPIO.ordinal(), RelatorioCadastroService.Campo.MUNICIPIO.getNome()));
		result.add(new SelectItem(RelatorioCadastroService.Campo.TELEFONE.ordinal(), RelatorioCadastroService.Campo.TELEFONE.getNome()));
		
		return result;
	}
	
	/* get-set*/
	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public boolean isIncluirInativos() {
		return incluirInativos;
	}

	public void setIncluirInativos(boolean incluirInativos) {
		this.incluirInativos = incluirInativos;
	}

	public Long getEscritorioContabilId() {
		return escritorioContabilId;
	}

	public void setEscritorioContabilId(Long escritorioContabilId) {
		this.escritorioContabilId = escritorioContabilId;
	}

	public Long getCnaeId() {
		return cnaeId;
	}

	public void setCnaeId(Long cnaeId) {
		this.cnaeId = cnaeId;
	}

	public Long getRepresentanteId() {
		return representanteId;
	}

	public void setRepresentanteId(Long representanteId) {
		this.representanteId = representanteId;
	}

	public Long getMunicipioId() {
		return municipioId;
	}

	public void setMunicipioId(Long municipioId) {
		this.municipioId = municipioId;
	}

	public Integer getTipoEstabelecimento() {
		return tipoEstabelecimento;
	}

	public void setTipoEstabelecimento(Integer tipoEstabelecimento) {
		this.tipoEstabelecimento = tipoEstabelecimento;
	}

	public Long[] getItemCustoList() {
		return itemCustoList;
	}

	public void setItemCustoList(Long[] itemCustoList) {
		this.itemCustoList = itemCustoList;
	}

	public Integer[] getCampoList() {
		return campoList;
	}

	public void setCampoList(Integer[] campoList) {
		this.campoList = campoList;
	}

	public boolean isIncluirMovimentacoes() {
		return incluirMovimentacoes;
	}

	public void setIncluirMovimentacoes(boolean incluirMovimentacoes) {
		this.incluirMovimentacoes = incluirMovimentacoes;
	}

	public Long getContratoCategoriaId() {
		return contratoCategoriaId;
	}

	public void setContratoCategoriaId(Long contratoCategoriaId) {
		this.contratoCategoriaId = contratoCategoriaId;
	}

	public boolean isNotEscritorioContabilId() {
		return notEscritorioContabilId;
	}

	public void setNotEscritorioContabilId(boolean notEscritorioContabilId) {
		this.notEscritorioContabilId = notEscritorioContabilId;
	}

	public boolean isNotCnaeId() {
		return notCnaeId;
	}

	public void setNotCnaeId(boolean notCnaeId) {
		this.notCnaeId = notCnaeId;
	}

	public boolean isNotRepresentanteId() {
		return notRepresentanteId;
	}

	public void setNotRepresentanteId(boolean notRepresentanteId) {
		this.notRepresentanteId = notRepresentanteId;
	}

	public boolean isNotMunicipioId() {
		return notMunicipioId;
	}

	public void setNotMunicipioId(boolean notMunicipioId) {
		this.notMunicipioId = notMunicipioId;
	}

	public boolean isNotTipoEstabelecimento() {
		return notTipoEstabelecimento;
	}

	public void setNotTipoEstabelecimento(boolean notTipoEstabelecimento) {
		this.notTipoEstabelecimento = notTipoEstabelecimento;
	}

	public boolean isNotContratoCategoriaId() {
		return notContratoCategoriaId;
	}

	public void setNotContratoCategoriaId(boolean notContratoCategoriaId) {
		this.notContratoCategoriaId = notContratoCategoriaId;
	}

	public Calendar getDataContratoInicio() {
		return dataContratoInicio;
	}

	public void setDataContratoInicio(Calendar dataContratoInicio) {
		this.dataContratoInicio = dataContratoInicio;
	}

	public Calendar getDataContratoFim() {
		return dataContratoFim;
	}

	public void setDataContratoFim(Calendar dataContratoFim) {
		this.dataContratoFim = dataContratoFim;
	}

	public Calendar getDataContratoRescisaoInicio() {
		return dataContratoRescisaoInicio;
	}

	public void setDataContratoRescisaoInicio(Calendar dataContratoRescisaoInicio) {
		this.dataContratoRescisaoInicio = dataContratoRescisaoInicio;
	}

	public Calendar getDataContratoRescisaoFim() {
		return dataContratoRescisaoFim;
	}

	public void setDataContratoRescisaoFim(Calendar dataContratoRescisaoFim) {
		this.dataContratoRescisaoFim = dataContratoRescisaoFim;
	}

	public Calendar getDataComecoAtividadeInicio() {
		return dataComecoAtividadeInicio;
	}

	public void setDataComecoAtividadeInicio(Calendar dataComecoAtividadeInicio) {
		this.dataComecoAtividadeInicio = dataComecoAtividadeInicio;
	}

	public Calendar getDataComecoAtividadeFim() {
		return dataComecoAtividadeFim;
	}

	public void setDataComecoAtividadeFim(Calendar dataComecoAtividadeFim) {
		this.dataComecoAtividadeFim = dataComecoAtividadeFim;
	}

	public Calendar getDataTerminoAtividadeInicio() {
		return dataTerminoAtividadeInicio;
	}

	public void setDataTerminoAtividadeInicio(Calendar dataTerminoAtividadeInicio) {
		this.dataTerminoAtividadeInicio = dataTerminoAtividadeInicio;
	}

	public Calendar getDataTerminoAtividadeFim() {
		return dataTerminoAtividadeFim;
	}

	public void setDataTerminoAtividadeFim(Calendar dataTerminoAtividadeFim) {
		this.dataTerminoAtividadeFim = dataTerminoAtividadeFim;
	}

	public Calendar getDataLancamentoInicio() {
		return dataLancamentoInicio;
	}

	public void setDataLancamentoInicio(Calendar dataLancamentoInicio) {
		this.dataLancamentoInicio = dataLancamentoInicio;
	}

	public Calendar getDataLancamentoFim() {
		return dataLancamentoFim;
	}

	public void setDataLancamentoFim(Calendar dataLancamentoFim) {
		this.dataLancamentoFim = dataLancamentoFim;
	}

	public Calendar getDataLancamentoVencimentoInicio() {
		return dataLancamentoVencimentoInicio;
	}

	public void setDataLancamentoVencimentoInicio(
			Calendar dataLancamentoVencimentoInicio) {
		this.dataLancamentoVencimentoInicio = dataLancamentoVencimentoInicio;
	}

	public Calendar getDataLancamentoVencimentoFim() {
		return dataLancamentoVencimentoFim;
	}

	public void setDataLancamentoVencimentoFim(Calendar dataLancamentoVencimentoFim) {
		this.dataLancamentoVencimentoFim = dataLancamentoVencimentoFim;
	}

	public Calendar getDataLancamentoRecebimentoInicio() {
		return dataLancamentoRecebimentoInicio;
	}

	public void setDataLancamentoRecebimentoInicio(
			Calendar dataLancamentoRecebimentoInicio) {
		this.dataLancamentoRecebimentoInicio = dataLancamentoRecebimentoInicio;
	}

	public Calendar getDataLancamentoRecebimentoFim() {
		return dataLancamentoRecebimentoFim;
	}

	public void setDataLancamentoRecebimentoFim(
			Calendar dataLancamentoRecebimentoFim) {
		this.dataLancamentoRecebimentoFim = dataLancamentoRecebimentoFim;
	}

	public boolean isIncluirDatasCadastro() {
		return incluirDatasCadastro;
	}

	public void setIncluirDatasCadastro(boolean incluirDatasCadastro) {
		this.incluirDatasCadastro = incluirDatasCadastro;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public Integer getOrdenacao() {
		return ordenacao;
	}

	public void setOrdenacao(Integer ordenacao) {
		this.ordenacao = ordenacao;
	}

	public Integer getAgrupamento() {
		return agrupamento;
	}

	public void setAgrupamento(Integer agrupamento) {
		this.agrupamento = agrupamento;
	}

	public Integer getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(Integer tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public boolean isIncluirDataContrato() {
		return incluirDataContrato;
	}

	public void setIncluirDataContrato(boolean incluirDataContrato) {
		this.incluirDataContrato = incluirDataContrato;
	}

	public boolean isIncluirDataContratoRescisao() {
		return incluirDataContratoRescisao;
	}

	public void setIncluirDataContratoRescisao(boolean incluirDataContratoRescisao) {
		this.incluirDataContratoRescisao = incluirDataContratoRescisao;
	}

	public boolean isIncluirDataComecoAtividade() {
		return incluirDataComecoAtividade;
	}

	public void setIncluirDataComecoAtividade(boolean incluirDataComecoAtividade) {
		this.incluirDataComecoAtividade = incluirDataComecoAtividade;
	}

	public boolean isIncluirDataTerminoAtividade() {
		return incluirDataTerminoAtividade;
	}

	public void setIncluirDataTerminoAtividade(boolean incluirDataTerminoAtividade) {
		this.incluirDataTerminoAtividade = incluirDataTerminoAtividade;
	}

	public boolean isIncluirDataLancamento() {
		return incluirDataLancamento;
	}

	public void setIncluirDataLancamento(boolean incluirDataLancamento) {
		this.incluirDataLancamento = incluirDataLancamento;
	}

	public boolean isIncluirDataLancamentoRecebimento() {
		return incluirDataLancamentoRecebimento;
	}

	public void setIncluirDataLancamentoRecebimento(
			boolean incluirDataLancamentoRecebimento) {
		this.incluirDataLancamentoRecebimento = incluirDataLancamentoRecebimento;
	}

	public boolean isIncluirDataLancamentoVencimento() {
		return incluirDataLancamentoVencimento;
	}

	public void setIncluirDataLancamentoVencimento(
			boolean incluirDataLancamentoVencimento) {
		this.incluirDataLancamentoVencimento = incluirDataLancamentoVencimento;
	}

}
