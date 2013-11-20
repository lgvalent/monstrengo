package br.com.orionsoft.financeiro.documento.cobranca.titulo.banco756;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.LockMode;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.cnab.Campo;
import br.com.orionsoft.cnab.IRegistro;
import br.com.orionsoft.cnab.banco756.cnab400.DetalheRemessaSemRegistro;
import br.com.orionsoft.cnab.banco756.cnab400.DetalheRetornoSemRegistro;
import br.com.orionsoft.cnab.banco756.cnab400.HeaderRemessa;
import br.com.orionsoft.cnab.banco756.cnab400.HeaderRetorno;
import br.com.orionsoft.cnab.banco756.cnab400.Instrucao;
import br.com.orionsoft.cnab.banco756.cnab400.TraillerRemessa;
import br.com.orionsoft.cnab.banco756.cnab400.TraillerRetorno;
import br.com.orionsoft.cnab.cnab400.Arquivo;
import br.com.orionsoft.cnab.cnab400.Remessa;
import br.com.orionsoft.cnab.cnab400.Retorno;
import br.com.orionsoft.cnab.exceptions.PropriedadeVaziaException;
import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaException;
import br.com.orionsoft.financeiro.documento.cobranca.IGerenciadorDocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoResultado;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoStatus;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.Cedente;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.DocumentoTitulo;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.GerenciadorBancoBasic;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.GerenciadorDocumentoTitulo;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.Ocorrencia;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.OcorrenciaControle;
import br.com.orionsoft.financeiro.utils.UtilsOcorrencia;
import br.com.orionsoft.financeiro.utils.UtilsRemessa;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.core.util.MathUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.Operator;
import br.com.orionsoft.monstrengo.crud.services.QueryCondiction;
import br.com.orionsoft.monstrengo.crud.services.QueryService;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;


/**
 * <p>Esta classe implementa o gerenciador de documento do tipo titulo.<p>
 *
 * @author Lucio
 * 
 * @spring.bean id="Gerenciador756" init-method="registrarGerenciador"
 * @spring.property name="provedorBanco" ref="ProvedorBanco"
 */
public class Gerenciador756 extends GerenciadorBancoBasic
{

	public static final String GERENCIADOR_NOME = "BANCOOB";
	public static final String GERENCIADOR_CODIGO = "756";

	/* Auxiliares para cálculo da linha digitável */
	public static final String MODALIDADE_CODIGO = "01";
	public static final String PARCELA = "001";

	public static final String OCORRENCIA_LIQUIDACAO = "99"; //Liquidação
	public static final long ESPECIE_TITULO = 01; //Duplicata
	public static final String EXTENSAO_ARQUIVO_REMESSA = ".REM";
	public static int NOSSO_NUMERO_LIMITE = 999999; //padrão 999999 (novecentos e noventa e nove mil e novecentos e noventa e nove)

	/*
	 * 7 (sete) é o tamanho definido para o código do Cedente  
	 * DEVE ser definido pois senão o Código de Barras não é gerado corretamente
	 */
	public static final int CEDENTE_CODIGO_LENGTH = 7;
	public static final int NOSSO_NUMERO_LENGTH = 8;

	public String getNome() {
		return GERENCIADOR_NOME;
	}

	public String getCodigo() {
		return GERENCIADOR_CODIGO;
	}
	
	public String getCampoLivre(IEntity<? extends DocumentoCobranca> documento) throws DocumentoCobrancaException{
		DocumentoTitulo titulo = (DocumentoTitulo)documento.getObject();
			
			StringBuffer campoLivre = new StringBuffer();
			
			// 1
			campoLivre.append(br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(titulo.getCedente().getCarteiraCodigo(), 1, true));
			
			// 4
			campoLivre.append(br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(titulo.getCedente().getContaBancaria().getAgenciaCodigo(), 4, true));
			
			// 2
			campoLivre.append(MODALIDADE_CODIGO);
			
			// 7 
			campoLivre.append(br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(titulo.getCedente().getCedenteCodigo(), CEDENTE_CODIGO_LENGTH, true));
					
			// 8
			campoLivre.append(br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(titulo.getNumeroDocumento(), NOSSO_NUMERO_LENGTH, true));
			
			// 3
			campoLivre.append(PARCELA);
			
			return campoLivre.toString();
	}

