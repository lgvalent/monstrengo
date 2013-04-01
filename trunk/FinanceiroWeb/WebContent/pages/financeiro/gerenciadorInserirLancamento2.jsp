<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>


<tiles:insert definition=".mainLayout">
	<tiles:put name="title" value="Inserir lancamento"/>
	<tiles:put name="img" value="../financeiro/img/menu_financeiro.png"/>
	<tiles:put name="sider" value="../financeiro/tiles/gerenciador/menu.jsp"/>
	<tiles:put name="body" value="../financeiro/tiles/gerenciador/inserirLancamento2.jsp"/>
</tiles:insert>