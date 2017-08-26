var querys = [];
var querystring;

var firstrun = true;

var ca_counter = 0;
var ca_indice = ['','id_os','id_os','id_vistoria'];
var ca_telas = ['','publicidade_lista_gps','manutencao_lista_gps','vistoria_lista_gps'];
var ca_tables = ['','publicidades','manutencoes','vistorias'];

var tabelas = [
	'carros',
	'fotografias',
	'usuarios',
	'vistoriasitens',
	'vistorias',
	'vistoriaspontos',
	'manutencoes',
	'manutencoespontos',
	'publicidades',
	'publicidadespontos',
	'publicidadeimagens',
	'itenstipo'
];

var logout_callback;

var counter = 0;

$(document).ready(function() {
	if(!$.sql.open()){
		alert('Versão do Browser não suportada!');
		return
	}
	var arrquery = location.search;
	arrquery = arrquery.split('?').join('');
	arrquery = arrquery.split('#').join('');
	arrquery = '({"' + arrquery.split('&').join('","').split('=').join('":"') + '"})';
	if(arrquery != '({""})'){
		querystring = eval(arrquery);
	}
	chkLogado();
});

function chkLogado(result){
	if (typeof(result) == "undefined") {
		var sqlLogado = "SELECT * FROM usuarios where logado_mobile = 'true' LIMIT 1";
		$.sql.query([sqlLogado],{ success:chkLogado, failure:chkLogado } );
	}else{
		if(typeof(result) == "string" || result == false || result.length == 0 ){
			// Possui as tabelas, mas não tem ninguém logado
			if( typeof(Android) != "undefined" ){
				// Usa credenciais do SisAutentica
				$('#bt_usuario').html($('#bt_usuario').html().split('***nome***').join( Android.js_NOMEUSUARIO() ));
				$('#token').val( Android.js_TOKEN() );
				document.getElementById("bt_usuario").style.display="block";

				abreTela("sincronizar");
			}else{
				// Abre tela pedindo CPF e Senha
				abreTela("login");
			}
		}else{
			// Ta tudo ok, e usuario logado
			$('#bt_usuario').html($('#bt_usuario').html().split('***nome***').join(result.item(0)['nome']));
			$('#token').val( result.item(0)['token'] );

			document.getElementById("bt_usuario").style.display="block";

			// Implementacao pra ir contra o bug do android de querystring
			if(typeof(Android) != 'undefined'){
				var qs = Android.getQueryString();
				//alert( qs );
				if(qs != '({""})'){
					querystring = eval( qs );
				}else{
					querystring = undefined
				}
			}

			if( typeof(querystring) == "undefined" || typeof(querystring.tela) == "undefined" || querystring.tela == "" ){

				checaAndamento();

			}else{
				if(querystring.tela == 'logout'){
					Logout();
				}else{
					var __tela = querystring.tela;
					var __parametro = querystring.parametro;
					var __subparametro = querystring.subparametro;
					querystring.tela = "";
					querystring.parametro = "";
					querystring.subparametro = "";
					abreTela(__tela, __parametro, __subparametro);
				}
			}	

			// exibe dados do usuario logado
			/*
			for (var i = 0; i < result.length; i++) {
				var msg = "Usuário Logado:\n\n";
				for (var j in result.item(i)) {
					msg+= j +': '+ result.item(i)[j] + "\n";
				}
				alert( msg );
			}
			*/
		}
	}
}
function Login(){
	if( $("#senha").val() != "" ){
		loading();
		var imei = "WEBBROWSER";
		if(typeof(Android) != "undefined"){
			imei = Android.getIMEI();
		}
		$.ajax({
			type: "POST",
			url: "http://autenticacao.kalitera.com.br",
			data: {
				cpf:   cleanUpCPF($("#cpf").val()),
				senha: md5($("#senha").val()),
				device: imei
			},
			success: function(json){
				if( json != "null" ){
					var objJSON = eval( '(' + json + ')' );
					$('#token').val(objJSON.sessionToken);
					URL_WEBSERVICE = objJSON.url_webservice;
					abreTela("sincronizar");
				}else{
					alert('Usuário ou senha inválidas.');
				}
			}
		});
	}
};

function Logout(callback){
	counter = 0;
	if( typeof(callback) != "undefined" ){
		logout_callback = callback;
	}else{
		logout_callback = function(){
			location.href="index.htm";
		};
	}
	var drop_tabelas = ('drop table '+ tabelas.join(',drop table ')).split(',');
	$.sql.query(drop_tabelas ,{ failure: contaDrop, success:contaDrop } );
}

function contaDrop(){
	counter++
	if( counter == tabelas.length ){
		logout_callback();
	}
}

function checaAndamento(result){
	var __id = "";
	var __tela = "";
	if(firstrun){
		__tela = "sincronizar";
	}else{
		__tela = "tarefas";
	}
	var checa = true;

	if(typeof(result) == "undefined" || typeof(result) == "string" || result == false || result.length == 0 ){
		checa = true;
	}else{
		__tela = ca_telas[ca_counter];
		__id = result.item(0)[ca_indice[ca_counter]];
		checa = false;
	}
	if( checa && ca_counter < ca_tables.length ){
		ca_counter++;
		var tbl = ca_tables[ca_counter];
		var ca_sql = "SELECT * FROM " + tbl + " where andamento = 'true' LIMIT 1";
		$.sql.query( ca_sql ,{ success:checaAndamento, failure:checaAndamento } );
	}else{
		abreTela(__tela, __id);
	}
}