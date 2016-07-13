package net.decaedro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.webkit.*;
import android.widget.Toast;

public class DKWebView {
	public static Activity activity;
	public static WebView create(Activity _activity, Context context, WebView wb, String UserAgent, boolean quitOnError){
		WebView myWebView = wb;
		activity = _activity;
		myWebView.setWebChromeClient((WebChromeClient) new KWebChromeClient( activity, context ));
		myWebView.getSettings().setLoadsImagesAutomatically(true);
		myWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
		myWebView.getSettings().setSupportZoom(false);
		myWebView.getSettings().setUserAgentString( myWebView.getSettings().getUserAgentString() + " ("+ UserAgent +")" );
		myWebView.getSettings().setAllowFileAccess(true);
		myWebView.getSettings().setAllowContentAccess(true);
		myWebView.getSettings().setAppCacheEnabled(true);
		myWebView.getSettings().setAppCachePath("/data/data/"+ context.getPackageName() +"/cache/");
		myWebView.getSettings().setAppCacheMaxSize(5*1024*1024);
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.getSettings().setDatabaseEnabled(true);
		myWebView.getSettings().setDatabasePath("/data/data/"+ context.getPackageName() +"/databases/");
		myWebView.getSettings().setDomStorageEnabled(true);
		myWebView.getSettings().setLoadWithOverviewMode(true);
		myWebView.getSettings().setUseWideViewPort(false);
		myWebView.setWebViewClient((WebViewClient) new DKWebViewClient( activity, context, quitOnError ));
		//myWebView.addJavascriptInterface(new JavaScriptInterface( context ), "Android");
		return myWebView;
	}

	//-------------- CLASSE DKWebViewClient - INICIO --------------//
	public static class DKWebViewClient extends WebViewClient {
		Activity mAtc;
		Context mCtx;
		boolean qError;
		DKWebViewClient(Activity act, Context ctx, boolean quitOE){
			qError = quitOE;
			mAtc = act;
			mCtx = ctx;
		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			//Toast.makeText(  view.getContext() , url ,  Toast.LENGTH_LONG).show();
			return false;		 
		}
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			view.clearView();
			view.loadUrl("about:blank");
			if(qError){
				Toast.makeText(  view.getContext() , "Internet indisponível",  Toast.LENGTH_LONG).show();
				mAtc.finish();
			}else{
				Toast.makeText(  view.getContext() , description ,  Toast.LENGTH_LONG).show();			
			}
		}
	}
	//-------------- CLASSE DKWebViewClient - FIM --------------//

	//-------------- CLASSE KWebChromeClient - INICIO --------------//
	public static class KWebChromeClient extends WebChromeClient {
		Activity mAtc;
		Context mCtx;
		KWebChromeClient(Activity act, Context ctx){
			mAtc = act;
			mCtx = ctx;
		}
		@Override
		public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
			quotaUpdater.updateQuota(estimatedSize * 4);
		}
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			((DKActivity) activity).dialog.dismiss();
			((DKActivity) activity).pdialog.dismiss();
			new AlertDialog.Builder(view.getContext()).setMessage(message).setCancelable(true).show();
			result.confirm();
			return true;
		}
		@Override
		public void onConsoleMessage(String message, int lineNumber, String sourceID) {
			((DKActivity) activity).dialog.dismiss();
			((DKActivity) activity).pdialog.dismiss();
			String finalmessage = "invoked: onConsoleMessage() - " + sourceID + ":" + lineNumber + " - " + message;
			Toast.makeText( mCtx , finalmessage ,  Toast.LENGTH_LONG).show();
			super.onConsoleMessage(message, lineNumber, sourceID);
		}
		@Override
		public boolean onConsoleMessage(ConsoleMessage cm) {
			String finalmessage = cm.message() + " \n\nFrom line " + cm.lineNumber() + " \nof " + cm.sourceId();
			Toast.makeText( mCtx , finalmessage ,  Toast.LENGTH_LONG).show();
			return true;
		}
	}
	//-------------- CLASSE KWebChromeClient - FIM --------------//
}