package net.decaedro.autentica;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;


import java.io.IOException;
import java.io.Serializable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

public class ParseAuthenticate implements ServerAuthenticate{

	public String MD5(String password) {
		String retorno = "";
		try {
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(password.getBytes());
			byte messageDigest[] = digest.digest();
	  
			StringBuffer MD5Hash = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2)
					h = "0" + h;
				MD5Hash.append(h);
			}
			retorno = MD5Hash.toString();	 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return retorno;
	}

    @Override
    public String userSignUp(String name, String email, String pass, String authType) throws Exception {
        String authtoken = null;
        return authtoken;
    }

    @Override
    public User userSignIn(String device, String user, String pass, String authType) throws Exception {
		String _url = "https://tie4.decaedro.net/autenticacao/index.php";
		String responseString = "";

		URL url = new URL(_url);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(15000);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		Uri.Builder builder = new Uri.Builder()
				.appendQueryParameter("cpf", user)
				.appendQueryParameter("senha", pass)
				.appendQueryParameter("device", device);
		String query = builder.build().getEncodedQuery();
		OutputStream os = conn.getOutputStream();
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(os, "UTF-8"));
		writer.write(query);
		writer.flush();
		writer.close();
		os.close();
		conn.connect();
		int responseCode = conn.getResponseCode();
		if (responseCode == HttpsURLConnection.HTTP_OK) {
			String line;
			BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line=br.readLine()) != null) {
				responseString+=line;
			}
		} else {
			ParseComError error = new Gson().fromJson(responseString, ParseComError.class);
			throw new Exception("Error signing-in ["+error.code+"] - " + error.error);
		}

		User loggedUser = null;
        try {
			if (responseString.indexOf("erro")>0) {
				Error error = new Gson().fromJson(responseString, Error.class);
				Log.d("bla", responseString);
				throw new Exception(error.erro);
			}
            loggedUser = new Gson().fromJson(responseString, User.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

		conn.disconnect();

        return loggedUser;
    }

    private class ParseComError implements Serializable {
        int code;
        String error;
    }
}
