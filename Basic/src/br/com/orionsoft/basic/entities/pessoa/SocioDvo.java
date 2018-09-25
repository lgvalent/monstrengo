package br.com.orionsoft.basic.entities.pessoa;



import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.StringUtils;
import br.com.orionsoft.monstrengo.core.util.ValidatorUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoBasic;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadata;
import br.com.orionsoft.monstrengo.crud.entity.metadata.IPropertyMetadataMutable;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
/**
 * Classe que valida entidades do tipo Socio. <br>
 * 
 * @author Lucio
 * @spring.bean id="SocioDvo" init-method="registerDvo"
 * @spring.property name="dvoManager" ref="DvoManager"
 * @spring.property name="capitalizeNames" value="false"
 * 
 */
public class SocioDvo extends DvoBasic<Socio> {

	/**
	 * Metodo que retorna a classe da entidade.
	 */
	public Class<Socio> getEntityType(){
		return Socio.class;	
	}
	
	/* Método que verifica o CNPJ de uma entidade do tipo Socio antes de gravá-la.
	 * Ele verifica se existe um CNPJ para validá-lo, se o documento é obrigatório é evitada a gravação sem o CNPJ.
	 */
	public void afterCreate(IEntity<Socio> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		checkIfFisicaIsDefined(entity);
	}
	
	/* Método que verifica o CPF de uma entidade do tipo Fisica antes de alterá-la. 
	 * Ele verifica se existe um CPF para validá-lo, se o documento é obrigatório é evitada a gravação sem o CPF.
	 */
	public void afterUpdate(IEntity<Socio> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		if(!checkIfFisicaIsDefined(entity)){
			Socio oSocio = entity.getObject();
			/* Remove espaços em branco desnecessários */
			oSocio.setNome(org.apache.commons.lang.StringUtils.strip(oSocio.getNome()));
		}
	}
	
	public void afterDelete(IEntity<Socio> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}
	
	public void beforeCreate(IEntity<Socio> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		
	}

	public void beforeDelete(IEntity<Socio> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}

	public void beforeUpdate(IEntity<Socio> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		checkIfFisicaIsDefined(entity);
	}

	/**
	 * Se a propriedade Fisica estiver definida, então Nome e DataNascimento serão somente Leitura,
	 * pois são redirecionamentos das propriedades originais
	 * @param entity
	 * @throws EntityException
	 * @throws DvoException
	 */
	private boolean checkIfFisicaIsDefined(IEntity<Socio> entity)
			throws EntityException, DvoException {
		Socio oSocio = entity.getObject();
		try {
			boolean usarDadosFisica = oSocio.getFisica() != null;
			IProperty propertyDN= entity.getProperty(Socio.DATA_NASCIMENTO);
			if(propertyDN.getInfo() instanceof IPropertyMetadataMutable){
				IPropertyMetadataMutable propertyMetadataMutable = ((IPropertyMetadataMutable)propertyDN.getInfo()).clone();
				propertyMetadataMutable.setReadOnly(usarDadosFisica);
				propertyDN.setInfo(propertyMetadataMutable);
			}
			IProperty propertyN= entity.getProperty(Socio.NOME);
			if(propertyN.getInfo() instanceof IPropertyMetadataMutable){
				IPropertyMetadataMutable propertyMetadataMutable = ((IPropertyMetadataMutable)propertyN.getInfo()).clone();
				propertyMetadataMutable.setReadOnly(usarDadosFisica);
				propertyN.setInfo(propertyMetadataMutable);
			}

			return usarDadosFisica;

		} catch (CloneNotSupportedException e) {
			DvoException dvoException = new DvoException(MessageList.createSingleInternalError(e));
			throw dvoException;
		}
	}
}