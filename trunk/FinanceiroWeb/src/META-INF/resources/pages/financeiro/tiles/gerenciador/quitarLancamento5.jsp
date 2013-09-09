<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form target="_blank" style="text-align:left;">
	<h:panelGroup rendered="#{menuBean.processMap.CompileDocumentProcess && quitarLancamentoBean.hasModelsDocumentEntity}">
		<h:outputText value="Para gerar um documento do movimento inserido, selecione o modelo na lista e clique em [doc]:" escape="false"/>
		<h:selectOneMenu id="modelDocumentEntityId" value="#{documentEntityBean.modelDocumentEntityId}" title="Selecione o modelo de documento de entidade">
			<f:selectItems value="#{quitarLancamentoBean.modelsDocumentEntity}"/>
		</h:selectOneMenu>
	</h:panelGroup>
	
	<h:panelGroup rendered="#{menuBean.processMap.ImprimirDocumentoPagamentoProcess}">
		<h:outputText value="Para imprimir o documento de pagamento diretamente na impressora, selecione a impressora na lista e clique em [imp]:" escape="false"/>
		<h:selectOneMenu value="#{imprimirDocumentoPagamentoBean.process.printerIndex}" id="impressora" title="Informe a impressora a ser utilizada">
			<f:selectItems value="#{imprimirDocumentoPagamentoBean.process.printerIndexList}"/>
		</h:selectOneMenu>
	</h:panelGroup>
	
	<h:dataTable id="tableMovimentos"
		  			 border="1" 
		             value="#{quitarLancamentoBean.process.movimentosInseridos.array}" 
		             var='item'
		             headerClass="tableListHeader"
		             footerClass="tableListFooter"
		             styleClass="tableList"
		             rowClasses="tableListRowEven,tableListRowOdd"
		             columnClasses="tableListColumn"
		             style="border-collapse: collapse"
		             width="100%">

				<h:column >
					<f:facet name="header">
						<h:outputText value="Lançamentos"/>
   	                </f:facet>
					<h:outputText value="#{item.object.lancamento}"/>
					<h:outputText value="<br>#{item}" escape="false"/>
				</h:column>
				<h:column >
					<f:facet name="header">
						<h:outputText value="Ações"/>
   	                </f:facet>
					<h:commandLink action="#{documentEntityBean.actionCompileFromEntity}" rendered="#{menuBean.processMap.CompileDocumentProcess && quitarLancamentoBean.hasModelsDocumentEntity}">
						<h:outputText value=" [doc] " title="Gera um documento do movimento quitado baseado no modelo acima selecionado"/>
	   					<f:param name="entityType" value="#{menuBean.infoMap.LancamentoMovimento.type.name}"/>
	   					<f:param name="entityId"   value="#{item.id}"/>
					</h:commandLink>
					<h:panelGroup rendered="#{menuBean.processMap.ImprimirDocumentoPagamentoProcess}">
						<h:commandLink action="#{imprimirDocumentoPagamentoBean.doDownload}" rendered="#{item.object.documentoPagamento!=null}">
							<h:outputText value=" [pdf] " title="Gera um arquivo PDF para impressão do documento de pagamento"/>
					     	<f:param name="documentoId" value="#{item.object.documentoPagamento.id}"/>
						</h:commandLink>
						<h:commandLink action="#{imprimirDocumentoPagamentoBean.doImprimir}" rendered="#{item.object.documentoPagamento!=null}">
							<h:outputText value=" [imp] " title="Imprime diretamente na impressora selecionada acima "/>
					     	<f:param name="documentoId" value="#{quitarLancamentoBean.process.documentoPagamentoId}"/>
						</h:commandLink>
						<h:commandLink action="#{imprimirDocumentoPagamentoBean.actionPrepararDocumento}" rendered="#{item.object.documentoPagamento!=null}">
							<h:outputText value=" [ver] " title="Abre a tela para seleção de modelos de layout e impressoras "/>
					     	<f:param name="documentoId" value="#{quitarLancamentoBean.process.documentoPagamentoId}"/>
						</h:commandLink>
					</h:panelGroup>
				</h:column>
		</h:dataTable>
</h:form>

<h:form id="form">
	<h:panelGrid width="100%">
<%-- Lucio 20110405: Tudo agora é acessado pela tabela acima
		<h:panelGroup rendered="#{menuBean.processMap.ImprimirDocumentoProcess && quitarLancamentoBean.process.documentoPagamentoId!=-1}">

			<h:outputText value="Você pode selecionar um impressora aqui "/>
   			<h:selectOneMenu value="#{imprimirDocumentoBean.process.printerIndex}" id="impressora" title="Informe a impressora a ser utilizada">
   				<f:selectItems value="#{imprimirDocumentoBean.process.printersIndex}"/>
	   		</h:selectOneMenu>
		   	<h:message  styleClass="errorMessage" for="impressora"/>

			<h:commandLink action="#{imprimirDocumentoPagamentoBean.actionPrepararDocumento}">
				<h:graphicImage value="../financeiro/img/imprimirDocumento_b.png" title="Imprime o documento de cobrança na impressora selecionada" style="border:0"/>
 				<h:outputText value="imprimir o documento #{quitarLancamentoBean.process.documentoPagamentoCategoria}" title="Imprimir o documento na impressora selecionada" />
		     	<f:param name="documentoId" value="#{quitarLancamentoBean.process.documentoPagamentoId}"/>
			</h:commandLink>
		</h:panelGroup>

		<h:panelGroup>
			<h:outputText value="Você pode "/>
			<h:outputLink value="javascript:popupPage('../financeiro/gerenciadorReciboMovimento.jsp?movimentoId=#{quitarLancamentoBean.ultimoMovimentoInserido}',600,800)">
				<h:graphicImage value="../financeiro/img/reciboMovimento.png" title="Exibe o recibo para impressão" style="border:0"/>
				<h:outputText value=" imprimir um recibo do movimento."/>
			</h:outputLink>
		</h:panelGroup>
		<h:panelGroup>
			<h:outputText value="Ou ainda, você pode "/>
			<h:outputLink value="javascript:popupPage('../financeiro/gerenciadorReciboMovimento.jsp?documentoId=#{quitarLancamentoBean.ultimoDocumentoInserido}',600,800)">
				<h:graphicImage value="../financeiro/img/reciboDocumento.png" title="Exibe o recibo para impressão" style="border:0"/>
				<h:outputText value=" imprimir um recibo do documento."/>
			</h:outputLink>
		</h:panelGroup>
		<h:outputText value=""/>
 --%>	
		<h:outputLink value="" onclick="javascript:window.close()">
			<h:outputText value="Voltar para a Posição do Cliente."/>
		</h:outputLink>
		<h:outputText value=""/>
		<h:commandLink value="Inserir um novo lançamento." title="Inicia a inserção de um novo lançamento" action="#{inserirLancamentoBean.actionRecibo}"/>
	</h:panelGrid>
</h:form>