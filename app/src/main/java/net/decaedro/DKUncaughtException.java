package net.decaedro;

import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.os.Environment;

public class DKUncaughtException implements UncaughtExceptionHandler {
    private UncaughtExceptionHandler defaultUEH;
    private String localPath;
	private String packageName;
	public DKUncaughtException(String _packageName) {
		packageName = _packageName;
        this.localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Decaedro";
		File chkfolder = new File( this.localPath );
		if (!chkfolder.exists()) {
			chkfolder.mkdir();
		}
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}
    public void uncaughtException(Thread t, Throwable e) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		String filename =  sdf.format(cal.getTime()) +".stacktrace";
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        writeToFile(stacktrace, filename);
        defaultUEH.uncaughtException(t, e);
    }
    private void writeToFile(String stacktrace, String filename) {
		try {
			File myFile = new File(this.localPath +"/"+ packageName +"_"+ filename);
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
			myOutWriter.append( stacktrace );
			myOutWriter.close();
			fOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}