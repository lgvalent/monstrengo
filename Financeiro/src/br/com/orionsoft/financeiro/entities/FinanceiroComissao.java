package br.com.orionsoft.financeiro.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ForeignKey;

import br.com.orionsoft.basic.entities.pessoa.GrupoRepresentanteComissao;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;

/**
 * Esta classe permite controlar as comissões pagas para um determinado grupot de representantes.
 * Ela especializa a classe abstrata do basij.jar.
 * @author lucio 20110703
 */
@Entity
@DiscriminatorValue("FIN")
public class FinanceiroComissao extends GrupoRepresentanteComissao{
    public static final String ITEM_CUSTO = "itemCusto";
    
    private ItemCusto itemCusto;
    
	@ManyToOne
	@JoinColumn(name=ITEM_CUSTO)
	@ForeignKey(name=ITEM_CUSTO)
    public ItemCusto getItemCusto() {return itemCusto;}
	public void setItemCusto(ItemCusto itemCusto) {this.itemCusto = itemCusto;}
}
