package br.com.orionsoft.financeiro.documento.cobranca.titulo.banco748;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.LockMode;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.cnab.ICampo;
import br.com.orionsoft.cnab.IRegistro;
import br.com.orionsoft.cnab.banco748.cnab400.DetalheRemessaSemRegistro;
import br.com.orionsoft.cnab.banco748.cnab400.DetalheRetornoSemRegistro;
import br.com.orionsoft.cnab.banco748.cnab400.Header;
import br.com.orionsoft.cnab.banco748.cnab400.Instrucao;
import br.com.orionsoft.cnab.banco748.cnab400.Trailer;
import br.com.orionsoft.cnab.banco756.cnab400.MotivosOcorrencia;
import br.com.orionsoft.cnab.cnab400.Arquivo;
import br.com.orionsoft.cnab.cnab400.Remessa;
import br.com.orionsoft.cnab.cnab400.Retorno;
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
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.services.QuitarLancamentoService;
import br.com.orionsoft.financeiro.gerenciador.utils.UtilsLancamento;
import br.com.orionsoft.financeiro.utils.UtilsOcorrencia;
import br.com.orionsoft.financeiro.utils.UtilsRemessa;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.core.util.MathUtils;
import br.com.orionsoft.monstrengo.core.util.StringUtils;
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
 */
public class Gerenciador748 extends GerenciadorBancoBasic
{

	public static final String GERENCIADOR_NOME = "SICREDI";
	public static final String GERENCIADOR_CODIGO = "748";

	/* Auxiliares para cálculo da linha digitável */
	public static final String POSTO = "28";
	public static final String PARCELA = "001";

	public static final String OCORRENCIA_REGISTRADO = "02";
	public static final String OCORRENCIA_REJEITADO = "03";
	public static final String OCORRENCIA_LIQUIDACAO = "06"; 
	public static final String OCORRENCIA_TARIFAS = "28";
	
	public static final long ESPECIE_TITULO = 01; //Duplicata
	public static final String EXTENSAO_PRIMEIRA_REMESSA = ".CRM";
	public static final String EXTENSAO_DEMAIS_REMESSA = ".RM%0d"; // d é o número de remessas do dia e > 1
	public static int NOSSO_NUMERO_LIMITE = 99999; //padrão 99999 (noventa e nove mil e novecentos e noventa e nove)
	public static int[] MAPA_OCORRENCIAS = new int[]{
		1001	,	99	,//"Remessa - Registrar título no banco"
		1003	,	99	,//"Remessa - Baixar"
		1005	,	99	,//"Remessa - Conceder abatimento"
		1006	,	99	,//"Remessa - Cancelar abatimento"
		1007	,	99	,//"Remessa - Conceder desconto"
		1008	,	99	,//"Remessa - Cancelar desconto"
		1009	,	99	,//"Remessa - Alterar vencimento"
		1010	,	99	,//"Remessa - Protestar"
		1011	,	99	,//"Remessa - Cancelar instrução de prote
		1012	,	99	,//"Remessa - Dispensar juros"
		1013	,	99	,//"Remessa - Alterar nome-endereço do sa
		1014	,	99	,//"Remessa - Alterar número de controle"
		1016	,	2	,//"Retorno - Registro confirmado"
		1017	,	3	,//"Retorno - Registro recusado"
		1018	,	3	,//"Retorno - Comando recusado"
		1019	,	6	,//"Retorno - Liquidado"
		1020	,	15	,//"Retorno - Liquidado em Cartório"
		1026	,	9	,//"Retorno - Baixado"
		1027	,	10	,//"Retorno - Baixado por devolução"
		1029	,	10	,//"Retorno - Baixa por protesto"
		1057	,	23	,//"Retorno - Encaminhado ao Cartório"
		1058	,	34	,//"Retorno - Retirado de Cartório"
		1072	,	36	,//"Retorno - Outras ocorrências"
		1080	,	28	 //"Retorno -Cobrança de Tarifa Bancária"
	};
	
	/*
	 * 7 (sete) é o tamanho definido para o código do Cedente  
	 * DEVE ser definido pois senão o Código de Barras não é gerado corretamente
	 */
	public static final int CEDENTE_CODIGO_LENGTH = 5;
	public static final int NOSSO_NUMERO_LENGTH = 9;

	public String getNome() {
		return GERENCIADOR_NOME;
	}

	public String getCodigo() {
		return GERENCIADOR_CODIGO;
	}
	
