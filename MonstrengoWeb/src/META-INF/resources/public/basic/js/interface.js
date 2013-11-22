/**
 * Lucio 08/08/2006
 * Para que as imegens das bordas n?o sejam impressas, todas elas foram 
 * definidas como background. No entanto, assim, as tabelas que as contem, que n?o
 * possuem conteudos, ficam com tamanho ZERO e o fundo n?o ? exibido. Para resolver isto,
 * no canto superior esquerdo e no canto inferior direito s?o colocadas as imagens
 * <img src="' + this.URL_IMAGES + 'inner_box_bg.png">, que ? uma imagem transparente
 * mas ocupa o espa?o necess?rio.
 
 */
function BorderCreator(urlInterface)
{
	this.URL_INTERFACE;
	this.URL_IMAGES;
	this.initBorder = initBorder;
	this.endBorder = endBorder;
	this.initOuterBorder = initOuterBorder;
	this.endOuterBorder = endOuterBorder;
	this.initDarkBorder = initDarkBorder;
	this.endDarkBorder = endDarkBorder;
	
	if(!urlInterface) {
		this.URL_INTERFACE = './';
	}
	else {
		this.URL_INTERFACE = urlInterface;
	}	
	this.URL_IMAGES = this.URL_INTERFACE + '../../public/basic/img/';
	
	function initBorder(width, height, align, valign) {
		document.write('<table width="' + width + '" height="' + height + '"	border="0"  cellpadding="0" cellspacing="0">');
		document.write('<tr>');
		document.write('<td background="' + this.URL_IMAGES + 'inner_box_top_left.gif"><img src="' + this.URL_IMAGES + 'inner_box_bg.png"></td>');
		document.write('<td width="100%" background="' + this.URL_IMAGES + 'inner_box_top.gif"></td>');
		document.write('<td background="' + this.URL_IMAGES + 'inner_box_top_right.gif"></td>');
		document.write('</tr>');
		document.write('<tr>');
		document.write('<td background="' + this.URL_IMAGES + 'inner_box_left.gif"></td>');
		document.write('<td background="' + this.URL_IMAGES + 'inner_box_bg.gif" align="' + align + '" valign="' + valign + '" height="' + height + '" >');
	}
	
	function endBorder() {
		document.write('</td>');
		document.write('<td background="' + this.URL_IMAGES + 'inner_box_right.gif"></td>');
		document.write('</tr>');
		document.write('<tr>');
		document.write('<td background="' + this.URL_IMAGES + 'inner_box_bottom_left.gif"></td>');
		document.write('<td background="' + this.URL_IMAGES + 'inner_box_bottom.gif"></td>');
		document.write('<td background="' + this.URL_IMAGES + 'inner_box_bottom_right.gif"><img src="' + this.URL_IMAGES + 'inner_box_bg.png"></td>');
		document.write('</tr>');
		document.write('</table>');
	}
	
	function initDarkBorder(width, height, align, valign) {
		document.write('<table width="' + width + '"	border="0"  cellpadding="0" cellspacing="0">');
		document.write('<tr>');
		document.write('<td background="' + this.URL_IMAGES + 'dark_inner_box_top_left.gif"><img src="' + this.URL_IMAGES + 'dark_inner_box_bg.png"></td>');
		document.write('<td width="100%" background="' + this.URL_IMAGES + 'dark_inner_box_top.gif"></td>');
		document.write('<td background="' + this.URL_IMAGES + 'dark_inner_box_top_right.gif"></td>');
		document.write('</tr>');
		document.write('<tr>');
		document.write('<td background="' + this.URL_IMAGES + 'dark_inner_box_left.gif"></td>');
		document.write('<td background="' + this.URL_IMAGES + 'dark_inner_box_bg.gif" align="' + align + '" valign="' + valign + '" height="' + height + '" >');
	}
	
	function endDarkBorder() {
		document.write('</td>');
		document.write('<td background="' + this.URL_IMAGES + 'dark_inner_box_right.gif"></td>');
		document.write('</tr>');
		document.write('<tr>');
		document.write('<td background="' + this.URL_IMAGES + 'dark_inner_box_bottom_left.gif"></td>');
		document.write('<td background="' + this.URL_IMAGES + 'dark_inner_box_bottom.gif"></td>');
		document.write('<td background="' + this.URL_IMAGES + 'dark_inner_box_bottom_right.gif"><img src="' + this.URL_IMAGES + 'dark_inner_box_bg.png"></td>');
		document.write('</tr>');
		document.write('</table>');
	}
	
	function initOuterBorder(width, height, align, valign) {
		document.write('<table width="' + width + '"	border="0"  cellpadding="0" cellspacing="0">');
		document.write('<tr>');
		document.write('<td><img src="' + this.URL_IMAGES + 'outer_box_top_left.gif"></td>');
		document.write('<td width="100%" background="' + this.URL_IMAGES + 'outer_box_top.gif"><img src="' + this.URL_IMAGES + 'outer_box_top.gif"></td>');
		document.write('<td><img src="' + this.URL_IMAGES + 'outer_box_top_right.gif"></td>');
		document.write('</tr>');
		document.write('<tr>');
		document.write('<td background="' + this.URL_IMAGES + 'outer_box_left.gif"><img src="' + this.URL_IMAGES + 'outer_box_left.gif"></td>');
		document.write('<td background="' + this.URL_IMAGES + 'outer_box_bg.gif" align="' + align + '" valign="' + valign + '" height="' + height + '" >');
	}
	
	function endOuterBorder() {
		document.write('</td>');
		document.write('<td background="' + this.URL_IMAGES + 'outer_box_right.gif"><img src="' + this.URL_IMAGES + 'outer_box_right.gif"></td>');
		document.write('</tr>');
		document.write('<tr>');
		document.write('<td><img src="' + this.URL_IMAGES + 'outer_box_bottom_left.gif"></td>');
		document.write('<td background="' + this.URL_IMAGES + 'outer_box_bottom.gif"><img src="' + this.URL_IMAGES + 'outer_box_bottom.gif"></td>');
		document.write('<td><img src="' + this.URL_IMAGES + 'outer_box_bottom_right.gif"></td>');
		document.write('</tr>');
		document.write('</table>');
	}	
}


