package ch.ffhs.privatecloudffhs.connection;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.ffhs.privatecloudffhs.database.SyncFile;
import ch.ffhs.privatecloudffhs.sync.SyncConnection;

import com.jcraft.jsch.*;

import android.util.Log;

public class SshConnection implements SyncConnection{
	
	protected ChannelSftp channelSftp = null; 
	protected Session session =  null;
	protected String remoteDir;
	
	protected Boolean connectionReady = false;
	
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
			channelSftp.mkdir(directory);

		} catch (SftpException e) {
			// FOLDER exists
			// TODO Auto-generated catch block
		//	e.printStackTrace();
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
		Log.d("SYNC INIT FOLDER", directory);
		try {
			channelSftp.cd(directory);
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
	}
}



