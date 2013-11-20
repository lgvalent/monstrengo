package br.com.orionsoft.financeiro.documento.cobranca.titulo.banco001;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.LockMode;

import br.com.orionsoft.cnab.ICampo;
import br.com.orionsoft.cnab.IRegistro;
import br.com.orionsoft.cnab.banco001.cnab240.HeaderArquivo;
import br.com.orionsoft.cnab.banco001.cnab240.HeaderLote;
import br.com.orionsoft.cnab.banco001.cnab240.TrailerArquivo;
import br.com.orionsoft.cnab.banco001.cnab240.TrailerLote;
import br.com.orionsoft.cnab.banco001.cnab240.remessa.SegmentoP;
import br.com.orionsoft.cnab.banco001.cnab240.remessa.SegmentoQ;
import br.com.orionsoft.cnab.banco001.cnab240.retorno.CodigoMovimentoRetorno;
import br.com.orionsoft.cnab.banco001.cnab240.retorno.SegmentoT;
import br.com.orionsoft.cnab.banco001.cnab240.retorno.SegmentoU;
import br.com.orionsoft.cnab.cnab240.Arquivo;
import br.com.orionsoft.cnab.cnab240.Lote;
import br.com.orionsoft.cnab.cnab240.Remessa;
import br.com.orionsoft.cnab.cnab240.Retorno;
import br.com.orionsoft.cnab.cnab240.Segmento;
import br.com.orionsoft.cnab.exceptions.PropriedadeVaziaException;
import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaException;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoResultado;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoStatus;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.Cedente;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.DocumentoTitulo;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.GerenciadorBancoBasic;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.GerenciadorDocumentoTitulo;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.Ocorrencia;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.OcorrenciaControle;
import br.com.orionsoft.financeiro.gerenciador.services.QuitarLancamentoService;
import br.com.orionsoft.financeiro.utils.UtilsOcorrencia;
import br.com.orionsoft.financeiro.utils.UtilsRemessa;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.BusinessMessage;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.core.util.MathUtils;
import br.com.orionsoft.monstrengo.core.util.StringUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IPropertyValue;
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
 * @spring.bean id="Gerenciador001" init-method="registrarGerenciador"
 * @spring.property name="provedorBanco" ref="ProvedorBanco"
 */
public class Gerenciador001 extends GerenciadorBancoBasic
{

	public static final String GERENCIADOR_NOME = "Banco do Brasil";
	public static final String GERENCIADOR_CODIGO = "001";

//	public static final String OCORRENCIA_LIQUIDACAO = "99"; //Liquida��o
//	public static final long ESPECIE_TITULO = 01; //Duplicata
	public static final String EXTENSAO_ARQUIVO_REMESSA = ".REM";

	/* 
	 * O c�digo do Cedente DEVE ser definido pois sen�o o C�digo de Barras n�o � gerado corretamente
	 */
	public static final int CEDENTE_QUATRO_POSICOES_CODIGO = 4;
	public static final int CEDENTE_QUATRO_POSICOES_NOSSO_NUMERO = 7;
	public static final int CEDENTE_QUATRO_POSICOES_NOSSO_NUMERO_LIMITE = 9999999;

	public static final int CEDENTE_SEIS_POSICOES_CODIGO = 6;
	public static final int CEDENTE_SEIS_POSICOES_NOSSO_NUMERO = 5;
	public static final int CEDENTE_SEIS_POSICOES_NOSSO_NUMERO_LIMITE = 99999;

	public static final int CEDENTE_SETE_POSICOES_CODIGO = 7;
	public static final int CEDENTE_SETE_POSICOES_NOSSO_NUMERO = 10;
	public static final int CEDENTE_SETE_POSICOES_NOSSO_NUMERO_LIMITE = 999999999; //FIXME - falta 1 digito, Integer n�o "alcan�a"



	public static final int AGENCIA_CODIGO_LENGTH = 4;
	public static final int CEDENTE_CODIGO_LENGTH = 9;
	public static final int CARTEIRA_CODIGO_LENGTH = 2;

	public static final String CARTEIRA_COBRANCA_SEM_REGISTRO = "18";


	public String getNome() {
		return GERENCIADOR_NOME;
	}

	public String getCodigo() {
		return GERENCIADOR_CODIGO;
	}


	public String getCampoLivre(IEntity<? extends DocumentoCobranca> documento) throws DocumentoCobrancaException{
		DocumentoTitulo oTitulo = (DocumentoTitulo)documento.getObject();
		Cedente oCedente = oTitulo.getCedente();

		StringBuffer result = new StringBuffer();
		//caso seja um conv�nio que utiliza um c�digo com 4 d�gitos 
		if (oCedente.getCedenteCodigo().length() == CEDENTE_QUATRO_POSICOES_CODIGO) {

			//caso seja um conv�nio que utiliza um c�digo com 6 d�gitos	
		}else if (oCedente.getCedenteCodigo().length() == CEDENTE_SEIS_POSICOES_CODIGO) {
			//nosso n�mero inteiro (com 17 caracteres, cedente+nossoNumero) sem DV
			result.append(StringUtils.formatNumber(oTitulo.getNumeroDocumento(), 8, true));
			
			// agencia
			result.append(StringUtils.formatNumber(oCedente.getContaBancaria().getAgenciaCodigo(), 4, true));

			//n�mero do cedente
			result.append(StringUtils.formatNumber(oCedente.getCedenteCodigo(), 11, true));

			//carteira
			result.append(StringUtils.formatNumber(oTitulo.getCedente().getCarteiraCodigo(), 2, true));

			//caso seja um conv�nio que utiliza um c�digo com 7 d�gitos (caso da cobran�a sem registro)	
		}else if (oCedente.getCedenteCodigo().length() == CEDENTE_SETE_POSICOES_CODIGO) {
			//zeros
			result.append(StringUtils.formatNumber("0", 6, true));

			//nosso n�mero inteiro (com 17 caracteres, cedente+nossoNumero) sem DV
//			result.append(StringUtils.formatNumber(oCedente.getCedenteCodigo(), CEDENTE_SETE_POSICOES_CODIGO, true));
			result.append(StringUtils.formatNumber(oTitulo.getNumeroDocumento(), CEDENTE_SETE_POSICOES_NOSSO_NUMERO + CEDENTE_SETE_POSICOES_CODIGO, true));

//			//n�mero do cedente
//			result.append(StringUtils.formatNumber(oCedente.getCedenteCodigo(), CEDENTE_SETE_POSICOES_CODIGO, true));
//
//			//complemento do nosso n�mero (apenas o nosso n�mero)
//			result.append(StringUtils.formatNumber(oTitulo.getNossoNumero(), CEDENTE_SETE_POSICOES_NOSSO_NUMERO, true));

			//carteira
			result.append(StringUtils.formatNumber(oTitulo.getCedente().getCarteiraCodigo(), 2, true));
		}

		return result.toString();
	}

	/**
	 * Este m�todo define e formata o nosso n�mero do t�tulo.<br>
	 * Ele utiliza e atualiza o sequencial de nosso n�mero controlado pelo cedente. 
	 */
	public String formatarNossoNumero(IEntity<? extends DocumentoCobranca> documento, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		try{
			DocumentoTitulo oTitulo = (DocumentoTitulo)documento.getObject();

			Cedente oCedente = oTitulo.getCedente();
			serviceDataOwner.getCurrentSession().lock(oCedente, LockMode.UPGRADE);

			/* Dumy update para for�ar um LockMode.UPGRADE. Segundo o pessoal, o lock somente � obtido quando o update ocorre!!! */
			UtilsCrud.objectUpdate(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), oCedente, serviceDataOwner);

			StringBuffer result = new StringBuffer("");

			//n�mero sequencial 'Nosso N�mero'
			long sequenciaNumeroDocumento = oCedente.getSequenciaNumeroDocumento();

			//caso seja um conv�nio que utiliza um c�digo com 4 d�gitos 
			if (oCedente.getCedenteCodigo().length() == CEDENTE_QUATRO_POSICOES_CODIGO) {

				//caso seja um conv�nio que utiliza um c�digo com 6 d�gitos	
			}else if (oCedente.getCedenteCodigo().length() == CEDENTE_SEIS_POSICOES_CODIGO) {

				//caso seja um conv�nio que utiliza um c�digo com 7 d�gitos (caso da cobran�a sem registro)	
			}else if (oCedente.getCedenteCodigo().length() == CEDENTE_SETE_POSICOES_CODIGO) {
				//n�mero do cedente sem o DV
				result.append(StringUtils.formatNumber(oCedente.getCedenteCodigo(), CEDENTE_SETE_POSICOES_CODIGO, true));

				//se o n�mero sequencial for 0 ou se tiver alcan�ado o valor m�ximo, atribui 1
				if (sequenciaNumeroDocumento == 0 || sequenciaNumeroDocumento > CEDENTE_SETE_POSICOES_NOSSO_NUMERO_LIMITE) {
					sequenciaNumeroDocumento = 1; //evita que o numero se "perca" caso o valor seja 0 e a data esteja correta
				}

				result.append(StringUtils.formatNumber(String.valueOf(sequenciaNumeroDocumento), CEDENTE_SETE_POSICOES_NOSSO_NUMERO, true));

				sequenciaNumeroDocumento += 1;
				oCedente.setSequenciaNumeroDocumento(sequenciaNumeroDocumento);

				/* Atualiza a Entidade Cedente pois o n�mero sequencial foi alterado
				 * OBS.: N�o � utilizado o encanamento de sess�o do banco serviceDataOwner = null.
				 * Isto porque, o Cedente deve ser atualizado no banco IMEDIATAMENTE � obten��o do nosso
				 * n�mero, para evitar que ocorra concorr�ncia de nosso n�mero */ 
				UtilsCrud.objectUpdate(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), oCedente, serviceDataOwner);

			}else {
				//FIXME - colocar erro
			}

			return result.toString();

		}catch (BusinessException e) {
			throw new DocumentoCobrancaException(e.getErrorList());
		}
	}

	public String formatarAgenciaCedente(IEntity<? extends DocumentoCobranca> documento) throws DocumentoCobrancaException{
		DocumentoTitulo titulo = (DocumentoTitulo)documento.getObject();
		return titulo.getCedente().getContaBancaria().getAgenciaCodigo() + " / " + StringUtils.formatNumber(titulo.getCedente().getCedenteCodigo(), CEDENTE_CODIGO_LENGTH, true);
	}

	public String getCodigoBarras(IEntity<? extends DocumentoCobranca> documento) throws DocumentoCobrancaException{
		try{
			DocumentoTitulo oTitulo = (DocumentoTitulo)documento.getObject();
			Cedente oCedente = oTitulo.getCedente();

			StringBuffer result = new StringBuffer();

			//3 posi��es para o c�digo do banco (1 a 3)
			result.append(StringUtils.formatNumber(oCedente.getContaBancaria().getBanco().getCodigo(), 3, true));

			//1 posi��o para moeda (4 a 4)
			result.append(StringUtils.formatNumber("9", 1, true));

			//4 posi��es para o fator de vencimento (6 a 9) - calculado a partir de 07/10/1997 (nossa data base � 03/07/2000, por isso se dimin�i 1000) 
			Long fatorVencimento = CalendarUtils.diffDay(oTitulo.getDataVencimento(), CalendarUtils.getBarCodeBaseDate());
			result.append(StringUtils.formatNumber(fatorVencimento.toString(), 4, false));

			//10 d�gitos para o valor (10 a 19)
			//FIXME - verificar se o m�todo format da classe String retira a pontua��o do n�mero BigDecimal
			result.append(StringUtils.formatNumber(String.format("%010d", getValorParaCodigoBarras(oTitulo).multiply(new BigDecimal("100")).longValue()), 10, true));

			//25 d�gitos para o campo livre (20 a 44)
			result.append(this.getCampoLivre(documento));

			//1 posi��o para o DV (5 a 5)
			//TODO - o c�lculo do DV s� deve ser efetuado por �ltimo e adicionado na posi��o 5 do c�digo de barras
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
		 * c�digo do banco (posi��o 1 a 3 do c�digo de barras) + 
		 * c�digo da moeda (posi��o 4 do c�digo de barras) + 
		 * cinco primeiras posi��es do campo livre (posi��es 20 a 24 do c�digo de barras) + 
		 * DV 
		 */
		StringBuffer campo1 = new StringBuffer();
		campo1.append(StringUtils.formatNumber(oTitulo.getCedente().getContaBancaria().getBanco().getCodigo(), 3, true));
		campo1.append(StringUtils.formatNumber("9", 1, true));
		campo1.append(StringUtils.formatNumber(campoLivre.substring(0, 5), 5, true));
		String campo1dv = MathUtils.modulo10(campo1.toString());
		result.append(campo1).append(campo1dv);

		/*
		 * segundo campo:
		 * posi��o 6 a 15 do campo livre (posi��o 25 a 34 do c�digo de barras) + 
		 * DV
		 */
		StringBuffer campo2 = new StringBuffer();
		campo2.append(StringUtils.formatNumber(campoLivre.substring(5, 15), 10, true));
		String campo2dv = MathUtils.modulo10(campo2.toString());
		result.append(campo2).append(campo2dv);

		/*
		 * terceiro campo:
		 * posi��es 16 a 25 do campo livre (posi��es 35 a 44 do c�digo de barras) + 
		 * DV
		 */
		StringBuffer campo3 = new StringBuffer();
		campo3.append(StringUtils.formatNumber(campoLivre.substring(15, 25), 10, true));
		String campo3dv = MathUtils.modulo10(campo3.toString());
		result.append(campo3).append(campo3dv);

		/*
		 * quarto campo:
		 * DV geral do c�digo de barras (posi��o 5 do c�digo de barras)
		 */
		String codigoBarras = getCodigoBarras(documento);
		result.append(" ");
		result.append(codigoBarras.substring(4, 5));

		/*
		 * quinto campo:
		 * fator vencimento (posi��es 6 a 9 do c�digo de barras) + 
		 * valor do documento (posi��es 10 a 19 do c�digo de barras)
		 */
		result.append(" ");
		result.append(codigoBarras.substring(5, 9));
		result.append(codigoBarras.substring(9, 19));
		
		/*
		 * Formata��o da linha digit�vel
		 */
		result.insert(5, ".");
		result.insert(11, " ");
		result.insert(17, ".");
		result.insert(24, " ");
		result.insert(30, ".");
		
		return result.toString();
	}

	@SuppressWarnings("unchecked")
	public File gerarRemessa(IEntity<? extends ConvenioCobranca> convenioCobranca , Calendar inicioPeriodo, Calendar finalPeriodo, Integer quantidadeDiasProtesto, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		IEntityList<DocumentoTitulo> entityList = null;

		try {	
			/* 
			 * Pesquisando pelos T�tulos que est�o prontos para serem enviados 
			 * ao banco (c�digo ocorr�ncia 1001) e pertencem ao convenio 
			 * passado por par�metro
			 */
			log.debug("Pesquisando os T�tulos que ser�o inclu�dos no arquivo de remessa");
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
			
			/* Se a data inicial e a data final n�o estiverem vazias, determinar o per�odo para este arquivo de remessa */
			condiction = new QueryCondiction(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().getEntityManager(),
					DocumentoTitulo.class,
					DocumentoTitulo.DATA_VENCIMENTO,
					Operator.BETWEEN,
					CalendarUtils.formatDate(inicioPeriodo),
					CalendarUtils.formatDate(finalPeriodo)
			);

			condiction.setInitOperator(QueryCondiction.INIT_AND);
			condictions.add(condiction);

			ServiceData sdQuery = new ServiceData(QueryService.SERVICE_NAME, serviceDataOwner);
			sdQuery.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, DocumentoTitulo.class);
			sdQuery.getArgumentList().setProperty(QueryService.IN_QUERY_CONDICTIONS, condictions);
			this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().execute(sdQuery);

			log.debug("Obtendo o(s) t�tulo(s) encontrado(s) na pesquisa");
			entityList = (IEntityList<DocumentoTitulo>) sdQuery.getOutputData(QueryService.OUT_ENTITY_LIST);
			log.debug("Foram encontrados " + entityList.getSize() + " t�tulos na pesquisa");

			/*
			 * O uso do cedente passado por par�metro causava erro de NonUniqueObject quando era feito o update desta entidade
			 * Dessa forma o cedente recebe a entidade cedente atribu�do ao DocumentoTitulo, evitando o erro na hora de realizar update na entidade
			 */
			if (!entityList.isEmpty()){
				convenioCobranca = entityList.getFirst().getProperty(DocumentoTitulo.CONVENIO_COBRANCA).getValue().getAsEntity();
			}

		}catch (BusinessException e) {
			throw new DocumentoCobrancaException(MessageList.createSingleInternalError(e));
		}

		//Se n�o encontrar nenhum t�tulo na pesquisa, retorna mensagem de erro
		if (entityList.isEmpty()){
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "REMESSA_NENHUM_TITULO"));
		}

		//Caso encontre t�tulos, tenta construir (preencher) o arquivo de remessa
		Arquivo arquivo = new Arquivo();