	/**
	 * Este método define e formata o nosso número do título.<br>
	 * Ele utiliza e atualiza o sequencial de nosso número controlado pelo cedente. 
	 */
	public String formatarNossoNumero(IEntity<? extends DocumentoCobranca> documento, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		try{
			DocumentoTitulo oTitulo = (DocumentoTitulo)documento.getObject();

			/* Tenta obter o LOCK sobre o objeto vinculado no Título */
			Cedente oCedente = oTitulo.getCedente();
			serviceDataOwner.getCurrentSession().lock(oCedente, LockMode.UPGRADE);

			/* Dumy update para forçar um LockMode.UPGRADE. Segundo o pessoal, o lock somente é obtido quando o update ocorre!!! */
// Ver se isto  verdade			UtilsCrud.objectUpdate(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), oCedente, serviceDataOwner);

			long nossoNumeroSequencial = (Long) oCedente.getSequenciaNumeroDocumento();
			int nossoNumeroAno = oCedente.getNossoNumeroAno();
			if (nossoNumeroSequencial == 0)
				nossoNumeroSequencial = 1; //evita que o numero se "perca" caso o valor seja 0 e a data esteja correta 

			Calendar anoAtual = Calendar.getInstance();
			if (nossoNumeroAno != anoAtual.get(Calendar.YEAR)){
				nossoNumeroAno = anoAtual.get(Calendar.YEAR);
				oCedente.setNossoNumeroAno(anoAtual.get(Calendar.YEAR));
			}

			if (nossoNumeroSequencial > NOSSO_NUMERO_LIMITE)
				nossoNumeroSequencial = 1;

			//para o BANCOOB o formato do nosso numero é YYXXXXXX, onde YY é o ano e XXXXXX é o número sequencial
			String nossoNumero = String.format("%02d",(nossoNumeroAno - 2000)) + String.format("%06d", nossoNumeroSequencial);

			nossoNumeroSequencial += 1;
			System.out.println("NOSSONUMERO:" + nossoNumeroSequencial);
			oCedente.setSequenciaNumeroDocumento(nossoNumeroSequencial);
			//Atualiza a Entidade Cedente pois o número sequencial e o ano podem ter sido alterados 
			UtilsCrud.objectUpdate(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), oCedente, serviceDataOwner);

			return nossoNumero;
		}catch (BusinessException e) {
			throw new DocumentoCobrancaException(e.getErrorList());
		}
	}

	public String formatarAgenciaCedente(IEntity<? extends DocumentoCobranca> documento) throws DocumentoCobrancaException{
		DocumentoTitulo titulo = (DocumentoTitulo)documento.getObject();
		return titulo.getCedente().getContaBancaria().getAgenciaCodigo() + " / " + br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(titulo.getCedente().getCedenteCodigo(), CEDENTE_CODIGO_LENGTH, true);
	}

	@SuppressWarnings("unchecked")
	public File gerarRemessa(IEntity<? extends ConvenioCobranca> convenioCobranca, Calendar inicioPeriodo, Calendar finalPeriodo, Integer quantidadeDiasProtesto, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		long numeroSequencial = 0;
		IEntityList<DocumentoTitulo> entityList = null;

		try {	
			/* 
			 * Pesquisando pelos Títulos que estão prontos para serem enviados 
			 * ao banco (código ocorrência 1001) e pertencem ao convenio 
			 * passado por parâmetro
			 */
			log.debug("Pesquisando os Títulos que serão incluídos no arquivo de remessa");
			List<QueryCondiction> condictions = new ArrayList<QueryCondiction>();

			QueryCondiction condiction = new QueryCondiction(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().getEntityManager(),
					DocumentoTitulo.class,
					DocumentoTitulo.CONVENIO_COBRANCA + '.' + "id",
					Operator.EQUAL,
					String.valueOf(convenioCobranca.getId()),
			"");

			condictions.add(condiction);

			condiction = new QueryCondiction(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().getEntityManager(),
					DocumentoTitulo.class,
					DocumentoTitulo.ULTIMA_OCORRENCIA + '.' + Ocorrencia.CODIGO,
					Operator.EQUAL,
					String.valueOf(Ocorrencia.REMESSA_REGISTRAR.getCodigo()),
			""); 

			condiction.setInitOperator(QueryCondiction.INIT_AND);
			condictions.add(condiction);

			condiction = new QueryCondiction(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().getEntityManager(),
					DocumentoTitulo.class,
					DocumentoTitulo.LAYOUT_ID,
					Operator.EQUAL,
					String.valueOf(GerenciadorDocumentoTitulo.LAYOUT_INT_1),
					""); 

			condiction.setInitOperator(QueryCondiction.INIT_AND);
			condictions.add(condiction);

			/* Se a data inicial e a data final não estiverem vazias, determinar o período para este arquivo de remessa */
			condiction = new QueryCondiction(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().getEntityManager(),
					DocumentoTitulo.class,
					DocumentoTitulo.DATA_VENCIMENTO,
					Operator.BETWEEN,
					CalendarUtils.formatDate(inicioPeriodo),
					CalendarUtils.formatDate(finalPeriodo));

			condictions.add(condiction);

			ServiceData sdQuery = new ServiceData(QueryService.SERVICE_NAME, serviceDataOwner);
			sdQuery.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, DocumentoTitulo.class);
			sdQuery.getArgumentList().setProperty(QueryService.IN_QUERY_CONDICTIONS, condictions);
			/*Lucio 20100822 - Ordena os títulos na remessa do banco por escritório contábil. Isto para facilitar a distribuição */ 
			sdQuery.getArgumentList().setProperty(QueryService.IN_ORDER_EXPRESSION_OPT, "entity." + DocumentoTitulo.CONTRATO+"."+Contrato.PESSOA+"."+Pessoa.ESCRITORIO_CONTABIL + ", entity." + DocumentoTitulo.CONTRATO+"."+Contrato.PESSOA+"."+Pessoa.NOME);
			this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().execute(sdQuery);

			log.debug("Obtendo o(s) título(s) encontrado(s) na pesquisa");
			entityList = (IEntityList<DocumentoTitulo>) sdQuery.getOutputData(QueryService.OUT_ENTITY_LIST);
			log.debug("Foram encontrados " + entityList.getSize() + " títulos na pesquisa");

			/*
			 * O uso do cedente passado por parâmetro causava erro de NonUniqueObject quando era feito o update desta entidade
			 * Dessa forma o cedente recebe a entidade cedente atribuído ao DocumentoTitulo, evitando o erro na hora de realizar update na entidade
			 */
			if (!entityList.isEmpty())
				convenioCobranca = entityList.getFirst().getProperty(DocumentoTitulo.CONVENIO_COBRANCA).getValue().getAsEntity();


		}catch (BusinessException e) {
			throw new DocumentoCobrancaException(MessageList.createSingleInternalError(e));
		}

		//Se não encontrar nenhum título na pesquisa, retorna mensagem de erro
		if (entityList.isEmpty())
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "REMESSA_NENHUM_TITULO"));


		//Caso encontre títulos, tenta construir (preencher) o arquivo de remessa
		Arquivo arquivo = new Arquivo();
		List<IRegistro> registros = new ArrayList<IRegistro>();

		/* Construindo Estrutura Básica */
		IRegistro header = null;
		IRegistro detalhe = null;
		IRegistro instrucao = null;
		IRegistro trailler = null; 

		/* Inserindo valores no arquivo de remessa */
		/* Construindo Header */
		try{
			header = new HeaderRemessa("");
			setRegistro(header, HeaderRemessa.DATA_GRAVACAO_ARQUIVO, "DATA_GRAVACAO_ARQUIVO", setData(CalendarUtils.getCalendar()).toUpperCase(), true);
			setRegistro(header, HeaderRemessa.NOME_CEDENTE_IMPRESSAO, "NOME_CEDENTE_IMPRESSAO", br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(verificarNull(convenioCobranca.getProperty(ConvenioCobranca.CONTRATANTE).getValue().getAsEntity().getProperty(Pessoa.NOME).getValue().getAsString())).toUpperCase(), true);
			setRegistro(header, HeaderRemessa.NUMERO_SEQUENCIAL, "NUMERO_SEQUENCIAL", ++numeroSequencial);
		}catch (BusinessException e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_CRIANDO_ARQUIVO"));
		} catch (Exception e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_CRIANDO_ESTRUTURA_REMESSA"));
		}

		Cedente oCedente =  (Cedente) convenioCobranca.getObject();

		BigDecimal tituloOkValorTotal = new BigDecimal("0"); //Totalizador dos valores dos titulos OK que puderam ser inseridos no arquivo de remessa
		BigDecimal tituloNoOkValorTotal = new BigDecimal("0"); //Totalizador dos valores dos titulos NOOK - NÃO puderam ser inseridos no arquivo de remessa  
		MessageList tituloOkList = new MessageList(); //possui mensagens informativas sobre os titulos que puderam ser inseridos no arquivo de remessa
		MessageList tituloNoOkList = new MessageList(); //possui mensagens informativas sobre os titulos que NÃO puderam ser inseridos no arquivo de remessa
		int titulosTotal = 0;
		int titulosInseridosOk = 0;
		boolean tituloOk;

		/* Construindo Detalhes e Instruções */
		for (IEntity<DocumentoTitulo> titulo : entityList){
			DocumentoTitulo oTitulo = titulo.getObject();
			titulosTotal += 1;
			tituloOk = true;

			try {
				detalhe = new DetalheRemessaSemRegistro("");
				instrucao = new Instrucao("");
			} catch (Exception e1) {
				throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_CRIANDO_ESTRUTURA_REMESSA"));
			}

			try{
				/* Construindo Detalhe */
				setRegistro(detalhe, DetalheRemessaSemRegistro.AGENCIA, "AGENCIA", br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(oCedente.getContaBancaria().getAgenciaCodigo(), 4, true).toUpperCase(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.CONTA_CORRENTE, "CONTA_CORRENTE", br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(oCedente.getContaBancaria().getContaCodigo() + oCedente.getContaBancaria().getContaDigito(), 10, true).toUpperCase(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.CODIGO_CEDENTE, "CODIGO_CEDENTE", br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(oCedente.getCedenteCodigo(), 7, true).toUpperCase(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.NOSSO_NUMERO, "NOSSO_NUMERO", oTitulo.getNumeroDocumento(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.NUMERO_DOCUMENTO, "NUMERO_DOCUMENTO", oTitulo.getNumeroDocumento().toUpperCase(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.DATA_VENCIMENTO, "DATA_VENCIMENTO", setData(oTitulo.getDataVencimento()).toUpperCase(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.VALOR_TITULO, "VALOR_TITULO", oTitulo.getValor(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.ESPECIE_TITULO, "ESPECIE_TITULO", ESPECIE_TITULO);
				setRegistro(detalhe, DetalheRemessaSemRegistro.IDENTIFICACAO, "IDENTIFICACAO", (oCedente.isAceite()?"S":"N").toUpperCase(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.DATA_EMISSAO_TITULO, "DATA_EMISSAO_TITULO", setData(oTitulo.getData()).toUpperCase(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.ID_TIPO_INSCRICAO_SACADO, "ID_TIPO_INSCRICAO_SACADO", oTitulo.getContrato().getPessoa().isFisica()?"01":"02", true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.NUMERO_INSCRICAO_SACADO, "NUMERO_INSCRICAO_SACADO", verificarNull(br.com.orionsoft.monstrengo.core.util.StringUtils.format(oTitulo.getContrato().getPessoa().getDocumento(), 14, " ", false, true).toUpperCase()), true);
				/*Lucio 20100822 - Coloca o código do escritório antes do nome do sacado para facilitar a organizaçã */
				String nomeSacado = null;
				if(oTitulo.getContrato().getPessoa().getEscritorioContabil() != null)
					nomeSacado = "[" + oTitulo.getContrato().getPessoa().getEscritorioContabil().getId() + "]" + oTitulo.getContrato().getPessoa().getNome();
				else
					nomeSacado = "[nd]" + oTitulo.getContrato().getPessoa().getNome();
					
				setRegistro(detalhe, DetalheRemessaSemRegistro.NOME_SACADO, "NOME_SACADO", verificarNull(br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(nomeSacado).toUpperCase()), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.ENDERECO_COMPLETO, "ENDERECO_COMPLETO", verificarNull(br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(UtilsRemessa.formatarEndereco(oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia(), DetalheRemessaSemRegistro.ENDERECO_COMPLETO.getSize()))).toUpperCase(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.CEP, "CEP", StringUtils.substring(oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getCep(), 0, 5).toUpperCase(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.SUFIXO_CEP, "SUFIXO_CEP", StringUtils.substring(oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getCep(), 5, 8).toUpperCase(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.NUMERO_SEQUENCIAL, "NUMERO_SEQUENCIAL", ++numeroSequencial);

				/* Construindo Instrução */
				setRegistro(instrucao, Instrucao.MENSAGEM_1, "MENSAGEM_1", br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(verificarNull(oTitulo.getDocumentoCobrancaCategoria().getInstrucoes0())).toUpperCase(), false);
				setRegistro(instrucao, Instrucao.MENSAGEM_2, "MENSAGEM_2", br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(verificarNull(oTitulo.getDocumentoCobrancaCategoria().getInstrucoes1())).toUpperCase(), false);
				setRegistro(instrucao, Instrucao.MENSAGEM_3, "MENSAGEM_3", br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(verificarNull(oTitulo.getDocumentoCobrancaCategoria().getInstrucoes2())).toUpperCase(), false);
				setRegistro(instrucao, Instrucao.MENSAGEM_4, "MENSAGEM_4", br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(verificarNull(oTitulo.getInstrucoes3())).toUpperCase(), false);
				setRegistro(instrucao, Instrucao.CODIGO_CEDENTE, "CODIGO_CEDENTE", br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(oCedente.getCedenteCodigo(), 7, true).toUpperCase(), true);
				setRegistro(instrucao, Instrucao.CARTEIRA, "CARTEIRA", oCedente.getCarteiraCodigo().toUpperCase(), true);
				setRegistro(instrucao, Instrucao.ID_TITULO_BANCO, "ID_TITULO_BANCO", oTitulo.getNumeroDocumento(), true);
				setRegistro(instrucao, Instrucao.NUMERO_SEQUENCIAL, "NUMERO_SEQUENCIAL", ++numeroSequencial);

				/* Atribuindo ocorrência ao titulo */
				try{
					IEntity<Ocorrencia> ultimaOcorrencia = UtilsOcorrencia.obterOcorrencia(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), Ocorrencia.REMESSA_ENVIADA.getCodigo(), serviceDataOwner);
					titulo.setPropertyValue(DocumentoTitulo.ULTIMA_OCORRENCIA, ultimaOcorrencia);
					titulo.setPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA, CalendarUtils.getCalendar());

					/* Adicionando ocorrência ao histórico de ocorrências deste titulo */
					IEntity<OcorrenciaControle> ocorrencia = UtilsCrud.create(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), OcorrenciaControle.class, serviceDataOwner);
					ocorrencia.setPropertyValue(OcorrenciaControle.TITULO, titulo);
					ocorrencia.setPropertyValue(OcorrenciaControle.OCORRENCIA, ultimaOcorrencia);
					ocorrencia.setPropertyValue(OcorrenciaControle.DATA_OCORRENCIA, CalendarUtils.getCalendar());
					UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), ocorrencia, serviceDataOwner);

					titulo.getProperty(DocumentoTitulo.OCORRENCIAS).getValue().<OcorrenciaControle>getAsEntityCollection().add(ocorrencia);
				}catch (BusinessException e) {
					throw new DocumentoCobrancaException(MessageList.create(BusinessMessage.TYPE_INFO, Gerenciador756.class, "REMESSA_ERRO_AO_SETAR", oTitulo.getContrato().toString(), oTitulo.getNumeroDocumento(), CalendarUtils.formatDate(oTitulo.getDataVencimento()), oTitulo.getValor(), e.getMessage()));
				}
			}catch (BusinessException e) {
				throw new DocumentoCobrancaException(e.getErrorList());
			}

			if (tituloOk){
				registros.add(detalhe);
				registros.add(instrucao);

				/* Atualizando o titulo */
				try {
					UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), titulo, serviceDataOwner);
				} catch (BusinessException e) {
					throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_ATUALIZANDO_ENTIDADE", titulo.getObject().toString()));
				}
				titulosInseridosOk += 1;
				tituloOkList.add(BusinessMessage.TYPE_INFO, Gerenciador756.class, "REMESSA_SUCESSO", oTitulo.getContrato().toString() ,oTitulo.getNumeroDocumento(), CalendarUtils.formatDate(oTitulo.getDataVencimento()), DecimalUtils.formatBigDecimal(oTitulo.getValor()));
				tituloOkValorTotal = tituloOkValorTotal.add(oTitulo.getValor());
			}else{
				tituloNoOkValorTotal = tituloNoOkValorTotal.add(oTitulo.getValor()) ;
			}
		}//for

		/* Construindo Trailler */
		try{
			trailler = new TraillerRemessa("");
			setRegistro(trailler, TraillerRemessa.NUMERO_SEQUENCIAL, "NUMERO_SEQUENCIAL", ++numeroSequencial);	
		}catch (BusinessException e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_CRIANDO_ARQUIVO"));
		} catch (Exception e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_CRIANDO_ESTRUTURA_REMESSA"));
		}

		/* Todos as partes foram montadas, inserindo valores no arquivo de remessa */
		arquivo.setHeader(header);
		arquivo.set(Arquivo.DETALHE, registros);
		arquivo.setTrailer(trailler);

		/* Obtendo o cnpj do contratante e o numero sequencial da remessa para montar o nome do arquivo de remessa */
		int remessaSequencial = 1;
		long diferencaDias = 0;
		String cnpj = null;
		String nomeArquivoRemessa = null;
		try {
			String cnpjTemp = (String) convenioCobranca.getProperty(ConvenioCobranca.CONTRATANTE).getValue().getAsEntity().getProperty(Pessoa.DOCUMENTO).getValue().getAsObject();
			cnpj = br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(cnpjTemp, 14, true); //14 é o tamanho do campo CNPJ
			if (cnpj.equals("") || cnpj.equals(null))
				throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_MONTANDO_NOME_ARQUIVO", "CNPJ atribuído: " + cnpj));

			Calendar dataUltimaRemessa = convenioCobranca.getProperty(Cedente.REMESSA_ULTIMA_DATA).getValue().getAsCalendar();
			Calendar data = CalendarUtils.getCalendar();
			diferencaDias = CalendarUtils.diffDay(data, dataUltimaRemessa);

			if(diferencaDias > 0){ //se o dia se alterou, o contador de remessa deve voltar ao valor 1
				remessaSequencial = 1;
			}else if(diferencaDias == 0){ //se o dia for o mesmo, obter o numero sequencial através do cedente
				remessaSequencial = convenioCobranca.getProperty(Cedente.REMESSA_NUMERO_SEQUENCIAL).getValue().getAsInteger();
				if (remessaSequencial == 0) //caso seja a primeira vez que o Cedente esteja criando o arquivo
					remessaSequencial = 1;
			}else{ //a diferença não deve ser negativa
				throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_MONTANDO_NOME_ARQUIVO", "Diferença entre a data atual e a data da última remessa (NÃO pode ser negativa): " + diferencaDias));
			}

			String dia = String.format("%02d", data.get(Calendar.DAY_OF_MONTH));
			String mes = String.format("%02d", data.get(Calendar.MONTH)+1); //+1 pois os meses no Calendar iniciam em 0
			String sequencial = String.format("%02d", remessaSequencial);

			nomeArquivoRemessa = cnpj + "_" + dia + mes + sequencial + EXTENSAO_ARQUIVO_REMESSA;
			if(nomeArquivoRemessa.length() != 25) //25 é a quantidade de caracteres que o nome do arquivo deve ter COM a sua extensão, DEVE sempre ser assim para o Sicoob
				throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_MONTANDO_NOME_ARQUIVO", "Tamanho do nome do arquivo (esperado 25) :" + nomeArquivoRemessa.length()));

		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_MONTANDO_NOME_ARQUIVO", e.getMessage()));
		} 

		/*
		 * O arquivo so é enviado após todas as etapas conluídas com sucesso,
		 * para evitar que o número sequencial da remessa fosse alterado de maneira equivocada
		 */

		/*	
		 * Arquivo apenas para obtermos o diretório temporário do sistema, pois o File.createTempFile adiciona 
		 * alguns caracteres de controle ao nome do arquivo, o que não nos interessa, pois o nome do arquivo deve 
		 * seguir exatamente o manual do Sicoob 
		 * 
		 */
		File tempFileDir = null;  
		File tempFile = null; 
		Remessa remessa = null;
		try {
			tempFileDir = File.createTempFile("remessa",null);

			tempFile = new File(tempFileDir.getParent(),nomeArquivoRemessa);

			remessa = new Remessa(tempFile, arquivo);
			
			tempFile = remessa.getFile();

			//por fim, o número sequencial da remessa é incrementado na entidade cedente, e a data da última remessa é atualizada
			remessaSequencial++;
			convenioCobranca.setPropertyValue(Cedente.REMESSA_NUMERO_SEQUENCIAL, remessaSequencial);
			convenioCobranca.setPropertyValue(Cedente.REMESSA_ULTIMA_DATA, CalendarUtils.getCalendar());
			UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), convenioCobranca, serviceDataOwner);
		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_ATUALIZANDO_CEDENTE", tempFileDir.getParent()));
		} catch (IOException e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_CRIANDO_ARQUIVO", e.getMessage()));
		}

		//Mostra quantos Titulos puderam ser inseridos no arquivo de remessa
		serviceDataOwner.getMessageList().add(BusinessMessage.TYPE_INFO, Gerenciador756.class, "REMESSA_QUANTIDADE_TITULOS", titulosInseridosOk, titulosTotal);

		/* 
		 * - verifica se tem mensagens de Ok
		 * - adiciona na primeira posição a mensagem cabeçalho de Ok 
		 */
		if (tituloOkList.size() > 0){
			serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador756.class, "REMESSA_CABECALHO_TITULOS_OK"));
			serviceDataOwner.getMessageList().addAll(tituloOkList);
			serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador756.class, "REMESSA_SUCESSO_TOTAL", DecimalUtils.formatBigDecimal(tituloOkValorTotal)));
		}

		/* 
		 * - verifica se tem mensagens de NoOk
		 * - adiciona na primeira posição a mensagem cabeçalho de NoOk 
		 */
		if (tituloNoOkList.size() > 0){
			serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador756.class, "REMESSA_CABECALHO_TITULOS_NO_OK"));
			serviceDataOwner.getMessageList().addAll(tituloNoOkList);
			serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador756.class, "REMESSA_FALHA_TOTAL", DecimalUtils.formatBigDecimal(tituloNoOkValorTotal)));
		}

		log.debug("Encerrando com sucesso o método gerarRemessa()");
		return tempFile;

	}

	public List<DocumentoRetornoResultado> receberRetorno(IEntity<? extends ConvenioCobranca> convenioCobranca, InputStream dados, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		Cedente oCedente = (Cedente) convenioCobranca.getObject();
		if (oCedente.getLayoutCnab().equals(GerenciadorDocumentoTitulo.CNAB_400)){

			Retorno retorno = null;

			try {
				log.debug("Lendo os dados do retorno");
				retorno = new Retorno(dados, HeaderRetorno.class, TraillerRetorno.class, new Class<?>[]{DetalheRetornoSemRegistro.class, Instrucao.class});
			} catch (IOException e) {
				log.debug("Erro ao preparar os dados do retorno:" + e.getMessage());
				DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.createSingleInternalError(e));
				de.getErrorList().add(Gerenciador756.class, "ERRO_INTERPRETANDO_ARQUIVO", "(Nome do arquivo não disponível)");
				throw de;		
			}

			int titulosTotal = 0; //contador para o total de titulos encontradas no arquivo de retorno
			int titulosAtualizados = 0; //contador para os titulos que puderam ser atualizadas no sistema
			MessageList tituloOkList = new MessageList(); //possui mensagens informativas sobre os titulos que foram atualizados
			MessageList tituloJaRetornadoList = new MessageList(); //possui mensagens informativas sobre os titulos que já foram lidos e processados pelo arquivo, evitando que seja baixado mais de uma vez
			MessageList tituloNoOkList = new MessageList(); //possui mensagens informativas sobre os titulos que NÃO puderam ser atualizados
			BigDecimal tituloOkValorTotal = new BigDecimal("0"); //Totalizador dos valores dos titulos OK
			BigDecimal tituloJaRetornadoValorTotal = new BigDecimal("0"); //Totalizador dos valores dos titulos que já tinham sido retornados anteriormente
			BigDecimal tituloNoOkValorTotal = new BigDecimal("0"); //Totalizador dos valores dos titulos NOOK
			
			/*
			 * Andre, 19/07/2008: estava retornando null ao invés de List<DocumentoRetornoResultado>, resultado
			 * em erro de NullPointer no serviço de receberRetorno de documento de cobrança.
			 * TODO - verificar se precisa tirar todos as lista de mensagens (tituloOk e titulosNoOk por exemplo)
			 */
			List<DocumentoRetornoResultado> result = new ArrayList<DocumentoRetornoResultado>();

			log.debug("Percorrendo os registros do arquivo de retorno");
			try{
				/*
				 * O procedimento é o seguinte: 
				 * - percorre o número registros do arquivo de retorno através do getDetalhes() 
				 * - cada detalhe possui informações de um titulo 
				 * - para cada detalhe, verifica o titulo no sistema e efetua a sua baixa
				 */
				for (IRegistro detalhes : retorno.getArquivo().get(Arquivo.DETALHE)){

					titulosTotal += 1;

					log.debug("Obtendo informações do arquivo de retorno para fazer a pesquisa de titulos");    

					String nossoNumero = detalhes.get(DetalheRetornoSemRegistro.NOSSO_NUMERO);

					log.debug("Pesquisando um titulo atraves do NOSSO_NUMERO");
					List<QueryCondiction> condictions = new ArrayList<QueryCondiction>();
					condictions.add(new QueryCondiction(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().getEntityManager(),
							DocumentoTitulo.class,
							DocumentoTitulo.NUMERO_DOCUMENTO,
							Operator.EQUAL,
							nossoNumero,
					""));

					ServiceData sdQuery = new ServiceData(QueryService.SERVICE_NAME, serviceDataOwner);
					sdQuery.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, DocumentoTitulo.class);
					sdQuery.getArgumentList().setProperty(QueryService.IN_QUERY_CONDICTIONS, condictions);
					this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().execute(sdQuery);

					log.debug("Obtendo o titulo encontrado na pesquisa");

					@SuppressWarnings("unchecked")
					IEntityList<DocumentoTitulo> entityList = (IEntityList<DocumentoTitulo>) sdQuery.getOutputData(QueryService.OUT_ENTITY_LIST);

					/* 
					 * Andre, 19/07/2008: resultado de um documento do arquivo de retorno 
					 */
					DocumentoRetornoResultado doc = new DocumentoRetornoResultado();
					doc.setId(IDAO.ENTITY_UNSAVED);
					doc.setNumeroDocumento(nossoNumero);
					result.add(doc);

					/*
					 * Se nenhum titulo for encontrado na pesquisa, adiciona o erro na lista e continua o programa;
					 */
					if (entityList.getSize() == 0){
						tituloNoOkList.add(BusinessMessage.TYPE_INFO, 
								Gerenciador756.class, 
								"TITULO_NAO_ENCONTRADO", 
								detalhes.get(DetalheRetornoSemRegistro.NOSSO_NUMERO), 
								CalendarUtils.formatDate(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_VENCIMENTO)), 
								CalendarUtils.formatDate(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA_BANCO)), 
								DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO)), 
								DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_PAGO)));
						tituloNoOkValorTotal = tituloNoOkValorTotal.add(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_PAGO));

						doc.setStatus(DocumentoRetornoStatus.NUMERO_DOCUMENTO_NAO_ENCONTRADO);

					}else if (entityList.getSize() > 1){
						tituloNoOkList.add(BusinessMessage.TYPE_INFO, 
								Gerenciador756.class, 
								"TITULO_ID_DUPLICADO", 
								detalhes.get(DetalheRetornoSemRegistro.NOSSO_NUMERO), 
								CalendarUtils.formatDate(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_VENCIMENTO)), 
								detalhes.get(DetalheRetornoSemRegistro.VALOR_TITULO), 
								CalendarUtils.formatDate(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA_BANCO)), 
								DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_PAGO)));

						doc.setStatus(DocumentoRetornoStatus.NUMERO_DOCUMENTO_DUPLICADO);

					}else{
						IEntity<DocumentoTitulo> titulo = entityList.getFirst();
						DocumentoTitulo oTitulo = titulo.getObject();
					
						/* 
						 * Andre, 19/07/2008: resultado de um documento do arquivo de retorno 
						 */
						doc.setId(titulo.getId());
						doc.setPessoa(titulo.getProperty(DocumentoTitulo.CONTRATO).getValue().getAsEntity().toString());
						doc.setDataDocumento(titulo.getProperty(DocumentoTitulo.DATA).getValue().getAsCalendar());
						doc.setDataVencimento(titulo.getProperty(DocumentoTitulo.DATA_VENCIMENTO).getValue().getAsCalendar());

						IEntity<Ocorrencia> ultOcorrencia = UtilsOcorrencia.obterOcorrencia(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), Ocorrencia.RETORNO_LIQUIDADO_SEM_REGISTRO.getCodigo(), serviceDataOwner);

						/* 
						 * Verificando se o Título já foi processado e contém o código referente a RETORNO_LIQUIDADO_SEM_REGISTRO,  dessa forma 
						 * evitamos que o título seja baixado duas ou mais vezes, se o mesmo arquivo de retorno for processado diversas vezes
						 */
						if (!titulo.getProperty(DocumentoTitulo.ULTIMA_OCORRENCIA).getValue().isValueNull() && titulo.getProperty(DocumentoTitulo.ULTIMA_OCORRENCIA).getValue().getAsEntity().getProperty(Ocorrencia.CODIGO).getValue().getAsInteger().equals(ultOcorrencia.getProperty(Ocorrencia.CODIGO).getValue().getAsInteger())){
							tituloJaRetornadoValorTotal = tituloJaRetornadoValorTotal.add(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal());
							tituloJaRetornadoList.add(BusinessMessage.TYPE_INFO, Gerenciador756.class, "TITULO_JA_RETORNADO", titulo.getProperty(DocumentoTitulo.CONTRATO).getValue().getAsEntity().toString(), titulo.getProperty(DocumentoTitulo.NUMERO_DOCUMENTO).getValue().getAsString(), CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_VENCIMENTO).getValue().getAsCalendar()), DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal()), CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA).getValue().getAsCalendar()), DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal()));
							doc.setStatus(DocumentoRetornoStatus.DOCUMENTO_JA_LIQUIDADO);
							doc.setValorDocumento(titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal());
							doc.setMensagem(new BusinessMessage(BusinessMessage.TYPE_INFO, 
									GerenciadorBancoBasic.class, 
									"TITULO_JA_RETORNADO", 
									CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA).getValue().getAsCalendar()),
									DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal())
							).getMessage());

						}else{ //se ainda não foi retornado, continua a operação
							@SuppressWarnings("unchecked")
							IEntity<DocumentoCobrancaCategoria> documentoCobrancaCategoria = (IEntity<DocumentoCobrancaCategoria>)titulo.getPropertyValue(DocumentoTitulo.DOCUMENTO_COBRANCA_CATEGORIA);

							log.debug("Setando as novas propriedades do titulo, conforme informações do arquivo de retorno");
							//Setando as novas propriedades da Guia
							try {
								try{
									//A data no arquivo de retorno do SICOOB é do tipo ddMMyy, diferente do padrão ddMMyyyy
									titulo.setPropertyValue(DocumentoTitulo.DATA_CREDITO, detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_CREDITO));

								} catch (BusinessException e) {
									log.debug("ERRO ao atualizar 'Titulo'- " + titulo.getPropertyValue(DocumentoTitulo.DATA_CREDITO) + " -/- 'Arq Retorno'- " + detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_CREDITO));
									throw new Exception("Titulo " + CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_CREDITO).getValue().getAsCalendar()) + "-/- Arq. Retorno " + detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_CREDITO));
								}

								try{
									//A data no arquivo de retorno do SICOOB é do tipo ddMMyy, diferente do padrão ddMMyyyy
									titulo.setPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA, detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA_BANCO));
									doc.setDataOcorrencia(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA_BANCO));
								} catch (BusinessException e) {
									log.debug("ERRO ao atualizar 'Titulo'- " + titulo.getPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA) + " -/- 'Arq Retorno'- " + detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA_BANCO));
									throw new Exception("Titulo " + CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA).getValue().getAsCalendar()) + "-/- Arq. Retorno " + detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA_BANCO));
								}

								try{
									titulo.setPropertyValue(DocumentoTitulo.VALOR_PAGO, detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_PAGO));
									doc.setValorPago(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_PAGO));
								} catch (BusinessException e) {
									log.debug("ERRO ao atualizar 'Titulo'- " + titulo.getPropertyValue(DocumentoTitulo.VALOR_PAGO) + " -/- 'Arq Retorno'- " + detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_PAGO));
									throw new Exception("Titulo " + DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal()) + "-/- Arq. Retorno " + DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_PAGO)));                		
								}

								try{
									/* Subtitui o valor do titulo, pois o usuario pode ter pago um valor maioir no banco */ 
									doc.setMensagem("O valor do título foi modificado de R$ "+ DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal()) +"para R$" + detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO));
									doc.setValorDocumento(titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal());
									titulo.setPropertyValue(DocumentoTitulo.VALOR, detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO));
								} catch (BusinessException e) {
									log.debug("ERRO ao atualizar 'Titulo'- " + titulo.getPropertyValue(DocumentoTitulo.VALOR) + " -/- 'Arq Retorno'- " + detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO));
									throw new Exception("Titulo " + DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal()) + "-/- Arq. Retorno " + DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO)));
								}

								try{
									/*
									 * se a diferença entre a data de ocorrência no banco e a data de vencimento for um 
									 * long negativo ou 0, significa que o documento foi pago até a sua data de vencimento, 
									 * caso contrário, deve-se verificar os juros e multas (por causa do atraso)
									 */ 
									super.definirMultasJuros(titulo.getObject(), detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA_BANCO));

								} catch (BusinessException e) {
									log.debug("ERRO ao atualizar 'Titulo'- Valores Extras");
									throw new Exception("Titulo - Erro Valores Extras");
								}

								log.debug("Atualizando a ocorrência do titulo no sistema - Título Liquidado");
								/* Antes de ser atualizado, o título recebe a ocorrência correspondente ao retorno com sucesso (ver Ocorrencia.java) */
								try{
									if (detalhes.get(DetalheRetornoSemRegistro.CODIGO_OCORRENCIA).equals(OCORRENCIA_LIQUIDACAO)){
										IEntity<Ocorrencia> ultimaOcorrencia = UtilsOcorrencia.obterOcorrencia(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), Ocorrencia.RETORNO_LIQUIDADO_SEM_REGISTRO.getCodigo(), serviceDataOwner);
										titulo.setPropertyValue(DocumentoTitulo.ULTIMA_OCORRENCIA, ultimaOcorrencia);
										titulo.setPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA, detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA_BANCO));
										/* Adicionando ocorrência ao histórico de ocorrências deste titulo */
										IEntity<OcorrenciaControle> ocorrencia = UtilsCrud.create(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), OcorrenciaControle.class, serviceDataOwner);
										ocorrencia.setPropertyValue(OcorrenciaControle.TITULO, titulo);
										ocorrencia.setPropertyValue(OcorrenciaControle.OCORRENCIA, ultimaOcorrencia);
										ocorrencia.setPropertyValue(OcorrenciaControle.DATA_OCORRENCIA, detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA_BANCO));
										UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), ocorrencia, serviceDataOwner);
										titulo.getProperty(DocumentoTitulo.OCORRENCIAS).getValue().<OcorrenciaControle>getAsEntityList().add(ocorrencia);
									}
									else
										throw new Exception("O Titulo com id=" + titulo.getId() + " está com ocorrência no arquivo de retorno: " + detalhes.get(DetalheRetornoSemRegistro.CODIGO_OCORRENCIA) + ". - O código de ocorrência esperado é: " + OCORRENCIA_LIQUIDACAO + " (Liquidação).");
								} catch (BusinessException e) {
									log.debug("ERRO ao atualizar 'Titulo'- " + titulo.getPropertyValue(DocumentoTitulo.ULTIMA_OCORRENCIA) + " -/- 'Arq Retorno'- " + detalhes.get(DetalheRetornoSemRegistro.CODIGO_OCORRENCIA));
									throw new Exception("Titulo " + titulo.getProperty(DocumentoTitulo.ULTIMA_OCORRENCIA) + "-/- Arq. Retorno " + detalhes.get(DetalheRetornoSemRegistro.CODIGO_OCORRENCIA));
								}

								log.debug("Propriedades setadas com SUCESSO, atualizando titulo no sistema");
								/*
								 * Propriedades setadas com sucesso:
								 * - atualiza o titulo no sistema (update)
								 * - adiciona a mensagem de sucesso na lista
								 * - incrementa o contador de titulos que foram atualizados com sucesso
								 */
								UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), titulo, serviceDataOwner);
								tituloOkList.add(BusinessMessage.TYPE_INFO, Gerenciador756.class, "RETORNO_SUCESSO", titulo.getProperty(DocumentoTitulo.CONTRATO).getValue().getAsEntity().toString(), titulo.getProperty(DocumentoTitulo.NUMERO_DOCUMENTO).getValue().getAsString(), CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_VENCIMENTO).getValue().getAsCalendar()), DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal()), CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA).getValue().getAsCalendar()), DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal()));
								titulosAtualizados += 1;
								tituloOkValorTotal = tituloOkValorTotal.add(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal());

								/*
								 * Andre, 12/07/2008: Não deve ser quitado diretamente, chamar o quitarDocumento do gerenciador de documentos
								 */
								IGerenciadorDocumentoCobranca gerenciadorDocumento = this.getProvedorBanco().getProvedorDocumentoCobranca().retrieveGerenciadorDocumentoCobranca(oCedente.getNomeGerenciadorDocumento());
								gerenciadorDocumento.quitarDocumento(oTitulo, oTitulo.getLancamentos().get(0).getContaPrevista(), oTitulo.getDataUltimaOcorrencia(), oTitulo.getDataCredito(), serviceDataOwner);

