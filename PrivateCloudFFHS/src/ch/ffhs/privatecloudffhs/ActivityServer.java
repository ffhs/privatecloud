package ch.ffhs.privatecloudffhs;

import java.util.ArrayList;
import java.util.List;

import ch.ffhs.privatecloudffhs.connection.ReadKey;
import ch.ffhs.privatecloudffhs.connection.RsaKeyGen;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.appcompat.R.id;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ActivityServer extends Activity {
	ActivityServer serveractivity;
	int serverid;
	PrivateCloudDatabase db;
	Server server;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent iin= getIntent();
        Bundle b = iin.getExtras();

		db = new PrivateCloudDatabase(getApplicationContext());

        if(b!=null)
        {
            serverid =(int) b.getInt("serverid");
           if ( serverid == 0)
           {
        	   server = new Server("New Servername");
           }
           else
           {
        	   server = db.getServer(serverid);
        	   //Load from DB
           }
        }
		setContentView(R.layout.activity_server);
//		SharedPreferences settings = getSharedPreferences(R.string.perfname,MODE_PRIVATE);
 //       String hostname = settings.getString(R.string.perfs_hostname);
        
	}
	
	
    public void onButtonClicked(View v){
    	switch(v.getId()) {
    		case R.id.server_Button_Genkey:
    			RsaKeyGen rsakeygen = new RsaKeyGen(this);
    			//rsakeygen.GenerateKey();
    			rsakeygen.GenerateMockKey();
    		break;
    		case R.id.server_Button_Showkey:
    			ReadKey readkey = new ReadKey(this);
    			readkey.ReadFile();
    		break;
    		case R.id.Settings_Button_cancel:
    			this.finish();
    		break;
    		
    		
    		
    	}
    }
    
    public void onRadioButtonClicked(View v){
    	Button buttongenkey = (Button) findViewById(R.id.server_Button_Genkey);
    	Button buttonshow = (Button) findViewById(R.id.server_Button_Showkey);
    	EditText password = (EditText) findViewById(R.id.Server_Password);
    	TextView pass = (TextView) findViewById(R.id.TextViewPW);
    	FrameLayout passlayout = (FrameLayout) findViewById(R.id.passwordlayout);
    	switch(v.getId()) {
		    case R.id.server_radio_key:
		    	passlayout.setVisibility(View.GONE);
		    	buttongenkey.setVisibility(View.VISIBLE);
		    	buttonshow.setVisibility(View.VISIBLE);
		    	password.setVisibility(View.GONE);
		    	pass.setVisibility(View.GONE);
			break;
		    case R.id.server_radio_pw:
		    	passlayout.setVisibility(View.VISIBLE);
		    	password.setVisibility(View.VISIBLE);
		    	pass.setVisibility(View.VISIBLE);
		    	buttongenkey.setVisibility(View.GONE);
		    	buttonshow.setVisibility(View.GONE);
		    break;
    	}
    }
   

}
