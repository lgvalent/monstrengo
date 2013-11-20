package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Calendar;

import org.apache.log4j.Logger;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;


/**
 * <p>Este classe abstrata implementa as funcionalidades básicas de qualquer
 * gerenciador de documento.</p>
 * <p>Este gerenciador está registrado em um Provedor de Documentos.</p>
 * <p>Um documento pode ter seus campos preenchidos automaticamente ou manualmente .</p>
 * <p>Esta classe básica implementa o método imprimirDocumento criando uma lista e chamando o
 * método imprimirDocumentos do gerenciador implementado.
 * <p>Cada gerenciador de documento implementa individualmente um método
 *  		- criarDocumento
 *  		- imprimirDocumentos
 *  que está na Interface IGerenciadorBanco</p>
 * @author Lucio
 *
 */
public abstract class GerenciadorBancoBasic implements IGerenciadorBanco
{
	private IProvedorBanco provedorBanco;

	protected final Logger log = Logger.getLogger(this.getClass());

	public IProvedorBanco getProvedorBanco() {return this.provedorBanco;}
	public void setProvedorBanco(IProvedorBanco provedor) {this.provedorBanco = provedor;}

	public void registrarGerenciador() {
		this.provedorBanco.registrarGerenciador(this);
	}

	/**
	 * Verifica se o valor pago no banco é o mesmo valor do título; Caso seja
	 * diferente, verifica as condições para consumir corretamente esta diferença: 
	 * - se não estiver atrasado vai direto para o lançamento da diferença como descontos ou acréscimos
	 * - se estiver atrasado, vai tentar consumir a diferença lançando multa e juros primeiro
	 *    -se o título possui valor R$ 0,00, presume um valor com base no valor pago e nas taxas de multa e juros que devem ter sido agregadas
	 *    -se a diferença for menor que o valor da multa, lança essa diferença apenas como multa e diferença<-0.00
	 *       senão, consome a multa da diferença e vai para análise de juros 
	 * 	  -se a diferença for menor que o valor dos juros, lança essa diferença como juros e diferença<-0.00
	 *       senão, consome os juros da diferença 
	 * - se ainda tiver algo na diferença, lança como descontos ou acréscimos, de acordo com o sinal da diferença
	 * 
	 * Valor de multaAtraso Cedente = 10% uma única vez Valor de jurosMora
	 * Cedente = 1% por dia de atraso
	 * 
	 * @version 20121217 Lucio
	 * @param titulo
	 * @throws EntityException
	 * @throws PropertyValueException
	 */
	protected void definirMultasJuros(DocumentoTitulo oTitulo, Calendar dataOcorrencia) throws BusinessException {
		try {
			log.debug("Tentando setar as propriedades Extras do titulo");
			long diasAtraso = CalendarUtils.diffDay(dataOcorrencia, oTitulo.getDataVencimento());
			diasAtraso -= oTitulo.getDocumentoCobrancaCategoria().getDiasToleranciaMultaAtraso();
			
			log.debug("dias entre a data de vencimento e a data de crédito (dias de atraso) = " + diasAtraso);
	
			log.debug("obtendo valor do titulo e o valor pago");
			BigDecimal valorTitulo = oTitulo.getValor();
			BigDecimal valorPago = oTitulo.getValorPago();
			
			BigDecimal diferenca = valorPago.subtract(valorTitulo);
			log.debug("obtendo a diferença entre o valor pago e o valor do titulo = " + diferenca);
	
			/* Verifica os dias de atraso.*/
			if ((diasAtraso > 0) && (diferenca.signum() > 0)) { // Pago atrasado e a diferença é POSITIVA, senão vai tudo pra desconto
				/* Está atrasado o título */ 
				BigDecimal multaAtraso = oTitulo.getDocumentoCobrancaCategoria().getMultaAtraso().divide(new BigDecimal(100)); // divide o valor obtido pelo banco
				// por 100 para obter a porcentagem
				log.debug("obtendo % multa por atraso = " + multaAtraso);
	
				// mora / 100
				BigDecimal jurosMora = oTitulo.getDocumentoCobrancaCategoria().getJurosMora().divide(new BigDecimal(100));
				// moraDiaria / 30
				BigDecimal moraDiaria = jurosMora.divide(new BigDecimal(30), 5,BigDecimal.ROUND_HALF_UP);
				// obtém a mora diária a partir da quantidade de dias atrasados,
				// obtido através da diferença entre a data do crédito no banco e a
				// data de vencimento
				BigDecimal moraTotal = moraDiaria.multiply(new BigDecimal(diasAtraso));
				log.debug("obtendo a mora calculada por quantidade de dias de atraso = " + moraTotal);
	
				if (DecimalUtils.isZero(valorTitulo)) {
					/* Expressão para presumir o valor original baseando no valor pago e os
					 * percentuais de multa e juros que deveriam ter sido aplicados.
					 * x->valor pago e y->valor original
					 * x = y + y*mu + y*ju  (/y)
					 * x/y = y/y + y*mu/y + y*ju/y -> x/y = 1 + mu + ju (/x)
					 * 1/y = 1/x + mu/x + ju/x  (*y)
					 * y*(1/y) = y*(1/x + mu/x + ju/x) -> 1 = y*(1/x + mu/x + ju/x)
					 * y = 1/(1/x + mu/x + ju/x)
					 */
					 BigDecimal divisor =  BigDecimal.ONE.divide(valorPago, MathContext.DECIMAL32).add(multaAtraso.divide(valorPago, MathContext.DECIMAL32)).add(moraTotal.divide(valorPago, MathContext.DECIMAL32));
			         BigDecimal valorPresumido = BigDecimal.ONE.divide(divisor, MathContext.DECIMAL32).setScale(2, RoundingMode.FLOOR);
					log.debug("Presumindo valor de título baseado no valor pago, multa e juros devidos: " + valorPresumido);
					valorTitulo = valorPresumido; // Define o valor do título para futuro cálculo e lançamento de multa e juros logo abaixo 
				}
				
				/* Continua daqui pra baixo com o valor original do titulo ou o valorPresumido pelo código acima */
				BigDecimal multa = valorTitulo.multiply(multaAtraso).setScale(2, BigDecimal.ROUND_HALF_UP);
				log.debug("calculando o valor da multa total = " + multa);
	
				/*	Se a multa é maior que a diferença entre valor pago e valor do titulo,
				 *  a diferença inteira é lançada como multa
				 */
				if (diferenca.subtract(multa).signum() < 0) { // se a
					log.debug("setando valor da multa quando a diferença é suficiente apenas para esta");
					oTitulo.setValorMulta(diferenca);
	
					diferenca = BigDecimal.ZERO; // consome a multa da diferença
				} else {
					log.debug("setando o valor de multa e mora (quando as duas devem ser atualizadas)");
					oTitulo.setValorMulta(multa);
	
					diferenca = diferenca.subtract(multa); // consome a multa da diferença
				}
	
				BigDecimal juros = moraTotal.multiply(valorTitulo).setScale(2, BigDecimal.ROUND_HALF_UP);
				log.debug("calculando o valor de juros total = " + juros);
	
				/*	Se os juros é maior que a diferença entre valor pago e valor do titulo,
				 *  a diferença inteira é lançada como juros
				 */
				if (diferenca.subtract(juros).signum() < 0) { // se a
					log.debug("setando valor dos juros quando a diferença é suficiente apenas para esta");
					oTitulo.setValorJuros(diferenca);
	
					diferenca = BigDecimal.ZERO; // consome a diferença
				} else {
					log.debug("setando o valor de multa e mora (quando as duas devem ser atualizadas)");
					oTitulo.setValorJuros(juros);
	
					diferenca = diferenca.subtract(juros); // consome a diferença
				}
			}
			
			/* Se ainda tiver diferença para ser consumida, lança tudo em ACRESCIMOS ou DESCONTOS */
			if (diferenca.signum() < 0) { // diferença entre valor pago e
				// valor do titulo for negativa, lança desconto
				log.debug("setando o desconto quando a diferença entre valor pago e valor do titulo é negativa");
				oTitulo.setValorDesconto(diferenca.abs());
			} if (diferenca.signum() > 0) { // diferença maior ou igual a zero
				// lança Acrescimo
				log.debug("setando o acréscimo quando a diferença entre valor pago e valor do titulo é positiva e pago em dia");
				oTitulo.setValorAcrescimo(diferenca);
			}
			log.debug("finalizando o método setarValoresExtras");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Este método decide se no código de barras vai o valor original do título ou o valor pago calculado.
	 * Isto facilita na hora do usuário pagar pela internet.
	 * @param documento
	 * @return
	 */
	protected BigDecimal getValorParaCodigoBarras(DocumentoTitulo oTitulo){
		/* Lucio 20120511: Verifica se colocará no código de barras o valor ORIGINAL do título
		 * ou o VALOR PAGO se o título já veio com Calculado com MULTA e JUROS */
		if(oTitulo.getValorPago() == null )
			return oTitulo.getValor();
		else
			return oTitulo.getValorPago();
	}
}
