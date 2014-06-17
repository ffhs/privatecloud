package ch.ffhs.privatecloudffhs.connection;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.Server;

import com.jcraft.jsch.*;

import android.os.AsyncTask;
import android.util.Log;

public class SshPwConnection extends SshConnection{	
	public SshPwConnection(Server server, Folder folder) {
		super(server,folder);		
		new LongOperation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
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
				
				channelSftp = (ChannelSftp)channel; 
				
				remoteDir = server.getRemoteroot();
				checkremotedir();
				channelSftp.cd(remoteDir); 			
				
		    	connected = true;
			}
			catch(Exception ex){
				ex.printStackTrace(); 
			}  
		}
	}

}