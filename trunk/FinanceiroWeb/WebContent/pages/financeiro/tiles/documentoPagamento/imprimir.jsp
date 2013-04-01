<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form">
	<h:panelGrid 
	    columns="3" 
	    cellpadding="3">
 		<h:outputLabel value="Documento de pagamento:"/>
		<h:outputText value="#{imprimirDocumentoPagamentoBean.process.documento}" />
		<h:outputText value="" />

 		<h:outputText value="#{menuBean.infoMap.DocumentoPagamento.propertiesMetadata.dataVencimento.label}:" title="#{menuBean.infoMap.DocumentoPagamento.propertiesMetadata.dataVencimento.hint}"/>
		<h:outputText value="#{imprimirDocumentoPagamentoBean.process.documento.propertiesMap.dataVencimento.value.asString}"/>
		<h:outputText value=""/>

 		<h:outputText value="Selecione o layout de impressão:" title="Por padrão é selecionado aquele que foi definido durante a geração do documento. "/>
   		<h:selectOneMenu value="#{imprimirDocumentoPagamentoBean.process.documento.propertiesMap.layoutId.value.asInteger}" id="layoutId">
   			<f:selectItems value="#{imprimirDocumentoPagamentoBean.process.layouts}"/>
   		</h:selectOneMenu>
   		
   		<%-- TODO Colocar opção para  descrição adicional no documento --%>
	   	<h:message  styleClass="errorMessage" for="layoutId"/>
	</h:panelGrid>

	<h:panelGrid columns="5">
		<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
		<h:commandButton value="Download PDF" action="#{imprimirDocumentoPagamentoBean.doDownload}" disabled="#{!menuBean.processMap.ImprimirDocumentoPagamentoProcess}" />
		<h:selectOneMenu value="#{imprimirDocumentoPagamentoBean.process.printerIndex}" id="impressora">
			<f:selectItems 	value="#{imprimirDocumentoPagamentoBean.process.printerIndexList}" />
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="impressora" />
		<h:commandButton value="Imprimir" action="#{imprimirDocumentoPagamentoBean.doImprimir}" disabled="#{!menuBean.processMap.ImprimirDocumentoPagamentoProcess}" />
	</h:panelGrid>
</h:form>
	