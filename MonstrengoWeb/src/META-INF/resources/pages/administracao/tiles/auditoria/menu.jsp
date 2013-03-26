<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<h:form id="formA"> 

	<h:panelGrid >
	      
	      <h:panelGrid columns="3" >
			 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.AuditCrudRegister.name}', '')" rendered="#{menuBean.crudMap.AuditCrudRegister.canRetrieve}">
			 	<h:graphicImage value="../basic/img/query.png" title="Pesquisar" style="border:0" />
			 </h:outputLink>
 			 <h:graphicImage value="../basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.AuditCrudRegister.canRetrieve}" />
	         <h:outputText value="#{menuBean.infoMap.AuditCrudRegister.label}" />
 	      </h:panelGrid>
			 
	      <h:panelGrid columns="3" >
			 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.AuditProcessRegister.name}','')" rendered="#{menuBean.crudMap.AuditProcessRegister.canRetrieve}">
			 	<h:graphicImage value="../basic/img/query.png" title="Pesquisar" style="border:0" />
			 </h:outputLink>
 			 <h:graphicImage value="../basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.AuditProcessRegister.canRetrieve}" />
	         <h:outputText value="#{menuBean.infoMap.AuditProcessRegister.label}" />
 	      </h:panelGrid>
			 
	      <h:panelGrid columns="3" >
			 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.AuditServiceRegister.name}','')" rendered="#{menuBean.crudMap.AuditServiceRegister.canRetrieve}">
			 	<h:graphicImage value="../basic/img/query.png" title="Pesquisar" style="border:0" />
			 </h:outputLink>
 			 <h:graphicImage value="../basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.AuditServiceRegister.canRetrieve}" />
	         <h:outputText value="#{menuBean.infoMap.AuditServiceRegister.label}" />
 	      </h:panelGrid>
			 
   	</h:panelGrid>
	
</h:form>	
