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

		<h:outputLabel value="#{menuBean.infoMap.LancamentoItem.propertiesMetadata.centroCusto.label}"/>
 		<h:panelGrid columns="4">
 			<h:graphicImage value="../../public/basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
			<h:inputText id="centroCusto"
				value="#{listarItemCustoBean.process.centroCustoIdList}"
				size="5" maxlength="5" rendered="true"
				title="Dê um clique na caixa de texto para abrir a pesquisa"
				styleClass="queryInputSelectOne"
				onclick="javascript:openSelectOneId('br.com.orionsoft.financeiro.gerenciador.entities.CentroCusto',this.value,this)"
				onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)"/>
		</h:panelGrid>
		<h:message styleClass="errorMessage" for="centroCusto"/>

		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.conta.label}"/>
		<h:selectOneMenu id="conta" value="#{listarItemCustoBean.process.contaId}">
			<f:selectItems value="#{listarItemCustoBean.process.listConta}"/>
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="conta"/>

		<h:outputLabel value="Mostrar colunas "/>
		<h:selectManyCheckbox id="colunas" value="#{listarItemCustoBean.process.colunaList}" style="font-size: small" >
			<f:selectItems value="#{listarItemCustoBean.process.listColunas}"/>
		</h:selectManyCheckbox>
		<h:message styleClass="errorMessage" for="colunas"/>

		<h:outputLabel value="Data de "/>
		<h:panelGroup>
			<h:inputText id="dataInicial" value="#{listarItemCustoBean.process.dataInicial.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime pattern="dd/MM/yyyy" locale="Locale.BRAZIL"/>
			</h:inputText>
			<h:outputLabel value=" até "/>
			<h:inputText id="dataFinal" value="#{listarItemCustoBean.process.dataFinal.time}" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime pattern="dd/MM/yyyy" locale="Locale.BRAZIL"/>
			</h:inputText>
		</h:panelGroup>
		<h:message styleClass="errorMessage" for="dataFinal"/>

		<h:commandButton value="Listar" action="#{listarItemCustoBean.doVisualizar}"/>
		<h:outputLabel value=""/>
		<h:outputLabel value=""/>
	</h:panelGrid>

<%-- Listagem --%>
	<f:verbatim>
		<br>
	</f:verbatim>
	<h:panelGrid rendered="#{listarItemCustoBean.visualizandoQuitado}">
		<h:dataTable
			width="100%"
			border="1"
			value="#{listarItemCustoBean.arrayQuitado}"
		    var='item'
		    rendered="#{listarItemCustoBean.sizeQuitado>0}"
		    headerClass="tableListHeader"
		    footerClass="tableListFooter"
		    styleClass="tableList"
		    rowClasses="tableListRowEven,tableListRowOdd"
		    columnClasses="tableListColumn,tableListColumn,tableListColumn,tableListColumn,tableListColumnRight,tableListColumnRight"
		    style="border-collapse: collapse">
			<h:column rendered="#{listarItemCustoBean.colunaData}" >
				<f:facet name="header">
					<h:outputText value="Data"/>
				</f:facet>
				<h:outputLabel value="#{item.data.time}">
					<f:convertDateTime pattern="dd/MM/yyyy" locale="Locale.BRAZIL"/>
				</h:outputLabel>
			</h:column>
			<h:column rendered="#{listarItemCustoBean.colunaConta}">
				<f:facet name="header">
					<h:outputText value="Conta"/>
				</f:facet>
				<h:outputLabel value="#{item.conta}" />
			</h:column>
			<h:column rendered="#{listarItemCustoBean.colunaCentroCusto}">
				<f:facet name="header">
					<h:outputText value="Centro de custo"/>
				</f:facet>
				<h:outputLabel value="#{item.centroCusto}" />
			</h:column>
			<h:column >
				<f:facet name="header">
					<h:outputText value="Item de custo"/>
				</f:facet>
				<h:outputLabel value="#{item.itemCusto}" />
			</h:column>
			<h:column >
				<f:facet name="header">
					<h:outputText value="Entradas"/>
				</f:facet>
				<h:panelGrid width="100%" styleClass="tableListColumnRight" style="border: none ">
					<h:outputLabel value="#{item.valorCredito}">
						<f:convertNumber pattern="###,###,##0.00" type="currency"/>
					</h:outputLabel>
				</h:panelGrid>
				<f:facet name="footer">
					<h:outputLabel value="#{listarItemCustoBean.process.credito}">
						<f:convertNumber pattern="###,###,##0.00" type="currency"/>
					</h:outputLabel>
				</f:facet>
			</h:column>
			<h:column >
				<f:facet name="header">
					<h:outputText value="Saídas"/>
				</f:facet>
				<h:panelGrid width="100%" styleClass="tableListColumnRight" style="border: none ">
					<h:outputLabel value="#{item.valorDebito}">
						<f:convertNumber pattern="###,###,##0.00" type="currency"/>
					</h:outputLabel>
				</h:panelGrid>
				<f:facet name="footer">
					<h:outputLabel value="#{listarItemCustoBean.process.debito}">
						<f:convertNumber pattern="###,###,##0.00" type="currency"/>
					</h:outputLabel>
				</f:facet>
			</h:column>
		</h:dataTable>
		<h:panelGrid columns="2" width="100%">
			<h:panelGrid columns="1" width="100%" style="text-align:left" styleClass="tableListRow" >
				<h:outputLabel value="#{listarItemCustoBean.sizeQuitado} registro(s) quitado(s)"/>
			</h:panelGrid>
			<h:panelGrid columns="2" width="100%" style="text-align:right" styleClass="tableListRow" >
			</h:panelGrid>
		</h:panelGrid>
	</h:panelGrid>

	<h:outputText styleClass="errorMessage" value="#{messagesBean.firstMessageSummary}" escape="false" />
</h:form>