//		/* Construindo Estrutura B�sica */
		IRegistro headerArquivo = null;
		IRegistro headerLote = null;
		IRegistro segmentoP = null;
		IRegistro segmentoQ = null;
//		IRegistro segmentoR = null;
//		IRegistro segmentoS = null;
		IRegistro trailerLote = null;
		IRegistro trailerArquivo = null;

		Cedente oCedente = (Cedente) convenioCobranca.getObject();
		
		/* Inserindo valores no arquivo de remessa */
		/* Construindo Header de Arquivo e Lote */
		try{
			headerArquivo = new HeaderArquivo("");
			setRegistro(headerArquivo, HeaderArquivo.CODIGO_DO_BANCO_NA_COMPENSACAO, "CODIGO_DO_BANCO_NA_COMPENSACAO", StringUtils.formatNumber(oCedente.getContaBancaria().getBanco().getCodigo(), HeaderArquivo.CODIGO_DO_BANCO_NA_COMPENSACAO.getSize(), true), true);
			setRegistro(headerArquivo, HeaderArquivo.LOTE_DE_SERVICO, "LOTE_DE_SERVICO", StringUtils.formatNumber("0", HeaderArquivo.LOTE_DE_SERVICO.getSize(), true), true);
			setRegistro(headerArquivo, HeaderArquivo.REGISTRO_HEADER_DE_ARQUIVO, "REGISTRO_HEADER_DE_ARQUIVO", StringUtils.formatNumber("0", HeaderArquivo.REGISTRO_HEADER_DE_ARQUIVO.getSize(), true), true);
			setRegistro(headerArquivo, HeaderArquivo.USO_EXCLUSIVO_FEBRABAN_1, "USO_EXCLUSIVO_FEBRABAN_1", StringUtils.formatAlpha(" ", HeaderArquivo.USO_EXCLUSIVO_FEBRABAN_1.getSize(), false), false);
			setRegistro(headerArquivo, HeaderArquivo.TIPO_DE_INSCRICAO_DA_EMPRESA, "TIPO_DE_INSCRICAO_DA_EMPRESA", StringUtils.formatAlpha("2", HeaderArquivo.TIPO_DE_INSCRICAO_DA_EMPRESA.getSize(), true), true);
			setRegistro(headerArquivo, HeaderArquivo.NUMERO_DE_INSCRICAO_DA_EMPRESA, "NUMERO_DE_INSCRICAO_DA_EMPRESA", StringUtils.formatNumber(oCedente.getContratante().getDocumento(), HeaderArquivo.NUMERO_DE_INSCRICAO_DA_EMPRESA.getSize(), true), true);
			/* 
			 * O c�digo � montado da seguinte forma:
			 * - n�mero do cedente com 9 posi��es alinhado a direita e preenchido com zeros a esquerda
			 * - c�digo do produto com 4 posi��es (0014 - cobran�a cedente)
			 * - carteira de cobran�a - oCedente.getCarteira()
			 * - varia��o da carteira de cobran�a (019) 
			 * TODO - colocar informa��es da varia��o da carteira de cobran�a e c�digo do produto (talvez em cedente)
			 */
			String numeroCedente = StringUtils.formatNumber(oCedente.getCedenteCodigo(), 9, true);
			String codigoProduto = "0014";
			String carteira = StringUtils.formatNumber(oCedente.getCarteiraCodigo(), 2, true);
			String variacaoCarteira = "019";
			setRegistro(headerArquivo, HeaderArquivo.CODIGO_DO_CONVENIO_NO_BANCO, "CODIGO_DO_CONVENIO_NO_BANCO", StringUtils.formatAlpha(numeroCedente + codigoProduto + carteira + variacaoCarteira + "  ", HeaderArquivo.CODIGO_DO_CONVENIO_NO_BANCO.getSize(), true), true);
			setRegistro(headerArquivo, HeaderArquivo.AGENCIA_MANTENEDORA_DA_CONTA, "AGENCIA_MANTENEDORA_DA_CONTA", StringUtils.formatNumber(oCedente.getContaBancaria().getAgenciaCodigo(), HeaderArquivo.AGENCIA_MANTENEDORA_DA_CONTA.getSize(), true), true);
			setRegistro(headerArquivo, HeaderArquivo.DIGITO_VERIFICADOR_DA_AGENCIA, "DIGITO_VERIFICADOR_DA_AGENCIA", StringUtils.formatNumber(oCedente.getContaBancaria().getAgenciaDigito(), HeaderArquivo.DIGITO_VERIFICADOR_DA_AGENCIA.getSize(), true), true);
			setRegistro(headerArquivo, HeaderArquivo.NUMERO_DA_CONTA_CORRENTE, "NUMERO_DA_CONTA_CORRENTE", StringUtils.formatNumber(oCedente.getContaBancaria().getContaCodigo(), HeaderArquivo.NUMERO_DA_CONTA_CORRENTE.getSize(), true), true);
			setRegistro(headerArquivo, HeaderArquivo.DIGITO_VERIFICADOR_DA_CONTA_CORRENTE, "DIGITO_VERIFICADOR_DA_CONTA_CORRENTE", StringUtils.formatNumber(oCedente.getContaBancaria().getContaDigito(), HeaderArquivo.DIGITO_VERIFICADOR_DA_CONTA_CORRENTE.getSize(), true), true);
			setRegistro(headerArquivo, HeaderArquivo.DIGITO_VERIFICADOR_DA_AGENCIA_CONTA, "DIGITO_VERIFICADOR_DA_AGENCIA_CONTA", StringUtils.formatAlpha(" ", HeaderArquivo.DIGITO_VERIFICADOR_DA_AGENCIA_CONTA.getSize(), false), false);
			setRegistro(headerArquivo, HeaderArquivo.NOME_DA_EMPRESA, "NOME_DA_EMPRESA", StringUtils.formatAlpha(StringUtils.removeAccent(oCedente.getContratante().getNome()).toUpperCase(), HeaderArquivo.NOME_DA_EMPRESA.getSize(), false), true);
			setRegistro(headerArquivo, HeaderArquivo.NOME_DO_BANCO, "NOME_DO_BANCO", StringUtils.formatAlpha(StringUtils.removeAccent(oCedente.getContratado().getPessoa().getNome()).toUpperCase(), HeaderArquivo.NOME_DO_BANCO.getSize(), false), true);
			setRegistro(headerArquivo, HeaderArquivo.USO_EXCLUSIVO_FEBRABAN_2, "USO_EXCLUSIVO_FEBRABAN_2", StringUtils.formatAlpha(" ", HeaderArquivo.USO_EXCLUSIVO_FEBRABAN_2.getSize(), false), false);
			setRegistro(headerArquivo, HeaderArquivo.CODIGO_REMESSA_RETORNO, "CODIGO_REMESSA_RETORNO", StringUtils.formatNumber("1", HeaderArquivo.CODIGO_REMESSA_RETORNO.getSize(), false), true);
			setRegistro(headerArquivo, HeaderArquivo.DATA_DE_GERACAO_DO_ARQUIVO, "DATA_DE_GERACAO_DO_ARQUIVO", StringUtils.formatNumber(formatarData(CalendarUtils.getCalendar()), HeaderArquivo.DATA_DE_GERACAO_DO_ARQUIVO.getSize(), false), true);
			DateFormat df = new SimpleDateFormat("HHmmss");
			Calendar c = Calendar.getInstance(); //usou-se Calendar.getInstance() ao inv�s de CalendarUtils.getCalendar() pois o segundo retorna uma data com a hora zerada
			setRegistro(headerArquivo, HeaderArquivo.HORA_DE_GERACAO_DO_ARQUIVO, "HORA_DE_GERACAO_DO_ARQUIVO", StringUtils.formatNumber(df.format(c.getTime()), HeaderArquivo.HORA_DE_GERACAO_DO_ARQUIVO.getSize(), true), true);
			setRegistro(headerArquivo, HeaderArquivo.NUMERO_SEQUENCIAL_DO_ARQUIVO, "NUMERO_SEQUENCIAL_DO_ARQUIVO", StringUtils.formatNumber("0", HeaderArquivo.NUMERO_SEQUENCIAL_DO_ARQUIVO.getSize(), false), true);
			setRegistro(headerArquivo, HeaderArquivo.NUMERO_DA_VERSAO_DO_LAYOUT_DO_ARQUIVO, "NUMERO_DA_VERSAO_DO_LAYOUT_DO_ARQUIVO", StringUtils.formatNumber("030", HeaderArquivo.NUMERO_DA_VERSAO_DO_LAYOUT_DO_ARQUIVO.getSize(), false), true);
			setRegistro(headerArquivo, HeaderArquivo.DENSIDADE_DE_GRAVACAO_DO_ARQUIVO, "DENSIDADE_DE_GRAVACAO_DO_ARQUIVO", StringUtils.formatNumber(String.valueOf("0"), HeaderArquivo.DENSIDADE_DE_GRAVACAO_DO_ARQUIVO.getSize(), false), true);
			setRegistro(headerArquivo, HeaderArquivo.PARA_USO_RESERVADO_DO_BANCO, "PARA_USO_RESERVADO_DO_BANCO", StringUtils.formatAlpha(" ", HeaderArquivo.PARA_USO_RESERVADO_DO_BANCO.getSize(), false), false);
			setRegistro(headerArquivo, HeaderArquivo.PARA_USO_RESERVADO_DA_EMPRESA, "PARA_USO_RESERVADO_DA_EMPRESA", StringUtils.formatAlpha(" ", HeaderArquivo.PARA_USO_RESERVADO_DA_EMPRESA.getSize(), false), false);
			setRegistro(headerArquivo, HeaderArquivo.USO_EXCLUSIVO_FEBRABAN_3, "USO_EXCLUSIVO_FEBRABAN_3", StringUtils.formatAlpha(" ", HeaderArquivo.USO_EXCLUSIVO_FEBRABAN_3.getSize(), false), false);
			setRegistro(headerArquivo, HeaderArquivo.IDENTIFICACAO_COBRANCA_SEM_PAPEL, "IDENTIFICACAO_COBRANCA_SEM_PAPEL", StringUtils.formatAlpha("CSP", HeaderArquivo.IDENTIFICACAO_COBRANCA_SEM_PAPEL.getSize(), false), true);
			setRegistro(headerArquivo, HeaderArquivo.USO_EXCLUSIVO_DAS_VANS, "USO_EXCLUSIVO_DAS_VANS", StringUtils.formatNumber("0", HeaderArquivo.USO_EXCLUSIVO_DAS_VANS.getSize(), false), true);
			setRegistro(headerArquivo, HeaderArquivo.TIPO_DE_SERVICO, "TIPO_DE_SERVICO", StringUtils.formatAlpha(" ", HeaderArquivo.TIPO_DE_SERVICO.getSize(), false), false);
			setRegistro(headerArquivo, HeaderArquivo.CODIGO_DAS_OCORRENCIAS, "CODIGO_DAS_OCORRENCIAS", StringUtils.formatAlpha(" ", HeaderArquivo.CODIGO_DAS_OCORRENCIAS.getSize(), false), false);
			
			headerLote = new HeaderLote("");
			setRegistro(headerLote, HeaderLote.CODIGO_DO_BANCO_NA_COMPENSACAO, "CODIGO_DO_BANCO_NA_COMPENSACAO", StringUtils.formatNumber(oCedente.getContaBancaria().getBanco().getCodigo(), HeaderLote.CODIGO_DO_BANCO_NA_COMPENSACAO.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.LOTE_DE_SERVICO, "LOTE_DE_SERVICO", StringUtils.formatNumber("0001", HeaderLote.LOTE_DE_SERVICO.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.REGISTRO_HEADER_DO_LOTE, "REGISTRO_HEADER_DO_LOTE", StringUtils.formatNumber("1", HeaderLote.REGISTRO_HEADER_DO_LOTE.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.TIPO_DE_OPERACAO, "TIPO_DE_OPERACAO", StringUtils.formatAlpha("R", HeaderLote.TIPO_DE_OPERACAO.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.TIPO_DE_SERVICO, "TIPO_DE_SERVICO", StringUtils.formatAlpha("01", HeaderLote.TIPO_DE_SERVICO.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.FORMA_DE_LANCAMENTO, "FORMA_DE_LANCAMENTO", StringUtils.formatNumber("0", HeaderLote.FORMA_DE_LANCAMENTO.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.NUMERO_DA_VERSAO_DO_LAYOUT_DO_LOTE, "NUMERO_DA_VERSAO_DO_LAYOUT_DO_LOTE", StringUtils.formatNumber("020", HeaderLote.NUMERO_DA_VERSAO_DO_LAYOUT_DO_LOTE.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.USO_EXCLUSIVO_FEBRABAN_1, "USO_EXCLUSIVO_FEBRABAN_1", StringUtils.formatNumber(" ", HeaderLote.USO_EXCLUSIVO_FEBRABAN_1.getSize(), false), false);
			setRegistro(headerLote, HeaderLote.TIPO_DE_INSCRICAO_DA_EMPRESA, "TIPO_DE_INSCRICAO_DA_EMPRESA", StringUtils.formatNumber("2", HeaderLote.TIPO_DE_INSCRICAO_DA_EMPRESA.getSize(), true), true);
			/* TODO - refazer aqui */
			setRegistro(headerLote, HeaderLote.NUMERO_DE_INSCRICAO_DA_EMPRESA, "NUMERO_DE_INSCRICAO_DA_EMPRESA", StringUtils.formatNumber(oCedente.getContratante().getDocumento(), HeaderLote.NUMERO_DE_INSCRICAO_DA_EMPRESA.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.CODIGO_DO_CONVENIO_NO_BANCO, "CODIGO_DO_CONVENIO_NO_BANCO", StringUtils.formatAlpha(numeroCedente + codigoProduto + carteira + variacaoCarteira + "  ", HeaderLote.CODIGO_DO_CONVENIO_NO_BANCO.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.AGENCIA_MANTENEDORA_DA_CONTA, "AGENCIA_MANTENEDORA_DA_CONTA", StringUtils.formatNumber(oCedente.getContaBancaria().getAgenciaCodigo(), HeaderLote.AGENCIA_MANTENEDORA_DA_CONTA.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.DIGITO_VERIFICADOR_DA_AGENCIA, "DIGITO_VERIFICADOR_DA_AGENCIA", StringUtils.formatNumber(oCedente.getContaBancaria().getAgenciaDigito(), HeaderLote.DIGITO_VERIFICADOR_DA_AGENCIA.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.NUMERO_DA_CONTA_CORRENTE, "NUMERO_DA_CONTA_CORRENTE", StringUtils.formatNumber(oCedente.getContaBancaria().getContaCodigo(), HeaderLote.NUMERO_DA_CONTA_CORRENTE.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.DIGITO_VERIFICADOR_DA_CONTA, "DIGITO_VERIFICADOR_DA_CONTA", StringUtils.formatNumber(oCedente.getContaBancaria().getContaDigito(), HeaderLote.DIGITO_VERIFICADOR_DA_CONTA.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.DIGITO_VERIFICADOR_DA_AGENCIA_CONTA, "DIGITO_VERIFICADOR_DA_AGENCIA_CONTA", StringUtils.formatAlpha(" ", HeaderLote.DIGITO_VERIFICADOR_DA_AGENCIA_CONTA.getSize(), false), false);
			setRegistro(headerLote, HeaderLote.NOME_DA_EMPRESA, "NOME_DA_EMPRESA", StringUtils.formatAlpha(StringUtils.removeAccent(oCedente.getContratante().getNome()).toUpperCase(), HeaderLote.NOME_DA_EMPRESA.getSize(), false), true);
/*
 * FIXME Comentei estas linhas por causa do erro.
 * Verificar pois houve mudan�as.			
 */
//			setRegistro(headerLote, HeaderLote.MENSAGEM_1, "MENSAGEM_1", StringUtils.formatAlpha(StringUtils.removeAccent(verificarNull(oCedente.getInstrucoes0()).toUpperCase()), HeaderLote.MENSAGEM_1.getSize(), false), false);
//			setRegistro(headerLote, HeaderLote.MENSAGEM_2, "MENSAGEM_2", StringUtils.formatAlpha(StringUtils.removeAccent(verificarNull(oCedente.getInstrucoes1()).toUpperCase()), HeaderLote.MENSAGEM_2.getSize(), false), false);
			setRegistro(headerLote, HeaderLote.NUMERO_REMESSA_RETORNO, "NUMERO_REMESSA_RETORNO", StringUtils.formatNumber("0", HeaderLote.NUMERO_REMESSA_RETORNO.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.DATA_DE_GRAVACAO_REMESSA_RETORNO, "DATA_DE_GRAVACAO_REMESSA_RETORNO", StringUtils.formatNumber(formatarData(CalendarUtils.getCalendar()), HeaderLote.DATA_DE_GRAVACAO_REMESSA_RETORNO.getSize(), true), true);
			setRegistro(headerLote, HeaderLote.DATA_DO_CREDITO, "DATA_DO_CREDITO", StringUtils.formatAlpha(" ", HeaderLote.DATA_DO_CREDITO.getSize(), false), false);
			setRegistro(headerLote, HeaderLote.USO_EXCLUSIVO_FEBRABAN_2, "USO_EXCLUSIVO_FEBRABAN_2", StringUtils.formatAlpha(" ", HeaderLote.USO_EXCLUSIVO_FEBRABAN_2.getSize(), false), false);
			
			/* Adicionando os headers no arquivo */
			arquivo.setHeaderArquivo(headerArquivo);
			Lote lote = new Lote();
			lote.setHeaderLote(headerLote);
			arquivo.getLotes().add(lote);
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_CRIANDO_ARQUIVO"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_CRIANDO_ESTRUTURA_REMESSA"));
		}

		BigDecimal tituloOkValorTotal = new BigDecimal("0"); //Totalizador dos valores dos titulos OK que puderam ser inseridos no arquivo de remessa
		BigDecimal tituloNoOkValorTotal = new BigDecimal("0"); //Totalizador dos valores dos titulos NOOK - N�O puderam ser inseridos no arquivo de remessa  
		MessageList tituloOkList = new MessageList(); //possui mensagens informativas sobre os titulos que puderam ser inseridos no arquivo de remessa
		MessageList tituloNoOkList = new MessageList(); //possui mensagens informativas sobre os titulos que N�O puderam ser inseridos no arquivo de remessa
		int titulosTotal = 0;
		int titulosInseridosOk = 0;
		boolean tituloOk;

		/* Construindo Segmentos P, Q, R e S */
		int numeroSequencialRegistroLote = 0; //a cada segmento deve-se incrementar este valor 
		for (IEntity<DocumentoTitulo> titulo : entityList){
			DocumentoTitulo oTitulo = titulo.getObject();
			titulosTotal += 1;
			tituloOk = true;

			try {
				segmentoP = new SegmentoP("");
				segmentoQ = new SegmentoQ("");
//				segmentoR = new SegmentoR("");
//				segmentoS = new SegmentoS("");
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_CRIANDO_ESTRUTURA_REMESSA"));
			}

			try{
				numeroSequencialRegistroLote++;
				/* Construindo Segmento P */
				setRegistro(segmentoP, SegmentoP.CODIGO_DO_BANCO_NA_COMPENSACAO, "CODIGO_DO_BANCO_NA_COMPENSACAO", StringUtils.formatNumber(oCedente.getContaBancaria().getBanco().getCodigo(), SegmentoP.CODIGO_DO_BANCO_NA_COMPENSACAO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.LOTE_DE_SERVICO, "LOTE_DE_SERVICO", StringUtils.formatNumber("0001", SegmentoP.LOTE_DE_SERVICO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.REGISTRO_DETALHE, "REGISTRO_DETALHE", StringUtils.formatNumber("3", SegmentoP.REGISTRO_DETALHE.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.NUMERO_SEQUENCIAL_DO_REGISTRO_NO_LOTE, "NUMERO_SEQUENCIAL_DO_REGISTRO_NO_LOTE", StringUtils.formatNumber(String.valueOf(numeroSequencialRegistroLote), SegmentoP.NUMERO_SEQUENCIAL_DO_REGISTRO_NO_LOTE.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.CODIGO_SEGMENTO_DO_REGISTRO_DETALHE, "CODIGO_SEGMENTO_DO_REGISTRO_DETALHE", StringUtils.formatAlpha("P", SegmentoP.CODIGO_SEGMENTO_DO_REGISTRO_DETALHE.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.USO_EXCLUSIVO_FEBRABAN_1, "USO_EXCLUSIVO_FEBRABAN_1", StringUtils.formatAlpha(" ", SegmentoP.USO_EXCLUSIVO_FEBRABAN_1.getSize(), false), false);
				setRegistro(segmentoP, SegmentoP.CODIGO_DE_MOVIMENTO, "CODIGO_DE_MOVIMENTO", StringUtils.formatNumber("01", SegmentoP.CODIGO_DE_MOVIMENTO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.AGENCIA_MANTENEDORA_DA_CONTA, "AGENCIA_MANTENEDORA_DA_CONTA", StringUtils.formatNumber(oCedente.getContaBancaria().getAgenciaCodigo(), SegmentoP.AGENCIA_MANTENEDORA_DA_CONTA.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.DIGITO_VERIFICADOR_DA_AGENCIA, "DIGITO_VERIFICADOR_DA_AGENCIA", StringUtils.formatNumber(oCedente.getContaBancaria().getAgenciaDigito(), SegmentoP.DIGITO_VERIFICADOR_DA_AGENCIA.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.NUMERO_DA_CONTA_CORRENTE, "NUMERO_DA_CONTA_CORRENTE", StringUtils.formatNumber(oCedente.getContaBancaria().getContaCodigo(), SegmentoP.NUMERO_DA_CONTA_CORRENTE.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.DIGITO_VERIFICADOR_DA_CONTA, "DIGITO_VERIFICADOR_DA_CONTA", StringUtils.formatNumber(oCedente.getContaBancaria().getContaDigito(), SegmentoP.DIGITO_VERIFICADOR_DA_CONTA.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.DIGITO_VERIFICADOR_DA_AGENCIA_CONTA, "DIGITO_VERIFICADOR_DA_AGENCIA_CONTA", StringUtils.formatAlpha(" ", SegmentoP.DIGITO_VERIFICADOR_DA_AGENCIA_CONTA.getSize(), false), false);
				/* O a seguir � o nosso n�mero de 11 posi��es que DEVE ser gerado pelo banco, logo, no arquivo este n�mero deve estar zerado, com 11 posi��es e formatado no tipo Alfanum�rico */
				setRegistro(segmentoP, SegmentoP.IDENTIFICACAO_DO_TITULO_NO_BANCO, "IDENTIFICACAO_DO_TITULO_NO_BANCO", StringUtils.formatAlpha("00000000000", SegmentoP.IDENTIFICACAO_DO_TITULO_NO_BANCO.getSize(), false), false);
				/* C�digo da carteira, segundo o manual � 1 - Cobran�a Simples */
				//TODO - verificar se a carteira indicada no cedente n�o � utilizada!?!?
				setRegistro(segmentoP, SegmentoP.CODIGO_DA_CARTEIRA, "CODIGO_DA_CARTEIRA", StringUtils.formatNumber("1", SegmentoP.CODIGO_DA_CARTEIRA.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.FORMA_DE_CADASTRAMENTO_DO_TITULO_NO_BANCO, "FORMA_DE_CADASTRAMENTO_DO_TITULO_NO_BANCO", StringUtils.formatNumber("1", SegmentoP.FORMA_DE_CADASTRAMENTO_DO_TITULO_NO_BANCO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.TIPO_DE_DOCUMENTO, "TIPO_DE_DOCUMENTO", StringUtils.formatNumber("1", SegmentoP.TIPO_DE_DOCUMENTO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.IDENTIFICACAO_DA_EMISSAO_DO_BLOQUETO, "IDENTIFICACAO_DA_EMISSAO_DO_BLOQUETO", StringUtils.formatNumber("1", SegmentoP.IDENTIFICACAO_DA_EMISSAO_DO_BLOQUETO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.IDENTIFICACAO_DA_DISTRIBUICAO, "IDENTIFICACAO_DA_DISTRIBUICAO", StringUtils.formatNumber("1", SegmentoP.IDENTIFICACAO_DA_DISTRIBUICAO.getSize(), true), true);
				//apesar do tamanho do campo para 'NUMERO_DO_DOCUMENTO_DE_COBRANCA' ser de 15 posi��es, o banco s� interpreta 10
				setRegistro(segmentoP, SegmentoP.NUMERO_DO_DOCUMENTO_DE_COBRANCA, "NUMERO_DO_DOCUMENTO_DE_COBRANCA", StringUtils.formatAlpha(String.valueOf(oTitulo.getId()), 10, true) + StringUtils.formatAlpha(" ", 5, false), false);
				setRegistro(segmentoP, SegmentoP.DATA_DE_VENCIMENTO_DO_TITULO, "DATA_DE_VENCIMENTO_DO_TITULO", StringUtils.formatNumber(formatarData(oTitulo.getDataVencimento()), SegmentoP.DATA_DE_VENCIMENTO_DO_TITULO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.VALOR_NOMINAL_DO_TITULO, "VALOR_NOMINAL_DO_TITULO",oTitulo.getValor(), true);
				setRegistro(segmentoP, SegmentoP.AGENCIA_ENCARREGADA_DA_COBRANCA, "AGENCIA_ENCARREGADA_DA_COBRANCA", StringUtils.formatNumber("0", SegmentoP.AGENCIA_ENCARREGADA_DA_COBRANCA.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.DIGITO_VERIFICADOR_DA_AGENCIA_2, "DIGITO_VERIFICADOR_DA_AGENCIA_2", StringUtils.formatAlpha(" ", SegmentoP.DIGITO_VERIFICADOR_DA_AGENCIA_2.getSize(), false), false);
				setRegistro(segmentoP, SegmentoP.ESPECIE_DO_TITULO, "ESPECIE_DO_TITULO", StringUtils.formatNumber("04", SegmentoP.ESPECIE_DO_TITULO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.IDENTIFICACAO_DE_TITULO_ACEITO_NAO_ACEITO, "IDENTIFICACAO_DE_TITULO_ACEITO_NAO_ACEITO", StringUtils.formatAlpha("N", SegmentoP.IDENTIFICACAO_DE_TITULO_ACEITO_NAO_ACEITO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.DATA_DA_EMISSAO_DO_TITULO, "DATA_DA_EMISSAO_DO_TITULO", StringUtils.formatNumber(formatarData(CalendarUtils.getCalendar()), SegmentoP.DATA_DA_EMISSAO_DO_TITULO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.CODIGO_DO_JUROS_DE_MORA, "CODIGO_DO_JUROS_DE_MORA", StringUtils.formatNumber("1", SegmentoP.CODIGO_DO_JUROS_DE_MORA.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.DATA_DO_JUROS_DE_MORA, "DATA_DO_JUROS_DE_MORA", StringUtils.formatNumber("0", SegmentoP.DATA_DO_JUROS_DE_MORA.getSize(), true), true);
				/*
				 * FIXME Comentei estas linhas por causa do erro.
				 * Verificar pois houve mudan�as.			
				 */
//				setRegistro(segmentoP, SegmentoP.JUROS_DE_MORA_POR_DIA_TAXA, "JUROS_DE_MORA_POR_DIA_TAXA", oCedente.getJurosMora(), true);
				setRegistro(segmentoP, SegmentoP.CODIGO_DO_DESCONTO_1, "CODIGO_DO_DESCONTO_1", StringUtils.formatNumber("2", SegmentoP.CODIGO_DO_DESCONTO_1.getSize(), true), true);
				Calendar dataDesconto = (Calendar)oTitulo.getDataVencimento().clone();
				dataDesconto.add(Calendar.DAY_OF_MONTH, -oCedente.getDiasAntecipacao());
				setRegistro(segmentoP, SegmentoP.DATA_DO_DESCONTO_1, "DATA_DO_DESCONTO_1", StringUtils.formatNumber(formatarData(dataDesconto), SegmentoP.DATA_DO_DESCONTO_1.getSize(), true), true);
				/*
				 * FIXME Comentei estas linhas por causa do erro.
				 * Verificar pois houve mudan�as.			
				 */
//				setRegistro(segmentoP, SegmentoP.VALOR_PERCENTUAL_A_SER_CONCEDIDO, "VALOR_PERCENTUAL_A_SER_CONCEDIDO", oCedente.getDescontoAntecipacao()!=null?oCedente.getDescontoAntecipacao():DecimalUtils.ZERO, true);
				setRegistro(segmentoP, SegmentoP.VALOR_DO_IOF_A_SER_RECOLHIDO, "VALOR_DO_IOF_A_SER_RECOLHIDO", StringUtils.formatNumber("0", SegmentoP.VALOR_DO_IOF_A_SER_RECOLHIDO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.VALOR_DO_ABATIMENTO, "VALOR_DO_ABATIMENTO", StringUtils.formatNumber("0", SegmentoP.VALOR_DO_ABATIMENTO.getSize(), true), true);
				/* formatar com 0's a esquerda antes de gravar (mesmo sendo alfanum�rico */
				setRegistro(segmentoP, SegmentoP.IDENTIFICACAO_DO_TITULO_NA_EMPRESA, "IDENTIFICACAO_DO_TITULO_NA_EMPRESA", StringUtils.formatNumber(String.valueOf(oTitulo.getId()), SegmentoP.IDENTIFICACAO_DO_TITULO_NA_EMPRESA.getSize(), true), true);
				//se utiliza protesto, usar c�digo 1 (dias corridos) ou 2 (dias �teis) - caso n�o use, c�digo 3 (n�o protestar)
				if (quantidadeDiasProtesto != null){ //utilizar protesto (c�digo 2)
					setRegistro(segmentoP, SegmentoP.CODIGO_PARA_PROTESTO, "CODIGO_PARA_PROTESTO", StringUtils.formatNumber("2", SegmentoP.CODIGO_PARA_PROTESTO.getSize(), true), true);
					setRegistro(segmentoP, SegmentoP.NUMERO_DE_DIAS_PARA_PROTESTO, "NUMERO_DE_DIAS_PARA_PROTESTO", StringUtils.formatNumber(String.valueOf(quantidadeDiasProtesto), SegmentoP.NUMERO_DE_DIAS_PARA_PROTESTO.getSize(), true), true);
				}else{ //n�o utiliza protesto (c�digo 3)
					setRegistro(segmentoP, SegmentoP.CODIGO_PARA_PROTESTO, "CODIGO_PARA_PROTESTO", StringUtils.formatNumber("3", SegmentoP.CODIGO_PARA_PROTESTO.getSize(), true), true);
					setRegistro(segmentoP, SegmentoP.NUMERO_DE_DIAS_PARA_PROTESTO, "NUMERO_DE_DIAS_PARA_PROTESTO", StringUtils.formatNumber("0", SegmentoP.NUMERO_DE_DIAS_PARA_PROTESTO.getSize(), true), true);
				}
				setRegistro(segmentoP, SegmentoP.CODIGO_PARA_BAIXA_DEVOLUCAO, "CODIGO_PARA_BAIXA_DEVOLUCAO", StringUtils.formatNumber("2", SegmentoP.CODIGO_PARA_BAIXA_DEVOLUCAO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.NUMERO_DE_DIAS_PARA_BAIXA_DEVOLUCAO, "NUMERO_DE_DIAS_PARA_BAIXA_DEVOLUCAO", StringUtils.formatNumber("0", SegmentoP.NUMERO_DE_DIAS_PARA_BAIXA_DEVOLUCAO.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.CODIGO_DA_MOEDA, "CODIGO_DA_MOEDA", StringUtils.formatNumber("09", SegmentoP.CODIGO_DA_MOEDA.getSize(), true), true);
				/* TODO - verificar porque usa esse n�mero - dica do banco: deixar zerado */
				setRegistro(segmentoP, SegmentoP.NUMERO_DO_CONTROLE_DA_OPERACAO_DEBITO_CRED, "NUMERO_DO_CONTROLE_DA_OPERACAO_DEBITO_CRED", StringUtils.formatNumber("16163644", SegmentoP.NUMERO_DO_CONTROLE_DA_OPERACAO_DEBITO_CRED.getSize(), true), true);
				setRegistro(segmentoP, SegmentoP.USO_EXCLUSIVO_FEBRABAN_2, "USO_EXCLUSIVO_FEBRABAN_2", StringUtils.formatAlpha(" ", SegmentoP.USO_EXCLUSIVO_FEBRABAN_2.getSize(), false), false);
				
				numeroSequencialRegistroLote++;
				/* Segmento Q */
				setRegistro(segmentoQ, SegmentoQ.CODIGO_DO_BANCO_NA_COMPENSACAO, "CODIGO_DO_BANCO_NA_COMPENSACAO", StringUtils.formatNumber(oCedente.getContaBancaria().getBanco().getCodigo(), SegmentoQ.CODIGO_DO_BANCO_NA_COMPENSACAO.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.LOTE_DE_SERVICO, "LOTE_DE_SERVICO", StringUtils.formatNumber("0001", SegmentoQ.LOTE_DE_SERVICO.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.REGISTRO_DETALHE, "REGISTRO_DETALHE", StringUtils.formatNumber("3", SegmentoQ.REGISTRO_DETALHE.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.NUMERO_SEQUENCIAL_DO_REGISTRO_NO_LOTE, "NUMERO_SEQUENCIAL_DO_REGISTRO_NO_LOTE", StringUtils.formatNumber(String.valueOf(numeroSequencialRegistroLote), SegmentoQ.NUMERO_SEQUENCIAL_DO_REGISTRO_NO_LOTE.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.CODIGO_SEGMENTO_DO_REGISTRO_DETALHE, "CODIGO_SEGMENTO_DO_REGISTRO_DETALHE", StringUtils.formatAlpha("Q", SegmentoQ.CODIGO_SEGMENTO_DO_REGISTRO_DETALHE.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.USO_EXCLUSIVO_FEBRABAN, "USO_EXCLUSIVO_FEBRABAN", StringUtils.formatAlpha(" ", SegmentoQ.USO_EXCLUSIVO_FEBRABAN.getSize(), false), false);
				setRegistro(segmentoQ, SegmentoQ.CODIGO_DE_MOVIMENTO, "CODIGO_DE_MOVIMENTO", StringUtils.formatNumber("01", SegmentoQ.CODIGO_DE_MOVIMENTO.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.TIPO_DE_INSCRICAO, "TIPO_DE_INSCRICAO", StringUtils.formatNumber("1", SegmentoQ.TIPO_DE_INSCRICAO.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.NUMERO_DE_INSCRICAO, "NUMERO_DE_INSCRICAO", StringUtils.formatNumber(oTitulo.getContrato().getPessoa().getDocumento(), SegmentoQ.NUMERO_DE_INSCRICAO.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.NOME, "NOME", StringUtils.formatAlpha(StringUtils.removeAccent(oTitulo.getContrato().getPessoa().getNome().toUpperCase()), SegmentoQ.NOME.getSize(), false), true);
				//apesar do campo ser de 40 posi��es, o banco s� interpreta 37
				setRegistro(segmentoQ, SegmentoQ.ENDERECO, "ENDERECO", StringUtils.formatAlpha(StringUtils.removeAccent(verificarNull(UtilsRemessa.formatarEndereco(oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia(), 37)).toUpperCase()) + StringUtils.formatAlpha("", 3, false), SegmentoQ.ENDERECO.getSize(), false), true);
				/* O banco n�o interpreta o campo BAIRRO */
//				setRegistro(segmentoQ, SegmentoQ.BAIRRO, "BAIRRO", StringUtils.formatAlpha(verificarNull(StringUtils.removeAccent(oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getBairro().getNome()).toUpperCase()), SegmentoQ.BAIRRO.getSize(), false), false);
				setRegistro(segmentoQ, SegmentoQ.BAIRRO, "BAIRRO", StringUtils.formatAlpha(" ", SegmentoQ.BAIRRO.getSize(), false), false);
				setRegistro(segmentoQ, SegmentoQ.CEP, "CEP", StringUtils.formatNumber(oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getCep().substring(0, oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getCep().length()-3), SegmentoQ.CEP.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.SUFIXO_DO_CEP, "SUFIXO_DO_CEP", StringUtils.formatNumber(oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getCep().substring(oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getCep().length()-SegmentoQ.SUFIXO_DO_CEP.getSize(), oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getCep().length()), SegmentoQ.SUFIXO_DO_CEP.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.CIDADE, "CIDADE", StringUtils.formatAlpha(StringUtils.removeAccent(oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getMunicipio().getNome().toUpperCase()), SegmentoQ.CIDADE.getSize(), false), true);
				setRegistro(segmentoQ, SegmentoQ.UNIDADE_DA_FEDERACAO, "UNIDADE_DA_FEDERACAO", StringUtils.formatAlpha(StringUtils.removeAccent(oTitulo.getContrato().getPessoa().getEnderecoCorrespondencia().getMunicipio().getUf().getSigla().toUpperCase()), SegmentoQ.UNIDADE_DA_FEDERACAO.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.TIPO_DE_INSCRICAO_2, "TIPO_DE_INSCRICAO_2", StringUtils.formatNumber("0", SegmentoQ.TIPO_DE_INSCRICAO_2.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.NUMERO_DE_INSCRICAO_2, "NUMERO_DE_INSCRICAO_2", StringUtils.formatNumber("0", SegmentoQ.NUMERO_DE_INSCRICAO_2.getSize(), true), true);
				setRegistro(segmentoQ, SegmentoQ.NOME_DO_SACADOR_AVALISTA, "NOME_DO_SACADOR_AVALISTA", StringUtils.formatAlpha(" ", SegmentoQ.NOME_DO_SACADOR_AVALISTA.getSize(), false), false);
				setRegistro(segmentoQ, SegmentoQ.CODIGO_BANCO_CORRESPONDENTE_NA_COMPENSACAO, "CODIGO_BANCO_CORRESPONDENTE_NA_COMPENSACAO", StringUtils.formatNumber("0", SegmentoQ.CODIGO_BANCO_CORRESPONDENTE_NA_COMPENSACAO.getSize(), true), true);
				//o nosso n�mero ser� criado pelo banco
				setRegistro(segmentoQ, SegmentoQ.NOSSO_NUMERO_NO_BANCO_CORRESPONDENTE, "NOSSO_NUMERO_NO_BANCO_CORRESPONDENTE", StringUtils.formatAlpha(" ", SegmentoQ.NOSSO_NUMERO_NO_BANCO_CORRESPONDENTE.getSize(), false), false);
				setRegistro(segmentoQ, SegmentoQ.USO_EXCLUSIVO, "USO_EXCLUSIVO", StringUtils.formatAlpha(" ", SegmentoQ.USO_EXCLUSIVO.getSize(), false), false);
				
				numeroSequencialRegistroLote++;
				/* Segmento R */
//				setRegistro(segmentoR, SegmentoR.CODIGO_DO_BANCO_NA_COMPENSACAO, "CODIGO_DO_BANCO_NA_COMPENSACAO", StringUtils.formatNumber(oCedente.getContaBancaria().getBanco().getCodigo(), SegmentoR.CODIGO_DO_BANCO_NA_COMPENSACAO.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.LOTE_DE_SERVICO, "LOTE_DE_SERVICO", StringUtils.formatNumber("0001", SegmentoR.LOTE_DE_SERVICO.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.REGISTRO_DETALHE, "REGISTRO_DETALHE", StringUtils.formatNumber("3", SegmentoR.REGISTRO_DETALHE.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.NUMERO_SEQUENCIAL_DO_REGISTRO_NO_LOTE, "NUMERO_SEQUENCIAL_DO_REGISTRO_NO_LOTE", StringUtils.formatNumber(String.valueOf(numeroSequencialRegistroLote), SegmentoR.NUMERO_SEQUENCIAL_DO_REGISTRO_NO_LOTE.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.CODIGO_SEGMENTO_DO_REGISTRO_DETALHE, "CODIGO_SEGMENTO_DO_REGISTRO_DETALHE", StringUtils.formatAlpha("R", SegmentoR.CODIGO_SEGMENTO_DO_REGISTRO_DETALHE.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.USO_EXCLUSIVO_FEBRABAN_1, "USO_EXCLUSIVO_FEBRABAN_1", StringUtils.formatAlpha(" ", SegmentoR.USO_EXCLUSIVO_FEBRABAN_1.getSize(), false), false);
				//c�digo de movimento '01' = Entrada de t�tulos
//				setRegistro(segmentoR, SegmentoR.CODIGO_DE_MOVIMENTO, "CODIGO_DE_MOVIMENTO", StringUtils.formatNumber("01", SegmentoR.CODIGO_DE_MOVIMENTO.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.CODIGO_DO_DESCONTO_2, "CODIGO_DO_DESCONTO_2", StringUtils.formatNumber("0", SegmentoR.CODIGO_DO_DESCONTO_2.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.DATA_DO_DESCONTO_2, "DATA_DO_DESCONTO_2", StringUtils.formatNumber("0", SegmentoR.DATA_DO_DESCONTO_2.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.VALOR_PERCENTUAL_A_SER_CONCEDIDO_1, "VALOR_PERCENTUAL_A_SER_CONCEDIDO_1", StringUtils.formatNumber("0", SegmentoR.VALOR_PERCENTUAL_A_SER_CONCEDIDO_1.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.CODIGO_DO_DESCONTO_3, "CODIGO_DO_DESCONTO_3", StringUtils.formatNumber("0", SegmentoR.CODIGO_DO_DESCONTO_3.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.DATA_DO_DESCONTO_3, "DATA_DO_DESCONTO_3", StringUtils.formatNumber("0", SegmentoR.DATA_DO_DESCONTO_3.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.VALOR_PERCENTUAL_A_SER_CONCEDIDO_2, "VALOR_PERCENTUAL_A_SER_CONCEDIDO_2", StringUtils.formatNumber("0", SegmentoR.VALOR_PERCENTUAL_A_SER_CONCEDIDO_2.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.CODIGO_DA_MULTA, "CODIGO_DA_MULTA", StringUtils.formatNumber("2", SegmentoR.VALOR_PERCENTUAL_A_SER_CONCEDIDO_2.getSize(), true), true);
				Calendar dataMulta = (Calendar)oTitulo.getDataVencimento().clone();
				dataMulta.add(Calendar.DAY_OF_MONTH, 1);
//				setRegistro(segmentoR, SegmentoR.DATA_DA_MULTA, "DATA_DA_MULTA", StringUtils.formatNumber(formatarData(dataMulta), SegmentoR.DATA_DA_MULTA.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.VALOR_PERCENTUAL_A_SER_APLICADO, "VALOR_PERCENTUAL_A_SER_APLICADO", oCedente.getMultaAtraso(), true);
//				setRegistro(segmentoR, SegmentoR.INFORMACAO_DO_BANCO_AO_SACADO, "INFORMACAO_DO_BANCO_AO_SACADO", StringUtils.formatAlpha(" ", SegmentoR.INFORMACAO_DO_BANCO_AO_SACADO.getSize(), false), false);
//				setRegistro(segmentoR, SegmentoR.MENSAGEM_3, "MENSAGEM_3", StringUtils.formatAlpha(" ", SegmentoR.MENSAGEM_3.getSize(), false), false);
//				setRegistro(segmentoR, SegmentoR.MENSAGEM_4, "MENSAGEM_4", StringUtils.formatAlpha(" ", SegmentoR.MENSAGEM_4.getSize(), false), false);
//				setRegistro(segmentoR, SegmentoR.CODIGO_DO_BANCO_DA_CONTA_DO_DEBITO, "CODIGO_DO_BANCO_DA_CONTA_DO_DEBITO", StringUtils.formatNumber("0", SegmentoR.CODIGO_DO_BANCO_DA_CONTA_DO_DEBITO.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.CODIGO_DA_AGENCIA_DO_DEBITO, "CODIGO_DA_AGENCIA_DO_DEBITO", StringUtils.formatNumber("0", SegmentoR.CODIGO_DA_AGENCIA_DO_DEBITO.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.CONTA_CORRENTE_DV_DO_DEBITO, "CONTA_CORRENTE_DV_DO_DEBITO", StringUtils.formatNumber("0", SegmentoR.CONTA_CORRENTE_DV_DO_DEBITO.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.CODIGOS_DE_OCORRENCIA_DO_SACADO, "CODIGOS_DE_OCORRENCIA_DO_SACADO", StringUtils.formatNumber("0", SegmentoR.CODIGOS_DE_OCORRENCIA_DO_SACADO.getSize(), true), true);
//				setRegistro(segmentoR, SegmentoR.USO_EXCLUSIVO_FEBRABAN_2, "USO_EXCLUSIVO_FEBRABAN_2", StringUtils.formatAlpha(" ", SegmentoR.USO_EXCLUSIVO_FEBRABAN_2.getSize(), false), false);
				
//				numeroSequencialRegistroLote++;
				/* Segmento S */
				//TODO - verificar se ir� utilizar Segmento S (Lucio)
				
				/* Atribuindo ocorr�ncia ao titulo */
				try{
					IEntity<Ocorrencia> ultimaOcorrencia = UtilsOcorrencia.obterOcorrencia(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), Ocorrencia.REMESSA_ENVIADA.getCodigo(), serviceDataOwner);
					titulo.setPropertyValue(DocumentoTitulo.ULTIMA_OCORRENCIA, ultimaOcorrencia);
					titulo.setPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA, CalendarUtils.getCalendar());

					/* Adicionando ocorr�ncia ao hist�rico de ocorr�ncias deste titulo */
					IEntity<OcorrenciaControle> ocorrencia = UtilsCrud.create(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), OcorrenciaControle.class, serviceDataOwner);
					ocorrencia.setPropertyValue(OcorrenciaControle.TITULO, titulo);
					ocorrencia.setPropertyValue(OcorrenciaControle.OCORRENCIA, ultimaOcorrencia);
					ocorrencia.setPropertyValue(OcorrenciaControle.DATA_OCORRENCIA, CalendarUtils.getCalendar());
					UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), ocorrencia, serviceDataOwner);

					titulo.getProperty(DocumentoTitulo.OCORRENCIAS).getValue().<OcorrenciaControle>getAsEntityList().add(ocorrencia);
				}catch (BusinessException e) {
					e.printStackTrace();
//					tituloNoOkList.add(BusinessMessage.TYPE_INFO, Gerenciador001.class, "REMESSA_ERRO_AO_SETAR", oTitulo.getNumeroDocumento(), oTitulo.getDataVencimento(),oTitulo.getValor(), "ULTIMA_OCORRENCIA");
//					tituloOk = false;
					throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "REMESSA_ERRO_AO_SETAR", oTitulo.getNumeroDocumento(), oTitulo.getDataVencimento(),oTitulo.getValor(), "ULTIMA_OCORRENCIA"));
				}
			}catch (BusinessException e) {
				tituloNoOkList.add(e.getErrorList());
				tituloOk = false;
			}

			if (tituloOk){
				/* Criando um segmento*/
				Segmento segmento = new Segmento();
				segmento.set(Segmento.SEGMENTO_P, segmentoP);
				segmento.set(Segmento.SEGMENTO_Q, segmentoQ);
//				segmento.set(Segmento.SEGMENTO_R, segmentoR);
//				segmento.set(Segmento.SEGMENTO_S, segmentoS);
				
				/* adicionando os segmentos no arquivo */
				arquivo.getLotes().get(0).getSegmentos().add(segmento);
				
				/* Atualizando o titulo */
				try {
					UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), titulo, serviceDataOwner);
				} catch (BusinessException e) {
					throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_ATUALIZANDO_ENTIDADE", titulo.getObject().toString()));
				}
				titulosInseridosOk += 1;
				tituloOkList.add(BusinessMessage.TYPE_INFO, Gerenciador001.class, "REMESSA_SUCESSO", oTitulo.getContrato().toString() , String.valueOf(oTitulo.getId()), CalendarUtils.formatDate(oTitulo.getDataVencimento()), DecimalUtils.formatBigDecimal(oTitulo.getValor()));
				tituloOkValorTotal = tituloOkValorTotal.add(oTitulo.getValor());
			}else{
				tituloNoOkValorTotal = tituloNoOkValorTotal.add(oTitulo.getValor()) ;
			}
		}//for

		/* Construindo Trailler de Lote e de Arquivo */
		try{
			trailerLote = new TrailerLote("");
			setRegistro(trailerLote, TrailerLote.CODIGO_DO_BANCO_NA_COMPENSACAO, "CODIGO_DO_BANCO_NA_COMPENSACAO", StringUtils.formatNumber(oCedente.getContaBancaria().getBanco().getCodigo(), TrailerLote.CODIGO_DO_BANCO_NA_COMPENSACAO.getSize(), true), true);
			setRegistro(trailerLote, TrailerLote.LOTE_DE_SERVICO, "LOTE_DE_SERVICO", StringUtils.formatNumber("0001", TrailerLote.LOTE_DE_SERVICO.getSize(), true), true);
			setRegistro(trailerLote, TrailerLote.REGISTRO_TRAILER_DO_LOTE, "REGISTRO_TRAILER_DO_LOTE", StringUtils.formatNumber("5", TrailerLote.REGISTRO_TRAILER_DO_LOTE.getSize(), true), true);
			setRegistro(trailerLote, TrailerLote.USO_EXCLUSIVO_FEBRABAN_1, "USO_EXCLUSIVO_FEBRABAN_1", StringUtils.formatAlpha(" ", TrailerLote.USO_EXCLUSIVO_FEBRABAN_1.getSize(), false), false);
			//atribui-se o 'numeroSequencialRegistroLote + 2' pois deve-se indicar o total de linhas dos segmentos mais 2 linhas que correspondem ao header de lote e trailer de lote
			setRegistro(trailerLote, TrailerLote.QUANTIDADE_DE_REGISTROS_DO_LOTE, "QUANTIDADE_DE_REGISTROS_DO_LOTE", StringUtils.formatNumber(String.valueOf(numeroSequencialRegistroLote+2), TrailerLote.QUANTIDADE_DE_REGISTROS_DO_LOTE.getSize(), true), true);
			setRegistro(trailerLote, TrailerLote.QUANTIDADE_DE_TITULOS_EM_COBRANCA_1, "QUANTIDADE_DE_TITULOS_EM_COBRANCA_1", StringUtils.formatNumber("0", TrailerLote.QUANTIDADE_DE_TITULOS_EM_COBRANCA_1.getSize(), true), true);
			setRegistro(trailerLote, TrailerLote.VALOR_TOTAL_DOS_TITULOS_EM_CARTEIRAS_1, "VALOR_TOTAL_DOS_TITULOS_EM_CARTEIRAS_1", StringUtils.formatNumber("0", TrailerLote.VALOR_TOTAL_DOS_TITULOS_EM_CARTEIRAS_1.getSize(), true), true);
			setRegistro(trailerLote, TrailerLote.QUANTIDADE_DE_TITULOS_EM_COBRANCA_2, "QUANTIDADE_DE_TITULOS_EM_COBRANCA_2", StringUtils.formatNumber("0", TrailerLote.QUANTIDADE_DE_TITULOS_EM_COBRANCA_2.getSize(), true), true);
			setRegistro(trailerLote, TrailerLote.VALOR_TOTAL_DOS_TITULOS_EM_CARTEIRAS_2, "VALOR_TOTAL_DOS_TITULOS_EM_CARTEIRAS_2", StringUtils.formatNumber("0", TrailerLote.VALOR_TOTAL_DOS_TITULOS_EM_CARTEIRAS_2.getSize(), true), true);
			setRegistro(trailerLote, TrailerLote.QUANTIDADE_DE_TITULOS_EM_COBRANCA_3, "QUANTIDADE_DE_TITULOS_EM_COBRANCA_3", StringUtils.formatNumber("0", TrailerLote.QUANTIDADE_DE_TITULOS_EM_COBRANCA_3.getSize(), true), true);
			setRegistro(trailerLote, TrailerLote.VALOR_TOTAL_DOS_TITULOS_EM_CARTEIRAS_3, "VALOR_TOTAL_DOS_TITULOS_EM_CARTEIRAS_3", StringUtils.formatNumber("0", TrailerLote.VALOR_TOTAL_DOS_TITULOS_EM_CARTEIRAS_3.getSize(), true), true);
			setRegistro(trailerLote, TrailerLote.QUANTIDADE_DE_TITULOS_EM_COBRANCA_4, "QUANTIDADE_DE_TITULOS_EM_COBRANCA_4", StringUtils.formatNumber("0", TrailerLote.QUANTIDADE_DE_TITULOS_EM_COBRANCA_4.getSize(), true), true);
			setRegistro(trailerLote, TrailerLote.VALOR_TOTAL_DOS_TITULOS_EM_CARTEIRAS_4, "VALOR_TOTAL_DOS_TITULOS_EM_CARTEIRAS_4", StringUtils.formatNumber("0", TrailerLote.VALOR_TOTAL_DOS_TITULOS_EM_CARTEIRAS_4.getSize(), true), true);
			setRegistro(trailerLote, TrailerLote.NUMERO_DO_AVISO_DE_LANCAMENTO, "NUMERO_DO_AVISO_DE_LANCAMENTO", StringUtils.formatAlpha(" ", TrailerLote.NUMERO_DO_AVISO_DE_LANCAMENTO.getSize(), false), false);
			setRegistro(trailerLote, TrailerLote.USO_EXCLUSIVO_FEBRABAN_2, "USO_EXCLUSIVO_FEBRABAN_2", StringUtils.formatAlpha(" ", TrailerLote.USO_EXCLUSIVO_FEBRABAN_2.getSize(), false), false);
			
			trailerArquivo = new TrailerArquivo("");
			setRegistro(trailerArquivo, TrailerArquivo.CODIGO_DO_BANCO_NA_COMPENSACAO, "CODIGO_DO_BANCO_NA_COMPENSACAO", StringUtils.formatNumber(oCedente.getContaBancaria().getBanco().getCodigo(), TrailerArquivo.CODIGO_DO_BANCO_NA_COMPENSACAO.getSize(), true), true);
			setRegistro(trailerArquivo, TrailerArquivo.LOTE_DE_SERVICO, "LOTE_DE_SERVICO", StringUtils.formatNumber("9999", TrailerArquivo.LOTE_DE_SERVICO.getSize(), true), true);
			setRegistro(trailerArquivo, TrailerArquivo.REGISTRO_TRAILER_DE_ARQUIVO, "REGISTRO_TRAILER_DE_ARQUIVO", StringUtils.formatNumber("9", TrailerArquivo.REGISTRO_TRAILER_DE_ARQUIVO.getSize(), true), true);
			setRegistro(trailerArquivo, TrailerArquivo.USO_EXCLUSIVO_FEBRABAN_1, "USO_EXCLUSIVO_FEBRABAN_1", StringUtils.formatAlpha(" ", TrailerArquivo.USO_EXCLUSIVO_FEBRABAN_1.getSize(), false), false);
			setRegistro(trailerArquivo, TrailerArquivo.QUANTIDADE_DE_LOTES_DO_ARQUIVO, "QUANTIDADE_DE_LOTES_DO_ARQUIVO", StringUtils.formatNumber("1", TrailerArquivo.QUANTIDADE_DE_LOTES_DO_ARQUIVO.getSize(), true), true);
			//atribui-se o 'numeroSequencialRegistroLote + 4' pois deve-se indicar o total de linhas dos segmentos mais 4 linhas que correspondem ao header e trailer de lote e header e trailer de arquivo
			setRegistro(trailerArquivo, TrailerArquivo.QUANTIDADE_DE_REGISTROS_DO_ARQUIVO, "QUANTIDADE_DE_REGISTROS_DO_ARQUIVO", StringUtils.formatNumber(String.valueOf(numeroSequencialRegistroLote+4), TrailerArquivo.QUANTIDADE_DE_REGISTROS_DO_ARQUIVO.getSize(), true), true);
			setRegistro(trailerArquivo, TrailerArquivo.QUANTIDADE_DE_CONTAS_PARA_CONC_LOTES, "QUANTIDADE_DE_CONTAS_PARA_CONC_LOTES", StringUtils.formatAlpha(" ", TrailerArquivo.QUANTIDADE_DE_CONTAS_PARA_CONC_LOTES.getSize(), false), false);
			//TODO - verificar porque usa "." no final
			setRegistro(trailerArquivo, TrailerArquivo.USO_EXCLUSIVO_FEBRABAN_2, "USO_EXCLUSIVO_FEBRABAN_2", StringUtils.format(".", TrailerArquivo.USO_EXCLUSIVO_FEBRABAN_2.getSize(), " ", true, false), false);
			
			/* Todos as partes foram montadas, inserindo valores no arquivo de remessa */
			arquivo.getLotes().get(0).setTrailerLote(trailerLote);
			arquivo.setTrailerArquivo(trailerArquivo);
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_CRIANDO_ARQUIVO"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_CRIANDO_ESTRUTURA_REMESSA"));
		}

		/* Obtendo o cnpj do contratante e o numero sequencial da remessa para montar o nome do arquivo de remessa */
		int remessaSequencial = 1;
		long diferencaDias = 0;
		String nomeArquivoRemessa = null;
		try {
			String cnpj = oCedente.getContratante().getDocumento();
			if (cnpj.equals("") || cnpj.equals(null)){
				throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_MONTANDO_NOME_ARQUIVO", "CNPJ atribu�do: " + cnpj));
			}

			Calendar dataUltimaRemessa;
			if (oCedente.getRemessaUltimaData() != null){
				dataUltimaRemessa = (Calendar)oCedente.getRemessaUltimaData().clone();
			}else{
				dataUltimaRemessa = CalendarUtils.getCalendar();
			}
			Calendar data = CalendarUtils.getCalendar();
			diferencaDias = CalendarUtils.diffDay(data, dataUltimaRemessa);

			if(diferencaDias > 0){ //se o dia se alterou, o contador de remessa deve voltar ao valor 1
				remessaSequencial = 1;
			}else if(diferencaDias == 0){ //se o dia for o mesmo, obter o numero sequencial atrav�s do cedente
				remessaSequencial = oCedente.getRemessaNumeroSequencial();
				if (remessaSequencial == 0){ //caso seja a primeira vez que o Cedente esteja criando o arquivo
					remessaSequencial = 1;
				}
			}else{ //a diferen�a n�o deve ser negativa
				throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_MONTANDO_NOME_ARQUIVO", "Diferen�a entre a data atual e a data da �ltima remessa (N�O pode ser negativa): " + diferencaDias));
			}

			String dia = String.format("%02d", data.get(Calendar.DAY_OF_MONTH));
			String mes = String.format("%02d", data.get(Calendar.MONTH)+1); //+1 pois os meses no Calendar iniciam em 0
			String ano = String.format("%02d", data.get(Calendar.YEAR)); 
			String sequencial = String.format("%02d", remessaSequencial);

			nomeArquivoRemessa = cnpj + "_" + dia + mes + ano + "_" + sequencial + EXTENSAO_ARQUIVO_REMESSA;
//			if(nomeArquivoRemessa.length() != 25) //25 � a quantidade de caracteres que o nome do arquivo deve ter COM a sua extens�o, DEVE sempre ser assim para o Sicoob
//				throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_MONTANDO_NOME_ARQUIVO", "Tamanho do nome do arquivo (esperado 25) :" + nomeArquivoRemessa.length()));

		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_MONTANDO_NOME_ARQUIVO", e.getMessage()));
		} 

		/*
		 * O arquivo so � enviado ap�s todas as etapas conlu�das com sucesso,
		 * para evitar que o n�mero sequencial da remessa fosse alterado de maneira equivocada
		 */

		/*	
		 * Arquivo apenas para obtermos o diret�rio tempor�rio do sistema, pois o File.createTempFile adiciona 
		 * alguns caracteres de controle ao nome do arquivo, o que n�o nos interessa, pois o nome do arquivo deve 
		 * seguir exatamente o manual do Sicoob 
		 * 
		 */
		File tempFileDir = null;  
		File tempFile = null; 
		try {
			tempFileDir = File.createTempFile("remessa",null);

			tempFile = new File(tempFileDir.getParent(),nomeArquivoRemessa);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_CRIANDO_ARQUIVO", e1.getMessage()));		} 
		Remessa remessa = null;

		try {
			remessa = new Remessa(tempFile, arquivo);
			tempFile = remessa.getFile();

			//por fim, o n�mero sequencial da remessa � incrementado na entidade cedente, e a data da �ltima remessa � atualizada
			remessaSequencial++;
			convenioCobranca.setPropertyValue(Cedente.REMESSA_NUMERO_SEQUENCIAL, remessaSequencial);
			convenioCobranca.setPropertyValue(Cedente.REMESSA_ULTIMA_DATA, CalendarUtils.getCalendar());
			UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), convenioCobranca, serviceDataOwner);
		} catch (BusinessException e) {
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_ATUALIZANDO_CEDENTE", tempFileDir.getParent()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_CRIANDO_ARQUIVO", e.getMessage()));		
		}

		//Mostra quantos Titulos puderam ser inseridos no arquivo de remessa
		serviceDataOwner.getMessageList().add(BusinessMessage.TYPE_INFO, Gerenciador001.class, "REMESSA_QUANTIDADE_TITULOS", titulosInseridosOk, titulosTotal);

		/* 
		 * - verifica se tem mensagens de Ok
		 * - adiciona na primeira posi��o a mensagem cabe�alho de Ok 
		 */
		if (tituloOkList.size() > 0){
			serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador001.class, "REMESSA_CABECALHO_TITULOS_OK"));
			serviceDataOwner.getMessageList().addAll(tituloOkList);
			serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador001.class, "REMESSA_SUCESSO_TOTAL", DecimalUtils.formatBigDecimal(tituloOkValorTotal)));
		}

		/* 
		 * - verifica se tem mensagens de NoOk
		 * - adiciona na primeira posi��o a mensagem cabe�alho de NoOk 
		 */
		if (tituloNoOkList.size() > 0){
			serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador001.class, "REMESSA_CABECALHO_TITULOS_NO_OK"));
			serviceDataOwner.getMessageList().addAll(tituloNoOkList);
			serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador001.class, "REMESSA_FALHA_TOTAL", DecimalUtils.formatBigDecimal(tituloNoOkValorTotal)));
		}

		log.debug("Encerrando com sucesso o m�todo gerarRemessa()");
		return tempFile;

	}
	
	private List<DocumentoRetornoResultado> retornoCNAB240(Cedente cedente, InputStream dados, ServiceData serviceDataOwner) throws BusinessException{
		final Class<?>[] classes = {
				HeaderArquivo.class,
				HeaderLote.class,
				SegmentoT.class,
				SegmentoU.class,
				TrailerLote.class,
				TrailerArquivo.class
		};
		Retorno retorno;

		try {
			retorno = new Retorno(dados, classes);
		} catch (Exception e) {
			log.debug("Erro ao preparar os dados do retorno:");
			DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_ABRINDO_ARQUIVO", "(Nome do arquivo n�o dispon�vel)"));
			throw de;
		}

		Arquivo arquivo = retorno.getArquivo();

		/*
		 * Verifica o Header do arquivo.
		 */
		IRegistro headerArquivo = arquivo.getHeaderArquivo();
		if (!new Integer(headerArquivo.get(HeaderArquivo.AGENCIA_MANTENEDORA_DA_CONTA)).equals(new Integer(cedente.getContaBancaria().getAgenciaCodigo()))) {
			// TODO que erro dar� aqui
			throw new DocumentoCobrancaException(MessageList.createSingleInternalError(new Exception("A ag�ncia que veio no arquivo n�o � igual � cadastrada no cedente"))); 
		}
		if (!new Integer(headerArquivo.get(HeaderArquivo.NUMERO_DA_CONTA_CORRENTE)).equals(new Integer(cedente.getContaBancaria().getContaCodigo()))) {
			// TODO que erro dar� aqui
			throw new DocumentoCobrancaException(MessageList.createSingleInternalError(new Exception("A conta que veio no arquivo n�o � igual � conta cadastrada no cedente"))); 
		}

		List<DocumentoRetornoResultado> result = new ArrayList<DocumentoRetornoResultado>();
		/*
		 * Processa os Lotes.
		 */
		for (Lote lote : arquivo.getLotes()) {
			IRegistro segmentoT;
			IRegistro segmentoU;
			for (Segmento segmentos : lote.getSegmentos()) {
				segmentoT = segmentos.get(Segmento.SEGMENTO_T);
				segmentoU = segmentos.get(Segmento.SEGMENTO_U);
				if (segmentoT != null && segmentoU != null) {
					/* Guarda a data de ocorrencia que ser� muito utilizada daqui pra frente */
					Calendar dataOcorrencia = segmentoU.getAsCalendar(SegmentoU.DATA_DA_OCORRENCIA);

					/* Antes de come�ar a manipular o t�tulo prepara os dados b�sicos do objeto que ir� retorna o resultado.
					 * Assim, datas e valores j� ser�o preenchidos. O Status do t�tulo �
					 * ent�o preenchido por cada parte do c�digo que identifica este status.
					 * Outras propriedades deste resultado s�o preenchidas de forma
					 * espalha no c�digo, como o id e o valor pago que s� podem 
					 * ser preenchidos adequadamente quando for poss�vel obter as informa��es destes
					 * campos.
					 * Ele j� � adicionado na lista de resultados, e o resto do c�digo trabalha
					 * com refer�ncia 
					 */
					String nossoNumero = segmentoT.get(SegmentoT.IDENTIFICACAO_DO_TITULO_NO_BANCO);
					DocumentoRetornoResultado doc = new DocumentoRetornoResultado();
					doc.setId(IDAO.ENTITY_UNSAVED);
					doc.setDataVencimento(segmentoT.getAsCalendar(SegmentoT.DATA_DO_VENCIMENTO_DO_TITULO));
					doc.setDataOcorrencia(dataOcorrencia);
					doc.setNumeroDocumento(nossoNumero);
					result.add(doc);

					/* Verifica se o c�digo da acorrencia corresponde � um c�digo de LIQUIDA��O 
					 * para atualizar o t�tulo no financeiro */
					if (!(segmentoT.get(SegmentoT.CODIGO_DE_MOVIMENTO).equals(CodigoMovimentoRetorno.CODIGO06.getCodigo()) || segmentoT.get(SegmentoT.CODIGO_DE_MOVIMENTO).equals(CodigoMovimentoRetorno.CODIGO17.getCodigo()))) {
						doc.setStatus(DocumentoRetornoStatus.OCORRENCIA_INVALIDA);
						doc.setMensagem(CodigoMovimentoRetorno.valueOf("CODIGO"+ segmentoT.get(SegmentoT.CODIGO_DE_MOVIMENTO)).getDescricao());
					}else{
						/* Define o valor pago no objeto de resultado somente depois de ter
						 * verificado se o movimento foi de quita��o */
						doc.setValorPago(segmentoU.getAsBigDecimal(SegmentoU.VALOR_PAGO_PELO_SACADO));

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
						 * Se nenhum titulo for encontrado na pesquisa, adiciona o erro na lista e continua o programa;
						 */
						if (entityList.getSize() == 0){
							doc.setStatus(DocumentoRetornoStatus.NUMERO_DOCUMENTO_NAO_ENCONTRADO);
						}else
							if (entityList.getSize() > 1){
								doc.setStatus(DocumentoRetornoStatus.NUMERO_DOCUMENTO_DUPLICADO);
							}else{
								/* Prepara as vari�veis que ser�o utilizadas */
								IEntity<DocumentoTitulo> titulo = entityList.getFirst();
								DocumentoTitulo oTitulo = titulo.getObject();
								
								/* Define o id do T�tulo somente depois de ter verificado que ele consta no banco de dados */
								doc.setId(oTitulo.getId());
								doc.setValorDocumento(oTitulo.getValor());
								doc.setDataDocumento(oTitulo.getData());
								doc.setPessoa(oTitulo.getContrato().toString());

								int codOcorrenciaLiquidacao = ((Ocorrencia) UtilsOcorrencia.obterOcorrencia(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), Ocorrencia.RETORNO_LIQUIDADO.getCodigo(), serviceDataOwner).getObject()).getCodigo();
								int codOcorrenciaLiqSemRegistro = ((Ocorrencia) UtilsOcorrencia.obterOcorrencia(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), Ocorrencia.RETORNO_LIQUIDADO_SEM_REGISTRO.getCodigo(), serviceDataOwner).getObject()).getCodigo();
								int codUltOcorrencia = oTitulo.getUltimaOcorrencia() != null?oTitulo.getUltimaOcorrencia().getCodigo():-1;
								
								/* 
								 * Verificando se o T�tulo j� foi processado e cont�m o c�digo referente a RETORNO_LIQUIDADO ou RETORNO_LIQUIDADO_SEM_REGISTRO,  dessa forma 
								 * evitamos que o t�tulo seja baixado duas ou mais vezes, se o mesmo arquivo de retorno for processado diversas vezes
								 */
								if (codUltOcorrencia == codOcorrenciaLiquidacao || codUltOcorrencia == codOcorrenciaLiqSemRegistro){
									doc.setStatus(DocumentoRetornoStatus.DOCUMENTO_JA_LIQUIDADO);
									doc.setValorDocumento(titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal());
									doc.setMensagem(new BusinessMessage(BusinessMessage.TYPE_INFO, 
											                            GerenciadorBancoBasic.class, 
											                            "TITULO_JA_RETORNADO", 
											                            CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA).getValue().getAsCalendar()),
											                            DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal())
											                           ).getMessage());
									
								}else{ //se ainda n�o foi retornado, continua a opera��o
									@SuppressWarnings("unused")
									String propNameErro = null;
									@SuppressWarnings("unused")
									ICampo campoUErro = null;
									@SuppressWarnings("unused")
									String msgErro = null;
									

									log.debug("Setando as novas propriedades do titulo, conforme informa��es do arquivo de retorno");
									//Setando as novas propriedades da Guia
									try {
										try{
											//A data no arquivo de retorno do SICOOB � do tipo ddMMyy, diferente do padr�o ddMMyyyy
											titulo.setPropertyValue(DocumentoTitulo.DATA_CREDITO, segmentoU.getAsCalendar(SegmentoU.DATA_DA_EFETIVACAO_DO_CREDITO));
										} catch (BusinessException e) {
											propNameErro = DocumentoTitulo.DATA_CREDITO;
											campoUErro = SegmentoU.DATA_DA_EFETIVACAO_DO_CREDITO;
											msgErro = e.getMessage();
										}
										
										try{
											//A data no arquivo de retorno do SICOOB � do tipo ddMMyy, diferente do padr�o ddMMyyyy
											titulo.setPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA, segmentoU.getAsCalendar(SegmentoU.DATA_DA_OCORRENCIA));
										} catch (BusinessException e) {
											propNameErro = DocumentoTitulo.DATA_ULTIMA_OCORRENCIA;
											campoUErro = SegmentoU.DATA_DA_EFETIVACAO_DO_CREDITO;
											msgErro = e.getMessage();
										}
										
										try{
											titulo.setPropertyValue(DocumentoTitulo.VALOR_PAGO, segmentoU.getAsBigDecimal(SegmentoU.VALOR_PAGO_PELO_SACADO));
										} catch (BusinessException e) {
											propNameErro = DocumentoTitulo.VALOR_PAGO;
											campoUErro = SegmentoU.VALOR_PAGO_PELO_SACADO;
											msgErro = e.getMessage();
										}
										
										try{
											/* Subtitui o valor do titulo, pois o usuario pode ter pago um valor maioir no banco */
											IPropertyValue propValue = titulo.getProperty(DocumentoTitulo.VALOR).getValue();
											propValue.setAsBigDecimal(segmentoT.getAsBigDecimal(SegmentoT.VALOR_NOMINAL_DO_TITULO));
											if(propValue.isModified())
												doc.setMensagem("O valor do t�tulo foi modificado de R$ "+ DecimalUtils.formatBigDecimal((BigDecimal) propValue.getOldValue()) +"para R$" + DecimalUtils.formatBigDecimal(propValue.getAsBigDecimal()));
										} catch (BusinessException e) {
											propNameErro = DocumentoTitulo.VALOR;
											campoUErro = SegmentoT.VALOR_NOMINAL_DO_TITULO;
											msgErro = e.getMessage();
										}

										try{
											/* Verifica e define MULTA E JUROS.
											 * DATA_ULTIMA_OCORRENCIA j� deve estar DEFINIDA no t�tulo */
											super.definirMultasJuros(oTitulo, segmentoU.getAsCalendar(SegmentoU.DATA_DA_OCORRENCIA));
										} catch (BusinessException e) {
											propNameErro = DocumentoTitulo.VALOR_MULTA;
											campoUErro = SegmentoU.VALOR_DE_OUTROS_CREDITOS;
											msgErro = e.getMessage();
										}
										

										try{
											/* Atualiza a �ltima ocorr�ncia no t�tulo */
											IEntity<Ocorrencia> ultimaOcorrencia = UtilsOcorrencia.obterOcorrencia(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), Ocorrencia.RETORNO_LIQUIDADO.getCodigo(), serviceDataOwner);
											titulo.setPropertyValue(DocumentoTitulo.ULTIMA_OCORRENCIA, ultimaOcorrencia);

											/* Adicionando ocorr�ncia ao hist�rico de ocorr�ncias deste titulo */
											IEntity<OcorrenciaControle> ocorrencia = UtilsCrud.create(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), OcorrenciaControle.class, serviceDataOwner);
											ocorrencia.setPropertyValue(OcorrenciaControle.TITULO, titulo);
											ocorrencia.setPropertyValue(OcorrenciaControle.OCORRENCIA, ultimaOcorrencia);
											ocorrencia.setPropertyValue(OcorrenciaControle.DATA_OCORRENCIA, dataOcorrencia);
											UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), ocorrencia, serviceDataOwner);

											titulo.getProperty(DocumentoTitulo.OCORRENCIAS).getValue().<OcorrenciaControle>getAsEntitySet().add(ocorrencia);
										} catch (BusinessException e) {
											propNameErro = DocumentoTitulo.ULTIMA_OCORRENCIA;
											campoUErro = SegmentoU.CODIGO_DE_MOVIMENTO;
											msgErro = e.getMessage();
										}
										
										/* COMPOR A MENSAFEM DE ERRO  */
//										log.debug("ERRO ao gravar a entidade (titulo):" + e.getMessage());
//										DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador756.class, "ERRO_GRAVANDO", titulo.getObject().toString()));
//										de.getErrorList().addAll(e.getErrorList());
//										throw de;

										log.debug("Propriedades setadas com SUCESSO, atualizando titulo no sistema");

										/*
										 * Propriedades setadas com sucesso:
										 * - atualiza o titulo no sistema (update)
										 * - adiciona a mensagem de sucesso na lista
										 * - incrementa o contador de titulos que foram atualizados com sucesso
										 */
										UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), titulo, serviceDataOwner);
										
										/* Considerando que o titulo ja foi Lan�ado no Financeiro, agora ele deve ser Quitado */
										ServiceData sdQuitarGrupo = new ServiceData(QuitarLancamentoService.SERVICE_NAME, serviceDataOwner);
										sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_CONTA, oTitulo.getDocumentoCobrancaCategoria().getContaPadrao());
										sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_DATA, titulo.getPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA));
										sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_DOCUMENTO_PAGAMENTO_OPT, null /* TODO */);
										sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_LANCAMENTO, oTitulo.getLancamentos().get(0));
										sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_VALOR, oTitulo.getValor());
//										sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_SUBSTITUIR_VALOR_OPT, true);
										this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().execute(sdQuitarGrupo);

										log.debug("Servi�o QuitarLancamentoService executado");

										// Se nenhum erro ocorrer define as propriedades 
										doc.setStatus(DocumentoRetornoStatus.LIQUIDADO_COM_SUCESSO);

									} catch (BusinessException e) {
										log.debug("ERRO ao gravar a entidade (titulo):" + e.getMessage());
										doc.setStatus(DocumentoRetornoStatus.ERRO_ATUALIZANDO_DOCUMENTO);
										doc.setMensagem(e.getMessage());
									} 

								}

							}
					}
				}

			}
		}
		return result;
	}

	public List<DocumentoRetornoResultado> receberRetorno(IEntity<? extends ConvenioCobranca> convenioCobranca, InputStream dados, ServiceData serviceDataOwner) throws DocumentoCobrancaException{
		Cedente cedente = (Cedente) convenioCobranca.getObject();
		if (cedente.getLayoutCnab().equals(GerenciadorDocumentoTitulo.CNAB_240)){
			try {
				return retornoCNAB240(cedente, dados, serviceDataOwner);
			} catch (ServiceException e) {
				throw new DocumentoCobrancaException(e.getErrorList());
			} catch (BusinessException e) {
				throw new DocumentoCobrancaException(e.getErrorList());
			}
		}

		log.debug("encerrando m�todo receberRetorno()");
		return null;

//		if (cedente.getLayoutCnab().equals(GerenciadorDocumentoTitulo.CNAB_400)){
//
//			Retorno retorno;
//
//			try {
//				RandomAccessFile rf;
//
//				log.debug("Lendo o arquivo");
//				rf = new RandomAccessFile(dados, "r");
//				retorno = new Retorno(rf);
//
//				rf.close();
//
//			} catch (IOException e1) {
//				log.debug("Erro ao abrir o arquivo:" + dados.getAbsolutePath());
//				DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_ABRINDO_ARQUIVO", dados.getAbsoluteFile()));
//				throw de;
//			} catch (BusinessException e) {
//				log.debug("Erro ao ler o arquivo:" + dados.getAbsolutePath() + ":" + e.getMessage());
//				DocumentoCobrancaException de = new DocumentoCobrancaException(e.getErrorList());
//				de.getErrorList().add(Gerenciador001.class, "ERRO_INTERPRETANDO_ARQUIVO", dados.getAbsolutePath());
//				throw de;		
//			}
//
//			int titulosTotal = 0; //contador para o total de titulos encontradas no arquivo de retorno
//			int titulosAtualizados = 0; //contador para os titulos que puderam ser atualizadas no sistema
//			MessageList tituloOkList = new MessageList(); //possui mensagens informativas sobre os titulos que foram atualizados
//			MessageList tituloJaRetornadoList = new MessageList(); //possui mensagens informativas sobre os titulos que j� foram lidos e processados pelo arquivo, evitando que seja baixado mais de uma vez
//			MessageList tituloNoOkList = new MessageList(); //possui mensagens informativas sobre os titulos que N�O puderam ser atualizados
//			BigDecimal tituloOkValorTotal = new BigDecimal("0"); //Totalizador dos valores dos titulos OK
//			BigDecimal tituloJaRetornadoValorTotal = new BigDecimal("0"); //Totalizador dos valores dos titulos que j� tinham sido retornados anteriormente
//			BigDecimal tituloNoOkValorTotal = new BigDecimal("0"); //Totalizador dos valores dos titulos NOOK
//
//			log.debug("Percorrendo os registros do arquivo de retorno");
//			try{
//				/*
//				 * O procedimento � o seguinte: 
//				 * - percorre o n�mero registros do arquivo de retorno atrav�s do getDetalhes() 
//				 * - cada detalhe possui informa��es de um titulo 
//				 * - para cada detalhe, verifica o titulo no sistema e efetua a sua baixa
//				 */
//				for (IRegistro detalhes : retorno.getArquivo().getDetalhes()){
//
//					titulosTotal += 1;
//
//					log.debug("Obtendo informa��es do arquivo de retorno para fazer a pesquisa de titulos");    
//
//					String nossoNumero = detalhes.get(Detalhe.NOSSO_NUMERO);
//
//					log.debug("Pesquisando um titulo atraves do NOSSO_NUMERO");
//					List<QueryCondiction> condictions = new ArrayList<QueryCondiction>();
//					condictions.add(new QueryCondiction(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().getEntityManager(),
//							DocumentoTitulo.class,
//							DocumentoTitulo.NOSSO_NUMERO,
//							Operator.EQUAL,
//							nossoNumero,
//					""));
//
//					ServiceData sdQuery = new ServiceData(QueryService.SERVICE_NAME, serviceDataOwner);
//					sdQuery.getArgumentList().setProperty(QueryService.IN_ENTITY_TYPE, DocumentoTitulo.class);
//					sdQuery.getArgumentList().setProperty(QueryService.IN_QUERY_CONDICTIONS, condictions);
//					this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().execute(sdQuery);
//
//					log.debug("Obtendo o titulo encontrado na pesquisa");
//
//					IEntityList entityList = (IEntityList) sdQuery.getOutputData(QueryService.OUT_ENTITY_LIST);
//
//					/*
//					 * Se nenhum titulo for encontrado na pesquisa, adiciona o erro na lista e continua o programa;
//					 */
//					if (entityList.getSize() == 0){
//						tituloNoOkList.add(BusinessMessage.TYPE_INFO, 
//								Gerenciador001.class, 
//								"TITULO_NAO_ENCONTRADO", 
//								detalhes.get(Detalhe.NOSSO_NUMERO), 
//								CalendarUtils.formatDate(getData(detalhes.get(Detalhe.DATA_VENCIMENTO))), 
//								CalendarUtils.formatDate(getData(detalhes.get(Detalhe.DATA_OCORRENCIA_BANCO))), 
//								DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(Detalhe.VALOR_TITULO)), 
//								DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(Detalhe.VALOR_PAGO)));
//						tituloNoOkValorTotal = tituloNoOkValorTotal.add(detalhes.getAsBigDecimal(Detalhe.VALOR_PAGO));
//					}else if (entityList.getSize() > 1){
//						tituloNoOkList.add(BusinessMessage.TYPE_INFO, 
//								Gerenciador001.class, 
//								"TITULO_ID_DUPLICADO", 
//								detalhes.get(Detalhe.NOSSO_NUMERO), 
//								CalendarUtils.formatDate(getData(detalhes.get(Detalhe.DATA_VENCIMENTO))), 
//								detalhes.get(Detalhe.VALOR_TITULO), 
//								CalendarUtils.formatDate(getData(detalhes.get(Detalhe.DATA_OCORRENCIA_BANCO))), 
//								DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(Detalhe.VALOR_PAGO)));
//
//					}else{
//						IEntity titulo = entityList.getFirst();
//
//						IEntity ultOcorrencia = UtilsOcorrencia.obterOcorrencia(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), Ocorrencia.RETORNO_LIQUIDADO_SEM_REGISTRO.getCodigo(), serviceDataOwner);
//
//						/* 
//						 * Verificando se o T�tulo j� foi processado e cont�m o c�digo referente a RETORNO_LIQUIDADO_SEM_REGISTRO,  dessa forma 
//						 * evitamos que o t�tulo seja baixado duas ou mais vezes, se o mesmo arquivo de retorno for processado diversas vezes
//						 */
//						if (!titulo.getProperty(DocumentoTitulo.ULTIMA_OCORRENCIA).getValue().isValueNull() && titulo.getProperty(DocumentoTitulo.ULTIMA_OCORRENCIA).getValue().getAsEntity().getProperty(Ocorrencia.CODIGO).getValue().getAsInteger().equals(ultOcorrencia.getProperty(Ocorrencia.CODIGO).getValue().getAsInteger())){
//							tituloJaRetornadoValorTotal = tituloJaRetornadoValorTotal.add(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal());
//							tituloJaRetornadoList.add(BusinessMessage.TYPE_INFO, Gerenciador001.class, "TITULO_JA_RETORNADO", titulo.getProperty(DocumentoTitulo.CONTRATO_FINANCEIRO).getValue().getAsEntity().toString(), titulo.getProperty(DocumentoTitulo.NOSSO_NUMERO).getValue().getAsString(), CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_VENCIMENTO).getValue().getAsCalendar()), DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal()), CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA).getValue().getAsCalendar()), DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal()));
//
//						}else{ //se ainda n�o foi retornado, continua a opera��o
//							IEntity formaPagamento = (IEntity)titulo.getPropertyValue(DocumentoTitulo.DOCUMENTO_COBRANCA_CATEGORIA);
//							Long contaId = formaPagamento.getProperty(DocumentoCobrancaCategoria.CONTA_PADRAO).getValue().getId();
//
//							log.debug("Setando as novas propriedades do titulo, conforme informa��es do arquivo de retorno");
//							//Setando as novas propriedades da Guia
//							try {
//								try{
//									//A data no arquivo de retorno do SICOOB � do tipo ddMMyy, diferente do padr�o ddMMyyyy
//									titulo.setPropertyValue(DocumentoTitulo.DATA_CREDITO, getData(detalhes.get(Detalhe.DATA_CREDITO)));
//								} catch (BusinessException e) {
//									log.debug("ERRO ao atualizar 'Titulo'- " + titulo.getPropertyValue(DocumentoTitulo.DATA_CREDITO) + " -/- 'Arq Retorno'- " + detalhes.getAsCalendar(Detalhe.DATA_CREDITO));
//									throw new Exception("Titulo " + CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_CREDITO).getValue().getAsCalendar()) + "-/- Arq. Retorno " + getData(detalhes.get(Detalhe.DATA_CREDITO)));
//								}
//
//								try{
//									//A data no arquivo de retorno do SICOOB � do tipo ddMMyy, diferente do padr�o ddMMyyyy
//									titulo.setPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA, getData(detalhes.get(Detalhe.DATA_OCORRENCIA_BANCO)));
//								} catch (BusinessException e) {
//									log.debug("ERRO ao atualizar 'Titulo'- " + titulo.getPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA) + " -/- 'Arq Retorno'- " + detalhes.getAsCalendar(Detalhe.DATA_OCORRENCIA_BANCO));
//									throw new Exception("Titulo " + CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA).getValue().getAsCalendar()) + "-/- Arq. Retorno " + getData(detalhes.get(Detalhe.DATA_OCORRENCIA_BANCO)));
//								}
//
//								try{
//									titulo.setPropertyValue(DocumentoTitulo.VALOR_PAGO, detalhes.getAsBigDecimal(Detalhe.VALOR_PAGO));
//								} catch (BusinessException e) {
//									log.debug("ERRO ao atualizar 'Titulo'- " + titulo.getPropertyValue(DocumentoTitulo.VALOR_PAGO) + " -/- 'Arq Retorno'- " + detalhes.getAsBigDecimal(Detalhe.VALOR_PAGO));
//									throw new Exception("Titulo " + DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal()) + "-/- Arq. Retorno " + DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(Detalhe.VALOR_PAGO)));                		
//								}
//
//								try{
//									/* Subtitui o valor do titulo, pois o usuario pode ter pago um valor maioir no banco */ 
//									titulo.setPropertyValue(DocumentoTitulo.VALOR, detalhes.getAsBigDecimal(Detalhe.VALOR_TITULO));
//								} catch (BusinessException e) {
//									log.debug("ERRO ao atualizar 'Titulo'- " + titulo.getPropertyValue(DocumentoTitulo.VALOR) + " -/- 'Arq Retorno'- " + detalhes.getAsBigDecimal(Detalhe.VALOR_TITULO));
//									throw new Exception("Titulo " + DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal()) + "-/- Arq. Retorno " + DecimalUtils.formatBigDecimal(detalhes.getAsBigDecimal(Detalhe.VALOR_TITULO)));                		
//								}
//
//								try{
//									//A data no arquivo de retorno do SICOOB � do tipo ddMMyy, diferente do padr�o ddMMyyyy, logo � usada a fun��o privada "formatarData" ao inv�s do uso do CalendarUtils
//									Calendar dataOcorrenciaBanco = this.getData(detalhes.get(Detalhe.DATA_OCORRENCIA_BANCO));
//									Calendar dataVencimento = titulo.getProperty(DocumentoTitulo.DATA_VENCIMENTO).getValue().getAsCalendar();
//									/*
//									 * se a diferen�a entre a data de ocorr�ncia no banco e a data de vencimento for um 
//									 * long negativo ou 0, significa que o documento foi pago at� a sua data de vencimento, 
//									 * caso contr�rio, deve-se verificar os juros e multas (por causa do atraso)
//									 */ 
//									long diasAtraso = CalendarUtils.diffDay(dataOcorrenciaBanco, dataVencimento); 
//									if (diasAtraso > cedente.getDiasToleranciaMultaAtraso()){ 
//										setarMultasJuros(titulo, diasAtraso);	
//									}
//								} catch (BusinessException e) {
//									log.debug("ERRO ao atualizar 'Titulo'- Valores Extras");
//									throw new Exception("Titulo - Erro Valores Extras");
//								}
//
//								log.debug("Atualizando a ocorr�ncia do titulo no sistema - T�tulo Liquidado");
//								/* Antes de ser atualizado, o t�tulo recebe a ocorr�ncia correspondente ao retorno com sucesso (ver Ocorrencia.java) */
////								try{
////								if (detalhes.get(Detalhe.CODIGO_OCORRENCIA).equals(OCORRENCIA_LIQUIDACAO)){
////								IEntity ultimaOcorrencia = UtilsOcorrencia.obterOcorrencia(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), Ocorrencia.RETORNO_LIQUIDADO_SEM_REGISTRO.getCodigo(), serviceDataOwner);
////								titulo.setPropertyValue(DocumentoTitulo.ULTIMA_OCORRENCIA, ultimaOcorrencia);
////								titulo.setPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA, this.getData(detalhes.get(Detalhe.DATA_OCORRENCIA_BANCO)));
//
////								/* Adicionando ocorr�ncia ao hist�rico de ocorr�ncias deste titulo */
////								IEntity ocorrencia = UtilsCrud.create(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), OcorrenciaControle.class, serviceDataOwner);
////								ocorrencia.setPropertyValue(OcorrenciaControle.TITULO, titulo);
////								ocorrencia.setPropertyValue(OcorrenciaControle.OCORRENCIA, ultimaOcorrencia);
////								ocorrencia.setPropertyValue(OcorrenciaControle.DATA_OCORRENCIA, this.getData(detalhes.get(Detalhe.DATA_OCORRENCIA_BANCO)));
////								UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), ocorrencia, serviceDataOwner);
//
////								titulo.getProperty(DocumentoTitulo.OCORRENCIAS).getValue().getAsEntitySet().add(ocorrencia);
////								}
////								else
////								throw new Exception("O Titulo com id=" + titulo.getId() + " est� com ocorr�ncia no arquivo de retorno: " + detalhes.get(Detalhe.CODIGO_OCORRENCIA) + ". - O c�digo de ocorr�ncia esperado �: " + OCORRENCIA_LIQUIDACAO + " (Liquida��o).");
////								} catch (BusinessException e) {
////								log.debug("ERRO ao atualizar 'Titulo'- " + titulo.getPropertyValue(DocumentoTitulo.ULTIMA_OCORRENCIA) + " -/- 'Arq Retorno'- " + detalhes.get(Detalhe.CODIGO_OCORRENCIA));
////								throw new Exception("Titulo " + titulo.getProperty(DocumentoTitulo.ULTIMA_OCORRENCIA) + "-/- Arq. Retorno " + detalhes.get(Detalhe.CODIGO_OCORRENCIA));
////								}
//
//								log.debug("Propriedades setadas com SUCESSO, atualizando titulo no sistema");
//								/*
//								 * Propriedades setadas com sucesso:
//								 * - atualiza o titulo no sistema (update)
//								 * - adiciona a mensagem de sucesso na lista
//								 * - incrementa o contador de titulos que foram atualizados com sucesso
//								 */
//								UtilsCrud.update(this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager(), titulo, serviceDataOwner);
//								tituloOkList.add(BusinessMessage.TYPE_INFO, Gerenciador001.class, "RETORNO_SUCESSO", titulo.getProperty(DocumentoTitulo.CONTRATO_FINANCEIRO).getValue().getAsEntity().toString(), titulo.getProperty(DocumentoTitulo.NOSSO_NUMERO).getValue().getAsString(), CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_VENCIMENTO).getValue().getAsCalendar()), DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal()), CalendarUtils.formatDate(titulo.getProperty(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA).getValue().getAsCalendar()), DecimalUtils.formatBigDecimal(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal()));
//								titulosAtualizados += 1;
//								tituloOkValorTotal = tituloOkValorTotal.add(titulo.getProperty(DocumentoTitulo.VALOR_PAGO).getValue().getAsBigDecimal());
//
//								/* Quita o documento com o valor do documento recebido do banco, com a op��o de sobrepor o valor antigo do titulo */
//								IEntity grupo = titulo.getProperty(DocumentoTitulo.LANCAMENTOS).getValue().getAsEntityCollection().getFirst();
//								long grupoId = grupo.getId();
//
//
//								/* Considerando que o titulo ja foi Lan�ado no Financeiro, agora ele deve ser Quitado */
//								ServiceData sdQuitarGrupo = new ServiceData(QuitarLancamentoService.SERVICE_NAME, serviceDataOwner);
//								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_CONTA, contaId);
//								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_DATA, titulo.getPropertyValue(DocumentoTitulo.DATA_ULTIMA_OCORRENCIA));
//								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_DOCUMENTO_PAGAMENTO_OPT, titulo);
//								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_LANCAMENTO, grupoId);
//								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_VALOR, titulo.getProperty(DocumentoTitulo.VALOR).getValue().getAsBigDecimal());
////								sdQuitarGrupo.getArgumentList().setProperty(QuitarLancamentoService.IN_SUBSTITUIR_VALOR_OPT, true);
//								this.getProvedorBanco().getProvedorDocumentoCobranca().getServiceManager().execute(sdQuitarGrupo);
//
//								log.debug("Servi�o QuitarLancamentoService executado");
//
//							} catch (BusinessException e) {
//								log.debug("ERRO ao gravar a entidade (titulo):" + e.getMessage());
//								DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_GRAVANDO", titulo.getObject().toString()));
//								de.getErrorList().addAll(e.getErrorList());
//								throw de;
//							} 
//							catch (Exception e) {
//								e.printStackTrace();
//								log.debug("ERRO ao atualizar valores do titulo:" + e.getMessage());
//								DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_SETANDO", titulo.getObject().toString(), e.getMessage()));
//								throw de;
//							}
//						}
//					}
//				}
//
//				//Mostra quantos Titulos existem no Arquivo de Retorno e quantos Titulos puderam ser Atualizados no sistema
//				serviceDataOwner.getMessageList().add(BusinessMessage.TYPE_INFO, Gerenciador001.class, "QUANTIDADE_TITULOS_PROCESSADOS", titulosAtualizados, titulosTotal);
//
//				/* 
//				 * - verifica se tem mensagens de Ok
//				 * - adiciona na primeira posi��o a mensagem cabe�alho de Ok 
//				 */
//				if (tituloOkList.size() > 0){
//					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador001.class, "CABECALHO_TITULOS_OK"));
//					serviceDataOwner.getMessageList().addAll(tituloOkList);
//					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador001.class, "RETORNO_SUCESSO_TOTAL", DecimalUtils.formatBigDecimal(tituloOkValorTotal)));
//				}
//
//				if (tituloJaRetornadoList.size() > 0){
//					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador001.class, "CABECALHO_TITULOS_JA_RETORNADOS"));
//					serviceDataOwner.getMessageList().addAll(tituloJaRetornadoList);
//					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador001.class, "RETORNO_JA_RETORNADOS_TOTAL", DecimalUtils.formatBigDecimal(tituloJaRetornadoValorTotal)));
//				}
//
//				/* 
//				 * - verifica se tem mensagens de NoOk
//				 * - adiciona na primeira posi��o a mensagem cabe�alho de NoOk 
//				 */
//				if (tituloNoOkList.size() > 0){
//					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador001.class, "CABECALHO_TITULOS_NO_OK"));
//					serviceDataOwner.getMessageList().addAll(tituloNoOkList);
//					serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador001.class, "RETORNO_FALHA_TOTAL", DecimalUtils.formatBigDecimal(tituloNoOkValorTotal)));
//					serviceDataOwner.getMessageList().add(BusinessMessage.TYPE_INFO, Gerenciador001.class, "INSTRUCAO_TITULOS_NO_OK");
//				}
//
//				serviceDataOwner.getMessageList().add(new BusinessMessage(BusinessMessage.TYPE_INFO, Gerenciador001.class, "RETORNO_TOTAL", DecimalUtils.formatBigDecimal(tituloNoOkValorTotal.add(tituloOkValorTotal).add(tituloJaRetornadoValorTotal))));
//
//			}catch(BusinessException e){
//				//se o arquivo n�o p�de ser interpretado, ex: se n�o for do tipo cnab240
//				log.debug("ERRO ao interpretar o arquivo");
//				DocumentoCobrancaException de = new DocumentoCobrancaException(MessageList.create(Gerenciador001.class, "ERRO_INTERPRETANDO_ARQUIVO", dados.getAbsolutePath()));
//				de.getErrorList().addAll(e.getErrorList());
//				throw de;
//			}finally{
//				//fecha os arquivos salvos temporariamente
//				try{
//					//TODO VERIFICAR SE O ARQUIVO PERMANECE NO DIRETORIO QUE O USUARIO PASSOU, QUE NAO DEVE SER APAGADO! OU AINDA SE � CRIADO UM ARQUIVO TEMP, E SE FOR, ESTE SIM DEVE SER APAGADO!  
////					if (tempRetorno != null)
////					tempRetorno.delete();
//				}catch (Exception e) {
//					log.debug("ERRO ao tentar fechar os arquivos tempor�rios");
//					e.printStackTrace();
//				}
//			}
//		}// fim do if para CNAB400

	}

	/**
	 * O arquivo de remessa do BB deve gravar a data no formato "ddMMyyyy"
	 * Esta fun��o converte para uma String no formato correto
	 */
	private String formatarData(Calendar data){
		return String.format("%02d",data.get(Calendar.DAY_OF_MONTH)) + String.format("%02d",data.get(Calendar.MONTH)+1) + String.format("%04d",data.get(Calendar.YEAR));
	}

	/** 
	 * <p>Esta fun��o faz uso do isEmpty (StringUtils - jakarta), que verifica se a String � vazia ("") ou null;
	 * caso ela seja null, esta fun��o retorna vazio ("") para evitar problemas de NullPointer, e caso n�o seja
	 * null ou vazia, retorna a pr�pria String passada por par�metro, sem modifica��es.
	 * 
	 * @param str - String a ser verificada 
	 * @return  String vazia ou a pr�pria String passada por par�metro
	 */
	private String verificarNull(String str){
		return (org.apache.commons.lang.StringUtils.isEmpty(str)?"":str);
	}

	private void setRegistro(IRegistro registro, ICampo campo, String nomePropriedade, String valor, boolean checkEmpty) throws BusinessException{
		try{
			registro.set(campo, valor, checkEmpty);
		} catch (PropriedadeVaziaException e) {
			throw new BusinessException(MessageList.create(Gerenciador001.class, "ERRO_SETANDO_REMESSA", nomePropriedade, valor));
		}
	}

	private void setRegistro(IRegistro registro, ICampo campo,  String nomePropriedade, BigDecimal valor, boolean checkEmpty) throws BusinessException{
		try{
			registro.setAsDecimal(campo, valor, checkEmpty);
		} catch (PropriedadeVaziaException e) {
			throw new BusinessException(MessageList.create(Gerenciador001.class, "ERRO_SETANDO_REMESSA", nomePropriedade, valor));
		}	
	}

}