	public String getCampoLivre(IEntity<? extends DocumentoCobranca> documento) throws DocumentoCobrancaException{
		DocumentoTitulo titulo = (DocumentoTitulo)documento.getObject();
			
			StringBuffer campoLivre = new StringBuffer();
			
			// 1 1-Cobranca com registro 3-Sem registro 
			campoLivre.append(br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(titulo.getCedente().getCarteiraCodigo(), 1, true));

			// 1 1-Carteira simples
			campoLivre.append("1");
			
			// 9 Nosso Numero
			campoLivre.append(br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(titulo.getNumeroDocumento(), NOSSO_NUMERO_LENGTH, true));

			// 4 Agencia
			campoLivre.append(br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(titulo.getCedente().getContaBancaria().getAgenciaCodigo(), 4, true));
			
			// 2 Posto !!!!!!!!1
			campoLivre.append(POSTO);
			
			// 5 Codigo Cedente
			campoLivre.append(br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(titulo.getCedente().getCedenteCodigo(), CEDENTE_CODIGO_LENGTH, true));
					
			// 1 Tem ou não valor expresso no título
			if(titulo.getValor().intValue() == 0)
				campoLivre.append("0");
			else
				campoLivre.append("1");
				
			// 1 Filler
			campoLivre.append("0");
			
			// 1 DV
			campoLivre.append(MathUtils.modulo11a(campoLivre.toString()));

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
			if (nossoNumeroSequencial == 0)
				nossoNumeroSequencial = 1; //evita que o numero se "perca" caso o valor seja 0 e a data esteja correta 

			Calendar anoAtual = Calendar.getInstance();
			int nossoNumeroAno = oCedente.getNossoNumeroAno();
			if (nossoNumeroAno != anoAtual.get(Calendar.YEAR)){
				nossoNumeroAno = anoAtual.get(Calendar.YEAR);
				nossoNumeroSequencial = 1; // Zera a cada ano
				oCedente.setNossoNumeroAno(anoAtual.get(Calendar.YEAR));
			}

			if (nossoNumeroSequencial > NOSSO_NUMERO_LIMITE)
				nossoNumeroSequencial = 1;

			//para o SICREDI o formato do nosso numero é YYDXXXXX, onde YY é o ano, D (1 gerado pelo banco acima disto é pelo Cedente) e XXXXX é o número sequencial
			String nossoNumero = String.format("%02d",(nossoNumeroAno - 2000)) + "2" + String.format("%05d", nossoNumeroSequencial);

			//Dígito é aaaappccccc + YYDXXXXX Módulo 11: aaaa-agência pp-posto ccccc-código cedente	
			String baseDigitoNossoNumero = StringUtils.formatNumber(oCedente.getContaBancaria().getAgenciaCodigo(), 4, false) + POSTO + StringUtils.formatNumber(oCedente.getCedenteCodigo(), 5, true) + nossoNumero;
			nossoNumero += MathUtils.modulo11a(baseDigitoNossoNumero);

			nossoNumeroSequencial += 1;
			log.debug("NOSSONUMERO:" + nossoNumeroSequencial);
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
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "REMESSA_NENHUM_TITULO"));


		//Caso encontre títulos, tenta construir (preencher) o arquivo de remessa
		Arquivo arquivo = new Arquivo();
		List<IRegistro> registros = new ArrayList<IRegistro>();

		/* Construindo Estrutura Básica */
		IRegistro header = null;
		IRegistro detalhe = null;
		IRegistro instrucao = null;
		IRegistro trailer = null; 

		/* Inserindo valores no arquivo de remessa */
		/* Construindo Header */
		Cedente oCedente = (Cedente) convenioCobranca.getObject();
		int remessaDiariaSequencial = 1;
		int remessaSequencial = oCedente.getRemessaNumeroSequencial() + 1;
		try{
			long diferencaDias = 0;
			Calendar dataUltimaRemessa = convenioCobranca.getProperty(Cedente.REMESSA_ULTIMA_DATA).getValue().getAsCalendar();
			Calendar data = CalendarUtils.getCalendar();
			diferencaDias = CalendarUtils.diffDay(data, dataUltimaRemessa);

			if(diferencaDias == 0){ //se o dia for o mesmo, incrementa 1 do dia. isto permite que PELO MENOS dois arquivos sejam enviados num mesmo dia para o SICREDI
				remessaDiariaSequencial++;
			}else if(diferencaDias < 0){ //a diferença não deve ser negativa
				throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_MONTANDO_NOME_ARQUIVO", "Diferença entre a data atual e a data da última remessa (NÃO pode ser negativa): " + diferencaDias));
			}

			header = new Header("");
			setRegistro(header, Header.CODIGO_CEDENTE, "CODIGO_CEDENTE", oCedente.getCedenteCodigo(), true);
			setRegistro(header, Header.CPF_CNPJ_CEDENTE, "CPF_CNPJ_CEDENTE", oCedente.getContratante().getDocumento(), true);
			setRegistro(header, Header.DATA_GRAVACAO_ARQUIVO, "DATA_GRAVACAO_ARQUIVO", CalendarUtils.formatDate("yyyyMMdd", Calendar.getInstance()), true);
			setRegistro(header, Header.NUMERO_REMESSA, "NUMERO_REMESSA", remessaSequencial + "" ,true);
			setRegistro(header, Header.NUMERO_SEQUENCIAL_REGISTRO, "NUMERO_SEQUENCIAL", ++numeroSequencial);
		}catch (BusinessException e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_CRIANDO_ARQUIVO"));
		} catch (Exception e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_CRIANDO_ESTRUTURA_REMESSA"));
		}

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
				throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_CRIANDO_ESTRUTURA_REMESSA"));
			}

			try{
				/* Construindo DetalheSemRegistro */
				setRegistro(detalhe, DetalheRemessaSemRegistro.NOSSO_NUMERO, "NOSSO_NUMERO", oTitulo.getNumeroDocumento(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.MULTA_POR_ATRASO, "MULTA_POR_ATRASO", oTitulo.getDocumentoCobrancaCategoria().getMultaAtraso(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.SEU_NUMERO, "SEU_NUMERO", oTitulo.getId()+"", true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.DATA_VENCIMENTO, "DATA_VENCIMENTO", CalendarUtils.formatDate("ddMMyy", oTitulo.getDataVencimento()), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.VALOR_TITULO, "VALOR_TITULO", oTitulo.getValor(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.DATA_EMISSAO, "DATA_EMISSAO", CalendarUtils.formatDate("ddMMyy", oTitulo.getData()), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.VALOR_PERC_JUROS_POR_DIA_ATRASO, "VALOR_PERC_JUROS_POR_DIA_ATRASO", oTitulo.getDocumentoCobrancaCategoria().getJurosMora().divide(new BigDecimal(30), 4, RoundingMode.HALF_UP), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.TIPO_PESSOA_SACADO, "TIPO_PESSOA_SACADO", (oTitulo.getContrato().getPessoa().isFisica()?"1":"2"), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.CPF_CNPJ_SACADO, "CPF_CNPJ_SACADO", oTitulo.getContrato().getPessoa().getDocumento(), true);
				/*Lucio 20100822 - Coloca o código do escritório antes do nome do sacado para facilitar a organizaçã */
				String nomeSacado = null;
				if(oTitulo.getContrato().getPessoa().getEscritorioContabil() != null)
					nomeSacado = "[" + oTitulo.getContrato().getPessoa().getEscritorioContabil().getId() + "]" + oTitulo.getContrato().getPessoa().getNome();
				else
					nomeSacado = "[nd]" + oTitulo.getContrato().getPessoa().getNome();
					
				setRegistro(detalhe, DetalheRemessaSemRegistro.NOME_SACADO, "NOME_SACADO", StringUtils.removeAccent(nomeSacado.toUpperCase()), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.ENDERECO_SACADO, "ENDERECO_SACADO", StringUtils.removeAccent(UtilsRemessa.formatarEndereco(oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia(), DetalheRemessaSemRegistro.ENDERECO_SACADO.getSize())).toUpperCase(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.CEP_SACADO, "CEP_SACADO", oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getCep(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.CIDADE_SACADO, "CIDADE_SACADO", StringUtils.removeAccent(oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getMunicipio().getNome().toUpperCase()), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.ESTADO_SACADO, "ESTADO_SACADO", oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getMunicipio().getUf().getSigla(), true);
				setRegistro(detalhe, DetalheRemessaSemRegistro.SEQUENCIAL_REGISTRO, "SEQUENCIAL_REGISTRO", ++numeroSequencial);

				/* Construindo Instrução */
				setRegistro(instrucao, Instrucao.NOSSO_NUMERO, "NOSSO_NUMERO", oTitulo.getNumeroDocumento(), true);
				setRegistro(instrucao, Instrucao.INSTRUCAO_1, "MENSAGEM_1", br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(verificarNull(oTitulo.getDocumentoCobrancaCategoria().getInstrucoes1())).toUpperCase(), false);
				setRegistro(instrucao, Instrucao.INSTRUCAO_2, "MENSAGEM_2", br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(verificarNull(oTitulo.getDocumentoCobrancaCategoria().getInstrucoes2())).toUpperCase(), false);
				setRegistro(instrucao, Instrucao.INSTRUCAO_3, "MENSAGEM_3", br.com.orionsoft.monstrengo.core.util.StringUtils.removeAccent(verificarNull(oTitulo.getInstrucoes3())).toUpperCase(), false);
				setRegistro(instrucao, Instrucao.SEU_NUMERO, "SEU_NUMERO", oTitulo.getId()+"", true);
				setRegistro(instrucao, Instrucao.SEQUENCIAL_REGISTRO, "SEQUENCIAL_REGISTRO", ++numeroSequencial);

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
					throw new DocumentoCobrancaException(MessageList.create(BusinessMessage.TYPE_INFO, Gerenciador748.class, "REMESSA_ERRO_AO_SETAR", oTitulo.getContrato().toString(), oTitulo.getNumeroDocumento(), CalendarUtils.formatDate(oTitulo.getDataVencimento()), oTitulo.getValor(), e.getMessage()));
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
					throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_ATUALIZANDO_ENTIDADE", titulo.getObject().toString()));
				}
				titulosInseridosOk += 1;
				tituloOkList.add(BusinessMessage.TYPE_INFO, Gerenciador748.class, "REMESSA_SUCESSO", oTitulo.getContrato().toString() ,oTitulo.getNumeroDocumento(), CalendarUtils.formatDate(oTitulo.getDataVencimento()), DecimalUtils.formatBigDecimal(oTitulo.getValor()));
				tituloOkValorTotal = tituloOkValorTotal.add(oTitulo.getValor());
			}else{
				tituloNoOkValorTotal = tituloNoOkValorTotal.add(oTitulo.getValor()) ;
			}
		}//for

		/* Construindo Trailer */
		try{
			trailer = new Trailer("");
			setRegistro(trailer, Trailer.CODIGO_CEDENTE, "CODIGO_CEDENTE", oCedente.getCedenteCodigo(), true);	
			setRegistro(trailer, Trailer.NUMERO_SEQUENCIAL_REGISTRO, "NUMERO_SEQUENCIAL_REGISTRO", ++numeroSequencial);	
		}catch (BusinessException e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_CRIANDO_ARQUIVO"));
		} catch (Exception e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_CRIANDO_ESTRUTURA_REMESSA"));
		}

		/* Todos as partes foram montadas, inserindo valores no arquivo de remessa */
		arquivo.setHeader(header);
		arquivo.set(Arquivo.DETALHE, registros);
		arquivo.setTrailer(trailer);

		/* Obtendo o cnpj do contratante e o numero sequencial da remessa para montar o nome do arquivo de remessa */
		String cnpj = null;
		String nomeArquivoRemessa = null;
		try {
			String cnpjTemp = (String) convenioCobranca.getProperty(ConvenioCobranca.CONTRATANTE).getValue().getAsEntity().getProperty(Pessoa.DOCUMENTO).getValue().getAsObject();
			cnpj = br.com.orionsoft.monstrengo.core.util.StringUtils.formatNumber(cnpjTemp, 14, true); //14 é o tamanho do campo CNPJ
			if (cnpj.equals("") || cnpj.equals(null))
				throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_MONTANDO_NOME_ARQUIVO", "CNPJ atribuído: " + cnpj));

			Calendar data = CalendarUtils.getCalendar();

			String dia = String.format("%02d", data.get(Calendar.DAY_OF_MONTH));
			int iMes = data.get(Calendar.MONTH)+1; //+1 pois os meses no Calendar iniciam em 0
			String mes;
			switch (iMes) {
				case 10: mes = "O";break;
				case 11: mes = "N";break;
				case 12: mes = "D";break;
				default: mes = String.format("%d", iMes); break;
			}
			
			nomeArquivoRemessa = oCedente.getCedenteCodigo() + mes + dia;
			if(remessaDiariaSequencial==1)
				nomeArquivoRemessa += EXTENSAO_PRIMEIRA_REMESSA;
			else
				nomeArquivoRemessa += String.format(EXTENSAO_PRIMEIRA_REMESSA, remessaDiariaSequencial);
			
			if(nomeArquivoRemessa.length() != 12) //CCCCCMDD.CRM Página 27 do manual
				throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_MONTANDO_NOME_ARQUIVO", "Tamanho do nome do arquivo (esperado 12: CCCCCMDD.CRM) :" + nomeArquivoRemessa.length()));

		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_MONTANDO_NOME_ARQUIVO", e.getMessage()));
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
		try {
			tempFileDir = File.createTempFile("remessa",null);

			tempFile = new File(tempFileDir.getParent(),nomeArquivoRemessa);
		} catch (IOException e1) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_CRIANDO_ARQUIVO", e1.getMessage()));		} 
		Remessa remessa = null;

		try {
			remessa = new Remessa(tempFile, arquivo);
			tempFile = remessa.getFile();

			//por fim, o número sequencial da remessa é incrementado na entidade cedente, e a data da última remessa é atualizada
			convenioCobranca.setPropertyValue(Cedente.REMESSA_NUMERO_SEQUENCIAL, remessaSequencial);
			convenioCobranca.setPropertyValue(Cedente.REMESSA_ULTIMA_DATA, CalendarUtils.getCalendar());
			UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), convenioCobranca, serviceDataOwner);
		} catch (Exception e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_ATUALIZANDO_CEDENTE", tempFileDir.getParent()));
		}

		//Mostra quantos Titulos puderam ser inseridos no arquivo de remessa
		serviceDataOwner.getMessageList().add(BusinessMessage.TYPE_INFO, Gerenciador748.class, "REMESSA_QUANTIDADE_TITULOS", titulosInseridosOk, titulosTotal);

		/* 
		 * - verifica se tem mensagens de Ok
		 * - adiciona na primeira posição a mensagem cabeçalho de Ok 
		 */
		if (tituloOkList.size() > 0){
			serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador748.class, "REMESSA_CABECALHO_TITULOS_OK"));
			serviceDataOwner.getMessageList().addAll(tituloOkList);
			serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador748.class, "REMESSA_SUCESSO_TOTAL", DecimalUtils.formatBigDecimal(tituloOkValorTotal)));
		}

		/* 
		 * - verifica se tem mensagens de NoOk
		 * - adiciona na primeira posição a mensagem cabeçalho de NoOk 
		 */
		if (tituloNoOkList.size() > 0){
			serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador748.class, "REMESSA_CABECALHO_TITULOS_NO_OK"));
			serviceDataOwner.getMessageList().addAll(tituloNoOkList);
			serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador748.class, "REMESSA_FALHA_TOTAL", DecimalUtils.formatBigDecimal(tituloNoOkValorTotal)));
		}

		log.debug("Encerrando com sucesso o método gerarRemessa()");
		return tempFile;

	}

	public List<DocumentoRetornoResultado> receberRetorno(IEntity<? extends ConvenioCobranca> convenioCobranca, InputStream dados, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		Cedente oCedente = (Cedente) convenioCobranca.getObject();
		if (oCedente.getLayoutCnab().equals(GerenciadorDocumentoTitulo.CNAB_400)){

			Retorno retorno;

			try {
				log.debug("Lendo os dados do retorno");
				retorno = new Retorno(dados, Header.class, Trailer.class, new Class<?>[]{DetalheRetornoSemRegistro.class});
			} catch (Exception e) {
				log.debug("Erro ao preparar os dados do retorno:" + e.getMessage());
				throw new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_INTERPRETANDO_ARQUIVO", "(Nome do arquivo não disponível) MSG:" + e.getMessage()));		
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

					/* Andre, 19/07/2008: resultado de um documento do arquivo de retorno */
					DocumentoRetornoResultado doc = new DocumentoRetornoResultado();
					doc.setId(IDAO.ENTITY_UNSAVED);
					doc.setNumeroDocumento(nossoNumero);
					result.add(doc);

					try {
						/* Se nenhum titulo for encontrado na pesquisa, adiciona o erro na lista e continua o programa; */
						if (entityList.getSize() == 0){
							tituloNoOkList.add(BusinessMessage.TYPE_INFO, 
									Gerenciador748.class, 
									"TITULO_NAO_ENCONTRADO", 
									detalhes.get(DetalheRetornoSemRegistro.NOSSO_NUMERO), 
									"(Não consta)", 
									CalendarUtils.formatDate(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA)), 
									DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO)), 
									DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_EFETIVAMENTE_PAGO)));
							tituloNoOkValorTotal = tituloNoOkValorTotal.add(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_EFETIVAMENTE_PAGO));

							doc.setStatus(DocumentoRetornoStatus.NUMERO_DOCUMENTO_NAO_ENCONTRADO);

						}else if (entityList.getSize() > 1){
							tituloNoOkList.add(BusinessMessage.TYPE_INFO, 
									Gerenciador748.class, 
									"TITULO_ID_DUPLICADO", 
									detalhes.get(DetalheRetornoSemRegistro.NOSSO_NUMERO), 
									"(Não consta)", 
									detalhes.get(DetalheRetornoSemRegistro.VALOR_TITULO), 
									CalendarUtils.formatDate(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA)), 
									DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_EFETIVAMENTE_PAGO)));

							doc.setStatus(DocumentoRetornoStatus.NUMERO_DOCUMENTO_DUPLICADO);
						}else{
							IEntity<DocumentoTitulo> titulo = entityList.getFirst();
							DocumentoTitulo oTitulo = titulo.getObject();

							/* Andre, 19/07/2008: resultado de um documento do arquivo de retorno */
							doc.setId(oTitulo.getId());
							doc.setPessoa(oTitulo.getContrato().toString());
							doc.setDataDocumento(oTitulo.getData());
							doc.setDataVencimento(oTitulo.getDataVencimento());

							IEntity<Ocorrencia> ultimaOcorrencia = UtilsOcorrencia.obterOcorrencia(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), 
									detalhes.getAsInteger(DetalheRetornoSemRegistro.OCORRENCIA),
									MAPA_OCORRENCIAS,
									serviceDataOwner);
							String ultimaOcorrenciaMotivo = MotivosOcorrencia.MOTIVOS.get(detalhes.get(DetalheRetornoSemRegistro.MOTIVO_OCORRENCIA));
							
							log.debug("Verificando ocorrência repetida no titulo sistema");
							boolean achouOcorrenciaControle = false;
							for(OcorrenciaControle ocorrenciaControle: titulo.getObject().getOcorrencias()){
								if(ocorrenciaControle.getOcorrencia().equals(
										ultimaOcorrencia.getObject()) && 
										ocorrenciaControle.getDataOcorrencia().equals(
												detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA))){
									achouOcorrenciaControle = true;
									break;
								}
							}

							/* Verificando se o Título já foi processado com esta ocorrência e data_ocorrencia
							   Evitamos que o título seja baixado duas ou mais vezes, se o mesmo arquivo de retorno for processado diversas vezes
							   Evitamos que uma ocorrência seja processada mais de uma vez */
							if (achouOcorrenciaControle){
								BusinessMessage message = new BusinessMessage(BusinessMessage.TYPE_INFO, 
										Gerenciador748.class, 
										"OCORRENCIA_JA_PROCESSADA", 
										oTitulo.getContrato() + "<br/>" + ultimaOcorrencia.toString(),
										oTitulo.getNumeroDocumento(),
										CalendarUtils.formatDate(oTitulo.getDataVencimento()),
										DecimalUtils.formatBigDecimal(oTitulo.getValor()) + " / " + detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO),
										CalendarUtils.formatDate(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA)),
										DecimalUtils.formatBigDecimal(oTitulo.getValorPago()) + " / " + detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_EFETIVAMENTE_PAGO)
										);
								
								doc.setStatus(DocumentoRetornoStatus.DOCUMENTO_JA_LIQUIDADO);
								doc.setValorDocumento(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO));
								doc.setMensagem(message.getMessage());

								tituloJaRetornadoValorTotal = tituloJaRetornadoValorTotal.add(oTitulo.getValor());
								tituloJaRetornadoList.add(message);
							}else{
								/* Antes de ser atualizado, o título recebe a ocorrência correspondente ao retorno com sucesso (ver Ocorrencia.java) */
								try{
									oTitulo.setUltimaOcorrencia(ultimaOcorrencia.getObject());
									oTitulo.setDataUltimaOcorrencia(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA));
									/* Adicionando ocorrência ao histórico de ocorrências deste titulo */
									IEntity<OcorrenciaControle> ocorrencia = UtilsCrud.create(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), OcorrenciaControle.class, serviceDataOwner);
									OcorrenciaControle oOcorrenciaControle = ocorrencia.getObject();
									oOcorrenciaControle.setTitulo(oTitulo);
									oOcorrenciaControle.setOcorrencia(ultimaOcorrencia.getObject());
									oOcorrenciaControle.setMotivo(ultimaOcorrenciaMotivo);
									oOcorrenciaControle.setDataOcorrencia(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA));
									UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), ocorrencia, serviceDataOwner);
									oTitulo.getOcorrencias().add(oOcorrenciaControle);
									UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), titulo, serviceDataOwner);

									/* Marcar o documento com STATUS */
									doc.setStatus(DocumentoRetornoStatus.OCORRENCIA_VALIDA);
								} catch (BusinessException e) {
									log.debug("ERRO ao atualizar 'Titulo'- " + oTitulo.getUltimaOcorrencia() + " -/- 'Arq Retorno'- " + detalhes.get(DetalheRetornoSemRegistro.OCORRENCIA));
									throw new Exception("Titulo " + oTitulo.getUltimaOcorrencia() + "-/- Arq. Retorno " + detalhes.get(DetalheRetornoSemRegistro.OCORRENCIA));
								}

								if(OCORRENCIA_TARIFAS.equals(detalhes.get(DetalheRetornoSemRegistro.OCORRENCIA))){
									/* Lucio 20121105: O valor da tarifa vem no campo VALOR_TITULO*/
									oTitulo.setValorTarifa(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO));
									UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), titulo, serviceDataOwner);

									Lancamento lancamentoTarifa = UtilsLancamento.inserir(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), 
															oTitulo.getLancamentos().get(0).getContaPrevista(),
															oTitulo.getCedente().getContratado(),
															CalendarUtils.getCalendar(),
															detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_LANCAMENTO_CC),
															"Tarifa bancária", //descricaoOpt, 
															null, // Operação 
															Transacao.DEBITO,
															oTitulo.getCedente().getCentroCustoGeral(),
															null, //classificacaoContabil
															oTitulo.getCedente().getItemCustoTarifa(),
															oTitulo.getValorTarifa(),
															null, //documentoCobrancaCategoriaOpt
															false, //naoReceberAposVencimento
															serviceDataOwner);
									
									lancamentoTarifa.setDocumentoCobranca(oTitulo);
									UtilsCrud.objectUpdate(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), oTitulo, serviceDataOwner);
									
							        ServiceData quitarLancamento = new ServiceData(QuitarLancamentoService.SERVICE_NAME, serviceDataOwner);
							        quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_CONTA, lancamentoTarifa.getContaPrevista());
							        quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_DATA, lancamentoTarifa.getDataVencimento());
							        quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_LANCAMENTO, lancamentoTarifa);
							        quitarLancamento.getArgumentList().setProperty(QuitarLancamentoService.IN_VALOR, lancamentoTarifa.getValor().abs());
							        
							        this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().execute(quitarLancamento);

									/* Registra a mensagem ocorrência da TARIFA  */
									BusinessMessage message = new BusinessMessage(BusinessMessage.TYPE_INFO, 
											Gerenciador748.class, 
											"OCORRENCIA_PROCESSADA", 
											oTitulo.getContrato() + "<br/>" + ultimaOcorrencia.toString() + ":<b>" + ultimaOcorrenciaMotivo + "</b>",
											oTitulo.getNumeroDocumento(),
											CalendarUtils.formatDate(oTitulo.getDataVencimento()),
											DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO)),
											CalendarUtils.formatDate(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA)),
											DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_EFETIVAMENTE_PAGO))
											);
									
									tituloJaRetornadoValorTotal = tituloJaRetornadoValorTotal.add(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO));
									tituloJaRetornadoList.add(message);

								}else
									if (OCORRENCIA_LIQUIDACAO.equals(detalhes.get(DetalheRetornoSemRegistro.OCORRENCIA))){ 
										//se ainda não foi retornado, continua a operação
										@SuppressWarnings("unchecked")
										IEntity<DocumentoCobrancaCategoria> documentoCobrancaCategoria = (IEntity<DocumentoCobrancaCategoria>)titulo.getPropertyValue(DocumentoTitulo.DOCUMENTO_COBRANCA_CATEGORIA);

										log.debug("Setando as novas propriedades do titulo, conforme informações do arquivo de retorno");
										//Setando as novas propriedades da Guia
											//A data no arquivo de retorno do SICOOB é do tipo ddMMyy, diferente do padrão ddMMyyyy
											oTitulo.setDataCredito(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_LANCAMENTO_CC));
											//A data no arquivo de retorno do SICOOB é do tipo ddMMyy, diferente do padrão ddMMyyyy
											oTitulo.setDataUltimaOcorrencia(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA));
											doc.setDataOcorrencia(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA));

											oTitulo.setValorPago(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_EFETIVAMENTE_PAGO));
											doc.setValorPago(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_EFETIVAMENTE_PAGO));

										try{
											/* Subtitui o valor do titulo, pois o usuario pode ter pago um valor maioir no banco */ 
											oTitulo.setValor(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO));
											doc.setMensagem("O valor do título foi modificado de R$ "+ titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsString() +"para R$" + detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO));
											doc.setValorDocumento(oTitulo.getValor());
										} catch (BusinessException e) {
											log.debug("ERRO ao atualizar 'Titulo'- " + titulo.getPropertyValue(DocumentoTitulo.VALOR) + " -/- 'Arq Retorno'- " + detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO));
											throw new Exception("Titulo " + DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal()) + "-/- Arq. Retorno " + DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO)));
										}

										try{
											super.definirMultasJuros(titulo.getObject(), detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA));	

										} catch (BusinessException e) {
											log.debug("ERRO ao atualizar 'Titulo'- Valores Extras");
											throw new Exception("Titulo - Erro Valores Extras");
										}

										log.debug("Propriedades setadas com SUCESSO, atualizando titulo no sistema");
										/*
										 * Propriedades setadas com sucesso:
										 * - atualiza o titulo no sistema (update)
										 * - adiciona a mensagem de sucesso na lista
										 * - incrementa o contador de titulos que foram atualizados com sucesso
										 */
										UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), titulo, serviceDataOwner);
										tituloOkList.add(BusinessMessage.TYPE_INFO, Gerenciador748.class, "RETORNO_SUCESSO", titulo.getProperty(DocumentoTitulo.CONTRATO).getValue().getAsEntity().toString(), titulo.getProperty(DocumentoTitulo.NUMERO_DOCUMENTO).getValue().getAsString(), CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_VENCIMENTO).getValue().getAsCalendar()), DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal()), CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA).getValue().getAsCalendar()), DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal()));
										titulosAtualizados += 1;
										tituloOkValorTotal = tituloOkValorTotal.add(oTitulo.getValor());

										/* Andre, 12/07/2008: Não deve ser quitado diretamente, chamar o quitarDocumento do gerenciador de documentos */
										IGerenciadorDocumentoCobranca gerenciadorDocumento = this.getProvedorBanco().getProvedorDocumentoCobranca().retrieveGerenciadorDocumentoCobranca(oCedente.getNomeGerenciadorDocumento());
										gerenciadorDocumento.quitarDocumento(oTitulo, oTitulo.getLancamentos().get(0).getContaPrevista(), oTitulo.getDataUltimaOcorrencia(), oTitulo.getDataCredito(), serviceDataOwner);

										/* ok, nenhum erro ocorreu */
										doc.setStatus(DocumentoRetornoStatus.LIQUIDADO_COM_SUCESSO);
									}else{
										/* Registra a mensagem das demais ocorrências */
										BusinessMessage message = new BusinessMessage(BusinessMessage.TYPE_INFO, 
												Gerenciador748.class, 
												"OCORRENCIA_PROCESSADA", 
												oTitulo.getContrato() + "<br/>" + ultimaOcorrencia.toString(),
												oTitulo.getNumeroDocumento(),
												CalendarUtils.formatDate(oTitulo.getDataVencimento()),
												DecimalUtils.formatBigDecimal(oTitulo.getValor()),
												CalendarUtils.formatDate(detalhes.getAsCalendar(DetalheRetornoSemRegistro.DATA_OCORRENCIA)),
												detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_EFETIVAMENTE_PAGO)
												);
										
										tituloJaRetornadoValorTotal = tituloJaRetornadoValorTotal.add(detalhes.getAsBigDecimal(DetalheRetornoSemRegistro.VALOR_TITULO));
										tituloJaRetornadoList.add(message);
									}
							}	

						}
					} catch (BusinessException e) {
						log.debug("ERRO ao gravar a entidade (titulo):" + e.getMessage());
						doc.setStatus(DocumentoRetornoStatus.ERRO_ATUALIZANDO_DOCUMENTO);
						doc.setMensagem(e.getMessage());
						DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_GRAVANDO", nossoNumero));
						de.getErrorList().addAll(e.getErrorList());
						throw de;
					} 
					catch (Exception e) {
						e.printStackTrace();
						log.debug("ERRO ao atualizar valores do titulo:" + e.getMessage());
						doc.setStatus(DocumentoRetornoStatus.ERRO_ATUALIZANDO_DOCUMENTO);
						doc.setMensagem(e.getMessage());
						DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_SETANDO", nossoNumero, e.getMessage()));
						throw de;
					}
				} // for

				//Mostra quantos Titulos existem no Arquivo de Retorno e quantos Titulos puderam ser Atualizados no sistema
				serviceDataOwner.getMessageList().add(BusinessMessage.TYPE_INFO, Gerenciador748.class, "QUANTIDADE_TITULOS_PROCESSADOS", titulosAtualizados, titulosTotal);

				/* 
				 * - verifica se tem mensagens de Ok
				 * - adiciona na primeira posição a mensagem cabeçalho de Ok 
				 */
				if (tituloOkList.size() > 0){
					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador748.class, "CABECALHO_TITULOS_OK"));
					serviceDataOwner.getMessageList().addAll(tituloOkList);
					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador748.class, "RETORNO_SUCESSO_TOTAL", DecimalUtils.formatBigDecimal(tituloOkValorTotal)));
				}

				if (tituloJaRetornadoList.size() > 0){
					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador748.class, "CABECALHO_OCORRENCIAS"));
					serviceDataOwner.getMessageList().addAll(tituloJaRetornadoList);
					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador748.class, "SUMARIO_OCORRENCIAS", DecimalUtils.formatBigDecimal(tituloJaRetornadoValorTotal)));
				}

				/* 
				 * - verifica se tem mensagens de NoOk
				 * - adiciona na primeira posição a mensagem cabeçalho de NoOk 
				 */
				if (tituloNoOkList.size() > 0){
					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador748.class, "CABECALHO_TITULOS_NO_OK"));
					serviceDataOwner.getMessageList().addAll(tituloNoOkList);
					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador748.class, "RETORNO_FALHA_TOTAL", DecimalUtils.formatBigDecimal(tituloNoOkValorTotal)));
					serviceDataOwner.getMessageList().add(BusinessMessage.TYPE_INFO, Gerenciador748.class, "INSTRUCAO_TITULOS_NO_OK");
				}

				serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador748.class, "RETORNO_TOTAL", DecimalUtils.formatBigDecimal(tituloNoOkValorTotal.add(tituloOkValorTotal).add(tituloJaRetornadoValorTotal))));

			}catch(BusinessException e){
				//se o arquivo não pôde ser interpretado, ex: se não for do tipo cnab240
				log.debug("ERRO ao interpretar o arquivo");
				DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "ERRO_INTERPRETANDO_ARQUIVO", "(Nome do arquivo não disponível)"));
				de.getErrorList().addAll(e.getErrorList());
				throw de;
			}
			
			log.debug("encerrando método receberRetorno()");
			return result;
		}// fim do if para CNAB400

		else{ //versão CNAB 240 ainda não implementada - indicar erro de que o padrão que está no Cedente está incompatível com o gerenciador de bancos 756
			log.debug("encerrando método receberRetorno(), erro: padrão cnab não implementado ainda");
			DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador748.class, "PADRAO_CNAB_DESCONHECIDO", oCedente.getLayoutCnab(), oCedente.toString(), GERENCIADOR_CODIGO+": "+GERENCIADOR_NOME));
			throw de;

		}

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
		return (org.apache.commons.lang.StringUtils.isEmpty(str)?"":str);
	}

	private void setRegistro(IRegistro registro, ICampo campo, String nomePropriedade, String valor, boolean checkEmpty) throws BusinessException{
		try{
			registro.set(campo, valor, checkEmpty);
		}catch (Exception e) {
			throw new BusinessException(MessageList.create(Gerenciador748.class, "ERRO_SETANDO_REMESSA", nomePropriedade, valor));
		}
	}

	private void setRegistro(IRegistro registro, ICampo campo,  String nomePropriedade, long valor) throws BusinessException{
		try{
			registro.setAsLong(campo, valor);
		}catch (Exception e) {
			throw new BusinessException(MessageList.create(Gerenciador748.class, "ERRO_SETANDO_REMESSA", nomePropriedade, valor));
		}		
	}

	private void setRegistro(IRegistro registro, ICampo campo,  String nomePropriedade, BigDecimal valor, boolean checkEmpty) throws BusinessException{
		try{
			registro.setAsDecimal(campo, valor, checkEmpty);
		}catch (Exception e) {
			throw new BusinessException(MessageList.create(Gerenciador748.class, "ERRO_SETANDO_REMESSA", nomePropriedade, valor));
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
		campo1.append("748");
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
}
