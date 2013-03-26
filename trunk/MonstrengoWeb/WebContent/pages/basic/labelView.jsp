<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>


<tiles:insert definition=".mainLayout">
	<tiles:put name="title" value="Controle de Etiquetas"/>
	<tiles:put name="img" value="../basic/img/label.png"/>
	<tiles:put name="sider" value="../basic/tiles/label/menu.jsp"/>
	<tiles:put name="body" value="../basic/tiles/label/view.jsp"/>
</tiles:insert>