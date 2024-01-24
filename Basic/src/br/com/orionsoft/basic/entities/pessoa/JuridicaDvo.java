package br.com.orionsoft.basic.entities.pessoa;

import br.com.orionsoft.basic.Manter;
import br.com.orionsoft.basic.entities.endereco.Endereco;
import br.com.orionsoft.basic.entities.endereco.Telefone;
import br.com.orionsoft.basic.entities.endereco.TipoTelefone;
import br.com.orionsoft.basic.services.ConsultarCNPJService;
import br.com.orionsoft.basic.services.ConsultarCNPJService.ConsultarCNPJBean;
import br.com.orionsoft.basic.services.ConsultarCNPJService.ConsultarCNPJBean.QuadroSocietario;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.util.StringUtils;
import br.com.orionsoft.monstrengo.core.util.ValidatorUtils;
import br.com.orionsoft.monstrengo.crud.entity.EntityException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntitySet;
import br.com.orionsoft.monstrengo.crud.entity.PropertyValueException;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.entity.dvo.DvoException;
import br.com.orionsoft.monstrengo.crud.services.ListService;
import br.com.orionsoft.monstrengo.mail.services.SendMailService;
import br.com.orionsoft.monstrengo.security.entities.UserSession;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
/**
 * Classe que valida entidades do tipo Juridica. <br>
 * 
 * @author Sergio
 * @spring.bean id="JuridicaDvo" init-method="registerDvo"
 * @spring.property name="dvoManager" ref="DvoManager"
 * @spring.property name="capitalizeNames" value="false"
 * 
 */
public class JuridicaDvo extends PessoaDvo<Juridica> {
	/** Permite configurar via Spring, se deseja ou não a capitalização
	 * dos nomes automaticamente. Pois em alguns casos isto não é bom.
	 * Mas no início de dados importados, isto é muito útil */
	private boolean capitalizeNames = true;

	public boolean isCapitalizeNames() {return capitalizeNames;}
	public void setCapitalizeNames(boolean capitalizeNames) {this.capitalizeNames = capitalizeNames;}

	/**
	 * Metodo que retorna a classe da entidade.
	 */
	public Class<Juridica> getEntityType(){
		return Juridica.class;	
	}
	
	/* Método que verifica o CNPJ de uma entidade do tipo Juridica antes de gravá-la.
	 * Ele verifica se existe um CNPJ para validá-lo, se o documento é obrigatório é evitada a gravação sem o CNPJ.
	 */
	public void afterCreate(IEntity<Juridica> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		consultarCNPJ(entity, serviceData);
		//////////////////////////////////////////////////////////////////////////////////

		/* Lucio 12/12/12: Define o endereço principal na lista de outros endereços */
		Juridica oJuridica = entity.getObject();
		oJuridica.getEnderecoCorrespondencia().setPessoa(oJuridica);
	}
	
	/* Método que verifica o CPF de uma entidade do tipo Fisica antes de alterá-la. 
	 * Ele verifica se existe um CPF para validá-lo, se o documento é obrigatório é evitada a gravação sem o CPF.
	 */
	public void afterUpdate(IEntity<Juridica> entity, UserSession userSession, ServiceData serviceData) throws DvoException, BusinessException {
		validarCnpjRepetido(entity, serviceData);

		DvoException dvoExceptions = new DvoException(new MessageList());
		
		try {
			super.afterUpdate(entity, userSession, serviceData);
		} catch (DvoException e) {
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		} 
        
        try {
			validarCnpj(entity);
		} catch (DvoException e) {
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		} 
		
		try {
			validarEMail(entity);
		} catch (DvoException e) {
			dvoExceptions.getErrorList().addAll(e.getErrorList());
		}
		
		Juridica oJuridica = entity.getObject();
		if(this.capitalizeNames){
			
			/* Coloca os nomes em Capitalize e remove espaços em branco desnecessários */
			oJuridica.setApelido(StringUtils.prepareStringField(oJuridica.getApelido())); 
			oJuridica.setNome(StringUtils.prepareStringField(oJuridica.getNome()));
		}else{
			/* Remove espaços em branco desnecessários */
			oJuridica.setApelido(org.apache.commons.lang.StringUtils.strip(oJuridica.getApelido())); 
			oJuridica.setNome(org.apache.commons.lang.StringUtils.strip(oJuridica.getNome()));
		}
		if(!dvoExceptions.getErrorList().isEmpty()){
			throw dvoExceptions;
		}
	}
	
	public void afterDelete(IEntity<Juridica> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}
	
	public void beforeCreate(IEntity<Juridica> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		
	}

	public void beforeDelete(IEntity<Juridica> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}

