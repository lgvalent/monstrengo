package br.com.orionsoft.basic.entities;

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

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;

/**
 * Os seguinte discriminadores são utilizados:<br>
 * <li>CONC - ContratoCategoria</li> 
 * <li>FINC - ContratoFinanceiroCategoria</li> 
 * <li>SINC - ContratoSindicatoCategoria</li> 
 * <li>BIBC - ContratoBibliotecaCategoria</li> 
 * <li>PRSC - ContratoProcessoSeletivoCategoria</li> 
 * <li>ACAC - ContratoAcademicoCategoria</li> 
 * <li>PRFC - ContratoProfessorCategoria</li> 
 * <li>CLIC - ContratoClinicoCategoria</li> 
 * <li>CLAC - ContratoClinicoAtendenteCategoria</li> 
 * <li>AGEC - ContratoAgendaCategoria</li>
 *  
 * @author Lucio
 * @version 20060426 
 * @hibernate.class table="basic_contrato_categoria"
 * @hibernate.joined-subclass-key column="id"
 */
@Entity
@Table(name="basic_contrato_categoria")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="discriminator", discriminatorType=DiscriminatorType.STRING, length=4)
@DiscriminatorValue("CONC")
public class ContratoCategoria {
    public static final String NOME = "nome";
    
    public static final String OBSERVACOES = "observacoes";

    protected long id = -1;
    protected String nome;
    protected Observacoes observacoes;

    /**
     * @hibernate.id generator-class="native" unsaved-value="-1"
     */
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    /**
     * @hibernate.property length="50"
     */
    @Column(length=50)
    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    
    /**
     * @hibernate.many-to-one unique="true" foreign-key="observacoes" cascade="delete"
     */
    @ManyToOne @Cascade(CascadeType.DELETE)
    @JoinColumn(name="observacoes")
    @ForeignKey(name="observacoes")
    public Observacoes getObservacoes() {return observacoes;}
    public void setObservacoes(Observacoes observacoes) {this.observacoes = observacoes;}

	public String toString() {
        return this.nome;
    }
}
