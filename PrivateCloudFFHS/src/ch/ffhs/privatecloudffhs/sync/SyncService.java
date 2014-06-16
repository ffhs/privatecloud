package ch.ffhs.privatecloudffhs.sync;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class SyncService extends Service {
	private static final String TAG = "SyncService";

	private Timer myTimer = null;
	private final IBinder syncServiceBinder = new SyncServiceBinder ();
	
	private static final String KEY_SYNCINTERVAL = "syncinterval";
	private static final String NAME_MYPREF = "cloudsettings";
	private SharedPreferences settings;
	
	private SyncManager syncManagerObj;

	
	/** inner class implements the broadcast timer*/
	private class TimeServiceTimerTask extends TimerTask {	
		private static final String TAG = "TimeServiceTimerTask";
		@Override
		public void run() {	
			sync();
		}
	};

	private void sync()
	{		
		int counter = 0;
		if((counter % 5) == 0)
		{
			Log.d(TAG, "NOT RUNNING");

		}
		else
		{
			Log.d(TAG, "RUNNING");

		}
		Log.d("SyncService", "Calling SyncManager");
		syncManagerObj.syncAllFolders();
		Log.d(TAG, "SYNC CALLED");

	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		
		super.onCreate();
		
		settings = getSharedPreferences(NAME_MYPREF, MODE_PRIVATE);
		
	//	myTimer = new Timer("myTimer");
	//	myTimer.scheduleAtFixedRate( new TimeServiceTimerTask(), 0, settings.getInt(KEY_SYNCINTERVAL, 1) * 1000 * 60);
	//	myTimer.scheduleAtFixedRate( new TimeServiceTimerTask(), 0,1000);

		syncManagerObj = new SyncManager(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return syncServiceBinder;
	}
	
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Log.d(TAG, "onRebind");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		
	    Intent bIntent = new Intent(this, SyncService.class);       
	    PendingIntent pbIntent = PendingIntent.getActivity(this, 0 , bIntent, Intent.FLAG_ACTIVITY_CLEAR_TOP);
	  
	    NotificationCompat.Builder bBuilder =
	            new NotificationCompat.Builder(this)
	                .setContentTitle("privatecloud")
	                .setContentText("sync")
	                .setAutoCancel(true)
	                .setOngoing(true)
	                .setContentIntent(pbIntent);
	    startForeground(1, bBuilder.build());
	    
	    return START_STICKY;
	}
	
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind2");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		stopForeground(true);
	}
	
	
	public class SyncServiceBinder extends Binder {		
		public void syncNow()
		{
			sync();
		}
	}
}