	public void beforeUpdate(IEntity<Juridica> arg0, UserSession userSession, ServiceData arg1) throws DvoException, BusinessException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Método que verifica o CNPJ de uma entidade recebendo como argumento um objeto do tipo Juridica.
	 * Ele verifica se o atributo CNPJ objeto do tipo Juridica é obrigatório, se for é evitada a gravação/alteração do mesmo.
	 */
	private void validarCnpj(IEntity<Juridica> entity) throws DvoException, BusinessException{
		try{
			/* Lucio 20090330: Permite a digitação de CNPJ 00.000.000/0000-00 (Sandra Rodrigues) */
			if(entity.getPropertyValue(Juridica.DOCUMENTO).equals("00000000000000"))
				return;

			/*
			 * Verifica se existe um CNPJ para validar.
			 */
			if(entity.getProperty(Juridica.DOCUMENTO).getValue().isValueNull()){
				/*
				 * Verifica se o CNPJ é requerido, evitando que uma entidade do tipo Juridica seja gravada/alterada sem o documento.
				 */
				if(entity.getProperty(Juridica.DOCUMENTO).getInfo().isRequired()){
					throw new DvoException(MessageList.create(JuridicaDvo.class, "CNPJ_REQUERIDO"));
				}
			}
			else{
				/*
				 * Caso o CNPJ não seja válido, uma mensagem será enviada.
				 */
				if(!ValidatorUtils.validarCNPJ(entity.getProperty(Juridica.DOCUMENTO).getValue().getAsObject().toString())){
					throw new DvoException(MessageList.create(JuridicaDvo.class, "CNPJ_INVALIDO", entity.getPropertyValue(Juridica.DOCUMENTO)));
					
				}
			}
				
		}catch(PropertyValueException e){
			throw new BusinessException(e.getErrorList());
			
		}
		catch(EntityException e){
		    throw new BusinessException(e.getErrorList());
		}    
	} 
	
	// Método que verifica se já existe um entidade do tipo Jurídica com o mesmo CNPJ.
	public void validarCnpjRepetido(IEntity<Juridica> entity, ServiceData serviceDataOwner) throws DvoException, BusinessException {
		if(log.isDebugEnabled())	
			log.debug("Verificando CNPJ repetido " + entity.getPropertyValue(Juridica.DOCUMENTO));

		/* Lucio 20090330: Permite a digitação de CNPJ 00.000.000/0000-00 (Sandra Rodrigues) */
		if(entity.getPropertyValue(Juridica.DOCUMENTO).equals("00000000000000"))
			return;

		ServiceData sd = new ServiceData(ListService.SERVICE_NAME, serviceDataOwner);
		sd.getArgumentList().setProperty(ListService.CLASS, Juridica.class);
		/* where entity.documento='00011122234' and entity.id!=1 */
		sd.getArgumentList().setProperty(ListService.CONDITION_OPT_STR, IDAO.ENTITY_ALIAS_HQL + "." + Juridica.DOCUMENTO + "= '" + entity.getPropertyValue(Juridica.DOCUMENTO) + "' and " + IDAO.ENTITY_ALIAS_HQL + "." + IDAO.PROPERTY_ID_NAME + "!=" + entity.getId());
		this.getDvoManager().getEntityManager().getServiceManager().execute(sd);
		
		IEntityList<Juridica> result = sd.getFirstOutput();
		
		if(!result.isEmpty()){
			IEntity<Juridica> juridica = result.getFirst();
			throw new DvoException(MessageList.create(JuridicaDvo.class, "CNPJ_REPETIDO", entity.getPropertyValue(Juridica.DOCUMENTO), juridica.getId() + ":" + juridica.toString()));
		}
	}
	
	/***
	 * Método que verifica se os são válidos.
	 */
	public void validarEMail(IEntity<Juridica> entity) throws DvoException, BusinessException {
		try{
			String emails = entity.getProperty(Juridica.EMAIL).getValue().getAsString();
			if(!StringUtils.isBlank(emails)){
				for(String email: emails.split(";"))
					if(!SendMailService.validateEMail(email))
						throw new DvoException(MessageList.create(JuridicaDvo.class, "EMAIL_INVALIDO", email));
			}
		}
		catch(PropertyValueException e) {
			throw new BusinessException(e.getErrorList());
		}
		catch(EntityException e) {
			throw new BusinessException(e.getErrorList());
		}
		
	}
	
