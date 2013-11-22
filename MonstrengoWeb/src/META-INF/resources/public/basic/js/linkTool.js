/**
 * Estas variaveis controlam um vetos com os ponteiros
 * para as janelas que foram criadas pelo popup para que
 * ao sair do sistema, um funcao closeAllWindows() seja executada
 * e todas as janelas sejam fechadas
 */
var windowArray = new Array();
var windowArraySize = 0;
// Adiciona uma nova janela na lista de janelas
function addWindow(window_){
	windowArray[windowArraySize] = window_;
	windowArraySize++;
}
// Fecha todas as janelas
function closeAllWindows() {
   for(var i = 0; i< windowArraySize ;i++)
       if(windowArray[i])
          windowArray[i].close();
}

function popupPageReturn_(page, x, y, popupName)
			{
    /* Calcula as coordenadas de posicionamento da janela */
	var top = 1;
	var left = 1;			
	if(x+y==0){
      x = getFrameWidth() -30 
      y = getFrameHeight() -30;
    }else{
	  left = (getFrameWidth() - x)/2;
	  top = (getFrameHeight() - y)/2;
    
    }
	
				var win = new Window(
										{
											className: "alphacube", 
											title: popupName, 
											width:x, 
											height:y,
											zIndex:150,opacity:1, 
											url: page,
											top: top, 
											left: left, 
											wiredDrag: true,
											showEffectOptions: {duration:0},
											showEffect:Element.show,
											recenterAuto: true/*,
											hideEffect: Effect.SwitchOff,
											parent: $('container')*/ 
										}
									); 
				//win.getContent().innerHTML = ""; 

				win.setDestroyOnClose(); 
				win.show();
				/*win.setConstraint(true, {left:10, right:10, top:10, bottom:10})
				win.toFront();*/
				if(x+y==0)
				  win.maximize();
			}

/**
 * Esta fun??o abre uma janela sem barras adicionais e com o tamanho especificado.
 * A nova janela aberta ? retornada. 
 * Links que usam as fun??es de abrir janela n?o necessitam de retorno, pois se 
 * a fun??o retornar algum objeto, a janela ativa poder? ser redirecionada. Assim,
 * os links podem usar a popupPage() que n?o retorna nada, somente abre a janela popup.
 */
function popupPageReturn(page, x, y, popupName) 
{
	//var x = 500;//page.width;
	//var y = 500;//page.height;
	var windowprops = "height=" + y + ",width=" + x + ",location=no,status=yes,scrollbars=yes,menubar=yes,toolbar=yes,resizable=yes";
	
	var myWindow = window.open(page, popupName,windowprops);
	
	if(x+y==0){
	  myWindow.moveTo(0,0);
      myWindow.resizeTo(screen.availWidth, screen.availHeight);
    }else{
	  myWindow.moveTo((screen.availWidth - x)/2, (screen.availHeight - y)/2);
    
    }
	
	// Coloca a janela em destaque
	myWindow.focus();
	
	// Adiciona a nova janela na lista de janelas
	addWindow(myWindow);

	return myWindow;
}

/**
 * Esta fun��o redimensiona a atual janela para o tamanho indicado e reposiciona no local solicitado.
 * caso top e left sejam omitidos, ela centraliza
 * �til para usar no evento onload() das janelas.
 */
function resizeWindow(top, left, width, height) 
{
	if(width+height==0){
   	   width = screen.availWidth;
   	   height = screen.availHeight;
	}
    window.resizeTo(width,height);

	if(top+left==0){
	  window.moveTo((screen.availWidth - width)/2, (screen.availHeight - height)/2);
    }else{
	  window.moveTo(left, top);
    }

}

/**
 * Esta fun??o abre uma janela sem barras adicionais e com o tamanho especificado.
 * A nova janela aberta n?o ? retornada. 
 * Links que usam as fun??es de abrir janela n?o necessitam de retorno, pois se 
 * a fun??o retornar algum objeto, a janela ativa poder? ser redirecionada. Assim,
 * os links podem usar esta fun??o.
 */
function popupPage(page, x, y) 
{
	popupPageReturn(page, x, y, "");
}

