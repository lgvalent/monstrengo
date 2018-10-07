package br.com.orionsoft.monstrengo.crud.entity;

import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IGroupMetadata;

public interface IGroupProperties {
    public IGroupMetadata getInfo();
    public IProperty[] getProperties();
}
