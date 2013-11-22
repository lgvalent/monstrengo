<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<f:subview id="userReport">
<h:outputLabel value="Carregue relatórios salvos através de filtros:" style="color: #0000FF;"/>

  <h:messages/>
  <h:form>
   <h:panelGrid columns="2">
     <h:outputLabel value="Por usuário:"/>
     <h:selectOneMenu id="userId" value="#{listUserReportBean.userId}" onchange="this.form.submit()">
       <f:selectItems value="#{listUserReportBean.userList}"/>
     </h:selectOneMenu>

   	 <h:outputLabel value="Por entidade:"/>
     <h:selectOneMenu id="entityId" value="#{listUserReportBean.entityId}" onchange="this.form.submit()">
       <f:selectItems value="#{listUserReportBean.entityList}"/>
     </h:selectOneMenu>
   </h:panelGrid>
	  <f:verbatim >	
		<br>
	  </f:verbatim >	

    <h:message for="userReportId"/>
	
	<h:commandButton styleClass="noprint" value="Recarregar" action="#{listUserReportBean.doReload}" title="Atualiza a lista atual" />
  </h:form>
  <h:form target="_blank">
	<h:dataTable value="#{listUserReportBean.userReports.list}"
				 width="100%"
	             var="item"
	             headerClass="tableListHeader"
	             styleClass="tableList"
	             rowClasses="tableListRowEven,tableListRowOdd"
	             columnClasses="tableListColumn"
			     style="widht:100%">
		<h:column>
			<f:facet name="header">
            	<h:outputText value="Nome do relatório"/>
            </f:facet>
			<h:outputLink styleClass="noprint" value="javascript:linkUpdate('#{item.info.type.name}', '#{item.id}')" rendered="#{menuBean.crudMap[item.info.type.simpleName].canUpdate}">
				<h:graphicImage value="../../public/basic/img/update.png" title="Altera os dados do registro" style="border:0"/>
			</h:outputLink>
			<h:graphicImage styleClass="noprint" value="../../public/basic/img/update_d.png" title="Você não possui direitos para alterar os dados do registro" style="border:0" rendered="#{!menuBean.crudMap[item.info.type.simpleName].canUpdate}"/>
			<h:outputLink styleClass="noprint" value="javascript:linkDelete('#{item.info.type.name}', '#{item.id}')" rendered="#{menuBean.crudMap[item.info.type.simpleName].canDelete}">
				<h:graphicImage value="../../public/basic/img/delete.png" title="Apaga o registro" style="border:0"/>
			</h:outputLink>
			<h:graphicImage styleClass="noprint" value="../../public/basic/img/delete_d.png" title="Você não possui direitos para apagar o registro" style="border:0" rendered="#{!menuBean.crudMap[item.info.type.simpleName].canDelete}"/>
	
			<h:commandLink action="#{queryBean.actionRestoreUserReport}">
				<h:outputText value="#{item}"title="#{item.propertiesMap.description.value.asString}"/>
				<f:param name="entityType" value="#{item.propertiesMap.applicationEntity.value.asEntity.propertiesMap.className.value.asString}" />
				<f:param name="userReportId" value="#{item.id}" />
		 	</h:commandLink>
		</h:column>
		<h:column>
			<f:facet name="header">
            	<h:outputText value="Operador"/>
            </f:facet>
            
        	<h:panelGroup rendered="#{(item.propertiesMap.applicationUser.value.id==listUserReportBean.userSessionBean.userSession.user.id)||(item.propertiesMap.applicationUser.value.valueNull)}">
	        	<h:selectOneMenu styleClass="noprint" id="userId" value="#{item.propertiesMap.applicationUser.value.id}" >
		    		<f:selectItems value="#{listUserReportBean.userTransferList}"/>
        		</h:selectOneMenu>
        		<h:commandButton  styleClass="noprint" value="Transferir" action="#{listUserReportBean.doSave}" title="Define o novo operador dono do relatório" />
            </h:panelGroup>
            
			<h:outputText  styleClass="noprint" value="#{item.propertiesMap.applicationUser.value.asEntity}" rendered="#{item.propertiesMap.applicationUser.value.id!=listUserReportBean.userSessionBean.userSession.user.id}"/>
			<h:outputText  styleClass="onlyprint" value="#{item.propertiesMap.applicationUser.value.asEntity}"/>
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
