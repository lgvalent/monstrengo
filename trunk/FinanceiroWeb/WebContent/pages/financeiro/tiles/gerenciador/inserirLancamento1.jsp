<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="a4j" uri="https://ajax4jsf.dev.java.net/ajax"%>

<h:panelGrid cellpadding="3" rendered="#{inserirLancamentoBean.process == null}">
	<h:messages/>
</h:panelGrid>

<a4j:region id="inserirLancamento" selfRendered="true">
	<a4j:status>
		<f:facet name="start">
			<f:verbatim>
				<div id="status" style="position:absolute; top:2px; right:2px; font: 14px; color:#FFFFFF; background: #993300">
		   			Carregando...
				</div>
			</f:verbatim>
		</f:facet>
	</a4j:status>

	<h:form id="form" rendered="#{inserirLancamentoBean.process != null}">
		<h:panelGrid columns="3" cellpadding="3" styleClass="tableList">
			<h:outputLabel value="Transação"/>
			<h:selectOneMenu id="transacao" value="#{inserirLancamentoBean.process.transacao}" styleClass="input">
				<f:selectItems value="#{inserirLancamentoBean.process.listTransacao}"/>
			</h:selectOneMenu>
			<h:message styleClass="errorMessage" for="transacao"/>
	
			<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.contaPrevista.label}"/>
			<h:selectOneMenu id="contaPrevista" value="#{inserirLancamentoBean.process.contaPrevista.id}">
				<f:selectItems value="#{inserirLancamentoBean.process.listContaPrevista}"/>
			</h:selectOneMenu>
			<h:message styleClass="errorMessage" for="contaPrevista"/>
	
			<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.data.label}"/>
			<h:inputText id="data" value="#{inserirLancamentoBean.process.data.time}" required="true" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
			</h:inputText>
			<h:message styleClass="errorMessage" for="data"/>
	
			<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.dataVencimento.label}"/>
			<h:inputText id="dataVencimento" value="#{inserirLancamentoBean.process.dataVencimento.time}" required="true" size="10" maxlength="10" onblur="return onblurCalendar(this, 'dd/MM/yyyy')">
				<f:convertDateTime locale="Locale.BRAZIL" pattern="dd/MM/yyyy"/>
			</h:inputText>
			<h:message styleClass="errorMessage" for="dataVencimento"/>
	
	 		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.contrato.label}"/>
	 		<h:panelGrid columns="3">
	 			<h:graphicImage value="../basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
				<h:inputText id="contrato"
					value="#{inserirLancamentoBean.process.contrato.id}"
					size="5" maxlength="5" rendered="true"
					title="Dê um clique na caixa de texto para abrir a pesquisa"
					styleClass="queryInputSelectOne"
					onclick="javascript:openSelectOneId('br.com.orionsoft.basic.entities.Contrato',this.value,this)"
					onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY)">
					<a4j:support event="onblur" reRender="contratoDescription,contratoErro" ajaxSingle="true"/>
				</h:inputText>
				<h:panelGroup id="contratoDescription" >
				   <h:outputText value="#{inserirLancamentoBean.process.contrato}" styleClass="tableViewColumnOdd"/>
			       <h:message styleClass="errorMessage" for="contrato"/>
				</h:panelGroup>
			</h:panelGrid>
			<h:outputText/>
			
			<%-- 
			<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.operacao.label}"/>
			<h:selectOneMenu id="operacao" value="#{inserirLancamentoBean.process.operacao.id}">
				<f:selectItems value="#{inserirLancamentoBean.process.listOperacao}"/>
			</h:selectOneMenu>
			<h:message styleClass="errorMessage" for="operacao"/>
			--%>
			
			<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.descricao.label}"/>
			<h:inputTextarea id="descricao" value="#{inserirLancamentoBean.process.descricao}" rows="3"/>
			<h:message styleClass="errorMessage" for="descricao"/>
	
		</h:panelGrid>
			
			<h:dataTable id="itemTable" 
						 value="#{inserirLancamentoBean.process.lancamentoItemBeanList}" 
						 var='item'
						 cellpadding='2'
						 headerClass="tableListHeader"
						 styleClass="tableList"
						 rowClasses="tableListRowEven,tableListRowOdd"
						 columnClasses="tableListColumn"
						 style="border-collapse: collapse; white-space: nowrap;" 
						 >
		
				<h:column>
					<f:facet name="header">
						<h:outputLabel value="Nº" styleClass="tableListHeader"/>
					</f:facet>
					<h:outputText id="numero" value="#{item.id}" />
					<h:message styleClass="errorMessage" for="numero"/>
				</h:column>
				
				<h:column>
					<f:facet name="header">
						<h:outputLabel value="Centro de custo" styleClass="tableListHeader"/>
					</f:facet>
					<h:selectOneMenu id="centroCusto" value="#{item.centroCusto.id}" styleClass="input">
						<f:selectItems value="#{inserirLancamentoBean.process.listCentroCusto}"/>
					</h:selectOneMenu>
					<h:message styleClass="errorMessage" for="centroCusto"/>
				</h:column>
	 			
				<h:column>
					<f:facet name="header">
						<h:outputLabel value="Item de custo"/>
					</f:facet>
					<h:panelGrid columns="2">
			 			<h:graphicImage onclick="javascript:openSelectOneId('br.com.orionsoft.financeiro.gerenciador.entities.ItemCusto','',document.getElementById('body:form:itemTable:0:itemCusto'))" value="../basic/img/query_open_select.png" title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa" style="border:0"/>
						<h:selectOneMenu id="itemCusto" value="#{item.itemCusto.id}" styleClass="input">
							<f:selectItems value="#{inserirLancamentoBean.process.listItemCusto}"/>
						</h:selectOneMenu>
					</h:panelGrid>
					<h:message styleClass="errorMessage" for="itemCusto"/>
				</h:column>
				
				<h:column>
					<f:facet name="header">
						<h:outputLabel value="Classificação contábil"/>
					</f:facet>
					<h:selectOneMenu id="classificacaoContabil" value="#{item.classificacaoContabil.id}" styleClass="input">
						<f:selectItems value="#{inserirLancamentoBean.process.listClassificacaoContabil}"/>
					</h:selectOneMenu>
					<h:message styleClass="errorMessage" for="classificacaoContabil"/>
				</h:column>
				
				<h:column>
					<f:facet name="header">
						<h:outputLabel value="Valor"/>
					</f:facet>
					<h:inputText id="valor" value="#{item.valor}" style="text-align: right">
						<f:convertNumber pattern="###,###,##0.00" type="currency"/>
						<a4j:support event="onblur" reRender="valor" ajaxSingle="true"/>
					</h:inputText>
					<h:message styleClass="errorMessage" for="valor"/>
				</h:column>
				
				<h:column>
					<f:facet name="header">
						<h:outputLabel value="Descrição"/>
					</f:facet>
					<h:inputText id="descricao" value="#{item.descricao}" />
					<h:message styleClass="errorMessage" for="descricao"/>
				</h:column>
				
				<h:column>
					<h:panelGroup>
						<h:commandLink action="#{inserirLancamentoBean.doRemoverItem}">
							<h:graphicImage value="../basic/img/delete.png" title="Remover item da lista" style="border:0"/>
							<f:param name="itemNumero" value="#{item.id}"/>
						</h:commandLink>
						<a4j:support event="onchange" reRender="itemTable" ajaxSingle="false"/>
					</h:panelGroup>
				</h:column> 
				
			</h:dataTable> 
	
			<h:panelGroup>
				<h:commandLink action="#{inserirLancamentoBean.doAdicionarItem}">
					<h:graphicImage value="../basic/img/add.png" title="Adicionar item à lista" style="border:0"/>
					<h:outputText value="Adicionar item" />
				</h:commandLink>
				<a4j:support event="onchange" reRender="itemTable" ajaxSingle="false"/>
			</h:panelGroup>
	
		<h:panelGrid columns="2">
			<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
			<h:commandButton value="Avançar >>" action="#{inserirLancamentoBean.actionInicio}"/>
		</h:panelGrid>
	</h:form>
</a4j:region>