//								/* Quita o documento com o valor do documento recebido do banco, com a opção de sobrepor o valor antigo do titulo */
//								IEntity grupo = titulo.getProperty(DocumentoTitulo.LANCAMENTOS).getValue().getAsEntityCollection().getFirst();
//								long grupoId = grupo.getId();

//								/* Considerando que o titulo ja foi Lançado no Financeiro, agora ele deve ser Quitado */
//								ServiceData sdQuitarGrupo = new ServiceData(QuitarLancamentoService.SERVICE_NAME, serviceDataOwner);
//								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_CONTA, contaId);
//								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_DATA, titulo.getPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA));
//								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_DOCUMENTO_PAGAMENTO_OPT, titulo);
//								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_LANCAMENTO, grupoId);
//								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_VALOR, titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal());
//								/* Andre, 19/06/2008: Campos juros e multa adicionados na hora de quitar um lançamento */	
//								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_JUROS_OPT, (!titulo.getProperty(DocumentoTitulo.VALOR_JUROS).getValue().isValueNull()) ? titulo.getProperty(DocumentoTitulo.VALOR_JUROS).getValue().getAsBigDecimal() : null);
//								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_MULTA_OPT, (!titulo.getProperty(DocumentoTitulo.VALOR_MULTA).getValue().isValueNull()) ? titulo.getProperty(DocumentoTitulo.VALOR_MULTA).getValue().getAsBigDecimal() : null);
////								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_SUBSTITUIR_VALOR_OPT, true);
//								this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().execute(sdQuitarGrupo);

