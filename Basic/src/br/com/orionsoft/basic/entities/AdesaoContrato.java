package br.com.orionsoft.basic.entities;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;

/**
 * Está classe descreve as propriedades de adesões de podem ser
 * vinculadas a um contrato com Adesão. É útil para indicar
 * a 'população' que está vinculada a um contrato. Isto
 * é utilizado pelo financeiro para lançar itens
 * que tenham a indicação do contrato que gerou o item e qual pessoa gerou.
 * As pessoas para gerar item no faturamento deverão estar aderidas à algum contrato :)
 * 
 * @author Lucio 20110725
 * @version 20110725 
 */
@Entity
@Table(name="basic_adesao_contrato")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator", discriminatorType=DiscriminatorType.STRING, length=3)
@DiscriminatorValue("ADE")
public class AdesaoContrato {
    public static final String PESSOA = "pessoa";
    public static final String DATA_ADESAO = "dataAdesao";
    public static final String DATA_REMOCAO = "dataRemocao";
    public static final String INATIVO = "inativo";
    public static final String OBSERVACOES = "observacoes";
	public static final String CODIGO = "codigo";
	public static final String CONTRATO = "contrato";
    
    protected long id = -1;
    protected boolean inativo;
    protected Pessoa pessoa;
    protected Calendar dataAdesao;
    protected Calendar dataRemocao;
    protected Observacoes observacoes;
    private String codigo;
    private ContratoComAdesao contrato;

	/**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * @hibernate.property
     */
    @Column
    @Temporal(TemporalType.DATE)
    public Calendar getDataAdesao() {
        return dataAdesao;
    }

    public void setDataAdesao(Calendar dataAdesao) {
        this.dataAdesao = dataAdesao;
    }

    @Column
    @Temporal(TemporalType.DATE)
    public Calendar getDataRemocao() {
        return dataRemocao;
    }

    public void setDataRemocao(Calendar dataRemocao) {
        this.dataRemocao = dataRemocao;
    }

    @Column
    public boolean isInativo() {
        return inativo;
    }

    public void setInativo(boolean inativo) {
        this.inativo = inativo;
    }

    /**
	 * Pessoa representa uma hierarquia extensa de subclasses.
	 * Assim, um política FetchMode.SELECT evitará um JOIN extenso 
     */
    @ManyToOne
	@Fetch(FetchMode.SELECT)
    @JoinColumn(name="pessoa")
    @ForeignKey(name="pessoa")
    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    @ManyToOne @Cascade(CascadeType.DELETE) /*@Column(unique=true)*/
	@JoinColumn(name="observacoes")
	@ForeignKey(name="observacoes")
    public Observacoes getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(Observacoes observacoes) {
        this.observacoes = observacoes;
    }

	@Column(length=20)
	@Index(name=CODIGO)
	public String getCodigo() {
		return codigo;
	}
	
	public void setCodigo(String codigo) {
		this.codigo = codigo==null?null:StringUtils.substring(codigo, 0, 20);
	}

    @ManyToOne
	public ContratoComAdesao getContrato() {return contrato;}
	public void setContrato(ContratoComAdesao contrato) {this.contrato = contrato;}

	public String toString() {
		String result = "";

		if (this.inativo){
			result += "[Inativo";
			if(this.dataRemocao!=null)
				result += " em " + CalendarUtils.formatDate(this.dataRemocao);
			result += "]";
		}

		if (this.pessoa != null)
            result += " " + this.pessoa.toString();
        
        return result;
    }
}
