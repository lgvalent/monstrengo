/* Este arquivo re?ne as fun??es de valida??o e outras utilit?rios que
 * podem ser utilizadas na telas do sistema
 *
 *	Essas fun??es s?o acionadas pelo 'onKeypress' do javascript
 *
 *	@author Juliana 20060407
 *	@version 20060407
 */


/* Esta fun��o permite somente a entrada
   de n�meros com uma v�rgula */
function keyPressFloat(input, e) {
	// Permite combina��o de teclas, como Ctrl+V
	if(e.ctrlKey) return true;
	
	if(window.event) {
           // for IE, e.keyCode or window.event.keyCode can be used
 	   key = e.keyCode;
	}
	else if(e.which) {
	   // netscape
	   key = e.which;
	}

	/* Permite entrada diferente de A..Z e a..z e permite as teclas de controles*/
	if ((key > 64) && (key < 126)){
		if ((key == 072) || (key == 75) || (key == 77) || (key == 80)) //setas de direcao
			return true;

		else
			return false;
	}

	if (key == 46)  return false;  //n�o permite '.' (ponto)


	/* Verifica se tem mais de uma v�rgula */
	/* Lucio 20110804: Com entrada de cole��es de BigDecimal, uma entrada poder� ter v�rias v�rgulas.
	 * Exemplo: 10,55;150,00;158,2;*/
//	if(key==44) {
//		return (input.value.indexOf(String.fromCharCode(key))==-1) && (input.value.length>0);
//
//	}

	return true;

}

/* Esta fun??o permite somente a entrada
   de n?meros inteiros */
function keyPressInt(input, e) {
	// Permite combina��o de teclas, como Ctrl+V
	if(e.ctrlKey) return true;

	if(window.event) {
           // for IE, e.keyCode or window.event.keyCode can be used
 	   key = e.keyCode;
	}
	else if(e.which) {
	   // netscape
	   key = e.which;
	}

	if ((key > 64) && (key < 126)){
		if ((key == 72) || (key == 75) || (key == 77) || (key == 80)) /*setas de direcao*/
			return true;

		else
			return false;
	}

	if ((key == 42) || (key == 44) || (key == 45) || (key == 46) || (key == 47))  return false;  /*n?o permite a entrada de '*' e '.' e '-' e ',' e '/', respectivamente*/

	return true;
}

/* Esta fun??o permite somente a entrada
 *  de datas
 * @deprecated Utilize keyPressCalendar() que � capaz de tratar
 *  m�scaras gen�ricas para dd/MM/yyyy
 */
function keyPressDate(input,e) {
	// Permite combina��o de teclas, como Ctrl+V
	if(e.ctrlKey) return true;

	if(window.event) {
           // for IE, e.keyCode or window.event.keyCode can be used
 	   key = e.keyCode;
	}
	else if(e.which) {
	   // netscape
	   key = e.which;
	}

	/* Permite entrada diferente de A..Z e a..z e permite as teclas de controles*/
	if ((key > 64) && (key < 126)){

		if ((key == 072) || (key == 75) || (key == 77) || (key == 80)) /*setas de direcao*/
			return true;

		else
			return false;
	}

	if (((key > 47) && (key < 58)) || (key<32)){
		/* Verifica se precisa inserir uma barra antes do n?mero */
		if(((input.value.length == 2) || (input.value.length == 5)) && !(key == 8)){
			input.value = input.value + "/";
		}
	}

	return true;
}


/* Esta fun??o permite entrada
   de qualquer mascara */

function keyPressMask(input_,mask_) {
    input=input_;
    mask=mask_;
    setTimeout("maskGenerics()",1)
}

function maskGenerics(){
  /* Remove os caracteres que n�o s�o (A a Z e 0 a 9) */
//  var entrada = input.value.replace(/[^a-zA-Z0-9\/]/g, "");
  var entrada = input.value;

  var mascara = mask;
  var saida = "";
  var i_ent = 0;
  var i_masc = 0;

  // Verifica se no input o conte�do j� est� no tamanho m�ximo da m�scara
  if(entrada.length > mascara.length){
     saida = entrada.substring(0,mascara.length);
  }else 
     /* Vai consumindo a entrada e a m�scara, quando um terminar o la�o p�ra */
     while((i_ent < entrada.length) && (i_masc < mascara.length)){
  	   /* Verifica na m�scara qual caractere � esperado (Letras de A a Z) */
  	   if(mascara.charAt(i_masc) == 'U'){
          var str = "" + entrada.charAt(i_ent);
          if(str.match(/[a-zA-Z]/)){
  	   	     saida += str.toUpperCase();
  	   	     i_masc++;
  	      }
          i_ent++;

  	   /* Verifica na m�scara qual caractere � esperado (0 a 9)*/
       }else if((mascara.charAt(i_masc) == '#') ||(mascara.charAt(i_masc) == '*') ){
           var str = "" + entrada.charAt(i_ent);
           if(str.match(/[0-9]/)){
  	   	      saida += str;
  	          i_masc++;
  	       }
           i_ent++;

  	   /* Verifica na m�scara qual caractere � esperado (Letras de A a Z) */
       }else if(mascara.charAt(i_masc) == 'L'){
           var str = "" + entrada.charAt(i_ent);
           if(str.match(/[a-zA-Z]/)){
  	   	      saida += str.toLowerCase();
  	   	      i_masc++;
  	       }
  	       i_ent++;

  	   /* Verifica na m�scara qual caractere � esperado (Qualquer caractere (A a Z e 0-9 ) */
       }else if(mascara.charAt(i_masc) == '?'){
            var str = "" + entrada.charAt(i_ent);
            if(str.match(/[0-9a-zA-Z]/)){
  	   	       saida += str;
  	   	       i_masc++;
  	        }
  	        i_ent++;
       }else{
  	   // Caractere n�o � de controle da m�scara (U,L, #,?), ent�o devolve ele na saida
  	       saida += mascara.charAt(i_masc);
  	       i_masc++;
       }
     }
     input.value = saida;
}


