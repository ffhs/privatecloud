package ch.ffhs.privatecloudffhs.sync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp.LsEntry;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import ch.ffhs.privatecloudffhs.database.SyncFile;

public class SyncClient extends AsyncTask<String, Void, String>   {

	private Folder folder;
	private Server server;
	private Boolean running;
	private PrivateCloudDatabase db;
	private SyncConnection syncConnectionObj;

	public SyncClient(Context context, Folder folder, SyncConnection syncConnectionObj, Server server) {
		super();
		this.folder = folder;
		this.syncConnectionObj = syncConnectionObj;
		this.server = server;
		
		db = new PrivateCloudDatabase(context);
	}
	
	
	private void sync()
	{
		syncLocalDirectory(new File(folder.getPath()));
		
		syncRemoteFile(server.getRemoteroot() + folder.getPath());
		
		folder.setLastsync("10-06-2014");
		db.updateFolder(folder);
		
		db.close();
	}
	
	private void setRunning(Boolean running)
	{
		this.running = running;
	}
	
	public boolean isRunning()
	{
		return running;
	}


	private void syncLocalDirectory(File dir) {
		File[] localFiles = dir.listFiles();
   		
		syncConnectionObj.mkDir(server.getRemoteroot() + dir.getPath());	
		
		Log.d("jada","Dir"+dir.getAbsolutePath());
		Log.d("jada","files:"+localFiles);
	   for (File file : localFiles) {
		   	if(file.isDirectory())
			{
		   		syncLocalDirectory(file);
			}
			else{
		   		syncConnectionObj.initFolderSync(server.getRemoteroot() + dir.getPath());			

				SyncFile cachedFile = db.getFile(file.getPath(), folder.getId());
				
				String localCheckSum = getLocalCheckSum(file.getPath());
				String remoteCheckSum = getRemoteCheckSum(file.getPath());

				// ****************  DELETE NUR ZUM TESTEN ****************
				if(file.getPath().contains("txt"))
				{
					//Read text from file
					StringBuilder text = new StringBuilder();

					try {
					    BufferedReader br = new BufferedReader(new FileReader(file));
					    String line;

					    while ((line = br.readLine()) != null) {
					        text.append(line);
					        text.append('\n');
					    }
					}
					catch (IOException e) {
					    //You'll need to add proper error handling here
					}
	//				Log.d("SYNC FILE DATA222", text.toString());
	
				}
		
				// first run
				if(cachedFile == null)
				{
					cachedFile = new SyncFile(folder.getId(), file.getPath());
					cachedFile = uploadFile(file, cachedFile);
					
					db.createFile(cachedFile);
					Log.d("SYNC SSHPW", "NEW FILE SYNC ...");
				}
				else
				{
					if(!cachedFile.isConflict())
					{
						if(cachedFile.getRemoteCheckSum() == null || remoteCheckSum.isEmpty())
						{
							Log.d("SYNC SSHPW", "REMOTE NULL UPLOAD...");

							cachedFile = uploadFile(file, cachedFile);
						}
						else if(cachedFile.getLocalCheckSum() == null)
						{
							Log.d("SYNC SSHPW", "LOCAL NULL DOWNLOAD...");

							cachedFile = downloadFile(file, cachedFile);
						}
						// check if remote has been changed since last sync
						else if(cachedFile.getRemoteCheckSum().equals(remoteCheckSum))
						{
							if(!cachedFile.getLocalCheckSum().equals(localCheckSum))
							{			
								Log.d("SYNC SSHPW", "LOCAL CHANGED UPLOAD...");

								cachedFile = uploadFile(file, cachedFile);
							}					
						}
						// file has been changed on remote srv
						else 
						{
							// check if file has been changed on the mobile device
							if(cachedFile.getLocalCheckSum().equals(localCheckSum))
							{
								cachedFile = downloadFile(file, cachedFile);
								Log.d("SYNC SSHPW", "FILE DOWNLOADED...");
							}
							else
							{
								// !conflict! file has been changed on remote srv and local device
								cachedFile.setConflict(true);
								Log.d("SYNC SSHPW", "FILE CHANGED REMOTE AND LOCAL...");
							}
						}					
						
						db.updateFile(cachedFile);
					}
					else
					{
						Log.d("SYNC SSHPW", "CONFILCT FILE");
					}
				}
			}			
		}
	   //this.cancel(isRunning());
	   //this.cancel(isCancelled());
	   
	}
	
