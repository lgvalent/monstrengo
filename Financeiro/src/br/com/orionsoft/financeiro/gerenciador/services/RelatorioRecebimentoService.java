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

import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.basic.entities.endereco.Municipio;
import br.com.orionsoft.basic.entities.pessoa.Juridica;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.NativeSQL;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.metadata.MetadataException;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;
import br.com.orionsoft.monstrengo.security.entities.ApplicationUser;

/**
 * Serviço que lista informações financeiras para o Relatório de Cobrança.
 * 
 * <p>
 * <b>Argumento:</b><br>
 * 
 * <p>
 * <b>Procedimento:</b>
 * 
 * @author Lucio
 * @version 20070808
 * 
 * @spring.bean id="RelatorioRecebimentoService" init-method="registerService"
 * @spring.property name="serviceManager" ref="ServiceManager"
 */
public class RelatorioRecebimentoService extends ServiceBasic {
	public static final String SERVICE_NAME = "RelatorioRecebimentoService";

	public class QueryRelatorioRecebimento {
		public static final String SELECT_MASTER = "" +
		"select " +
		"  pessoa.nome nome, " +
		"  pessoa.documento documento, " +
		"  (select concat(ddd, numero) from basic_telefone fone where fone.pessoa_id = pessoa.id and fone.tipoTelefone = 1 limit 1) telefone, " +
		"  lancamento.descricao descricao, " +
		"  lancamento.dataVencimento dataVencimento, " +
		"  lancamento_movimento.data dataRecebimento, " +
		"  lancamento.valor valorLancamento, " +
		"  lancamento_movimento.valor valor, " +
		"  (:slave) as qtd " +
		"from " + 
		"  financeiro_lancamento lancamento " +
		"inner join financeiro_lancamento_movimento lancamento_movimento on ( " +
		"  lancamento.id = lancamento_movimento.lancamento) " +
		"inner join basic_contrato contrato on ( " +
		"  contrato.id = lancamento.contrato) " +
		"inner join basic_pessoa pessoa on ( " +
		"  pessoa.id = contrato.pessoa) " +
		"left outer join basic_endereco endereco on ( " +
		"  endereco.id = pessoa.enderecoCorrespondencia) " +
		"left outer join financeiro_lancamento_item lancamento_item on ( " +
		"  lancamento_item.lancamento = lancamento.id) ";
//		"left outer join financeiro_item_custo itemCusto on ( " +
//		"  itemCusto.id = lancamento_item.itemCusto) ";
		
//			"select " +
//			"  pessoa.nome nome, " +
//			"  pessoa.documento documento, " +
//			"  (select concat(ddd, numero) from basic_telefone fone where fone.pessoa_id = pessoa.id and fone.tipoTelefone = 1 limit 1) telefone, " +
//			"  descricao.nome descricao, " +
//			"  grupo.dataVencimento dataVencimento, " +
//			"  movimento.dataVencimento dataRecebimento, " +
//			"  grupo.valor valorGrupo, " +
//			"  movimento.valor valor, " +
//			"  (:slave) as qtd " +
//			"from " + 
//			"  financeiro_grupo grupo " +
//			"inner join financeiro_movimento movimento on ( " +
//			"  grupo.id = movimento.grupo) " +
//			"inner join basic_contrato contrato on ( " +
//			"  contrato.id = grupo.contrato) " +
//			"inner join basic_pessoa pessoa on ( " +
//			"  pessoa.id = contrato.pessoa) " +
//			"inner join basic_endereco endereco on ( " +
//			"  endereco.id = pessoa.enderecoCorrespondencia) " +
//			"left outer join financeiro_descricao descricao on ( " +
//			"  descricao.id = grupo.descricao) " +
//			"left outer join financeiro_lancamento lancamento on ( " +
//			"  lancamento.grupo = grupo.id) " +
//			"left outer join financeiro_item_custo itemCusto on ( " +
//			"  itemCusto.id = lancamento.itemCusto) ";
		public static final String WHERE_MASTER = "" +
			"where " +
			"     (lancamento_movimento.lancamentoMovimentoCategoria = 'QUITADO') ";