function GridCreator(){
	this.lineClass1 = 'gridTd1';
	this.lineClass2 = 'gridTd2';
	this.headerClass = 'headerTd';
	this.footerClass = 'footerTd';
	this.gridClass = 'gridTable';
	
	this.swapCounter = true;
	this.enableSwap = true;	
	
	this.initGrid = initGrid;
	this.endGrid = endGrid;
	this.initLine = initLine;
	this.endLine = endLine;
	this.initDataColumn = initDataColumn;
	this.endDataColumn = endDataColumn;
	this.initHeaderColumn = initHeaderColumn;
	this.endHeaderColumn = endHeaderColumn;
	this.initFooterColumn = initFooterColumn;
	this.endFooterColumn = endFooterColumn;
	this.drawHeaderColumn = drawHeaderColumn;
	this.drawDataColumn = drawDataColumn;
	this.drawFooterColumn = drawFooterColumn;

	function initGrid(width, height, canEnableSwap) {
		this.enableSwap = canEnableSwap;
		this.swapCounter = true;
		document.write('<table width="' + width + '" height="' + height + '" border="0"  cellpadding="3" cellspacing="1" class="' + this.gridClass + '">');
	}
	
	function endGrid() {
		this.enableSwap = true;
		this.lineClass = 'gridLine1';
		document.write('</table>');
	}
	
	function initLine() {
		this.swapCounter = !this.swapCounter;
		document.write('<tr>');
	}
	
	function endLine() {
		document.write('</tr>');
	}
	
	function initHeaderColumn(width, height, align, valign, colspan) {
		this.initDataColumn(width, height, align, valign, colspan, this.headerClass);
	}
	
	function endHeaderColumn() {
		this.endDataColumn();
	}
	
	function drawHeaderColumn(value, width, height, align, valign, colspan) {
		this.initHeaderColumn(width, height, align, valign, colspan);
		document.write(value);
		this.endHeaderColumn();
	}
	
	function initDataColumn(width, height, align, valign, colspan, customCssClass) {
		var cssClass = this.lineClass1;
		if(this.enableSwap){
			if(this.swapCounter){
				cssClass = this.lineClass2;
			}
		}
		if(customCssClass){
			cssClass = customCssClass;
		}		
		document.write('<td width="' + width + '" height="' + height + '" align="' + align + '" valign="' + valign + '" class="' + cssClass + '" colspan="' + colspan + '">');
	}
	
	function endDataColumn() {
		document.write('</td>');
	}
	
	function drawDataColumn(value, width, height, align, valign, colspan, customCssClass) {
		this.initDataColumn(width, height, align, valign, colspan, customCssClass);
		document.write(value);
		this.endDataColumn();
	}
	
	function initFooterColumn(width, height, align, valign, colspan) {
		this.initDataColumn(width, height, align, valign, colspan, this.footerClass);
	}
	
	function drawFooterColumn(value, width, height, align, valign, colspan){
		this.initFooterColumn(width, height, align, valign, colspan);
			document.write(value);
		this.endFooterColumn();
	}	
	
	function endFooterColumn() {
		this.endDataColumn();
	}
}

function changeStyle(anObject, anCssClass)
{
	anObject.className = anCssClass;
}

/* gambi */

