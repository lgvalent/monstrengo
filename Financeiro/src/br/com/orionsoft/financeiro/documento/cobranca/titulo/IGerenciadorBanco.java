package br.com.orionsoft.financeiro.documento.cobranca.titulo;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import br.com.orionsoft.financeiro.documento.cobranca.ConvenioCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobranca;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaException;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoResultado;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;

/**
 * Esta Interface define as funcionalidades de um gerenciador de documento.
 * 
 * @version 20060601
 *
 */
public interface IGerenciadorBanco {
   public String getCampoLivre(IEntity<? extends DocumentoCobranca> documento) throws DocumentoCobrancaException;
   
   public String getCodigoBarras(IEntity<? extends DocumentoCobranca> documento) throws DocumentoCobrancaException;
   
   public String getLinhaDigitavel(IEntity<? extends DocumentoCobranca> documento) throws DocumentoCobrancaException;

   public String formatarAgenciaCedente(IEntity<? extends DocumentoCobranca> documento) throws DocumentoCobrancaException;
   
   public String formatarNossoNumero(IEntity<? extends DocumentoCobranca> documento,  ServiceData serviceDataOwner) throws DocumentoCobrancaException;
   
   /*
    * Caso a remessa não use protesto, deixar o campo 'quantidadeDiasProtesto' null
    */
   public File gerarRemessa(IEntity<? extends ConvenioCobranca> convenioCobranca, Calendar inicioPeriodo, Calendar finalPeriodo, Integer quantidadeDiasProtesto, ServiceData serviceDataOwner) throws DocumentoCobrancaException;   

   /* Cedente é necessário para saber se o layout do arquivo de retorno é CNAB240 ou CNAB400
    * O arquivo vindo da interface é um InputStream, mas já é tratado retornando um File, pois podemos precisar do nome do arquivo, 
    * o que não é possível obter com o InputStream 
    */  
   public List<DocumentoRetornoResultado> receberRetorno(IEntity<? extends ConvenioCobranca> convenioCobranca, InputStream dados, ServiceData serviceDataOwner) throws DocumentoCobrancaException;

   public String getCodigo();
   public String getNome();
   
   public IProvedorBanco getProvedorBanco();
   public void setProvedorBanco(IProvedorBanco provedor);
   
   public void registrarGerenciador();

}
