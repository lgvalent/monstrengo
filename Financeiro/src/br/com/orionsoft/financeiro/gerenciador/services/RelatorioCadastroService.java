package br.com.orionsoft.financeiro.gerenciador.services;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.MaskFormatter;

import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;

import br.com.orionsoft.basic.entities.endereco.Endereco;
import br.com.orionsoft.basic.entities.pessoa.Juridica;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.NativeSQL;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;

/**
 * Monta a sql com os parametros selecionados para gerar o relatorio de cadastro.
 * A String da sql e construida utilizando a classe NativeSQL e apos sua execucao,
 * o resultado e passado para os objetos da classe QueryRelatorioCadastro, que 
 * alimentam o relatorio
 * 
 * @author Antonio e Juliana
 * 
 * @spring.bean id="RelatorioCadastroService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 *
 */
public class RelatorioCadastroService extends ServiceBasic {

	/* Classe que ira alimentar o relatorio Jasper*/
	public class QueryRelatorioCadastro {
		/* Campos que serao mostrados no relatorio*/
		private String nome; 
		private String apelido;
		private String documento;
		private String municipio;
		private String bairro;
		private String logradouro;
		private String numero;
		private String complemento;
		private String cep;
		private String caixaPostal;
		private String telefone;

		public QueryRelatorioCadastro(String nome, String apelido,
				String documento, String municipio, String bairro,
				String logradouro, String numero, String complemento,
				String cep, String caixaPostal, String telefone) {
			super();
			this.nome = nome;
			this.apelido = apelido;
			this.documento = documento;
			this.municipio = municipio;
			this.bairro = bairro;
			this.logradouro = logradouro;
			this.numero = numero;
			this.complemento = complemento;
			this.cep = cep;
			this.caixaPostal = caixaPostal;
			this.telefone = telefone;
		}
		public String getNome() {
			return nome;
		}
		public void setNome(String nome) {
			this.nome = nome;
		}
		public String getApelido() {
			return apelido;
		}
		public void setApelido(String apelido) {
			this.apelido = apelido;
		}
		public String getDocumento() {
			return documento;
		}
		public void setDocumento(String documento) {
			this.documento = documento;
		}
		public String getMunicipio() {
			return municipio;
		}
		public void setMunicipio(String municipio) {
			this.municipio = municipio;
		}
		public String getBairro() {
			return bairro;
		}
		public void setBairro(String bairro) {
			this.bairro = bairro;
		}
		public String getLogradouro() {
			return logradouro;
		}
		public void setLogradouro(String logradouro) {
			this.logradouro = logradouro;
		}
		public String getNumero() {
			return numero;
		}
		public void setNumero(String numero) {
			this.numero = numero;
		}
		public String getComplemento() {
			return complemento;
		}
		public void setComplemento(String complemento) {
			this.complemento = complemento;
		}
		public String getCep() {
			return cep;
		}
		public void setCep(String cep) {
			this.cep = cep;
		}
		public String getCaixaPostal() {
			return caixaPostal;
		}
		public void setCaixaPostal(String caixaPostal) {
			this.caixaPostal = caixaPostal;
		}
		public String getTelefone() {
			return telefone;
		}
		public void setTelefone(String telefone) {
			this.telefone = telefone;
		}
		
		/* Metodo que ERA para ser utilizado para o calculo do tamanho de cada campo
		 * para a montagem do relatorio dinamicamente, via programação*/
		public int getTamanho(Campo campo){
			int tamanho = 0;
			switch (campo) {

			case BAIRRO:
				if(RelatorioCadastroService.this.bairro != null){
					tamanho = RelatorioCadastroService.this.bairro.length(); 
				}
				break;
			case CAIXA_POSTAL: 
				if(RelatorioCadastroService.this.caixaPostal != null){
					tamanho = RelatorioCadastroService.this.caixaPostal.length();
				}
				break;
			case CEP: 
				if(RelatorioCadastroService.this.cep != null){
					tamanho = RelatorioCadastroService.this.cep.length();
				}
				break;
			case CPF_CNPJ: 
				if(RelatorioCadastroService.this.documento != null){
					tamanho = RelatorioCadastroService.this.documento.length();
				}
				break;
			case COMPLEMENTO: 
				if(RelatorioCadastroService.this.complemento != null){
					tamanho = RelatorioCadastroService.this.complemento.length();
				}
				break;
			case MUNICIPIO: 
				if(RelatorioCadastroService.this.municipio != null){
					tamanho = RelatorioCadastroService.this.municipio.length();
				}
				break;
			case NOME_FANTASIA: 
				if(RelatorioCadastroService.this.apelido != null){
					tamanho = RelatorioCadastroService.this.apelido.length();
				}
				break;
			case NUMERO: 
				if(RelatorioCadastroService.this.numero != null){
					tamanho = RelatorioCadastroService.this.numero.length();	
				}
				break;
			case LOGRADOURO: 
				if(RelatorioCadastroService.this.logradouro != null){
					tamanho = RelatorioCadastroService.this.logradouro.length(); 
				}
				break;
			case RAZAO_SOCIAL: 
				if(RelatorioCadastroService.this.nome!= null){
					tamanho = RelatorioCadastroService.this.nome.length(); 
				}
				break;
			case TELEFONE: 
				if(RelatorioCadastroService.this.telefones != null){
					tamanho = RelatorioCadastroService.this.telefones.length(); 	
				}
				break;
			}
			return tamanho;
		}

	}


	public static final String SERVICE_NAME = "RelatorioCadastroService";

