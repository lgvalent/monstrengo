<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

	<h:form id="form">
<%-- Entrada de dados para geração do relatório --%>
		<h:panelGrid
			columns="3"
			cellpadding="3"
			border="1"
			styleClass="tableList"
			style="border-collapse: collapse">

			<%-- Conta --%>
			<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.conta.label}"/>
			<h:selectOneMenu id="conta" value="#{listarLancamentoMovimentoBean.process.conta}">
				<f:selectItems value="#{listarLancamentoMovimentoBean.process.listConta}"/>
			</h:selectOneMenu>
			<h:message styleClass="errorMessage" for="conta"/>
	
			<%-- Tipo de transação --%>
			<h:outputLabel value="Tipo de transação:"/>
			<h:selectManyCheckbox id="transacao" value="#{listarLancamentoMovimentoBean.process.transacaoList}" style="font-size: small">
				<f:selectItems value="#{listarLancamentoMovimentoBean.process.listTransacao}" />
			</h:selectManyCheckbox>
			<h:message styleClass="errorMessage" for="transacao"/>
	
			<%-- Listagem --%>
			<h:outputLabel value="Tipo de listagem:"/>
			<h:selectOneMenu id="listagem" value="#{listarLancamentoMovimentoBean.process.listagem}">
				<f:selectItems value="#{listarLancamentoMovimentoBean.process.listListagem}"/>
			</h:selectOneMenu>
			<h:message styleClass="errorMessage" for="listagem"/>
	
			<%-- Ordem --%>
			<h:outputLabel value="Ordem:"/>
			<h:selectOneRadio id="ordem" value="#{listarLancamentoMovimentoBean.process.ordem}" style="font-size: small">
				<f:selectItems value="#{listarLancamentoMovimentoBean.process.listOrdem}"/>
			</h:selectOneRadio>
			<h:message styleClass="errorMessage" for="ordem"/>

			<%-- Data de lançamento --%>
			<h:outputLabel value="Data de lançamento:"/>
			<h:panelGroup>
				<h:outputLabel value="de "/>
				<h:inputText id="dataLancamentoInicial" value="#{listarLancamentoMovimentoBean.process.dataLancamentoInicial.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
					<f:convertDateTime pattern="dd/MM/yyyy" locale="Locale.BRAZIL"/>
				</h:inputText>
				<h:outputLabel value=" até "/>
				<h:inputText id="dataLancamentoFinal" value="#{listarLancamentoMovimentoBean.process.dataLancamentoFinal.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
					<f:convertDateTime pattern="dd/MM/yyyy" locale="Locale.BRAZIL"/>
				</h:inputText>
			</h:panelGroup>
			<h:message styleClass="errorMessage" for="dataLancamentoInicial"/>

			<%-- Data de quitação --%>
			<h:outputLabel value="Data de quitação:"/>
			<h:panelGroup>
				<h:outputLabel value="de "/>
				<h:inputText id="dataQuitacaoInicial" value="#{listarLancamentoMovimentoBean.process.dataQuitacaoInicial.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
					<f:convertDateTime pattern="dd/MM/yyyy" locale="Locale.BRAZIL"/>
				</h:inputText>
				<h:outputLabel value=" até "/>
				<h:inputText id="dataQuitacaoFinal" value="#{listarLancamentoMovimentoBean.process.dataQuitacaoFinal.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
					<f:convertDateTime pattern="dd/MM/yyyy" locale="Locale.BRAZIL"/>
				</h:inputText>
			</h:panelGroup>
			<h:message styleClass="errorMessage" for="dataQuitacaoInicial"/>
 
			<%-- Data de vencimento --%>
			<h:outputLabel value="Data de vencimento:"/>
			<h:panelGroup>
				<h:outputLabel value="de "/>
				<h:inputText id="dataVencimentoInicial" value="#{listarLancamentoMovimentoBean.process.dataVencimentoInicial.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
					<f:convertDateTime pattern="dd/MM/yyyy" locale="Locale.BRAZIL"/>
				</h:inputText>
				<h:outputLabel value=" até "/>
				<h:inputText id="dataVencimentoFinal" value="#{listarLancamentoMovimentoBean.process.dataVencimentoFinal.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
					<f:convertDateTime pattern="dd/MM/yyyy" locale="Locale.BRAZIL"/>
				</h:inputText>
			</h:panelGroup>
			<h:message styleClass="errorMessage" for="dataVencimentoInicial"/>
 
			<%-- Centro custo --%>
			<h:outputLabel value="#{menuBean.infoMap.LancamentoItem.propertiesMetadata.centroCusto.label}:"/>
	 		<h:panelGrid columns="2">
	 			<h:graphicImage value="../basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="centroCustoId"
					value="#{listarLancamentoMovimentoBean.process.centroCusto}"
					size="5" maxlength="5"
					title="Dê um clique na caixa de texto para abrir a pesquisa"
					styleClass="queryInputSelectOne"
					onclick="javascript:openSelectOneId('br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto',this.value,this)"
					onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
			</h:panelGrid>
			<h:message styleClass="errorMessage" for="centroCustoId"/>

			<%-- Item custo --%>
			<h:outputLabel value="#{menuBean.infoMap.LancamentoItem.propertiesMetadata.itemCusto.label}:"/>
	 		<h:panelGrid columns="4" >
	 			<h:graphicImage value="../basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="itemCustoList"
					value="#{listarLancamentoMovimentoBean.process.itemCustoIdList}"
					size="5" maxlength="5" rendered="true"
					title="Dê um clique na caixa de texto para abrir a pesquisa"
					styleClass="queryInputSelectOne"
					onclick="javascript:openSelectOneId('br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto',this.value,this)"
					onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
				<h:selectBooleanCheckbox value="#{listarLancamentoMovimentoBean.process.itemCustoNot}" />
				<h:outputLabel value="Exceto o selecionado" for="" style="font-size: small"/>
			</h:panelGrid>
			<h:message styleClass="errorMessage" for="itemCustoList"/>

			<%-- Categoria do documento de cobrança --%>	
