package ch.ffhs.privatecloudffhs;

import java.util.ArrayList;
import java.util.List;

import ch.ffhs.privatecloud.database.Server;
import ch.ffhs.privatecloudffhs.connection.ReadKey;
import ch.ffhs.privatecloudffhs.connection.RsaKeyGen;
import ch.ffhs.privatecloudffhs.list.ServerListAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class Settings extends Activity {

	ListView listView=null;
	Context contex=null;
	ServerListAdapter adapter=null;
	private List<Server> list=new ArrayList<Server>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);
		contex=this;
		listView = (ListView) findViewById(R.id.Servers_List);
		
		// load some dummy data
        for(int index=0; index< 4; index++){
        	Server test = new Server("TestServer" + index);
        	list.add(test);
        }
        
        adapter	= new ServerListAdapter(contex, list);
        listView.setAdapter(adapter);
		
		
//		SharedPreferences settings = getSharedPreferences(R.string.perfname,MODE_PRIVATE);
 //       String hostname = settings.getString(R.string.perfs_hostname);
        
	}
    public void onButtonClicked(View v){
    	switch(v.getId()) {
    		case R.id.Settings_Button_cancel:
    			this.finish();
    		break;
    		case R.id.Settings_Button_Add:
    			Server server = new Server("New Server");
    			
    			Intent activityserver = new Intent(this,ActivityServer.class);
    			startActivity(activityserver);
    		break;
    		
    		
    		
    	}
    }

}
