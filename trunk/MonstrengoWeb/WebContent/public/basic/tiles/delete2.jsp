<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>

<%--Esta página mostra a entidade SEM dependencias de outras entidades, ou seja, 
quando a lista de entidades dependentes retorna vazia. Então, coloca-se o botão Continuar--%>
<f:subview id="delete2">
	<h:form>
		<%-- Sincroniza a atual entidade manipulada pela view --%>
		<h:inputHidden immediate="true" id="currentEntityKey"value="#{deleteBean.currentEntityKey}" validator="#{deleteBean.validateCurrentEntityKey}" />
		<%-- Fornece um acesso para que o BEAN invalide um valor da view JSF e forçe a reconstrução quando necessário --%>
		<h:inputHidden immediate="true"	binding="#{deleteBean.inputCurrentEntityKey}" />

		<h:panelGrid columns="1">
			<h:outputText styleClass="title" value="Você está excluindo um registro de <i>#{deleteBean.currentEntity.info.label}</i>:<br>" escape="false" />
		</h:panelGrid>

		<h:dataTable value="#{deleteBean.currentEntity.groupsProperties}" var='group' width="100%">
			<h:column>
				<f:verbatim>
					<fieldset><legend class="tableViewDeleteHeader">
				</f:verbatim>
				<h:outputLabel value="#{group.info.label}" />
				<f:verbatim>
					</legend>
				</f:verbatim>

				<h:dataTable value="#{group.properties}" 
					width="100%"
					var='item'
					styleClass="tableView"
					rowClasses="tableViewRowEven,tableViewRowOdd"
					columnClasses="tableViewColumnEven, tableViewColumnOdd"
					style="border-collapse: collapse">

					<h:column>
						<h:outputLabel value="#{item.info.label}:" title="#{item.info.hint}" rendered="#{item.info.visible}" />
					</h:column>

					<h:column>
						<h:outputText value="#{item.value.asString}" title="#{item.info.hint}" rendered="#{item.info.visible}" />
					</h:column>
				</h:dataTable>
			</h:column>
		</h:dataTable>

		<h:panelGrid columns="1">
			<h:outputText value="Descreva uma justificativa para excluir o registro:" />
			<h:inputTextarea id="justification" required="true" value="#{deleteBean.justification}" style="width: 420px">
				<f:validateLength minimum="5" />
			</h:inputTextarea>
			<h:message for="justification" styleClass="errorMessage" />
		</h:panelGrid>

		<f:verbatim>
			<br>
		</f:verbatim>

		<%--h:commandButton value="Cancelar" action="#{deleteBean.actionCancel}" immediate="true"/--%>
		<h:commandButton value="Cancelar" onclick="javascript:window.close();" />
		<h:commandButton value="Confirmar" onclick="javascript:return confirm('Confirma a exclusão do registro?')" action="#{deleteBean.actionConfirm}" />

	</h:form>

</f:subview>
