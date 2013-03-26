package br.com.orionsoft.monstrengo.crud.entity.metadata;

import java.util.ArrayList;
import java.util.List;

import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;

public class GroupMetadata implements IGroupMetadata {

	private int index;
	private String name;

	private String label;
	private String hint;
	private String description;
	private String colorName;
	
	private List<IPropertyMetadata> properties = new ArrayList<IPropertyMetadata>();

	public GroupMetadata(int index, String name){
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {return this.index;}
	public void setIndex(int index) {this.index = index;}

	public String getName() {return this.name;}

	public List<IPropertyMetadata> getProperties() {
		return properties;
	}
	
	public boolean equals(Object o){
		return o!=null && (((IGroupMetadata) o).getIndex()==this.index);
	}
	
	public String toString(){
		return this.index + ":" + this.name;
 
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}


}
