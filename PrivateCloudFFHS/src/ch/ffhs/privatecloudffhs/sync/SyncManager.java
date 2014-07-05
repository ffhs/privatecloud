package ch.ffhs.privatecloudffhs.sync;

import java.util.ArrayList;
import java.util.List;

import ch.ffhs.privatecloudffhs.R;
import ch.ffhs.privatecloudffhs.connection.SshCertConnection;
import ch.ffhs.privatecloudffhs.connection.SshPwConnection;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import ch.ffhs.privatecloudffhs.gui.ActivityMain;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class SyncManager {
	private static final String TAG = "SyncManager";
	private PrivateCloudDatabase db;
	private Context context;
	private ArrayList<SyncClient> syncClients;
	public static final String ProgressBar_Intent = "ch.ffhs.privatecloudffhs.sync";

	public SyncManager(Context context) {
		this.context = context;
		
		db = new PrivateCloudDatabase(context);
		syncClients = new ArrayList<SyncClient>();
	}
	
	
	private class LongOperation extends AsyncTask<String, String, String> {
		
		public LongOperation(){
			super();
		}
		@Override
		protected String doInBackground(String... params) {			
			List<Folder> syncfolders = db.getAllFolders();
			for (Folder folder : syncfolders) {
				Server server = db.getServer(folder.getServerId());
				if(server != null && (server.getPassword() != null || server.getCertpath() != null)){
						
					SyncConnection syncConnectionObj = null;
	
					if(server.getPassword() != null) {
						// build connection with password-based authentication 
						syncConnectionObj = new SshPwConnection(server,folder);
					}
					else {
						// build connection using cert
						syncConnectionObj = new SshCertConnection(server,folder);
					}
					
					SyncClient syncClientObj = new SyncClient(context, folder, syncConnectionObj, server);
					syncClientObj.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
	
					syncClients.add(syncClientObj);
				}
				else
				{
					StringBuilder errorMsg =  new StringBuilder();
					errorMsg .append(context.getString(R.string.error)).append(", ");
					errorMsg.append(context.getString(R.string.progress_connection_error)).append(" ");
					errorMsg.append(context.getString(R.string.progress_error_no_server_linked_1)).append(folder.getPath()).append(" ");
					
					publishProgress(errorMsg.toString(), "0");
				}
			}	
			
			if(syncfolders.size() == 0) {
				publishProgress(context.getString(R.string.error) + ", " + context.getString(R.string.progress_error_no_folders), "0");
			}
			
			while(isRunning())
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			conflictnotification();
			
			Log.d(TAG, "DONE");

			return "Executed";
		}
		

		protected void onProgressUpdate(String... progress) {
			Intent i = new Intent();
			Bundle extras = new Bundle();

			if (Integer.parseInt(progress[1]) > 99) progress[1] = "99";
			if (progress.length > 2 && progress[2] == "1") progress[1] = "100";

			extras.putString("TEXT", progress[0]);
			extras.putString("PERCENT", progress[1]);
			i.putExtras(extras);

			i.setAction(ProgressBar_Intent);
			context.sendBroadcast(i);
		}
	}
	
	
	public void sync()
	{
		Log.d(TAG, "SYNC CALLED");
		if(!isRunning())
		{
			new LongOperation().execute("");
		}
		else
		{
			Log.d(TAG, "CLIENTS ARE RUNNING - NO EXECUTE");
		}		
	}
	
	
	@SuppressLint("NewApi")
	private void conflictnotification(){
		db = new PrivateCloudDatabase(context);
		if(db.isanyconflict()){
			Log.d(TAG, "There is a conflict");
			int icon = R.drawable.notification_icon;
			long when = System.currentTimeMillis();
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
			Intent intent=new Intent(context,ActivityMain.class);
			PendingIntent  pending=PendingIntent.getActivity(context, 0, intent, 0);
			Notification notification;
	        if (Build.VERSION.SDK_INT < 11) {
	            notification = new Notification(icon, "Title", when);
	            notification.setLatestEventInfo(
	                    context,
	                    context.getResources().getString(R.string.notification_title),
	                    context.getResources().getString(R.string.notification_text),
	                    pending);
	        } else {
	            notification = new Notification.Builder(context)
	                    .setContentTitle(context.getResources().getString(R.string.notification_title))
	                    .setContentText(
	                    context.getResources().getString(R.string.notification_text)).setSmallIcon(R.drawable.notification_icon)
	                    .setContentIntent(pending).setWhen(when).setAutoCancel(true)
	                    .build();
	            	//notification.flags = Notification.FLAG_ONGOING_EVENT;
	            	notification.setLatestEventInfo(
		                    context,
		                    context.getResources().getString(R.string.notification_title),
		                    context.getResources().getString(R.string.notification_text),
		                    pending);
	        }
			mNotificationManager.notify(1, notification);
			
			
		}
		else{
			Log.d(TAG, "There is no conflict");
		}
	}
	
	public boolean isRunning()
	{
		for(SyncClient syncClient : syncClients)
		{
			if(syncClient.isRunning())
			{
				Log.d(TAG,"running");
				return true;
			}
		}
		
		return false;
	}
	

}
