package ch.ffhs.privatecloudffhs.connection;
import ch.ffhs.privatecloudffhs.database.Server;
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
		@Override
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
				Log.d("SYNC CONNECT", remoteDir);

				Log.d("SYNC CONNECT", "CONNECTION READY");
				channelSftp = (ChannelSftp)channel; 
				
				mkDir(remoteDir);
				channelSftp.cd(remoteDir); 			
				
		    	connectionReady = true;
			}
			catch(Exception ex){
				ex.printStackTrace(); 
			}  
		}
	}

}