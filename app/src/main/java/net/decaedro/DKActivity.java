package net.decaedro;
 
import android.app.*;
import android.accounts.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.location.*;
import android.webkit.*;
import android.content.*;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.provider.Settings;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageInfo;

import java.lang.Exception;
import java.lang.reflect.Field;
import java.io.*;

public class DKActivity extends Activity
{
	public WebView myWebView;
	public String WebviewJSStr;
	protected String LOG_TAG = "MainActivity";
	private String userAgent = "10edroTie";

	protected boolean dialogCreated = false;
	
	public ProgressDialog dialog;
	public ProgressDialog pdialog;
    protected LocationManager locationManager;
	protected AccountManager mAccountManager;
	protected String ACCOUNT_TYPE = "net.decaedro.auth_example";
	protected String TOKEN = "";
	protected String NOME_USUARIO = "";
	protected String URL_WEBSERVICE = "";
	protected String strPackage = "";
	protected String strVersionName = "";
	protected String strApk = "";
	protected String newExif = "";
	protected String querystring = "({\"\"})";

	protected ValueCallback<Uri> mUploadMessage;  
	protected final static int FILECHOOSER_RESULTCODE = 1; 
	protected final static int TAKE_PICTURE = 1; 
	protected final static int TAKE_B64_PICTURE = 2; 

	protected Uri picUri;
	protected Bundle bundle;

	public boolean onCreateOptionsMenu(Menu menu) {
		if( dialogCreated ){
			dialog.dismiss();
			pdialog.dismiss();
		}
		menu.add(0, 0, 0, "Sair");
		return super.onCreateOptionsMenu(menu);
	}
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	public void showMsg( String msg){
		if( dialogCreated ){
			dialog.dismiss();
			pdialog.dismiss();
		}
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	private void checkAccount(){
		mAccountManager = AccountManager.get(this);
		final Account availableAccounts[] = mAccountManager.getAccountsByType(ACCOUNT_TYPE);
		if (availableAccounts.length == 0) {
			showMsg( "Error: Nenhum usuário cadastrado." );
			abreAutentica();
        } else {
			if(availableAccounts.length == 1){
				getExistingAccountAuthToken(availableAccounts[0], "Full access");
			}else{
				String name[] = new String[availableAccounts.length];
				for (int i = 0; i < availableAccounts.length; i++) {
					name[i] = availableAccounts[i].name;
				}
				new AlertDialog.Builder(this).setTitle("Usuários Cadastrados:").setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getExistingAccountAuthToken(availableAccounts[which], "Full access");
					}
				}).show();	
			}
        }
    }
	private void getExistingAccountAuthToken(Account account, String authTokenType) {
		TOKEN = mAccountManager.getUserData(account, "TOKEN" );
		NOME_USUARIO = mAccountManager.getUserData(account, "NOME_USUARIO" );
		URL_WEBSERVICE = mAccountManager.getUserData(account, "URL_WEBSERVICE" );
		if(TOKEN != ""){
			start();
		}
	}
	public void abreAutentica(){
		PackageManager pm = getPackageManager();
		String packageName = "net.decaedro.autentica";
		try {
			PackageInfo info=pm.getPackageInfo(packageName,PackageManager.GET_META_DATA);
			Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			intent.putExtra(Settings.EXTRA_AUTHORITIES, new String[] { packageName });
			startActivity(intent);
		} catch (NameNotFoundException e) {
			showMsg( "Error: O Decaedro Autentica não está instalado." );
			finish();
		}
	}

	public String findDeviceID() {
		String serviceName = Context.TELEPHONY_SERVICE;
		TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(serviceName);
		String deviceID = m_telephonyManager.getDeviceId();
		return deviceID;
	}


 @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new DKUncaughtException( getPackageName() ));
		try {
			checkAccount();
		} catch (Exception ex) {
			showMsg( "Error: " + ex.getMessage() );
		}
		start();
	}
	public void copiaAjax(){
		try {
			for (String fileName : getAssets().list("ajax")) {
				File outputFile = new File(getFilesDir().getPath() + "/" + fileName);
				FileOutputStream out = new FileOutputStream(outputFile);
				InputStream in = getAssets().open( "ajax/" + fileName );
				copy(in,out);
				out.close();
				in.close();
			}
		} catch (Exception e) {
			showMsg(e.toString());
		}	
	}
	public void start(){
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Erro com versão antiga de android!
			//showMsg(ex.toString());
		}
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			strPackage = getPackageName();
			strVersionName = pinfo.versionName;
		} catch (NameNotFoundException e) {
			//showMsg(e.toString());
		}
		createDialogs();
		//dialog.show();
		copiaAjax();
    }
	public void createDialogs(){
		dialog = new ProgressDialog( this );
		dialog.setTitle("");
		dialog.setIndeterminate(false);
		dialog.setCancelable(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setMessage("Carregando...\nPor favor, aguarde...");
		
		pdialog = new ProgressDialog( this );
		pdialog.setTitle("");
		pdialog.setIndeterminate(false);
		pdialog.setCancelable(false);
		pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pdialog.setMax(100);
		pdialog.setMessage("Sincronizando...");
		pdialog.setProgress(0);	

		dialogCreated = true;
	}
 @Override
 public boolean onKeyDown(int keyCode, KeyEvent event) {
    if( keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot() ) {
        doQuit();
        return true;
    } else {
		 if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
			 myWebView.goBack();
			 return true;
		 }
        return super.onKeyDown(keyCode, event);
    }
 }
 	public void doQuit(){
		new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("")
        .setMessage("Deseja sair da aplicação?")
        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
				finish();
            }
        })
        .setNegativeButton("Não", null)
        .show();
	}

	private static final int IO_BUFFER_SIZE = 8 * 1024;

	private static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[IO_BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}
}