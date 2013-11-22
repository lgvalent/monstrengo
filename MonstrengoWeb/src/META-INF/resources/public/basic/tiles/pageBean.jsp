<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>


<h:form>
	<h:outputLink value="list.jsp?entity=#{entityBean.entity.className}&parent=#{parentBean.entity.className}&parentId=#{parentBean.entity.id}&property=#{parentBean.propertyName}&page=1&pageSize=#{pageBean.size}" rendered="#{not pageBean.first}">
		<h:outputText value="|< " />
		<%--h:graphicImage styleClass="tableListHeaderImageBorder" value="../images/ASC.gif" / --%>
	</h:outputLink>

	<h:outputLink value="list.jsp?entity=#{entityBean.entity.className}&parent=#{parentBean.entity.className}&parentId=#{parentBean.entity.id}&property=#{parentBean.propertyName}&page=#{pageBean.index - 1}&pageSize=#{pageBean.size}" rendered="#{not pageBean.first}">
		<h:outputText value=" << " />
	</h:outputLink>
	
	<h:outputLink value="list.jsp?entity=#{entityBean.entity.className}&parent=#{parentBean.entity.className}&parentId=#{parentBean.entity.id}&property=#{parentBean.propertyName}&page=#{pageBean.index + 1}&pageSize=#{pageBean.size}" rendered="#{not pageBean.last}">
		<h:outputText value=" >> " />
	</h:outputLink>
	<h:outputLink value="list.jsp?entity=#{entityBean.entity.className}&parent=#{parentBean.entity.className}&parentId=#{parentBean.entity.id}&property=#{parentBean.propertyName}&page=#{pageBean.count}&pageSize=#{pageBean.size}" rendered="#{not pageBean.last}">
		<h:outputText value=" >|" />
	</h:outputLink>
	
</h:form>
