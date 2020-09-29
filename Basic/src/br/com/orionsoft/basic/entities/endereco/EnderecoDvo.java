package br.com.orionsoft.basic.entities.endereco;



import javax.json.Json;
import javax.json.JsonObject;

import org.apache.tools.ant.filters.StringInputStream;

import br.com.orionsoft.basic.Manter;
import br.com.orionsoft.basic.services.ConsultarCEPService;
import br.com.orionsoft.basic.services.ConsultarCEPService.ConsultarCepBean;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.StringUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoBasic;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
/**
 * Classe que valida entidades do tipo Endereco. <br>
 * 
 * @author Lucio
 * @spring.bean id="EnderecoDvo" init-method="registerDvo"
 * @spring.property name="dvoManager" ref="DvoManager"
 * @spring.property name="capitalizeNames" value="false"
 * 
 */
public class EnderecoDvo extends DvoBasic<Endereco> {

	/**
	 * Metodo que retorna a classe da entidade.
	 */
	public Class<Endereco> getEntityType(){
		return Endereco.class;	
	}

	public void afterCreate(IEntity<Endereco> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
	}

	public void afterUpdate(IEntity<Endereco> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		validaCEP(entity, serviceData);
	}

	public void afterDelete(IEntity<Endereco> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {

	}

	public void beforeCreate(IEntity<Endereco> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {

	}

	public void beforeDelete(IEntity<Endereco> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {

	}

	public void beforeUpdate(IEntity<Endereco> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
	}

	/**
	 * Verifica o CEP, faz uma busca pelos dados do CEP e sincroniza com o banco, 
	 * cadastrando as novas ruas, bairros, cidades
	 * @param entity
	 * @throws EntityException
	 * @throws DvoException
	 */
	@SuppressWarnings("unchecked")
	private void validaCEP(IEntity<Endereco> endereco, ServiceData serviceData) throws DvoException {
		
		try {
			ServiceData service = new ServiceData(ConsultarCEPService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(ConsultarCEPService.IN_CEP, endereco.getObject().getCep());
			this.getDvoManager().getEntityManager().getServiceManager().execute(service);

			ConsultarCepBean bean = service.getFirstOutput();

			Manter manter = new Manter(this.getDvoManager().getEntityManager().getServiceManager(), serviceData);
			
			if(endereco.getProperty(Endereco.CEP).getValue().isModified()){
				Bairro oBairro = new Bairro();
				oBairro.setNome(bean.getBairro());
				IEntity<Bairro> bairro = manter.manterBairro(oBairro);
				if (bairro!=null)
					endereco.getProperty(Endereco.BAIRRO).getValue().setAsEntity(bairro);

				Municipio oMunicipio = new Municipio();
				oMunicipio.setNome(bean.getLocalidade());
				oMunicipio.setUf(Uf.valueOf(bean.getUf()));
				oMunicipio.setCodigoIbge(bean.getIbge());
				IEntity<Municipio> municipio = manter.manterMunicipio(oMunicipio);
				if (municipio!=null)
					endereco.getProperty(Endereco.MUNICIPIO).getValue().setAsEntity(municipio);

				Logradouro oLogradouro = new Logradouro();
				String log = bean.getLogradouro();
				if(!log.isEmpty()){ // Alguns CEP não retornam logradouro, somente município!
					int firstSpace = log.indexOf(" ");
					oLogradouro.setTipoLogradouro(TipoLogradouro.valueOf(StringUtils.removeAccent(log.substring(0,firstSpace).toUpperCase())));
					oLogradouro.setNome(log.substring(firstSpace+1));
				} else
					oLogradouro.setNome(".");
				IEntity<Logradouro> logradouro = manter.manterLogradouro(oLogradouro);
				if (logradouro!=null)
					endereco.getProperty(Endereco.LOGRADOURO).getValue().setAsEntity(logradouro);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DvoException(MessageList.create(EnderecoDvo.class, "CEP_INVALIDO", endereco.getObject().getCep(), e.getMessage()));
		}
	}
}