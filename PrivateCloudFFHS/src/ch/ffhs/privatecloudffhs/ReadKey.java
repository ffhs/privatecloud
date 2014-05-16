package ch.ffhs.privatecloudffhs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;



public class ReadKey {
	Context context;
	public ReadKey(Context context) {
		super();
		this.context = context;
	}
	public void ReadFile(){
		StringBuilder text = new StringBuilder();

			String appRootDir = context.getApplicationInfo().dataDir;
		    String rsakeypath = appRootDir + "/id_rsa.pub";
			InputStream instream;
			try {
				instream = new FileInputStream(rsakeypath);
				if (instream != null) {
				  // prepare the file for reading
				  InputStreamReader inputreader = new InputStreamReader(instream);
				  BufferedReader buffreader = new BufferedReader(inputreader);

				  String line;

					  // read every line of the file into the line-variable, on line at the time
					  
				     try {
				    	 do {
						     line = buffreader.readLine();
						     text.append(line);
						     text.append('\n');
						    // do something with the line 
						  } while (line != null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    // do something with the line 
	
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new AlertDialog.Builder(context)
		    .setTitle("RSA Key")
		    .setMessage(text)
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // continue with delete
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // do nothing
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		     .show();
			

		
	}
	
        //result = new String(buffer);
}