/**
 * Esta fun??o abre uma janela sem barras adicionais e com o tamanho especificado.
 * A nova janela aberta n?o ? retornada. 
 * Links que usam as fun??es de abrir janela n?o necessitam de retorno, pois se 
 * a fun??o retornar algum objeto, a janela ativa poder? ser redirecionada. Assim,
 * os links podem usar esta fun??o.
 */
function popupPageName(page, x, y, name) 
{
	popupPageReturn(page, x, y, name);
}

/**
 * Abre a p�gina requisitada na mesma janela ativa
 */
function windowPage(page) 
{
	window.location = page;
//	window.open(page, '_self', false);
}

function linkRetrieve(entityType, entityId)
{
	var page="../basic/retrieve.xhtml?entityType=" + entityType + "&entityId=" + entityId + "&link=true";
	popupPage(page,660,495);
}

function linkRetrieveParent(parentType, parentId, parentProperty)
{
	var page="../basic/retrieve.xhtml?parentType=" + parentType + "&parentId=" + parentId + "&parentProperty=" + parentProperty + "&link=true";  
	popupPage(page,660,495);
}

function linkQueryParent(parentType, parentId, parentProperty)
{
	var page="../basic/query.xhtml?parentType=" + parentType + "&parentId=" + parentId + "&parentProperty=" + parentProperty + "&link=true";  
	popupPage(page,660,495);
}

function linkRetrieveLocal(entityType, entityId)
{
	var page="../basic/retrieve.xhtml?entityType=" + entityType + "&entityId=" + entityId + "&link=true";
	windowPage(page);
}


/**
 * Exibe uma nova janela completa para um apesquisa
 */
function linkQuery(entityType)
{
	var page="../basic/query.xhtml?entityType=" + entityType + "&link=true";  
	popupPage(page,660,495);
}

function linkQueryFilter(entityType, filter)
{
	var page="../basic/query.xhtml?entityType=" + entityType + "&filter=" + filter + "&link=true";  
	popupPage(page,660,495);
}

function linkQueryFilterParent(parentType, parentId, parentProperty, filter)
{
	var page="../basic/query.xhtml?parentType=" + parentType + "&parentId=" + parentId + "&parentProperty=" + parentProperty + "&filter=" + filter + "&link=true";  
	popupPage(page,660,495);
}

function linkQueryUserReport(entityType, userReportId)
{
	var page="../basic/query.xhtml?entityType=" + entityType + "&userReportId=" + userReportId + "&link=true";  
	popupPageReturn(page,660,495,'Relat�rio');
}

/*
* Esta fun??o verifica se a tecla pressionada
* ? a tecla de pesquisa definida para acionar o onclik().
* Assim, o operador poder? apertar a tecla ao inv?s de
* clicar com o mouse.
*/
var KEY_OPEN_QUERY="p";

function checkKeyClick(input, event, keyChar)
{
  if((event.which==keyChar.charCodeAt(0)) && event.altKey){
     input.onclick();
     return false;
   }else
     return true;
}

/*
* Esta fun??o abre uma janela de pesquisa com:
* - o tipo de entidade determinado;
* - um filtro inicial;
* - o componente que dever? receber o id selecionado na pesquisa
*/
function openSelectOneId(entityType, filter, destInput)
{
   openSelectOneProp(entityType, filter, 'id', destInput);
}

/*
* Esta fun??o abre uma janela de pesquisa com:
* - o tipo de entidade determinado;
* - um filtro inicial;
* - o nome da propriedade da entidade que dever? ser retornada
* - o componente que dever? receber o valor do campo solicitado da entidade selecionada na pesquisa
*/
function openSelectOneProp(entityType, filter, srcProp, destInput)
{
	if(destInput == null)
		destInputId = 'noInput';
	else
		destInputId = destInput.id;
		
	if(srcProp == null) alert('� necess�rio informar um campo da entidade que ser� retornado: openSelectOneField(entityType, filter, srcField, destInput)');
	
	/** Informa para a janela que ser? chamada qual o id do componente que
	 dever? ser atualizado se o operador selecionar um id na lista */
	var page="../basic/query.xhtml?entityType=" + entityType + "&filter=" + filter + "&selectProperty=" + srcProp + "&selectOneDest=" + destInputId + "&link=true";  
	popupPageReturn(page,660,495,'');
}

