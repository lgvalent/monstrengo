package br.com.orionsoft.financeiro.documento.cobranca;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.cobranca.suporte.DocumentoRetornoResultado;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IProperty;

/**
 * Esta Interface define as funcionalidades de um gerenciador de documento.
 * 
 * @version 20060601
 *
 */
public interface IGerenciadorDocumentoCobranca {
   /**
    * Cria um documento SEM PERSISTI-LO. Somente as propriedades b�sicas s�o preenchidas.
    * � muito �til, pois durante o processo de quita��o de algum Grupo, um documento deve ser 
    * criado, a as vezes, alguns dados do documento devem ser preenchidos pelo Operador, no entanto,
    * o operador pode desistir ou cancelar a quita��o, assim, o documento que ele criou n�o deve
    * ser persistido, por isto este m�todo s� cria. Quando ele vai inserir algum lan�amento 
    * para confirmar a quita��o a rotina respons�vel dever� chamar depois o lancarDocumento().
    * @param contrato
    * @param documentoCobrancaCategoria
    * @param dataDocumento
    * @param dataVencimento
    * @param valorDocumento
    * @param recebimento
    * @param serviceDataOwner
    * @return
    * @throws BusinessException
    */
   public IEntity<? extends DocumentoCobranca> criarDocumento(IEntity<Contrato> contrato, IEntity<? extends DocumentoCobrancaCategoria> documentoCobrancaCategoria, Calendar dataDocumento, Calendar dataVencimento, BigDecimal valorDocumento, Transacao transacao, ServiceData serviceDataOwner) throws BusinessException;
 
   /**
    * Permite imprimir em uma impressora local ou em um outputStream.
    * @param documentos
    * @param outputStream
    * @param printerIndex
    * @param serviceDataOwner
    * @throws DocumentoCobrancaException
    * @version 20090403
    */
   public void imprimirDocumentos(List<DocumentoCobrancaBean> documentos, OutputStream outputStream, int printerIndex, InputStream inputStreamImagem, ServiceData serviceDataOwner) throws DocumentoCobrancaException;
   
   public void imprimirDocumento(IEntity<? extends DocumentoCobranca> documento, OutputStream outputStream, int printerIndex, String instrucoesAdicionais, InputStream inputStreamImagem, ServiceData serviceDataOwner) throws DocumentoCobrancaException;

   /** Dada a inst�ncia de um documento, o  gerenciador respons�vel pelo mesmo efetuar�
    * os c�lculos particulares de multa, juros e outros, colocando estes valores calculados 
    * nas propriedades do documento e <b>retornando o valor total que dever� ser recebido</b>*/
   public BigDecimal calcularVencimento(IEntity<? extends DocumentoCobranca> documento, Calendar dataPagamento, ServiceData serviceDataOwner) throws DocumentoCobrancaException;

   public void quitarDocumento(DocumentoCobranca documento, Conta conta, Calendar dataQuitacao, Calendar dataCompensacao, ServiceData serviceDataOwner) throws DocumentoCobrancaException;
  
   public void baixarDocumento(DocumentoCobranca documento, Calendar dataBaixa, ServiceData serviceDataOwner) throws DocumentoCobrancaException;
   
   /**
    * Este m�todo confirma que um documento criado pelo m�todo criarDocumento() dever� ser 
    * permanentemente persistido e, em muitos casos, o receber algumas informa��es extras como 
    * um sequenciador de numera��o e outros controles mais espec�ficos para cada tipo de documento.  
    * @param documento
    * @param conta
    * @param data
    * @param serviceDataOwner
    * @throws DocumentoCobrancaException
    */
   public void lancarDocumento(IEntity<? extends DocumentoCobranca> documento, ServiceData serviceDataOwner) throws DocumentoCobrancaException;
   
   public void estornarDocumento(IEntity<? extends DocumentoCobranca> documento, long movimentoId, Calendar data, ServiceData serviceDataOwner) throws DocumentoCobrancaException;
   
   /**
    * Este m�todo cancela um documento; utilizado, por exemplo, quando um novo documento e uma nova forma de pagamento
    * s�o atribu�dos a um grupo, sendo necess�rio o cancelamento do documento anterior (passado por par�metro).
    * @param documento
    * @param dataCancelamento
    * @param serviceDataOwner
    * @throws DocumentoCobrancaException
    */
   public void cancelarDocumento(IEntity<? extends DocumentoCobranca> documento, Calendar dataCancelamento, ServiceData serviceDataOwner) throws DocumentoCobrancaException;
   
   /* 
    * � passado o Id do cedente pois este deve ser salvo no �ltimo instante no m�todo gerarRemessa do banco correspondente, evitando NonUniqueObject 
    * O arquivo vindo da interface � um OutputStream, mas j� � tratado retornando um File, pois podemos precisar do nome do arquivo, 
    * o que n�o � poss�vel obter com o OutputStream
    * Caso a remessa n�o use protesto, deixar o campo 'quantidadeDiasProtesto' null 
    */
   public File gerarRemessa(IEntity<? extends ConvenioCobranca> convenioCobranca, Calendar inicioPeriodo, Calendar finalPeriodo, Integer quantidadeDiasProtesto, ServiceData serviceDataOwner) throws DocumentoCobrancaException;   
   
   /*
    *  Cedente � necess�rio para saber se o layout do arquivo de retorno � CNAB240 ou CNAB400
    *  O arquivo vindo da interface � um InputStream, mas j� � tratado retornando um File, pois podemos precisar do nome do arquivo, 
    *  o que n�o � poss�vel obter com o InputStream 
   	*/ 
   public List<DocumentoRetornoResultado> receberRetorno(IEntity<? extends ConvenioCobranca> convenioCobranca, InputStream dados, ServiceData serviceDataOwner) throws DocumentoCobrancaException;

   /**
    * Este m�todo retorna uma lista do tipo de items de sele��o (id, nome)
    * com os layouts dispon�veis no Gerenciador de Documento implementado
    * @return
    */
   public List<SelectItem> getLayouts();
   
   public String getNome();
   
   public boolean isPreenchimentoManual();

   public IProvedorDocumentoCobranca getProvedorDocumentoCobranca();
   public void setProvedorDocumentoCobranca(IProvedorDocumentoCobranca provedor);
   
   public void registrarGerenciador();
   
   public List<IProperty> retrievePropriedadesPreenchimentoManual(IEntity<? extends DocumentoCobranca> documento) throws BusinessException;

}
