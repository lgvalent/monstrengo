package br.com.orionsoft.financeiro;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.notification.RunNotifier;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.basic.entities.pessoa.Juridica;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.Cedente;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.GerenciadorDocumentoTitulo;
import br.com.orionsoft.financeiro.documento.cobranca.titulo.services.PersistirOcorrenciasService;
import br.com.orionsoft.financeiro.documento.pagamento.ConvenioPagamento;
import br.com.orionsoft.financeiro.documento.pagamento.DocumentoPagamentoCategoria;
import br.com.orionsoft.financeiro.documento.pagamento.cheque.ConvenioCheque;
import br.com.orionsoft.financeiro.gerenciador.entities.Banco;
import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabil;
import br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabilCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ContaBancaria;
import br.com.orionsoft.financeiro.gerenciador.entities.ContaCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Operacao;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.service.ServiceData;
import br.com.orionsoft.monstrengo.core.service.ServiceException;
import br.com.orionsoft.monstrengo.core.test.ServiceBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class PopularTabelas extends ServiceBasicTest {

	@Test
	public void popular() throws BusinessException {
		/* Executa primeiro a população do módulo basic */
		try {
			new TestClassRunner(br.com.orionsoft.basic.PopularTabelas.class).run(new RunNotifier());
		} catch (InitializationError e) {
			e.printStackTrace();
		}

		persistirOcorrencias();
		retrieveDocumentoCobrancaCategoria();
		retrieveDocumentoPagamentoCategoria();
		retrieveClassificacaoContabil();
		retrieveCentroCusto();
		retrieveItemCusto();
		retrieveOperacao();
	}

	void persistirOcorrencias() {

		try {
			/*
			 * Persiste todas as ocorrências que estão declaradas nas constantes
			 * da classe Ocorrencia.java
			 * 
			 * @see Ocorrencia.java
			 */
			ServiceData sd = new ServiceData(
					PersistirOcorrenciasService.SERVICE_NAME, null);
			this.serviceManager.execute(sd);

			Assert.assertTrue(sd.getMessageList().isTransactionSuccess());
		} catch (ServiceException e) {
			e.printStackTrace();
			UtilsTest.showMessageList(e.getErrorList());
			Assert.assertTrue(true);
		}
	}

	ContaCategoria retrieveCategoriaConta() throws BusinessException {
		/* ContaCategoria */
		ContaCategoria categoriaConta = UtilsCrud.objectCreate(this.serviceManager,	ContaCategoria.class, null);
		categoriaConta.setNome("Contas caixa");
		UtilsCrud.objectUpdate(this.serviceManager, categoriaConta, null);
		return categoriaConta;
	}

	Conta retrieveConta() throws BusinessException {
		/* Conta */
		Conta conta = UtilsCrud.objectCreate(this.serviceManager, Conta.class, null);
		conta.setContaCategoria(retrieveCategoriaConta());
		conta.setDataAbertura(CalendarUtils.getCalendar(1, Calendar.JANUARY, 2007));
		conta.setNome("Caixa");
		conta.setSaldoAbertura(DecimalUtils.getBigDecimal(0.0));
		UtilsCrud.objectUpdate(this.serviceManager, conta, null);
		return conta;
	}

	ClassificacaoContabilCategoria retrieveClassificacaoContabilCategoria() throws BusinessException {
		/* ClassificacaoContabilCategoria */
		ClassificacaoContabilCategoria categoriaClassificacaoContabil = UtilsCrud.objectCreate(this.serviceManager,
						ClassificacaoContabilCategoria.class, null);
		categoriaClassificacaoContabil.setNome("Categoria classificação contábil");
		UtilsCrud.objectUpdate(this.serviceManager, categoriaClassificacaoContabil,null);
		return categoriaClassificacaoContabil;
	}

	ClassificacaoContabil retrieveClassificacaoContabil() throws BusinessException {
		/* ClassificacaoContabil */
		ClassificacaoContabil classificacaoContabil = UtilsCrud.objectCreate(this.serviceManager,ClassificacaoContabil.class, null);
		classificacaoContabil.setClassificacaoContabilCategoria(retrieveClassificacaoContabilCategoria());
		classificacaoContabil.setNome("Classificação contábil");
		UtilsCrud.objectUpdate(this.serviceManager, classificacaoContabil, null);
		return classificacaoContabil;
	}

	CentroCusto retrieveCentroCusto() throws BusinessException {
		/* CentroCusto */
		CentroCusto centroCusto = UtilsCrud.objectCreate(this.serviceManager,	CentroCusto.class, null);
		centroCusto.setNome("Administração");
		UtilsCrud.objectUpdate(this.serviceManager, centroCusto, null);

		centroCusto = UtilsCrud.objectCreate(this.serviceManager,	CentroCusto.class, null);
		centroCusto.setNome("Produção");
		UtilsCrud.objectUpdate(this.serviceManager, centroCusto, null);
		return centroCusto;
	}

	ItemCusto retrieveItemCusto() throws BusinessException {
		/* ItemCusto */
		ItemCusto itemCusto = UtilsCrud.objectCreate(this.serviceManager,ItemCusto.class, null);
		itemCusto.setNome("Material de escritório");
		UtilsCrud.objectUpdate(this.serviceManager, itemCusto, null);
		
		itemCusto = UtilsCrud.objectCreate(this.serviceManager,ItemCusto.class, null);
		itemCusto.setNome("Material de consumo");
		UtilsCrud.objectUpdate(this.serviceManager, itemCusto, null);

		itemCusto = UtilsCrud.objectCreate(this.serviceManager,ItemCusto.class, null);
		itemCusto.setNome("Diversos");
		UtilsCrud.objectUpdate(this.serviceManager, itemCusto, null);
		return itemCusto;
	}

	Operacao retrieveOperacao() throws BusinessException {
		/* Operacao */
		Operacao operacao = UtilsCrud.objectCreate(this.serviceManager,Operacao.class, null);
		operacao.setNome("Operação");
		UtilsCrud.objectUpdate(this.serviceManager, operacao, null);
		return operacao;
	}
	
	Cedente retrieveCedente() throws BusinessException {
		/* ConvenioCobranca */
		Cedente cedente = UtilsCrud.objectCreate(this.serviceManager, Cedente.class,null);
		cedente.setNome("Uningá");
		cedente.setNomeGerenciadorDocumento(GerenciadorDocumentoTitulo.GERENCIADOR_NOME);
		cedente.setContratante((Juridica)UtilsCrud.objectRetrieve(this.serviceManager, Juridica.class, 3, null));
		cedente.setContratado((Contrato)UtilsCrud.objectRetrieve(this.serviceManager, Contrato.class, 1, null));
		cedente.setContaBancaria(retrieveContaBancaria());
		cedente.setCedenteCodigo("1060112");
		cedente.setCedenteDigito("");

		// cedente.setPropertyValue(Cedente.
		UtilsCrud.objectUpdate(this.serviceManager, cedente, null);
		return cedente;
	}

	ContaBancaria retrieveContaBancaria() throws BusinessException {
		/* DocumentoCobrancaCategoria */
		ContaBancaria contaBancaria = UtilsCrud.objectCreate(this.serviceManager, ContaBancaria.class, null);
		contaBancaria.setBanco(retrieveBanco());
		contaBancaria.setAgenciaCodigo("3407");
		contaBancaria.setAgenciaDigito("X");
		contaBancaria.setContaCodigo("55067");
		contaBancaria.setContaDigito("1");
		UtilsCrud.objectUpdate(this.serviceManager, contaBancaria, null);
		return contaBancaria;
	}

	Banco retrieveBanco() throws BusinessException {
		/* DocumentoCobrancaCategoria */
		Banco banco = UtilsCrud.objectCreate(this.serviceManager, Banco.class, null);
		banco.setCodigo("001");
		banco.setDigito("9");
		banco.setNome("Banco do Brasil");
		UtilsCrud.objectUpdate(this.serviceManager, banco, null);
		return banco;
	}

	DocumentoCobrancaCategoria retrieveDocumentoCobrancaCategoria() throws BusinessException {
		/* DocumentoCobrancaCategoria */
		DocumentoCobrancaCategoria documentoCobrancaCategoria = UtilsCrud.objectCreate(this.serviceManager, DocumentoCobrancaCategoria.class, null);
		documentoCobrancaCategoria.setContaPadrao(retrieveConta());
		documentoCobrancaCategoria.setConvenioCobranca(retrieveCedente());
		documentoCobrancaCategoria.setNome("Boleto BB");
		UtilsCrud.objectUpdate(this.serviceManager, documentoCobrancaCategoria, null);
		return documentoCobrancaCategoria;
	}

	ConvenioPagamento retrieveConvenioPagamento() throws BusinessException {
		ConvenioPagamento convenioPagamento = UtilsCrud.objectCreate(this.serviceManager, ConvenioPagamento.class, null);
		convenioPagamento.setNome("Convênio de pagamento");
		UtilsCrud.objectUpdate(this.serviceManager, convenioPagamento, null);
		return convenioPagamento;
	}
	
	ConvenioPagamento retrieveConvenioCheque() throws BusinessException {
		ConvenioCheque convenioCheque = UtilsCrud.objectCreate(this.serviceManager, ConvenioCheque.class, null);
		convenioCheque.setNome("Convênio Cheque");
		UtilsCrud.objectUpdate(this.serviceManager, convenioCheque, null);
		return convenioCheque;
	}
	
	DocumentoPagamentoCategoria retrieveDocumentoPagamentoCategoria() throws BusinessException {
		/* DocumentoPagamentoCategoria - Cheque */
		DocumentoPagamentoCategoria documentoPagamentoCategoria = UtilsCrud.objectCreate(this.serviceManager, DocumentoPagamentoCategoria.class, null);
		documentoPagamentoCategoria.setContaPadrao(retrieveConta());
		documentoPagamentoCategoria.setNome("Cheque");
		documentoPagamentoCategoria.setConvenioPagamento(retrieveConvenioCheque());
		UtilsCrud.objectUpdate(this.serviceManager, documentoPagamentoCategoria, null);

		/* DocumentoPagamentoCategoria - Dinheiro */
		documentoPagamentoCategoria = UtilsCrud.objectCreate(this.serviceManager, DocumentoPagamentoCategoria.class, null);
		documentoPagamentoCategoria.setContaPadrao(retrieveConta());
		documentoPagamentoCategoria.setNome("Dinheiro");
		documentoPagamentoCategoria.setConvenioPagamento(retrieveConvenioPagamento());
		UtilsCrud.objectUpdate(this.serviceManager, documentoPagamentoCategoria, null);
		return documentoPagamentoCategoria;
	}

}