	public static final String IN_CPF_CNPJ = "cpfCnpj";
	public static final String IN_INCLUIR_INATIVOS = "incluirInativos";
	public static final String IN_DATA_CONTRATO_INICIO = "dataContratoInicio";
	public static final String IN_DATA_CONTRATO_FIM = "dataContratoFim";
	public static final String IN_DATA_CONTRATO_RESCISAO_INICIO = "dataContratoRescisaoInicio";
	public static final String IN_DATA_CONTRATO_RESCISAO_FIM = "dataContratoRescisaoFim";
	public static final String IN_DATA_COMECO_ATIVIDADE_INICIO = "dataComecoAtividadeInicio"; // Contrato.pessoa.dataInicial
	public static final String IN_DATA_COMECO_ATIVIDADE_FIM = "dataComecoAtividadeFim"; // Contrato.pessoa.dataInicial
	public static final String IN_DATA_TERMINO_ATIVIDADE_INICIO = "dataTerminoAtividadeInicio"; //Contrato.pessoa.dataFinal
	public static final String IN_DATA_TERMINO_ATIVIDADE_FIM = "dataTerminoAtividadeFim"; //Contrato.pessoa.dataFinal
	public static final String IN_DATA_LANCAMENTO_INICIO = "dataLancamentoInicio";
	public static final String IN_DATA_LANCAMENTO_FIM = "dataLancamentoFim";
	public static final String IN_DATA_LANCAMENTO_VENCIMENTO_INICIO = "dataLancamentoVencimentoInicio";
	public static final String IN_DATA_LANCAMENTO_VENCIMENTO_FIM = "dataLancamentoVencimentoFim";
	public static final String IN_DATA_LANCAMENTO_RECEBIMENTO_INICIO = "dataLancamentoRecebimentoInicio"; //Lancamento.LancamentoMovimento.data
	public static final String IN_DATA_LANCAMENTO_RECEBIMENTO_FIM = "dataLancamentoRecebimentoFim";
	public static final String IN_ESCRITORIO_CONTABIL_ID = "escritorioContabilId";
	public static final String IN_CNAE_ID = "cnaeId";
	public static final String IN_REPRESENTANTE_ID = "representanteId";
	public static final String IN_MUNICIPIO_ID = "municioId";
	public static final String IN_TIPO_ESTABELECIMENTO = "tipoEstabelecimento";
	public static final String IN_ORDENACAO = "ordenacao";
//	public static final String IN_AGRUPAMENTO = "agrupamento";
	public static final String IN_TIPO_RELATORIO = "tipoRelatorio";
	public static final String IN_ITEM_CUSTO_ID_LIST = "itemCustoIdList";
	public static final String IN_CAMPO_LIST = "campoList";
	public static final String IN_INCLUIR_MOVIMENTACOES = "incluirMovimentacoes";
	public static final String IN_CONTRATO_CATEGORIA_ID = "contratoCategoriaId";
	public static final String IN_OUTPUT_STREAM = "outputStream";
	
	/* Flags que indicam que a pesquisa sera feita para itens diferentes dos selecionados,
	 * ou seja, exceto os selecionados*/
	public static final String IN_NOT_ESCRITORIO_CONTABIL_ID= "notEscritorioContabilId";
	public static final String IN_NOT_CNAE_ID = "notCnaeId";
	public static final String IN_NOT_REPRESENTANTE_ID = "notRepresentanteId";
	public static final String IN_NOT_MUNICIPIO_ID = "notMunicipioId";
	public static final String IN_NOT_TIPO_ESTABELECIMENTO_ID = "notTipoEstabelecimentoId";
	public static final String IN_NOT_CONTRATO_CATEGORIA_ID = "notContratoCategoriaId";

	/* Variaveis que indicam quais datas serao incluidas na consulta.
	 * Inicialmente, todas as variaveis serao desabilitadas a receberem valores,
	 * o que evita erros de passagem de parametros atraves do JSF, pois os campos datas
	 * do .jsp esperam receber o valor Calendar.time e geram o erro:
	 * 'Error setting property 'time' in bean of type null' 
	 * Isto foi feito porque nem sempre todas as datas sao requeridas na consulta para
	 * gerar o relatorio*/
	public static final String IN_INCLUIR_DATAS_CADASTRO = "incluirDatasCadastro";//boolean que indica se as datas de cadastro ser�o ou n�o utilizadas na consulta
	public static final String IN_INCLUIR_DATA_CONTRATO = "incluirDataContrato";
	public static final String IN_INCLUIR_DATA_CONTRATO_RESCISAO = "incluirDataContratoRescisao";
	public static final String IN_INCLUIR_DATA_COMECO_ATIVIDADE = "incluirDataComecoAtividade";
	public static final String IN_INCLUIR_DATA_TERMINO_ATIVIDADE = "incluirDataTerminoAtividade";
	public static final String IN_INCLUIR_DATA_LANCAMENTO = "incluirDataLancamento";
	public static final String IN_INCLUIR_DATA_LANCAMENTO_RECEBIMENTO = "incluirDataLancamentoRecebimento";
	public static final String IN_INCLUIR_DATA_LANCAMENTO_VENCIMENTO = "incluirDataLancamentoVencimento"; 
	
