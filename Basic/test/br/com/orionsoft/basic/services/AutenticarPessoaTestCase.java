package br.com.orionsoft.basic.services;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;

/**
 * Esse classe testa o relacionamento entre as classes pessoa, juridica e
 * fisica. Tentando verificar as dificuldades de se lidar com heran�a e n�o
 * associa��o.
 * 
 * @author marcia
 * 
 */
public class AutenticarPessoaTestCase extends ServiceBasicTest {

	@Test
	public void testRun() {
		try {
			String inNumeroDocumento = "00736581979";
			String inCodigoSeguranca = "teste";
			String inSenha = "123";
			Integer inOpcaoContratos = AutenticarPessoaService.OPCAO_CONTRATOS_TODOS;

			ServiceData sd = new ServiceData(AutenticarPessoaService.SERVICE_NAME, null);
			sd.getArgumentList().setProperty(AutenticarPessoaService.IN_NUMERO_DOCUMENTO, inNumeroDocumento);
			sd.getArgumentList().setProperty(AutenticarPessoaService.IN_CODIGO_SEGURANCA_OPT, inCodigoSeguranca);
			sd.getArgumentList().setProperty(AutenticarPessoaService.IN_SENHA, inSenha);
			sd.getArgumentList().setProperty(AutenticarPessoaService.IN_OPCAO_CONTRATOS_OPT, inOpcaoContratos);

			this.serviceManager.execute(sd);

			IEntityList<Object> list = sd.getFirstOutput();


			if(list.isEmpty())
				System.out.println("NENHUM CONTRATO ENCONTRATO");

			for(IEntity entity: list){
				UtilsTest.showEntityProperties(entity);
				System.out.println(entity.getObject().getClass().getName());
			}

		} catch (BusinessException e) {
			UtilsTest.showMessageList(e.getErrorList());

			Assert.assertTrue(false);
		}
	}

}
