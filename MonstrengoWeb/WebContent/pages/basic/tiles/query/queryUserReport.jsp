<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

<h:outputLabel value="Grave e recupere relatórios personalizados: " style="color: #FF751A;"/>
<h:panelGrid columns="2">
  <h:panelGrid >
    <h:panelGrid>
      <h:panelGroup>
      	<h:outputLabel value="Meus relatórios:"/>
      	<h:commandLink action="#{queryBean.doRefreshListUserReport}">
	      	<h:outputText value="[atualizar]" title="Atualiza a lista de relatórios salvos"/>
      	</h:commandLink>
      </h:panelGroup>
      <h:selectOneMenu id="userReportId" value="#{queryBean.currentParams.userReportId}">
        <f:selectItems value="#{queryBean.listUserReport}"/>
      </h:selectOneMenu>
      <h:message for="userReportId"/>
      
      <h:panelGrid columns="4">
  	    <h:commandButton value="Carregar" action="#{queryBean.doRestoreUserReport}" onclick="this.value='Carregando...'" title="Carrega o relatório atualmente selecionado na lista"/>
	    <h:commandButton value="Excluir" onclick="javascript:return confirm('Confirma exclusão do relatório selecionado na lista?')" action="#{queryBean.doDeleteUserReport}" title="Exclui o relatório atualmente selecionado na lista"/>
	    <h:commandButton value="Limpar" action="#{queryBean.doClearUserReport}" onclick="this.value='Limpando...'" title="Limpa todos os atuais parâmetros da pesquisa"/>
	    <h:commandButton value="Salvar" onclick="javascript:return (popupPage('../basic/querySaveUserReport.jsp',600,300) || false)" title="Salva os atuais parâmetros de pesquisa para serem recuperados depois"/>
      </h:panelGrid>
    </h:panelGrid>
  </h:panelGrid>
  <h:panelGrid >
     <h:outputLabel value="Relatório ativo: #{queryBean.currentProcess.userReport.name}"/>
     <h:outputLabel value="Descrição: #{queryBean.currentProcess.userReport.description}"/>
  </h:panelGrid>
</h:panelGrid>
	