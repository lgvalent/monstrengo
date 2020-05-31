package br.com.orionsoft.financeiro.gerenciador.services;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.MaskFormatter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.basic.entities.commons.Frequencia;
import br.com.orionsoft.basic.entities.pessoa.EscritorioContabil;
import br.com.orionsoft.basic.entities.pessoa.Juridica;
import br.com.orionsoft.financeiro.utils.UtilsJuros;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.service.ServiceResultBean;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.core.util.NativeSQL;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Serviço que lista informações financeiras para o Relatório de Cobrança.
 * 
 * <p>
 * <b>Argumento:</b><br>
 * 
 * <p>
 * <b>Procedimento:</b>
 * 
 * @author Antonio Alves
 * @version 20070430
 * 
 * @spring.bean id="RelatorioCobrancaService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class RelatorioCobrancaService extends ServiceBasic {
	public enum RelatorioCobrancaModelo {
		RETRATO("Retrato"),
		PAISAGEM("Paisagem");
		
		private String nome;
		
		private RelatorioCobrancaModelo(String nome) {
			this.nome = nome;
		}
		
		public String getNome() {
			return nome;
		}
	}

	public class QueryRelatorioCobranca extends ServiceResultBean{
		public static final String SELECT_MASTER = "" +
		"select " +
		"  contrato.id contratoId, " +
		"  pessoa.id id, " +
		"  pessoa.nome nome, " +
		"  pessoa.documento documento, " +
		"  (select concat(ddd, numero) from basic_telefone fone where fone.pessoa_id = pessoa.id and fone.tipoTelefone = 1 limit 1) telefone, " +
		"  logradouro.tipoLogradouro enderecoTipoLogradouro, " +
		"  logradouro.nome enderecoNomeLogradouro, " +
		"  endereco.numero enderecoNumero, " +
		"  endereco.complemento enderecoComplemento, " +
		"  endereco.cep enderecoCep, " +
		"  municipio.nome enderecoMunicipio, " +
		"  municipio.uf enderecoUF, " +
		"  pessoa.email email, " +
		"  lancamento.id idLancamento, " +
		"  lancamento.descricao descricao, " +
		"  lancamento.data data, " +	
		"  lancamento.dataVencimento dataVencimento, " +
		"  lancamento.saldo valorOriginal, " +
		"  lancamento_item.itemCusto idItemCusto, " +
		"  documento_cobranca_categoria.nome nomeDocumentoCobrancaCategoria, " +
		"  documento_cobranca_categoria.multaAtraso multa, " +
		"  documento_cobranca_categoria.jurosMora juros, " +
		"  documento_cobranca_categoria.descontoAntecipacao desconto, " +
		"  documento_cobranca_categoria.diasToleranciaMultaAtraso diasTolerancia, " +
		"  (:slave) as qtd " +
		"from " + 
		"  financeiro_lancamento lancamento " +
		"inner join basic_contrato contrato on ( " +
		"  contrato.id = lancamento.contrato) " +
		"inner join basic_pessoa pessoa on ( " +
		"  pessoa.id = contrato.pessoa) " +
		"left outer join basic_endereco endereco on ( " +
		"  endereco.id = pessoa.enderecoCorrespondencia) " +
		"left outer join basic_cnae cnae on ( " +
		"  cnae.id = pessoa.cnae) " +
		"left outer join basic_logradouro logradouro on " +
		"  logradouro.id = endereco.logradouro " +
		"left outer join basic_municipio municipio on ( " +
		"  municipio.id = endereco.municipio) " +
		"left outer join financeiro_lancamento_item lancamento_item on ( " +
		"  lancamento_item.lancamento = lancamento.id) " +
		"left outer join financeiro_documento_cobranca documento_cobranca on ( " +
		"  lancamento.documentoCobranca = documento_cobranca.id) " +
		"left outer join financeiro_documento_cobranca_categoria documento_cobranca_categoria on ( " +
		"  documento_cobranca_categoria.id = documento_cobranca.documentoCobrancaCategoria) ";
		
		public static final String WHERE_MASTER = "" +
			"where " +
			"	  (lancamento.saldo > 0)";

		public static final String SELECT_SLAVE = "" +
		"select " +
		"  count(*) " +
		"from " + 
		"  financeiro_lancamento lancamento " +
		"inner join basic_contrato fcontrato on ( " +
		"  fcontrato.id = lancamento.contrato) " +
		"inner join basic_pessoa pessoa on ( " +
		"  pessoa.id = fcontrato.pessoa) " +
		"left outer join basic_cnae cnae on ( " +
		"  cnae.id = pessoa.cnae) " +
		"left outer join basic_endereco endereco on ( " +
		"  endereco.id = pessoa.enderecoCorrespondencia) " +
		"left outer join financeiro_lancamento_item lancamento_item on ( " +
		"  lancamento_item.lancamento = lancamento.id) ";

		public static final String WHERE_SLAVE = "" +
			"where " +
			"     (lancamento.contrato = contrato.id) and " +
			"	  (lancamento.saldo > 0)";
		
		public static final String ORDER = "" +
			"order by	" +
			"	  pessoa.nome,	" +
			"	  pessoa.documento,	" +
			"	  lancamento.dataVencimento";
		
		public static final String SELECT_PAGOS = "" +
		"select " +
		"  distinct bc.pessoa " +
		"from " +
		"  financeiro_lancamento_movimento flm " +
		"inner join financeiro_lancamento fl " +
		"  on flm.lancamento = fl.id " +
		"inner join basic_contrato bc " +
		"  on fl.contrato = bc.id ";

		public static final String WHERE_PAGOS = "" +
			"where " +
			"     (flm.lancamentoMovimentoCategoria = 'QUITADO') and " +
			"     (!flm.estornado)";

		private Long contratoId;
		private Long id;
		private String nome;
		private String documento;
		private String telefone;
		private String endereco;
		private String email;
		private String descricao;
		private Date data;
		private Date dataVencimento;
		private Long idItemCusto;
		private BigDecimal valorOriginal;
		private BigDecimal valorMulta;
		private BigDecimal valorJuros;
		private BigDecimal valorCorrigido;

		public QueryRelatorioCobranca(
				Long contratoId, 
				Long id, 
				String nome, 
				String documento, 
				String telefone, 
				String endereco, 
				String email, 
				String descricao, 
				Date data, 
				Date dataVencimento, 
				BigDecimal valorOriginal, 
				Long idItemCusto, 
				BigDecimal valorMulta, 
				BigDecimal valorJuros, 
				BigDecimal valorCorrigido
				) {
			super();
			this.contratoId = contratoId;
			this.id = id;
			this.nome = nome;
			this.documento = documento;
			this.telefone = telefone;
			this.endereco = endereco;
			this.email= email;
			this.descricao = descricao;
			this.data = data;
			this.dataVencimento = dataVencimento;
			this.valorOriginal = valorOriginal;
			this.idItemCusto = idItemCusto;
			this.valorMulta = valorMulta;
			this.valorJuros = valorJuros;
			this.valorCorrigido = valorCorrigido;
		}
		
		public Date getData() {
			return data;
		}

		public Date getDataVencimento() {
			return dataVencimento;
		}

		public String getDescricao() {
			return descricao;
		}

		public String getDocumento() {
			return documento;
		}

		public String getNome() {
			return nome;
		}

		public String getTelefone() {
			return telefone;
		}

		public BigDecimal getValorOriginal() {
			return valorOriginal;
		}
		
		public BigDecimal getValorMulta() {
			return valorMulta;
		}
		
		public BigDecimal getValorJuros() {
			return valorJuros;
		}

		public Long getContratoId() {
			return contratoId;
		}

		public Long getId() {
			return id;
		}

		public BigDecimal getValorCorrigido() {
			return valorCorrigido;
		}

		public String getEndereco() {
			return endereco;
		}

		public String getEmail() {
			return email;
		}

		public Long getIdItemCusto() {
			return idItemCusto;
		}
	}

	public static final String SERVICE_NAME = "RelatorioCobrancaService";
	public static final int TIPO_CONTRATO_TODOS = 0;
	public static final int TIPO_CONTRATO_ATIVOS = 1;
	public static final int TIPO_CONTRATO_INATIVOS = 2;
	public static final String IN_ITEM_CUSTO_ID_LIST = "itemCustoIdList";
	public static final String IN_ITEM_CUSTO_NOT = "itemCustoNot";
	public static final String IN_ESCRITORIO_CONTABIL_ID_LIST = "escritorioContabilIdList";
	public static final String IN_CATEGORIA_CONTRATO_ID = "categoriaContratoId";
	public static final String IN_TIPO_CONTRATO = "tipoContrato";
	public static final String IN_DATA_LANCAMENTO_INICIAL = "dataLancamentoInicial";
	public static final String IN_DATA_LANCAMENTO_FINAL = "dataLancamentoFinal";
	public static final String IN_DATA_VENCIMENTO_INICIAL = "dataVencimentoInicial";
	public static final String IN_DATA_VENCIMENTO_FINAL = "dataVencimentoFinal";
	public static final String IN_DATA_PAGAMENTO_OPT = "dataPagamento";
	public static final String IN_QUANTIDADE_ITENS_INICIAL = "quantidadeItensInicial";
	public static final String IN_QUANTIDADE_ITENS_FINAL = "quantidadeItensFinal";
	public static final String IN_QUANTIDADE_ITENS_PAGOS_INICIAL = "quantidadeItensPagosInicial";
	public static final String IN_QUANTIDADE_ITENS_PAGOS_FINAL = "quantidadeItensPagosFinal";
	public static final String IN_POSSUI_ITENS_PAGO = "possuiItensPago";
	public static final String IN_CPF_CNPJ_OPT = "cpfCnpj";
	public static final String IN_MUNICIPIO_ID_OPT = "municipio";
	public static final String IN_NOT_MUNICIPIO_OPT = "notMunicipio";
	public static final String IN_CNAE_ID_OPT = "cnae";
	public static final String IN_CNAE_DESCRICAO_OPT = "cnaeDescricao";
