<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

  <h:panelGroup rendered="#{queryBean.currentProcess.userReport.pageParam.itemsCount==0}">
	<h:outputLabel value="Nenhum registro selecionado."/>
	<h:commandButton id="refreshEmpty" value="Atualizar" action="#{queryBean.doPageRefresh}" onclick="this.value='Atualizando...'"/>
  </h:panelGroup>
<%--h:form--%>

  <h:panelGrid columns="2" rendered="#{queryBean.currentProcess.userReport.pageParam.itemsCount>0}">
	<h:panelGroup>
	  <h:outputText value="Visualizando registros de #{queryBean.currentProcess.userReport.pageParam.firstItemIndexPage+1} a #{queryBean.currentProcess.userReport.pageParam.lastItemIndexPage+1} (<b>#{queryBean.currentProcess.userReport.pageParam.itemsCount}</b> total, em #{queryBean.currentProcess.userReport.queryTime/1000} segundos)<br>" escape="false" styleClass="tableViewColumnEven"/>

		<%-- h:panelGrid columns="15"  columnClasses="tableViewColumnEven"--%>
		<h:panelGroup styleClass="noprint">
		  <h:commandLink action="#{queryBean.doGoFirstPage}" rendered="#{not queryBean.currentProcess.userReport.pageParam.first}">
			<h:graphicImage value="../../public/basic/img/arrow_first.png" style="border: none;"/>
		  </h:commandLink>
		  <h:graphicImage value="../../public/basic/img/arrow_first_d.png" rendered="#{queryBean.currentProcess.userReport.pageParam.first}"/>	

		  <h:commandLink action="#{queryBean.doGoPriorPage}" rendered="#{not queryBean.currentProcess.userReport.pageParam.first}">
			<h:graphicImage value="../../public/basic/img/arrow_prior.png" style="border: none;"/>
		  </h:commandLink>
		  <h:graphicImage value="../../public/basic/img/arrow_prior_d.png" rendered="#{queryBean.currentProcess.userReport.pageParam.first}"/>		

		  <h:outputLabel value="Página:" title="Permite ir para uma determinada página" accesskey="p"/>
		  <h:selectOneMenu id="setpage" value="#{queryBean.currentProcess.userReport.pageParam.page}" disabled="#{queryBean.currentProcess.userReport.pageParam.pageCount==1}" style="width:50px">
			<f:selectItems value="#{queryBean.currentProcess.userReport.pageParam.pageList}"/>
		  </h:selectOneMenu>
		  <h:message for="setpage" />
	
		  <h:commandLink action="#{queryBean.doGoNextPage}" rendered="#{not queryBean.currentProcess.userReport.pageParam.last}">
			<h:graphicImage value="../../public/basic/img/arrow_next.png" style="border: none;"/>
		  </h:commandLink>
		  <h:graphicImage value="../../public/basic/img/arrow_next_d.png" rendered="#{queryBean.currentProcess.userReport.pageParam.last}"/>	

		  <h:commandLink action="#{queryBean.doGoLastPage}" rendered="#{not queryBean.currentProcess.userReport.pageParam.last}">
			<h:graphicImage value="../../public/basic/img/arrow_last.png" style="border: none;"/>
		  </h:commandLink>
		  <h:graphicImage value="../../public/basic/img/arrow_last_d.png" rendered="#{queryBean.currentProcess.userReport.pageParam.last}"/>	

		  <h:outputLabel value="Tamanho da página:" title="Permite alterar o número de registros que são visualizados por página" accesskey="s"/>
		  <h:selectOneMenu id="setpagesize" value="#{queryBean.currentProcess.userReport.pageParam.pageSize}" style="width:50px">
			<f:selectItems value="#{queryBean.currentProcess.userReport.pageParam.pageSizesList}"/>
		  </h:selectOneMenu>
		  <h:message for="setpagesize" />

		  <h:commandButton id="refresh" value="Atualizar" action="#{queryBean.doPageRefresh}" onclick="this.value='Atualizando...'"/>
		</h:panelGroup>
	</h:panelGroup>
	
    <h:outputLink styleClass="noprint" value="javascript:popupPage('../basic/queryReport.jsp',800,600)">
		<h:graphicImage value="../../public/basic/img/printer.png" title="Exibe o relatório da pesquisa" style="border:0"/>
	</h:outputLink>
  </h:panelGrid>
<%--/h:form--%>
  