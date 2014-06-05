package ch.ffhs.privatecloudffhs.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncReceiver extends BroadcastReceiver {   

    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.d("TimeService", "RECEIVER DIESEEEE");
		Log.e("TimeService", "RECEIVER DIESEEEE");
		Intent myIntent = new Intent(context, SyncService.class);
        context.startService(myIntent);
    }

}