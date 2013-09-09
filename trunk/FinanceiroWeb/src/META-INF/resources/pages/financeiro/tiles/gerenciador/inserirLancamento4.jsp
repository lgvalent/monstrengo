<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form" >
	<h:panelGrid width="100%" columns="2" cellpadding="3" styleClass="tableList" columnClasses="tableListColumn" rowClasses="tableListRowEven,tableListRowOdd">
		<h:outputLabel value="Transação:"/>
		<h:outputText value="#{inserirLancamentoBean.process.transacaoNome}" />
		
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.contaPrevista.label}:"/>
		<h:outputText value="#{inserirLancamentoBean.process.contaPrevista}" />
 
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.dataVencimento.label}:"/>
		<h:outputText value="#{inserirLancamentoBean.process.dataVencimento.time}" />
		
 		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.contrato.label}:"/>
		<h:outputText value="#{inserirLancamentoBean.process.contrato}" />

		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.operacao.label}:"/>
		<h:outputText value="#{inserirLancamentoBean.process.operacao}" />

		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.descricao.label}:"/>
		<h:outputText value="#{inserirLancamentoBean.process.descricao}" />
		
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.documentoCobranca.label}:"/>
		<h:outputText value="#{inserirLancamentoBean.process.documentoCobranca}" />
		
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.documentoPagamento.label}:"/>
		<h:outputText value="#{inserirLancamentoBean.process.documentoPagamento}" />
		
		<h:outputLabel value="#{menuBean.infoMap.Lancamento.propertiesMetadata.valor.label}:"/>
		<h:outputText value="#{inserirLancamentoBean.process.valor}"/>
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
			<h:outputText value="#{item.centroCusto}"/>
		</h:column>
--
		<h:column>
			<f:facet name="header">
				<h:outputLabel value="Item de custo"/>
			</f:facet>
			<h:outputText value="#{item.itemCusto}"/>
		</h:column>
			
		<h:column>
			<f:facet name="header">
				<h:outputLabel value="Classificação contábil"/>
			</f:facet>
			<h:outputText value="#{item.classificacaoContabil}"/>
		</h:column>
			
		<h:column>
			<f:facet name="header">
				<h:outputLabel value="Valor"/>
			</f:facet>
			<h:outputText value="#{item.valor}" style="text-align: right">
				<f:convertNumber pattern="###,###,##0.00" type="currency"/>
			</h:outputText>
		</h:column>
			
		<h:column>
			<f:facet name="header">
				<h:outputLabel value="Descrição"/>
			</f:facet>
			<h:outputText value="#{item.descricao}" />
		</h:column>
	</h:dataTable>
	 
	<h:panelGrid columns="2">
		<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
		<h:commandButton value="Confirmar" action="#{inserirLancamentoBean.actionInserir}"/>
	</h:panelGrid>
</h:form>

	