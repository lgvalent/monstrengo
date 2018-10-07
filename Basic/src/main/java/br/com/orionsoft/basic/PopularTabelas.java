package br.com.orionsoft.basic;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.notification.RunNotifier;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.ContratoCategoria;
import br.com.orionsoft.basic.entities.Observacoes;
import br.com.orionsoft.basic.entities.endereco.Bairro;
import br.com.orionsoft.basic.entities.endereco.Endereco;
import br.com.orionsoft.basic.entities.endereco.EnderecoCategoria;
import br.com.orionsoft.basic.entities.endereco.Logradouro;
import br.com.orionsoft.basic.entities.endereco.Municipio;
import br.com.orionsoft.basic.entities.endereco.Telefone;
import br.com.orionsoft.basic.entities.endereco.TipoLogradouro;
import br.com.orionsoft.basic.entities.endereco.TipoTelefone;
import br.com.orionsoft.basic.entities.endereco.Uf;
import br.com.orionsoft.basic.entities.pessoa.CNAE;
import br.com.orionsoft.basic.entities.pessoa.Cargo;
import br.com.orionsoft.basic.entities.pessoa.Contador;
import br.com.orionsoft.basic.entities.pessoa.Contato;
import br.com.orionsoft.basic.entities.pessoa.EscritorioContabil;
import br.com.orionsoft.basic.entities.pessoa.EstadoCivil;
import br.com.orionsoft.basic.entities.pessoa.Fisica;
import br.com.orionsoft.basic.entities.pessoa.Juridica;
import br.com.orionsoft.basic.entities.pessoa.Profissao;
import br.com.orionsoft.basic.entities.pessoa.Socio;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.entity.IEntitySet;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Esta classe cria as diversas entidades do módulo para que os testes do módulo
 * possam ser executados sobre dados válidos.
 * 
 * @author lucio 20070910
 *
 */
public class PopularTabelas extends br.com.orionsoft.monstrengo.PopularTabelas{

	/**
	 * Permite que esta classe seja executada diretamente por linha de comando.
	 * 
	 * @param args
	 * @throws InitializationError
	 */
	public static void main(String[] args) throws InitializationError
	{
		new TestClassRunner(PopularTabelas.class).run(new RunNotifier());
	}
	
