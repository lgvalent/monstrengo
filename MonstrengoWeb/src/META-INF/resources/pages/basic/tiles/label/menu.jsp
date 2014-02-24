<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<h:form id="formMensalidade" style="text-align:left;">
	<h:panelGrid columns="7" >
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.AddressLabel.name}')" rendered="#{menuBean.crudMap.AddressLabel.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.AddressLabel.canCreate}" />

		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQuery('#{menuBean.infoMap.AddressLabel.name}')" rendered="#{menuBean.crudMap.AddressLabel.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		</h:outputLink>
		<h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.AddressLabel.canRetrieve}" />
		<h:outputText value="#{menuBean.infoMap.AddressLabel.label}" />
 	</h:panelGrid>

	<h:panelGrid columns="7" >
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.AddressLabelGroup.name}')" rendered="#{menuBean.crudMap.AddressLabelGroup.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.AddressLabelGroup.canCreate}" />

		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQuery('#{menuBean.infoMap.AddressLabelGroup.name}')" rendered="#{menuBean.crudMap.AddressLabelGroup.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		</h:outputLink>
		<h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.AddressLabelGroup.canRetrieve}" />
		<h:outputText value="#{menuBean.infoMap.AddressLabelGroup.label}" />
 	</h:panelGrid>

	<h:panelGrid columns="7" >
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.ModelLabel.name}')" rendered="#{menuBean.crudMap.ModelLabel.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.ModelLabel.canCreate}" />

		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQuery('#{menuBean.infoMap.ModelLabel.name}')" rendered="#{menuBean.crudMap.ModelLabel.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		</h:outputLink>
		<h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.ModelLabel.canRetrieve}" />
		<h:outputText value="#{menuBean.infoMap.ModelLabel.label}" />
 	</h:panelGrid>

	<h:panelGrid columns="7" >
		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkCreatePopup('#{menuBean.infoMap.ModelLabelEntity.name}')" rendered="#{menuBean.crudMap.ModelLabelEntity.canCreate}">
			<h:graphicImage value="../../public/basic/img/create.png" title="Criar novo registro" style="border:0" />
		 </h:outputLink>
		 <h:graphicImage value="../../public/basic/img/create_d.png" title="Você não possui direitos para criar um novo registro" style="border:0" rendered="#{!menuBean.crudMap.ModelLabelEntity.canCreate}" />

		 <h:outputText value=" " />
		 <h:outputLink value="javascript:linkQuery('#{menuBean.infoMap.ModelLabelEntity.name}')" rendered="#{menuBean.crudMap.ModelLabelEntity.canRetrieve}">
		 	<h:graphicImage value="../../public/basic/img/query.png" title="Pesquisar" style="border:0" />
		</h:outputLink>
		<h:graphicImage value="../../public/basic/img/query_d.png" title="Você não possui direitos para visualizar esta entidade" style="border:0" rendered="#{!menuBean.crudMap.ModelLabelEntity.canRetrieve}" />
		<h:outputText value="#{menuBean.infoMap.ModelLabelEntity.label}" />
 	</h:panelGrid>
</h:form>