	/**
	 * Verifica o CNPJ, faz uma busca pelos dados do CNPJ e sincroniza com o banco, 
	 * cadastrando as novas ruas, bairros, cidades
	 * @param entity
	 * @throws EntityException
	 * @throws DvoException
	 */
	@SuppressWarnings("unchecked")
	private void consultarCNPJ(IEntity<Juridica> juridica, ServiceData serviceData) throws DvoException {
		Juridica oJuridica = juridica.getObject();
		try {
			if(juridica.getProperty(Juridica.DOCUMENTO).getValue().isModified()){
				juridica.getProperty(Juridica.DOCUMENTO).getValue().setModified(false);
				
				ServiceData service = new ServiceData(ConsultarCNPJService.SERVICE_NAME, serviceData);
				service.getArgumentList().setProperty(ConsultarCNPJService.IN_CNPJ, juridica.getObject().getDocumento());
				this.getDvoManager().getEntityManager().getServiceManager().execute(service);

				ConsultarCNPJBean bean = service.getFirstOutput();

				Manter manter = new Manter(this.getDvoManager().getEntityManager().getServiceManager(), serviceData);
				
				if(!bean.getSituacao().equals("ATIVA"))
					throw new DvoException(MessageList.create(JuridicaDvo.class, "CNPJ_INATIVO", oJuridica.getDocumento(), bean.getMotivoSituacao()));
				oJuridica.setNome(bean.getNome());
				if(!bean.getFantasia().isEmpty())
					oJuridica.setApelido(bean.getFantasia());
				else
					oJuridica.setApelido(bean.getNome());
				oJuridica.setCapitalSocial(bean.getCapitalSocial());
//				oJuridica.setNumeroFuncionarios(0);
				oJuridica.setTipoEstabelecimento(StringUtils.prepareStringField(bean.getTipo()));
//				oJuridica.setRegimeTributario(regimeTributario);
//				oJuridica.setDocumento(documento);
//				oJuridica.setIe(ie);

				CNAE oCNAE = new CNAE();
				oCNAE.setNome(bean.getAtividadePrincipal().get(0).getTexto());
				oCNAE.setCodigo(bean.getAtividadePrincipal().get(0).getCodigo());
				IEntity<CNAE> CNAE = manter.manterCnae(oCNAE);
				if (CNAE!=null)
					juridica.getProperty(Juridica.CNAE).getValue().setAsEntity(CNAE);
				
//				oJuridica.setCmc(cmc);
//				oJuridica.setDataCadastro(dataCadastro);
				oJuridica.setDataInicial(bean.getAbertura());

				if(!bean.getCep().isEmpty()) // Set via IEntity para marcar modified=true e forçar a validação
					juridica.getProperty(Pessoa.ENDERECO_CORRESPONDENCIA).getValue().getAsEntity().getProperty(Endereco.CEP).getValue().setAsObject(bean.getCep());
				
				oJuridica.getEnderecoCorrespondencia().setNumero(bean.getNumero());
				oJuridica.getEnderecoCorrespondencia().setComplemento(bean.getComplemento());

				if(!bean.getTelefones().isEmpty()){
					IEntitySet<Telefone> fones = juridica.getProperty(Pessoa.TELEFONES).getValue().getAsEntitySet();
					fones.clear(); // Evita duplicidade a cada validação
					for(String fone: bean.getTelefones()){
						TipoTelefone oTipoTelefone = new TipoTelefone();
						oTipoTelefone.setNome("Comercial");
						IEntity<TipoTelefone> tipoTelefone = manter.manterTipoTelefone(oTipoTelefone);
						if(tipoTelefone != null){
							String numero = StringUtils.removeNonDigit(fone);
							Telefone oTelefone = fones.getRunEntity().getObject();
							oTelefone.setTipoTelefone(tipoTelefone.getObject());
							oTelefone.setDdd(numero.substring(0,2));
							numero = numero.substring(2, numero.length());
							numero = numero.length() < 9?String.format("%0" + (9-numero.length()) + "d", 0) + numero: numero;
							oTelefone.setNumero(numero);

							fones.runAdd();
						}
					}
				}

				oJuridica.setEmail(bean.getEmail());
				
				IEntitySet<Socio> socios = juridica.getProperty(Juridica.SOCIOS).getValue().getAsEntitySet();
				socios.clear(); // Evita duplicidade a cada validação
				for(QuadroSocietario q: bean.getQuadroSocietario()){
					Socio oSocio = socios.getRunEntity().getObject();

					Cargo oCargo = new Cargo();
					oCargo.setNome(q.getQualificacaoNome());
					IEntity<Cargo> cargo = manter.manterCargo(oCargo);
					if(cargo != null)
						oSocio.setCargo(cargo.getObject());

					//					oSocio.setDataEntrada(dataAdmissao);
					oSocio.setDataNascimento(CalendarUtils.getCalendar(1900, 0, 1));
					//					oSocio.setDataSaida(dataDemissao);
					//					oSocio.setFisica(fisica);
					//					oSocio.setJuridica(juridica)
					oSocio.setNome(q.getNome());

					socios.runAdd();
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			throw new DvoException(MessageList.create(JuridicaDvo.class, "ERRO_CONSULTAR_CNPJ", oJuridica.getDocumento(), e.getMessage()));
		}
	}

}