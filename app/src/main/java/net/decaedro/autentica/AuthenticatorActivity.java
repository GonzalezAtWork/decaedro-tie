package net.decaedro.autentica;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.telephony.TelephonyManager;

import net.decaedro.tie.R;

import java.lang.Exception;

import static net.decaedro.autentica.AccountGeneral.sServerAuthenticate;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private final int REQ_SIGNUP = 1;
    public Context context;

    private final String TAG = this.getClass().getSimpleName();

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
		context = getBaseContext();
        mAccountManager = AccountManager.get(context);

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        if (accountName != null) {
            ((TextView)findViewById(R.id.accountName)).setText(accountName);
        }

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
/*
        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent signup = new Intent(getBaseContext(), SignUpActivity.class);
                signup.putExtras(getIntent().getExtras());
                startActivityForResult(signup, REQ_SIGNUP);
            }
        });
		*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }
    public String findDeviceID() {
        String deviceID = "";
        try{
            String serviceName = context.TELEPHONY_SERVICE;
            TelephonyManager m_telephonyManager = (TelephonyManager) getSystemService(serviceName);
            deviceID =			m_telephonyManager.getDeviceId();
        }catch(Exception e){
            Toast.makeText( context , "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return deviceID;
    }
    public void submit() {
		Toast.makeText( context , "Autenticando...", Toast.LENGTH_SHORT).show();
		findViewById(R.id.submit).setClickable(false);
		findViewById(R.id.submit).setEnabled(false);

		final String userDevice = findDeviceID();
		final String userName = ((TextView) findViewById(R.id.accountName)).getText().toString();
        final String userPass = ((TextView) findViewById(R.id.accountPassword)).getText().toString();

        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {

                User loggedUser = null;
                Bundle data = new Bundle();
                try {
                    loggedUser = sServerAuthenticate.userSignIn(userDevice, userName, userPass, mAuthTokenType);
                    data.putString(AccountManager.KEY_ACCOUNT_NAME, loggedUser.username);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, loggedUser.sessionToken);
                    data.putString(PARAM_USER_PASS, userPass);

					data.putString("TOKEN", loggedUser.sessionToken);
					data.putString("URL_WEBSERVICE", loggedUser.url_webservice);
					data.putString("NOME_USUARIO", loggedUser.nome_usuario);
					data.putString("NOME_PERFIL", loggedUser.nome_perfil);

                } catch (Exception e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage()); 
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
					String msg = "";
					msg += intent.getStringExtra(KEY_ERROR_MESSAGE);
					if( msg.equals("") || msg.equals("null") ){
						msg = "Problema na autenticação.";
					}
                    Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
					findViewById(R.id.submit).setClickable(true);
					findViewById(R.id.submit).setEnabled(true);
                } else {
                    finishLogin(intent);
                }
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            mAccountManager.addAccountExplicitly(account, accountPassword, null);

			mAccountManager.setUserData(account, "TOKEN", intent.getStringExtra("TOKEN") );
			mAccountManager.setUserData(account, "URL_WEBSERVICE", intent.getStringExtra("URL_WEBSERVICE") );
			mAccountManager.setUserData(account, "NOME_USUARIO", intent.getStringExtra("NOME_USUARIO") );
			mAccountManager.setUserData(account, "NOME_PERFIL", intent.getStringExtra("NOME_PERFIL") );
			
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

}
