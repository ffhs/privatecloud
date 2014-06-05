package ch.ffhs.privatecloudffhs;

import ch.ffhs.privatecloudffhs.R.string;
import ch.ffhs.privatecloudffhs.connection.SshConnection;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.sync.SyncService;
import ch.ffhs.privatecloudffhs.sync.SyncService.SyncServiceBinder;
import ch.ffhs.privatecloudffhs.util.SystemUiHider;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class Main extends Activity {

	 PrivateCloudDatabase db;
	 Context context = null;
		private SyncServiceBinder syncService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
		db = new PrivateCloudDatabase(getApplicationContext());
		context = this;
		
        setContentView(R.layout.activity_main);
        
        Intent syncServiceIntent = new Intent(this, SyncService.class);
    	startService(syncServiceIntent);
   		bindService(syncServiceIntent, syncServiceConnection, Context.BIND_AUTO_CREATE);
    }
   
    
    protected void onDestroy() {
		Log.d("MAIN", "onDestroy");
		super.onDestroy();
	}




    
    public void onButtonClicked(View v){
    	switch(v.getId()) {
    		case R.id.Main_Button_Settings:
    			Intent settings = new Intent(this,Settings.class);
    			startActivity(settings);
    		break;
    		
    		case R.id.Main_Button_SyncNow:
    			syncService.syncNow();
    			//SshConnection sshconnection = new SshConnection(this);
	        	//sshconnection.Connect();
    		break;
    		
    		case R.id.Main_Button_Folders:
    			if(db.getAllServers() == null)
    			{
    				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		 
					// set title
					alertDialogBuilder.setTitle(R.string.error);
		 
					// set dialog message
					alertDialogBuilder
						.setMessage(R.string.error_server_first)
						.setCancelable(false)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						}
					);				
					
	 				// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();
    			}
    			else
    			{
    				Intent folders = new Intent(this,Folders.class);
        			startActivity(folders);
    			}
    		break;
    	}
    }
    
    
	
	private ServiceConnection syncServiceConnection = new ServiceConnection() {
		private final String TAG = "syncServiceConnection";
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected");
			syncService = (SyncServiceBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			syncService = null;
			Log.d(TAG, "onServiceDisconnected");
		}

	};

    
}