	@Test
	public void popular() {
		try {
			/* Executa primeiro a população do módulo framework */
			super.popular();
			
			retrieveContrato();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
	
	public IEntity<ContratoCategoria> retrieveContratoCategoria() throws Exception {
		IEntity<ContratoCategoria> entity = UtilsCrud.create(this.serviceManager, ContratoCategoria.class, null);
		entity.setPropertyValue(ContratoCategoria.NOME, "Contrato básico");
		UtilsCrud.update(this.serviceManager, entity, null);
		return entity;
	}
	
	public IEntity<Contrato> retrieveContrato() throws Exception {
		IEntity<Contrato> entity = UtilsCrud.create(this.serviceManager, Contrato.class, null);
		entity.setPropertyValue(Contrato.CATEGORIA, retrieveContratoCategoria());
		entity.setPropertyValue(Contrato.DATA_INICIO, CalendarUtils.getCalendar(2007, Calendar.JANUARY, 1));
		entity.setPropertyValue(Contrato.PESSOA, retrievePessoaJuridica());
		UtilsCrud.update(this.serviceManager, entity, null);
		return entity;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IEntity<Juridica> retrievePessoaJuridica()throws Exception{  
		IEntity<Juridica> juridica = UtilsCrud.create(this.serviceManager, Juridica.class, null); 
		juridica.setPropertyValue(Juridica.NOME, "PessoaTeste"); 
		juridica.setPropertyValue(Juridica.APELIDO, "PessoaTeste"); 
		juridica.setPropertyValue(Juridica.DATA_CADASTRO, CalendarUtils.getCalendar("01/01/2007")); 
		juridica.setPropertyValue(Juridica.DATA_FINAL, CalendarUtils.getCalendar("01/05/2007")); 
		juridica.setPropertyValue(Juridica.DATA_INICIAL, CalendarUtils.getCalendar("01/01/2007")); 
		juridica.setPropertyValue(Juridica.DOCUMENTO, "02332390000122"); 
		juridica.setPropertyValue(Juridica.EMAIL, "tescase@tescase.com"); 
		juridica.setPropertyValue(Juridica.ENDERECO_CORRESPONDENCIA,retrieveEndereco());
		juridica.setPropertyValue(Juridica.WWW, "www.bsvti.com.br");
		juridica.setPropertyValue(Juridica.TELEFONES, retrieveTelefones());
		IEntityList enderecos = retrieveEnderecos();
		juridica.getProperty(Juridica.ENDERECOS).getValue().getAsEntityList().addAll(enderecos);		
		juridica.setPropertyValue(Juridica.IE,"784751445");
		juridica.setPropertyValue(Juridica.TIPO_ESTABELECIMENTO,"Atacadista");
		juridica.setPropertyValue(Juridica.CNAE, retrieveCnae());
	    juridica.setPropertyValue(Juridica.CONTATO, retrieveContato());
	/*  juridica.setPropertyValue(Juridica.SOCIOS, retrieveSocio());
	    juridica.setPropertyValue(Juridica.ESCRITORIO_CONTABIL, retrieveEscritorioContabil());*/
	    juridica.setPropertyValue(Juridica.CONTADOR, retrieveContador());
	    juridica.setPropertyValue(Juridica.CMC, "58745");
	    juridica.setPropertyValue(Juridica.NUMERO_FUNCIONARIOS, 15);
	    juridica.setPropertyValue(Juridica.CAPITAL_SOCIAL, new BigDecimal(150000));
		UtilsCrud.update(this.serviceManager, juridica, null);
		return juridica;
	}
	
	public IEntity<Fisica> retrievePessoaFisica()throws Exception{  
		IEntity<Fisica> fisica = UtilsCrud.create(this.serviceManager, Fisica.class, null); 
		fisica.setPropertyValue(Fisica.NOME, "PessoaTeste"); 
		fisica.setPropertyValue(Fisica.APELIDO, "PessoaTeste"); 
		fisica.setPropertyValue(Fisica.DATA_CADASTRO, CalendarUtils.getCalendar("01/01/2007")); 
		fisica.setPropertyValue(Fisica.DATA_FINAL, CalendarUtils.getCalendar("01/05/2007")); 
		fisica.setPropertyValue(Fisica.DATA_INICIAL, CalendarUtils.getCalendar("01/01/2007"));
		fisica.setPropertyValue(Fisica.DOCUMENTO, "03509749936");
		fisica.setPropertyValue(Fisica.EMAIL, "tescase@tescase.com");  
		fisica.setPropertyValue(Fisica.WWW, "www.bsvti.com.br");
		fisica.setPropertyValue(Fisica.ENDERECO_CORRESPONDENCIA, retrieveEndereco());
		fisica.setPropertyValue(Fisica.TELEFONES, retrieveTelefones());
		fisica.setPropertyValue(Fisica.ENDERECOS, retrieveEnderecos());		
		fisica.setPropertyValue(Fisica.RG_UF_EXPEDIDOR, Uf.PR);
		fisica.setPropertyValue(Fisica.RG_NUMERO,"784751445");
		fisica.setPropertyValue(Fisica.RG_ORGAO_EXPEDIDOR,"SSP");
		fisica.setPropertyValue(Fisica.RG_DATA_EXPEDICAO, CalendarUtils.getCalendar("06/05/2001"));
		fisica.setPropertyValue(Fisica.PIS_PASEP, "870001");
		fisica.setPropertyValue(Fisica.ESTADO_CIVIL, EstadoCivil.DIVORCIADO);
		fisica.setPropertyValue(Fisica.PROFISSAO, retrieveProfissao());
		fisica.setPropertyValue(Fisica.SEXO, "M");
		fisica.setPropertyValue(Fisica.NATURALIDADE, retrieveMunicipio());
		UtilsCrud.update(this.serviceManager, fisica, null);
		return fisica;
	}
	
	public IEntityList<Endereco> retrieveEnderecos()throws Exception{  

		IEntity<EnderecoCategoria> enderecoCategoria = UtilsCrud.create(this.serviceManager, EnderecoCategoria.class, null);
		enderecoCategoria.setPropertyValue(EnderecoCategoria.NOME, "Cobrança");
		UtilsCrud.update(this.serviceManager, enderecoCategoria, null);
		
		IEntity<Endereco> endereco = UtilsCrud.create(this.serviceManager, Endereco.class, null);
		endereco.setPropertyValue(Endereco.NUMERO, 53);
		endereco.setPropertyValue(Endereco.COMPLEMENTO, "Comercial");
		endereco.setPropertyValue(Endereco.CAIXA_POSTAL, "12124");
		endereco.setPropertyValue(Endereco.LOGRADOURO, retrieveLogradouro());
		endereco.setPropertyValue(Endereco.BAIRRO, retrieveBairro());
		endereco.setPropertyValue(Endereco.CEP, "87030020");
		endereco.setPropertyValue(Endereco.MUNICIPIO, retrieveMunicipio());
		endereco.setPropertyValue(Endereco.CATEGORIA, enderecoCategoria);
		UtilsCrud.update(this.serviceManager, endereco, null);
		
				
		
		IEntityList<Endereco> list = this.serviceManager.getEntityManager().<Endereco>getEntityList(null, Endereco.class);
		list.add(endereco);
		
		return list;
	}
	public IEntity<Endereco> retrieveEndereco()throws Exception{  
		IEntity<Endereco> endereco = UtilsCrud.create(this.serviceManager, Endereco.class, null);
		endereco.setPropertyValue(Endereco.NUMERO, 53);
		endereco.setPropertyValue(Endereco.COMPLEMENTO, "Comercial");
		endereco.setPropertyValue(Endereco.CAIXA_POSTAL, "12124");
		endereco.setPropertyValue(Endereco.LOGRADOURO, retrieveLogradouro());
		endereco.setPropertyValue(Endereco.BAIRRO, retrieveBairro());
		endereco.setPropertyValue(Endereco.CEP, "87030020");
		endereco.setPropertyValue(Endereco.MUNICIPIO, retrieveMunicipio());
		UtilsCrud.update(this.serviceManager, endereco, null);
		return endereco;
	}
	public IEntity<Municipio> retrieveMunicipio () throws Exception{ 
		IEntity<Municipio> municipio = UtilsCrud.create(this.serviceManager, Municipio.class, null);
		municipio.setPropertyValue(Municipio.NOME, "Maringá");
		municipio.setPropertyValue(Municipio.UF, Uf.PR);
		UtilsCrud.update(this.serviceManager, municipio, null);
		return municipio;
	}
	public IEntity<Bairro> retrieveBairro()throws Exception{
		IEntity<Bairro> bairro = UtilsCrud.create(this.serviceManager, Bairro.class,null);
		bairro.setPropertyValue(Bairro.NOME, "Centro");
		UtilsCrud.update(this.serviceManager, bairro, null);
		return bairro;
	}
	public IEntity<Logradouro> retrieveLogradouro() throws Exception{
		IEntity<Logradouro> logradouro = UtilsCrud.create(this.serviceManager, Logradouro.class, null);
		logradouro.setPropertyValue(Logradouro.NOME, "Brasil");
		logradouro.setPropertyValue(Logradouro.TIPO_LOGRADOURO, TipoLogradouro.AVENIDA);
		UtilsCrud.update(this.serviceManager, logradouro, null);
		return logradouro;
	}
	public IEntity<TipoTelefone> retrieveTipoTelefone()throws Exception{
		IEntity<TipoTelefone> tipoTelefone = UtilsCrud.create(this.serviceManager, TipoTelefone.class, null);
		tipoTelefone.setPropertyValue(TipoTelefone.NOME, "Residencial");
		UtilsCrud.update(this.serviceManager, tipoTelefone, null);
		return tipoTelefone;
	}
	
	public IEntitySet<Telefone> retrieveTelefones() throws Exception{
		IEntitySet<Telefone> set = this.serviceManager.getEntityManager().getEntitySet(null, Telefone.class);
		IEntity<Telefone> telefone = UtilsCrud.create(this.serviceManager, Telefone.class, null);
		telefone.setPropertyValue(Telefone.DDD, "44");
		telefone.setPropertyValue(Telefone.NUMERO, "44557744");
		telefone.setPropertyValue(Telefone.RAMAL, "2145");
		telefone.setPropertyValue(Telefone.TIPO_TELEFONE, retrieveTipoTelefone());
		UtilsCrud.update(this.serviceManager, telefone, null);
		set.add(telefone);
		return set;
	}
	public IEntity<CNAE> retrieveCnae()throws Exception{
		IEntity<CNAE> cnae = UtilsCrud.create(this.serviceManager, CNAE.class, null);
		cnae.setPropertyValue(CNAE.CODIGO, "124514");
		cnae.setPropertyValue(CNAE.NOME, "test do nome do cnae");
		UtilsCrud.update(this.serviceManager, cnae, null);
		return cnae;
	}
	public IEntity<Cargo> retrieveCargo()throws Exception{
		IEntity<Cargo> cargo = UtilsCrud.create(this.serviceManager, Cargo.class, null);
		cargo.setPropertyValue(Cargo.NOME, "Diretor");
		UtilsCrud.update(this.serviceManager, cargo, null);
		return cargo;
	}
	public IEntity<Contato> retrieveContato()throws Exception{
		IEntity<Contato> contato = UtilsCrud.create(this.serviceManager,Contato.class,null);
		contato.setPropertyValue(Contato.NOME_CONTATO, "Luiz Fernando da Silva");
		contato.setPropertyValue(Contato.CARGO,retrieveCargo());
		UtilsCrud.update(this.serviceManager, contato, null);
		return contato;
	}
	
	public IEntitySet<Socio> retrieveSocio()throws Exception{
		IEntitySet<Socio> set = this.serviceManager.getEntityManager().getEntitySet(null, Socio.class);
		IEntity<Socio> socio = UtilsCrud.create(this.serviceManager, Socio.class, null);
		socio.setPropertyValue(Socio.CARGO, retrieveCargo());
		socio.setPropertyValue(Socio.DATA_ENTRADA, CalendarUtils.getCalendar("01/01/2000"));
		socio.setPropertyValue(Socio.DATA_SAIDA, CalendarUtils.getCalendar("01/01/2007"));
		socio.setPropertyValue(Socio.FISICA, retrievePessoaFisicaSocio());
		UtilsCrud.update(this.serviceManager, socio, null);
		set.add(socio);
		return set;
	}
	public IEntity<Observacoes> retrieveObservacoes()throws Exception{
		IEntity<Observacoes> observacoes = UtilsCrud.create(this.serviceManager, Observacoes.class, null);
		observacoes.setPropertyValue(Observacoes.OBSERVACOES, "Observacao do testCase");
		observacoes.setPropertyValue(Observacoes.EXIBIR, true);
		UtilsCrud.update(this.serviceManager, observacoes, null);
		return observacoes;
	}
	public IEntity<EscritorioContabil> retrieveEscritorioContabil()throws Exception{
		IEntity<EscritorioContabil> escritorioContabil = UtilsCrud.create(this.serviceManager, EscritorioContabil.class, null);
		escritorioContabil.setPropertyValue(EscritorioContabil.PESSOA, retrievePessoaEscritorio());
		escritorioContabil.setPropertyValue(EscritorioContabil.DATA_CADASTRO, CalendarUtils.getCalendar("09/07/2007"));
		escritorioContabil.setPropertyValue(EscritorioContabil.OBSERVACOES, retrieveObservacoes());
		UtilsCrud.update(this.serviceManager, escritorioContabil, null);
		return escritorioContabil;
	}
	public IEntity<Contador> retrieveContador()throws Exception{
		IEntity<Contador> contador = UtilsCrud.create(this.serviceManager, Contador.class, null);
		contador.setPropertyValue(Contador.ESCRITORIO_CONTABIL, retrieveEscritorioContabil());
		contador.setPropertyValue(Contador.FISICA, retrievePessoaFisicaContador());
		contador.setPropertyValue(Contador.CRC, "PR123456A1");
		contador.setPropertyValue(Contador.DATA_CADASTRO,CalendarUtils.getCalendar("01/01/2007"));
		contador.setPropertyValue(Contador.OBSERVACOES, retrieveObservacoes());
		UtilsCrud.update(this.serviceManager, contador, null);
		return contador;
	}
	public IEntity<Profissao> retrieveProfissao()throws Exception{
		IEntity<Profissao> profissao = UtilsCrud.create(this.serviceManager, Profissao.class, null);
		profissao.setPropertyValue(Profissao.CODIGO, "1234");
		profissao.setPropertyValue(Profissao.NOME, "Contador");
		UtilsCrud.update(this.serviceManager, profissao, null);
		return profissao;
	}

	public IEntity<Juridica> retrievePessoaEscritorio()throws Exception{  
		IEntity<Juridica> juridica = UtilsCrud.create(this.serviceManager, Juridica.class, null); 
		juridica.setPropertyValue(Juridica.NOME, "PessoaEscritotioTeste"); 
		juridica.setPropertyValue(Juridica.APELIDO, "PessoaEscritotioTeste"); 
		juridica.setPropertyValue(Juridica.DATA_CADASTRO, CalendarUtils.getCalendar("01/01/2007")); 
		juridica.setPropertyValue(Juridica.DATA_FINAL, CalendarUtils.getCalendar("01/05/2007")); 
		juridica.setPropertyValue(Juridica.DATA_INICIAL, CalendarUtils.getCalendar("01/01/2007")); 
		juridica.setPropertyValue(Juridica.DOCUMENTO, "02332390000122"); 
		juridica.setPropertyValue(Juridica.EMAIL, "tescase@tescase.com"); 
		juridica.setPropertyValue(Juridica.ENDERECO_CORRESPONDENCIA,retrieveEndereco());
		juridica.setPropertyValue(Juridica.WWW, "www.bsvti.com.br");
		juridica.setPropertyValue(Juridica.TELEFONES, retrieveTelefones());
		juridica.setPropertyValue(Juridica.IE,"784751445");
		juridica.setPropertyValue(Juridica.TIPO_ESTABELECIMENTO,"Principal");
		juridica.setPropertyValue(Juridica.CNAE, retrieveCnae());
	    juridica.setPropertyValue(Juridica.CMC, "1234");
	    juridica.setPropertyValue(Juridica.CAPITAL_SOCIAL, new BigDecimal(150000));
		UtilsCrud.update(this.serviceManager, juridica, null);
		return juridica;
	}
	public IEntity<Fisica> retrievePessoaFisicaContador()throws Exception{  
		IEntity<Fisica> fisica = UtilsCrud.create(this.serviceManager, Fisica.class, null); 
		fisica.setPropertyValue(Fisica.NOME, "PessoaTeste"); 
		fisica.setPropertyValue(Fisica.APELIDO, "PessoaTeste"); 
		fisica.setPropertyValue(Fisica.DATA_CADASTRO, CalendarUtils.getCalendar("01/01/2007")); 
		fisica.setPropertyValue(Fisica.DATA_FINAL, CalendarUtils.getCalendar("01/05/2007")); 
		fisica.setPropertyValue(Fisica.DATA_INICIAL, CalendarUtils.getCalendar("01/01/2007"));
		fisica.setPropertyValue(Fisica.DOCUMENTO, "00736581979");
		fisica.setPropertyValue(Fisica.EMAIL, "tescase@tescase.com");  
		fisica.setPropertyValue(Fisica.WWW, "www.bsvti.com.br");
		fisica.setPropertyValue(Fisica.ENDERECO_CORRESPONDENCIA, retrieveEndereco());
		fisica.setPropertyValue(Fisica.TELEFONES, retrieveTelefones());
		fisica.setPropertyValue(Fisica.ENDERECOS, retrieveEnderecos());		
		fisica.setPropertyValue(Fisica.RG_UF_EXPEDIDOR, Uf.PR);
		fisica.setPropertyValue(Fisica.RG_NUMERO,"784751445");
		fisica.setPropertyValue(Fisica.RG_ORGAO_EXPEDIDOR,"SSP");
		fisica.setPropertyValue(Fisica.RG_DATA_EXPEDICAO, CalendarUtils.getCalendar("06/05/2001"));
		fisica.setPropertyValue(Fisica.PIS_PASEP, "870001");
		fisica.setPropertyValue(Fisica.ESTADO_CIVIL, EstadoCivil.DIVORCIADO);
		fisica.setPropertyValue(Fisica.PROFISSAO, retrieveProfissao());
		fisica.setPropertyValue(Fisica.SEXO, "M");
		fisica.setPropertyValue(Fisica.NATURALIDADE, retrieveMunicipio());
		UtilsCrud.update(this.serviceManager, fisica, null);
		return fisica;
	}
	public IEntity<Fisica> retrievePessoaFisicaSocio()throws Exception{  
		IEntity<Fisica> fisica = UtilsCrud.create(this.serviceManager, Fisica.class, null); 
		fisica.setPropertyValue(Fisica.NOME, "PessoaTeste"); 
		fisica.setPropertyValue(Fisica.APELIDO, "PessoaTeste"); 
		fisica.setPropertyValue(Fisica.DATA_CADASTRO, CalendarUtils.getCalendar("01/01/2007")); 
		fisica.setPropertyValue(Fisica.DATA_FINAL, CalendarUtils.getCalendar("01/05/2007")); 
		fisica.setPropertyValue(Fisica.DATA_INICIAL, CalendarUtils.getCalendar("01/01/2007"));
		fisica.setPropertyValue(Fisica.DOCUMENTO, "00000000191");
		fisica.setPropertyValue(Fisica.EMAIL, "tescase@tescase.com");  
		fisica.setPropertyValue(Fisica.WWW, "www.bsvti.com.br");
		fisica.setPropertyValue(Fisica.ENDERECO_CORRESPONDENCIA, retrieveEndereco());
		fisica.setPropertyValue(Fisica.TELEFONES, retrieveTelefones());
		fisica.setPropertyValue(Fisica.ENDERECOS, retrieveEnderecos());		
		fisica.setPropertyValue(Fisica.RG_UF_EXPEDIDOR, Uf.PR);
		fisica.setPropertyValue(Fisica.RG_NUMERO,"784751445");
		fisica.setPropertyValue(Fisica.RG_ORGAO_EXPEDIDOR,"SSP");
		fisica.setPropertyValue(Fisica.RG_DATA_EXPEDICAO, CalendarUtils.getCalendar("06/05/2001"));
		fisica.setPropertyValue(Fisica.PIS_PASEP, "870001");
		fisica.setPropertyValue(Fisica.ESTADO_CIVIL, EstadoCivil.DIVORCIADO);
		fisica.setPropertyValue(Fisica.PROFISSAO, retrieveProfissao());
		fisica.setPropertyValue(Fisica.SEXO, "M");
		fisica.setPropertyValue(Fisica.NATURALIDADE, retrieveMunicipio());
		UtilsCrud.update(this.serviceManager, fisica, null);
		return fisica;
	}	
}
