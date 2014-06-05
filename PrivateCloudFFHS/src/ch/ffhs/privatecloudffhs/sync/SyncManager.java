package ch.ffhs.privatecloudffhs.sync;

import android.util.Log;

public class SyncManager {
	private Boolean running;
	int counter;
	private static final String TAG = "TimeService";


	public SyncManager() {
		this.running = true;
	}

	public Boolean isRunning() {
		counter++;
		
		if((counter % 5) == 0)
		{
			Log.d(TAG, "NOT RUNNING");

		}
		else
		{
			Log.d(TAG, "RUNNING");

		}
		return running;
	}	
	
}
