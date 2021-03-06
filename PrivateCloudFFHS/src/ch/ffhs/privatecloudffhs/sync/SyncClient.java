package ch.ffhs.privatecloudffhs.sync;

import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Vector;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import ch.ffhs.privatecloudffhs.database.SyncFile;
/**
* SyncClient
*
* Für jeden zu synchronisierenden lokalen Order wird ein SyncClient gestartet. Der SyncClient führt die synchronisation des Ordners durch.
* Die Verbindung wird mit dem syncConnectionObj hergestellt welches am Konstruktor übergeben wird.
*  
* @author         Thierry Baumann, Martin Müller, Pascal Bieri
*/
public class SyncClient extends AsyncTask<String, String, String> {

	private Folder folder;
	private Server server;
	private Boolean running;
	private PrivateCloudDatabase db;
	private SyncConnection syncConnectionObj;
	public static final String ProgressBar_Intent = "ch.ffhs.privatecloudffhs.sync";
	private Context context;
	private int filecount;
	private int maxfiles;
	private int percent;
	private static final String TAG = "SyncClient";
	private boolean syncerror = false;
	public SyncClient(Context context, Folder folder, SyncConnection syncConnectionObj, Server server) {
		super();
		this.folder = folder;
		this.syncConnectionObj = syncConnectionObj;
		this.server = server;
		this.context = context;
		db = new PrivateCloudDatabase(context);
	}

	private void sync() {
		File file = new File(folder.getPath());

		// Reset values for Progressbar before Sync --> syncLocalDirectory() is
		// recursive
		maxfiles = getFilesCount(file);
		percent = 0;
		filecount = 0;
		syncLocalDirectory(file);
		if(!syncerror)
		{
			syncRemoteFile(server.getRemoteroot() + folder.getPath());
		}
		if(syncerror)
		{
			connectionError();
		}else
		{
			folder.setLastsync(new Date());
			db.updateFolder(folder);
			//mark as done
			publishProgress("", "100", "1");
		}
		db.close();
	}

	private void setRunning(Boolean running) {
		this.running = running;
	}

	public boolean isRunning() {
		return running;
	}

	private void syncLocalDirectory(File dir) {
		File[] localFiles = dir.listFiles();
		syncConnectionObj.mkDir(server.getRemoteroot() + dir.getPath());
		Log.d(TAG,"Dir"+dir.getAbsolutePath());
		Log.d(TAG,"files:"+localFiles);
	   for (File file : localFiles) {
		   	if(syncerror){
		   		connectionError();
		   	}
		   	else
		   	{
		   		Log.d(TAG,"Processing File:"+file.getPath());
			   	if(file.isDirectory())
				{
			   		syncLocalDirectory(file);
				}
				else{
					filecount++;
					percent = filecount * 100 / maxfiles;
					Log.e("SyncClient", "Maxfiles:" + maxfiles + " filecount:" + filecount + " result:" + percent);
					publishProgress(file.getAbsolutePath(), Integer.toString(percent));
			   		syncConnectionObj.initFolderSync(server.getRemoteroot() + dir.getPath());			
	
					SyncFile cachedFile = db.getFile(file.getPath(), folder.getId());
					
					String localCheckSum = getLocalCheckSum(file.getPath());
					String remoteCheckSum = getRemoteCheckSum(file.getPath());
	
					// first run
					if(cachedFile == null)
					{
						cachedFile = new SyncFile(folder.getId(), file.getPath());
						cachedFile = uploadFile(file, cachedFile);
						if(cachedFile==null)
						{
							syncerror=true;
						}else
						{
							db.createFile(cachedFile);
							Log.d(TAG, "NEW FILE SYNC ...");
						}
					}
					else
					{
						if(!cachedFile.isConflict())
						{
							if(cachedFile != null && file != null && remoteCheckSum != null) 
							{
								if(cachedFile.getRemoteCheckSum() == null || remoteCheckSum.isEmpty())
								{
									
									Log.d(TAG, "REMOTE NULL UPLOAD...");
									cachedFile = uploadFile(file, cachedFile);
								}
								else if(cachedFile.getLocalCheckSum() == null)
								{
									Log.d(TAG, "LOCAL NULL DOWNLOAD...");
		
									cachedFile = downloadFile(file, cachedFile);
								}
								// check if remote has been changed since last sync
								else if(cachedFile.getRemoteCheckSum().equals(remoteCheckSum))
								{
									if(!cachedFile.getLocalCheckSum().equals(localCheckSum))
									{			
										Log.d(TAG, "LOCAL CHANGED UPLOAD...");
		
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
										Log.d(TAG, "FILE DOWNLOADED...");
									}
									else
									{
										// !conflict! file has been changed on remote srv and local device
										cachedFile.setConflict(true);
										Log.d(TAG, "FILE CHANGED REMOTE AND LOCAL...");
									}
								}					
								if(cachedFile != null)
								{
									db.updateFile(cachedFile);
								}
							}
						}
						else
						{
							Log.d(TAG, "CONFILCT FILE");
						}
					}
				}	
		   	}

	   }



		// this.cancel(isRunning());
		// this.cancel(isCancelled());
	}

