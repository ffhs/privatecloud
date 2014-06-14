package ch.ffhs.privatecloudffhs.connection;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import ch.ffhs.privatecloudffhs.database.SyncFile;
import ch.ffhs.privatecloudffhs.sync.SyncConnection;

import com.jcraft.jsch.*;

import android.os.AsyncTask;
import android.util.Log;

public class SshPwConnection extends SshConnection{
	private Server server;
	
	public SshPwConnection(Server server) {
		super();
		
		this.server = server;
		
		new LongOperation().execute("");
	}
	
	
	private class LongOperation extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... params) {
			connect();			
			return "Executed";
		}

		private void connect()  {
			Channel channel = null; 
			
			try{ 
				JSch jsch = new JSch(); 
				session = jsch.getSession(server.getUsername(),server.getHostname(),server.getPort()); 
				session.setPassword(server.getPassword()); 
				
				java.util.Properties config = new java.util.Properties(); 
				config.put("StrictHostKeyChecking", "no"); 
				
				session.setConfig(config); 
				session.connect(); 
				
				channel = session.openChannel("sftp"); 
				channel.connect(); 
				
				remoteDir = server.getRemoteroot();
				
				Log.d("SYNC CONNECT", "CONNECTION READY");
				channelSftp = (ChannelSftp)channel; 
				channelSftp.cd(remoteDir); 			
				
		    	connectionReady = true;
			}
			catch(Exception ex){
				ex.printStackTrace(); 
			}  
		}
	}

}