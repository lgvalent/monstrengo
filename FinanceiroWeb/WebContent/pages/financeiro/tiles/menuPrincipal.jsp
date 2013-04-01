<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<f:subview id="menu_principal">
  <h:form>
    <h:messages/>
	<h:panelGrid columns="5" cellpadding="10" width="100%" style="text-align:center">
	
	<h:outputLink value="javascript:popupPageName('../financeiro/menu.jsp',0,0, '')">
    	<h:graphicImage value="../financeiro/img/menu_financeiro.png" title="Financeiro" style="border:0"/>
		<f:verbatim><br></f:verbatim>
		<h:outputText value="Financeiro" style="color:#000099" escape="false"/>
	</h:outputLink>

	<h:outputLink value="javascript:popupPageName('../academico/menu.jsp',800,600, 'ACADEMICO')">	
		<h:graphicImage value="../academico/img/menu.png" title="Cobrança" style="border:0"/>
		<f:verbatim><br></f:verbatim>
		<h:outputText value="Acadêmico" style="color:#000099" escape="false"/>
	</h:outputLink>	
	
	<h:outputLink value="javascript:popupPageName('../vestibular_basico/menu.jsp',800,600, 'PROCESSO_SELETIVO')">	
		<h:graphicImage value="../vestibular_basico/img/menu.png" title="Processo Seletivo" style="border:0"/>
		<f:verbatim><br></f:verbatim>
		<h:outputText value="Processo<br>Seletivo" style="color:#000099" escape="false"/>
	</h:outputLink>	

	<h:outputLink value="javascript:popupPageName('../materiais/menu.jsp',450,600,'MATERIAIS')">	
    	<h:graphicImage value="../materiais/img/menu.png" title="Cadastro" style="border:0"/>
		<f:verbatim><br></f:verbatim>
		<h:outputText value="Processamento<br>de Materiais" style="color:#000099" escape="false"/>
	</h:outputLink>	

	<h:outputLink value="javascript:popupPageName('../clinico/menu.jsp',450,600,'CLINICO')">	
    	<h:graphicImage value="../clinico/img/menu.png" title="Cadastro" style="border:0"/>
		<f:verbatim><br></f:verbatim>
		<h:outputText value="Centros<br>Clínicos" style="color:#000099" escape="false"/>
	</h:outputLink>	

	<h:outputLink value="javascript:popupPageName('../agenda/menu.jsp',450,600,'CLINICO')">	
    	<h:graphicImage value="../agenda/img/menu.png" title="Cadastro" style="border:0"/>
		<f:verbatim><br></f:verbatim>
		<h:outputText value="Agendas<br>e Reservas" style="color:#000099" escape="false"/>
	</h:outputLink>	

	<h:outputLink value="javascript:popupPageName('../basic/menu.jsp',450,600,'CADASTRO')">	
    	<h:graphicImage value="../basic/img/menu_cadastro.png" title="Cadastro" style="border:0"/>
		<f:verbatim><br></f:verbatim>
		<h:outputText value="Cadastro Geral" style="color:#000099" escape="false"/>
	</h:outputLink>	

	<h:outputLink value="javascript:popupPageName('../biblioteca/menu.jsp',600,500,'BIBLIOTECA')">	
    	<h:graphicImage value="../biblioteca/img/menu.png" title="Biblioteca" style="border:0"/>
		<f:verbatim><br></f:verbatim>
		<h:outputText value="Bibliotecas" style="color:#000099" escape="false"/>
	</h:outputLink>	

	<h:outputLink value="javascript:popupPageName('../administracao/menu.jsp',600,500, 'ADMINISTRACAO')">	
    	<h:graphicImage value="../administracao/img/menu_administracao.png" title="Administração" style="border:0"/>
		<f:verbatim><br></f:verbatim>
		<h:outputText value="Administração" style="color:#000099" escape="false"/>
	</h:outputLink>
	
	<h:panelGrid>
		<h:panelGrid width="100%" style="text-align:center;">
   			<h:graphicImage value="../estatistica/img/menu_estatistica.png" title="Relatórios" style="border:0;" />
   		</h:panelGrid>
		<h:outputLink value="javascript:popupPage('../basic/listUserReport.jsp',0,0)">
			<h:graphicImage value="../basic/img/run_task.png" style="border:0" width="16" height="16"/>	
			<h:outputText value="Relatórios personalizados" style="color:#000099"/>
		</h:outputLink>
		<h:outputLink value="javascript:popupPageName('../basic/labelView.jsp',0,0, VIEW_LABEL_LIST)" rendered="#{menuBean.crudMap.AddressLabel.canRetrieve}">
			<h:graphicImage value="../basic/img/run_task.png" style="border:0" width="16" height="16"/>	
			<h:outputText value="Etiquetas" style="color:#000099"/>
		</h:outputLink>	
		<h:outputLink value="javascript:popupPage('../basic/documentList.jsp',0,0)">	
			<h:graphicImage value="../basic/img/run_task.png" style="border:0" width="16" height="16"/>	
			<h:outputText value="Documentos" style="color:#000099"/>
		</h:outputLink>	
	</h:panelGrid>	
	</h:panelGrid>
  </h:form>	   	    	    	
  <h:inputHidden value="Força a crição das estruturas de segurança para agilizar o acesso ao sistema e demorar somente na abertura da tela" rendered="#{menuBean.infoMap!=null && menuBean.crudMap!=null && menuBean.processMap!=null}"/>
</f:subview> 