function closeSelectOne(destInputId, id, queryWindow)
{
	
	/** Obtem o elemento 'selectOneId' que armazena o componente que dever?
	      receber o valor id
	   Chama a janela pai 
	   Busca o componente dentro do pai
	   Altera o valor do componente na janela pai
	 dever? ser atualizado se o operador selecionar um id na lista */
	if(destInputId !='noInput')
	{
		var parentWindow = queryWindow.opener;
		var destComponent = parentWindow.document.getElementById(destInputId);
		destComponent.value = id;
	
		/* Pelo estilo determina se submete ou n?o o formul?rio */
		if(destComponent.className == 'queryInputSelectOneSubmit')
			destComponent.form.submit();
		
		parentWindow.focus();
	}
	queryWindow.close();
}

function linkCreate(entityType)
{
	var page="../basic/create.xhtml?entityType=" + entityType + "&link=true";
	windowPage(page);
}

function linkCreatePopup(entityType)
{
	var page="../basic/create.xhtml?entityType=" + entityType + "&link=true";
	popupPage(page,660,495);
}

function linkCreateCopy(entityType, entityCopyId)
{
	var page="../basic/create.xhtml?entityType=" + entityType + "&entityId=" + entityCopyId + "&link=true";
	windowPage(page);
}

function linkDeletePopup(entityType, entityId)
{
	var page="../basic/delete1.xhtml?entityType=" + entityType + "&entityId=" + entityId + "&link=true";
	popupPage(page,660,495);
}

function linkDelete(entityType, entityId)
{
	var page="../basic/delete1.xhtml?entityType=" + entityType + "&entityId=" + entityId + "&link=true";
	windowPage(page);
}

function linkUpdatePopup(entityType, entityId)
{
	var page="../basic/update.xhtml?entityType=" + entityType + "&entityId=" + entityId + "&link=true";
	popupPage(page,660,495);
}
function linkUpdate(entityType, entityId)
{
	var page="../basic/update.xhtml?entityType=" + entityType + "&entityId=" + entityId + "&link=true";
	windowPage(page);
}

function linkCheckAuditCrud(entityType, entityId)
{
	var page="../basic/securityCheckAuditCrud.xhtml?entityType=" + entityType + "&entityId=" + entityId + "&link=true";  
	popupPage(page,660,300);
}


/**
 * Obtem a largura da região útil do browser.
 * Compatível com vários navegadores.
 * fonte: http://www.quirksmode.org/js/winprop.html
 */
function getFrameWidth()
{
  if (self.innerWidth)
  {
	return self.innerWidth;
  }
  else if (document.documentElement && document.documentElement.clientWidth)
  {
	return document.documentElement.clientWidth;
  }
  else if (document.body)
  {
	return document.body.clientWidth;
  }
  else
    return 0;
}


/**
 * Obtem a altura da região útil do browser.
 * Compatível com vários navegadores.
 * fonte: http://www.quirksmode.org/js/winprop.html
 */
function getFrameHeight()
{
  if (self.innerHeight)
  {
	return self.innerHeight;
  }
  else if (document.documentElement && document.documentElement.clientHeight)
  {
	return document.documentElement.clientHeight;
  }
  else if (document.body)
  {
	return document.body.clientHeight;
  }
  else
    return 0;
}

function getModule()
{
  // Percorrer o Documento e achar Input Hidden com nome 'Module'
  // Lembrando que um input sera identifica por Edit:_Edit01:Module
  // Ou seja, achar a subString :Module e retornar o valor do componente
  
  return ModuleName;
}

/******************************************************************************/
/*                                                                            */
/*  CONSTANTES QUE CONTROLAM OS NOMES DAS VISÔES                              */
/*  Ùteis para as visões que abrem popup e que é interessante abrir sempre    */
/*                                                                            */
/******************************************************************************/
var VIEW_LABEL_LIST="viewLabelList";
