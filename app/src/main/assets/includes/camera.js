var foto_propriedades = {id_ponto:"", id_ocorrencia:"", id_os:"", id_vistoria:"", id_item:"", nome:"", otima:"", simak:"" };
function tiraFoto(nome, id_ponto, id_ocorrencia, id_os, id_vistoria, id_item, otima, simak){
	if( typeof(Android) != "undefined"){
		//loading();
		nome = nome + "_" + new Date().getTime();
		foto_propriedades.id_ponto = id_ponto;
		foto_propriedades.otima = otima;
		foto_propriedades.simak = simak;
		foto_propriedades.id_ocorrencia = id_ocorrencia; 
		foto_propriedades.id_os = id_os;
		foto_propriedades.id_vistoria = id_vistoria;
		foto_propriedades.id_item = id_item;
		foto_propriedades.nome = nome;
		var newExif = "";
		newExif += "DADOS KALITERA\n";
		newExif += "SIMAK: "+ foto_propriedades.simak +"\n";
		newExif += "OTIMA: "+ foto_propriedades.otima +"\n";
		newExif += "OCORRENCIA: "+ foto_propriedades.id_ocorrencia +"";
		Android.js_tiraFoto(foto_propriedades.nome, 'cb_tiraFoto', newExif );
	}else{
		// fazer implementacao pelo browser com FileAPI
		alert( "TIRA FOTO" );
	}
}
function cb_tiraFoto(path){
	var b64 = Android.js_getBase64(path);
	if(b64.indexOf('ERRO') < 0){
		var newsrc = "data:image/jpeg;base64," + b64;
		var img = new Image();
		img.setAttribute("stamp", new Date().getTime() );
		img.setAttribute("foto","ok");
		img.setAttribute("id_ponto",foto_propriedades.id_ponto);
		img.setAttribute("id_ocorrencia",foto_propriedades.id_ocorrencia);
		img.setAttribute("id_os",foto_propriedades.id_os);
		img.setAttribute("id_vistoria",foto_propriedades.id_vistoria);
		img.setAttribute("id_item",foto_propriedades.id_item);
		img.setAttribute("nome",foto_propriedades.nome);
		img.setAttribute("uploaded","no");
		img.setAttribute("data",getDate());
		img.src = newsrc;
		img.width = 300;
		document.getElementById('img_content').appendChild(img);
		foto_propriedades = {id_ponto:"", id_ocorrencia:"", id_os:"", id_vistoria:"", id_item:"", nome:""};
	}else{
		// Exibe msg de erro
		alert( b64 );
	}
}
