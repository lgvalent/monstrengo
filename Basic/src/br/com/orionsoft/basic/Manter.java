package br.com.orionsoft.basic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Expression;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.ContratoCategoria;
import br.com.orionsoft.basic.entities.ContratoContato;
import br.com.orionsoft.basic.entities.ContratoContatoMotivo;
import br.com.orionsoft.basic.entities.Observacoes;
import br.com.orionsoft.basic.entities.commons.Feriado;
import br.com.orionsoft.basic.entities.commons.FeriadoRecesso;
import br.com.orionsoft.basic.entities.endereco.Bairro;
import br.com.orionsoft.basic.entities.endereco.Endereco;
import br.com.orionsoft.basic.entities.endereco.EnderecoCategoria;
import br.com.orionsoft.basic.entities.endereco.Logradouro;
import br.com.orionsoft.basic.entities.endereco.Municipio;
import br.com.orionsoft.basic.entities.endereco.Telefone;
import br.com.orionsoft.basic.entities.endereco.TipoTelefone;
import br.com.orionsoft.basic.entities.pessoa.CNAE;
import br.com.orionsoft.basic.entities.pessoa.Cargo;
import br.com.orionsoft.basic.entities.pessoa.Contador;
import br.com.orionsoft.basic.entities.pessoa.Contato;
import br.com.orionsoft.basic.entities.pessoa.EscritorioContabil;
import br.com.orionsoft.basic.entities.pessoa.Fisica;
import br.com.orionsoft.basic.entities.pessoa.Funcionario;
import br.com.orionsoft.basic.entities.pessoa.GrauParentesco;
import br.com.orionsoft.basic.entities.pessoa.GrupoRepresentante;
import br.com.orionsoft.basic.entities.pessoa.Juridica;
import br.com.orionsoft.basic.entities.pessoa.NecessidadeEspecial;
import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.basic.entities.pessoa.Profissao;
import br.com.orionsoft.basic.entities.pessoa.Representante;
import br.com.orionsoft.basic.entities.pessoa.ResponsavelCpf;
import br.com.orionsoft.basic.entities.pessoa.Socio;
import br.com.orionsoft.monstrengo.ManterBasic;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.IServiceManager;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.EntityList;
import br.com.orionsoft.monstrengo.crud.entity.EntitySet;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntitySet;
import br.com.orionsoft.monstrengo.crud.entity.dao.IDAO;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;


/**
 * Essa classe faz a verificação da existencia de um determinado registro.
 * Se o registro ja existir no banco, será retornada a entidade
 * prenchida com as informações desse registro, caso contrario
 * sera criada uma nova entidade e gravado no banco.
 * 
 *  05/11/2007 -
 *  
  		br.com.orionsoft.basic.entities.Contrato.class
		br.com.orionsoft.basic.entities.ContratoCategoria.class
		br.com.orionsoft.basic.entities.ContratoContato.class
		br.com.orionsoft.basic.entities.ContratoContatoMotivo.class
		#br.com.orionsoft.basic.entities.Observacoes.class
		#br.com.orionsoft.basic.entities.commons.Feriado.class
		#br.com.orionsoft.basic.entities.commons.FeriadoRecesso.class
		-br.com.orionsoft.basic.entities.commons.Mes.class
		#br.com.orionsoft.basic.entities.endereco.Bairro.class
		#br.com.orionsoft.basic.entities.endereco.Endereco.class
		#br.com.orionsoft.basic.entities.endereco.EnderecoCategoria.class
		#br.com.orionsoft.basic.entities.endereco.Logradouro.class
		#br.com.orionsoft.basic.entities.endereco.Municipio.class
		#br.com.orionsoft.basic.entities.endereco.Telefone.class
		#br.com.orionsoft.basic.entities.endereco.TipoTelefone.class
		-br.com.orionsoft.basic.entities.endereco.Uf.class
		#br.com.orionsoft.basic.entities.pessoa.CNAE.class
		#br.com.orionsoft.basic.entities.pessoa.Cargo.class
		#br.com.orionsoft.basic.entities.pessoa.Contador.class
		#br.com.orionsoft.basic.entities.pessoa.Contato.class
		#br.com.orionsoft.basic.entities.pessoa.EscritorioContabil.class
		-br.com.orionsoft.basic.entities.pessoa.EstadoCivil.class
		#br.com.orionsoft.basic.entities.pessoa.Fisica.class
		#br.com.orionsoft.basic.entities.pessoa.Funcionario.class
		#br.com.orionsoft.basic.entities.pessoa.GrauParentesco.class
		#br.com.orionsoft.basic.entities.pessoa.GrupoRepresentante.class
		#br.com.orionsoft.basic.entities.pessoa.Juridica.class
		#br.com.orionsoft.basic.entities.pessoa.NecessidadeEspecial.class
		#br.com.orionsoft.basic.entities.pessoa.Pessoa.class
		#br.com.orionsoft.basic.entities.pessoa.PessoaEndereco.class
		#br.com.orionsoft.basic.entities.pessoa.Profissao.class
		#br.com.orionsoft.basic.entities.pessoa.Representante.class
		#br.com.orionsoft.basic.entities.pessoa.ResponsavelCpf.class
		#br.com.orionsoft.basic.entities.pessoa.Socio.class

 * 
 * Created 2005/12/26
 * @author marcia
 * @version
 */
public class Manter extends ManterBasic
{
    
    public Manter(IServiceManager serviceManager, ServiceData serviceDataOwner)
    {
        super(serviceManager, serviceDataOwner);
    }
    
