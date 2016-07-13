package net.decaedro.tie;

import android.app.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import android.content.*;
import android.net.Uri;
import android.util.Base64;
import android.provider.MediaStore;
import android.media.ExifInterface;

import java.lang.Exception;
import java.io.*;

import net.decaedro.DKWebView;
import net.decaedro.DKActivity;

public class MainActivity extends DKActivity {

	public String php_endereco = "http://ocorrencias.decaedro.net";
	public String userAgent = "Decaedro Ocorrencias";

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case TAKE_PICTURE:
				if(resultCode == Activity.RESULT_OK) {
					Uri mypic = picUri;
					mUploadMessage.onReceiveValue(mypic);
					mUploadMessage = null;
				} else {
					mUploadMessage.onReceiveValue(null);
					mUploadMessage = null;
					return;
				}
			case TAKE_B64_PICTURE:
				if(resultCode == Activity.RESULT_OK) {
					String path = picUri.toString().replace("file://","");
					try {
						ExifInterface exif = new ExifInterface(path);
						exif.setAttribute("UserComment", newExif);
						exif.saveAttributes();
					}catch(Exception e){
						showMsg( "Erro com EXIF: " + e.toString() );
					}
					myWebView.loadUrl("javascript:cb_tiraFoto('" + path +"')");
				} else {
					return;
				}
			default:
				return;
		}
	}

	public String getBase64( String path ){
		String encodedImage = "";
		try{
			FileInputStream mFileInputStream = new FileInputStream( path );
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024]; 
            int bytesRead = 0;
            while ((bytesRead = mFileInputStream.read(b)) != -1) {
               bos.write(b, 0, bytesRead);
            }
            byte[] ba = bos.toByteArray();
            encodedImage = Base64.encodeToString(ba, Base64.DEFAULT);
		} catch ( Exception e ){
			encodedImage = "ERRO: " + e.toString();
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			double sdAvailSize = (double)stat.getAvailableBlocks() * (double)stat.getBlockSize();
			if( sdAvailSize < 300000){
				encodedImage = "ERRO: Sem espaço no cartão de memória";
			}
		}
		if(encodedImage.length() > 600000){
			encodedImage = "ERRO: Resolução/Qualidade da imagem não suportada\n\nAltere a qualidade para Normal e o tamanho para 640x480(0.3MP)";
		}
		if( encodedImage.equals("") ){
			encodedImage = "ERRO: Problema na geração da fotografia";
		}
		return encodedImage;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Voltar para Inicio");
		menu.add(0, 2, 0, "Sobre");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 1:
				myWebView.loadUrl( php_endereco );
				return true;
			case 2:
				String _msg = "Este aplicativo é de propriedade única e exclusiva \nDecaedro Studio\n\n Versão: " + strVersionName;

				new AlertDialog.Builder( myWebView.getContext() ).setMessage( _msg ).setCancelable(true).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}	
	@Override
	public void start(){
		super.start();
		setContentView(R.layout.main);
		myWebView = DKWebView.create( (Activity) this, this, (WebView) findViewById(R.id.webView1), userAgent, true );
        myWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
		myWebView.loadUrl( php_endereco );  
    }
	public class JavaScriptInterface {
		Context mContext;
		JavaScriptInterface(Context c) { mContext = c; }
		public void loading_show() { dialog.show(); }
		public void loading_hide() { dialog.dismiss(); }
		public void closeMyActivity() { finish(); }
		public void js_showMsg(String msg){ showMsg(msg); }
		public void openMenu() { openOptionsMenu(); }
		public String js_NOMEUSUARIO(){ return NOME_USUARIO; }
		public String js_TOKEN(){ return TOKEN; }
		public String js_URLWEBSERVICE(){ return URL_WEBSERVICE; }
		public String getWebService(){ return URL_WEBSERVICE; }
		public String getIMEI(){ return findDeviceID(); }
		public String getPackage(){ return strPackage; }
		public String getVersionName(){ return strVersionName; }
		public String js_getBase64(String path){ return getBase64(path); }
		public void js_tiraFoto(String filename, String callback, String _newExif){  
			newExif = _newExif;
			Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
			File chkSDfolder;
			String folder = "/mnt/ExtSD/DCIM/";
			chkSDfolder = new File( folder );
			if (!chkSDfolder.exists()) {
				folder = "/storage/extSdCard/DCIM/";
				chkSDfolder = new File( folder );
			}
			if (!chkSDfolder.exists()) {
				folder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/";
				chkSDfolder = new File( folder );
			}
			folder = folder + "/Decaedro";
			File chkfolder = new File( folder );
			if (!chkfolder.exists()) {
				chkfolder.mkdir();
			}
			filename = filename +".jpg";
			File photo = new File(folder, filename);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
			picUri = Uri.fromFile(photo);
			MainActivity.this.startActivityForResult(cameraIntent, MainActivity.TAKE_B64_PICTURE);    
		}
		public void js_OpenOptions(String _title, String _options, String _values, String _checkeds, String _callback ){
			loading_hide();
			final String retCallback = _callback;
			final String retOptions = _options;
			final String retValues = _values;
			CharSequence options[] = _options.split(",");	
			CharSequence values[] = _values.split(",");
			CharSequence base_checkeds[] = _checkeds.split(",");
			final boolean checkeds[] = new boolean[base_checkeds.length];
			for (int i = 0; i < base_checkeds.length; i++) {		
				checkeds[i] = base_checkeds[i].equals("true");
			}
			new AlertDialog.Builder( mContext )
				.setTitle( _title )
				.setMultiChoiceItems(
						options,
						checkeds,
						new DialogInterface.OnMultiChoiceClickListener() {
							public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
								checkeds[whichButton] = isChecked;
							}
						})
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String retCheckeds = "";
						for (int i = 0; i < checkeds.length; i++) {		
							if(retCheckeds != ""){
								retCheckeds += ",";
							}
							retCheckeds += new Boolean(checkeds[i]).toString();
						}
						myWebView.loadUrl("javascript:"+ retCallback +"('"+ retValues +"','"+ retCheckeds +"')");					
					}
				})
				.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//showMsg("Cancelado pelo Usuário");
					}
				})
				.show();
		}
	}
}