//	public static final String IN_ESCRITORIO_ID_OPT = "escritorio";
	public static final String IN_CONTRATO_REPRESENTANTE_ID_OPT = "contratoReprensentanteId";
	public static final String IN_OMITIR_VALORES = "omitirValores";
	public static final String IN_OUTPUT_STREAM = "outputStream";
	public static final String IN_RELATORIO_COBRANCA_MODELO = "relatorioCobrancaModelo";

	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("Iniciando a execução do serviço RelatorioCobrancaService");
		log.debug("Preparando os argumentos.");
		
		/*
		 * Parâmetros obrigatórios
		 */
		Long[] inItemCustoIdList = (Long[]) serviceData.getArgumentList().getProperty(IN_ITEM_CUSTO_ID_LIST);
		Boolean inNotItemCusto = (Boolean) serviceData.getArgumentList().getProperty(IN_ITEM_CUSTO_NOT);
		Long inCategoriaContratoId = (Long) serviceData.getArgumentList().getProperty(IN_CATEGORIA_CONTRATO_ID);
		int inTipoContrato = (Integer) serviceData.getArgumentList().getProperty(IN_TIPO_CONTRATO);
		Calendar inDataLancamentoInicial = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_LANCAMENTO_INICIAL);
		Calendar inDataLancamentoFinal = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_LANCAMENTO_FINAL);
		Calendar inDataVencimentoInicial = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO_INICIAL);
		Calendar inDataVencimentoFinal = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO_FINAL);
		Integer inQuantidadeItensInicial = (Integer) serviceData.getArgumentList().getProperty(IN_QUANTIDADE_ITENS_INICIAL);
		Integer inQuantidadeItensFinal = (Integer) serviceData.getArgumentList().getProperty(IN_QUANTIDADE_ITENS_FINAL);
