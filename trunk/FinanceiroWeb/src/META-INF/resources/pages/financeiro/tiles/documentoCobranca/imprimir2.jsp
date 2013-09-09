<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<%-- Esta página (imprimir2.jsp) mostra o resultado do processo de visualizar a lista de grcs e
	 oferece a opção de imprimir as guias e gerar etiquetas--%>

<h:panelGrid cellpadding="3" rendered="#{imprimirDocumentosCobrancaBean.currentProcess == null}">
	<h:messages/>
</h:panelGrid>

<h:form id="form1" rendered="#{imprimirDocumentosCobrancaBean.currentProcess != null}" >

	<h:panelGrid columns="4" cellpadding="3">
		<h:outputText value="Ordenar por: " title="Escolha o tipo ordenação para imprimir os documentos."/>
      	<h:selectOneRadio id="ordem" value="#{imprimirDocumentosCobrancaBean.currentProcess.tipoOrdem}">
               <f:selectItems value="#{imprimirDocumentosCobrancaBean.currentProcess.tiposOrdem}"/>
	  	</h:selectOneRadio>
      	<h:message  styleClass="errorMessage" for="ordem" />
		<h:commandButton value="Atualizar lista" action="#{imprimirDocumentosCobrancaBean.doVisualizar}" onclick="this.value='Aguarde...'" title="Atualiza a lista de acordo com o tipo de ordenação escolhido"/>

		<h:outputText value="Instruções adicionais:" title="Insira alguma instrução adicional que será impressa nos documentos"/>
		<%--h:inputTextarea value="#{imprimirDocumentosCobrancaBean.currentProcess.instrucoesAdicionais}" id="instrucoesAdicionais" rows="2" cols="50" /--%>
		<h:inputText value="#{imprimirDocumentosCobrancaBean.currentProcess.instrucoesAdicionais}" id="instrucoesAdicionais" size="50" maxlength="100" required="false"/>
	    <h:message styleClass="errorMessage" for="instrucoesAdicionais" />
		<h:outputText value=""/>
		
	</h:panelGrid>

	<f:verbatim><hr></f:verbatim>
	<h:panelGrid rendered="#{imprimirDocumentosCobrancaBean.currentProcess.visualizando}">

		<h:outputLabel value="Total de registro(s): #{imprimirDocumentosCobrancaBean.currentProcess.size}" />
		<%-- h:messages styleClass="errorMessage" /--%>
		<f:verbatim><br></f:verbatim>
		
		<h:panelGrid rendered="#{imprimirDocumentosCobrancaBean.currentProcess.size>0}">
			<h:panelGroup>
				<h:outputLabel value="Seleção: " />
				<h:commandButton value="Marcar" title="Marca todos os documentos da lista atual" onclick="return checkAll(this.form, 'myCheck')"/>
				<h:commandButton value="Intervalo" title="Marca todos os documentos entre um intervalo definido" onclick="return checkRange(this.form, 'myCheck')"/>
				<h:commandButton value="Desmarcar" title="Desmarca todos os documentos da lista atual"  onclick="return clearAll(this.form, 'myCheck')"/>
				<h:commandButton value="Inverter" title="Inverte a seleção todos os documentos da lista atual" onclick="return inverseAll(this.form, 'myCheck')"/>
			</h:panelGroup>
		</h:panelGrid>		
		
		<h:panelGroup>
			<h:commandButton value="Download PDF" action="#{imprimirDocumentosCobrancaBean.doDownload}"  disabled="#{imprimirDocumentosCobrancaBean.currentProcess.size==0}" onclick="this.value='Aguarde...'" title="Gera um PDF com os documentos selecionados"/>
			<h:selectOneMenu value="#{imprimirDocumentosCobrancaBean.currentProcess.printerIndex}" id="impressora">
				<f:selectItems 	value="#{imprimirDocumentosCobrancaBean.currentProcess.printerIndexList}" />
			</h:selectOneMenu>
			<h:message styleClass="errorMessage" for="impressora" />
			<h:commandButton value="Imprimir documentos" action="#{imprimirDocumentosCobrancaBean.doImprimir}"  disabled="#{imprimirDocumentosCobrancaBean.currentProcess.size==0}" onclick="this.value='Aguarde...'" title="Imprime os documentos selecionados dessa lista na impressora selecionada "/>
   		</h:panelGroup>
		<h:panelGroup>
			<h:commandButton value="Gerar etiquetas da lista" action="#{imprimirDocumentosCobrancaBean.doGerarEtiquetas}"  disabled="#{imprimirDocumentosCobrancaBean.currentProcess.size==0}" onclick="this.value='Aguarde...'" title="Gera as etiquetas para os documentos selecionados"/>
				<h:outputLink value="javascript:popupPageName('../basic/labelView.jsp',0,0, VIEW_LABEL_LIST)" rendered="#{menuBean.crudMap.AddressLabel.canRetrieve}">
					<h:outputText value=" Visualizar "/>
				</h:outputLink>
				<h:outputText value="a lista de etiquetas geradas para os documentos"/>
		</h:panelGroup>
		<h:outputText value="Observação: Selecione somente os documentos de uma mesma categoria para impressão!"/>
		<%-- h:outputText styleClass="errorMessage" value="#{messagesBean.firstMessageSummary}" escape="false" /> --%>			

		<h:dataTable border="1" value="#{imprimirDocumentosCobrancaBean.currentProcess.beanList}"
			var='item' rendered="#{imprimirDocumentosCobrancaBean.currentProcess.size>0}"
			headerClass="tableListHeader" styleClass="tableList"
			rowClasses="tableListRowEven,tableListRowOdd"
			columnClasses="tableListColumn" style="border-collapse: collapse">

			<h:column>
				<h:selectBooleanCheckbox id="myCheck" value="#{item.checked}" />
			</h:column>

			<h:column>
				<f:facet name="header">
					<h:outputText value="Contrato" />
				</f:facet>
				<h:outputLink value="javascript:linkRetrieve('#{menuBean.infoMap.DocumentoCobranca.type.name}', '#{item.id}')"  rendered="#{menuBean.crudMap.Documento.canRetrieve}">
					<h:outputLabel value="#{item.pessoa} (#{item.documento})" />
				</h:outputLink>
				<h:panelGroup rendered="#{!menuBean.crudMap.Documento.canRetrieve}">
					<h:outputLabel value="#{item.pessoa} (#{item.documento})" />
				</h:panelGroup>
			</h:column>

			<h:column>
				<f:facet name="header">
					<h:outputText value="Documento de cobrança" />
				</f:facet>
				<h:outputLabel value="#{item.documentoCobrancaCategoria}" />
			</h:column>

			<h:column>
				<f:facet name="header">
					<h:outputText value="Data Documento" />
				</f:facet>
				<h:outputText value="#{item.dataDocumento.time}" />
			</h:column>

			<h:column>
				<f:facet name="header">
					<h:outputText value="Data Vencimento" />
				</f:facet>
				<h:outputText value="#{item.dataDocumento.time}" />
			</h:column>

			<h:column>
				<f:facet name="header">
					<h:outputText value="Valor" />
				</f:facet>
				<h:outputText value="#{item.valorDocumento}" />
			</h:column>

		</h:dataTable>
	</h:panelGrid>
</h:form>
