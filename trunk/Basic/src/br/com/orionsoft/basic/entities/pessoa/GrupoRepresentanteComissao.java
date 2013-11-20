package br.com.orionsoft.basic.entities.pessoa;

import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

/**
 * Esta classe permite controlar as comissões pagas para um determinado representante
 * @author lucio 20110703
 */
@Entity
@Table(name="basic_grupo_representante_comissao")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator", discriminatorType=DiscriminatorType.STRING, length=3)
public abstract class GrupoRepresentanteComissao {
    public static final String GRUPO_REPRESENTANTE = "grupoRepresentante";
    public static final String VALOR = "valor";
    public static final String PERCENTUAL = "percentual";
    
    private long id = -1;
    private GrupoRepresentante grupoRepresentante;
    private BigDecimal valor;
    private Double percentual;
    
    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

	@ManyToOne
	@JoinColumn(name=GRUPO_REPRESENTANTE)
	@ForeignKey(name=GRUPO_REPRESENTANTE)
    public GrupoRepresentante getGrupoRepresentante() {return grupoRepresentante;}
	public void setGrupoRepresentante(GrupoRepresentante grupoRepresentante) {this.grupoRepresentante = grupoRepresentante;}

	@Basic
	public BigDecimal getValor() {return valor;}
	public void setValor(BigDecimal valor) {this.valor = valor;}

	@Basic
	public Double getPercentual() {return percentual;}
	public void setPercentual(Double percentual) {this.percentual = percentual;}
}
