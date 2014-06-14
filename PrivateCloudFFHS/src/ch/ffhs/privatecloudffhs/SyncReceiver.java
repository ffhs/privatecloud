package ch.ffhs.privatecloudffhs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SyncReceiver extends BroadcastReceiver {   

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TimeService", "RECEIVER FU");
		Log.e("TimeService", "RECEIVER FU");
  //   Intent myIntent = new Intent(context, SyncService.class);
    // context.startService(myIntent);
    }

}