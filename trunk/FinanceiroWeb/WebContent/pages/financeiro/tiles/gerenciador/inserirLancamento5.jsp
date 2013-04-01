<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form target="_blank" rendered="#{menuBean.processMap.CompileDocumentProcess && inserirLancamentoBean.hasModelsDocumentEntity}" style="text-align:left;">
	<h:outputText value="Para gerar um documento do lançamento, selecione o modelo " escape="false"/>
	<h:selectOneMenu id="modelDocumentEntityId" value="#{documentEntityBean.modelDocumentEntityId}" title="Selecione o modelo de documento de entidade">
		<f:selectItems value="#{inserirLancamentoBean.modelsDocumentEntity}"/>
	</h:selectOneMenu>
	<h:outputText value=" e clique em "/>
	<h:commandLink action="#{documentEntityBean.actionCompileFromEntity}">
		<h:outputText value=" visualizar."/>
	   	<f:param name="entityType" value="#{menuBean.infoMap.Lancamento.type.name}"/>
	   	<f:param name="entityId"   value="#{inserirLancamentoBean.process.lancamentoInserido.id}"/>
	</h:commandLink>
</h:form>

<h:panelGrid width="100%">
<h:form id="documentoCobranca">
		<h:outputLabel value="Você também poderá:"/>
		<h:panelGroup rendered="#{menuBean.processMap.ImprimirDocumentoCobrancaProcess && inserirLancamentoBean.possuiDocumentoCobrancaInserido}">
			<h:outputText value="Selecionar uma impressora aqui "/>
   			<h:selectOneMenu value="#{imprimirDocumentoCobrancaBean.process.printerIndex}" id="impressoraCobranca" title="Informe a impressora a ser utilizada">
	   			<f:selectItems value="#{imprimirDocumentoCobrancaBean.process.printerIndexList}"/>
	   		</h:selectOneMenu>
			<h:message  styleClass="errorMessage" for="impressoraCobranca"/>
			<h:commandLink action="#{imprimirDocumentoCobrancaBean.doImprimir}" rendered="#{menuBean.processMap.ImprimirDocumentoCobrancaProcess && inserirLancamentoBean.possuiDocumentoCobrancaInserido}">
				<h:graphicImage value="../financeiro/img/imprimirDocumento_b.png" title="Imprime o documento na impressora selecionada, com o layout de impressão definido no próprio documento." style="border:0"/>
 				<h:outputText value="Imprimir o documento de cobrança."/>
		     	<f:param name="documentoId" value="#{inserirLancamentoBean.process.documentoCobranca.id}"/>
			</h:commandLink>

			<h:outputText value="<br>" escape="false"/>
			<h:commandLink action="#{imprimirDocumentoCobrancaBean.doDownload}" rendered="#{menuBean.processMap.ImprimirDocumentoCobrancaProcess && inserirLancamentoBean.possuiDocumentoCobrancaInserido}">
				<h:graphicImage value="../financeiro/img/imprimirDocumento_b.png" title="Gerar arquivo PDF do documento de cobrança." style="border:0"/>
 				<h:outputText value="Download PDF" title="Gerar arquivo PDF do documento gerado." />
		     	<f:param name="documentoId" value="#{inserirLancamentoBean.process.documentoCobranca.id}"/>
			</h:commandLink>
		</h:panelGroup>
</h:form>

<h:form id="documentoPagamento">
		<h:outputLabel value="Você também poderá:"/>
		<h:panelGroup rendered="#{menuBean.processMap.ImprimirDocumentoPagamentoProcess && inserirLancamentoBean.possuiDocumentoPagamentoInserido}">
			<h:outputText value="Selecionar uma impressora aqui "/>
   			<h:selectOneMenu value="#{imprimirDocumentoPagamentoBean.process.printerIndex}" id="impressoraPagamento" title="Informe a impressora a ser utilizada">
	   			<f:selectItems value="#{imprimirDocumentoPagamentoBean.process.printerIndexList}"/>
	   		</h:selectOneMenu>
			<h:message  styleClass="errorMessage" for="impressoraPagamento"/>
			<h:commandLink action="#{imprimirDocumentoPagamentoBean.doImprimir}" rendered="#{menuBean.processMap.ImprimirDocumentoPagamentoProcess && inserirLancamentoBean.possuiDocumentoPagamentoInserido}">
				<h:graphicImage value="../financeiro/img/imprimirDocumento_b.png" title="Imprime o documento na impressora selecionada, com o layout de impressão definido no próprio documento." style="border:0"/>
 				<h:outputText value="Imprimir o documento de pagamento."/>
		     	<f:param name="documentoId" value="#{inserirLancamentoBean.process.documentoPagamento.id}"/>
			</h:commandLink>

			<h:outputText value="<br>" escape="false"/>
			<h:commandLink action="#{imprimirDocumentoPagamentoBean.doDownload}" rendered="#{menuBean.processMap.ImprimirDocumentoPagamentoProcess && inserirLancamentoBean.possuiDocumentoPagamentoInserido}">
				<h:graphicImage value="../financeiro/img/imprimirDocumento_b.png" title="Gerar arquivo PDF do documento de pagamento." style="border:0"/>
 				<h:outputText value="Download PDF" title="Gerar arquivo PDF do documento gerado." />
		     	<f:param name="documentoId" value="#{inserirLancamentoBean.process.documentoPagamento.id}"/>
			</h:commandLink>
		</h:panelGroup>
</h:form>

<h:form style="text-align:left;">
		<h:panelGroup>
			<h:outputText value=""/>
			<h:commandLink value="Quitar o lançamento inserido." action="#{quitarLancamentoBean.actionInicio}">
		     	<f:param name="lancamentoId" value="#{inserirLancamentoBean.process.lancamentoInserido.id}"/>
		     	<f:param name="lancamentoSaldo" value="#{inserirLancamentoBean.process.lancamentoInserido.object.valor}"/>
			</h:commandLink>
		</h:panelGroup>		
</h:form>

<h:form>
		<h:commandLink value="Inserir um novo lançamento." title="Inicia a inserção de um novo lançamento" action="#{inserirLancamentoBean.actionRecibo}"/>
</h:form>
</h:panelGrid>		