package br.com.orionsoft.basic.process;

import java.util.List;

import br.com.orionsoft.basic.entities.pessoa.Pessoa;
import br.com.orionsoft.basic.services.CriarNovaSenhaPessoaService;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.exception.MessageList;
import br.com.orionsoft.monstrengo.core.process.ProcessBasic;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

/**
 * Processo de alteração da senha de uma pessoa</p>
 *
 * <p><b>Procedimentos:</b>
 * <br>Definir o número do documento: <i>setNumeroDocumento(String)</i>
 * <br>Definir a senha atual: <i>setSenha(String)</i>
 * <br>Definir a nova senha: <i>setNovaSenha(String)</i>
 * <br>Definir a confirmação da nova senha: <i>setNovaSenhaConfirmacao(String)</i>
 * <br>Gravar as alterações por <i>runAlterar()</i>.
 *
 * @author bellincanta
 * @since 20072610
 *
 * @spring.bean id="AlterarSenhaPessoaProcess" destroy-method="finish" singleton="false"
 * @spring.property name="processManager" ref="ProcessManager"
 *
 */

public class AlterarSenhaPessoaProcess extends ProcessBasic {

	public static final String PROCESS_NAME = "AlterarSenhaPessoaProcess";

	public String getProcessName() {
		return PROCESS_NAME;
	}
	private String numeroDocumento;
	private String novaSenha;
	private String novaSenhaConfirmacao;

	public String getNumeroDocumento() {return numeroDocumento;}
	public void setNumeroDocumento(String numeroDocumento) {this.numeroDocumento = numeroDocumento;}

	public String getNovaSenha() {return novaSenha;}
	public void setNovaSenha(String novaSenha) {this.novaSenha = novaSenha;}

	public String getNovaSenhaConfirmacao() {return novaSenhaConfirmacao;}
	public void setNovaSenhaConfirmacao(String novaSenhaConfirmacao) {this.novaSenhaConfirmacao = novaSenhaConfirmacao;}

	public boolean runAlterar(){
		super.beforeRun();

		try {
			/* Verifica se o novo password e sua confirmação batem */
			if(!novaSenha.equals(novaSenhaConfirmacao))
				throw new ProcessException(MessageList.create(AlterarSenhaPessoaProcess.class, "ERRO_CONFIRM_PASSWORD"));

			List<Pessoa> pessoas = UtilsCrud.objectList(this.getProcessManager().getServiceManager(), Pessoa.class, Pessoa.DOCUMENTO + "='" + numeroDocumento + "'", null);
			
			if(pessoas.size()==0)
				throw new ProcessException(MessageList.create(AlterarSenhaPessoaProcess.class, "ERRO_DOCUMENTO_INVALIDO", numeroDocumento));
				
			if(pessoas.size()>1)
				throw new ProcessException(MessageList.create(AlterarSenhaPessoaProcess.class, "ERRO_DOCUMENTO_REPETIDO", numeroDocumento));
			
			ServiceData service = new ServiceData(CriarNovaSenhaPessoaService.SERVICE_NAME, null);
			service.getArgumentList().setProperty(CriarNovaSenhaPessoaService.IN_PESSOA, pessoas.get(0));
			service.getArgumentList().setProperty(CriarNovaSenhaPessoaService.IN_NOVA_SENHA, novaSenha);

			this.getProcessManager().getServiceManager().execute(service);

			// Exibe a mensagem sucesso
            this.getMessageList().addAll(service.getMessageList());
            
            return true;


		} catch (BusinessException e) {
			this.getMessageList().addAll(e.getErrorList());
		}
		
		return false;
	}

}
