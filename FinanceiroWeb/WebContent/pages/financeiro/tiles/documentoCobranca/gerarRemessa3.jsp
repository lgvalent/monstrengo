<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form">
	<h:panelGrid width="100%">
		<h:outputText style="color:red" value="Faça o download do arquivo abaixo e salve-o em um local de seu computador" />
		<h:outputText style="color:red" value="ATENÇÃO! - Caso não salve o arquivo, ele será perdido ao fechar esta janela do sistema" />

		<f:verbatim><br></f:verbatim>
		<h:commandLink  action="#{gerarDocumentoCobrancaRemessaBean.actionDownloadRemessa}">
			<h:outputText value="#{gerarDocumentoCobrancaRemessaBean.process.nomeArquivoRemessa}" style="text-align: center;" title="Clique aqui para realizar o download do arquivo de remessa"/>
		</h:commandLink>

		<f:verbatim><br></f:verbatim>
		<h:commandLink action="#{gerarDocumentoCobrancaRemessaBean.actionRelatorio}">
			<h:outputText value="Clique aqui para visualizar o relatório referente à criação deste arquivo de remessa" />
		</h:commandLink>
	</h:panelGrid>
</h:form>
	