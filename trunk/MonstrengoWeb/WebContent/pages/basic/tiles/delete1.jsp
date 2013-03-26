<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>

<%--Esta pagina mostra as dependencias que a entidade a ser deletada possui.
Se a entidade não possuir dependencias com outras entidades,então coloca-se o botão Continuar
redirecionando para o delete2.jsp--%>

<f:subview id="delete1">
	<h:form>
		<%-- Sincroniza a atual entidade manipulada pela view --%>
		<h:inputHidden immediate="true" id="currentEntityKey" value="#{deleteBean.currentEntityKey}" validator="#{deleteBean.validateCurrentEntityKey}" />
		<%-- Fornece um acesso para que o BEAN invalide um valor da view JSF e forçe a reconstrução quando necessário --%>
		<h:inputHidden immediate="true" binding="#{deleteBean.inputCurrentEntityKey}" />

		<h:panelGrid rendered="#{deleteBean.hasDependences}" width="100%">
			<h:panelGroup>
				<h:graphicImage value="../basic/img/alert_icon.png"/>
				<h:outputText value=" A entidade <b>(#{deleteBean.currentEntity.info.label}) #{deleteBean.currentEntity}</b> está vinculada às entidades listadas abaixo que impedem a sua exclusão do sistema.<br>" escape="false"/>
				<h:outputText value="<p>Você deve excluir primeiro os elementos da listagem abaixo, para então poder excluir esta entidade." escape="false"/>
				<h:outputText value=" Ou ainda, você pode remover a dependência editando ela " escape="false" rendered="#{deleteBean.canUpdate}"/>
				<h:outputLink value="javascript:linkUpdate('#{deleteBean.currentEntity.info.type.name}', '#{deleteBean.currentEntity.id}')" rendered="#{deleteBean.canUpdate}">
					<h:graphicImage value="../basic/img/update.png" title="Altera os dados do registro" style="border:0"/>
					<h:outputText value="aqui." escape="false" rendered="#{deleteBean.canUpdate}"/>
				</h:outputLink>
			</h:panelGroup>
		</h:panelGrid>
		
		<h:panelGrid rendered="#{!deleteBean.hasDependences}">
			<h:outputText value="Não há mais registros vinculados a <b>(#{deleteBean.currentEntity.info.label}) #{deleteBean.currentEntity}</b>" escape="false"/>
			<h:commandButton value="Continuar >>" action="delete2"/>
		</h:panelGrid>
		<h:panelGroup rendered="#{deleteBean.hasDependences}">


			<%--obtendo a estrutura DependecesBean que possui as entidades dependentes da entidade a ser excluida--%>
			<h:dataTable value="#{deleteBean.process.dependencesBean}" 
						 var='dependents'
						 width="100%" 
						 border="1"
						 style="border-collapse: collapse"
						 styleClass="tableList">
				<h:column>
					<h:outputText value="Entidade dependente: <b>#{dependents.entity.label}</b><br>" escape="false"/>
					<h:outputText value="Através da propriedade: #{dependents.property.label}" />

					<h:dataTable value="#{dependents.entityList.list}" 
								 var='item' 
								 width="100%"
								 rowClasses="tableViewRowEven,tableViewRowOdd"
								 style="border-collapse: collapse; font-size:small">
						<h:column>
							<h:selectBooleanCheckbox id="myCheck" value="#{item.selected}"/>
				  			<h:commandLink action="#{deleteBean.actionDeleteChild}" rendered="#{item.info.canDelete}">
								<h:graphicImage value="../basic/img/delete.png" title="Apaga o registro" style="border:0"/>
						     	<f:param name="entityType" value="#{item.info.type.name}"/>
						     	<f:param name="entityId"   value="#{item.id}"/>
							</h:commandLink>
							<h:outputLink value="javascript:linkRetrieve('#{item.object.class.name}', '#{item.id}')" rendered="#{item.info.canRetrieve}">
								<h:graphicImage value="../basic/img/retrieve.png" title="Visualiza os detalhes do registro" style="border:0"/>
							</h:outputLink>
							<h:graphicImage value="../basic/img/retrieve_d.png" title="Você não possui direitos para visualizar os dados do registro" style="border:0" rendered="#{!item.info.canRetrieve}"/>
							<%--	
							usando o commandLink, não abre outra tela, mas não há erro de atualização de entidades (erro de transaction)
							usando o outputLink, abre outra tela, mas o erro por link da atualização de entidades permanece
												
							<h:outputLink value="javascript:linkDelete('#{item.info.type.name}', '#{item.id}')" rendered="#{item.info.canDelete}">
								<h:graphicImage value="../basic/img/delete.png" title="Apaga o registro" style="border:0" />
							</h:outputLink>
							--%>
							<h:graphicImage value="../basic/img/delete_d.png" title="Você não possui direitos para apagar o registro" style="border:0" rendered="#{!item.info.canDelete}"/>
						</h:column>

						<h:column>
							<h:outputText value="#{item}"/>
						</h:column>
					</h:dataTable>
				</h:column>
			</h:dataTable>

			<h:graphicImage value="../agenda/img/arrow.png"/>
			<h:commandButton value="Todas" title="Marca todas as dependências para exclusão" onclick="return checkAll(this.form, 'myCheck')"/>
			<h:commandButton value="Nenhuma" title="Desmarca todas dependências" onclick="return clearAll(this.form, 'myCheck')"/>

			<h:panelGrid columns="1">
				<h:outputText value="Descreva uma justificativa para excluir as dependências:" />
				<h:inputTextarea id="justification" value="#{deleteBean.justification}" style="width: 420px">
					<f:validateLength minimum="5" />
				</h:inputTextarea>
				<h:message for="justification" styleClass="errorMessage" />
				<h:commandButton value="Excluir dependências marcadas" onclick="javascript:return confirm('Confirma a exclusão das dependências selecionadas?')" action="#{deleteBean.actionDeleteChilds}" />
			</h:panelGrid>
		</h:panelGroup>
				
	</h:form>

</f:subview>