		public static final String SELECT_SLAVE = "" +
		"select " +
		"  count(*) " +
		"from " + 
		"  financeiro_lancamento flancamento " +
		"inner join financeiro_lancamento_movimento lancamento_movimento on ( " +
		"  flancamento.id = lancamento_movimento.lancamento) " +
		"inner join basic_contrato fcontrato on ( " +
		"  fcontrato.id = flancamento.contrato) " +
		"inner join basic_pessoa pessoa on ( " +
		"  pessoa.id = fcontrato.pessoa) " +
		"inner join basic_endereco endereco on ( " +
		"  endereco.id = pessoa.enderecoCorrespondencia) " +
		"left outer join financeiro_lancamento_item lancamento_item on ( " +
		"  lancamento_item.lancamento = flancamento.id) ";
//		"left outer join financeiro_item_custo itemCusto on ( " +
//		"  itemCusto.id = lancamento_item.itemCusto) ";
		
//		"select " +
//		"  count(*) " +
//		"from " + 
//		"  financeiro_grupo fgrupo " +
//		"inner join financeiro_movimento movimento on ( " +
//		"  fgrupo.id = movimento.grupo) " +
//		"inner join basic_contrato fcontrato on ( " +
//		"  fcontrato.id = fgrupo.contrato) " +
//		"inner join basic_pessoa pessoa on ( " +
//		"  pessoa.id = fcontrato.pessoa) " +
//		"inner join basic_endereco endereco on ( " +
//		"  endereco.id = pessoa.enderecoCorrespondencia) " +
//		"left outer join financeiro_descricao descricao on ( " +
//		"  descricao.id = fgrupo.descricao) " +
//		"left outer join financeiro_lancamento lancamento on ( " +
//		"  lancamento.grupo = fgrupo.id) " +
//		"left outer join financeiro_item_custo itemCusto on ( " +
//		"  itemCusto.id = lancamento.itemCusto) ";
		public static final String WHERE_SLAVE = "" +
			"where " +
			"     (flancamento.contrato = contrato.id) and " +
			"     (lancamento_movimento.lancamentoMovimentoCategoria = 'QUITADO') ";
		
		public static final String ORDER = "" +
			"order by	" +
			"	  pessoa.nome,	" +
			"	  pessoa.documento,	" +
			"	  lancamento.dataVencimento";
		
		public static final String GROUP = "" +
				"group by " +
				"  ";
		
		private String nome;
		private String documento;
		private String telefone;
		private String descricao;
		private Date dataVencimento;
		private Date dataRecebimento;
		private BigDecimal valorGrupo;
		private BigDecimal valor;

		public QueryRelatorioRecebimento(String nome, String documento, String telefone, String descricao, Date dataVencimento, Date dataRecebimento, BigDecimal valorGrupo, BigDecimal valor) {
			super();
			this.nome = nome;
			this.documento = documento;
			this.telefone = telefone;
			this.descricao = descricao;
			this.dataVencimento = dataVencimento;
			this.dataRecebimento = dataRecebimento;
			this.valorGrupo = valorGrupo;
			this.valor = valor;
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

		public BigDecimal getValor() {
			return valor;
		}

		public Date getDataRecebimento() {
			return dataRecebimento;
		}

		public BigDecimal getValorGrupo() {
			return valorGrupo;
		}
	}

	public static final String IN_ITEM_CUSTO_ID_LIST = "itemCustoIdList";
	public static final String IN_ITEM_CUSTO_NOT = "itemCustoNot";
	public static final String IN_ESCRITORIO_CONTABIL_ID_LIST = "escritorioContabilIdList";
	public static final String IN_CATEGORIA_CONTRATO_ID = "categoriaContratoId";
	public static final String IN_TIPO_CONTRATO = "tipoContrato";
//	public static final String IN_DATA_LANCAMENTO_INICIAL = "dataLancamentoInicial";
//	public static final String IN_DATA_LANCAMENTO_FINAL = "dataLancamentoFinal";
	public static final String IN_DATA_VENCIMENTO_INICIAL = "dataVencimentoInicial";
	public static final String IN_DATA_VENCIMENTO_FINAL = "dataVencimentoFinal";
	public static final String IN_DATA_RECEBIMENTO_INICIAL = "dataRecebimentoInicial";
	public static final String IN_DATA_RECEBIMENTO_FINAL = "dataRecebimentoFinal";
	public static final String IN_QUANTIDADE_ITENS_INICIAL = "quantidadeItensInicial";
	public static final String IN_QUANTIDADE_ITENS_FINAL = "quantidadeItensFinal";
	public static final String IN_CPF_CNPJ = "cpfCnpj";
	public static final String IN_MUNICIPIO_ID_OPT = "municipio";
	public static final String IN_NOT_MUNICIPIO_OPT = "notMunicipio";
	public static final String IN_CONTA_ID_OPT = "conta";
	public static final String IN_CONTRATO_REPRESENTANTE_ID_OPT = "contratoReprensentanteId";
	public static final String IN_OUTPUT_STREAM = "outputStream";
    public static final String IN_APPLICATION_USER_OPT = "applicationUser";
	
