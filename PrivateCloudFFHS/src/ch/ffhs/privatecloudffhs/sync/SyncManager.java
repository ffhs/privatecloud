package ch.ffhs.privatecloudffhs.sync;

import java.util.Iterator;
import java.util.List;

import ch.ffhs.privatecloudffhs.connection.SshCertConnection;
import ch.ffhs.privatecloudffhs.connection.SshConnection;
import ch.ffhs.privatecloudffhs.connection.SshPwConnection;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import android.content.Context;
import android.util.Log;

public class SyncManager {
	private Boolean running;
	private static final String TAG = "TimeService";
	private PrivateCloudDatabase db;
	private Context context;


	public SyncManager(Context context) {
		this.context = context;
		this.running = true;
		
		db = new PrivateCloudDatabase(context);
	}

	public void syncAllFolders()
	{
		List<Folder> syncfolders = db.getAllFolders();
		
		
		for (Folder folder : syncfolders) {
			Server server = db.getServer(folder.getServerId());
			SyncConnection syncConnectionObj = null;

			if(server.getPassword() == null)
			{
				// build connection using cert
				syncConnectionObj = new SshCertConnection(server, context);
			}
			else
			{
				// build connection with password-based authentication 
				syncConnectionObj = new SshPwConnection(server);
			}
			
			for (int i = 0; i < 3; i++) {
				if(!syncConnectionObj.isReady())
				{
					try {
						Thread.sleep(1000);
					} catch(InterruptedException e) {
					}
				}
				else
				{
					break;
				}
			}
			
			if(syncConnectionObj.isReady())
			{
				SyncClient syncClientObj = new SyncClient(context, folder, syncConnectionObj);
				syncClientObj.sync();				
			}
		}
		
		
	}
	
	public Boolean isRunning() {
		
		Log.d(TAG, "RUNNING");
		return running;
	}	
	
}
