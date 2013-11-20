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
 * <p>Este classe abstrata implementa as funcionalidades b�sicas de qualquer
 * gerenciador de documento.</p>
 * <p>Este gerenciador est� registrado em um Provedor de Documentos.</p>
 * <p>Um documento pode ter seus campos preenchidos automaticamente ou manualmente .</p>
 * <p>Esta classe b�sica implementa o m�todo imprimirDocumento criando uma lista e chamando o
 * m�todo imprimirDocumentos do gerenciador implementado.
 * <p>Cada gerenciador de documento implementa individualmente um m�todo
 *  		- criarDocumento
 *  		- imprimirDocumentos
 *  que est� na Interface IGerenciadorBanco</p>
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
	 * Verifica se o valor pago no banco � o mesmo valor do t�tulo; Caso seja
	 * diferente, verifica as condi��es para consumir corretamente esta diferen�a: 
	 * - se n�o estiver atrasado vai direto para o lan�amento da diferen�a como descontos ou acr�scimos
	 * - se estiver atrasado, vai tentar consumir a diferen�a lan�ando multa e juros primeiro
	 *    -se o t�tulo possui valor R$ 0,00, presume um valor com base no valor pago e nas taxas de multa e juros que devem ter sido agregadas
	 *    -se a diferen�a for menor que o valor da multa, lan�a essa diferen�a apenas como multa e diferen�a<-0.00
	 *       sen�o, consome a multa da diferen�a e vai para an�lise de juros 
	 * 	  -se a diferen�a for menor que o valor dos juros, lan�a essa diferen�a como juros e diferen�a<-0.00
	 *       sen�o, consome os juros da diferen�a 
	 * - se ainda tiver algo na diferen�a, lan�a como descontos ou acr�scimos, de acordo com o sinal da diferen�a
	 * 
	 * Valor de multaAtraso Cedente = 10% uma �nica vez Valor de jurosMora
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
			
			log.debug("dias entre a data de vencimento e a data de cr�dito (dias de atraso) = " + diasAtraso);
	
			log.debug("obtendo valor do titulo e o valor pago");
			BigDecimal valorTitulo = oTitulo.getValor();
			BigDecimal valorPago = oTitulo.getValorPago();
			
			BigDecimal diferenca = valorPago.subtract(valorTitulo);
			log.debug("obtendo a diferen�a entre o valor pago e o valor do titulo = " + diferenca);
	
			/* Verifica os dias de atraso.*/
			if ((diasAtraso > 0) && (diferenca.signum() > 0)) { // Pago atrasado e a diferen�a � POSITIVA, sen�o vai tudo pra desconto
				/* Est� atrasado o t�tulo */ 
				BigDecimal multaAtraso = oTitulo.getDocumentoCobrancaCategoria().getMultaAtraso().divide(new BigDecimal(100)); // divide o valor obtido pelo banco
				// por 100 para obter a porcentagem
				log.debug("obtendo % multa por atraso = " + multaAtraso);
	
				// mora / 100
				BigDecimal jurosMora = oTitulo.getDocumentoCobrancaCategoria().getJurosMora().divide(new BigDecimal(100));
				// moraDiaria / 30
				BigDecimal moraDiaria = jurosMora.divide(new BigDecimal(30), 5,BigDecimal.ROUND_HALF_UP);
				// obt�m a mora di�ria a partir da quantidade de dias atrasados,
				// obtido atrav�s da diferen�a entre a data do cr�dito no banco e a
				// data de vencimento
				BigDecimal moraTotal = moraDiaria.multiply(new BigDecimal(diasAtraso));
				log.debug("obtendo a mora calculada por quantidade de dias de atraso = " + moraTotal);
	
				if (DecimalUtils.isZero(valorTitulo)) {
					/* Express�o para presumir o valor original baseando no valor pago e os
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
					log.debug("Presumindo valor de t�tulo baseado no valor pago, multa e juros devidos: " + valorPresumido);
					valorTitulo = valorPresumido; // Define o valor do t�tulo para futuro c�lculo e lan�amento de multa e juros logo abaixo 
				}
				
				/* Continua daqui pra baixo com o valor original do titulo ou o valorPresumido pelo c�digo acima */
				BigDecimal multa = valorTitulo.multiply(multaAtraso).setScale(2, BigDecimal.ROUND_HALF_UP);
				log.debug("calculando o valor da multa total = " + multa);
	
				/*	Se a multa � maior que a diferen�a entre valor pago e valor do titulo,
				 *  a diferen�a inteira � lan�ada como multa
				 */
				if (diferenca.subtract(multa).signum() < 0) { // se a
					log.debug("setando valor da multa quando a diferen�a � suficiente apenas para esta");
					oTitulo.setValorMulta(diferenca);
	
					diferenca = BigDecimal.ZERO; // consome a multa da diferen�a
				} else {
					log.debug("setando o valor de multa e mora (quando as duas devem ser atualizadas)");
					oTitulo.setValorMulta(multa);
	
					diferenca = diferenca.subtract(multa); // consome a multa da diferen�a
				}
	
				BigDecimal juros = moraTotal.multiply(valorTitulo).setScale(2, BigDecimal.ROUND_HALF_UP);
				log.debug("calculando o valor de juros total = " + juros);
	
				/*	Se os juros � maior que a diferen�a entre valor pago e valor do titulo,
				 *  a diferen�a inteira � lan�ada como juros
				 */
				if (diferenca.subtract(juros).signum() < 0) { // se a
					log.debug("setando valor dos juros quando a diferen�a � suficiente apenas para esta");
					oTitulo.setValorJuros(diferenca);
	
					diferenca = BigDecimal.ZERO; // consome a diferen�a
				} else {
					log.debug("setando o valor de multa e mora (quando as duas devem ser atualizadas)");
					oTitulo.setValorJuros(juros);
	
					diferenca = diferenca.subtract(juros); // consome a diferen�a
				}
			}
			
			/* Se ainda tiver diferen�a para ser consumida, lan�a tudo em ACRESCIMOS ou DESCONTOS */
			if (diferenca.signum() < 0) { // diferen�a entre valor pago e
				// valor do titulo for negativa, lan�a desconto
				log.debug("setando o desconto quando a diferen�a entre valor pago e valor do titulo � negativa");
				oTitulo.setValorDesconto(diferenca.abs());
			} if (diferenca.signum() > 0) { // diferen�a maior ou igual a zero
				// lan�a Acrescimo
				log.debug("setando o acr�scimo quando a diferen�a entre valor pago e valor do titulo � positiva e pago em dia");
				oTitulo.setValorAcrescimo(diferenca);
			}
			log.debug("finalizando o m�todo setarValoresExtras");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Este m�todo decide se no c�digo de barras vai o valor original do t�tulo ou o valor pago calculado.
	 * Isto facilita na hora do usu�rio pagar pela internet.
	 * @param documento
	 * @return
	 */
	protected BigDecimal getValorParaCodigoBarras(DocumentoTitulo oTitulo){
		/* Lucio 20120511: Verifica se colocar� no c�digo de barras o valor ORIGINAL do t�tulo
		 * ou o VALOR PAGO se o t�tulo j� veio com Calculado com MULTA e JUROS */
		if(oTitulo.getValorPago() == null )
			return oTitulo.getValor();
		else
			return oTitulo.getValorPago();
	}
}