	public enum TipoContrato {
		TODOS("Todos"),
		ATIVOS("Ativos"),
		INATIVOS("Inativos");
		
		private String nome;

		public String getNome() {
			return nome;
		}

		private TipoContrato(String nome) {
			this.nome = nome;
		}
		
	}

	public void execute(ServiceData serviceData) throws ServiceException {
		log.debug("Iniciando a execução do serviço RelatorioRecebimentoService");
		log.debug("Preparando os argumentos.");
		
		/*
		 * Parâmetros obrigatórios
		 */
		String inItemCustoIdList = (String) serviceData.getArgumentList().getProperty(IN_ITEM_CUSTO_ID_LIST);
		Boolean inNotItemCusto = (Boolean) serviceData.getArgumentList().getProperty(IN_ITEM_CUSTO_NOT);
		Long inCategoriaContratoId = (Long) serviceData.getArgumentList().getProperty(IN_CATEGORIA_CONTRATO_ID);
//		Calendar inDataLancamentoInicial = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_LANCAMENTO_INICIAL);
//		Calendar inDataLancamentoFinal = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_LANCAMENTO_FINAL);
		Calendar inDataVencimentoInicial = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO_INICIAL);
		Calendar inDataVencimentoFinal = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_VENCIMENTO_FINAL);
		Calendar inDataRecebimentoInicial = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_RECEBIMENTO_INICIAL);
		Calendar inDataRecebimentoFinal = (Calendar) serviceData.getArgumentList().getProperty(IN_DATA_RECEBIMENTO_FINAL);
		Integer inQuantidadeItensInicial = (Integer) serviceData.getArgumentList().getProperty(IN_QUANTIDADE_ITENS_INICIAL);
		Integer inQuantidadeItensFinal = (Integer) serviceData.getArgumentList().getProperty(IN_QUANTIDADE_ITENS_FINAL);
		String inCpfCnpj = (String) serviceData.getArgumentList().getProperty(IN_CPF_CNPJ);
		OutputStream inOutputStream = (OutputStream) serviceData.getArgumentList().getProperty(IN_OUTPUT_STREAM);
		int inTipoContrato = (Integer) serviceData.getArgumentList().getProperty(IN_TIPO_CONTRATO);

    	ApplicationUser inApplicationUser = (serviceData.getArgumentList().containsProperty(IN_APPLICATION_USER_OPT) ?
    			(ApplicationUser) serviceData.getArgumentList().getProperty(IN_APPLICATION_USER_OPT) : null);
		
		/*
		 * Parâmetros opcionais
		 */
		String inEscritorioContabilIdList = (serviceData.getArgumentList().containsProperty(IN_ESCRITORIO_CONTABIL_ID_LIST) ?
				(String) serviceData.getArgumentList().getProperty(IN_ESCRITORIO_CONTABIL_ID_LIST) : null);
		Long inMunicipioId = (serviceData.getArgumentList().containsProperty(IN_MUNICIPIO_ID_OPT) ?
				(Long) serviceData.getArgumentList().getProperty(IN_MUNICIPIO_ID_OPT) : IDAO.ENTITY_UNSAVED);
		Boolean inNotMunicipio = (serviceData.getArgumentList().containsProperty(IN_NOT_MUNICIPIO_OPT) ?
				(Boolean) serviceData.getArgumentList().getProperty(IN_NOT_MUNICIPIO_OPT) : false);
		Long inContaId = (serviceData.getArgumentList().containsProperty(IN_CONTA_ID_OPT) ?
				(Long) serviceData.getArgumentList().getProperty(IN_CONTA_ID_OPT) : IDAO.ENTITY_UNSAVED);
		Long inContratoRepresentanteId = (serviceData.getArgumentList().containsProperty(IN_CONTRATO_REPRESENTANTE_ID_OPT) ?
				(Long) serviceData.getArgumentList().getProperty(IN_CONTRATO_REPRESENTANTE_ID_OPT) : IDAO.ENTITY_UNSAVED);
		
