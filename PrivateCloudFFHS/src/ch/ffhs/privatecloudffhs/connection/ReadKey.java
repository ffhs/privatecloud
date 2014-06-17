package ch.ffhs.privatecloudffhs.connection;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ffhs.privatecloudffhs.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;



public class ReadKey {
	Context context;
	String goodkey;
	public ReadKey(Context context) {
		super();
		this.context = context;
	}
	public void ReadFile(String keypath){
		StringBuilder text = new StringBuilder();
		    String rsakeypath = keypath+".pub";
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
			goodkey = text.toString();
			new AlertDialog.Builder(context)
		    .setTitle("RSA Key")
		    .setMessage(text)
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        @Override
				public void onClick(DialogInterface dialog, int which) { 
		            // continue with delete
		        }
		     })
		    .setNegativeButton(R.string.server_key_sendmail, new DialogInterface.OnClickListener() {
		        @Override
				public void onClick(DialogInterface dialog, int which) { 
		        	Intent intent = new Intent(Intent.ACTION_SEND);
		        	intent.setType("text/html");
		        	intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
		        	intent.putExtra(Intent.EXTRA_SUBJECT, "RSA Public key");
		        	intent.putExtra(Intent.EXTRA_TEXT, goodkey);
		        	context.startActivity(Intent.createChooser(intent, "Send Email"));
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		     .show();
			

		
	}
	
        //result = new String(buffer);
}