	private void syncRemoteFile(String path)
	{
		Vector<LsEntry> remoteList = syncConnectionObj.listRemoteDir(path);
		Log.e("SYNC CLIENT REMOTE PATH", path);
		
		for(LsEntry remoteObj : remoteList)
		{
			String fileName = remoteObj.getFilename();
			Log.d("jada","Processing remote File:"+fileName);
			if(!fileName.equals(".") && !fileName.equals(".."))
			{
				if(remoteObj.getLongname().substring(0,1).equals("d"))
				{
					syncRemoteFile(path + "/" + remoteObj.getFilename());
				}
				else
				{
					String localPath = path.replace(server.getRemoteroot() + "/", "") + "/" + fileName;
							
					if(db.getFile(localPath, folder.getId()) == null)
					{
						Log.d("jada","Remote file not held locally, Localpath:"+localPath);
						File file = new File(localPath);
						
						SyncFile syncFile = new SyncFile(folder.getId(), localPath);
						syncFile = downloadFile(file, syncFile);
						
						db.createFile(syncFile);
					}
				}
			}
		}
	}

	private SyncFile uploadFile(File file, SyncFile syncFile)
	{
		syncFile = syncConnectionObj.uploadFile(file, syncFile);
		syncFile.setLocalCheckSum(getLocalCheckSum(file.getPath())); 	

		return syncFile;
	}
	
	
	private SyncFile downloadFile(File file, SyncFile syncFile)
	{
		syncFile = syncConnectionObj.downloadFile(file, syncFile);
		syncFile.setLocalCheckSum(getLocalCheckSum(file.getPath())); 	

		return syncFile;
	}
	
	
	private String getLocalCheckSum(String filePath) {
	    InputStream inputStream = null;
	    try {
	        inputStream = new FileInputStream(filePath);
	        byte[] buffer = new byte[1024];
	        MessageDigest digest = MessageDigest.getInstance("MD5");
	        int numRead = 0;
	        while (numRead != -1) {
	            numRead = inputStream.read(buffer);
	            if (numRead > 0)
	                digest.update(buffer, 0, numRead);
	        }
	        
	        byte [] md5Bytes = digest.digest();
	        String returnVal = "";
	        
	        for (int i = 0; i < md5Bytes.length; i++) {
	            returnVal += Integer.toString(( md5Bytes[i] & 0xff ) + 0x100, 16).substring(1);
	        }
	        
	        return returnVal;
	    } catch (Exception e) {
	        return null;
	    } finally {
	        if (inputStream != null) {
	            try {
	                inputStream.close();
	            } catch (Exception e) { }
	        }
	    }
	}
	
	
	private String getRemoteCheckSum(String filePath)
	{
		return syncConnectionObj.getRemoteCheckSum(filePath);
	}


	@Override
	protected String doInBackground(String... params) {
		// wait until connection is ready
		for (int i = 0; i < 20; i++) {
			if(!syncConnectionObj.isConnected())
			{
				Log.d("SYNC CLIENT", "SYNC SLEEP");

				try {
					Thread.sleep(1000);
				} catch(InterruptedException e) {
				}
			}
			else
			{
				break;
			}
		}
		
		if(syncConnectionObj.isConnected()) {
			Log.d("SYNC CLIENT", "SYNC START DO IN BACKGROUND");

			sync();				
		}
		
		return "Executed";
	}
	

	protected void onPreExecute() {
		setRunning(true);
	}
	
	protected void onPostExecute(String result) {
		setRunning(false);
	}
}



