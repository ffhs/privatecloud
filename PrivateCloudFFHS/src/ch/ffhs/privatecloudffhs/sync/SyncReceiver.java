package ch.ffhs.privatecloudffhs.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * SyncReceiver
 * 
 * Startet den Service nach dem Bootvorgang des Geräts
 * 
 * @author Thierry Baumann
 */

public class SyncReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent myIntent = new Intent(context, SyncService.class);
		context.startService(myIntent);
	}

}