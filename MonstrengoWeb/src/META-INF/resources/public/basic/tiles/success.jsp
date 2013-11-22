<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<f:subview id="success">
	<h:dataTable value="#{messagesBean.fromSecondMessage}" var='msg'
		style="color:blue">
		<h:column>
			<f:facet name="header">
				<h:outputText value="Mensagens adicionais:" />
			</f:facet>

			<h:outputText value="#{msg.summary}" escape="false" />
		</h:column>
	</h:dataTable>

    <f:verbatim>
		<SCRIPT>borderCreator.initDarkBorder('100%', '', 'center', 'top')</SCRIPT>
	</f:verbatim>
	<h:panelGrid columns="3">
		<h:outputLink value="javascript:history.back()">
			<h:panelGrid style="text-align:center;">
				<h:graphicImage value="../../public/basic/img/voltar.png" title="Voltar"
					style="border:0" />
				<h:outputText value="Voltar" style="font-size:small"/>
			</h:panelGrid>
		</h:outputLink>


		<%-- javascript para imprimir página corrente --%>
		<h:outputLink value="javascript:window.print()">
			<h:panelGrid style="text-align:center;">
				<h:graphicImage value="../../public/basic/img/printer.png" title="Imprimir"
					style="border:0" />
				<h:outputText value="Imprimir esta mensagem" style="font-size:small" />
			</h:panelGrid>
		</h:outputLink>

		<h:outputLink value="javascript:window.close()">
			<h:panelGrid style="text-align:center;">
				<h:graphicImage value="../../public/basic/img/close_b.png"
					title="Fecha esta mensagem" style="border:0" />
				<h:outputText value="Fechar" style="font-size:small"/>
			</h:panelGrid>
		</h:outputLink>
	</h:panelGrid>
    <f:verbatim>
		<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>
	</f:verbatim>


</f:subview>

