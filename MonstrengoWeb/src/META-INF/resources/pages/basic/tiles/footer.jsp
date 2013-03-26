<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<h:panelGrid styleClass="footer">
	<h:outputText value="#{footerBean.linha1}" escape="false"/>
	<h:outputText value="#{footerBean.linha2}" escape="false"/>
	<h:outputText value="#{footerBean.linha3}" escape="false"/>
	<h:outputText value="#{footerBean.linha4}" escape="false"/>	
	<h:outputText value="<font style='font-size:small'><b>OrionSoft</b></font>" escape="false"/>	
	<h:outputText value="<font style='font-size:small'><b>Acesse:</b></font><a href='http://orionsig.blogspot.com/' target='_blank'>orionsig.blogspot.com</a>" escape="false"/>	
</h:panelGrid>