	/* Como os campos a serem exibidos no relatorio sao escolhidos 
	 * pelo usuario, deve-se fazer o tratamento dos campos que nao 
	 * foram selecionados para a exibicao, devido a clausula SELECT. 
	 * Assim, as variaveis abaixo sao utilizadas para receber o resultado 
	 * do ResultSet, para verificar quais campos não foram selecionados 
	 * (tratamento de null).
	 */
	/* Variaveis temporarias para receber o resultado do ResultSet.
	 * Elas sao avaliadas pelo IReport atraves da expresssao
	 * (!$F{campo}.equals(null)) na configuracao 
	 * 'Imprimir quando a Expressao'. Dessa forma, quando o campo
	 * nao e selecionado pelo usuario, nem o texto estatico e o campo (field)
	 * sao exibidos no relatorio*/
	private String nome;
	private String apelido;
	private String documento; 
	private String municipio; 
	private String bairro;
	private String logradouro;
	private String numero;
	private String complemento;
	private String cep;
	private String caixaPostal;
	private String telefones;
	private int tamanhoNome = 0 ;
	private int tamanhoNomeMaximo = 0;
	/* Variaveis para montar o layout do relatorio*/
	private int posicaoX = 0;
	private int posicaoY = 0;
	private int alturaBanda = 0;
	private boolean mudarLinha = false;
	private int relatorioTamanho;
	
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	public void execute(ServiceData sd) throws ServiceException {
		log.debug(">>Iniciando RelatorioCadastroService...");

		/*
		 * Lista os parametros do servico
		 */
		String inCpfCnpj = (String)sd.getArgumentList().getProperty(IN_CPF_CNPJ);
		Boolean inIncluirInativos = (Boolean)sd.getArgumentList().getProperty(IN_INCLUIR_INATIVOS);
		Calendar inDataContratoInicio = (Calendar) sd.getArgumentList().getProperty(IN_DATA_CONTRATO_INICIO);
		Calendar inDataContratoFim = (Calendar) sd.getArgumentList().getProperty(IN_DATA_CONTRATO_FIM);
		Calendar inDataContratoRescisaoInicio = (Calendar) sd.getArgumentList().getProperty(IN_DATA_CONTRATO_RESCISAO_INICIO);
		Calendar inDataContratoRescisaoFim = (Calendar) sd.getArgumentList().getProperty(IN_DATA_CONTRATO_RESCISAO_FIM);
		Calendar inDataComecoAtividadeInicio = (Calendar) sd.getArgumentList().getProperty(IN_DATA_COMECO_ATIVIDADE_INICIO);
		Calendar inDataComecoAtividadeFim = (Calendar) sd.getArgumentList().getProperty(IN_DATA_COMECO_ATIVIDADE_FIM);
		Calendar inDataTerminoAtividadeInicio = (Calendar) sd.getArgumentList().getProperty(IN_DATA_TERMINO_ATIVIDADE_INICIO);
		Calendar inDataTerminoAtividadeFim = (Calendar) sd.getArgumentList().getProperty(IN_DATA_TERMINO_ATIVIDADE_FIM);
		Calendar inDataLancamentoInicio = (Calendar) sd.getArgumentList().getProperty(IN_DATA_LANCAMENTO_INICIO);
		Calendar inDataLancamentoFim = (Calendar) sd.getArgumentList().getProperty(IN_DATA_LANCAMENTO_FIM);
		Calendar inDataLancamentoVencimentoInicio = (Calendar) sd.getArgumentList().getProperty(IN_DATA_LANCAMENTO_VENCIMENTO_INICIO);
		Calendar inDataLancamentoVencimentoFim = (Calendar) sd.getArgumentList().getProperty(IN_DATA_LANCAMENTO_VENCIMENTO_FIM);
		Calendar inDataLancamentoRecebimentoInicio = (Calendar) sd.getArgumentList().getProperty(IN_DATA_LANCAMENTO_RECEBIMENTO_INICIO);
		Calendar inDataLancamentoRecebimentoFim = (Calendar) sd.getArgumentList().getProperty(IN_DATA_LANCAMENTO_RECEBIMENTO_FIM);
		Long inEscritorioContabilId = (Long) sd.getArgumentList().getProperty(IN_ESCRITORIO_CONTABIL_ID);
		Long inCnaeId = (Long) sd.getArgumentList().getProperty(IN_CNAE_ID);
		Long inRepresentanteId = (Long) sd.getArgumentList().getProperty(IN_REPRESENTANTE_ID);
		Long inMunicipioId = (Long) sd.getArgumentList().getProperty(IN_MUNICIPIO_ID);
		TipoEstabelecimento inTipoEstabelecimento = (TipoEstabelecimento) sd.getArgumentList().getProperty(IN_TIPO_ESTABELECIMENTO);
		/* A ordenacao deixa de ser um enum e passa a utilizar os mesmos campos do enum de CAMPO*/
//		Ordenacao inOrdenacao = (Ordenacao) sd.getArgumentList().getProperty(IN_ORDENACAO);
		Integer inOrdenacao = (Integer) sd.getArgumentList().getProperty(IN_ORDENACAO);
//		Agrupamento inAgrupamento = (Agrupamento) sd.getArgumentList().getProperty(IN_AGRUPAMENTO);
		TipoRelatorio inTipoRelatorio = (TipoRelatorio) sd.getArgumentList().getProperty(IN_TIPO_RELATORIO);
		Long[] inItemCustoIdList = (Long[]) sd.getArgumentList().getProperty(IN_ITEM_CUSTO_ID_LIST);
		Integer[] inCampoList = (Integer[])sd.getArgumentList().getProperty(IN_CAMPO_LIST);
		Boolean inIncluirMovimentacoes = (Boolean)sd.getArgumentList().getProperty(IN_INCLUIR_MOVIMENTACOES);
		Long inContratoCategoriaId = (Long) sd.getArgumentList().getProperty(IN_CONTRATO_CATEGORIA_ID);
		OutputStream inOutputStream = (OutputStream) sd.getArgumentList().getProperty(IN_OUTPUT_STREAM);
		
		/* Flags que indicam que a pesquisa sera feita para itens diferentes dos selecionados,
		 * ou seja, exceto os selecionados*/
		Boolean inNotEscritorioContabilId = (Boolean)sd.getArgumentList().getProperty(IN_NOT_ESCRITORIO_CONTABIL_ID);
		Boolean inNotCnaeId = (Boolean)sd.getArgumentList().getProperty(IN_NOT_CNAE_ID);
		Boolean inNotRepresentanteId = (Boolean)sd.getArgumentList().getProperty(IN_NOT_REPRESENTANTE_ID);
		Boolean inNotMunicipioId = (Boolean)sd.getArgumentList().getProperty(IN_NOT_MUNICIPIO_ID);
		Boolean inNotTipoEstabelecimento = (Boolean)sd.getArgumentList().getProperty(IN_NOT_TIPO_ESTABELECIMENTO_ID);
		Boolean inNotContratoCategoriaId = (Boolean)sd.getArgumentList().getProperty(IN_NOT_CONTRATO_CATEGORIA_ID);
		
		/* Flags que indicam quais datas foram selecionadas como filtros na consulta*/
		Boolean inIncluirDatasCadastro = (Boolean)sd.getArgumentList().getProperty(IN_INCLUIR_DATAS_CADASTRO);
		Boolean inIncluirDataContrato = (Boolean)sd.getArgumentList().getProperty(IN_INCLUIR_DATA_CONTRATO);
		Boolean inIncluirDataContratoRescisao = (Boolean)sd.getArgumentList().getProperty(IN_INCLUIR_DATA_CONTRATO_RESCISAO);
		Boolean inIncluirDataComecoAtividade = (Boolean)sd.getArgumentList().getProperty(IN_INCLUIR_DATA_COMECO_ATIVIDADE);
		Boolean inIncluirDataTerminoAtividade = (Boolean)sd.getArgumentList().getProperty(IN_INCLUIR_DATA_TERMINO_ATIVIDADE);
		Boolean inIncluirDataLancamento = (Boolean)sd.getArgumentList().getProperty(IN_INCLUIR_DATA_LANCAMENTO);
		Boolean inIncluirDataLancamentoRecebimento = (Boolean)sd.getArgumentList().getProperty(IN_INCLUIR_DATA_LANCAMENTO_RECEBIMENTO);
		Boolean inIncluirDataLancamentoVencimento = (Boolean)sd.getArgumentList().getProperty(IN_INCLUIR_DATA_LANCAMENTO_VENCIMENTO);


		try {
			NativeSQL sql = null; 
			if (inTipoRelatorio == TipoRelatorio.LISTAGEM) {

				/* Construtor: NativeSQL(Session session, String select, String where, String having, String group, String order) */
				sql = new NativeSQL(sd.getCurrentSession(), QueryListagem.SQL_SELECT, null, null, null, null);

				sql = MontarSql(sql, 
						inCpfCnpj, 
						inIncluirInativos, 
						inDataContratoInicio,
						inDataContratoFim,
						inDataContratoRescisaoInicio,
						inDataContratoRescisaoFim,
						inDataComecoAtividadeInicio,
						inDataComecoAtividadeFim,
						inDataTerminoAtividadeInicio,
						inDataTerminoAtividadeFim,
						inDataLancamentoInicio,
						inDataLancamentoFim,
						inDataLancamentoVencimentoInicio,
						inDataLancamentoVencimentoFim,
						inDataLancamentoRecebimentoInicio,
						inDataLancamentoRecebimentoFim,
						inEscritorioContabilId, 
						inCnaeId, 
						inRepresentanteId, 
						inMunicipioId, 
						inTipoEstabelecimento, 
						inOrdenacao, 
						/*inAgrupamento,*/ 
						inItemCustoIdList, 
						inCampoList, 
						inIncluirMovimentacoes, 
						inContratoCategoriaId,
						inNotEscritorioContabilId,
						inNotCnaeId,
						inNotRepresentanteId,
						inNotMunicipioId,
						inNotTipoEstabelecimento,
						inNotContratoCategoriaId,
						inIncluirDatasCadastro,
						inIncluirDataContrato,
						inIncluirDataContratoRescisao,
						inIncluirDataComecoAtividade,
						inIncluirDataTerminoAtividade,
						inIncluirDataLancamento,
						inIncluirDataLancamentoRecebimento, 
						inIncluirDataLancamentoVencimento);

				System.out.println("==> "+sql.getSql());
			}

			if(sql != null){
				sql.executeQuery();
				ResultSet rs = sql.executeQuery();
				List<QueryRelatorioCadastro> list = new ArrayList<QueryRelatorioCadastro>();

				log.debug("Preparando mascara dos campos Documento. RelatorioCadastroService");
				MaskFormatter mfDocumento = new MaskFormatter(this.getServiceManager().getEntityManager().getEntityMetadata(Juridica.class).getPropertyMetadata(Juridica.DOCUMENTO).getEditMask());
				mfDocumento.setValueContainsLiteralCharacters(false);
				mfDocumento.setAllowsInvalid(true);

				/* Apos obtida a String sql e depois de executa-la,
				 * o resultado da consulta (ResultSet), armazenado em rs, 
				 * alimenta os objetos da classe QueryRelatorioCadastro, que 
				 * serao passado para o Jasper.*/

				QueryRelatorioCadastro query = null;
				while (rs.next()) {
					/* Para cada rs verifica os itens selecionados no inCampoList, para
					 * posteriormente serem passados para os objetos da classe
					 * QueryRelatorioCadastro*/
					for(int campoSelecionado: inCampoList){

						if(campoSelecionado == Campo.RAZAO_SOCIAL.ordinal()){
							nome = rs.getString(Campo.RAZAO_SOCIAL.campo);
							tamanhoNome = nome.length();
							if(tamanhoNome > tamanhoNomeMaximo){
								tamanhoNomeMaximo = tamanhoNome;
							}
						}
						if(campoSelecionado == Campo.NOME_FANTASIA.ordinal()){
							apelido = rs.getString(Campo.NOME_FANTASIA.campo);
						}
						if(campoSelecionado == Campo.CPF_CNPJ.ordinal()){
							documento = mfDocumento.valueToString(rs.getString(Campo.CPF_CNPJ.campo));
						}
						if(campoSelecionado == Campo.MUNICIPIO.ordinal()){
							municipio = rs.getString(Campo.MUNICIPIO.campo);
						}
						if(campoSelecionado == Campo.BAIRRO.ordinal()){
							bairro = rs.getString(Campo.BAIRRO.campo);
						}
						if(campoSelecionado == Campo.LOGRADOURO.ordinal()){
							logradouro = rs.getString(Campo.LOGRADOURO.campo);
						}
						if(campoSelecionado == Campo.NUMERO.ordinal()){
							numero = rs.getString(Campo.NUMERO.campo);
						}
						if(campoSelecionado == Campo.COMPLEMENTO.ordinal()){
							complemento = rs.getString(Campo.COMPLEMENTO.campo);
						}
						if(campoSelecionado == Campo.CEP.ordinal()){
							cep = rs.getString(Campo.CEP.campo);
						}
						if(campoSelecionado == Campo.CAIXA_POSTAL.ordinal()){
							caixaPostal = rs.getString(Campo.CAIXA_POSTAL.campo);
						}
						if(campoSelecionado == Campo.TELEFONE.ordinal()){
							telefones = rs.getString(Campo.TELEFONE.campo);
						}

					}//fim do for

					query = new QueryRelatorioCadastro(
							nome,
							apelido,
							documento,
							municipio,
							bairro,
							logradouro,
							numero,
							complemento,
							cep,
							caixaPostal,
							telefones);
					list.add(query);
				}//fim do while
				sd.getOutputData().add(list);


				/* JASPER */

				log.debug("Compilando o relatorio.");
				String nomeArquivoFonte = "RelatorioCadastro.jrxml";

				/* Le o modelo de relatorio*/
				JasperDesign design = JRXmlLoader.load(getClass().getResourceAsStream(nomeArquivoFonte));
				/* Como o metodo montarRelatorio e chamado varias vezes, o calculo do tamanho do 
				 * espaco do relatorio a ser preenchido e calculado uma vez e passado como parametro 
				 * para o metodo*/
				relatorioTamanho = design.getPageWidth() - (design.getLeftMargin() + design.getRightMargin());

				Campo campo; //para identificar qual campo foi selecionado
				/* TODO Redimensionar a posicao dos campos, de acordo com o conteudo*/
				for (int i = 0; i<inCampoList.length ; i++){
					campo = Campo.values()[inCampoList[i]];
					montarRelatorio(design, campo);
				}		
				/* Define a altura da banda do columnHeader e Detail do relatorio Jasper,
				 * apos obtido a disposicao dos campos de acordo com a selecao da consulta
				 */
				definirAlturaBanda(design);

				/* Define o tipo de orientacao (retrato ou paisagem de acordo com a quantidade de campo*/
				if(inCampoList.length <=3){
					design.setOrientation(JRReport.ORIENTATION_PORTRAIT);
				}
				else{
					design.setOrientation(JRReport.ORIENTATION_LANDSCAPE);
				}
				/* Apos montada o design, passar para o relatorio*/
				JasperReport relatorio = JasperCompileManager.compileReport(design);
//				JasperReport relatorio = JasperCompileManager.compileReport(getClass().getResourceAsStream(nomeArquivoFonte));

				Map<String, Integer> parametros = new HashMap<String, Integer>();
				parametros.put("QUANTIDADE_CAMPOS", inCampoList.length);

				log.debug("Imprimindo o relatorio.");
				JasperPrint print = JasperFillManager.fillReport(relatorio, parametros, new JRBeanCollectionDataSource(list));

				if (inOutputStream != null) {
					JasperExportManager.exportReportToPdfStream(print, inOutputStream);
				}
			}//fim do if

		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			log.fatal(e.getMessage());
			/* Indica que o servico falhou por causa de uma excecao da SQL. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		} catch (ParseException e) {
			/* Indica que o servico falhou por causa de uma excecao de Parser da funcao MaskFormatter. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		} catch (MetadataException e) {
			/* Indica que o servico falhou por causa de uma excecao de Metadata. */
			throw new ServiceException(MessageList.createSingleInternalError(e));
		} catch (EntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private NativeSQL MontarSql(NativeSQL sql, 
			String inCpfCnpj, 
			Boolean inIncluirInativos, 
			Calendar inDataContratoInicio,
			Calendar inDataContratoFim,
			Calendar inDataContratoRescisaoInicio,
			Calendar inDataContratoRescisaoFim,
			Calendar inDataComecoAtividadeInicio,
			Calendar inDataComecoAtividadeFim,
			Calendar inDataTerminoAtividadeInicio,
			Calendar inDataTerminoAtividadeFim,
			Calendar inDataLancamentoInicio,
			Calendar inDataLancamentoFim,
			Calendar inDataLancamentoVencimentoInicio,
			Calendar inDataLancamentoVencimentoFim,
			Calendar inDataLancamentoRecebimentoInicio,
			Calendar inDataLancamentoRecebimentoFim,
			Long inEscritorioContabilId, 
			Long inCnaeId, 
			Long inRepresentanteId, 
			Long inMunicipioId, 
			TipoEstabelecimento inTipoEstabelecimento, 
			/*Ordenacao inOrdenacao,*/
			Integer inOrdenacao,
			/*Agrupamento inAgrupamento,*/ 
			Long[] inItemCustoIdList, 
			Integer[] inCampoList, 
			Boolean inIncluirMovimentacoes, 
			Long inContratoCategoriaId,
			Boolean inNotEscritorioContabilId,
			Boolean inNotCnaeId,
			Boolean inNotRepresentanteId,
			Boolean inNotMunicipioId,
			Boolean inNotTipoEstabelecimento,
			Boolean inNotContratoCategoriaId,
			Boolean inIncluirDatasCadastro,
			Boolean inIncluirDataContrato,
			Boolean inIncluirDataContratoRescisao,
			Boolean inIncluirDataComecoAtividade,
			Boolean inIncluirDataTerminoAtividade,
			Boolean inIncluirDataLancamento,
			Boolean inIncluirDataLancamentoRecebimento,
			Boolean inIncluirDataLancamentoVencimento) {
		/*
		 * SELECT
		 */
		StringBuilder select = new StringBuilder();
		if (inCampoList != null) {
			select.append(Campo.toString(inCampoList));
			/* A consulta possui vários joins que ocasionam a repetição de registros no resultado da sql.
			 * Por exemplo, Contrato inner Join Lancamento selecionará varios registros da mesma Pessoa.
			 * Assim, é feito o Group by pelos campos que são selecionados para serem mostrados no relatorio*/
			sql.addGroup(Campo.setGroupBy(inCampoList));
		}

		/*
		 * WHERE
		 */
		if (StringUtils.isNotBlank(inCpfCnpj)) {
			sql.addWhere("Pessoa.documento = '" + inCpfCnpj + "'");
		}
		if (!inIncluirInativos) {
			sql.addWhere("Contrato.inativo = false");
		}
		/*
		 * Verificacao das datas do cadastro do contrato e da pessoa
		 * 
		 * Obs (juliana - 23/01/09: No phpmyadmin, a busca por datas utilizando 
		 * CalendarUtils.formatToSQLDate(data) nao funciona
		 * No MySQL Query Browser (Windows) a data esta do formato "aaaa-mm-dd"  
		 */
		if(inIncluirDatasCadastro){
			if (inIncluirDataContrato) {
				sql.addWhere("Contrato.dataInicio >= '" + CalendarUtils.formatToSQLDate(inDataContratoInicio) + "'");
				sql.addWhere("Contrato.dataInicio <= '" + CalendarUtils.formatToSQLDate(inDataContratoFim) + "'");
			}
			if (inIncluirDataContratoRescisao) {
				sql.addWhere("Contrato.dataRescisao >= '" + CalendarUtils.formatToSQLDate(inDataContratoRescisaoInicio) + "'");
				sql.addWhere("Contrato.dataRescisao <= '" + CalendarUtils.formatToSQLDate(inDataContratoRescisaoFim) + "'");
			}
			if (inIncluirDataComecoAtividade) {
				sql.addWhere("Pessoa.dataInicial >= '" + CalendarUtils.formatToSQLDate(inDataComecoAtividadeInicio) + "'");
				sql.addWhere("Pessoa.dataInicial <= '" + CalendarUtils.formatToSQLDate(inDataComecoAtividadeFim) + "'");
			}
			if (inIncluirDataTerminoAtividade) {
				sql.addWhere("Pessoa.dataFinal >= '" + CalendarUtils.formatToSQLDate(inDataTerminoAtividadeInicio) + "'");
				sql.addWhere("Pessoa.dataFinal <= '" + CalendarUtils.formatToSQLDate(inDataTerminoAtividadeFim) + "'");
			}
		}
		if (inEscritorioContabilId != null) {
			if(inNotEscritorioContabilId){
				sql.addWhere("Pessoa.escritorioContabil != " + inEscritorioContabilId);
			}
			else{
				sql.addWhere("Pessoa.escritorioContabil = " + inEscritorioContabilId);
			}
		}
		if (inCnaeId != null) {
			if(inNotCnaeId){
				sql.addWhere("Pessoa.cnae != " + inCnaeId);
			}
			else{
				sql.addWhere("Pessoa.cnae = " + inCnaeId);
			}
		}
		if (inRepresentanteId != null) {
			if(inNotRepresentanteId){
				sql.addWhere("Contrato.representante != " + inRepresentanteId);
			}
			else{
				sql.addWhere("Contrato.representante = " + inRepresentanteId);
			}
		}
		if (inMunicipioId != null) {
			if(inNotMunicipioId){
				sql.addWhere("Endereco.municipio != " + inMunicipioId);
			}
			else{
				sql.addWhere("Endereco.municipio = " + inMunicipioId);
			}
		}
		if (inTipoEstabelecimento != null) {
			if(!(inTipoEstabelecimento.equals(TipoEstabelecimento.TODOS))){
				if(inNotTipoEstabelecimento){
					sql.addWhere("Pessoa.tipoEstabelecimento != '" + inTipoEstabelecimento.getNome() + "'");
				}
				else{
					sql.addWhere("Pessoa.tipoEstabelecimento = '" + inTipoEstabelecimento.getNome() + "'");
				}
			}
		}
		if (inContratoCategoriaId != null) {
			if(inContratoCategoriaId != IDAO.ENTITY_UNSAVED){
				if(inNotContratoCategoriaId){
					sql.addWhere("Contrato.categoria != " + inContratoCategoriaId);
				}
				else{
					sql.addWhere("Contrato.categoria = " + inContratoCategoriaId);
				}
			}
		}

		/*
		 * FILTRAR POR MOVIMENTACOES
		 */
		if (inIncluirMovimentacoes){

			/* Verificacao das datas do financeiro, que correspondem ao lancamento e lancamentoMovimento*/
			if (inIncluirDataLancamento) {
				sql.addWhere("Lancamento.data >= '" + CalendarUtils.formatToSQLDate(inDataLancamentoInicio) + "'");
				sql.addWhere("Lancamento.data <= '" + CalendarUtils.formatToSQLDate(inDataLancamentoFim) + "'");
			}
			if (inIncluirDataLancamentoVencimento) {
				sql.addWhere("Lancamento.dataVencimento >= '" + CalendarUtils.formatToSQLDate(inDataLancamentoVencimentoInicio) + "'");
				sql.addWhere("Lancamento.dataVencimento <= '" + CalendarUtils.formatToSQLDate(inDataLancamentoVencimentoFim) + "'");
			}

			if (inIncluirDataLancamentoRecebimento) {
				sql.addWhere("LancamentoMovimento.data >= '" + CalendarUtils.formatToSQLDate(inDataLancamentoRecebimentoInicio) + "'");
				sql.addWhere("LancamentoMovimento.data <= '" + CalendarUtils.formatToSQLDate(inDataLancamentoRecebimentoFim) + "'");
			}
			if (inItemCustoIdList != null) {
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < inItemCustoIdList.length; i++) {
					if (i > 0)
						builder.append(", ");
					builder.append(inItemCustoIdList[i]);
				}
				sql.addWhere("itemCusto in (" + builder.toString() + ")");
			}
		}

		/*
		 * ORDER
		 * 
		 * TODO - a ordenacao deve ser feita por um campo que está contido dentro dos 
		 * campos que foram selecionados para serem exibidos no relatorio
		 */

		if (inOrdenacao != null) {
			/* Tratamento das propriedades para não dar erro de ambiguidade*/
			sql.addOrder(Campo.values()[inOrdenacao].getCampo());
		}

		/*
		 * GROUP
		 */
		/* Para cada tipo de agrupamento, fazer um Jasper diferente*/
		/* TODO: Foi decidido que o agrupamento na consulta fica por enquanto sem ser tratado,
		 * uma vez que a clausula group by influencia a clausula order by*/
//		if (inAgrupamento != null) {
//		}

		sql.setSelectFields("select", select.toString());

		return sql;
	}

	/*
	 * Constantes para montar a lista de tipo de estabelecimento, propriedade
	 * de pessoa juridica. Os valores foram observados no arquivo Juridica.properties
	 * tipoEstabelecimento.valuesList=Filial,Principal,Unico
	 */
	public enum TipoEstabelecimento {
		TODOS("Todos"),
		PRINCIPAL("Principal"),
		FILIAL("Filial"),
		UNICO("�nico");

		private String nome;

		private TipoEstabelecimento(String nome) {
			this.nome = nome;
		}

		public String getNome() {
			return nome;
		}
	}

	/*
	 * Constantes para montar a lista de opcoes para ordenacao.
	 * (01/04/09) Este enum foi comentado, pois a ordenação depende 
	 * dos campos que foram selecionados para serem mostrados no
	 * relatorio
	 */
//	public enum Ordenacao {
//		NOME("Nome"),
//		APELIDO("Apelido"),
//		ENDERECO("Endereço"),
//		TIPO_ESTABELECIMENTO("Tipo de estabelecimento"),
//		ESCRITORIO_CONTABIL("Escritorio contábil"),
//		CNAE("CNAE"),
//		DOCUMENTO("Documento"),
//		MUNICIPIO("Municipio"),
//		BAIRRO("Bairro"),
//		CEP("CEP"),
//		REPRESENTANTE("Representante");

//		private String nome;

//		private Ordenacao(String nome) {
//			this.nome = nome;
//		}

//		public String getNome() {
//			return nome;
//		}
//	}

/*
 * Constantes para montar a lista de opcoes para agrupamento
 */
	public enum Agrupamento {
		ESCRITORIO_CONTABIL("Escrit�rio cont�bil"),
		CNAE("CNAE"),
		MUNICIPIO("Munic�pio"),
		REPRESENTANTE("Representante");

		private String nome;

		private Agrupamento(String nome) {
			this.nome = nome;
		}

		public String getNome() {
			return nome;
		}
	}

	/*
	 * Constantes para montar a lista de opcoes para tipo de relatorio
	 */
	public enum TipoRelatorio {
		LISTAGEM("Listagem");
		/*
		 * TODO Nesta versao somente ha um tipo de relatorio.
		 * As outras opcoes sao para uma versao futura em que em uma mesma tela podera ser selecionado tipos diferentes de relatorios. 
		 */
//		CARTA_COBRANCA = 1;
//		ETIQUETA = 2;
//		LISTAGEM_EMAIL = 3;

		private String nome;

		private TipoRelatorio(String nome) {
			this.nome = nome;
		}

		public String getNome() {
			return nome;
		}
	}

	/*
	 * Constantes para montar a lista de campos a serem mostrados no relatorio
	 * A ordem do enum Campo e utilizada para a cria���o do relat�rio no Jasper/IReport
	 * Assim, se caso mudar a posicao desse enum, manter a correspondencia no arquivo
	 */
	public enum Campo {
		RAZAO_SOCIAL("Raz�o social/Nome", "Pessoa." + Pessoa.NOME),
		NOME_FANTASIA("Nome fantasia/Apelido", "Pessoa." + Pessoa.APELIDO),
		CPF_CNPJ("CPF/CNPJ", "Pessoa." + Pessoa.DOCUMENTO),
		MUNICIPIO("Munic�pio", "Endereco." + Endereco.MUNICIPIO),
		BAIRRO("Bairro", "Endereco." + Endereco.BAIRRO),
		LOGRADOURO("Logradouro", "Endereco." + Endereco.LOGRADOURO),
		NUMERO("N�mero", "Endereco." + Endereco.NUMERO),
		COMPLEMENTO("Complemento", "Endereco." + Endereco.COMPLEMENTO),
		CEP("CEP", "Endereco." + Endereco.CEP),
		CAIXA_POSTAL("Caixa postal", "Endereco." + Endereco.CAIXA_POSTAL),
		TELEFONE("Telefone", Pessoa.TELEFONES);

		private String nome;
		private String campo;

		private Campo(String nome, String campo) {
			this.nome = nome;
			this.campo = campo;
		}

		/* Monta a String para ser passada à clausula Group by pelo 
		 * metodo addGroup() da classe NativeSQL
		 * Não é possivel reutilizar o metodo toString da classe Campo,
		 * pois ha o tratamento para os alias para evitar
		 * ambiguidades de atributos na clausula select*/
		public static String setGroupBy(Integer[] index){
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < index.length; i++) {
				if (i > 0){
					builder.append(", ");
				}
				if (Campo.values()[index[i]].ordinal() == Campo.RAZAO_SOCIAL.ordinal()){
					builder.append("Pessoa.nome");
				}
				else if (Campo.values()[index[i]].ordinal() == Campo.MUNICIPIO.ordinal()){
					builder.append("Municipio.nome");
				}
				else if (Campo.values()[index[i]].ordinal() == Campo.BAIRRO.ordinal()){
					builder.append("Bairro.nome");
				}
				else if (Campo.values()[index[i]].ordinal() == Campo.LOGRADOURO.ordinal()){
					builder.append("Logradouro.nome");
				}
				else if (Campo.values()[index[i]].ordinal() == Campo.NUMERO.ordinal()){
					builder.append("Endereco.numero");
				}
				else{
					builder.append(Campo.values()[index[i]].getCampo());
				}
			}
			return builder.toString();
		}

		/* Retorna a String de campos que serão visualizados no relatório.*/
		public static String toString(Integer[] index) {
			StringBuilder builder = new StringBuilder();

			for (int i = 0; i < index.length; i++) {
				if (i > 0){
					builder.append(", ");
				}

				/* Se o nome, municipio, bairro e/ou logradouro forem selecionados na mesma consulta, 
				 * terá que ser mostrado o nome de cada campo, e não o id contido na
				 * entidade Endereço.*/
				if (Campo.values()[index[i]].ordinal() == Campo.RAZAO_SOCIAL.ordinal()){
					builder.append("Pessoa.nome as " + Pessoa.NOME);
				}
				else if (Campo.values()[index[i]].ordinal() == Campo.MUNICIPIO.ordinal()){
					builder.append("Municipio.nome as " + Endereco.MUNICIPIO);
				}
				else if (Campo.values()[index[i]].ordinal() == Campo.BAIRRO.ordinal()){
					builder.append("Bairro.nome as " + Endereco.BAIRRO);
				}
				else if (Campo.values()[index[i]].ordinal() == Campo.LOGRADOURO.ordinal()){
					builder.append("Logradouro.nome as " + Endereco.LOGRADOURO);
				}
				/* Ambas entidades Endereco e Telefone possuem o atributo numero. 
				 * Se ambos os campos (numero do endereco e numero de telefone) forem
				 * selecionados, dara erro de Sql pois o atributo numero e ambiguo, pois 
				 * esta na entidade Endereco e Telefone. */
				else if (Campo.values()[index[i]].ordinal() == Campo.NUMERO.ordinal()){
					builder.append("Endereco.numero as " + Endereco.NUMERO);
				}else if (Campo.values()[index[i]].ordinal() == Campo.TELEFONE.ordinal()){
//					builder.append("Telefone.numero as " + Pessoa.TELEFONES);
					/* Como uma pessoa pode ter varios telefones, sera exibido apenas um deles, que é do tipo Comercial (id=1)*/
					builder.append("(select concat(ddd, numero) from basic_telefone fone where fone.pessoa_id = Pessoa.id and fone.tipoTelefone = 1 limit 1) as telefones");
					/* "as telefones" pois Pessoa.TELEFONES = telefones, e é o que está contido na formação do enum campo, que é 
					 * utilizado para formar a consulta sql do relatorio*/
				}
				else{
					builder.append(Campo.values()[index[i]].getCampo());
				}
			}
			return builder.toString();
		}

		public String getNome() {
			return nome;
		}

		public String getCampo() {
			return campo;
		}
	}