//		log.debug("Executando SQL para corrigir CATEGORIA em BASIC_CONTRATO.");
///*
// 		select * from sindicato_contrato sc
//		inner join basic_contrato bc on bc.id = sc.id and bc.categoria != sc.categoria
//*/
//		try {
//			String sqlFinanceiro = 
//				"update basic_contrato as b " +
//				"set b.categoria = (select c.categoria from financeiro_contrato as c where b.id = c.id) " +
//				"where b.categoria is null";
//			String sqlSindicato = 
//				"update basic_contrato as b " +
//				"set b.categoria = (select c.categoria from sindicato_contrato as c where b.id = c.id) " +
//				"where b.categoria is null";
//			NativeSQL sql = new NativeSQL(
//					serviceData.getCurrentSession(),
//					sqlFinanceiro);
//			sql.executeUpdate();
//			sql = new NativeSQL(
//					serviceData.getCurrentSession(),
//					sqlSindicato);
//			sql.executeUpdate();
//		} catch (HibernateException e) {
//            throw new ServiceException(MessageList.createSingleInternalError(e));
//		} catch (SQLException e) {
//            throw new ServiceException(MessageList.createSingleInternalError(e));
//		}
		
		log.debug("Montando SQL para busca dos dados.");
		/* SQL Slave */
		NativeSQL sqlSlave = new NativeSQL(
				serviceData.getCurrentSession(), 
				QueryRelatorioRecebimento.SELECT_SLAVE, 
				QueryRelatorioRecebimento.WHERE_SLAVE, 
				null,
				null,
				null);
		if (inMunicipioId != IDAO.ENTITY_UNSAVED)
			if (inNotMunicipio)
				sqlSlave.addWhere("(endereco.municipio != "+inMunicipioId+")");
			else
				sqlSlave.addWhere("(endereco.municipio = "+inMunicipioId+")");
		if (inContaId != IDAO.ENTITY_UNSAVED)
			sqlSlave.addWhere("(lancamento_movimento.conta = "+inContaId+")");
		if (inContratoRepresentanteId != IDAO.ENTITY_UNSAVED)
			sqlSlave.addWhere("(fcontrato.representante = "+inContratoRepresentanteId+")");
		if (StringUtils.isNotBlank(inItemCustoIdList))
			if (inNotItemCusto)
				sqlSlave.addWhere("(lancamento_item.itemCusto not in ("+inItemCustoIdList+"))");
			else
				sqlSlave.addWhere("(lancamento_item.itemCusto in ("+inItemCustoIdList+"))");
		if (StringUtils.isNotBlank(inEscritorioContabilIdList))
			sqlSlave.addWhere("(pessoa.escritorioContabil in ("+inEscritorioContabilIdList+"))");
		if (inCategoriaContratoId != IDAO.ENTITY_UNSAVED)
			sqlSlave.addWhere("(fcontrato.categoria = "+inCategoriaContratoId+")");
