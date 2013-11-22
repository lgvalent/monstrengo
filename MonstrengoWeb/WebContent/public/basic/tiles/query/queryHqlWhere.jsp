<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="tiles" uri="http://jakarta.apache.org/struts/tags-tiles" %>

<h:panelGrid  columnClasses="tableViewColumnEven">
	<h:outputText value="Expressão avançada para filtragem: "/>
	<h:inputTextarea 
		   id="hqlWhere" 
		   value="#{queryBean.currentProcess.userReport.hqlWhereParam.hqlWhere}" 
		   title="Digite o código HQL para filtragem avançada. Utilize a palavra 'entity.' para referenciar a atual entidade da pesquisa" 
		   cols="100" 
		   rows="5" />
	<h:commandButton value="Aplicar" action="#{queryBean.doFilter}" onclick="this.value='Aplicando...'"/>
</h:panelGrid>
	