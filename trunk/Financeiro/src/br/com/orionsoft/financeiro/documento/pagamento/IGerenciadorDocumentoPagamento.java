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
    * Cria um documento SEM PERSISTI-LO. Somente as propriedades básicas são preenchidas.
    * É muito útil, pois durante o processo de quitação de algum Grupo, um documento deve ser 
    * criado, a as vezes, alguns dados do documento devem ser preenchidos pelo Operador, no entanto,
    * o operador pode desistir ou cancelar a quitação, assim, o documento que ele criou não deve
    * ser persistido, por isto este método só cria. Quando ele vai inserir algum lançamento 
    * para confirmar a quitação a rotina responsável deverá chamar depois o lancarDocumento().
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

   /** Dada a instância de um documento, o  gerenciador responsável pelo mesmo efetuará
    * os cálculos particulares de multa, juros e outros, colocando estes valores calculados 
    * nas propriedades do documento e <b>retornando o valor total que deverá ser recebido</b>*/
   public BigDecimal calcularVencimento(IEntity<? extends DocumentoPagamento> documento, Calendar dataPagamento, ServiceData serviceDataOwner) throws DocumentoPagamentoException;

   public void quitarDocumento(IEntity<? extends DocumentoPagamento> documento, Long contaId, Calendar data, ServiceData serviceDataOwner) throws DocumentoPagamentoException;
  
   public void baixarDocumento(DocumentoPagamento documento, Calendar dataBaixa, ServiceData serviceDataOwner) throws DocumentoPagamentoException;
   
   /**
    * Este método confirma que um documento criado pelo método criarDocumento() deverá ser 
    * permanentemente persistido e, em muitos casos, o receber algumas informações extras como 
    * um sequenciador de numeração e outros controles mais específicos para cada tipo de documento.  
    * @param documento
    * @param conta
    * @param data
    * @param serviceDataOwner
    * @throws DocumentoPagamentoException
    */
   public void lancarDocumento(IEntity<? extends DocumentoPagamento> documento, ServiceData serviceDataOwner) throws DocumentoPagamentoException;
   
   public void estornarDocumento(IEntity<? extends DocumentoPagamento> documento, long movimentoId, Calendar data, ServiceData serviceDataOwner) throws DocumentoPagamentoException;
   
   /**
    * Este método cancela um documento; utilizado, por exemplo, quando um novo documento e uma nova forma de pagamento
    * são atribuídos a um grupo, sendo necessário o cancelamento do documento anterior (passado por parâmetro).
    * @param documento
    * @param dataCancelamento
    * @param serviceDataOwner
    * @throws DocumentoPagamentoException
    */
   public void cancelarDocumento(IEntity<? extends DocumentoPagamento> documento, Calendar dataCancelamento, ServiceData serviceDataOwner) throws DocumentoPagamentoException;
   
   /* 
    * É passado o Id do cedente pois este deve ser salvo no último instante no método gerarRemessa do banco correspondente, evitando NonUniqueObject 
    * O arquivo vindo da interface é um OutputStream, mas já é tratado retornando um File, pois podemos precisar do nome do arquivo, 
    * o que não é possível obter com o OutputStream 
    */
   public File gerarRemessa(IEntity<? extends ConvenioPagamento> convenioPagamento, Calendar inicioPeriodo, Calendar finalPeriodo, ServiceData serviceDataOwner) throws DocumentoPagamentoException;   
   
   /*
    *  Cedente é necessário para saber se o layout do arquivo de retorno é CNAB240 ou CNAB400
    *  O arquivo vindo da interface é um InputStream, mas já é tratado retornando um File, pois podemos precisar do nome do arquivo, 
    *  o que não é possível obter com o InputStream 
   	*/ 
   public void receberRetorno(IEntity<? extends ConvenioPagamento> convenioPagamento, File dados, ServiceData serviceDataOwner) throws DocumentoPagamentoException;

   /**
    * Este método retorna uma lista do tipo de items de seleção (id, nome)
    * com os layouts disponíveis no Gerenciador de Documento implementado
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