//		if (inDataLancamentoInicial != null && inDataLancamentoFinal != null)
//			sqlSlave.addWhere("(flancamento.data between '"+CalendarUtils.formatToSQLDate(inDataLancamentoInicial)+"' and '"+CalendarUtils.formatToSQLDate(inDataLancamentoFinal)+"')");
		if (inDataVencimentoInicial != null && inDataVencimentoFinal != null)
			sqlSlave.addWhere("(flancamento.dataVencimento between '"+CalendarUtils.formatToSQLDate(inDataVencimentoInicial)+"' and '"+CalendarUtils.formatToSQLDate(inDataVencimentoFinal)+"')");
		if (inDataRecebimentoInicial != null && inDataRecebimentoFinal != null)
			sqlSlave.addWhere("(lancamento_movimento.data between '"+CalendarUtils.formatToSQLDate(inDataRecebimentoInicial)+"' and '"+CalendarUtils.formatToSQLDate(inDataRecebimentoFinal)+"')");
		if (StringUtils.isNotBlank(inCpfCnpj))
			sqlSlave.addWhere("(pessoa.documento = '"+inCpfCnpj+"')");
		if (inTipoContrato == TipoContrato.ATIVOS.ordinal())
			sqlSlave.addWhere("(fcontrato.inativo = false)");
		if (inTipoContrato == TipoContrato.INATIVOS.ordinal())
			sqlSlave.addWhere("(fcontrato.inativo = true)");
		/*
		 * Lucio 20100711: Filtra os movimento das contas que o atual operador possui direito
		 */
		if (inApplicationUser != null) {
			sqlSlave.addWhere("(flancamento.contaPrevista in (select fcu.conta from financeiro_conta_user fcu where fcu.applicationUser = :applicationUserId))");
			sqlSlave.setLong("applicationUserId", inApplicationUser.getId());

			sqlSlave.addWhere("(lancamento_movimento.conta in (select fcu.conta from financeiro_conta_user fcu where fcu.applicationUser = :applicationUserId))");
			sqlSlave.setLong("applicationUserId", inApplicationUser.getId());
		}

		/* SQL Master */
		String having = "";
		if (inQuantidadeItensInicial != null && inQuantidadeItensFinal != null && (inQuantidadeItensInicial + inQuantidadeItensFinal > 0))
			having = "having qtd between ".concat(Integer.toString(inQuantidadeItensInicial)).concat(" and ").concat(Integer.toString(inQuantidadeItensFinal));
		NativeSQL sqlMaster = new NativeSQL(
				serviceData.getCurrentSession(), 
				QueryRelatorioRecebimento.SELECT_MASTER, 
				QueryRelatorioRecebimento.WHERE_MASTER, 
				having,
				null,
				QueryRelatorioRecebimento.ORDER);
		if (inMunicipioId != IDAO.ENTITY_UNSAVED)
			if (inNotMunicipio)
				sqlMaster.addWhere("(endereco.municipio != "+inMunicipioId+")");
			else
				sqlMaster.addWhere("(endereco.municipio = "+inMunicipioId+")");
//			sqlMaster.addWhere("(endereco.municipio = "+inMunicipioId+")");
		if (inContaId != IDAO.ENTITY_UNSAVED)
			sqlMaster.addWhere("(lancamento_movimento.conta = "+inContaId+")");
		if (inContratoRepresentanteId != IDAO.ENTITY_UNSAVED)
			sqlMaster.addWhere("(contrato.representante = "+inContratoRepresentanteId+")");
		if (StringUtils.isNotBlank(inItemCustoIdList))
			if (inNotItemCusto)
				sqlMaster.addWhere("(lancamento_item.itemCusto not in ("+inItemCustoIdList+"))");
			else
				sqlMaster.addWhere("(lancamento_item.itemCusto in ("+inItemCustoIdList+"))");
		if (StringUtils.isNotBlank(inEscritorioContabilIdList))
			sqlMaster.addWhere("(pessoa.escritorioContabil in ("+inEscritorioContabilIdList+"))");
		if (inCategoriaContratoId != IDAO.ENTITY_UNSAVED)
			sqlMaster.addWhere("(contrato.categoria = "+inCategoriaContratoId+")");
