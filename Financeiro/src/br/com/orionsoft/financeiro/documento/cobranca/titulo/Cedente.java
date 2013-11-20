package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;

/**
 * @author marcia
 */
@Entity
@DiscriminatorValue("CED")
public class Cedente extends ConvenioCobranca{
    
    public static final String CEDENTE_CODIGO = "cedenteCodigo";
    public static final String CEDENTE_DIGITO = "cedenteDigito";
    
    public static final String CARTEIRA_CODIGO = "carteiraCodigo";
    public static final String LOCAL_PAGAMENTO = "localPagamento";
    public static final String ACEITE = "aceite";
    
    /** Um único centro de custo para todas as taxas. Assim, economiza relacionamento e joins do hibernate */
    public static final String CENTRO_CUSTO_GERAL = "centroCustoGeral";
    
    public static final String ITEM_CUSTO_IOF = "itemCustoIof";
    public static final String ITEM_CUSTO_TARIFA = "itemCustoTarifa";
    public static final String VALOR_TARIFA = "valorTarifa";
    public static final String BANCO_GERA_OCORRENCIA_VALOR_TARIFA = "bancoGeraOcorrenciaValorTarifa";

    public static final String CANCELADO = "cancelado";
    public static final String CANCELADO_DATA = "canceladoData"; 
    
    public static final String LAYOUT_CNAB = "layoutCnab"; //define se o arquivo de retorno/remessa é cnab240 ou cnab400 ou outros
    public static final String NOSSO_NUMERO_ANO = "nossoNumeroAno";
    
    private String cedenteCodigo;
    private String cedenteDigito;
    
    private String carteiraCodigo;
    private String carteiraVariacao;
    
    private String localPagamento;
    
    private boolean aceite;
    
    private CentroCusto centroCustoGeral;    
    private ItemCusto itemCustoIof;
    private ItemCusto itemCustoTarifa;
    private BigDecimal valorTarifa;
    private boolean bancoGeraOcorrenciaValorTarifa;

    private boolean cancelado;
    private Calendar canceladoData;
    
    private String layoutCnab;
    private int nossoNumeroAno = 0; //não pode ser null para evitar erros na formatação do NossoNumero
    
    @Column
    public boolean isAceite() {
        return aceite;
    }

    public void setAceite(boolean aceite) {
        this.aceite = aceite;
    }

    @Column(length=2)
    public String getCarteiraCodigo() {
        return carteiraCodigo;
    }

    public void setCarteiraCodigo(String carteiraCodigo) {
        this.carteiraCodigo = carteiraCodigo;
    }

    @Column(length=3)
    public String getCarteiraVariacao() {
		return carteiraVariacao;
	}

	public void setCarteiraVariacao(String carteiraVariacao) {
		this.carteiraVariacao = carteiraVariacao;
	}

	@Column(length=7)
    public String getCedenteCodigo() {
        return cedenteCodigo;
    }

    public void setCedenteCodigo(String cedenteCodigo) {
        this.cedenteCodigo = cedenteCodigo;
    }

    @Column(length=1)
    public String getCedenteDigito() {
        return cedenteDigito;
    }

    public void setCedenteDigito(String cedenteDigito) {
        this.cedenteDigito = cedenteDigito;
    }

    @Column(length=80)
    public String getLocalPagamento() {
        return localPagamento;
    }

    public void setLocalPagamento(String localPagamento) {
        this.localPagamento = localPagamento;
    }
    
    @Column
    public int getNossoNumeroAno() {
        return nossoNumeroAno;
    }

    public void setNossoNumeroAno(int nossoNumeroAno) {
        this.nossoNumeroAno = nossoNumeroAno;
    }
    
    @Column
    public boolean isCancelado() {
        return cancelado;
    }

    public void setCancelado(boolean cancelado) {
        this.cancelado = cancelado;
    }

    @Column
    @Temporal(TemporalType.DATE)
    public Calendar getCanceladoData() {
        return canceladoData;
    }

    public void setCanceladoData(Calendar canceladoData) {
        this.canceladoData = canceladoData;
    }

    public String toString() {
        String result="Cedente ";
        result += this.getId();
        result += ": " + this.getNome();
        result += " (No.:" + super.getSequenciaNumeroDocumento() + ")";
        
        return result;
    }

    @ManyToOne
	@JoinColumn(name = "centroCustoGeral")
	@ForeignKey(name = "centroCustoGeral")
	public CentroCusto getCentroCustoGeral(){return centroCustoGeral;}
	public void setCentroCustoGeral(CentroCusto centroCustoGeral){this.centroCustoGeral = centroCustoGeral;}

	@ManyToOne
	@JoinColumn(name = ITEM_CUSTO_IOF)
	@ForeignKey(name = ITEM_CUSTO_IOF)
	public ItemCusto getItemCustoIof() {return itemCustoIof;}
	public void setItemCustoIof(ItemCusto itemCustoIof) {this.itemCustoIof = itemCustoIof;}
	
	@ManyToOne
	@JoinColumn(name = ITEM_CUSTO_TARIFA)
	@ForeignKey(name = ITEM_CUSTO_TARIFA)
	public ItemCusto getItemCustoTarifa(){return itemCustoTarifa;}
	public void setItemCustoTarifa(ItemCusto itemCustoTarifa){this.itemCustoTarifa = itemCustoTarifa;}

	@Column(length=10)
	public String getLayoutCnab() {return layoutCnab;}
	public void setLayoutCnab(String layoutCnab) {this.layoutCnab = layoutCnab;}

	@Column
	public BigDecimal getValorTarifa() {return valorTarifa;}
	public void setValorTarifa(BigDecimal valorTarifa) {this.valorTarifa = valorTarifa;}

	@Column
	public boolean isBancoGeraOcorrenciaValorTarifa() {return bancoGeraOcorrenciaValorTarifa;}
	public void setBancoGeraOcorrenciaValorTarifa(boolean bancoGeraOcorrenciaValorTarifa) {this.bancoGeraOcorrenciaValorTarifa = bancoGeraOcorrenciaValorTarifa;}
}
