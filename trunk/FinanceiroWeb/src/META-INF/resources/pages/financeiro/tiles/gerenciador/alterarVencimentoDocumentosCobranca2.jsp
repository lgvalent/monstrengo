<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:panelGrid cellpadding="3" rendered="#{alterarVencimentoDocumentosCobrancaBean.process == null}">
	<h:messages/>
</h:panelGrid>

<h:form id="form" >
	<h:dataTable 
		border="1" 
		value="#{alterarVencimentoDocumentosCobrancaBean.process.documentos.array}"
        var='item'
		headerClass="tableListHeader"
		footerClass="tableListFooter"
		styleClass="tableList"
		rowClasses="tableListRowEven,tableListRowOdd"
		columnClasses="tableListColumn"
		style="border-collapse: collapse"
		width="100%">
	  <h:column>
		<h:selectBooleanCheckbox value="#{item.selected}"/>
	  </h:column>
	  <h:column>
	  	<f:facet name="header">
			<h:outputLabel value="#{menuBean.infoMap.DocumentoCobranca.propertiesMetadata.dataVencimento.label}:"/>
	  	</f:facet>	
		<h:outputText value="#{item.propertiesMap.dataVencimento.value.asString}" />
	  </h:column>
	  <h:column>
	  	<f:facet name="header">
			<h:outputText value="Descrição" />
	  	</f:facet>	
		<h:outputLabel value="#{menuBean.infoMap.DocumentoCobranca.propertiesMetadata.contrato.label}:"/>
		<h:outputText value="#{item.object.contrato}" />
 
		<h:outputText value="<br>#{menuBean.infoMap.DocumentoCobranca.propertiesMetadata.documentoCobrancaCategoria.label}:"  escape="false" />
		<h:outputText value="#{item.object.documentoCobrancaCategoria}"/>
		
		<h:outputLabel value="#{menuBean.infoMap.DocumentoCobranca.propertiesMetadata.valor.label}:"/>
		<h:outputText value="#{item.object.valor}">
			<f:convertNumber type="currency"/>
		</h:outputText>
	  </h:column>
	</h:dataTable>

	<h:panelGroup>
		<h:commandButton value="Imprimir" title="Abre a tela para impressão dos documentos" action="#{alterarVencimentoDocumentosCobrancaBean.actionImprimir}"/>
	</h:panelGroup>
</h:form>