<%-- 
			<h:outputLabel value="#{menuBean.infoMap.DocumentoCobranca.propertiesMetadata.documentoCobrancaCategoria.label}:"/>
			<h:selectOneMenu id="documentoCobrancaCategoria" value="#{listarLancamentoMovimentoBean.process.documentoCobrancaCategoriaId}" >
				<f:selectItems value="#{listarLancamentoMovimentoBean.process.documentoCobrancaCategoriaList}"/>
			</h:selectOneMenu>
			<h:message styleClass="errorMessage" for="documentoCobrancaCategoria"/>
 --%>
		</h:panelGrid>
		<h:commandButton value="Listar" action="#{listarLancamentoMovimentoBean.doVisualizar}" styleClass="noprint"/>
	</h:form>

	<h:form target="_blank">
		
<%-- Painel de operação quitados --%>
		<h:panelGrid rendered="#{listarLancamentoMovimentoBean.visualizando}">
			<h:dataTable
				width="100%"
				border="1"
				value="#{listarLancamentoMovimentoBean.list}"
			    var='item'
			    rendered="#{listarLancamentoMovimentoBean.size>0}"
			    headerClass="tableListHeader"
			    footerClass="tableListFooter"
			    styleClass="tableList"
			    rowClasses="tableListRowEven,tableListRowOdd"
			    columnClasses="tableListColumn,tableListColumn,tableListColumn,tableListColumn,tableListColumn,tableListColumnRight,tableListColumnRight,tableListColumnRight,tableListColumnRight,tableListColumnRight"
			    style="border-collapse: collapse">
	
				<h:column >
					<h:outputLink value="javascript:linkRetrieve('#{menuBean.infoMap.LancamentoMovimento.type.name}', '#{item.id}')"  rendered="#{menuBean.crudMap.LancamentoMovimento.canRetrieve}" styleClass="noprint">
						<h:graphicImage value="../basic/img/retrieve.png" title="Visualiza os detalhes do registro" style="border:0"/>
					</h:outputLink>
					<h:graphicImage value="../basic/img/retrieve_d.png" title="Você não possui direitos para visualizar os dados do registro" style="border:0" rendered="#{!menuBean.crudMap.LancamentoMovimento.canRetrieve}" styleClass="noprint"/>
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Data"/>
					</f:facet>
					<h:outputLabel value="#{item.dataQuitacao.time}" >
						<f:convertDateTime pattern="dd/MM/yyyy" locale="pt_BR"/>
					</h:outputLabel>
				</h:column>
