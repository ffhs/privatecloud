package ch.ffhs.privatecloudffhs;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.*;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class SshConnection {
Context context;
	public SshConnection(Context context) {
	super();
	this.context = context;
}
	public void Connect()  {
		/*
		JSch jsch=new JSch();
		
		try {
			String appRootDir = context.getApplicationInfo().dataDir;
		    String rsakeypath = appRootDir + "/id_rsa";
		    final byte[] privateKey = getPrivateKeyAsByteStream(rsakeypath);
		    final byte[] emptyPassPhrase = new byte[0];
			jsch.addIdentity(
		            "syncuser01",    // String userName
		            privateKey,          // byte[] privateKey 
		            null,            // byte[] publicKey
		            emptyPassPhrase  // byte[] passPhrase
		        );
			Session session=jsch.getSession("syncuser01", "ffhs.p45q.net", 22);
			session.connect();
			
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		new LongOperation().execute("");
		}
	private static byte[] getPrivateKeyAsByteStream(String pathk) {  
	    // TODO Auto-generated method stub  
	    final File privateKeyLocation = new File(pathk);  
	    InputStream is = null;  
	    try {  
	        is = new FileInputStream(privateKeyLocation);  
	    } catch (FileNotFoundException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    }  
	    long length = privateKeyLocation.length();  
	    if (length > Integer.MAX_VALUE) {  
	        try {  
	            throw new IOException(  
	                    "File to process is too big to process in this example.");  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    final byte[] bytes = new byte[(int) length];  
	  
	    // Read in the bytes  
	    int offset = 0;  
	    int numRead = 0;  
	    try {  
	        while ((offset < bytes.length)  
	                && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {  
	  
	            offset += numRead;  
	  
	        }  
	    } catch (IOException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    }  
	  
	    // Ensure all the bytes have been read in  
	    if (offset < bytes.length) {  
	        try {  
	            throw new IOException("Could not completely read file "  
	                    + privateKeyLocation.getName());  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    try {  
	        is.close();  
	    } catch (IOException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    }  
	    return bytes;  
	  
	}
	private class LongOperation extends AsyncTask<String, Void, String> {
		

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
						JSch jsch=new JSch();
						
						try {
							String appRootDir = context.getApplicationInfo().dataDir;
						    String rsakeypath = appRootDir + "/id_rsa";
						    final byte[] privateKey = getPrivateKeyAsByteStream(rsakeypath);
						    final byte[] emptyPassPhrase = new byte[0];
							jsch.addIdentity(
						            "syncuser01",    // String userName
						            privateKey,          // byte[] privateKey 
						            null,            // byte[] publicKey
						            emptyPassPhrase  // byte[] passPhrase
						        );
							java.util.Properties config = new java.util.Properties(); 
							config.put("StrictHostKeyChecking", "no");
							
							Session session=jsch.getSession("syncuser01", "ffhs.p45q.net", 22);
							session.setConfig(config);
							session.connect();
						} catch (JSchException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return "jada";
			
		} 
	}
	
}

