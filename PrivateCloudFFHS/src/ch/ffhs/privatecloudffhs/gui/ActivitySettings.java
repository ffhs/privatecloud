package ch.ffhs.privatecloudffhs.gui;

import java.util.ArrayList;
import java.util.List;

import ch.ffhs.privatecloudffhs.R;
import ch.ffhs.privatecloudffhs.R.id;
import ch.ffhs.privatecloudffhs.R.layout;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import ch.ffhs.privatecloudffhs.gui.adapter.ServerListAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ActivitySettings extends Activity {



	private static final String NAME_MYPREF = "cloudsettings";
	private static final String KEY_SYNCINTERVAL = "syncinterval";
	private static final String KEY_ONWIFI = "onwifi";
	private static final String KEY_ONCHARGE = "oncharge";
	ListView listView = null;
	Context context = null;
	ServerListAdapter adapter = null;
	PrivateCloudDatabase db;
	int syncinterval;
	boolean onwifi;
	boolean oncharge;
	EditText syncintervalEditText;
	CheckBox onwifiCheckBox;
	CheckBox onchargeCheckBox;
	SharedPreferences settings;
	private List<Server> list=new ArrayList<Server>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);
		context=this;
		listView = (ListView) findViewById(R.id.Servers_List);
		syncintervalEditText = (EditText) findViewById(R.id.Settings_Syncinterval);
		onwifiCheckBox = (CheckBox) findViewById(R.id.Settings_chkbx_onlyWifi);
		onchargeCheckBox = (CheckBox) findViewById(R.id.Settings_chkbx_onlyCharging);
		settings = getSharedPreferences(NAME_MYPREF,MODE_PRIVATE);
		db = new PrivateCloudDatabase(getApplicationContext());

		adapter	= new ServerListAdapter(context);
        listView.setAdapter(adapter);
		
        listView.setClickable(true);
        loadValues();
        refreshFolderList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        
          @Override
          public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            Object o = listView.getItemAtPosition(position);
            int serverid = ((Server) o).getId();
            Log.d("jada","clicked"+serverid);
            Intent activityserver = new Intent(context,ActivityServer.class);
			activityserver.putExtra("serverid", serverid);
			startActivity(activityserver);
          }
        });
//		SharedPreferences settings = getSharedPreferences(R.string.perfname,MODE_PRIVATE);
//       String hostname = settings.getString(R.string.perfs_hostname);
        
	}

	private void refreshFolderList()
	{
        adapter.refreshList(db.getAllServers());
        db.closeDB();
	}
	
	private void loadValues(){
        syncinterval = settings.getInt(KEY_SYNCINTERVAL, 0);
        onwifi = settings.getBoolean(KEY_ONWIFI, false);
        oncharge = settings.getBoolean(KEY_ONCHARGE, false);
        syncintervalEditText.setText(""+syncinterval);
        onwifiCheckBox.setChecked(onwifi);
        onchargeCheckBox.setChecked(oncharge);
	}
    public void onButtonClicked(View v){
    	switch(v.getId()) {
    		case R.id.Settings_Button_cancel:
    			this.finish();
    		break;
    		case R.id.Settings_Button_Add:
    			int serverid = 0;
    			Intent activityserver = new Intent(this,ActivityServer.class);
    			activityserver.putExtra("serverid", serverid);
    			startActivity(activityserver);
    		break;
    		case R.id.Settings_Button_save:
    			if (!syncintervalEditText.getText().toString().matches(""))
    			{
	    			SharedPreferences.Editor editor = settings.edit();
	    			editor.putInt(KEY_SYNCINTERVAL, Integer.parseInt(syncintervalEditText.getText().toString()));
	    			editor.putBoolean(KEY_ONWIFI, onwifiCheckBox.isChecked());
	    			editor.putBoolean(KEY_ONCHARGE, onchargeCheckBox.isChecked());
	    			editor.commit();
	    			this.finish();
    			}
    			else
    			{
    				Toast.makeText(ActivitySettings.this, "Invalid Sync interval", Toast.LENGTH_LONG).show();
    			}
    		break;
    		
    		
    		
    	}
    }
    @Override
	public void onResume() {
        super.onResume();

        refreshFolderList();
    }
}