//		Integer inQuantidadeItensPagosInicial = (Integer) serviceData.getArgumentList().getProperty(IN_QUANTIDADE_ITENS_PAGOS_INICIAL);
//		Integer inQuantidadeItensPagosFinal = (Integer) serviceData.getArgumentList().getProperty(IN_QUANTIDADE_ITENS_PAGOS_FINAL);
		Boolean inPossuiItensPagos = (Boolean) serviceData.getArgumentList().getProperty(IN_POSSUI_ITENS_PAGO);
		String inCpfCnpj = serviceData.getArgumentList().containsProperty(IN_CPF_CNPJ_OPT)?(String) serviceData.getArgumentList().getProperty(IN_CPF_CNPJ_OPT):null;
		OutputStream inOutputStream = (OutputStream) serviceData.getArgumentList().getProperty(IN_OUTPUT_STREAM);
		RelatorioCobrancaModelo inRelatorioCobrancaModelo = (RelatorioCobrancaModelo) serviceData.getArgumentList().getProperty(IN_RELATORIO_COBRANCA_MODELO);
		
		/*
		 * Parâmetros opcionais
		 */
		Long inCnaeId = (serviceData.getArgumentList().containsProperty(IN_CNAE_ID_OPT) ?
				(Long) serviceData.getArgumentList().getProperty(IN_CNAE_ID_OPT) : null);
		String inCnaeDescricao = (serviceData.getArgumentList().containsProperty(IN_CNAE_DESCRICAO_OPT) ?
				(String) serviceData.getArgumentList().getProperty(IN_CNAE_DESCRICAO_OPT) : null);
		Long inMunicipioId = (serviceData.getArgumentList().containsProperty(IN_MUNICIPIO_ID_OPT) ?
				(Long) serviceData.getArgumentList().getProperty(IN_MUNICIPIO_ID_OPT) : IDAO.ENTITY_UNSAVED);
		Boolean inNotMunicipio = (serviceData.getArgumentList().containsProperty(IN_NOT_MUNICIPIO_OPT) ?
				(Boolean) serviceData.getArgumentList().getProperty(IN_NOT_MUNICIPIO_OPT) : false);
		Long[] inEscritorioContabilIdList = (serviceData.getArgumentList().containsProperty(IN_ESCRITORIO_CONTABIL_ID_LIST) ?
				(Long[]) serviceData.getArgumentList().getProperty(IN_ESCRITORIO_CONTABIL_ID_LIST) : null);
		Long inContratoRepresentanteId = (serviceData.getArgumentList().containsProperty(IN_CONTRATO_REPRESENTANTE_ID_OPT) ?
				(Long) serviceData.getArgumentList().getProperty(IN_CONTRATO_REPRESENTANTE_ID_OPT) : IDAO.ENTITY_UNSAVED);
		Boolean inOmitirValores = (serviceData.getArgumentList().containsProperty(IN_OMITIR_VALORES) ?
				(Boolean) serviceData.getArgumentList().getProperty(IN_OMITIR_VALORES) : false);
		Calendar inDataPagamento = (serviceData.getArgumentList().containsProperty(IN_DATA_PAGAMENTO_OPT) ?
				(Calendar) serviceData.getArgumentList().getProperty(IN_DATA_PAGAMENTO_OPT) : CalendarUtils.getCalendar());
		
		log.debug("Montando SQL para busca dos dados.");
		/* SQL Slave */
		NativeSQL sqlSlave = new NativeSQL(
				serviceData.getCurrentSession(), 
				QueryRelatorioCobranca.SELECT_SLAVE, 
				QueryRelatorioCobranca.WHERE_SLAVE, 
				null,
				null,
				null);
		if (inCnaeId != null)
			sqlSlave.addWhere("(pessoa.cnae = "+inCnaeId+")");
		StringBuffer cnaeDescricaoSql = new StringBuffer("(");
		if (StringUtils.isNotBlank(inCnaeDescricao)){
			String[] descricoes = inCnaeDescricao.replace(", ", ",").split(",");
			int i = 0;
			for(String desc: descricoes){
				if (i>0)
					cnaeDescricaoSql.append(" OR ");
				cnaeDescricaoSql.append("(cnae.nome like '%"+desc+"%')");
				i++;
			}
			cnaeDescricaoSql.append(")");
			sqlSlave.addWhere(cnaeDescricaoSql.toString());
		}
		if (inMunicipioId != IDAO.ENTITY_UNSAVED)
			if (inNotMunicipio)
				sqlSlave.addWhere("(endereco.municipio != "+inMunicipioId+")");
			else
				sqlSlave.addWhere("(endereco.municipio = "+inMunicipioId+")");
		if (!ArrayUtils.isEmpty(inEscritorioContabilIdList))
			sqlSlave.addWhere("(pessoa.escritorioContabil in ("+ br.com.orionsoft.monstrengo.core.util.StringUtils.toString(inEscritorioContabilIdList)+"))");
		if (inContratoRepresentanteId != IDAO.ENTITY_UNSAVED)
			sqlSlave.addWhere("(contrato.representante = "+inContratoRepresentanteId+")");
		if (!ArrayUtils.isEmpty(inItemCustoIdList))
				sqlSlave.addWhere("(lancamento_item.itemCusto "+ (inNotItemCusto?"not":"") +" in ("+br.com.orionsoft.monstrengo.core.util.StringUtils.toString(inItemCustoIdList)+"))");
		if (inCategoriaContratoId != IDAO.ENTITY_UNSAVED)
			sqlSlave.addWhere("(fcontrato.categoria = "+inCategoriaContratoId+")");
		if (inDataLancamentoInicial != null && inDataLancamentoFinal != null)
			sqlSlave.addWhere("(lancamento.data between '"+CalendarUtils.formatToSQLDate(inDataLancamentoInicial)+"' and '"+CalendarUtils.formatToSQLDate(inDataLancamentoFinal)+"')");
		if (inDataVencimentoInicial != null && inDataVencimentoFinal != null)
			sqlSlave.addWhere("(documento_cobranca.dataVencimento between '"+CalendarUtils.formatToSQLDate(inDataVencimentoInicial)+"' and '"+CalendarUtils.formatToSQLDate(inDataVencimentoFinal)+"')");
		if (StringUtils.isNotBlank(inCpfCnpj))
			/* CNPJ, inclusive parciais para pegar todas as filiais. */
			sqlSlave.addWhere("(pessoa.documento like '"+inCpfCnpj+"%')");
		switch (inTipoContrato) {
		case TIPO_CONTRATO_ATIVOS:
			sqlSlave.addWhere("(fcontrato.inativo = false)");
			break;

		case TIPO_CONTRATO_INATIVOS:
			sqlSlave.addWhere("(fcontrato.inativo = true)");
			break;

		default:
			break;
		}

		/* SQL Master */
		String having = "";
		if (inQuantidadeItensInicial != null && inQuantidadeItensFinal != null){
			if(inQuantidadeItensInicial > 0 || inQuantidadeItensFinal > 0)
				having = "having qtd between ".concat(Integer.toString(inQuantidadeItensInicial)).concat(" and ").concat(Integer.toString(inQuantidadeItensFinal));
		}

		NativeSQL sqlMaster = new NativeSQL(
				serviceData.getCurrentSession(), 
				QueryRelatorioCobranca.SELECT_MASTER, 
				QueryRelatorioCobranca.WHERE_MASTER, 
				having,
				null,
				QueryRelatorioCobranca.ORDER);
		if (inCnaeId != null)
			sqlMaster.addWhere("(pessoa.cnae = "+inCnaeId+")");
		if (StringUtils.isNotBlank(inCnaeDescricao))
			sqlMaster.addWhere(cnaeDescricaoSql.toString());
		if (inMunicipioId != IDAO.ENTITY_UNSAVED)
			if (inNotMunicipio)
				sqlMaster.addWhere("(endereco.municipio != "+inMunicipioId+")");
			else
				sqlMaster.addWhere("(endereco.municipio = "+inMunicipioId+")");
		String escritorio = "";
		if (!ArrayUtils.isEmpty(inEscritorioContabilIdList)) {
			sqlMaster.addWhere("(pessoa.escritorioContabil in ("+ br.com.orionsoft.monstrengo.core.util.StringUtils.toString(inEscritorioContabilIdList)+"))");
			try {
				EscritorioContabil ec = UtilsCrud.objectRetrieve(this.getServiceManager(), EscritorioContabil.class, inEscritorioContabilIdList[0], null);
				escritorio = ec.getPessoa().getNome();
			} catch (Exception e) {
			}
		}
		if (inContratoRepresentanteId != IDAO.ENTITY_UNSAVED)
			sqlMaster.addWhere("(contrato.representante = "+inContratoRepresentanteId+")");
		if (!ArrayUtils.isEmpty(inItemCustoIdList))
				sqlMaster.addWhere("(lancamento_item.itemCusto "+ (inNotItemCusto?"not":"") +" in ("+br.com.orionsoft.monstrengo.core.util.StringUtils.toString(inItemCustoIdList)+"))");
		if (inCategoriaContratoId != IDAO.ENTITY_UNSAVED)
			sqlMaster.addWhere("(contrato.categoria = "+inCategoriaContratoId+")");
		if (inDataLancamentoInicial != null && inDataLancamentoFinal != null)
			sqlMaster.addWhere("(lancamento.data between '"+CalendarUtils.formatToSQLDate(inDataLancamentoInicial)+"' and '"+CalendarUtils.formatToSQLDate(inDataLancamentoFinal)+"')");
		if (inDataVencimentoInicial != null && inDataVencimentoFinal != null)
			sqlMaster.addWhere("(lancamento.dataVencimento between '"+CalendarUtils.formatToSQLDate(inDataVencimentoInicial)+"' and '"+CalendarUtils.formatToSQLDate(inDataVencimentoFinal)+"')");
		if (StringUtils.isNotBlank(inCpfCnpj))
			/* CNPJ, inclusive parciais para pegar todas as filiais. */
			sqlMaster.addWhere("(pessoa.documento like '"+inCpfCnpj+"%')");
		switch (inTipoContrato) {
		case TIPO_CONTRATO_ATIVOS:
			sqlMaster.addWhere("(contrato.inativo = false)");
			break;

		case TIPO_CONTRATO_INATIVOS:
			sqlMaster.addWhere("(contrato.inativo = true)");
			break;

		default:
			break;
		}
		
		/* SQL Pagos */
