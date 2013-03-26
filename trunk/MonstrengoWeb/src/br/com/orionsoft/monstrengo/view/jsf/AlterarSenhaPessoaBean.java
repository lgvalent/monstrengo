package br.com.orionsoft.monstrengo.view.jsf;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.orionsoft.basic.process.AlterarSenhaPessoaProcess;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.process.ProcessException;
import br.com.orionsoft.monstrengo.view.jsf.bean.BeanSessionBasic;
import br.com.orionsoft.monstrengo.view.jsf.util.FacesUtils;

/**
 * Bean que controla a view do alteracao da senha da pessao
 *
 * @author bellincanta
 * @since 20070925
 */

@ManagedBean
@SessionScoped
public class AlterarSenhaPessoaBean extends BeanSessionBasic{

	/** Define a view JSF que é ativada para cada view */
	public static final String FACES_VIEW_PASSO_1 = "basic/alterarSenhaPessoa";

	private String numeroDocumento;
	private String novaSenha;
	private String novaSenhaConfirmacao;

	public String actionStart() {
		numeroDocumento = "";
		novaSenha = "";
		novaSenhaConfirmacao = "";

		return FACES_VIEW_PASSO_1;
	}

	public String actionAlterar() {
		String result = "";
		try {
			AlterarSenhaPessoaProcess process = (AlterarSenhaPessoaProcess) getApplicationBean().getProcessManager().createProcessByName(AlterarSenhaPessoaProcess.PROCESS_NAME,null);

			process.setNumeroDocumento(numeroDocumento);
			process.setNovaSenha(novaSenha);
			process.setNovaSenhaConfirmacao(novaSenhaConfirmacao);

			if(process.runAlterar()){
				result = FacesUtils.FACES_VIEW_SUCCESS;
			}else{
				result = FacesUtils.FACES_VIEW_FAILURE;
			}

			FacesUtils.addErrorMsgs(process.getMessageList());

			process.finish();

		} catch (ProcessException e) {
			FacesUtils.addErrorMsgs(e.getErrorList());
			result = FacesUtils.FACES_VIEW_FAILURE;
		}

		return result;
	}

	public void doReload() throws BusinessException, Exception {
		// TODO Auto-generated method stub
	}
	public void doReset() throws BusinessException, Exception {
		// TODO Auto-generated method stub
	}
	public String getNovaSenha() {return novaSenha;}
	public void setNovaSenha(String novaSenha) {this.novaSenha = novaSenha;}

	public String getNumeroDocumento() {return numeroDocumento;}
	public void setNumeroDocumento(String numeroDocumento) {this.numeroDocumento = numeroDocumento;}

	public String getNovaSenhaConfirmacao() {return novaSenhaConfirmacao;}
	public void setNovaSenhaConfirmacao(String confirmarSenhaNova) {this.novaSenhaConfirmacao = confirmarSenhaNova;}
}