//								log.debug("Serviço QuitarLancamentoService executado");

								/*
								 * ok, nenhum erro ocorreu
								 */
								doc.setStatus(DocumentoRetornoStatus.LIQUIDADO_COM_SUCESSO);

							} catch (BusinessException e) {
								log.debug("ERRO ao gravar a entidade (titulo):" + e.getMessage());
								doc.setStatus(DocumentoRetornoStatus.ERRO_ATUALIZANDO_DOCUMENTO);
								doc.setMensagem(e.getMessage());
								DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_GRAVANDO", titulo.getObject().toString()));
								de.getErrorList().addAll(e.getErrorList());
								throw de;
							} 
							catch (Exception e) {
								e.printStackTrace();
								log.debug("ERRO ao atualizar valores do titulo:" + e.getMessage());
								doc.setStatus(DocumentoRetornoStatus.ERRO_ATUALIZANDO_DOCUMENTO);
								doc.setMensagem(e.getMessage());
								DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_SETANDO", titulo.getObject().toString(), e.getMessage()));
								throw de;
							}
						}
					}
				}

				//Mostra quantos Titulos existem no Arquivo de Retorno e quantos Titulos puderam ser Atualizados no sistema
				serviceDataOwner.getMessageList().add(BusinessMessage.TYPE_INFO, Gerenciador756.class, "QUANTIDADE_TITULOS_PROCESSADOS", titulosAtualizados, titulosTotal);

				/* 
				 * - verifica se tem mensagens de Ok
				 * - adiciona na primeira posição a mensagem cabeçalho de Ok 
				 */
				if (tituloOkList.size() > 0){
					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador756.class, "CABECALHO_TITULOS_OK"));
					serviceDataOwner.getMessageList().addAll(tituloOkList);
					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador756.class, "RETORNO_SUCESSO_TOTAL", DecimalUtils.formatBigDecimal(tituloOkValorTotal)));
				}

				if (tituloJaRetornadoList.size() > 0){
					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador756.class, "CABECALHO_TITULOS_JA_RETORNADOS"));
					serviceDataOwner.getMessageList().addAll(tituloJaRetornadoList);
					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador756.class, "RETORNO_JA_RETORNADOS_TOTAL", DecimalUtils.formatBigDecimal(tituloJaRetornadoValorTotal)));
				}

				/* 
				 * - verifica se tem mensagens de NoOk
				 * - adiciona na primeira posição a mensagem cabeçalho de NoOk 
				 */
				if (tituloNoOkList.size() > 0){
					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador756.class, "CABECALHO_TITULOS_NO_OK"));
					serviceDataOwner.getMessageList().addAll(tituloNoOkList);
					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador756.class, "RETORNO_FALHA_TOTAL", DecimalUtils.formatBigDecimal(tituloNoOkValorTotal)));
					serviceDataOwner.getMessageList().add(BusinessMessage.TYPE_INFO, Gerenciador756.class, "INSTRUCAO_TITULOS_NO_OK");
				}

				serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador756.class, "RETORNO_TOTAL", DecimalUtils.formatBigDecimal(tituloNoOkValorTotal.add(tituloOkValorTotal).add(tituloJaRetornadoValorTotal))));

			}catch(BusinessException e){
				//se o arquivo não pôde ser interpretado, ex: se não for do tipo cnab240
				log.debug("ERRO ao interpretar o arquivo");
				DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_INTERPRETANDO_ARQUIVO", "(Nome do arquivo não disponível)"));
				de.getErrorList().addAll(e.getErrorList());
				throw de;
			}
			
			log.debug("encerrando método receberRetorno()");
			return result;
		}// fim do if para CNAB400

		else{ //versão CNAB 240 ainda não implementada - indicar erro de que o padrão que está no Cedente está incompatível com o gerenciador de bancos 756
			log.debug("encerrando método receberRetorno(), erro: padrão cnab não implementado ainda");
			DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "PADRAO_CNAB_DESCONHECIDO", oCedente.getLayoutCnab(), oCedente.toString(), GERENCIADOR_CODIGO+": "+GERENCIADOR_NOME));
			throw de;

		}

	}

	/**
	 * O arquivo de remessa do SICOOB deve gravar a data no formato "ddMMyy"
	 * Esta função converte para uma String no formato correto
	 */
	private String setData(Calendar data){
		return String.format("%02d",data.get(Calendar.DAY_OF_MONTH)) + String.format("%02d",data.get(Calendar.MONTH)+1) + String.format("%02d",data.get(Calendar.YEAR)-2000);
	}

	/** 
	 * <p>Esta função faz uso do isEmpty (StringUtils - jakarta), que verifica se a String é vazia ("") ou null;
	 * caso ela seja null, esta função retorna vazio ("") para evitar problemas de NullPointer, e caso não seja
	 * null ou vazia, retorna a própria String passada por parâmetro, sem modificações.
	 * 
	 * @param str - String a ser verificada 
	 * @return  String vazia ou a própria String passada por parâmetro
	 */
	private String verificarNull(String str){
		return (StringUtils.isEmpty(str)?"":str);
	}

	private void setRegistro(IRegistro registro, Campo campo, String nomePropriedade, String valor, boolean checkEmpty) throws BusinessException{
		try{
			registro.set(campo, valor, checkEmpty);
		}catch (PropriedadeVaziaException e) {
			throw new BusinessException(MessageList.create(Gerenciador756.class, "ERRO_SETANDO_REMESSA", nomePropriedade, valor));
		}
	}

	private void setRegistro(IRegistro registro, Campo campo,  String nomePropriedade, long valor){
			registro.setAsLong(campo, valor);
	}

	private void setRegistro(IRegistro registro, Campo campo,  String nomePropriedade, BigDecimal valor, boolean checkEmpty) throws BusinessException{
		try{
			registro.setAsDecimal(campo, valor, checkEmpty);
		}catch (PropriedadeVaziaException e) {
			throw new BusinessException(MessageList.create(Gerenciador756.class, "ERRO_SETANDO_REMESSA", nomePropriedade, valor));
		}	
	}

	/**
	* 39
	*/
	public String getCodigoBarras(IEntity<? extends DocumentoCobranca> documento) throws DocumentoCobrancaException {
		try{

			DocumentoTitulo oTitulo = (DocumentoTitulo)documento.getObject();
				
			Cedente oCedente = oTitulo.getCedente();

			StringBuffer result = new StringBuffer();

			//3 posições para o código do banco (1 a 3)
			result.append(br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(oCedente.getContaBancaria().getBanco().getCodigo(), 3, true));

			//1 posição para moeda (4 a 4)
			result.append("9");

			//4 posições para o fator de vencimento (6 a 9) - calculado a partir de 07/10/1997) 
			Long fatorVencimento = CalendarUtils.diffDay(oTitulo.getDataVencimento(), CalendarUtils.getCalendar(1997, Calendar.OCTOBER, 7));
			result.append(br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(fatorVencimento.toString(), 4, false));

			//10 dígitos para o valor (10 a 19)
			//FIXME - verificar se o método format da classe String retira a pontuação do número BigDecimal
			result.append(br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(String.format("%010d", getValorParaCodigoBarras(oTitulo).multiply(new BigDecimal("100")).longValue()), 10, true));

			//25 dígitos para o campo livre (20 a 44)
			result.append(this.getCampoLivre(documento));

			//1 posição para o DV (5 a 5)
			//OBS - o cálculo do DV só deve ser efetuado por último e adicionado na posição 5 do código de barras
			String dv = MathUtils.modulo11(result.toString());
			result.insert(4, dv);

			return result.toString();

		}catch (BusinessException e) {
			throw new DocumentoCobrancaException(e.getErrorList());
		}
	}

	public String getLinhaDigitavel(IEntity<? extends DocumentoCobranca> documento) throws DocumentoCobrancaException {
		DocumentoTitulo oTitulo = (DocumentoTitulo)documento.getObject();

		StringBuffer result = new StringBuffer();
		String campoLivre = getCampoLivre(documento);

		/* 
		 * primeiro campo: 
		 * código do banco (posição 1 a 3 do código de barras) + 
		 * código da moeda (posição 4 do código de barras) + 
		 * cinco primeiras posições do campo livre (posições 20 a 24 do código de barras) + 
		 * DV 
		 */
		StringBuffer campo1 = new StringBuffer();
		campo1.append(br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(oTitulo.getCedente().getContaBancaria().getBanco().getCodigo(), 3, true));
		campo1.append("9");
		campo1.append(campoLivre.substring(0, 5));
		String campo1dv = MathUtils.modulo10(campo1.toString());
		result.append(campo1).append(campo1dv);

		/*
		 * segundo campo:
		 * posição 1 a 10 do campo livre + 
		 * DV
		 */
		StringBuffer campo2 = new StringBuffer();
		campo2.append(campoLivre.substring(5, 15));
		String campo2dv = MathUtils.modulo10(campo2.toString());
		result.append(campo2).append(campo2dv);

		/*
		 * terceiro campo:
		 * posições 11 a 20 do campo livre + 
		 * DV
		 */
		StringBuffer campo3 = new StringBuffer();
		campo3.append(campoLivre.substring(15, 25));
		String campo3dv = MathUtils.modulo10(campo3.toString());
		result.append(campo3).append(campo3dv);

		/*
		 * quarto campo:
		 * DV geral do código de barras (posição 5 do código de barras)
		 */
		String codigoBarras = getCodigoBarras(documento);
		result.append(" ");
		result.append(codigoBarras.substring(4, 5));

		/*
		 * quinto campo:
		 * fator vencimento (posições 6 a 9 do código de barras) + 
		 * valor do documento (posições 10 a 19 do código de barras)
		 */
		result.append(" ");
		result.append(codigoBarras.substring(5, 9));
		result.append(codigoBarras.substring(9, 19));
		
		/*
		 * Formatação da linha digitável
		 */
		result.insert(5, ".");
		result.insert(11, " ");
		result.insert(17, ".");
		result.insert(24, " ");
		result.insert(30, ".");
		
		return result.toString();
	}

	public static void main(String[] args) {
		 BigDecimal multa = new BigDecimal(0.02); 
		 BigDecimal juros = new BigDecimal((0.01/30) * 8); 
		 BigDecimal valorPago = new BigDecimal(34.20); 

		/* Expressão para presumir o valor original baseando no valor pago e os
		 * percentuais de multa e juros que deveriam ter sido aplicados.
		 * x->valor pago e y->valor original
		 * x = y + y*mu + y*ju  (/y)
		 * x/y = y/y + y*mu/y + y*ju/y -> x/y = 1 + mu + ju (/x)
		 * 1/y = 1/x + mu/x + ju/x  (*y)
		 * y*(1/y) = y*(1/x + mu/x + ju/x) -> 1 = y*(1/x + mu/x + ju/x)
		 * y = 1/(1/x + mu/x + ju/x)
		 */
		 BigDecimal.ONE.divide(valorPago, MathContext.DECIMAL32);
		 BigDecimal divisor =  BigDecimal.ONE.divide(valorPago, MathContext.DECIMAL32).add(multa.divide(valorPago, MathContext.DECIMAL32)).add(juros.divide(valorPago, MathContext.DECIMAL32));
         BigDecimal valorPresumido = BigDecimal.ONE.divide(divisor, MathContext.DECIMAL32).setScale(2, RoundingMode.FLOOR);
         System.out.println(valorPresumido);
		

	}
}
