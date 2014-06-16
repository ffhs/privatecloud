package ch.ffhs.privatecloudffhs;

import ch.ffhs.privatecloud.triggers.ShakeDetector;
import ch.ffhs.privatecloud.triggers.ShakeDetector.OnShakeListener;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.sync.SyncService;
import ch.ffhs.privatecloudffhs.sync.SyncService.SyncServiceBinder;
import ch.ffhs.privatecloudffhs.util.SystemUiHider;
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
import android.util.Log;
import android.view.View;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider 
 */
public class Main extends Activity {
	
	
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
            	Log.d("MAIN", "onShake");
            	syncService.syncNow();
                //handleShakeEvent(count);
            }
        });
		
        setContentView(R.layout.activity_main);
        
        Intent syncServiceIntent = new Intent(this, SyncService.class);
    	startService(syncServiceIntent);
   		bindService(syncServiceIntent, syncServiceConnection, Context.BIND_AUTO_CREATE);
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
			Log.d(TAG, "onServiceDisconnected");
			syncService = null;
		}

	};
	
	@Override
    public void onResume() {
        Log.d("MAIN", "onResume");
        super.onResume();
        // register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,    SensorManager.SENSOR_DELAY_UI);
    }
 
    @Override
    public void onPause() {
    	Log.d("MAIN", "onPause");
        // unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    
}
