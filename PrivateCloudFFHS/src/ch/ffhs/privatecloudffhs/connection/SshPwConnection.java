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

public class SshPwConnection implements SyncConnection{
	private Server server;
	
	private ChannelSftp channelSftp = null; 
	private Session session =  null;
	private String remoteDir;
	
	private Boolean connectionReady = false;
	
	public SshPwConnection(Server server) {
		this.server = server;
		
		new LongOperation().execute("");
	}
	
	public Boolean isReady()
	{
		return connectionReady;
	}

	private String sendCommand(String command)
	  {
	     StringBuilder outputBuffer = new StringBuilder();

	     try
	     {
	        Channel channel = session.openChannel("exec");
	        ((ChannelExec)channel).setCommand(command);
	        channel.connect();
	        InputStream commandOutput = channel.getInputStream();
	        int readByte = commandOutput.read();

	        while(readByte != 0xffffffff)
	        {
	           outputBuffer.append((char)readByte);
	           readByte = commandOutput.read();
	        }

	        channel.disconnect();
	     }
	     catch(IOException ioX)
	     {
	        Log.d("ERROR SYNCTEST2", ioX.getMessage());
	        return null;
	     }
	     catch(JSchException jschX)
	     {
	    	 Log.d("ERROR SYNCTEST1", jschX.getMessage());
	        return null;
	     }

	     return outputBuffer.toString();
	 }
	
	
	public SyncFile uploadFile(File file, SyncFile syncFile)
	{
		try {
			channelSftp.put(new FileInputStream(file), file.getName()); 

			// get remote checksum from new uploaded file
			syncFile.setRemoteCheckSum(getRemoteCheckSum(file.getPath()));
			
			return syncFile;
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("SYNC", "ERROR UPLOAD");
		}
		
		return null;
	}
	
	
	public SyncFile downloadFile(File file, SyncFile syncFile)
	{
		try { 
			byte[] buffer = new byte[1024]; 
			BufferedInputStream bis = new BufferedInputStream(channelSftp.get(remoteDir + file.getPath())); 
			File newFile = new File(file.getPath()); 
			
			OutputStream os = new FileOutputStream(newFile); 
			BufferedOutputStream bos = new BufferedOutputStream(os); 
			int readCount; 
			//System.out.println("Getting: " + theLine); 
			while( (readCount = bis.read(buffer)) > 0) { 
				bos.write(buffer, 0, readCount); 
			} 
		
			bis.close(); 
			bos.close(); 
			
			// get remote checksum from downloaded file
			syncFile.setRemoteCheckSum(getRemoteCheckSum(file.getPath()));
			
			return syncFile;
			
		} catch(Exception ex){ 
			ex.printStackTrace(); 
		}
		
		return null;
	}
	
	public void mkDir(String directory)
	{
		try {					
			channelSftp.mkdir(remoteDir + directory);

		} catch (SftpException e) {
			// FOLDER exists
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	
	public String getRemoteCheckSum(String filePath)
	{
		StringBuilder command = new StringBuilder();
		command.append("md5sum").append(" ");
		command.append("\"").append(remoteDir).append(filePath).append("\"");
		
		String result = sendCommand(command.toString());
		
		if(result.length() > 32) return result.substring(0, 32);

		return result;
	}
	
	public void initFolderSync(String directory)
	{
		try {
			channelSftp.cd(remoteDir + directory);
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
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



