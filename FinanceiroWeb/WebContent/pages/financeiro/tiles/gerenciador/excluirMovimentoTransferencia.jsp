<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:panelGrid cellpadding="3" rendered="#{excluirMovimentoTransferenciaBean.process == null}">
	<h:messages/>
</h:panelGrid>

<h:form id="form" >
	<h:panelGrid columns="2" cellpadding="3" styleClass="tableList" columnClasses="tableListColumn" rowClasses="tableListRowEven,tableListRowOdd">
		<h:outputLabel value="De conta:"/>
		<h:outputText value="#{excluirMovimentoTransferenciaBean.process.lancamentoMovimento.object.conta.nome}" />
 
		<h:outputLabel value="Para conta:"/>
		<h:outputText value="#{excluirMovimentoTransferenciaBean.process.lancamentoMovimento.object.transferencia.conta.nome}" />

		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.data.label}:"/>
		<h:outputText value="#{excluirMovimentoTransferenciaBean.process.lancamentoMovimento.object.data.time}" />
		
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.descricao.label}:"/>
		<h:outputText value="#{excluirMovimentoTransferenciaBean.process.lancamentoMovimento.object.descricao}" />
		
		<h:outputLabel value="#{menuBean.infoMap.LancamentoMovimento.propertiesMetadata.valorTotal.label}:"/>
		<h:outputText value="#{excluirMovimentoTransferenciaBean.process.lancamentoMovimento.object.valorTotal}">
			<f:convertNumber type="currency"/>
		</h:outputText>
		
	</h:panelGrid>

	<h:panelGrid columns="3" cellpadding="1" style="font: 12px">
		<h:outputLabel value="Motivo da exclusão:"/>
		<h:inputTextarea id="justificativa" value="#{excluirMovimentoTransferenciaBean.justificativa}" rows="3" required="true"/>
		<h:message styleClass="errorMessage" for="justificativa"/>

		<h:panelGroup>
			<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
			<h:commandButton value="Confirmar" action="#{excluirMovimentoTransferenciaBean.doExcluir}"/>
		</h:panelGroup>
		<h:outputLabel value=""/>
		<h:outputLabel value=""/>
	</h:panelGrid>
</h:form>
