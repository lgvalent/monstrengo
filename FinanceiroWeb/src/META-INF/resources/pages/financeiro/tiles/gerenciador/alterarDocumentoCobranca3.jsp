<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form">
    <f:verbatim><br></f:verbatim>

	<h:panelGrid width="100%" rendered="#{alterarDocumentoCobrancaBean.process.documentoNovo != null}">
		<h:commandLink action="#{imprimirDocumentoCobrancaBean.actionPrepararDocumento}" rendered="#{menuBean.processMap.ImprimirDocumentoCobrancaProcess}">
			<h:graphicImage value="../financeiro/img/imprimirDocumento.png" title="Imprimir o documento" style="border:0" />
			<h:outputText value=" Imprimir o novo documento de cobrança" escape="false"/>
	     	<f:param name="documentoId" value="#{alterarDocumentoCobrancaBean.process.documentoNovo.id}"/>
		</h:commandLink>
	</h:panelGrid>
	
	<h:panelGrid>
		<h:panelGroup>
			<h:graphicImage value="../sindicato/img/button_ok.png" style="border:0; margin-top:5px"/>
			<h:commandLink value=" Concluir " action="#{alterarDocumentoCobrancaBean.doConcluir}"/>
			<h:outputText value="esta operação"/>
		</h:panelGroup>
	</h:panelGrid>
	
    <f:verbatim><br></f:verbatim>

	<h:dataTable value="#{messagesBean.fromSecondMessage}" var='msg' style="color:blue; text-align:left">
		<h:column>
			<f:facet name="header">
				<h:outputText value="Mensagens adicionais:"/>
			</f:facet>
			<h:outputText value="#{msg.summary}" escape="false" />
		</h:column>
	</h:dataTable>
</h:form>	