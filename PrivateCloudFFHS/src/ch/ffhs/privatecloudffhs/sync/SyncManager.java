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
import android.util.Log;

/**
 * SyncManager
 * 
 * Der SyncManager ist für die synchronisation zustänig und wird vom Service
 * instanziert. Hier wird für jenden Order ein SynClient gestartet und
 * enstprchenden den Servereinstellungen ein Zertifikat oder Benutzername/PW
 * Connection Objekt erstellt.
 * 
 * @author Thierry Baumann
 */

public class SyncManager {
	private static final String TAG = "SyncManager";
	private PrivateCloudDatabase db;
	private Context context;
	private ArrayList<SyncClient> syncClients;

	public SyncManager(Context context) {
		this.context = context;

		db = new PrivateCloudDatabase(context);
		syncClients = new ArrayList<SyncClient>();
	}

	private class LongOperation extends AsyncTask<String, Integer, String> {

		public LongOperation() {
			super();
		}

		@Override
		protected String doInBackground(String... params) {
			List<Folder> syncfolders = db.getAllFolders();
			for (Folder folder : syncfolders) {
				Server server = db.getServer(folder.getServerId());
				if (server != null) {

					SyncConnection syncConnectionObj = null;

					if (server.getPassword() == null) {
						// build connection using cert
						syncConnectionObj = new SshCertConnection(server,
								folder);

					} else {
						// build connection with password-based authentication
						syncConnectionObj = new SshPwConnection(server, folder);
					}

					SyncClient syncClientObj = new SyncClient(context, folder,
							syncConnectionObj, server);
					syncClientObj.executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, "");

					syncClients.add(syncClientObj);
				}
			}

			while (isRunning()) {
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
	}

	public void sync() {
		Log.d(TAG, "SYNC CALLED");
		if (!isRunning()) {
			new LongOperation().execute("");
		} else {
			Log.d(TAG, "CLIENTS ARE RUNNING - NO EXECUTE");
		}
	}

	@SuppressLint("NewApi")
	private void conflictnotification() {
		db = new PrivateCloudDatabase(context);
		if (db.isanyconflict()) {
			Log.d("jada", "There is a conflict");
			int icon = R.drawable.notification_icon;
			long when = System.currentTimeMillis();
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(ns);
			Intent intent = new Intent(context, ActivityMain.class);
			PendingIntent pending = PendingIntent.getActivity(context, 0,
					intent, 0);
			Notification notification;
			if (Build.VERSION.SDK_INT < 11) {
				notification = new Notification(icon, "Title", when);
				notification.setLatestEventInfo(context, context.getResources()
						.getString(R.string.notification_title), context
						.getResources().getString(R.string.notification_text),
						pending);
			} else {
				notification = new Notification.Builder(context)
						.setContentTitle(
								context.getResources().getString(
										R.string.notification_title))
						.setContentText(
								context.getResources().getString(
										R.string.notification_text))
						.setSmallIcon(R.drawable.notification_icon)
						.setContentIntent(pending).setWhen(when)
						.setAutoCancel(true).build();
				// notification.flags = Notification.FLAG_ONGOING_EVENT;
				notification.setLatestEventInfo(context, context.getResources()
						.getString(R.string.notification_title), context
						.getResources().getString(R.string.notification_text),
						pending);
			}
			mNotificationManager.notify(1, notification);

		} else {
			Log.d("jada", "There is no conflict");
		}
		
	}

	public boolean isRunning() {
		for (SyncClient syncClient : syncClients) {
			if (syncClient.isRunning()) {
				Log.d("threads", "running");
				return true;
			}
			// syncClients.remove(syncClient);
		}

		return false;
	}
}
