package br.com.orionsoft.financeiro.documento.pagamento;

import java.io.File;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import br.com.orionsoft.basic.entities.Contrato;
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
public interface IGerenciadorDocumentoPagamento {
   /**
    * Cria um documento SEM PERSISTI-LO. Somente as propriedades b�sicas s�o preenchidas.
    * � muito �til, pois durante o processo de quita��o de algum Grupo, um documento deve ser 
    * criado, a as vezes, alguns dados do documento devem ser preenchidos pelo Operador, no entanto,
    * o operador pode desistir ou cancelar a quita��o, assim, o documento que ele criou n�o deve
    * ser persistido, por isto este m�todo s� cria. Quando ele vai inserir algum lan�amento 
    * para confirmar a quita��o a rotina respons�vel dever� chamar depois o lancarDocumento().
    * @param contrato
    * @param documentoPagamentoCategoria
    * @param dataDocumento
    * @param dataVencimento
    * @param valorDocumento
    * @param recebimento
    * @param serviceDataOwner
    * @return
    * @throws BusinessException
    */
   public IEntity<? extends DocumentoPagamento> criarDocumento(IEntity<Contrato> contrato, IEntity<? extends DocumentoPagamentoCategoria> documentoPagamentoCategoria, Calendar dataDocumento, Calendar dataVencimento, BigDecimal valorDocumento, Transacao transacao, ServiceData serviceDataOwner) throws BusinessException;
 
   public void imprimirDocumentos(List<DocumentoPagamentoBean> documentos, OutputStream outputStream, int printIndex, ServiceData serviceDataOwner) throws DocumentoPagamentoException;
   
   public void imprimirDocumento(IEntity<? extends DocumentoPagamento> documento, OutputStream outputStream, int printIndex, String instrucoesAdicionais, ServiceData serviceDataOwner) throws DocumentoPagamentoException;

   /** Dada a inst�ncia de um documento, o  gerenciador respons�vel pelo mesmo efetuar�
    * os c�lculos particulares de multa, juros e outros, colocando estes valores calculados 
    * nas propriedades do documento e <b>retornando o valor total que dever� ser recebido</b>*/
   public BigDecimal calcularVencimento(IEntity<? extends DocumentoPagamento> documento, Calendar dataPagamento, ServiceData serviceDataOwner) throws DocumentoPagamentoException;

   public void quitarDocumento(IEntity<? extends DocumentoPagamento> documento, Long contaId, Calendar data, ServiceData serviceDataOwner) throws DocumentoPagamentoException;
  
   public void baixarDocumento(DocumentoPagamento documento, Calendar dataBaixa, ServiceData serviceDataOwner) throws DocumentoPagamentoException;
   
   /**
    * Este m�todo confirma que um documento criado pelo m�todo criarDocumento() dever� ser 
    * permanentemente persistido e, em muitos casos, o receber algumas informa��es extras como 
    * um sequenciador de numera��o e outros controles mais espec�ficos para cada tipo de documento.  
    * @param documento
    * @param conta
    * @param data
    * @param serviceDataOwner
    * @throws DocumentoPagamentoException
    */
   public void lancarDocumento(IEntity<? extends DocumentoPagamento> documento, ServiceData serviceDataOwner) throws DocumentoPagamentoException;
   
   public void estornarDocumento(IEntity<? extends DocumentoPagamento> documento, long movimentoId, Calendar data, ServiceData serviceDataOwner) throws DocumentoPagamentoException;
   
   /**
    * Este m�todo cancela um documento; utilizado, por exemplo, quando um novo documento e uma nova forma de pagamento
    * s�o atribu�dos a um grupo, sendo necess�rio o cancelamento do documento anterior (passado por par�metro).
    * @param documento
    * @param dataCancelamento
    * @param serviceDataOwner
    * @throws DocumentoPagamentoException
    */
   public void cancelarDocumento(IEntity<? extends DocumentoPagamento> documento, Calendar dataCancelamento, ServiceData serviceDataOwner) throws DocumentoPagamentoException;
   
   /* 
    * � passado o Id do cedente pois este deve ser salvo no �ltimo instante no m�todo gerarRemessa do banco correspondente, evitando NonUniqueObject 
    * O arquivo vindo da interface � um OutputStream, mas j� � tratado retornando um File, pois podemos precisar do nome do arquivo, 
    * o que n�o � poss�vel obter com o OutputStream 
    */
   public File gerarRemessa(IEntity<? extends ConvenioPagamento> convenioPagamento, Calendar inicioPeriodo, Calendar finalPeriodo, ServiceData serviceDataOwner) throws DocumentoPagamentoException;   
   
   /*
    *  Cedente � necess�rio para saber se o layout do arquivo de retorno � CNAB240 ou CNAB400
    *  O arquivo vindo da interface � um InputStream, mas j� � tratado retornando um File, pois podemos precisar do nome do arquivo, 
    *  o que n�o � poss�vel obter com o InputStream 
   	*/ 
   public void receberRetorno(IEntity<? extends ConvenioPagamento> convenioPagamento, File dados, ServiceData serviceDataOwner) throws DocumentoPagamentoException;

   /**
    * Este m�todo retorna uma lista do tipo de items de sele��o (id, nome)
    * com os layouts dispon�veis no Gerenciador de Documento implementado
    * @return
    */
   public List<SelectItem> getLayouts();
   
   public String getNome();
   
   public boolean isPreenchimentoManual();

   public IProvedorDocumentoPagamento getProvedorDocumentoPagamento();
   public void setProvedorDocumentoPagamento(IProvedorDocumentoPagamento provedor);
   
   public void registrarGerenciador();
   
   public List<IProperty> retrievePropriedadesPreenchimentoManual(IEntity<? extends DocumentoPagamento> documento) throws BusinessException;

}
