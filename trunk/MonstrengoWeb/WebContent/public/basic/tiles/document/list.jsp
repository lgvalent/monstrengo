<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<f:subview id="userReport">

<f:verbatim >	
	<br>
</f:verbatim >	

<h:outputLabel value="Carregue modelos de documentos salvos:" style="color: #0000FF;"/>

  <h:messages/>
  <h:form>
<%--<h:outputText styleClass="title" value="Criar um novo modelo de documento:" escape="false" />
h:commandLink action="#{documentEntityBean.actionCreate}" rendered="#{menuBean.crudMap.ModelDocumentEntity.canCreate}">
	<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
</h:commandLink>
<h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.ModelDocumentEntity.canCreate}" /--%>

   <h:panelGrid columns="2">
     <h:outputLabel value="Por usuário:"/>
     <h:selectOneMenu id="userId" value="#{listDocumentBean.userId}" onchange="this.form.submit()">
       <f:selectItems value="#{listDocumentBean.userList}"/>
     </h:selectOneMenu>

   	 <h:outputLabel value="Por entidade:"/>
     <h:selectOneMenu id="entityId" value="#{listDocumentBean.entityId}" onchange="this.form.submit()">
       <f:selectItems value="#{listDocumentBean.entityList}"/>
     </h:selectOneMenu>
   </h:panelGrid>
	  <f:verbatim >	
		<br>
	  </f:verbatim >	

    <h:message for="userReportId"/>
</h:form>	
<h:panelGrid columns="2">
  <h:form>
	<h:commandButton value="Recarregar" action="#{listDocumentBean.doReload}" title="Atualiza a lista atual" />
  </h:form>
  <h:form target="_blank">
	<h:commandButton value="Criar" action="#{documentEntityBean.actionCreate}" title="Cria um novo modelo de documento" />
  </h:form>	
</h:panelGrid>

  <h:form >
	<h:dataTable value="#{listDocumentBean.documents.list}"
				 width="100%"
	             var="item"
	             headerClass="tableListHeader"
	             styleClass="tableList"
	             rowClasses="tableListRowEven,tableListRowOdd"
	             columnClasses="tableListColumn"
			     style="widht:100%">
		<h:column>
			<f:facet name="header">
            	<h:outputText value="Nome do modelo de documento"/>
            </f:facet>
				<h:commandLink action="#{documentEntityBean.actionPreview}" rendered="#{menuBean.crudMap[item.info.type.simpleName].canRetrieve}">
					<h:graphicImage value="../../public/basic/img/preview.png" style="border:0" width="16" height="16"title="Previsualiza o documento"/>
			    	<f:param name="documentId"   value="#{item.id}"/>
				</h:commandLink>

				<h:commandLink action="#{documentEntityBean.actionUpdate}" rendered="#{menuBean.crudMap[item.info.type.simpleName].canUpdate}">
					<h:graphicImage value="../../public/basic/img/update.png" title="Altera os dados do registro" style="border:0"/>
			    	<f:param name="documentId"   value="#{item.id}"/>
				</h:commandLink>
				<h:graphicImage value="../../public/basic/img/update_d.png" title="Você não possui direitos para alterar os dados do registro" style="border:0" rendered="#{!menuBean.crudMap[item.info.type.simpleName].canUpdate}"/>

				<%--h:outputLink value="javascript:linkUpdate('#{item.info.type.name}', '#{item.id}')" rendered="#{menuBean.crudMap[item.info.type.simpleName].canUpdate}">
					<h:graphicImage value="../../public/basic/img/update.png" title="Altera os dados do registro" style="border:0"/>
				</h:outputLink>
				<h:graphicImage value="../../public/basic/img/update_d.png" title="Você não possui direitos para alterar os dados do registro" style="border:0" rendered="#{!menuBean.crudMap[item.info.type.simpleName].canUpdate}"/--%>

				<h:outputLink value="javascript:linkDelete('#{item.info.type.name}', '#{item.id}')" rendered="#{menuBean.crudMap[item.info.type.simpleName].canDelete}">
					<h:graphicImage value="../../public/basic/img/delete.png" title="Apaga o registro" style="border:0"/>
				</h:outputLink>
				<h:graphicImage value="../../public/basic/img/delete_d.png" title="Você não possui direitos para apagar o registro" style="border:0" rendered="#{!menuBean.crudMap[item.info.type.simpleName].canDelete}"/>
	
				<h:commandLink action="#{documentEntityBean.actionCompile}" rendered="#{item.propertiesMap.applicationEntity.value.valueNull and menuBean.processMap.CompileDocumentProcess}" >
					<h:graphicImage value="../../public/basic/img/run_task.png" style="border:0" width="16" height="16"title="Gera o documento que n�o precisa de entidade"/>
					<h:outputText value="#{item}"title="#{item.propertiesMap.description.value.asString}"/>
			    	<f:param name="documentId" value="#{item.id}"/>
				</h:commandLink>

				<h:outputText value="#{item}<br>" style="font-weight:bold;" rendered="#{!(item.propertiesMap.applicationEntity.value.valueNull and menuBean.processMap.CompileDocumentProcess)}" escape="false"/>
				<h:outputText style="margin-left:1.5cm; font-style:italic;" value="#{item.propertiesMap.description.value.asString}" rendered="#{!(item.propertiesMap.applicationEntity.value.valueNull and menuBean.processMap.CompileDocumentProcess)}"/>
		</h:column>
		<h:column>
			<f:facet name="header">
            	<h:outputText value="Operador"/>
            </f:facet>
            
        	<h:panelGroup rendered="#{(item.propertiesMap.applicationUser.value.id==listDocumentBean.userSessionBean.userSession.user.id)||(item.propertiesMap.applicationUser.value.valueNull)}">
	        	<h:selectOneMenu id="userId" value="#{item.propertiesMap.applicationUser.value.id}" >
		    		<f:selectItems value="#{listDocumentBean.userTransferList}"/>
        		</h:selectOneMenu>
        		<h:commandButton value="Transferir" action="#{listDocumentBean.doSave}" title="Define o novo operador dono do modelo de documento" />
            </h:panelGroup>
            
			<h:outputText value="#{item.propertiesMap.applicationUser.value.asEntity}" rendered="#{item.propertiesMap.applicationUser.value.id!=listDocumentBean.userSessionBean.userSession.user.id}"/>
		</h:column>
		<h:column>
			<f:facet name="header">
            	<h:outputText value="Data de criação"/>
            </f:facet>
			<h:outputText value="#{item.propertiesMap.date.value.asString}"/>
		</h:column>
	</h:dataTable>
   </h:form>

</f:subview>