function GridCreator2(){
	this.lineClass1 = 'gridTd1';
	this.lineClass2 = 'gridTd2';
	this.headerClass = 'headerTd';
	this.footerClass = 'footerTd';
	this.gridClass = 'gridTable';
	
	this.swapCounter = true;
	this.enableSwap = true;	
	
	this.initGrid = initGrid;
	this.endGrid = endGrid;
	this.initLine = initLine;
	this.endLine = endLine;
	this.initDataColumn = initDataColumn;
	this.endDataColumn = endDataColumn;
	this.initHeaderColumn = initHeaderColumn;
	this.endHeaderColumn = endHeaderColumn;
	this.initFooterColumn = initFooterColumn;
	this.endFooterColumn = endFooterColumn;
	this.drawHeaderColumn = drawHeaderColumn;
	this.drawDataColumn = drawDataColumn;
	this.drawFooterColumn = drawFooterColumn;

	function initGrid(width, height, canEnableSwap) {
		this.enableSwap = canEnableSwap;
		this.swapCounter = true;
		document.write('<table width="' + width + '" height="' + height + '" border="0"  cellpadding="3" cellspacing="1" class="' + this.gridClass + '">');
	}
	
	function endGrid() {
		this.enableSwap = true;
		this.lineClass = 'gridLine1';
		document.write('</table>');
	}
	
	function initLine() {
		this.swapCounter = !this.swapCounter;
		document.write('<tr>');
	}
	
	function endLine() {
		document.write('</tr>');
	}
	
	function initHeaderColumn(width, height, align, valign, colspan) {
		this.initDataColumn(width, height, align, valign, colspan, this.headerClass);
	}
	
	function endHeaderColumn() {
		this.endDataColumn();
	}
	
	function drawHeaderColumn(value, width, height, align, valign, colspan) {
		this.initHeaderColumn(width, height, align, valign, colspan);
		document.write(value);
		this.endHeaderColumn();
	}
	
	function initDataColumn(width, height, align, valign, colspan, customCssClass) {
		var cssClass = this.lineClass1;
		if(this.enableSwap){
			if(this.swapCounter){
				cssClass = this.lineClass2;
			}
		}
		if(customCssClass){
			cssClass = customCssClass;
		}		
		document.write('<td width="' + width + '" height="' + height + '" align="' + align + '" valign="' + valign + '" class="' + cssClass + '" colspan="' + colspan + '">');
	}
	
	function endDataColumn() {
		document.write('</td>');
	}
	
	function drawDataColumn(value, width, height, align, valign, colspan, customCssClass) {
		this.initDataColumn(width, height, align, valign, colspan, customCssClass);
		document.write(value);
		this.endDataColumn();
	}
	
	function initFooterColumn(width, height, align, valign, colspan) {
		this.initDataColumn(width, height, align, valign, colspan, this.footerClass);
	}
	
	function drawFooterColumn(value, width, height, align, valign, colspan){
		this.initFooterColumn(width, height, align, valign, colspan);
			document.write(value);
		this.endFooterColumn();
	}	
	
	function endFooterColumn() {
		this.endDataColumn();
	}
}

/* Procura dentro dos formul?rios o primeiro elemento
que pode receber foco. Fonte: http://javascript.internet.com/forms/form-focus.html 
Alterado por Lucio para pegar todos os formul?rios da p?gina
*/
function placeFocus() {
  /* Verifica se o browser suporta e se a p?gina j? foi rolada pelo usu?rio
    para n?o tirar a p?gina donde ele posicionou */
  if(window.pageYOffset != null && window.pageYOffset == 0){
    if (document.forms.length > 0) {
	  for (f = 0; f < document.forms.length; f++) {
        var form = document.forms[f];
        for (i = 0; i < form.length; i++) {
      	  field = form.elements[i];
          // Seleciona inputs e selects (verifica se a propriedade type existe antes do .toString() 
          if ((field.type == "text") || (field.type == "textarea") || (field.type != null && field.type.toString().charAt(0) == "s")) {
        	if(!field.disabled){
	           field.focus();
    	       
    	       // Somente inputs tem .select()
    	       if((field.type == "text") || (field.type == "textarea"))
	    	       field.select();
        	   // interrompe a funçao no primeiro componente encontrado
        	   return;
        	}
          }
        }
      }
    }
  }
}

/* 
Procura dentro dos formulários o ÚLTIMO elemento que pode receber foco. 
Fonte: http://javascript.internet.com/forms/form-focus.html 
Criado por Andre para manter o último campo texto como foco nos wizards do projeto ProcessoSeletivo
*/
function placeLastFocus() {
  /* Verifica se o browser suporta e se a página já foi rolada pelo usuário
    para não tirar a página donde ele posicionou */
  if(window.pageYOffset != null && window.pageYOffset == 0){
    if (document.forms.length > 0) {
	  for (f = document.forms.length-1; f >= 0; f--) {
        var form = document.forms[f];
        for (i = form.length-1; i >= 0; i--) {
      	  field = form.elements[i];
          // Seleciona inputs  
          if ((field.type == "text") || (field.type == "textarea")) {
        	if(!field.disabled){
	           field.focus();
    	       break;
        	}
          }
        }
      }
    }
  }
}
