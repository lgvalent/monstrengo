<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<h:form id="form">
	<h:panelGrid columns="3" cellpadding="3">
		<h:outputLabel
			value="Selecione o tipo de documento de cobrança que será vinculado a este movimento:" />
		<h:selectOneMenu id="documentoCobrancaCategoriaId"
			value="#{alterarDocumentoCobrancaBean.process.categoriaId}"
			onchange="this.form.submit();">
			<f:selectItems
				value="#{alterarDocumentoCobrancaBean.process.categoriaList}" />
		</h:selectOneMenu>
		<h:message styleClass="errorMessage" for="documentoCobrancaCategoriaId" />
	</h:panelGrid>

	<h:panelGrid
		rendered="#{!alterarDocumentoCobrancaBean.temPropriedadesEditaveis}">
		<h:outputLabel
			value="Nenhuma informação adicional para este documento de cobrança é necessária."
			styleClass="infoMessage" />
	</h:panelGrid>

	<h:panelGrid
		rendered="#{alterarDocumentoCobrancaBean.temPropriedadesEditaveis}">
		<h:outputLabel
			value="Preencha as informações adicionais sobre o documento de cobrança "
			styleClass="infoMessage" />
		<h:dataTable border="0"
			value="#{alterarDocumentoCobrancaBean.propriedadesEditaveis}"
			var='item'
			rendered="#{alterarDocumentoCobrancaBean.temPropriedadesEditaveis}"
			width="100%" styleClass="tableView"
			rowClasses="tableViewRowEven,tableViewRowOdd"
			columnClasses="tableViewColumnEven, tableViewColumnOdd, description"
			style="border-collapse: collapse">

			<h:column id="label"
				rendered="#{item.info.visible && !item.info.calculated && !item.info.readOnly}">
				<h:outputLabel value="*" rendered="#{item.info.required}"
					styleClass="errorMessage" />
				<h:outputLabel value="#{item.info.label}:" title="#{item.info.hint}" />
			</h:column>

			<h:column id="values"
				rendered="#{item.info.visible && !item.info.calculated && !item.info.readOnly}">
				<%-- Exibe lista de escolha para campos primitivos  --%>
				<h:panelGroup
					rendered="#{item.info.editShowList && item.info.primitive}">
					<h:selectOneMenu id="selectPrimitiveInput"
						value="#{item.value.asString}" required="#{item.info.required}">
						<f:selectItems value="#{item.valuesList}" />
					</h:selectOneMenu>
				</h:panelGroup>

				<%-- Exibe caixa de entrada de texto para campos primitivos  --%>
				<h:panelGroup
					rendered="#{!item.info.editShowList && item.info.primitive}">
					<h:inputTextarea id="txtInput" value="#{item.value.asString}"
						title="#{item.info.hint}" required="#{item.info.required}"
						rendered="#{item.info.isString() && item.info.size>100  && !item.info.hasEditMask}"
						disabled="#{item.info.readOnly}" rows="5" cols="40" />
					<h:message for="txtInput" styleClass="errorMessage" />

					<h:inputText id="strInput" value="#{item.value.asString}"
						title="#{item.info.hint}" required="#{item.info.required}"
						rendered="#{item.info.isString() && item.info.size<101  && !item.info.hasEditMask}"
						disabled="#{item.info.readOnly}" size="30"
						maxlength="#{item.info.size}" />
					<h:message for="strInput" styleClass="errorMessage" />

					<h:inputText id="strMaskInput" value="#{item.value.asString}"
						title="#{item.info.hint}" required="#{item.info.required}"
						rendered="#{item.info.isString() && item.info.hasEditMask}"
						disabled="#{item.info.readOnly}" size="30"
						maxlength="#{item.info.editMaskSize}" />
					<h:message for="strMaskInput" styleClass="errorMessage" />

					<h:inputText id="intInput" value="#{item.value.asString}"
						title="#{item.info.hint}" required="#{item.info.required}"
						rendered="#{item.info.isInteger()}" disabled="#{item.info.readOnly}"
						size="10" onkeypress="return keyPressInt(this,event)">
						<f:validateLongRange minimum="-999999999" maximum="999999999" />
					</h:inputText>
					<h:message for="intInput" styleClass="errorMessage" />

					<h:inputText id="longInput" value="#{item.value.asString}"
						title="#{item.info.hint}" required="#{item.info.required}"
						rendered="#{item.info.isLong()}" disabled="#{item.info.readOnly}"
						size="10" onkeypress="return keyPressInt(this,event)">
						<f:validateLongRange minimum="-999999999" maximum="999999999" />
					</h:inputText>
					<h:message for="longInput" styleClass="errorMessage" />

					<h:inputText id="bigInput" value="#{item.value.asString}"
						title="#{item.info.hint}" required="#{item.info.required}"
						rendered="#{item.info.bigDecimal}"
						disabled="#{item.info.readOnly}" size="10"
						onkeypress="return keyPressFloat(this,event)" />
					<h:message for="bigInput" styleClass="errorMessage" />

					<h:inputText id="floatInput" value="#{item.value.asString}"
						title="#{item.info.hint}" required="#{item.info.required}"
						rendered="#{item.info.isFloat()}" disabled="#{item.info.readOnly}"
						size="10" onkeypress="return keyPressFloat(this,event)" />
					<h:message for="floatInput" styleClass="errorMessage" />

					<h:inputText id="doubleInput" value="#{item.value.asString}"
						title="#{item.info.hint}" required="#{item.info.required}"
						rendered="#{item.info.isDouble()}" disabled="#{item.info.readOnly}"
						size="10" onkeypress="return keyPressFloat(this,event)" />
					<h:message for="doubleInput" styleClass="errorMessage" />

					<h:inputText id="dateInput" value="#{item.value.asString}"
						title="#{item.info.hint}" required="#{item.info.required}"
						rendered="#{item.info.calendar}" disabled="#{item.info.readOnly}"
						size="12"
						onblur="return onblurCalendar(this,'#{item.info.editMask}')" />
					<h:message for="dateInput" styleClass="errorMessage" />
				</h:panelGroup>

				<%-- Exibe informações sobre a máscara do campo --%>
				<h:panelGroup
					rendered="#{item.info.hasEditMask && !item.info.isBoolean()}">
					<f:verbatim>
						<br>
					</f:verbatim>
					<h:outputLabel value="#{item.info.editMask}"
						title="O conteúdo do campo só será validado se estiver preenchido de acordo com esta máscara"
						style="color:orange;" />
					<h:outputLink
						value="javascript:popupPage('../basic/help/editMask.html', 500,600)">
						<h:outputText value="[?]" title="Abre uma tela com ajuda" />
					</h:outputLink>
				</h:panelGroup>

				<h:selectBooleanCheckbox id="bolInput"
					value="#{item.value.asBoolean}" title="#{item.info.hint}"
					required="#{item.info.required}" rendered="#{item.info.isBoolean()}"
					disabled="#{item.info.readOnly}">
				</h:selectBooleanCheckbox>
				<h:message for="bolInput" styleClass="errorMessage" />

				<%-- Subclasse em SelectMenu --%>
				<h:panelGroup
					rendered="#{item.info.editShowList && item.info.entity && !item.info.collection}">
					<h:selectOneMenu id="selectSubClassInput" value="#{item.value.id}"
						required="#{item.info.required}">
						<f:selectItems value="#{item.valuesList}" />
					</h:selectOneMenu>

					<h:outputLink
						value="javascript:linkRetrieve('#{item.info.type.name}', '#{item.value.asEntity.id}')"
						rendered="#{not item.value.valueNull}">
						<h:graphicImage value="../../public/basic/img/retrieve.png"
							title="Visualiza os detalhes do registro" style="border:0" />
					</h:outputLink>
					<h:outputLink
						value="javascript:linkCreatePopup('#{item.info.type.name}')">
						<h:graphicImage value="../../public/basic/img/create.png"
							title="Cria um novo registro. Será necessário clicar em validar para atualizar a lista com o novo item"
							style="border:0" />
					</h:outputLink>
					<h:outputLink value="javascript:document.forms[0].submit()">
						<h:graphicImage value="../../public/basic/img/reload.png"
							title="Recarrega a página e a lista de seleção" style="border:0" />
					</h:outputLink>
				</h:panelGroup>
				<h:message for="selectSubClassInput" styleClass="errorMessage" />

				<%-- Subclasse em por link --%>
				<h:panelGroup
					rendered="#{!item.info.editShowList && item.info.entity && !item.info.collection}">
					<h:graphicImage value="../../public/basic/img/query_open_select.png"
						title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
						style="border:0" />
					<h:inputText id="idSubClassInput" value="#{item.value.id}"
						styleClass="queryInputSelectOne"
						title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
						required="#{item.info.required}" size="3"
						onclick="javascript:openSelectOneId('#{item.info.type.name}',this.value,this)"
						onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY);" />

					<h:outputLink
						onclick="javascript:linkRetrieve('#{item.info.type.name}', '#{item.value.asEntity.id}')"
						rendered="#{not item.value.valueNull}">
						<h:outputText value="  #{item.value.asString}"
							title="Visualiza mais informações de #{item.info.label}" />
					</h:outputLink>
				</h:panelGroup>
				<h:message for="idSubClassInput" styleClass="errorMessage" />

				<%-- Lista --%>
				<%-- Primitiva --%>
				<%-- Entidade --%>
				<h:panelGroup
					rendered="#{item.info.collection and item.info.entity}">
					<h:outputLabel
						value="Para adicionar um novo item na lista de #{item.info.label}, pesquise o item aqui" />
					<h:graphicImage value="../../public/basic/img/query_open_select.png"
						title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
						style="border:0" />
					<h:inputText id="idCollInput"
						value="#{item.value.asEntityCollection.runId}"
						styleClass="queryInputSelectOne"
						title="Aperte a tecla 'p' ou dê um clique na caixa de texto para abrir a pesquisa"
						size="3"
						onclick="javascript:openSelectOneId('#{item.info.type.name}',this.value,this)"
						onkeypress="javascript:return checkKeyClick(this, event, KEY_OPEN_QUERY);" />
					<h:outputLabel value=" e depois clique em " />
					<h:commandLink action="#{createBean.doAddToCollection}">
						<h:outputText value="adicionar." />
						<f:param name="collProperty" value="#{item.info.name}" />
					</h:commandLink>
				</h:panelGroup>
				<h:dataTable width="100%"
					value="#{item.value.asEntityCollection.array}" var='col'
					styleClass="tableCollectionUpdate"
					footerClass="tableCollectionUpdateHeader"
					rowClasses="tableViewRowEven,tableViewRowOdd"
					columnClasses="tableViewColumnOdd"
					style="border-collapse: collapse"
					rendered="#{item.info.collection and item.info.entity}">

					<h:column>
						<h:commandLink action="#{createBean.doRemoveFromCollection}">
							<h:graphicImage value="../../public/basic/img/delete.png"
								title="Remove esta entidade da lista" style="border:0" />
							<f:param name="collProperty" value="#{item.info.name}" />
							<f:param name="collItemId" value="#{col.id}" />
						</h:commandLink>

						<h:outputLink
							value="javascript:linkRetrieve('#{col.info.type.name}', '#{col.id}')">
							<h:outputText value="#{col.object}:" title="#{col.info.hint}" />
						</h:outputLink>
					</h:column>
				</h:dataTable>
			</h:column>

			<h:column id="help"
				rendered="#{item.info.visible && !item.info.calculated && !item.info.readOnly}">
				<h:outputText value="#{item.info.description}" escape="false" />
			</h:column>

		</h:dataTable>
	</h:panelGrid>

	<h:panelGrid columns="2">
		<h:commandButton value="Cancelar"
			onclick="javascript:if(confirm('Confirma abandonar as alterações?'))window.close();" />
		<h:commandButton value="Avançar >>"
			action="#{alterarDocumentoCobrancaBean.doVisualizar}" />
	</h:panelGrid>
</h:form>
