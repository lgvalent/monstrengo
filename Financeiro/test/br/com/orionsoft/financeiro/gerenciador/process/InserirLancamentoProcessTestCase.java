package br.com.orionsoft.financeiro.gerenciador.process;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import javax.faces.model.SelectItem;

import org.junit.Assert;
import org.junit.Test;

import br.com.orionsoft.basic.entities.Contrato;
import br.com.orionsoft.financeiro.documento.cobranca.DocumentoCobrancaCategoria;
import br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.ClassificacaoContabil;
import br.com.orionsoft.financeiro.gerenciador.entities.Conta;
import br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto;
import br.com.orionsoft.financeiro.gerenciador.entities.Lancamento;
import br.com.orionsoft.financeiro.gerenciador.entities.LancamentoItem;
import br.com.orionsoft.financeiro.gerenciador.entities.Operacao;
import br.com.orionsoft.financeiro.gerenciador.entities.Transacao;
import br.com.orionsoft.financeiro.gerenciador.process.InserirLancamentoProcess.LancamentoItemBean;
import br.com.orionsoft.monstrengo.core.exception.BusinessException;
import br.com.orionsoft.monstrengo.core.test.ProcessBasicTest;
import br.com.orionsoft.monstrengo.core.test.UtilsTest;
import br.com.orionsoft.monstrengo.core.util.CalendarUtils;
import br.com.orionsoft.monstrengo.core.util.DecimalUtils;
import br.com.orionsoft.monstrengo.crud.entity.IEntity;
import br.com.orionsoft.monstrengo.crud.entity.IEntityList;
import br.com.orionsoft.monstrengo.crud.services.UtilsCrud;

public class InserirLancamentoProcessTestCase extends ProcessBasicTest{
	InserirLancamentoProcess process;

	public void setUp() throws Exception {
		super.setUp();
		process = (InserirLancamentoProcess) this.processManager.createProcessByName(InserirLancamentoProcess.PROCESS_NAME, this.getAdminSession());
	}

	public void tearDown() throws Exception {
		super.tearDown();
		process = null;
	}

	@Test 
	public void testExecute() throws BusinessException, ParseException {
		String descricao ="test de InserirLancamentoProcess";
		IEntity<Contrato> contrato = UtilsCrud.retrieve(this.processManager.getServiceManager(), Contrato.class, 1l, null);
		CentroCusto centroCusto = UtilsCrud.objectRetrieve(this.processManager.getServiceManager(), CentroCusto.class, 1l, null);
		IEntity<DocumentoCobrancaCategoria> documentoCobrancaCategoria = UtilsCrud.retrieve(this.processManager.getServiceManager(), DocumentoCobrancaCategoria.class, 1l, null);
		IEntity<Conta> contaPrevista = UtilsCrud.retrieve(this.processManager.getServiceManager(), Conta.class, 1l, null);
		IEntity<Operacao> operacao = UtilsCrud.retrieve(this.processManager.getServiceManager(), Operacao.class, 1l, null);
		ClassificacaoContabil classificacaoContabil = UtilsCrud.objectRetrieve(this.processManager.getServiceManager(), ClassificacaoContabil.class, 1l, null);
		List<ItemCusto> itemCustoList = UtilsCrud.objectList(this.processManager.getServiceManager(), ItemCusto.class, null);
		List<LancamentoItemBean> lancamentoItemBeanList = process.getLancamentoItemBeanList(); 
//		IEntity documento = UtilsCrud.retrieve(this.processManager.getServiceManager(), DocumentoCobranca.class, 1l, null);

		BigDecimal valor = DecimalUtils.getBigDecimal(1200.00);
		BigDecimal valorItem = DecimalUtils.getBigDecimal(200.00);
		Calendar data = CalendarUtils.getCalendar();
		Calendar dataVencimento = CalendarUtils.getCalendar(2007, Calendar.AUGUST, 31);
		Transacao transacao = Transacao.CREDITO;

		/* Remove o primeiro elemento da lista criado pelo Process.start() */
		process.getLancamentoItemBeanList().remove(1);
		for (ItemCusto itemCusto : itemCustoList) {
			process.criarNovoItem();
			LancamentoItemBean lancamentoItem = lancamentoItemBeanList.get(lancamentoItemBeanList.size() - 1);
			lancamentoItem.setCentroCustoItem(new SelectItem(centroCusto.getId(), centroCusto.toString()));
//			lancamentoItem.setClassificacaoContabil(classificacaoContabil);
			lancamentoItem.setItemCustoItem(new SelectItem(itemCusto.getId(), itemCusto.toString()));
			lancamentoItem.setValor(valorItem.doubleValue());
			lancamentoItemBeanList.add(lancamentoItem);
		}
		
		process.setDescricao(descricao);
		process.setLancamentoItemBeanList(lancamentoItemBeanList);
		process.getParamContrato().setValue(contrato);
		process.setValor(valor);
		process.setData(data);
		process.setDataVencimento(dataVencimento);
		process.setTransacao(transacao.ordinal());
		process.getParamOperacao().setValue(operacao);
		process.getParamContaPrevista().setValue(contaPrevista);
		process.setDocumentoCobrancaCategoria(documentoCobrancaCategoria);

		Assert.assertTrue(process.runCriarDocumentoCobranca());
		UtilsTest.showEntityProperties(process.getDocumentoCobranca());

		Assert.assertTrue(process.runInserirLancamento());

		IEntityList<Lancamento> lancamentos = process.getLancamentosInseridos();

		Lancamento lancamento = UtilsCrud.objectRetrieve(this.processManager.getServiceManager(), Lancamento.class, lancamentos.get(0).getId(), null);

		Assert.assertEquals(valor, lancamento.getSaldo());

		Assert.assertEquals(descricao, lancamento.getDescricao());
		Assert.assertEquals(contrato.toString(), lancamento.getContrato().toString());
		Assert.assertEquals(valor, lancamento.getValor());
		Assert.assertEquals(data, lancamento.getData());
		Assert.assertEquals(dataVencimento, lancamento.getDataVencimento());
		Assert.assertEquals(operacao.toString(), lancamento.getOperacao().toString());
		Assert.assertEquals(contaPrevista.toString(), lancamento.getContaPrevista().toString());

		List<LancamentoItem> lancamentoItemList = lancamento.getLancamentoItens(); 

		for (LancamentoItem lancamentoItem: lancamentoItemList){
			Assert.assertEquals(centroCusto.toString(), lancamentoItem.getCentroCusto().toString());
		}
	}
}
