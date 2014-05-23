package ch.ffhs.privatecloudffhs;

import ch.ffhs.privatecloud.database.Server;
import ch.ffhs.privatecloudffhs.connection.ReadKey;
import ch.ffhs.privatecloudffhs.connection.RsaKeyGen;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ActivityServer extends Activity {
	ActivityServer serveractivity;
	int serverid;
	Server server;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            serverid =(int) b.getInt("serverid");
           if ( serverid == 0)
           {
        	   server = new Server("New Servername");
           }
           else
           {
        	   //Load from DB
           }
        }
		setContentView(R.layout.activity_settings);
//		SharedPreferences settings = getSharedPreferences(R.string.perfname,MODE_PRIVATE);
 //       String hostname = settings.getString(R.string.perfs_hostname);
        
	}
	
	
    public void onButtonClicked(View v){
    	switch(v.getId()) {
    		case R.id.Settings_Button_Genkey:
    			RsaKeyGen rsakeygen = new RsaKeyGen(this);
    			//rsakeygen.GenerateKey();
    			rsakeygen.GenerateMockKey();
    		break;
    		case R.id.Settings_Button_Showkey:
    			ReadKey readkey = new ReadKey(this);
    			readkey.ReadFile();
    		break;
    		case R.id.Settings_Button_cancel:
    			this.finish();
    		break;
    		
    		
    		
    	}
    }

}
