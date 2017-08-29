package net.decaedro.autentica;

import java.io.Serializable;

public class User implements Serializable {

	public String url_webservice;
	public String id_usuario;
	public String nome_usuario;
	public String id_perfil;
	public String nome_perfil;
	public String permissoes;

	public String username;
	public String sessionToken;

	public String toString() {
		String retorno = "";
		retorno += "{";
		retorno += "'url_webservice':'"+url_webservice+"',";
		retorno += "'id_usuario':'"+id_usuario+"',";
		retorno += "'nome_usuario':'"+nome_usuario+"',";
		retorno += "'id_perfil':'"+id_perfil+"',";
		retorno += "'nome_perfil':'"+nome_perfil+"',";
		retorno += "'permissoes':'"+permissoes+"',";
		retorno += "'username':'"+username+"',";
		retorno += "'sessionToken':'"+sessionToken+"'";
		retorno += "}";
		return retorno;
	}
}