	private void syncRemoteFile(String path) {

		Vector<LsEntry> remoteList = syncConnectionObj.listRemoteDir(path);
		Log.e("SYNC CLIENT REMOTE PATH", path);
		filecount = 0;
		percent = 0;

		if(remoteList==null)
		{
			connectionError();
			syncerror = true;	
		}
		else
		{
			maxfiles = remoteList.size();	
			
			for(LsEntry remoteObj : remoteList)
			{
				if(syncerror){
			   		Log.d("syncclient","connectionerror");
			   	}
				else
			  	{
					String fileName = remoteObj.getFilename();
					Log.d("syncclient","Processing remote File:"+fileName);
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
								Log.d(TAG,"Remote file not held locally, Localpath:"+localPath);
								File file = new File(localPath);
								
								SyncFile syncFile = new SyncFile(folder.getId(), localPath);
								syncFile = downloadFile(file, syncFile);
								
								db.createFile(syncFile);
							}
						}


					}
					//updating Progressbar
					filecount++;
					percent = filecount * 100 / maxfiles;
					Log.e("SyncClient", "Maxfiles:" + maxfiles + " filecount:" + filecount + " result:" + percent);
					publishProgress(path, Integer.toString(percent));
				
			   	}
			}
		}
	}

	private SyncFile uploadFile(File file, SyncFile syncFile)
	{
		
		syncFile = syncConnectionObj.uploadFile(file, syncFile);
		if(syncFile == null)
		{
			syncerror = true;
			return null;
		}
		else
		{
			syncFile.setLocalCheckSum(getLocalCheckSum(file.getPath())); 	
			return syncFile;
		}
	}



	private SyncFile downloadFile(File file, SyncFile syncFile) {
		syncFile = syncConnectionObj.downloadFile(file, syncFile);
		try {
			syncFile.setLocalCheckSum(getLocalCheckSum(file.getPath()));
		} catch (Exception e) {
			syncerror = true;
			e.printStackTrace();
		} 	
		return syncFile;
	}
	private void connectionError(){
		publishProgress("Connection error", "0");
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

			byte[] md5Bytes = digest.digest();
			String returnVal = "";

			for (int i = 0; i < md5Bytes.length; i++) {
				returnVal += Integer.toString((md5Bytes[i] & 0xff) + 0x100, 16)
						.substring(1);
			}

			return returnVal;
		} catch (Exception e) {
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}
		}
	}

	
	
	private String getRemoteCheckSum(String filePath)
	{
		String remoteChecksum;
		remoteChecksum = syncConnectionObj.getRemoteCheckSum(filePath);
		if(remoteChecksum==null)
		{
			syncerror = true;
			return "123";
		}
		return remoteChecksum;
	}

	private static int getFilesCount(File file) {
		File[] files = file.listFiles();
		int count = 0;
		for (File f : files)
			if (f.isDirectory())
				count += getFilesCount(f);
			else
				count++;

		return count;
	}

	@Override
	protected String doInBackground(String... params) {
		// wait until connection is ready
		for (int i = 0; i < 20; i++) {
			if (!syncConnectionObj.isConnected()
					&& !syncConnectionObj.isError()) {
				Log.d(TAG, "SYNC SLEEP");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			} else {
				break;
			}
		}

		if (syncConnectionObj.isError()) {
			publishProgress(syncConnectionObj.getErrorMsg(), "0");
		}

		if (syncConnectionObj.isConnected()) {
			Log.d(TAG, "SYNC START DO IN BACKGROUND");
			sync();
		}

		return "Executed";
	}

	protected void onProgressUpdate(String... progress) {
		Intent i = new Intent();
		Bundle extras = new Bundle();
		Log.e("SyncClient", "Building Intent. TEXT:" + progress[0] + " PERCENT:" + progress[1]);

		if (Integer.parseInt(progress[1]) > 99) progress[1] = "99";
		if (progress.length > 2 && progress[2] == "1") progress[1] = "100";

		extras.putString("TEXT", progress[0]);
		extras.putString("PERCENT", progress[1]);
		i.putExtras(extras);
		Log.e("SyncClient", "Intent Extras: " + i.getExtras());

		i.setAction(ProgressBar_Intent);
		// i.setFlags(progress[0]);
		Log.e("SyncClient", "Sending Broadcast: " + i);
		context.sendBroadcast(i);
	}

	protected void onPreExecute() {
		setRunning(true);
	}

	protected void onPostExecute(String result) {
		setRunning(false);
	}
}
