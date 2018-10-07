package br.com.orionsoft.monstrengo.crud.entity;

import br.com.orionsoft.monstrengo.crud.entity.IGroupProperties;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;

public class GroupProperty implements IGroupProperties {

	private IGroupMetadata info;
	private IProperty[] properties;
	
	public GroupProperty(IGroupMetadata info, IProperty[] properties){
		this.info = info;
		this.properties = properties;
	}
	
	public IGroupMetadata getInfo() {return this.info;}

	public IProperty[] getProperties() {return this.properties;}

}
