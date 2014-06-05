package ch.ffhs.privatecloudffhs.sync;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


public class SyncService extends Service {
	private static final String TAG = "TimeService";
	private Timer myTimer = null;
	private int counter = 0;
	private long time = 0;
	
	private Handler timeCallbackHandler = null;
	private final IBinder timeServiceBinder = new TimeServiceBinder();
//	private SyncManager = new TimeS;
	
	
	/** inner class implements the broadcast timer*/
	private class TimeServiceTimerTask extends TimerTask {	
		private static final String TAG = "TimeServiceTimerTask";
		public void run() {		
			time   = + System.currentTimeMillis();
			counter++;
			Log.d(TAG, "counter = " + counter);
			Message msg = new Message();
			Bundle bundle = new Bundle();

			bundle.putLong("TIME", System.currentTimeMillis());
			bundle.putInt("COUNTER", counter);
			bundle.putInt("PID", android.os.Process.myPid());
			bundle.putInt("TID", android.os.Process.myTid());
			msg.setData(bundle);
			if(timeCallbackHandler != null){
				timeCallbackHandler.sendMessage(msg);
			}
			
			sync();
		}
	};

	private void sync()
	{
		if((counter % 5) == 0)
		{
			Log.d(TAG, "NOT RUNNING");

		}
		else
		{
			Log.d(TAG, "RUNNING");

		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		myTimer = new Timer("myTimer");
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		myTimer.scheduleAtFixedRate( new TimeServiceTimerTask(), 0, 1000);
		return timeServiceBinder;
	}
	
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Log.d(TAG, "onRebind");
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(TAG, "onStart");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		timeCallbackHandler = null;
		myTimer.cancel();
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		myTimer = null;
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}
	
	public class TimeServiceBinder extends Binder {
		private final String TAG = "TimeServiceBinder";
		
		public int getCounter() {
			return counter;
		}

		public int getTest() {
			return counter + 1100110011;
		}

		public long getTime() {
			return time;
		}

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
		
		public void setActivityCallbackHandler(Handler callback){
			Log.d(TAG, "");
			timeCallbackHandler = callback;
		}

	}
}