	/* Como existe atributos definidos na clausula SELECT 
	 * (nao e apenas uma clausula do tipo SELECT *), 
	 * nao acontecera overhead na execucao do SQL, devido a
	 * projecao que o mySql realiza durante a consulta. 
	 * */
	public class QueryListagem {
		public static final String SQL_SELECT = "select :select from basic_pessoa Pessoa " +
		"inner join basic_contrato Contrato on Contrato.pessoa = Pessoa.id " +
		"inner join basic_endereco Endereco on Endereco.id = Pessoa.enderecoCorrespondencia "+
		"inner join basic_municipio Municipio on Municipio.id = Endereco.municipio "+
		"inner join basic_bairro Bairro on Bairro.id = Endereco.bairro "+
		"inner join basic_logradouro Logradouro on Logradouro.id = Endereco.logradouro "+
		"inner join financeiro_lancamento Lancamento on Contrato.id = Lancamento.contrato " +
		"inner join financeiro_lancamento_item LancamentoItem on LancamentoItem.lancamento = Lancamento.id " +
		"inner join financeiro_lancamento_movimento LancamentoMovimento on LancamentoMovimento.lancamento = Lancamento.id " ;
//		"inner join basic_telefone Telefone on Telefone.pessoa_id = Pessoa.id";
	}

