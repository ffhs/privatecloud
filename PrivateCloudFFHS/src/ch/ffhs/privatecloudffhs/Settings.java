package ch.ffhs.privatecloudffhs;

import java.util.ArrayList;
import java.util.List;

import ch.ffhs.privatecloudffhs.adapter.ServerListAdapter;
import ch.ffhs.privatecloudffhs.connection.ReadKey;
import ch.ffhs.privatecloudffhs.connection.RsaKeyGen;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class Settings extends Activity {

	ListView listView=null;
	Context contex=null;
	ServerListAdapter adapter=null;
	PrivateCloudDatabase db;

	private List<Server> list=new ArrayList<Server>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);
		contex=this;
		listView = (ListView) findViewById(R.id.Servers_List);
		
		db = new PrivateCloudDatabase(getApplicationContext());

		// load some dummy data
        for(int index=0; index< 4; index++){
        	Server test = new Server("TestServer DB" + index);
        	db.createServer(test);
        }
        
        adapter	= new ServerListAdapter(contex);
        listView.setAdapter(adapter);
		
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

          @Override
          public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            Object o = listView.getItemAtPosition(position);
            int serverid = ((Server) o).getId();
            Log.d("jada","clicked"+serverid);
            Intent activityserver = new Intent(contex,ActivityServer.class);
			activityserver.putExtra("serverid", serverid);
			startActivity(activityserver);
          }
        });
//		SharedPreferences settings = getSharedPreferences(R.string.perfname,MODE_PRIVATE);
//       String hostname = settings.getString(R.string.perfs_hostname);
        
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
    		
    		
    		
    	}
    }

}
