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
    * Cria um documento SEM PERSISTI-LO. Somente as propriedades básicas são preenchidas.
    * É muito útil, pois durante o processo de quitação de algum Grupo, um documento deve ser 
    * criado, a as vezes, alguns dados do documento devem ser preenchidos pelo Operador, no entanto,
    * o operador pode desistir ou cancelar a quitação, assim, o documento que ele criou não deve
    * ser persistido, por isto este método só cria. Quando ele vai inserir algum lançamento 
    * para confirmar a quitação a rotina responsável deverá chamar depois o lancarDocumento().
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

   /** Dada a instância de um documento, o  gerenciador responsável pelo mesmo efetuará
    * os cálculos particulares de multa, juros e outros, colocando estes valores calculados 
    * nas propriedades do documento e <b>retornando o valor total que deverá ser recebido</b>*/
   public BigDecimal calcularVencimento(IEntity<? extends DocumentoCobranca> documento, Calendar dataPagamento, ServiceData serviceDataOwner) throws DocumentoCobrancaException;

   public void quitarDocumento(DocumentoCobranca documento, Conta conta, Calendar dataQuitacao, Calendar dataCompensacao, ServiceData serviceDataOwner) throws DocumentoCobrancaException;
  
   public void baixarDocumento(DocumentoCobranca documento, Calendar dataBaixa, ServiceData serviceDataOwner) throws DocumentoCobrancaException;
   
   /**
    * Este método confirma que um documento criado pelo método criarDocumento() deverá ser 
    * permanentemente persistido e, em muitos casos, o receber algumas informações extras como 
    * um sequenciador de numeração e outros controles mais específicos para cada tipo de documento.  
    * @param documento
    * @param conta
    * @param data
    * @param serviceDataOwner
    * @throws DocumentoCobrancaException
    */
   public void lancarDocumento(IEntity<? extends DocumentoCobranca> documento, ServiceData serviceDataOwner) throws DocumentoCobrancaException;
   
   public void estornarDocumento(IEntity<? extends DocumentoCobranca> documento, long movimentoId, Calendar data, ServiceData serviceDataOwner) throws DocumentoCobrancaException;
   
   /**
    * Este método cancela um documento; utilizado, por exemplo, quando um novo documento e uma nova forma de pagamento
    * são atribuídos a um grupo, sendo necessário o cancelamento do documento anterior (passado por parâmetro).
    * @param documento
    * @param dataCancelamento
    * @param serviceDataOwner
    * @throws DocumentoCobrancaException
    */
   public void cancelarDocumento(IEntity<? extends DocumentoCobranca> documento, Calendar dataCancelamento, ServiceData serviceDataOwner) throws DocumentoCobrancaException;
   
   /* 
    * É passado o Id do cedente pois este deve ser salvo no último instante no método gerarRemessa do banco correspondente, evitando NonUniqueObject 
    * O arquivo vindo da interface é um OutputStream, mas já é tratado retornando um File, pois podemos precisar do nome do arquivo, 
    * o que não é possível obter com o OutputStream
    * Caso a remessa não use protesto, deixar o campo 'quantidadeDiasProtesto' null 
    */
   public File gerarRemessa(IEntity<? extends ConvenioCobranca> convenioCobranca, Calendar inicioPeriodo, Calendar finalPeriodo, Integer quantidadeDiasProtesto, ServiceData serviceDataOwner) throws DocumentoCobrancaException;   
   
   /*
    *  Cedente é necessário para saber se o layout do arquivo de retorno é CNAB240 ou CNAB400
    *  O arquivo vindo da interface é um InputStream, mas já é tratado retornando um File, pois podemos precisar do nome do arquivo, 
    *  o que não é possível obter com o InputStream 
   	*/ 
   public List<DocumentoRetornoResultado> receberRetorno(IEntity<? extends ConvenioCobranca> convenioCobranca, InputStream dados, ServiceData serviceDataOwner) throws DocumentoCobrancaException;

   /**
    * Este método retorna uma lista do tipo de items de seleção (id, nome)
    * com os layouts disponíveis no Gerenciador de Documento implementado
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