    @SuppressWarnings("unchecked")
    /**
     * Metodo de manutencao de municipios. Verifica se ja existe algum
     * registro no banco com o mesmo nome e mesma uf.
     * Retorna um IEntity preenchido com os valores do registro
     * O nome do municipio deve ser preenchido caso contrario o IEntity será nulo. 
     * Caso a uf não seja especificada, sera feita a comparação somente com o
     * nome do municipio.
     */
    public IEntity manterMunicipio(Municipio municipio) throws BusinessException, HibernateException
    {
    	log.debug("ManterMunicipio");
    	if ((municipio!=null) && (municipio.getNome()!=null))
    	{
    		Criteria crit = this.serviceData.getCurrentSession().createCriteria(Municipio.class);
    		crit.add(Expression.eq(Municipio.NOME, municipio.getNome()));
    		
    		// Verificando se uf é nulo. No banco do sivamar a coluna uf pode ser nula.
    		if (municipio.getUf() != null){
    			crit.add(Expression.eq(Municipio.UF, municipio.getUf()));
    		}
    		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Municipio.class);
    		
    		// se o municipio nao estiver cadastrado
    		if (entityList.size() == 0)
    		{
    			// cria um novo municipio
    			IEntity municipioEntity = UtilsCrud.create(this.serviceManager, Municipio.class, this.serviceData);
                manterPrimitiveProperties(municipioEntity, municipio);

    			// Grava o registro no banco.
    			UtilsCrud.update(this.serviceManager, municipioEntity, this.serviceData);
    			
    			return municipioEntity;
    		}
    		// se sim pega o id e retorna
    		return entityList.get(0);
    	}
    	// se o municipio ou o nome do municipio é nulo.
    	return null;
    }
    
    @SuppressWarnings("unchecked")
    public IEntity manterBairro(Bairro bairro) throws BusinessException, HibernateException
    {
    	log.debug("ManterBairro");
        if ((bairro!=null) && (bairro.getNome()!=null))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(Bairro.class);
            crit.add(Expression.eq(Bairro.NOME, bairro.getNome()));
            IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Bairro.class);
            
            if (entityList.size() == 0)
            {
                IEntity bairroEntity = UtilsCrud.create(this.serviceManager, Bairro.class, this.serviceData);
                manterPrimitiveProperties(bairroEntity, bairro);
                
                UtilsCrud.update(this.serviceManager, bairroEntity, this.serviceData);
                return bairroEntity;
            }
            return entityList.get(0);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public IEntity manterLogradouro(Logradouro logradouro) throws BusinessException, HibernateException
    {
    	log.debug("ManterLogradouro");
        if ((logradouro!=null) && (logradouro.getNome()!=null))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(Logradouro.class);
            crit.add(Expression.eq(Logradouro.NOME, logradouro.getNome()));
            if (logradouro.getTipoLogradouro()!=null)
                crit.add(Expression.eq(Logradouro.TIPO_LOGRADOURO, logradouro.getTipoLogradouro()));
            
            IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Logradouro.class);
            
            if (entityList.size() == 0)
            {
                IEntity logradouroEntity = UtilsCrud.create(this.serviceManager, Logradouro.class, this.serviceData);

                manterPrimitiveProperties(logradouroEntity, logradouro);
                
                UtilsCrud.update(this.serviceManager, logradouroEntity, this.serviceData);
                
                return logradouroEntity;
            }
            return entityList.get(0);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public IEntity manterEnderecoCategoria(EnderecoCategoria enderecoCategoria) throws BusinessException, HibernateException
    {
    	log.debug("ManterLogradouro");
        if ((enderecoCategoria!=null) && (enderecoCategoria.getNome()!=null))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(EnderecoCategoria.class);
            crit.add(Expression.eq(EnderecoCategoria.NOME, enderecoCategoria.getNome()));
            
            IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), EnderecoCategoria.class);
            
            if (entityList.size() == 0)
            {
                IEntity enderecoCategoriaEntity = UtilsCrud.create(this.serviceManager, EnderecoCategoria.class, this.serviceData);

                manterPrimitiveProperties(enderecoCategoriaEntity, enderecoCategoria);
                
                UtilsCrud.update(this.serviceManager, enderecoCategoriaEntity, this.serviceData);
                
                return enderecoCategoriaEntity;
            }
            
            return entityList.get(0);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    /**
     *  Endereco é ONE-TO-ONE, logo não é necessário pesquisar se ele já existe 
     */
    public IEntity manterEndereco(Endereco endereco) throws BusinessException, HibernateException
    {
    	log.debug("Endereco");
        if (endereco!=null)
        {
//        	log.debug("ManterCep");
//            if ((endereco!=null) && (endereco.getCep()!=null))
//            {
//                Criteria crit = this.serviceData.getCurrentSession().createCriteria(Endereco.class);
//                crit.add(Expression.eq(Endereco.CEP, endereco.getCep()));
//                crit.add(Expression.eq(Endereco.NUMERO, endereco.getNumero()));
//                crit.add(Expression.eq(Endereco.COMPLEMENTO, endereco.getComplemento()));
//                
//                // Se não tiver nenhum bairro setado vai fazer a consulta so pelo nome do logradouro
//                if ((endereco.getBairro()!=null) && (endereco.getBairro().getNome()!=null))
//                {
//                    crit.createAlias(Endereco.BAIRRO , "bairro");
//                    crit.add(Expression.eq("bairro.nome", endereco.getBairro().getNome()));
//                }
//                
//                // Tenta filtrar por logradouro 
//                if ((endereco.getLogradouro()!=null) && (endereco.getLogradouro().getNome()!=null))
//                {
//                	crit.createAlias(Endereco.LOGRADOURO , "logradouro");
//                	crit.add(Expression.eq("logradouro.nome", endereco.getLogradouro().getNome()));
//                }
//                
//                // Tenta filtrar por municipio 
//                if ((endereco.getMunicipio()!=null) && (endereco.getMunicipio().getNome()!=null))
//                {
//                	crit.createAlias(Endereco.MUNICIPIO, "municipio");
//                	crit.add(Expression.eq("municipio.nome", endereco.getMunicipio().getNome()));
//                }
//                
//                IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Endereco.class);
//                
//                if(entityList.size()==0){
                	
                	IEntity enderecoEntity = UtilsCrud.create(this.serviceManager, Endereco.class, this.serviceData);
                	
                	manterPrimitiveProperties(enderecoEntity, endereco);
                	
                	
                	IEntity bairro = manterBairro(endereco.getBairro());
                	if (bairro!=null)
                		enderecoEntity.getProperty(Endereco.BAIRRO).getValue().setAsEntity(bairro);
                	
                	IEntity municipio = manterMunicipio(endereco.getMunicipio());
                	if (municipio!=null)
                		enderecoEntity.getProperty(Endereco.MUNICIPIO).getValue().setAsEntity(municipio);
                	
                	IEntity logradouro = manterLogradouro(endereco.getLogradouro());
                	if (logradouro!=null)
                		enderecoEntity.getProperty(Endereco.LOGRADOURO).getValue().setAsEntity(logradouro);
                	
                	UtilsCrud.update(this.serviceManager, enderecoEntity, this.serviceData);
                	
                	return enderecoEntity;
//                }
//                
//                return entityList.get(0);
//            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public IEntity manterProfissao(Profissao profissao) throws BusinessException, HibernateException
    {
    	log.debug("ManterProfissao");
        if ((profissao!=null) && (profissao.getNome()!=null))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(Profissao.class);
            crit.add(Expression.eq(Profissao.NOME, profissao.getNome()));
            
            IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Profissao.class);
            
            if (entityList.size() == 0)
            {
                IEntity profissaoEntity = UtilsCrud.create(this.serviceManager, Profissao.class, this.serviceData);
                
                manterPrimitiveProperties(profissaoEntity, profissao);

                UtilsCrud.update(this.serviceManager, profissaoEntity, this.serviceData);
                return profissaoEntity;
            }
            return entityList.get(0);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public IEntity manterCargo(Cargo cargo) throws BusinessException, HibernateException
    {
    	log.debug("Cargo");
        if ((cargo!=null) && (cargo.getNome()!=null))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(Cargo.class);
            crit.add(Expression.eq(Cargo.NOME, cargo.getNome()));
            
            IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Cargo.class);
            
            if (entityList.size() == 0)
            {
                IEntity cargoEntity = UtilsCrud.create(this.serviceManager, Cargo.class, this.serviceData);
                
                manterPrimitiveProperties(cargoEntity, cargo);

                UtilsCrud.update(this.serviceManager, cargoEntity, this.serviceData);
                return cargoEntity;
            }
            
            return entityList.get(0);
        }
        return null;
    }
    
    /** 
     * Verifica se um id foi passado para o cnae para então utilizar e localizar
     * Senão busca o cnae pelo nome
     * 
     */
    @SuppressWarnings("unchecked")
    public IEntity manterCnae(CNAE cnae) throws BusinessException, HibernateException
    {
    	log.debug("ManterRamo: " + cnae.getId() + "-" + cnae.getCodigo() + ":" + cnae.getNome());
        if ((cnae!=null) && ((cnae.getNome()!=null)||(cnae.getCodigo()!=null)))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(CNAE.class);
            /* Verifica se um id foi passado para o cnae para então utilizar e localizar */
            if(cnae.getId()!=IDAO.ENTITY_UNSAVED)
                crit.add(Expression.eq(IDAO.PROPERTY_ID_NAME, cnae.getId()));
             else
                 if(StringUtils.isNotEmpty(cnae.getCodigo()))
                     crit.add(Expression.eq(CNAE.CODIGO, cnae.getCodigo()));
                  else
                	  crit.add(Expression.eq(CNAE.NOME, cnae.getNome()));
            	
            IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), CNAE.class);
            
            if (entityList.size() == 0)
            {
                IEntity ramoEntity = UtilsCrud.create(this.serviceManager, CNAE.class, this.serviceData);

                manterPrimitiveProperties(ramoEntity, cnae);

                
                UtilsCrud.update(this.serviceManager, ramoEntity, this.serviceData);
                return ramoEntity;
            }
            return entityList.get(0);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public IEntity manterTipoTelefone(TipoTelefone tipoTelefone) throws BusinessException, HibernateException
    {
    	log.debug("ManterTipoTelefone");
        if ((tipoTelefone!=null) && (tipoTelefone.getNome()!=null))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(TipoTelefone.class);
            crit.add(Expression.eq(TipoTelefone.NOME, tipoTelefone.getNome()));
            IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), TipoTelefone.class);
            
            if (entityList.size() == 0)
            {
                IEntity tpTelefone = UtilsCrud.create(this.serviceManager, TipoTelefone.class, this.serviceData);
                
                manterPrimitiveProperties(tpTelefone, tipoTelefone);

                UtilsCrud.update(this.serviceManager, tpTelefone, this.serviceData);
                return tpTelefone;
            }
            return entityList.get(0);
        }
        return null;
    }
    
    /** 
     * O telefone é sempre cadastrado, sem verificar a existencia do numero. Pois mais de 
     * uma pessoa pode ter o mesmo numero, porém a alteração de um não implica na alteração 
     * do outro. 
     */
    public IEntitySet manterTelefones(Set<Telefone> telefones) throws BusinessException, HibernateException
    {
    	log.debug("ManterTelefones");
        // Cria um set de entidades TIPADO vazio.
        IEntitySet result = new EntitySet(new HashSet<Object>(), 
                                          this.serviceManager.getEntityManager().getEntityMetadata(Telefone.class),
                                          this.serviceManager.getEntityManager());
        
        // Pessoa pode possuir uma collection de telefones.
        if (!telefones.isEmpty())
        {
            for (Telefone tel : telefones)
            {
                IEntity tipoTelefone = manterTipoTelefone(tel.getTipoTelefone());
                IEntity telefone = UtilsCrud.create(this.serviceManager, Telefone.class, this.serviceData);

                manterPrimitiveProperties(telefone, tel);

                telefone.getProperty(Telefone.TIPO_TELEFONE).getValue().setAsEntity(tipoTelefone);
                
                UtilsCrud.update(this.serviceManager, telefone, this.serviceData);
                result.add(telefone);
            }
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public IEntity manterNecessidadeEspecial(NecessidadeEspecial necessidadeEspecial) throws BusinessException, HibernateException
    {
    	log.debug("ManterNecessidadeEspecial");
        if ((necessidadeEspecial!=null) && (necessidadeEspecial.getDescricao()!=null))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(NecessidadeEspecial.class);
            crit.add(Expression.eq(NecessidadeEspecial.DESCRICAO, necessidadeEspecial.getDescricao()));

            IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), NecessidadeEspecial.class);
            
            if (entityList.size() == 0)
            {
                IEntity necessidadeEspecialEntity = UtilsCrud.create(this.serviceManager, NecessidadeEspecial.class, this.serviceData);
                
                manterPrimitiveProperties(necessidadeEspecialEntity, necessidadeEspecial);

                UtilsCrud.update(this.serviceManager, necessidadeEspecialEntity, this.serviceData);
                return necessidadeEspecialEntity;
            }
            return entityList.get(0);
        }
        return null;
    }
    
    /** 
     *  
     */
    public IEntityList manterNecessidadesEspeciais(List<NecessidadeEspecial> necessidadesEspeciais) throws BusinessException, HibernateException
    {
    	log.debug("ManterNecessidadesEspeciais");
        // Cria uma list de entidades TIPADO vazio.
        IEntityList result = new EntityList(new ArrayList<Object>(), 
                                          this.serviceManager.getEntityManager().getEntityMetadata(NecessidadeEspecial.class),
                                          this.serviceManager.getEntityManager());
        
        // Pessoa pode possuir uma collection de telefones.
        if (!necessidadesEspeciais.isEmpty())
        {
            for (NecessidadeEspecial necessidadeEspecial: necessidadesEspeciais)
            {
            	IEntity necessidadeEspecialEntity = manterNecessidadeEspecial(necessidadeEspecial);
            	
            	if(necessidadeEspecialEntity != null)
            		result.add(necessidadeEspecialEntity);
            }
        }
        return result;
    }
    
    public IEntity manterContato(Contato contato) throws BusinessException, HibernateException
    {
    	log.debug("ManterContato");
        if (contato!=null)
        {
            IEntity contatoEntity = UtilsCrud.create(this.serviceManager, Contato.class, this.serviceData);
            IEntity cargo = manterCargo(contato.getCargo());

            manterPrimitiveProperties(contatoEntity, contato);

            contatoEntity.getProperty(Contato.CARGO).getValue().setAsEntity(cargo);
            
            UtilsCrud.update(this.serviceManager, contatoEntity, this.serviceData);
            return contatoEntity;
        }
        return null;
    }
    
    /** 
     * Verifica se um id foi passado para o cnae para então utilizar e localizar
     * Senão busca o cnae pelo nome
     * 
     */
    @SuppressWarnings("unchecked")
    public IEntity manterGrauParentesco(GrauParentesco grauParentesco) throws BusinessException, HibernateException
    {
    	log.debug("ManterGrauParentesco");
        if ((grauParentesco!=null) && (grauParentesco.getNome()!=null))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(GrauParentesco.class);
            crit.add(Expression.eq(GrauParentesco.NOME, grauParentesco.getNome()));
            	
            IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), GrauParentesco.class);
            
            if (entityList.size() == 0)
            {
                IEntity grauParentescoEntity = UtilsCrud.create(this.serviceManager, GrauParentesco.class, this.serviceData);

                manterPrimitiveProperties(grauParentescoEntity, grauParentesco);

                UtilsCrud.update(this.serviceManager, grauParentescoEntity, this.serviceData);
                return grauParentescoEntity;
            }
            return entityList.get(0);
        }
        return null;
    }
    
    /**
     * Todo responsável é único para uma pessoa  
     */
    public IEntity manterResponsavelCpf(ResponsavelCpf responsavelCpf) throws BusinessException, HibernateException
    {
    	log.debug("ManterReponsavelCpf");
        if (responsavelCpf!=null)
        {
            IEntity responsavelCpfEntity = UtilsCrud.create(this.serviceManager, ResponsavelCpf.class, this.serviceData);
            
            IEntity grauParentesco = manterGrauParentesco(responsavelCpf.getGrauParentesco());

            manterPrimitiveProperties(responsavelCpfEntity, responsavelCpf);

            responsavelCpfEntity.getProperty(ResponsavelCpf.GRAU_PARENTESCO).getValue().setAsEntity(grauParentesco);
            
            UtilsCrud.update(this.serviceManager, responsavelCpfEntity, this.serviceData);
            return responsavelCpfEntity;
        }
        return null;
    }
    
    /** 
     *  
     */
    public IEntityList manterEnderecos(List<Endereco> enderecos) throws BusinessException, HibernateException
    {
    	log.debug("ManterPessoaEndereco");
        // Cria uma list de entidades TIPADO vazio.
        IEntityList result = new EntityList(new ArrayList<Object>(), 
                                          this.serviceManager.getEntityManager().getEntityMetadata(Endereco.class),
                                          this.serviceManager.getEntityManager());
        
        // Pessoa pode possuir uma collection de telefones.
        if (enderecos!= null && !enderecos.isEmpty())
        {
            for (Endereco endereco: enderecos)
            {
            	IEntity enderecoEntity = manterEndereco(endereco);
            	
            	if(enderecoEntity != null)
            		result.add(enderecoEntity);
            }
        }
        return result;
    }
    
    @SuppressWarnings({"unchecked","static-access"})
    public IEntity manterPessoaFisica(Fisica fisica) throws BusinessException, HibernateException
    {
    	log.debug("ManterFisica");
        if ((fisica!=null) && (fisica.getNome()!=null))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(Fisica.class);
            crit.add(Expression.eq(Fisica.NOME, fisica.getNome()));
            if (fisica.getDocumento() != null)
                crit.add(Expression.eq(Fisica.DOCUMENTO, fisica.getDocumento()));
            IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Fisica.class);
            
            if (entityList.size() == 0)
            {
                IEntity fisicaEntity = UtilsCrud.create(this.serviceManager, Fisica.class, this.serviceData);

                manterPrimitiveProperties(fisicaEntity, fisica);

                IEntity profissao = manterProfissao(fisica.getProfissao());
                if (profissao!=null)
                    fisicaEntity.getProperty(Fisica.PROFISSAO).getValue().setAsEntity(profissao);
                
                IEntity naturalidade  = manterMunicipio(fisica.getNaturalidade());
                if (naturalidade!=null)
                    fisicaEntity.getProperty(Fisica.NATURALIDADE).getValue().setAsEntity(naturalidade);
                
                IEntity responsavelCpf = manterResponsavelCpf(fisica.getResponsavelCpf());
                if (responsavelCpf!=null)
                    fisicaEntity.getProperty(Fisica.RESPONSAVEL_CPF).getValue().setAsEntity(responsavelCpf);
                
                IEntityList necessidadesEspeciais = manterNecessidadesEspeciais(fisica.getNecessidadesEspeciais());
                if (necessidadesEspeciais!=null)
                    fisicaEntity.getProperty(Fisica.NECESSIDADES_ESPECIAIS).getValue().setAsEntityList(necessidadesEspeciais);
                
                /* Herdados de Pessoa.class */
                IEntityList enderecos = manterEnderecos(fisica.getEnderecos());
                if (enderecos!=null)
                    fisicaEntity.getProperty(Fisica.ENDERECOS).getValue().setAsEntityList(enderecos);
                
                IEntity endereco = manterEndereco(fisica.getEnderecoCorrespondencia());
                if (endereco!=null)
                    fisicaEntity.getProperty(Fisica.ENDERECO_CORRESPONDENCIA).getValue().setAsEntity(endereco);
                
                IEntitySet telefones = manterTelefones(fisica.getTelefones());
                if (telefones!=null)
                    fisicaEntity.getProperty(Fisica.TELEFONES).getValue().setAsEntitySet(telefones);
                
                UtilsCrud.update(this.serviceManager, fisicaEntity, this.serviceData);
                return fisicaEntity;
            }
            log.info("Pessoa Física já cadastrada. Usando referência para: " + entityList.get(0).toString());
            return entityList.get(0);
        }
        return null;
    }
    
    public IEntity manterPessoa(Pessoa pessoa) throws BusinessException, HibernateException
    {
    	log.debug("ManterPessoa");
        if ((pessoa!=null) && (pessoa.getNome()!=null))
        {
            if(pessoa.isFisica())
                return manterPessoaFisica(pessoa.getAsFisica());
            return manterPessoaJuridica(pessoa.getAsJuridica());
        }

        return null;
    }
    
    @SuppressWarnings({"static-access","unchecked"})
    public IEntity manterPessoaJuridica(Juridica juridica) throws BusinessException, HibernateException
    {
    	log.debug("ManterJuridica");
        if ((juridica!=null) && (juridica.getNome()!=null))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(Juridica.class);
            crit.add(Expression.eq(Juridica.NOME, juridica.getNome()));
            
            if (juridica.getDocumento()!=null)
                crit.add(Expression.eq(Juridica.DOCUMENTO, juridica.getDocumento()));
            
            IEntityList<Juridica> entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Juridica.class);
            
            if (entityList.size() == 0)
            {
                IEntity<Juridica> juridicaEntity = UtilsCrud.create(this.serviceManager, Juridica.class, this.serviceData);
  
                manterPrimitiveProperties(juridicaEntity, juridica);

                IEntity<CNAE> ramo = manterCnae(juridica.getCnae());
                if (ramo!=null)
                    juridicaEntity.getProperty(Juridica.CNAE).getValue().setAsEntity(ramo);
                
                IEntity<Contrato> contato = manterContato(juridica.getContato());
                if (contato!=null)
                    juridicaEntity.getProperty(Juridica.CONTATO).getValue().setAsEntity(contato);
                
                IEntitySet<Socio> socios = manterSocios(juridica.getSocios(), juridicaEntity.getObject());
                if (socios!=null)
                    juridicaEntity.getProperty(Juridica.SOCIOS).getValue().setAsEntitySet(socios);
                
                IEntity<EscritorioContabil> escritorioContabil = manterEscritorioContabil(juridica.getEscritorioContabil());
                if (escritorioContabil!=null)
                	juridicaEntity.getProperty(Juridica.ESCRITORIO_CONTABIL).getValue().setAsEntity(escritorioContabil);
                
                IEntity<Contador> contador = manterContador(juridica.getContador());
                if (contador!=null)
                	juridicaEntity.getProperty(Juridica.CONTADOR).getValue().setAsEntity(contador);
                
//                Quando era manter list, ele passa a lista que sera usada para manter
                IEntitySet<Funcionario> funcionarios = manterFuncionarios(juridica.getFuncionarios(), juridicaEntity.getObject());
                if (funcionarios!=null)
                	juridicaEntity.getProperty(Juridica.FUNCIONARIOS).getValue().setAsEntitySet(funcionarios);
                
                /* Herdados de Pessoa.class */
                IEntityList<Endereco> enderecos = manterEnderecos(juridica.getEnderecos());
                if (enderecos!=null)
                    juridicaEntity.getProperty(Fisica.ENDERECOS).getValue().setAsEntityList(enderecos);
                
                IEntity<Endereco> endereco = manterEndereco(juridica.getEnderecoCorrespondencia());
                if (endereco!=null)
                    juridicaEntity.getProperty(Fisica.ENDERECO_CORRESPONDENCIA).getValue().setAsEntity(endereco);
                
                IEntitySet<Telefone> telefones = manterTelefones(juridica.getTelefones());
                if (telefones!=null)
                    juridicaEntity.getProperty(Fisica.TELEFONES).getValue().setAsEntitySet(telefones);
                
                UtilsCrud.update(this.serviceManager, juridicaEntity, this.serviceData);
                
                return juridicaEntity;
            }
            log.info("Pessoa Jurídica já cadastrada. Usando referência para: " + entityList.get(0).toString());
            return entityList.get(0);
        } 
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public IEntity manterEscritorioContabil(EscritorioContabil escritorioContabil) throws BusinessException, HibernateException
    {
    	log.debug("ManterEscritorioContabil");
        if ((escritorioContabil!=null) && ((escritorioContabil.getId()!=IDAO.ENTITY_UNSAVED)||((escritorioContabil.getPessoa() != null) && (escritorioContabil.getPessoa().getNome()!=null))))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(EscritorioContabil.class);
            /* Verifica se um id foi passado para então utilizar e localizar */
            if(escritorioContabil.getId()!=IDAO.ENTITY_UNSAVED)
            	crit.add(Expression.eq(IDAO.PROPERTY_ID_NAME, escritorioContabil.getId()));
            else{
            	crit.createAlias(EscritorioContabil.PESSOA, "pessoa");
            	crit.add(Expression.eq("pessoa.nome", escritorioContabil.getPessoa().getNome()));
            	if (escritorioContabil.getPessoa().getDocumento()!=null)
            		crit.add(Expression.eq("pessoa.documento", escritorioContabil.getPessoa().getDocumento()));
            }
            
            IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), EscritorioContabil.class);
            
            if (entityList.size() == 0)
            {
                // pegando enderecoCorrespondencia porque enderecos é uma collection
                IEntity pessoa = manterPessoa(escritorioContabil.getPessoa());
                IEntity observacoes = manterObservacoes(escritorioContabil.getObservacoes());
                IEntitySet contadores = manterContadores(escritorioContabil.getContadores());

                IEntity escritorioEntity = UtilsCrud.create(this.serviceManager, EscritorioContabil.class, this.serviceData);
                                
                manterPrimitiveProperties(escritorioEntity, escritorioContabil);
                
//                escritorioEntity.getProperty(EscritorioContabil.DATA_CADASTRO).getValue().setAsCalendar(escritorioContabil.getDataCadastro());
                if (contadores!=null)
                    escritorioEntity .getProperty(EscritorioContabil.CONTADORES).getValue().setAsEntitySet(contadores);
                
                escritorioEntity.getProperty(EscritorioContabil.PESSOA).getValue().setAsEntity(pessoa);
                escritorioEntity.getProperty(EscritorioContabil.OBSERVACOES).getValue().setAsEntity(observacoes);

                UtilsCrud.update(this.serviceManager, escritorioEntity, this.serviceData);
                
                return escritorioEntity;
            }
            return entityList.get(0);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public IEntity manterContador(Contador contador) throws BusinessException, HibernateException
    {
    	log.debug("ManterContador");
        if ((contador!=null) && (contador.getFisica().getNome()!=null))
        {
            Criteria crit = this.serviceData.getCurrentSession().createCriteria(Contador.class);
            crit.createAlias(Contador.FISICA, "fisica");
            // poderia ser verificado o crc, mas no banco do sivamar não tem o numero
            crit.add(Expression.eq("fisica." + Fisica.NOME, contador.getFisica().getNome()));
            crit.add(Expression.eq("fisica." + Fisica.DOCUMENTO, contador.getFisica().getDocumento()));
            IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Contador.class);
            
            if (entityList.size() == 0)
            {
                // pegando enderecoCorrespondencia porque enderecos é uma collection
                IEntity fisica = manterPessoaFisica(contador.getFisica());
                IEntity observacoes = manterObservacoes(contador.getObservacoes());
                IEntity contadorEntity = UtilsCrud.create(this.serviceManager, Contador.class, this.serviceData);

                manterPrimitiveProperties(contadorEntity, contador);

                contadorEntity.getProperty(Contador.FISICA).getValue().setAsEntity(fisica);
                contadorEntity.getProperty(Contador.OBSERVACOES).getValue().setAsEntity(observacoes);
                
                UtilsCrud.update(this.serviceManager, contadorEntity, this.serviceData);
                
                return contadorEntity;
            }
            return entityList.get(0);
        }
        return null;
    }
    
    /**
     * Cada obervação é uma nova obervação gravada. 
     * 
     * @param observacoes
     * @return
     * @throws BusinessException
     */
    public IEntity manterObservacoes(Observacoes observacoes) throws BusinessException
    {
    	log.debug("ManterObservacoes");
        if ((observacoes!=null) && (observacoes.getObservacoes()!=null))
        {
            IEntity observacoesEntity = UtilsCrud.create(this.serviceManager, Observacoes.class, this.serviceData);
            
            manterPrimitiveProperties(observacoesEntity, observacoes);
            
            UtilsCrud.update(this.serviceManager, observacoesEntity, serviceData);
            
            return observacoesEntity;
        }
        return null;
    }
    
    /**
     * Para evitar a recurção Juridica->*Socio->Juridica
     * o que causaria manterJuridica{ manterSocios( manterJuridica .....))
     * Já é passa a qual juridica estes sócios pertencerão, que no 
     * caso esta Juridica foi instanciada e depois chamou este procedimento.
     * @param juridica
     * @return
     */
    public IEntitySet manterSocios(Set<Socio> socios, Juridica juridica) throws BusinessException, HibernateException
    {
    	log.debug("ManterSocios");
        // Cria um set de entidades TIPADO vazio.
        IEntitySet result = new EntitySet(new HashSet<Object>(), 
                                          this.serviceManager.getEntityManager().getEntityMetadata(Socio.class),
                                          this.serviceManager.getEntityManager());
        
        if (!socios.isEmpty())
        {
            for (Socio socio : socios)
            {
                IEntity socioEntity = manterSocio(socio, juridica);

                result.add(socioEntity);
            }
        }
        return result;
    }

    /**
     * É obrigatorio definir a Fisica e a Juridica o socio.
     * @param socio
     * @return
     * @throws BusinessException
     * @throws HibernateException
     */
    public IEntity manterSocio(Socio socio, Juridica oJuridica) throws BusinessException, HibernateException
    {
    	log.debug("ManterSocio");
        if (socio!=null && socio.getFisica()!=null)
        {
       		IEntity fisica = manterPessoaFisica(socio.getFisica());
        		
       		if(fisica != null && oJuridica!=null){
    			Criteria crit = this.serviceData.getCurrentSession().createCriteria(Socio.class);
    			crit.add(Expression.eq(Socio.FISICA, fisica.getObject()));
    			/* Se a Juridica ainda não foi salva, logo não deve ser procurada no banco*/
    			if(oJuridica.getId() != IDAO.ENTITY_UNSAVED)
    				crit.add(Expression.eq(Socio.JURIDICA, oJuridica));
    			
                IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Socio.class);
    			
    			if (entityList.size() == 0)
    			{
    				IEntity socioEntity = UtilsCrud.create(this.serviceManager, Socio.class, this.serviceData);
    				IEntity cargo = manterCargo(socio.getCargo());
    				
    				manterPrimitiveProperties(socioEntity, socio);
    				
    				socioEntity.getProperty(Socio.FISICA).getValue().setAsEntity(fisica);
    				socioEntity.getProperty(Socio.CARGO).getValue().setAsEntity(cargo);
    				
    				/* Este relacionamento deve ser mantido pelo lado ONE, ou seja, Juridica */
    				//socioEntity.getProperty(Socio.JURIDICA).getValue().setAsEntity(juridica);

    				UtilsCrud.update(this.serviceManager, socioEntity, this.serviceData);
    				return socioEntity;
    			}
    			
				return entityList.getFirst();
       		}
        }
        
        return null;
    }
    
    /**
     * É obrigao definir a Juridica e a Fisica do funcionário
     * @param funcionario
     * @return
     * @throws BusinessException
     * @throws HibernateException
     */
    public IEntity manterFuncionario(Funcionario funcionario, Juridica oJuridica) throws BusinessException, HibernateException
    {
    	log.debug("ManterFuncionario");
    	if ((funcionario!=null) && (funcionario.getFisica()!=null))
    	{
    		IEntity fisica = manterPessoaFisica(funcionario.getFisica());
    		IEntity juridica = this.serviceManager.getEntityManager().getEntity(oJuridica);
    		
    		if(fisica != null && juridica!=null){
    			
    			Criteria crit = this.serviceData.getCurrentSession().createCriteria(Funcionario.class);
    			crit.add(Expression.eq(Funcionario.FISICA, fisica.getObject()));

    			/* Se a Juridica ainda não foi salva, logo não deve ser procurada no banco*/
    			if(oJuridica.getId() != IDAO.ENTITY_UNSAVED)
    				crit.add(Expression.eq(Funcionario.JURIDICA, juridica.getObject()));
    			
    			IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Funcionario.class);
    			
    			if (entityList.size() == 0)
    			{
    				IEntity funcionarioEntity = UtilsCrud.create(this.serviceManager, Funcionario.class, this.serviceData);
    				
    				manterPrimitiveProperties(funcionarioEntity, funcionario);
    				
   					funcionarioEntity.getProperty(Funcionario.FISICA).getValue().setAsEntity(fisica);

    				/* Este relacionamento deve ser mantido pelo lado ONE, ou seja, Juridica */
//    				funcionarioEntity.getProperty(Funcionario.JURIDICA).getValue().setAsEntity(juridica);
    				
    				UtilsCrud.update(this.serviceManager, funcionarioEntity, this.serviceData);
    				return funcionarioEntity;
    			}
    			
    			return entityList.get(0);
    		}
    	}
    	return null;
    }

    public IEntitySet manterFuncionarios(Set<Funcionario> funcionarios, Juridica juridica) throws BusinessException, HibernateException
    {
    	log.debug("ManterFuncionarios");
        // Cria um set de entidades TIPADO vazio.
        IEntitySet result = new EntitySet(new HashSet<Object>(), 
                                          this.serviceManager.getEntityManager().getEntityMetadata(Funcionario.class),
                                          this.serviceManager.getEntityManager());
        
        // Pessoa pode possuir uma collection de telefones.
        if (!funcionarios.isEmpty())
        {
            for (Funcionario funcionario: funcionarios)
            {
                IEntity funcionarioEntity = manterFuncionario(funcionario, juridica);

                result.add(funcionarioEntity);
            }
        }
        return result;
    }
    
    public IEntitySet manterContadores(Set<Contador> contadores) throws BusinessException, HibernateException
    {
    	log.debug("ManterContadores");
        // Cria um set de entidades TIPADO vazio.
        IEntitySet result = new EntitySet(new HashSet<Object>(), 
                                          this.serviceManager.getEntityManager().getEntityMetadata(Contador.class),
                                          this.serviceManager.getEntityManager());
        
        // Pessoa pode possuir uma collection de telefones.
        if (!contadores.isEmpty())
        {
            for (Contador contador: contadores)
            {
                IEntity contadorEntity = manterContador(contador);

                result.add(contadorEntity);
            }
        }
        return result;
    }
    
    public IEntity manterGrupoRepresentante(GrupoRepresentante grupoRepresentante) throws BusinessException, HibernateException
    {
    	log.debug("ManterGrupoRepresentante");
        if ((grupoRepresentante!=null))
        {
        	if((grupoRepresentante!=null) && (grupoRepresentante.getNome()!=null)){
        		Criteria crit = this.serviceData.getCurrentSession().createCriteria(GrupoRepresentante.class);
        		crit.add(Expression.eq(GrupoRepresentante.NOME, grupoRepresentante.getNome()));
        		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), GrupoRepresentante.class);
        		
        		if (entityList.size() == 0)
        		{
        			IEntity grupoRepresentanteEntity = UtilsCrud.create(this.serviceManager, GrupoRepresentante.class, this.serviceData);
        			
        			manterPrimitiveProperties(grupoRepresentanteEntity, grupoRepresentante);
        			
        			UtilsCrud.update(this.serviceManager, grupoRepresentanteEntity, this.serviceData);
        			return grupoRepresentanteEntity;
        		}
        		return entityList.getFirst();
        	}
        }
        return null;
    }
    
    public IEntity manterRepresentante(Representante representante) throws BusinessException, HibernateException
    {
    	log.debug("ManterRepresentante");
    	if ((representante!=null))
    	{
    		Criteria crit = this.serviceData.getCurrentSession().createCriteria(Representante.class);
			IEntity fisica = null;
    		/* Verifica se um id foi passado para então utilizar e localizar */
    		if(representante.getId()!=IDAO.ENTITY_UNSAVED)
    			crit.add(Expression.eq(IDAO.PROPERTY_ID_NAME, representante.getId()));
    		else{

    			fisica = manterPessoaFisica(representante.getFisica());

    			if(fisica!=null){
    				crit.add(Expression.eq(Representante.FISICA, fisica.getObject()));
    			}
    		}
    		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Representante.class);

    		if (entityList.size() == 0)
    		{
    					IEntity representanteEntity = UtilsCrud.create(this.serviceManager, Representante.class, this.serviceData);

    					manterPrimitiveProperties(representanteEntity, representante);

    					IEntity grupoRepresentante = manterGrupoRepresentante(representante.getGrupoRepresentante());
    					representanteEntity.setPropertyValue(Representante.GRUPO_REPRESENTANTE, grupoRepresentante);

    					UtilsCrud.update(this.serviceManager, representanteEntity, this.serviceData);
    					representanteEntity.getProperty(Representante.FISICA).getValue().setAsEntity(fisica);

    					return representanteEntity;
    		}
    		return entityList.getFirst();
   		}
   		return null;
    }    
    
    /** Feriado e FeriadoRecesso são mantidos aqui */
    public IEntity manterFeriado(Feriado feriado) throws BusinessException, HibernateException
    {
    	log.debug("ManterFeriado");
        if ((feriado!=null))
        {
        	if((feriado!=null) && (feriado.getDescricao()!=null)){
        		Criteria crit = this.serviceData.getCurrentSession().createCriteria(Feriado.class);
        		crit.add(Expression.eq(Feriado.DESCRICAO, feriado.getDescricao()));
        		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Feriado.class);
        		
        		if (entityList.size() == 0)
        		{
        			if (feriado instanceof FeriadoRecesso) {
						IEntity feriadoRecessoEntity = UtilsCrud.create(this.serviceManager, FeriadoRecesso.class, this.serviceData);
						
						manterPrimitiveProperties(feriadoRecessoEntity, feriado);
						
						UtilsCrud.update(this.serviceManager, feriadoRecessoEntity, this.serviceData);
						return feriadoRecessoEntity;
					}else{
						IEntity feriadoEntity = UtilsCrud.create(this.serviceManager, Feriado.class, this.serviceData);
						
						manterPrimitiveProperties(feriadoEntity, feriado);
        			
						UtilsCrud.update(this.serviceManager, feriadoEntity, this.serviceData);
						return feriadoEntity;
					}
        		}
        		return entityList.getFirst();
        	}
        }
        return null;
    }
    
    /*=================*/
        
    public IEntity manterContrato(Contrato contrato) throws BusinessException, HibernateException {

    	log.debug("ManterContrato");
    	/* Lucio 20071201: Se a classe do contrato não for
    	 * de um contrato do módulo basic então tenta identificar o tipo contrato para localizar
    	 * o módulo, a classe manter e o método que mantem a classe.
    	 * Esta foi uma solução para evitar o acoplamento do módulo 
    	 * basic com seus utilizadores que estendes classes.
    	 * new br.com.orionsoft.basic.Manter(this.serviceManager, this.serviceData).manterContrato(lancamento.getContrato());
    	 * 
    	 */
    	if(contrato.getClass() != Contrato.class)
    		return this.manterClassFinding(contrato);
    	

    	/* Senão continua normalmente */
    	if ((contrato != null) && (contrato.getCodigo() != null)) {

    		Criteria crit = this.serviceData.getCurrentSession().createCriteria(Contrato.class);
    		crit.add(Expression.eq(Contrato.CODIGO, contrato.getCodigo()));

    		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), Contrato.class);

    		if (entityList.size() == 0) {
    			IEntity contratoEntity = UtilsCrud.create(this.serviceManager, Contrato.class, this.serviceData);

    			manterPrimitiveProperties(contratoEntity, contrato);

    			IEntity pessoa = manterPessoa(contrato.getPessoa());
    			if (pessoa != null)
    				contratoEntity.getProperty(Contrato.PESSOA).getValue().setAsEntity(pessoa);
    			
    			IEntity representante = manterRepresentante(contrato.getRepresentante());
    			if (representante != null)
    				contratoEntity.getProperty(Contrato.REPRESENTANTE).getValue().setAsEntity(representante);
    			
    			IEntity categoria = manterContratoCategoria(contrato.getCategoria());
    			if (categoria != null)
    				contratoEntity.getProperty(Contrato.CATEGORIA).getValue().setAsEntity(categoria);
    			
                IEntitySet contatos = manterContratoContatos(contrato.getContatos());
                if (contatos != null)
                	contratoEntity.getProperty(Contrato.CONTATOS).getValue().setAsEntitySet(contatos);
    			
    			IEntity observacoes = manterObservacoes(contrato.getObservacoes());
    			contratoEntity.getProperty(Contrato.OBSERVACOES).getValue().setAsEntity(observacoes);

    			UtilsCrud.update(this.serviceManager, contratoEntity, this.serviceData);
    			return contratoEntity;
    		}

    		return entityList.get(0);
    	}
		return null;
    }
    
    public IEntity manterContratoCategoria(ContratoCategoria contratoCategoria) throws BusinessException, HibernateException {

    	log.debug("ManterContratoCategoria");

    	if ((contratoCategoria != null) && (contratoCategoria.getNome() != null)) {

    		Criteria crit = this.serviceData.getCurrentSession().createCriteria(ContratoCategoria.class);
    		if(contratoCategoria.getId() != IDAO.ENTITY_UNSAVED)
        		crit.add(Expression.eq(IDAO.PROPERTY_ID_NAME, contratoCategoria.getId()));
    		else
    			crit.add(Expression.eq(ContratoCategoria.NOME, contratoCategoria.getNome()));

    		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), ContratoCategoria.class);

    		if (entityList.size() == 0) {
    			IEntity contratoCategoriaEntity = UtilsCrud.create(this.serviceManager, ContratoCategoria.class, this.serviceData);

    			manterPrimitiveProperties(contratoCategoriaEntity, contratoCategoria);
		
    			IEntity observacoes = manterObservacoes(contratoCategoria.getObservacoes());
    			contratoCategoriaEntity.getProperty(ContratoCategoria.OBSERVACOES).getValue().setAsEntity(observacoes);

    			UtilsCrud.update(this.serviceManager, contratoCategoriaEntity, this.serviceData);
    			return contratoCategoriaEntity;
    		}

    		return entityList.get(0);
    	}
		return null;
    }
    
    public IEntitySet manterContratoContatos(Set<ContratoContato> contratoContatos) throws BusinessException, HibernateException
    {
    	log.debug("ManterContratoContatos");
        // Cria um set de entidades TIPADO vazio.
        IEntitySet result = new EntitySet(new HashSet<Object>(), 
                                          this.serviceManager.getEntityManager().getEntityMetadata(ContratoContato.class),
                                          this.serviceManager.getEntityManager());
        
        if (!contratoContatos.isEmpty())
        {
            for (ContratoContato contratoContato: contratoContatos)
            {
                IEntity contratoContatoEntity = manterContratoContato(contratoContato);

                result.add(contratoContatoEntity);
            }
        }
        return result;
    }

    public IEntity manterContratoContato(ContratoContato contratoContato) throws BusinessException, HibernateException
    {
    	log.debug("ManterContratoContato");
    	
    	if ((contratoContato != null) && (contratoContato.getRepresentante()!=null))
    	{
    		Criteria crit = this.serviceData.getCurrentSession().createCriteria(ContratoContato.class);
    		crit.add(Expression.eq(ContratoContato.REPRESENTANTE, contratoContato.getRepresentante()));

    		IEntityList entityList = this.serviceManager.getEntityManager().getEntityList(crit.list(), ContratoContato.class);

    		if (entityList.size() == 0)
    		{
    			IEntity contratoContatoEntity = UtilsCrud.create(this.serviceManager, ContratoContato.class, this.serviceData);

    			manterPrimitiveProperties(contratoContatoEntity, contratoContato);
    			
    			IEntity motivo = manterContratoContatoMotivo(contratoContato.getMotivo());
                if (motivo != null)
                	contratoContatoEntity.getProperty(Contrato.CONTATOS).getValue().setAsEntity(motivo);

    			UtilsCrud.update(this.serviceManager, contratoContatoEntity, this.serviceData);
    			return contratoContatoEntity;
    		}
    		return entityList.get(0);
    	}
    	return null;
    }
    
    public IEntity manterContratoContatoMotivo(ContratoContatoMotivo contratoContatoMotivo) throws BusinessException
    {
    	log.debug("ManterContratoContatoMotivo");
    	
        if ((contratoContatoMotivo != null) && (contratoContatoMotivo.getDescricao() != null))
        {
            IEntity contratoContatoMotivoEntity = UtilsCrud.create(this.serviceManager, ContratoContatoMotivo.class, this.serviceData);
            
            manterPrimitiveProperties(contratoContatoMotivoEntity, contratoContatoMotivo);
            
            UtilsCrud.update(this.serviceManager, contratoContatoMotivoEntity, serviceData);
            
            return contratoContatoMotivoEntity;
        }
        return null;
    }
    


}