	/* Monta o relatorio jasper dinamicamente de acordo com os parametros selecionados*/
	public void montarRelatorio(JasperDesign design, Campo campo){
		/* Variaveis para montar o layout do relatorio*/
		/* O relatorio apresentara a quantidade de campos que foram selecionados da tela.
		 * Assim, o laco itera sobre a quantidade de campos que estarao no relatorio*/

		/* Obtenho a lista de elementos do Jasper*/
		JRElement[] detailElements = design.getDetail().getElements();
		JRElement detailElement;

		/* Elementos da banda ColumnHeader do Jasper*/
		JRElement[] columnHeaderElements = design.getColumnHeader().getElements();
		JRElement columnHeaderElement;
		JRDesignStaticText staticField;

		JRDesignElement designElement;

		detailElement = detailElements[campo.ordinal()];
		designElement = (JRDesignElement) detailElements[campo.ordinal()];
		staticField = (JRDesignStaticText) columnHeaderElements[campo.ordinal()];
		/* A posicao dos elementos do columnHeader sao correspondentes
		 * aos elementos do Jasper.jrxml (RelatorioCadatro.jrxml)*/
		columnHeaderElement = columnHeaderElements[campo.ordinal()];

		detailElement.setX(posicaoX);
		columnHeaderElement.setX(posicaoX);
		staticField.setY(posicaoY);
		designElement.setY(posicaoY);
//		System.out.println("AAAAA posicao >> " + campo.getCampo() + " - " + (posicaoX + designElement.getWidth()) + " BBBBB tamanho >> " + relatorioTamanho );
		if(posicaoX + designElement.getWidth() > relatorioTamanho){
			posicaoX = 0;
			posicaoY = designElement.getHeight();
			mudarLinha = true;
			designElement.setX(posicaoX);
			designElement.setY(posicaoY);
			staticField = (JRDesignStaticText) columnHeaderElement;
			staticField.setY(posicaoY);
			staticField.setX(posicaoX);
		}
		else{
			posicaoX += detailElement.getWidth();
			alturaBanda = designElement.getHeight();
		}
	}
	/* Define a altura da banda ColumnHeader e Detail do relatorio Jasper
	 * Se a flag mudarLinha for true, então a altura da banda será a que está
	 * definida no arquivo .jrxml; se mudarLinha for false, então haverá apenas 
	 * uma linha para cada registro e a altura das bandas serão ajustadas para 
	 * o tamanho do elemento 
	 * */
	public void definirAlturaBanda(JasperDesign design){
		JRDesignBand detailBand= ((JRDesignBand) design.getDetail());
		JRDesignBand columnHeaderBand= ((JRDesignBand) design.getColumnHeader());
		if(mudarLinha){
			alturaBanda = detailBand.getHeight();
		}
		detailBand.setHeight(alturaBanda);
		columnHeaderBand.setHeight(alturaBanda);
	}

}

/*

select * from basic_pessoa Pessoa
inner join basic_contrato Contrato on Contrato.pessoa = Pessoa.id
inner join basic_endereco Endereco on Endereco.id = Pessoa.enderecoCorrespondencia
where Pessoa.documento = '02332390000122'
and Contrato.inativo = false
and Pessoa.escritorioContabil = 1
and Pessoa.cnae = 1
and Endereco.municipio = 1
and Pessoa.tipoEstabelecimento = 'Único'

 */