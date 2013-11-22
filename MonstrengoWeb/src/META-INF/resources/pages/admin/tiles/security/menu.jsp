<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<h:form id="formA"> 

  <h:panelGrid >
	 <h:outputLink value="../../pages/admin/securityCreateSecurityStructure.jsp" rendered="#{menuBean.processMap.CreateSecurityStructureProcess}">
   		<h:graphicImage value="../../public/basic/img/run_task.png" style="border:0"/>
     	<h:outputText  value="Criar ou alterar a estrutura de segurança de um operador ou grupo"/>
  	 </h:outputLink>

	 <h:outputLink value="../../pages/admin/securityChangePassword.jsp" rendered="#{menuBean.processMap.ChangePasswordProcess}">
   		<h:graphicImage value="../../public/basic/img/run_task.png" style="border:0"/>
     	<h:outputText  value="Alterar a senha do operador atualmente autenticado no sistema"/>
  	 </h:outputLink>

	 <h:outputLink value="../../pages/admin/securityOverwritePassword.jsp" rendered="#{menuBean.processMap.OverwritePasswordProcess}">
   		<h:graphicImage value="../../public/basic/img/run_task.png" style="border:0"/>
     	<h:outputText  value="Alterar a senha de um operador sem saber a senha atual do mesmo"/>
  	 </h:outputLink>
  </h:panelGrid >
    <f:verbatim>
		<hr>
	</f:verbatim>
	
	<h:panelGrid >
	      
	      <h:panelGrid columns="7" >
			 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.ApplicationUser.name}')" rendered="#{menuBean.crudMap.ApplicationUser.canCreate}">
				<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
			 </h:outputLink>
			 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.ApplicationUser.canCreate}" />

			 <h:outputText value=" " />
			 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.ApplicationUser.name}', '')" rendered="#{menuBean.crudMap.ApplicationUser.canRetrieve}">
			 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
			 </h:outputLink>
 			 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.ApplicationUser.canRetrieve}" />
			 <h:outputText value=" " />

	         <h:outputText value="#{menuBean.infoMap.ApplicationUser.label}" />
 	      </h:panelGrid>
			 
	      <h:panelGrid columns="7" >
			 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.SecurityGroup.name}')" rendered="#{menuBean.crudMap.SecurityGroup.canCreate}">
				<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
			 </h:outputLink>
			 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.SecurityGroup.canCreate}" />

			 <h:outputText value=" " />
			 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.SecurityGroup.name}','')" rendered="#{menuBean.crudMap.SecurityGroup.canRetrieve}">
			 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
			 </h:outputLink>
 			 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.SecurityGroup.canRetrieve}" />
			 <h:outputText value=" " />

	         <h:outputText value="#{menuBean.infoMap.SecurityGroup.label}" />
 	      </h:panelGrid>

	      <h:panelGrid columns="7" >
			 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.RightCrud.name}')" rendered="#{menuBean.crudMap.RightCrud.canCreate}">
				<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
			 </h:outputLink>
			 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.RightCrud.canCreate}" />

			 <h:outputText value=" " />
			 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.RightCrud.name}','')" rendered="#{menuBean.crudMap.RightCrud.canRetrieve}">
			 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
			 </h:outputLink>
 			 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.RightCrud.canRetrieve}" />
			 <h:outputText value=" " />

	         <h:outputText value="#{menuBean.infoMap.RightCrud.label}" />
 	      </h:panelGrid>
			 
	      <h:panelGrid columns="7" >
			 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.ApplicationEntity.name}')" rendered="#{menuBean.crudMap.ApplicationEntity.canCreate}">
				<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
			 </h:outputLink>
			 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.ApplicationEntity.canCreate}" />

			 <h:outputText value=" " />
			 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.ApplicationEntity.name}','')" rendered="#{menuBean.crudMap.ApplicationEntity.canRetrieve}">
			 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
			 </h:outputLink>
 			 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.ApplicationEntity.canRetrieve}" />
			 <h:outputText value=" " />

	         <h:outputText value="#{menuBean.infoMap.ApplicationEntity.label}" />
 	      </h:panelGrid>

	      <h:panelGrid columns="7" >
			 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.RightProcess.name}')" rendered="#{menuBean.crudMap.RightProcess.canCreate}">
				<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
			 </h:outputLink>
			 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.RightProcess.canCreate}" />

			 <h:outputText value=" " />
			 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.RightProcess.name}','')" rendered="#{menuBean.crudMap.RightProcess.canRetrieve}">
			 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
			 </h:outputLink>
 			 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.RightProcess.canRetrieve}" />
			 <h:outputText value=" " />

	         <h:outputText value="#{menuBean.infoMap.RightProcess.label}" />
 	      </h:panelGrid>

	      <h:panelGrid columns="7" >
			 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.ApplicationProcess.name}')" rendered="#{menuBean.crudMap.ApplicationProcess.canCreate}">
				<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
			 </h:outputLink>
			 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.ApplicationProcess.canCreate}" />

			 <h:outputText value=" " />
			 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.ApplicationProcess.name}','')" rendered="#{menuBean.crudMap.ApplicationProcess.canRetrieve}">
			 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
			 </h:outputLink>
 			 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.ApplicationProcess.canRetrieve}" />
			 <h:outputText value=" " />

	         <h:outputText value="#{menuBean.infoMap.ApplicationProcess.label}" />
 	      </h:panelGrid>

	      <h:panelGrid columns="7" >
			 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.ApplicationModule.name}')" rendered="#{menuBean.crudMap.ApplicationModule.canCeate}">
				<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
			 </h:outputLink>
			 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.ApplicationModule.canCreate}" />

			 <h:outputText value=" " />
			 <h:outputLink value="javascript:linkQueryFilter('#{menuBean.infoMap.ApplicationModule.name}','')" rendered="#{menuBean.crudMap.ApplicationModule.canRetrieve}">
			 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
			 </h:outputLink>
 			 <h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.ApplicationModule.canRetrieve}" />
			 <h:outputText value=" " />

	         <h:outputText value="#{menuBean.infoMap.ApplicationModule.label}" />
 	      </h:panelGrid>

   	</h:panelGrid>
	
</h:form>	
