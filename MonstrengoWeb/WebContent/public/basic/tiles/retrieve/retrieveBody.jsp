<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<f:subview id="rtvBdy">
 	<h:form >
		<%-- Sincroniza a atual entidade manipulada pela view --%>''
		<h:inputHidden immediate="true" id="currentEntityKey" value="#{retrieveBean.currentEntityKey}" validator="#{retrieveBean.validateCurrentEntityKey}"/>
		<%-- Fornece um acesso para que o BEAN invalide um valor da view JSF e forçe a reconstrução quando necessário --%>
		<h:inputHidden immediate="true" binding="#{retrieveBean.inputCurrentEntityKey}"/>

	    <%--f:verbatim>
			<SCRIPT>borderCreator.initDarkBorder('100%', '', 'left', 'top')</SCRIPT>
		</f:verbatim --%>

	    <%-- Lucio 20090819: Retirado até achar uma solução de personalização por usuário para que ele escolha usar ou não os popups
	        f:verbatim>
    		<div class="floatMenu noprint">
                ir para
                <img width="16" height="16" src="../../public/basic/img/close.png" style="border: 0pt none ;" title="Fechar" onmousedown="this.parentNode.style.display='none'"> 
    			<a href="#" >topo</a>
    			<a href="#labels" >etiquetas</a>
    			<a href="#documents" >documentos</a>
    			<a href="#actions" >ações</a>
    		</div>
		</f:verbatim--%>
		
		<h:outputText styleClass="title" value="Você está visualizando a entidade <i>#{retrieveBean.currentEntity.info.label}</i>: " escape="false"/>
		<h:panelGrid columns="2" width="100%" style="color:#{retrieveBean.currentEntity.info.colorName}">
			<h:panelGrid styleClass="noprint" columns="7" style="vertical-align: top">
			    	<h:outputLink value="javascript:linkCheckAuditCrud('#{retrieveBean.currentEntity.info.type.name}', #{retrieveBean.currentEntity.id})" rendered="#{menuBean.crudMap.AuditCrudRegister.canRetrieve}">
						<h:graphicImage value="../../public/basic/img/checkAuditCrud.png" title="Verifica quem criou e alterou este registro" style="border:0" />
				    </h:outputLink>

	  				<h:commandLink action="#{updateBean.actionEdit}" rendered="#{retrieveBean.canUpdate}">
						<h:graphicImage value="../../public/basic/img/update.png" title="Altera os dados do registro" style="border:0"/>
				     	<f:param name="entityType" value="#{retrieveBean.currentEntity.info.type.name}"/>
				     	<f:param name="entityId"   value="#{retrieveBean.entityParam.id}"/>
					</h:commandLink>
					<h:graphicImage value="../../public/basic/img/update_d.png" title="Você não possui direitos para alterar os dados do registro" style="border:0" rendered="#{!retrieveBean.canUpdate}"/>
					
		  			<h:commandLink action="#{deleteBean.actionDelete}" rendered="#{retrieveBean.canDelete}">
						<h:graphicImage value="../../public/basic/img/delete.png" title="Apaga o registro" style="border:0"/>
				     	<f:param name="entityType" value="#{retrieveBean.currentEntity.info.type.name}"/>
				     	<f:param name="entityId"   value="#{retrieveBean.entityParam.id}"/>
					</h:commandLink>
					<h:graphicImage value="../../public/basic/img/delete_d.png" title="Você não possui direitos para apagar o registro" style="border:0" rendered="#{!retrieveBean.canDelete}"/>
				
		  			<h:commandLink action="#{createBean.actionCreate}" rendered="#{retrieveBean.canCreate}">
						<h:graphicImage value="../../public/basic/img/create.png" title="Cria um novo registro" style="border:0"/>
				     	<f:param name="entityType" value="#{retrieveBean.currentEntity.info.type.name}"/>
				     	<f:param name="entityId" value="#{retrieveBean.entityParam.id}"/>
					</h:commandLink>
					<h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!retrieveBean.canCreate}"/>
				
					<h:outputLink value="javascript: closeSelectOne('#{retrieveBean.currentParams.selectOneDest}','#{retrieveBean.selectPropertyValue}',window)" rendered="#{retrieveBean.selectOneActive}">
						<h:graphicImage style="border: none;" value="../../public/basic/img/query_select_one.png" alt="Clique para selecionar este registro e voltar para a tela anterior"/>
					</h:outputLink>

		  			<h:outputLink value="javascript:window.close()" rendered="#{!retrieveBean.selectOneActive}">
						<h:graphicImage value="../../public/basic/img/close.png" title="Fecha esta visualização" style="border:0"/>
					</h:outputLink>
			</h:panelGrid>
			<h:outputText value="#{retrieveBean.currentEntity.info.description}<br>" escape="false" styleClass="description"/>
		</h:panelGrid>

	    <%--f:verbatim>
			<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>
		</f:verbatim --%>

		<h:dataTable 
			value="#{retrieveBean.currentEntity.groupsProperties}" 
			var="group"
			width="100%"
			
			>

			<h:column>
				<f:verbatim>
					<fieldset><legend class="tableViewHeader">
				</f:verbatim>
					<h:outputLabel value="#{group.info.label}"/>
				<f:verbatim>
					</legend>
				</f:verbatim>

				<h:dataTable 
					value="#{group.properties}" 
					var='item'
				    styleClass="tableView"
				    rowClasses="tableViewRowEven,tableViewRowOdd"
				    columnClasses="tableViewColumnEven, tableViewColumnOdd"
				    style="border-collapse: collapse;">

					<h:column>
						<h:outputText value="#{item.info.label}" title="#{item.info.hint}" rendered="#{item.info.visible}" style="color:#{item.info.colorName};"/>
					</h:column>

					<h:column>
					
						<h:outputText value="#{item.value.asString}" title="#{item.info.hint}" rendered="#{(item.info.string or item.info.long or item.info.calendar or item.info.bigDecimal or item.info.float or item.info.double or item.info.integer or item.info.enum) and item.info.visible}" />
						
						<h:selectBooleanCheckbox disabled="true" value="#{item.value.asBoolean}" title="#{item.info.hint}" rendered="#{item.info.boolean and item.info.visible}"/>

						<%-- SET --%>
						<%-- Primitiva --%>

						<%-- Entidade --%>
						<h:dataTable width="100%" 
							 value="#{item.value.asEntitySet.array}" 
							 var='col' 
							 styleClass="tableCollectionUpdate" 
							 footerClass="tableCollectionUpdateHeader" 
							 rowClasses="tableViewRowEven,tableViewRowOdd" 
							 columnClasses="tableViewColumnOdd"
							 style="border-collapse: collapse" 
							 rendered="#{item.info.set and item.info.entity and (item.value.asEntitySet.size != 0)}">

							<h:column>
								<f:facet name="footer">
									<h:outputLink value="javascript:linkQueryParent('#{retrieveBean.currentEntity.info.type.name}', '#{retrieveBean.currentEntity.id}', '#{item.info.name}')">
										<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar na lista de #{item.info.label}" style="border:0" />
										<h:outputText value="Pesquisar na lista de #{item.info.label}" />
									</h:outputLink>
								</f:facet>

								<h:outputLink value="javascript:linkRetrieve('#{col.info.type.name}', '#{col.id}')">
									<h:outputText value="#{col.object}" title="#{col.info.hint}" />
								</h:outputLink>
							</h:column>
						</h:dataTable>
				
						<h:dataTable width="100%" 
							 value="#{item.value.asEntityList.list}" 
							 var='col' 
							 styleClass="tableCollectionUpdate" 
							 footerClass="tableCollectionUpdateHeader" 
							 rowClasses="tableViewRowEven,tableViewRowOdd" 
							 columnClasses="tableViewColumnOdd"
							 style="border-collapse: collapse" 
							 rendered="#{item.info.list and item.info.entity and (item.value.asEntityList.size > 0)}">

							<h:column>
								<f:facet name="footer">
									<h:outputLink value="javascript:linkQueryParent('#{retrieveBean.currentEntity.info.type.name}', '#{retrieveBean.currentEntity.id}', '#{item.info.name}')">
										<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar na lista de #{item.info.label}" style="border:0" />
										<h:outputText value="Pesquisar na lista de #{item.info.label}" />
									</h:outputLink>
								</f:facet>

								<h:outputLink value="javascript:linkRetrieve('#{col.info.type.name}', '#{col.id}')">
									<h:outputText value="#{col.object}" title="#{col.info.hint}" />
								</h:outputLink>
							</h:column>
						</h:dataTable>
				
						<h:commandLink action="#{retrieveBean.actionView}" rendered="#{item.info.entity and not item.info.collection and item.info.visible}">
							<h:outputText value="#{item.value.asString}" title="#{item.info.hint}" />
							<f:param name="parentType" value="#{retrieveBean.currentEntity.info.type.name}" />
							<f:param name="parentId" value="#{retrieveBean.currentEntity.id}" />
							<f:param name="parentProperty" value="#{item.info.name}" />
						</h:commandLink>

					</h:column>
				</h:dataTable>
				
				<f:verbatim>
					</fieldset>
				</f:verbatim>
			</h:column>
		</h:dataTable>

	    <%-- f:verbatim>
			<SCRIPT>borderCreator.initDarkBorder('100%', '', 'center', 'top')</SCRIPT>
		</f:verbatim --%>
		<h:panelGrid columns="1" styleClass="noprint" bgcolor="#{retrieveBean.currentEntity.info.colorName}" width="100%">
			<h:panelGrid columns="7" styleClass="noprint" style="background-color:#{retrieveBean.currentEntity.info.colorName}">

