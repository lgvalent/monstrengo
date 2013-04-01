<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form">
	<h:panelGrid 
	    columns="3" 
	    cellpadding="3">
 		<h:outputLabel value="Documento que será impresso:"/>
		<h:outputText value="#{imprimirDocumentoCobrancaBean.process.documento}" />
		<h:outputText value="" />

 		<h:outputText value="#{menuBean.infoMap.DocumentoCobranca.propertiesMetadata.dataVencimento.label}:" title="#{menuBean.infoMap.DocumentoCobranca.propertiesMetadata.dataVencimento.hint}"/>
		<h:outputText value="#{imprimirDocumentoCobrancaBean.process.documento.propertiesMap.dataVencimento.value.asString}"/>
		<h:outputText value=""/>

 		<h:outputText value="Selecione o layout de impressão:" title="Selecione o layout de impressão que deseja utilizar. Por padrão é selecionado aquele que foi definido durante a geração do documento."/>
   		<h:selectOneMenu value="#{imprimirDocumentoCobrancaBean.process.documento.propertiesMap.layoutId.value.asInteger}" id="layoutId">
   			<f:selectItems value="#{imprimirDocumentoCobrancaBean.process.layouts}"/>
   		</h:selectOneMenu>
   		
   		<%-- TODO Colocar opção para  descrição adicional no documento --%>
	   	<h:message  styleClass="errorMessage" for="layoutId"/>
	</h:panelGrid>

	<h:panelGrid columns="5">
		<h:commandButton value="Cancelar" onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();"/>
		<h:commandButton value="Download PDF" action="#{imprimirDocumentoCobrancaBean.doDownload}" disabled="#{!menuBean.processMap.ImprimirDocumentoCobrancaProcess}" />
		<h:selectOneMenu value="#{imprimirDocumentoCobrancaBean.process.printerIndex}" id="impressora">
			<f:selectItems 	value="#{imprimirDocumentoCobrancaBean.process.printerIndexList}" />
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="impressora" />
		<h:commandButton value="Imprimir" action="#{imprimirDocumentoCobrancaBean.doImprimir}" disabled="#{!menuBean.processMap.ImprimirDocumentoCobrancaProcess}" />
	</h:panelGrid>
</h:form>
	