<%-- 
				<h:column rendered="#{listarLancamentoMovimentoBean.process.conta == -1}">
 --%>
 				<h:column>
					<f:facet name="header">
						<h:outputText value="Conta"/>
					</f:facet>
					<h:outputLabel value="#{item.conta}" style="white-space: nowrap"/>
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Contrato"/>
					</f:facet>
					<h:outputLabel value="#{item.pessoa}" />
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Descrição"/>
					</f:facet>
					<h:outputLabel value="#{item.descricao}" />
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Valor"/>
					</f:facet>
					<h:outputLabel value="#{item.valorMovimento}" >
						<f:convertNumber pattern="###,###,##0.00" type="currency"/>
					</h:outputLabel>
					<f:facet name="footer">
						<h:outputLabel value="#{listarLancamentoMovimentoBean.process.somaMovimento}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputLabel>
					</f:facet>
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Juros"/>
					</f:facet>
					<h:outputLabel value="#{item.valorJuros}" >
						<f:convertNumber pattern="###,###,##0.00" type="currency"/>
					</h:outputLabel>
					<f:facet name="footer">
						<h:outputLabel value="#{listarLancamentoMovimentoBean.process.somaJuros}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputLabel>
					</f:facet>
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Multa"/>
					</f:facet>
					<h:outputLabel value="#{item.valorMulta}" >
						<f:convertNumber pattern="###,###,##0.00" type="currency"/>
					</h:outputLabel>
					<f:facet name="footer">
						<h:outputLabel value="#{listarLancamentoMovimentoBean.process.somaMulta}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputLabel>
					</f:facet>
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Desconto"/>
					</f:facet>
					<h:outputLabel value="#{item.valorDesconto}" >
						<f:convertNumber pattern="###,###,##0.00" type="currency"/>
					</h:outputLabel>
					<f:facet name="footer">
						<h:outputLabel value="#{listarLancamentoMovimentoBean.process.somaDesconto}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputLabel>
					</f:facet>
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Acréscimos"/>
					</f:facet>
					<h:outputLabel value="#{item.valorAcrescimo}" >
						<f:convertNumber pattern="###,###,##0.00" type="currency"/>
					</h:outputLabel>
					<f:facet name="footer">
						<h:outputLabel value="#{listarLancamentoMovimentoBean.process.somaAcrescimo}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputLabel>
					</f:facet>
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Total"/>
					</f:facet>
					<h:outputLabel value="#{item.valorTotal}" >
						<f:convertNumber pattern="###,###,##0.00" type="currency"/>
					</h:outputLabel>
					<f:facet name="footer">
						<h:outputLabel value="#{listarLancamentoMovimentoBean.process.somaTotal}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputLabel>
					</f:facet>
				</h:column>
<%-- 
				<h:column >
					<f:facet name="header">
						<h:outputText style="text-align:right" value="Saídas"/>
					</f:facet>
					<h:panelGrid width="100%" style="text-align:right; font: 12px" styleClass="tableListRow">
						<h:outputLabel value="#{item.valorMovimento}" rendered="#{item.valorMovimento<0}">
							<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						</h:outputLabel>
					</h:panelGrid>
					<f:facet name="footer">
						<h:panelGrid width="100%" style="text-align:right; font: 12px" styleClass="tableListRow">
							<h:outputLabel value="#{listarLancamentoMovimentoBean.process.debito}" style="text-align:right">
								<f:convertNumber pattern="###,###,##0.00" type="currency"/>
							</h:outputLabel>
						</h:panelGrid>
					</f:facet>
				</h:column>
 --%>				
<%-- 
				<h:column >
					<h:commandLink action="#{transferirBean.actionTransferir}" target="transferir">
						<h:graphicImage value="../financeiro/img/transfer.png" title="Cria uma transferência usando os dados deste registro" style="border:0"/>
				     	<f:param name="movimentoId" value="#{item.id}"/>
					</h:commandLink>
				</h:column>
 --%>
			</h:dataTable>
			<h:panelGrid columns="5" style="text-align:left" styleClass="tableListRow" >
				<h:outputLabel value="#{listarLancamentoMovimentoBean.size} registro(s)"/>
				<h:outputLabel value=" | Créditos: "/>
				<h:outputLabel value="#{listarLancamentoMovimentoBean.process.credito}">
					<f:convertNumber pattern="###,###,##0.00" type="currency"/>
				</h:outputLabel>	
				<h:outputLabel value=" | Débitos: "/>
				<h:outputLabel value="#{listarLancamentoMovimentoBean.process.debito}">
					<f:convertNumber pattern="###,###,##0.00" type="currency"/>
				</h:outputLabel>	
			</h:panelGrid>
<%-- 
				<h:panelGrid columns="2" width="100%" style="text-align:right; font: 12px" styleClass="tableListRow" >
					<h:outputText value="Saldo inicial: #{listarLancamentoMovimentoBean.saldoInicial}"/>
					<h:outputText value="Saldo final: #{listarLancamentoMovimentoBean.saldoFinal}"/>
				</h:panelGrid>
 --%>
			</h:panelGrid>
		
		<h:outputText styleClass="errorMessage" value="#{messagesBean.firstMessageSummary}" escape="false" />
	</h:form>