<%-- POR LINK
 					<h:outputLink value="javascript:linkUpdate('#{retrieveBean.currentEntity.info.type.name}', '#{retrieveBean.currentEntity.id}')" rendered="#{retrieveBean.canUpdate}">
						<h:graphicImage value="../../public/basic/img/update.png" title="Altera os dados do registro" style="border:0"/>
					</h:outputLink>
					<h:graphicImage value="../../public/basic/img/update_d.png" title="Você não possui direitos para alterar os dados do registro" style="border:0" rendered="#{!retrieveBean.canUpdate}"/>

										
					<h:outputLink value="javascript:linkDelete('#{retrieveBean.currentEntity.info.type.name}', '#{retrieveBean.currentEntity.id}')" rendered="#{retrieveBean.canDelete}">
						<h:graphicImage value="../../public/basic/img/delete.png" title="Apaga o registro" style="border:0"/>
					</h:outputLink>
					<h:graphicImage value="../../public/basic/img/delete_d.png" title="Você não possui direitos para apagar o registro" style="border:0" rendered="#{!retrieveBean.canDelete}"/>
--%>

<%-- POR ACTION --%>
			    	<h:outputLink value="javascript:linkCheckAuditCrud('#{retrieveBean.currentEntity.info.type.name}', #{retrieveBean.currentEntity.id})" rendered="#{menuBean.crudMap.AuditCrudRegister.canRetrieve}">
						<h:graphicImage value="../../public/basic/img/checkAuditCrud_b.png" title="Verifica quem criou e alterou este registro" style="border:0" />
				    </h:outputLink>

	  				<h:commandLink action="#{updateBean.actionEdit}" rendered="#{retrieveBean.canUpdate}">
						<h:graphicImage value="../../public/basic/img/update_b.png" title="Altera os dados do registro" style="border:0"/>
				     	<f:param name="entityType" value="#{retrieveBean.currentEntity.info.type.name}"/>
				     	<f:param name="entityId"   value="#{retrieveBean.entityParam.id}"/>
					</h:commandLink>
					<h:graphicImage value="../../public/basic/img/update_b_d.png" title="Você não possui direitos para alterar os dados do registro" style="border:0" rendered="#{!retrieveBean.canUpdate}"/>
					
		  			<h:commandLink action="#{deleteBean.actionDelete}" rendered="#{retrieveBean.canDelete}">
						<h:graphicImage value="../../public/basic/img/delete_b.png" title="Apaga o registro" style="border:0"/>
				     	<f:param name="entityType" value="#{retrieveBean.currentEntity.info.type.name}"/>
				     	<f:param name="entityId"   value="#{retrieveBean.entityParam.id}"/>
					</h:commandLink>
					<h:graphicImage value="../../public/basic/img/delete_b_d.png" title="Você não possui direitos para apagar o registro" style="border:0" rendered="#{!retrieveBean.canDelete}"/>
				
		  			<h:commandLink action="#{createBean.actionCreate}" rendered="#{retrieveBean.canCreate}">
						<h:graphicImage value="../../public/basic/img/create_b.png" title="Cria um novo registro" style="border:0"/>
				     	<f:param name="entityType" value="#{retrieveBean.currentEntity.info.type.name}"/>
				     	<f:param name="entityId" value="#{retrieveBean.entityParam.id}"/>
					</h:commandLink>
					<h:graphicImage value="../../public/basic/img/create_b_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!retrieveBean.canCreate}"/>
				
					<h:outputLink value="javascript: closeSelectOne('#{retrieveBean.currentParams.selectOneDest}','#{retrieveBean.selectPropertyValue}',window)" rendered="#{retrieveBean.selectOneActive}">
						<h:graphicImage style="border: none;" value="../../public/basic/img/query_select_one_b.png" alt="Clique para selecionar este registro e voltar para a tela anterior"/>
					</h:outputLink>

		  			<h:outputLink value="javascript:window.close()" rendered="#{!retrieveBean.selectOneActive}">
						<h:graphicImage value="../../public/basic/img/close_b.png" title="Fecha esta visualização" style="border:0"/>
					</h:outputLink>
			</h:panelGrid>
		</h:panelGrid>
	    <%-- f:verbatim>
			<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>
		</f:verbatim --%>		
	</h:form>
 	<h:form rendered="#{menuBean.crudMap.AddressLabel.canCreate && labelEntityBean.hasModelsLabelEntity(retrieveBean.currentEntity)}" styleClass="noprint">
	    <%-- f:verbatim>
			<SCRIPT>borderCreator.initDarkBorder('100%', '', 'center', 'top')</SCRIPT>
		</f:verbatim --%>
	    <f:verbatim>
    		<a name="labels"/>
		</f:verbatim>
		<h:panelGrid columns="2">
			<h:graphicImage value="../../public/basic/img/label.png" style="border:0"/>
			<h:panelGroup>
				<h:outputText value="Para gerar uma etiqueta de <i>#{retrieveBean.currentEntity.info.label}</i>, selecione o modelo de etiqueta " escape="false"/>
				<h:selectOneMenu id="modelLabelEntityId" value="#{labelEntityBean.modelLabelEntityId}" title="Selecione o modelo de etiqueta de entidade">
					<f:selectItems value="#{labelEntityBean.getModelsLabelEntity(retrieveBean.currentEntity)}"/>
				</h:selectOneMenu>
				<h:outputText value=" e o grupo "/>
				<h:selectOneMenu id="addressLabelGroupId" value="#{labelEntityBean.addressLabelGroupId}" title="Selecione o grupo desejado para a etiqueta gerada">
					<f:selectItems value="#{labelEntityBean.addressLabelGroupList}"/>
				</h:selectOneMenu>
				<h:outputText value=" e clique em "/>
				<h:commandLink action="#{labelEntityBean.doCreate(retrieveBean.currentEntity.info.type.name, retrieveBean.entityParam.id)}">
					<h:outputText value=" GERAR "/>
				</h:commandLink>
				<h:outputText value=". Uma etiqueta será inserida na "/>
		    	<h:outputLink onclick="javascript:popupPage('../basic/labelView.jsp',800,600, VIEW_LABEL_LIST)" rendered="#{menuBean.crudMap.AddressLabel.canRetrieve}">
					<h:outputText value=" Lista de Etiquetas"/>
			    </h:outputLink>
		    </h:panelGroup>
		</h:panelGrid>
	    <%-- f:verbatim>
			<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>
		</f:verbatim --%>
 	</h:form>
	
 	<h:form target="_blank" styleClass="noprint">
	    <%-- f:verbatim>
			<SCRIPT>borderCreator.initDarkBorder('100%', '', 'center', 'top')</SCRIPT>
		</f:verbatim --%>
	    <f:verbatim>
    		<a name="documents"/>
		</f:verbatim>
		<h:panelGrid columns="2" rendered="#{menuBean.processMap.CompileDocumentProcess && documentEntityBean.hasModelsDocumentEntity(retrieveBean.currentEntity)}">
			<h:graphicImage value="../../public/basic/img/document.png" style="border:0"/>
			<h:panelGroup>
				<h:outputText value="Para gerar um documento de <i>#{retrieveBean.currentEntity.info.label}</i>, selecione o modelo de documento " escape="false"/>
				<h:selectOneMenu id="modelDocumentEntityId" value="#{documentEntityBean.modelDocumentEntityId}" title="Selecione o modelo de documento de entidade">
					<f:selectItems value="#{documentEntityBean.getModelsDocumentEntity(retrieveBean.currentEntity)}"/>
				</h:selectOneMenu>
				<h:outputText value=" e clique em "/>
				<h:commandLink action="#{documentEntityBean.actionCompileFromEntity(retrieveBean.currentEntity.info.type.name, retrieveBean.entityParam.id)}">
					<h:outputText value=" visualizar "/>
			    	<f:param name="entityType" value="#{retrieveBean.currentEntity.info.type.name}"/>
			    	<f:param name="entityId"   value="#{retrieveBean.entityParam.id}"/>
				</h:commandLink>
		    </h:panelGroup>
		</h:panelGrid>
	    <%-- f:verbatim>
			<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>
		</f:verbatim --%>
 	</h:form>
 	
 	<h:form target="_blank" styleClass="noprint">
		<%-- Sincroniza a atual entidade manipulada pela view --%>
		<h:inputHidden immediate="true" id="currentEntityKey" value="#{retrieveBean.currentEntityKey}" validator="#{retrieveBean.validateCurrentEntityKey}"/>
		<%-- Fornece um acesso para que o BEAN invalide um valor da view JSF e forçe a reconstrução quando necessário --%>
		<h:inputHidden immediate="true" binding="#{retrieveBean.inputCurrentEntityKey}"/>

	    <f:verbatim>
	    	<a name="actions"/>
			<SCRIPT>borderCreator.initDarkBorder('100%', '', 'left', 'top')</SCRIPT>
		</f:verbatim>
		<h:dataTable
			value="#{runnableProcessBean.getRunnableViewsForUser(retrieveBean.currentEntity)}"
			var="runnableView">
			
			<h:column>
				<h:panelGroup rendered="#{runnableView.runnableProcessEntry.disabled}">
					<h:graphicImage value="../../public/basic/img/run_task_d.png" style="border:0"/>
					<h:outputText value="#{runnableView.runnableProcessEntry.info.label()}" title="#{runnableView.runnableProcessEntry.info.hint()}"/>
					<h:outputText value="<br>#{runnableView.runnableProcessEntry.message.message}" styleClass="description" escape="false"/>
				</h:panelGroup>
				<h:panelGroup rendered="#{!runnableView.runnableProcessEntry.disabled}">
					<h:commandLink action="#{runnableProcessBean.actionActivateView(runnableView.viewName, retrieveBean.currentEntity)}" >
						<h:graphicImage value="../../public/basic/img/run_task.png" style="border:0" />
						<h:outputText value="#{runnableView.runnableProcessEntry.info.label()}"/>
			    		<f:param name="runnableViewName" value="#{runnableView.viewName}"/>
					</h:commandLink>
					<h:outputText value="<br>&nbsp &nbsp #{runnableView.runnableProcessEntry.info.hint()}" styleClass="description" escape="false"/>
				</h:panelGroup>
			</h:column>
		</h:dataTable>
	    <f:verbatim>
			<SCRIPT>borderCreator.endDarkBorder()</SCRIPT>
		</f:verbatim>
 	</h:form>
 	
 	
</f:subview>