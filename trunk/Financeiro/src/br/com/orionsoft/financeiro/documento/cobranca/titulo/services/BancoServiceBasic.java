package br.com.orionsoft.financeiro.documento.cobranca.titulo.services;


import org.apache.commons.lang.StringUtils;

import br.com.orionsoft.financeiro.documento.cobranca.IProvedorDocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.services.BaixarDocumentoCobrancaService;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.Cedente;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.GerenciadorDocumentoTitulo;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.IGerenciadorBanco;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.IProvedorBanco;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.ProvedorBanco;
import br.com.orionsoft.financeiro.gerenciador.entities.Banco;
import br.com.orionsoft.financeiro.gerenciador.entities.ContaBancaria;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.service.ServiceBasic;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;


/**
 * <p>Este classe implementa as funcionalidades básicas de qualquer
 * serviço que venha a utilizar o provedor de bancos e seus gerenciadores
 * (756, etc)
 *
 * @author Andre
 *
 */
public abstract class BancoServiceBasic extends ServiceBasic implements IBancoService
{
	private static IProvedorBanco provedorBanco = null;

	public IProvedorBanco getProvedorBanco() {return BancoServiceBasic.provedorBanco;}
	public void setProvedorBanco(IProvedorBanco provedorBanco) {BancoServiceBasic.provedorBanco = provedorBanco;}
	
	@Override
	public void registerService() throws Exception {
		super.registerService();
		
    	if(provedorBanco == null){
    		/* Pega a estrutura de gerenciadores e provedores já definidas nos serviços mais genéricos de documento de cobrança */
    		BaixarDocumentoCobrancaService service =  (BaixarDocumentoCobrancaService) super.getServiceManager().getServiceByName(BaixarDocumentoCobrancaService.SERVICE_NAME);
    		
    		GerenciadorDocumentoTitulo gerenciador = (GerenciadorDocumentoTitulo) service.getProvedorDocumentoCobranca().retrieveGerenciadorDocumentoCobranca(GerenciadorDocumentoTitulo.GERENCIADOR_NOME);
    		provedorBanco = gerenciador.getProvedorBanco();
    	}
	}
	
	/**
	 * Obtem o gerenciador de banco de um determinado cedente. 
	 * @param cedente
	 * @return
	 * @throws BusinessException 
	 */
	public IGerenciadorBanco retrieveGerenciadorBanco(IEntity cedente) throws BusinessException{
		log.debug("Obtendo o nome do Gerenciador de Banco associado ao convênio passado");
		String codigoBanco = null;
		if(cedente!= null)
			codigoBanco = cedente.getProperty(Cedente.CONTA_BANCARIA).getValue().getAsEntity().getProperty(ContaBancaria.BANCO).getValue().getAsEntity().getProperty(Banco.CODIGO).getValue().getAsString();
		
		log.debug("Verificando se foi possível achar o gerenciador responsável");
		if(StringUtils.isEmpty(codigoBanco))
			throw new ServiceException(MessageList.create(BancoServiceBasic.class, "GERENCIADOR_NAO_DEFINIDO"));

		log.debug("Obtendo junto ao ProvedorBanco o gerenciador responsável");
		return this.getProvedorBanco().retrieveGerenciadorBanco(codigoBanco);
	}

 }
