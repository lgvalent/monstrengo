<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".menuLayout">
	<tiles:put name="title" value="Lista de Relatórios"/>
	<tiles:put name="img" value="../estatistica/img/menu_estatistica.png"/>
	<tiles:put name="body" value="../../public/basic/tiles/userReport/userReportBody.jsp"/>
</tiles:insert>