//		if (inDataLancamentoInicial != null && inDataLancamentoFinal != null)
//			sqlMaster.addWhere("(lancamento.data between '"+CalendarUtils.formatToSQLDate(inDataLancamentoInicial)+"' and '"+CalendarUtils.formatToSQLDate(inDataLancamentoFinal)+"')");
		if (inDataVencimentoInicial != null && inDataVencimentoFinal != null)
			sqlMaster.addWhere("(lancamento.dataVencimento between '"+CalendarUtils.formatToSQLDate(inDataVencimentoInicial)+"' and '"+CalendarUtils.formatToSQLDate(inDataVencimentoFinal)+"')");
		if (inDataRecebimentoInicial != null && inDataRecebimentoFinal != null)
			sqlMaster.addWhere("(lancamento_movimento.data between '"+CalendarUtils.formatToSQLDate(inDataRecebimentoInicial)+"' and '"+CalendarUtils.formatToSQLDate(inDataRecebimentoFinal)+"')");
		if (StringUtils.isNotBlank(inCpfCnpj))
			sqlMaster.addWhere("(pessoa.documento = '"+inCpfCnpj+"')");
		if (inTipoContrato == TipoContrato.ATIVOS.ordinal())
			sqlMaster.addWhere("(contrato.inativo = false)");
		if (inTipoContrato == TipoContrato.INATIVOS.ordinal())
			sqlMaster.addWhere("(contrato.inativo = true)");
		/*
		 * Lucio 20100711: Filtra os movimento das contas que o atual operador possui direito
		 */
		if (inApplicationUser != null) {
			sqlMaster.addWhere("(lancamento.contaPrevista in (select fcu.conta from financeiro_conta_user fcu where fcu.applicationUser = :applicationUserId))");
			sqlMaster.setLong("applicationUserId", inApplicationUser.getId());

			sqlMaster.addWhere("(lancamento_movimento.conta in (select fcu.conta from financeiro_conta_user fcu where fcu.applicationUser = :applicationUserId))");
			sqlMaster.setLong("applicationUserId", inApplicationUser.getId());
		}

		
		sqlMaster.setParameter("slave", sqlSlave.getSql());
		
		try {
			log.debug("Preparando mascará dos campos Documento e Telefone.");
			MaskFormatter mfDocumento = new MaskFormatter(this.getServiceManager().getEntityManager().getEntityMetadata(Juridica.class).getPropertyMetadata(Juridica.DOCUMENTO).getEditMask());
			mfDocumento.setValueContainsLiteralCharacters(false);
			mfDocumento.setAllowsInvalid(true);

			MaskFormatter mfTelefone = new MaskFormatter("(##)#####-####");
			mfTelefone.setValueContainsLiteralCharacters(false);
			mfTelefone.setAllowsInvalid(true);

			log.debug("Executando a SQL.");
			System.out.println("==> " + sqlMaster.getSql());
			ResultSet rs = sqlMaster.executeQuery();
			List<QueryRelatorioRecebimento> list = new ArrayList<QueryRelatorioRecebimento>();
			Calendar data = CalendarUtils.getCalendar();
			String telefone;
			while (rs.next()) {
				if (rs.getString("telefone") != null)
					telefone = mfTelefone.valueToString(rs.getString("telefone"));
				else
					telefone = "";
				QueryRelatorioRecebimento query = new QueryRelatorioRecebimento(
						rs.getString("nome"),
						mfDocumento.valueToString(rs.getString("documento")),
						telefone,
						rs.getString("descricao"),
						rs.getDate("dataVencimento", data),
						rs.getDate("dataRecebimento", data),
						rs.getBigDecimal("valorLancamento"),
						rs.getBigDecimal("valor"));
				list.add(query);
			}
			serviceData.getOutputData().add(list);
			
			log.debug("Filtrando por número de itens.");
			
			log.debug("Compilando o relatório.");
			String nomeArquivoFonte = "RelatorioRecebimento.jrxml";
	        Map<String, String> parametros = new HashMap<String, String>();
			JasperReport relatorio = JasperCompileManager.compileReport(getClass().getResourceAsStream(nomeArquivoFonte));

			/*
			 * Verifica se foi passado um municipio para exibir no relatório
			 */
			Municipio municipio = null;
			if ((inMunicipioId != IDAO.ENTITY_UNSAVED) && (inNotMunicipio == false)) {
				municipio = UtilsCrud.retrieve(this.getServiceManager(), Municipio.class, inMunicipioId, null).getObject();
				parametros.put("municipio", municipio.getNome() + " - " + municipio.getUf());
			}
			
			log.debug("Imprimindo o relatório.");
			JasperPrint print = JasperFillManager.fillReport(relatorio, parametros, new JRBeanCollectionDataSource(list));
			
	        JasperExportManager.exportReportToPdfStream(print, inOutputStream);
	        
//		} catch (HibernateException e) {
//            log.fatal(e.getMessage());
//            /* Indica que o serviço falhou por causa de uma exceção do hibernate. */
//            throw new ServiceException(MessageList.createSingleInternalError(e));
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
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
            throw new ServiceException(MessageList.createSingleInternalError(e));
		}
	}

	public String getServiceName() {
		return SERVICE_NAME;
	}

}
