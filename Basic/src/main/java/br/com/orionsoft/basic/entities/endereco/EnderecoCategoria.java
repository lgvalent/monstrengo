package br.com.orionsoft.basic.entities.endereco;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;

/**
 * Created on 02/09/2007
 * @author Lucio
 * @author Andre
 */
@Entity
@Table(name="basic_endereco_categoria")
public class EnderecoCategoria{
    
    public static final String NOME = "nome";

    private long id = IDAO.ENTITY_UNSAVED;
    private String nome;
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    @Column(length=20)
    public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}

	public String toString() {
        return this.nome != null ? this.nome : "";
    }
        
}