//		if (inQuantidadeItensPagosInicial != null && inQuantidadeItensPagosFinal != null){
		if (inPossuiItensPagos){
//			String havingPagos = "having count(*) between ".concat(Integer.toString(inQuantidadeItensPagosInicial)).concat(" and ").concat(Integer.toString(inQuantidadeItensPagosFinal));
			NativeSQL sqlPagos = new NativeSQL(
					serviceData.getCurrentSession(), 
					QueryRelatorioCobranca.SELECT_PAGOS, 
					QueryRelatorioCobranca.WHERE_PAGOS, 
					null, //havingPagos,
					null, //" group by bc.pessoa ",
					null);
			
			if (inDataLancamentoInicial != null && inDataLancamentoFinal != null)
				sqlPagos.addWhere("(fl.data between '"+CalendarUtils.formatToSQLDate(inDataLancamentoInicial)+"' and '"+CalendarUtils.formatToSQLDate(inDataLancamentoFinal)+"')");
		
			//  Integra à SQL Master
			sqlMaster.addWhere(" (pessoa.id in (" + sqlPagos.getSql() + ") )");
		}
		
		sqlMaster.setParameter("slave", sqlSlave.getSql());
		
		try {
			log.debug("Preparando mascará dos campos Documento e Telefone.");
			MaskFormatter mfDocumento = new MaskFormatter(this.getServiceManager().getEntityManager().getEntityMetadata(Juridica.class).getPropertyMetadata(Juridica.DOCUMENTO).getEditMask());
			mfDocumento.setValueContainsLiteralCharacters(false);
			mfDocumento.setAllowsInvalid(true);

			log.debug("Executando a SQL.");

			MaskFormatter mfTelefone = new MaskFormatter("(##)#####-####");
			mfTelefone.setValueContainsLiteralCharacters(false);
			mfTelefone.setAllowsInvalid(true);
			
			log.debug("Executando a SQL.");
			System.out.println(sqlMaster.getSql());
			ResultSet rs = sqlMaster.executeQuery();
			List<QueryRelatorioCobranca> list = new ArrayList<QueryRelatorioCobranca>();
			Calendar data = CalendarUtils.getCalendar();
			while (rs.next()) {
				BigDecimal multaAdicional = DecimalUtils.ZERO;
				/* TODO Multa adicional de 2% para GRCSU */
				if (rs.getString("nomeDocumentoCobrancaCategoria") != null &&
					rs.getString("nomeDocumentoCobrancaCategoria").equals("GRCSU")) {
					multaAdicional = DecimalUtils.getBigDecimal(2.0);
				}
				Calendar dataVencimento = CalendarUtils.getCalendar(rs.getDate("dataVencimento"));
				Calendar dataPagamento = inDataPagamento;
				BigDecimal valorOriginal = rs.getBigDecimal("valorOriginal");
				BigDecimal valorMulta = UtilsJuros.calcularMulta(valorOriginal, rs.getBigDecimal("multa"), multaAdicional, Frequencia.MENSAL, dataVencimento, dataPagamento, rs.getInt("diasTolerancia"));				
				BigDecimal valorJuros = UtilsJuros.calcularJuros(valorOriginal, rs.getBigDecimal("juros"), dataVencimento, dataPagamento, rs.getInt("diasTolerancia"));				
				BigDecimal valorCorrigido = valorOriginal.add(valorMulta).add(valorJuros);				
				String telefone = "";
				if (rs.getString("telefone") != null){
					try{
						telefone = mfTelefone.valueToString(rs.getString("telefone"));
					}catch(ParseException e){
						telefone = rs.getString("telefone");
					}
				}
				StringBuilder endereco = new StringBuilder();
				if (rs.getString("enderecoNomeLogradouro") != null)
					endereco.append(rs.getString("enderecoTipoLogradouro")).append(" ")
						.append(rs.getString("enderecoNomeLogradouro")).append(", ")
						.append(rs.getString("enderecoNumero")).append(" | ")
						.append(rs.getString("enderecoMunicipio")).append(" - ")
						.append(rs.getString("enderecoUF"));
				
				QueryRelatorioCobranca query = new QueryRelatorioCobranca(
						rs.getLong("contratoId"),
						rs.getLong("id"),
						rs.getString("nome"),
						mfDocumento.valueToString(rs.getString("documento")),
						telefone,
						endereco.toString(),
						rs.getString("email"),
						rs.getString("descricao"),
						rs.getDate("data", data),
						rs.getDate("dataVencimento", data),
						(inOmitirValores ? null : valorOriginal),
						rs.getLong("idItemCusto"),
						(inOmitirValores ? null : valorMulta),
						(inOmitirValores ? null : valorJuros),
						(inOmitirValores ? null : valorCorrigido));
				list.add(query);
			}
			
			if(log.isDebugEnabled()) log.debug("Registros selecionados para cobrança # " + list.size());			
			serviceData.getOutputData().add(list);
			
			
			log.debug("Compilando o relatório.");
	        Map<String, String> parametros = new HashMap<String, String>();
	        parametros.put("Memo1", "Impresso em " + CalendarUtils.formatDateTime(Calendar.getInstance()));
	        parametros.put("Memo2", "Valores calculados para pagamento até " + CalendarUtils.formatDate(inDataPagamento));
	        parametros.put("Memo3", escritorio);

	        JasperReport relatorio = null;
	        if(inRelatorioCobrancaModelo == RelatorioCobrancaModelo.RETRATO)
	        	relatorio = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioCobrancaRetrato.jrxml"));
	        else
	        	relatorio = JasperCompileManager.compileReport(getClass().getResourceAsStream("RelatorioCobrancaPaisagem.jrxml"));
	        	
	        if (inOutputStream != null) {
	        	log.debug("Imprimindo o relatório.");
	        	JasperPrint print = JasperFillManager.fillReport(relatorio, parametros, new JRBeanCollectionDataSource(list));
	        	JasperExportManager.exportReportToPdfStream(print, inOutputStream);
	        }
	        
		} catch (SQLException e) {
            log.fatal(e.getMessage());
            /* Indica que o serviço falhou por causa de uma exceção da SQL. */
            throw new ServiceException(MessageList.createSingleInternalError(e));
		} catch (JRException e) {
            /* Indica que o serviço falhou por causa de uma exceção do Jasper. */
            throw new ServiceException(MessageList.createSingleInternalError(e));
		} catch (MetadataException e) {
            /* Indica que o serviço falhou por causa de uma exceção de Metadata. */
            throw new ServiceException(MessageList.createSingleInternalError(e));
		} catch (EntityException e) {
            /* Indica que o serviço falhou por causa de uma exceção de Entity. */
            throw new ServiceException(MessageList.createSingleInternalError(e));
		} catch (ParseException e) {
            /* Indica que o serviço falhou por causa de uma exceção de Parser da função MaskFormatter. */
            throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

}
