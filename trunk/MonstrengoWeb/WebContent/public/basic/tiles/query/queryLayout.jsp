<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<f:subview id="Query">
  <h:form id="formQuery">
	<%-- Sincroniza a atual entidade manipulada pela view --%>
	<h:inputHidden immediate="true" id="currentEntityKey" value="#{queryBean.currentEntityKey}" validator="#{queryBean.validateCurrentEntityKey}"/>
	<%-- Fornece um acesso para que o BEAN invalide um valor da view JSF e forçe a reconstrução quando necessário --%>
	<h:inputHidden immediate="true" binding="#{queryBean.inputCurrentEntityKey}"/> 
	<%--/h:form--%>
    <%-- Lucio 20090819: Retirado até achar uma solução de personalização por usuário para que ele escolha usar ou não os popups
		f:verbatim>
   		<div class="floatMenu noprint">
			ir para 
            <img width="16" height="16" src="../../public/basic/img/close.png" style="border: 0pt none ;" title="Fechar" onmousedown="this.parentNode.style.display='none'"> 
   			<a href="#" >topo</a>
   			<a href="#hqlWhere" >expressão</a>
   			<a href="#condiction" >filtro</a>
   			<a href="#order" >ordenação</a>
   			<a href="#body" >resultado</a>
   			<a href="#labels" >etiquetas</a>
   		</div>
	</f:verbatim--%>
				
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr valign="top">
			<td>
			  <h:panelGroup styleClass="parentGroupHeader" rendered="#{queryBean.currentProcess.userReport.parentParam.hasParent}">
			    <f:verbatim>
					<script>borderCreator.initDarkBorder('100%', '', 'center', 'top')</script>
				</f:verbatim>
				<tiles:insert attribute="parent" flush="false"/>
			    <f:verbatim>
					<script>borderCreator.endDarkBorder()</script>
				</f:verbatim>
			  </h:panelGroup>
			</td>
		</tr>
		<tr>
			<td>
	    	    <a name="filter"/>
				<script>borderCreator.initDarkBorder('100%', '', 'left', 'top')</script>
				<tiles:insert attribute="filter" ignore="false" flush="false"/>
				<script>borderCreator.endDarkBorder()</script>
			</td>
		</tr>
		<tr>
			<td>
			  <h:panelGroup rendered="#{queryBean.currentParams.advancedUserReport == true}">
			    <f:verbatim>
					<script>borderCreator.initDarkBorder('100%', '', 'left', 'top')</script>
			    </f:verbatim>
			    <tiles:insert attribute="userReport" ignore="false" flush="false"/>
			    <f:verbatim>				
				<script>borderCreator.endDarkBorder()</script>
			    </f:verbatim>
			  </h:panelGroup>			    				
			</td>
		</tr>
		<tr>
			<td>
	    	  <a name="hqlWhere"/>
			  <h:panelGroup rendered="#{queryBean.currentParams.advancedHqlWhere == true}">
			    <f:verbatim>
					<script>borderCreator.initDarkBorder('100%', '', 'left', 'top')</script>
			    </f:verbatim>
			    <tiles:insert attribute="hqlWhere" ignore="false" flush="false"/>
			    <f:verbatim>				
				<script>borderCreator.endDarkBorder()</script>
			    </f:verbatim>
			  </h:panelGroup>			    				
			</td>
		</tr>
		<tr>
			<td>
			  <h:panelGroup rendered="#{queryBean.currentParams.advancedQuery == true}">
			    <f:verbatim>
					<script>borderCreator.initDarkBorder('100%', '', 'left', 'top')</script>
			    </f:verbatim>
			    <tiles:insert attribute="condiction" ignore="false" flush="false"/>
			    <f:verbatim>				
				<script>borderCreator.endDarkBorder()</script>
			    </f:verbatim>
			  </h:panelGroup>			    				
			</td>
		</tr>
		<tr>
			<td>
	    	  <a name="order"/>
			  <h:panelGroup rendered="#{queryBean.currentParams.advancedOrder == true}">
			    <f:verbatim>
					<script>borderCreator.initDarkBorder('100%', '', 'left', 'top')</script>
			    </f:verbatim>
			    <tiles:insert attribute="order" ignore="false" flush="false"/>
			    <f:verbatim>				
				<script>borderCreator.endDarkBorder()</script>
			    </f:verbatim>
			  </h:panelGroup>			    				
			</td>
		</tr>
		<tr>
			<td>
			  <h:panelGroup rendered="#{queryBean.currentParams.advancedResult == true}">
			    <f:verbatim>
					<script>borderCreator.initDarkBorder('100%', '', 'left', 'top')</script>
			    </f:verbatim>
			    <tiles:insert attribute="result" ignore="false" flush="false"/>
			    <f:verbatim>				
				<script>borderCreator.endDarkBorder()</script>
			    </f:verbatim>
			  </h:panelGroup>			    				
			</td>
		</tr>
		<tr>
			<td>
				<f:subview id="UpperPagination">
					<script>borderCreator.initDarkBorder('100%', '', 'left', 'top')</script>
					<tiles:insert attribute="pagination" ignore="false" flush="false"/>
					<script>borderCreator.endDarkBorder()</script>
				</f:subview>
			</td>
		</tr>
		<tr>
    	    <a name="body"/>
			<td align="center">
				<br>
				<tiles:insert attribute="body" flush="false"/>
				<br>
			</td>
		</tr>
		<%-- tr>
			<td>
				<script>borderCreator.initDarkBorder('100%', '', 'center', 'top')</script>
				<tiles:insert attribute="command" ignore="false"/>
				<script>borderCreator.endDarkBorder()</script>
			</td>
		</tr --%>
		<tr>
			<td>
	    	  <a name="labels"/>
			  <h:panelGroup rendered="#{menuBean.crudMap.AddressLabel.canCreate && queryBean.hasModelsLabelEntity}">
			    <f:verbatim>
					<script>borderCreator.initDarkBorder('100%', '', 'left', 'top')</script>
			    </f:verbatim>
			    <tiles:insert attribute="label" ignore="false" flush="false"/>
			    <f:verbatim>				
				<script>borderCreator.endDarkBorder()</script>
			    </f:verbatim>
			  </h:panelGroup>			    				
			</td>
		</tr>
	</table>
  <%--/h:form--%>
  </h:form>  
</f:subview>
