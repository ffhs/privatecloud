package ch.ffhs.privatecloudffhs.connection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ch.ffhs.privatecloudffhs.database.Server;

import com.jcraft.jsch.*;

import android.os.AsyncTask;
import android.util.Log;

public class SshCertConnection extends SshConnection {
	private Server server;


	public SshCertConnection(Server server) {
		super();
		
		this.server = server;
		
		new LongOperation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
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
			connect();			
			return "Executed";
		}

		private void connect()  {
			Channel channel = null; 
			
			try {					
				JSch jsch = new JSch(); 
								
			    String rsakeypath =    server.getCertpath();
			    
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
			
				session = jsch.getSession(server.getUsername(),server.getHostname(),server.getPort()); 
				session.setConfig(config);
				session.connect();

				channel = session.openChannel("sftp"); 
				channel.connect(); 
								
				channelSftp = (ChannelSftp)channel; 

				remoteDir = server.getRemoteroot();
				mkDir(remoteDir);
				channelSftp.cd(remoteDir); 			
				
		    	connected = true;
					
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}