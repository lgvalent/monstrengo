package br.com.orionsoft.basic.ws.site;

import javax.jws.WebMethod;
import javax.jws.WebParam;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.monstrengo.core.service.ServiceManager;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

//@WebService
public class BasicSiteWs{

	/**
	 * Permite verificar se a senha fornecida para o cpf ou cnpj é válida.
	 * A senha passada pode ser literal ou a MD5.
	 * Se a senha tiver 32 caracteres, sabe-se que é uma MD5.
	 * Senão é a literal que será convertida em MD5 para comparação.
	 * @param cpfCnpj
	 * @param senha
	 * @return
	 */
//	@WebMethod
	public boolean verificarSenha(@WebParam String cpfCnpj,
	                              @WebParam String senha){
		return true;
	}

	/**
	 * Atualiza a senha da pessoa no sistema. 
	 * @param cpfCnpj
	 * @param senha
	 * @return
	 */
//	@WebMethod
	public void atualizarSenha(@WebParam String cpfCnpj,
	                           @WebParam String novaSenha){
		
	}

	/**
	 * Atualiza a senha da pessoa no sistema. 
	 * @param cpfCnpj
	 * @param senha
	 * @return
	 */
	@WebMethod
	public ContratoWeb obterContrato(@WebParam String cpfCnpj){
		
		IEntityList<Contrato> contratos;
		try {
			ApplicationContext ctx = new FileSystemXmlApplicationContext("applicationContext.xml");
			ServiceManager serviceManager = (ServiceManager) ctx.getBean("ServiceManager");
			
			contratos = UtilsCrud.list(serviceManager, Contrato.class, "entity.pessoa.documento='"+ cpfCnpj + "'", null);
			Contrato contrato = contratos.get(0).getObject();
		    return new ContratoWeb(contrato);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	public static void main(String[] args) {
		BasicSiteWs bs = new BasicSiteWs();
		System.out.println(bs.obterContrato("00726581979"));
	}
}
