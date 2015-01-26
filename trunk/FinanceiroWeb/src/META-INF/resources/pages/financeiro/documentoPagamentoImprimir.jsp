<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>


<tiles:insert definition=".menuLayout">
	<tiles:put name="title" value="Impressão de documento de pagamento avulso"/>
	<tiles:put name="img" value="../financeiro/img/imprimirDocumento_b.png"/>
	<tiles:put name="body" value="/pages/financeiro/tiles/documentoPagamento/imprimir.jsp"/>
</tiles:insert>