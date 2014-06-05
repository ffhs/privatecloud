package ch.ffhs.privatecloudffhs.sync;

import java.util.Timer;
import java.util.TimerTask;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class SyncService extends Service {
	private Timer myTimer = null;
	private final IBinder syncServiceBinder = new SyncServiceBinder ();
	
	private static final String KEY_SYNCINTERVAL = "syncinterval";
	private static final String NAME_MYPREF = "cloudsettings";
	private SharedPreferences settings;
	
	private static final String TAG = "SyncService";
	
	/** inner class implements the broadcast timer*/
	private class TimeServiceTimerTask extends TimerTask {	
		private static final String TAG = "TimeServiceTimerTask";
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
		
		Log.d(TAG, "SYNC CALLED");

	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		
		super.onCreate();
		
		settings = getSharedPreferences(NAME_MYPREF,MODE_PRIVATE);
		
		myTimer = new Timer("myTimer");
		myTimer.scheduleAtFixedRate( new TimeServiceTimerTask(), 0, settings.getInt(KEY_SYNCINTERVAL, 1) * 1000 * 60);
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
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    Intent bIntent = new Intent(this, SyncService.class);       
	    PendingIntent pbIntent = PendingIntent.getActivity(this, 0 , bIntent, Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    NotificationCompat.Builder bBuilder =
	            new NotificationCompat.Builder(this)
	                .setContentTitle("title")
	                .setContentText("sub title")
	                .setAutoCancel(true)
	                .setOngoing(true)
	                .setContentIntent(pbIntent);
	    Notification barNotif = bBuilder.build();
	    this.startForeground(1, barNotif);
	    
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
		private final String TAG = "SyncServiceBinder";
	
		public int getPID(){
			return android.os.Process.myPid();
		}
		
		
		public int getTID(){
			return android.os.Process.myTid();
		}
		
		public void syncNow()
		{
			sync();
		}
		
		public void sendCounter() {

		}

		public void sendTime() {

		}
		
	}
}
