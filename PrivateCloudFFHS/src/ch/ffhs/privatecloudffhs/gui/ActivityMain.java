package ch.ffhs.privatecloudffhs.gui;

import java.util.Date;

import org.w3c.dom.Text;

import ch.ffhs.privatecloudffhs.R;
import ch.ffhs.privatecloudffhs.R.id;
import ch.ffhs.privatecloudffhs.R.layout;
import ch.ffhs.privatecloudffhs.R.string;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.gui.util.SystemUiHider;
import ch.ffhs.privatecloudffhs.sync.SyncService;
import ch.ffhs.privatecloudffhs.sync.SyncService.SyncServiceBinder;
import ch.ffhs.privatecloudffhs.triggers.ShakeDetector;
import ch.ffhs.privatecloudffhs.triggers.ShakeDetector.OnShakeListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider 
 */
public class ActivityMain extends Activity {
	
	
	//The following are used for the shake detection
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;


	 PrivateCloudDatabase db;
	 Context context = null;
		private SyncServiceBinder syncService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
		db = new PrivateCloudDatabase(getApplicationContext());
		context = this;
		
		// ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new OnShakeListener() {
 
            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
            	Log.d("MAIN", "Device shake detected");
            	syncService.syncNow();
                //handleShakeEvent(count);
            }
        });
		
        setContentView(R.layout.activity_main);
        
        Intent syncServiceIntent = new Intent(this, SyncService.class);
    	startService(syncServiceIntent);
   		bindService(syncServiceIntent, syncServiceConnection, Context.BIND_AUTO_CREATE);   		
    }
    
    protected void onResume() {
	 	super.onResume();
	 	
	 	mSensorManager.registerListener(mShakeDetector, mAccelerometer,    SensorManager.SENSOR_DELAY_UI);
		
		Button buttonConflict = (Button) findViewById(R.id.Main_Button_Conflict);
		if(db.isanyconflict())
   		{
   			buttonConflict.setVisibility(View.VISIBLE);
    	}
    	else
    	{
    		buttonConflict.setVisibility(View.GONE);	    	
   		}
		
		Folder lastSyncedFolder = db.getLastSyncedFolder();
		TextView txtLastSync = (TextView) findViewById(R.id.Main_Text_LastSync);
		if(lastSyncedFolder != null && lastSyncedFolder.getLastsync() == "01.01.1970 00:00")
   		{
			StringBuilder text = new StringBuilder();
			text.append(getString(R.string.main_label_lastsync)).append(" ").append(lastSyncedFolder.getLastsync());
			
			txtLastSync.setText(text.toString());
    	}
    	else
    	{
			txtLastSync.setText(R.string.main_label_lastsync_none);
        }
    }
   
    
    @Override
	protected void onDestroy() {
		Log.d("MAIN", "onDestroy");
		unbindService(syncServiceConnection);

		super.onDestroy();
	}




    
    public void onButtonClicked(View v){
    	switch(v.getId()) {
    		case R.id.Main_Button_Settings:
    			Intent settings = new Intent(this,ActivitySettings.class);
    			startActivity(settings);
    		break;
    		
    		case R.id.Main_Button_SyncNow:
    			Log.d("MAIN", "R.id.Main_Button_SyncNow clicked");
    			syncService.syncNow();
    		break;
    		
    		case R.id.Main_Button_Conflict:
        		Intent conflict = new Intent(this,ActivityConflict.class);
        		startActivity(conflict);
        	break;
        		
    		
    		case R.id.Main_Button_Folders:
    			if(db.getAllServers().size() == 0)
    			{
    				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		 
					// set title
					alertDialogBuilder.setTitle(R.string.error);
		 
					// set dialog message
					alertDialogBuilder
						.setMessage(R.string.error_server_first)
						.setCancelable(false)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							@Override
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
    				Intent folders = new Intent(this,ActivityFolders.class);
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
			Log.d(TAG, "onServiceDisconnected");

			syncService = null;
		}

	};
	

    @Override
    public void onPause() {
    	Log.d("MAIN", "onPause");
        // unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    
}
