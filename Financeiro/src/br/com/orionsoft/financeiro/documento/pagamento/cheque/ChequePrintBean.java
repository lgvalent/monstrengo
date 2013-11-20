package br.com.orionsoft.financeiro.documento.pagamento.cheque;

import java.util.Calendar;

import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.utils.Extenso;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Classe que faz a ligação com o layout de Cheque do iReport<br>
 * 
 * @author Andre
 * 
 */
public class ChequePrintBean {
	
    public ChequePrintBean(IEntity<DocumentoCheque> cheque)  {
        DocumentoCheque oCheque = cheque.getObject();
    	
        if(oCheque.getBanco()!=null)
        	codigoBanco = oCheque.getBanco().getCodigo();

        numeroCheque = "Controle:" + oCheque.getNumeroDocumento();

        valorDecimal = "##("+DecimalUtils.formatBigDecimal(oCheque.getValor())+")##";
        valorExtenso = "("+Extenso.getExtenso(oCheque.getValor())+")---";
        
        /* Verifica se o cheque é: cobrança, neste caso o favorecido é o sindicato; ou pagamento, neste caso o favorecido é a pessoa vinculada ao contrato */
        if (oCheque.getTransacao() == Transacao.DEBITO)
        	nomeFavorecido = oCheque.getContrato().getPessoa().getNome();
        else
        	nomeFavorecido = oCheque.getConvenioPagamento().getContratante().getNome();
        
        //formato da String de cidadeEstado = Maringa-PR
        cidadeEstado = oCheque.getConvenioPagamento().getContratante().getEnderecoCorrespondencia().getMunicipio().getNome() + "-" 
        			   + oCheque.getConvenioPagamento().getContratante().getEnderecoCorrespondencia().getMunicipio().getUf();
        
        /* O cheque é impresso com a data do vencimento do mesmo */
        /* 20090331 Lucio - Vedovati - A data tem que ser a de criaçao */
        Calendar dataVencimento = oCheque.getDataImpressao();
        
        dia = String.valueOf(dataVencimento.get(Calendar.DAY_OF_MONTH));
        mes = String.format("%1$tB", dataVencimento.getTime());
        ano = String.valueOf(dataVencimento.get(Calendar.YEAR));
        
    }

	public ChequePrintBean() {}

	/* DADOS DO CHEQUE */
    private String numeroCheque;
    private String codigoBanco;
    private String valorExtenso;
    private String valorDecimal;
    private String nomeFavorecido;
    private String cidadeEstado;
    private String dia;
    private String mes;
    private String ano;
    
	public String getNumeroCheque() {return numeroCheque;}
	public void setNumeroCheque(String numeroCheque) {this.numeroCheque = numeroCheque;}
    
	public String getCodigoBanco() {return codigoBanco;}
	public void setCodigoBanco(String codigoBanco) {this.codigoBanco = codigoBanco;}
    
    public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public String getMes() {
		return mes;
	}

	public void setMes(String mes) {
		this.mes = mes;
	}

	public String getNomeFavorecido() {
		return nomeFavorecido;
	}

	public void setNomeFavorecido(String nomeFavorecido) {
		this.nomeFavorecido = nomeFavorecido;
	}

	public String getValorDecimal() {
		return valorDecimal;
	}

	public void setValorDecimal(String valorDecimal) {
		this.valorDecimal = valorDecimal;
	}

	public String getValorExtenso() {
		return valorExtenso;
	}

	public void setValorExtenso(String valorExtenso) {
		this.valorExtenso = valorExtenso;
	}

	public String getCidadeEstado() {
		return cidadeEstado;
	}

	public void setCidadeEstado(String cidadeEstado) {
		this.cidadeEstado = cidadeEstado;
	}

	public static void main(String[] args)
	{
        System.out.println(String.format("Controle: %6s","123"));

	}
}