/* Dispara o mecanismo de timeOut para tratar m�scara de Calendar
 */
function keyPressCalendar(input_,mask_) {
  input=input_;
  mask=mask_;
  setTimeout("keyPressCalendar_()",1)
}

/* Dispara o tratamento da m�scara de Calendar
 * na sa�da, pois durante o onKeyPress fica ruim alterar parte da
 * data
 */
function onblurCalendar(input_,mask_) {
  input=input_;
  mask=mask_;
  setTimeout("keyPressCalendar_()",1)
}

/* Este m�todo trata algumas particularidades do tratamento da
 * m�scara de datas que ao inv�s de usar os padr�es U, L, ? e *
 * utiliza o padr�o dd/MM/yyyy hh:mm:ss
 */

function keyPressCalendar_() {
  /* Acrescenta 20 ou 19 se a data estiver com 6 digitos 010101 => 01012001*/
  if(input.value.length == 6){
    /* Verifica se o ano est� no futuro ou passado para determinar um
       prefixo 19 ou 20 */
    ano = parseInt(input.value.substr(4,2));
    anoCorrente = new Date().getFullYear();
    anoPrefixo = "";
   
    if(ano < 50) 
      anoPrefixo = "20"
    else
      anoPrefixo = "19"

    input.value = input.value.substr(0,4) + anoPrefixo + input.value.substr(4,2);
  }
  
  /* Remove caracteres n�o num�ricos */
  input.value = input.value.replace(/[^a-zA-Z0-9\/:]/g, "");

  /* Verifica se o primeiro caractere � uma barra '/'
   * Se for, ent�o significa que o operador poder� entrar somente
   * o m�s do tipo /01
   */
  if(input.value.charAt(0) == '/'){
    /* Define a m�scara PUBLICA como /## */
    mask="/##";
  }else{
    /* Transformando dd/MM/yyyy hh:mm:ss em ##/##/#### ##:##:## */
    mask = mask.replace(/[dDmMyYhHsS]/g, "#");
  }
  setTimeout("maskGenerics()",1)
}

/* Verifica se a data digitada possui somente 2 d�gitos de ano para acrescentar o 19--
 */
function onblurCalendar_() {
  if(input.value.length == 8)
  	 input.value = input.value.substr(0,6) + '20' + input.value.substr(6,8); 
//  if(input.value.length == 2 || input.value.length == 3)
//  	 input.value = input.value.substr(0,2) + new Date().getMonth() + '/' + input.value.substr(6,8); 
}

function checkAll(form, patern){
    for(i=0;i< form.length;i++){
	    o = form.elements[i];
	    if(o.id.lastIndexOf(patern)!=-1){
	       o.checked = true;
	    }
    }
    return false;
  }

  function clearAll(form, patern){
    for(i=0;i< form.length;i++){
	    o = form.elements[i];
	    if(o.id.lastIndexOf(patern)!=-1){
	       o.checked = false;
	    }
    }
    return false;
  }

  function inverseAll(form, patern){
    for(i=0;i< form.length;i++){
	    o = form.elements[i];
	    if(o.id.lastIndexOf(patern)!=-1){
	       o.checked = !o.checked;
	    }
    }
    return false;
  }


  function checkAll(form, patern){
    for(i=0;i< form.length;i++){
	    o = form.elements[i];
	    if(o.id.lastIndexOf(patern)!=-1){
	       o.checked = true;
	    }
    }
    return false;
  }

  function clearAll(form, patern){
    for(i=0;i< form.length;i++){
	    o = form.elements[i];
	    if(o.id.lastIndexOf(patern)!=-1){
	       o.checked = false;
	    }
    }
    return false;
  }

  function inverseAll(form, patern){
    for(i=0;i< form.length;i++){
	    o = form.elements[i];
	    if(o.id.lastIndexOf(patern)!=-1){
	       o.checked = !o.checked;
	    }
    }
    return false;
  }

  function checkRange(form, patern){
    var rangeStarted = false;
    var rangeFinished = false;

    for(i=0;i< form.length;i++){
	    o = form.elements[i];
	    if(o.id.lastIndexOf(patern)!=-1){
	       if(o.checked&&!rangeStarted){
	         rangeStarted = true;
	       }else if(o.checked&&rangeStarted){
	         rangeFinished = true;
	       }else if(rangeStarted&&!rangeFinished){
	         o.checked = true;
	       }
	    }
    }

    return false;
  }


/* Utils */
function isNumber(number){
   var invalid = false;

   for (i=0; i < number.length; i++){
      var char = number.charAt(i);
      if(char != "." && char != "," && char != "-"){
         if (isNaN(parseInt(char))) invalid = true;
      }
   }
   return !invalid;
}

/** Remove os caracteres que n�o sema */
function onblurAlfanumeric(input){
	   input.value = input.value.replace(/[^a-zA-Z0-9 ]/g, "");
}

/** Controla a ativa��o de componentes para recuperar focus ap�s uma chamada Ajax
 * no onfocus:setActiveComponent(this)
 * no ajax:oncomplete:getActiveComponent() 
 * Exemplo de uso: quitarLancamento2.jsp */
function setActiveComponent(c){
	componentName = c.name;
}

function getActiveComponent(){
	document.getElementById(componentName).focus();
	document.getElementById(componentName).select();

	return document.getElementById(componentName);
}
