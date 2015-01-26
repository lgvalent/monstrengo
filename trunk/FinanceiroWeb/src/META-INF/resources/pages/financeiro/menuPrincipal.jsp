<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<tiles:insert definition=".welcomeLayout">
	<tiles:put name="title" value="Menu Principal do Financeiro"/>
	<tiles:put name="body" value="/pages/financeiro/tiles/menuPrincipal.jsp"/>
</tiles:insert>