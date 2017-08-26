var xmlloaded = false;
var xmlHttp;
var parametro;
var subparametro;
var endereco = "";
var URL_WEBSERVICE = "";

function abreTela(tipo, novo_parametro, novo_subparametro){
	parametro = novo_parametro;
	subparametro = novo_subparametro;
	newHref(tipo + ".htm");
}
function newHref(_endereco){
	endereco = _endereco;
	loading('show','yes');
	xmlloaded = false;
	runScript = function(){};
	try{
		xmlHttp = new XMLHttpRequest();
		if(isAndroid){
			xmlHttp.open('GET', 'content://com.kalitera.sismanut/' + endereco+'#'+(new Date().getTime()), true);
		}else{
			xmlHttp.open('GET', 'ajax/' + endereco+'#'+(new Date().getTime()), true);		
		}
		xmlHttp.onreadystatechange = function(){
			if( xmlHttp.readyState == 4 ){
				var htmlRetorno = xmlHttp.responseText
				xmlloaded = true;
				$('#conteudo').html(htmlRetorno);			
				if( document.getElementById('novo_rodape') ){
					$('#rodape').html( $('#novo_rodape').html() );
					$('#novo_rodape').html('');
					document.getElementById('rodape').style.display = 'block';
					$('#rodape').trigger('create');
				}else{
					document.getElementById('rodape').style.display = 'none';
					$('#rodape').html('');				
				}
				loading('hide');
				$('#conteudo').trigger('create');
				$('html, body').animate({ scrollTop: 0 }, 0);
				//window.location.href="#" + endereco.split('.htm').join('');	
				//volta para o topo da tela
				window.location.href="#";	
				// Ultima coisa: roda o script da tela carregada
				runScript();
			}	
		};
		xmlHttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xmlHttp.send(null);
	}catch(e){
		alert('Browser não suporta acesso local: \n\nUsar: "chrome.exe --allow-file-access-from-files"');
	}
}
function menuApp(){
	Android.openMenu();
}
function fechaApp(){
	Android.closeMyActivity();
}
$(document).ready(function() {
	if(typeof(Android) == 'undefined'){
		//Rodando do Browser, e não do app - usar chrome.exe --allow-file-access-from-files
		isAndroid = false;
		document.getElementById('bt_menu').style.display = 'none';
		URL_WEBSERVICE = "http://homologacao.kalitera.com.br/ajax/";
	}else{
		URL_WEBSERVICE = Android.getWebService();
	}
});
var isAndroid = true;
var file = window.location.pathname.substring(window.location.pathname.lastIndexOf('/') + 1);