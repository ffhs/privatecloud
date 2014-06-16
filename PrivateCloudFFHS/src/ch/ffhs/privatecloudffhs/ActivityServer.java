package ch.ffhs.privatecloudffhs;

import java.util.Random;

import ch.ffhs.privatecloudffhs.connection.ReadKey;
import ch.ffhs.privatecloudffhs.connection.RsaKeyGen;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityServer extends Activity {
	ActivityServer serveractivity;
	int serverid;
	PrivateCloudDatabase db;
	Server server;
	EditText servername;
	EditText username;
	EditText password;
	EditText port;
	EditText hostname;
	EditText remoteroot;
	RadioButton passauth;
	RadioButton keyauth;
	Button buttongenkey;
	Button buttonshow;
	TextView pass;
	Spinner proto;
	FrameLayout passlayout;
	boolean newserver;
	int protoint;
	Context context = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server);
		Intent iin= getIntent();
        Bundle b = iin.getExtras();
        servername = (EditText) findViewById(R.id.Server_EditText_Servername);
        username = (EditText) findViewById(R.id.Server_EditText_Username);
        password = (EditText) findViewById(R.id.Server_EditText_Password);
        hostname = (EditText) findViewById(R.id.Server_EditText_Hostname);
        remoteroot = (EditText) findViewById(R.id.Server_EditText_Remotedir);
        keyauth = (RadioButton) findViewById(R.id.server_radio_key);
        passauth = (RadioButton) findViewById(R.id.server_radio_pw);
		port = (EditText) findViewById(R.id.Server_EditText_Port);
		proto = (Spinner) findViewById(R.id.Server_Spinner_protocol);
		buttongenkey = (Button) findViewById(R.id.server_Button_Genkey);
    	buttonshow = (Button) findViewById(R.id.server_Button_Showkey);
    	pass = (TextView) findViewById(R.id.TextViewPW);
    	passlayout = (FrameLayout) findViewById(R.id.passwordlayout);
        db = new PrivateCloudDatabase(getApplicationContext());
		
        if(b!=null)
        {
            serverid =b.getInt("serverid");
           if ( serverid == 0)
           {
        	   server = new Server("New Servername");
           }
           else
           {
        	   server = db.getServer(serverid);
        	   //Load from DB
        	   Log.d("jada","ServerID:"+serverid);
           }
        }
        loadValues();
//		SharedPreferences settings = getSharedPreferences(R.string.perfname,MODE_PRIVATE);
 //       String hostname = settings.getString(R.string.perfs_hostname);
        
	}
	
	public void loadValues(){
		Log.d("jada","test"+server.getServername()+server.getUsername()+server.getPassword());
		if(server.getServername() != null)
		{
			servername.setText(server.getServername());
		}
		if(server.getUsername() != null)
		{
			username.setText(server.getUsername());
		}
		if(server.getPort() != 0)
		{
			port.setText(server.getPort() + "");
		}
		if(server.getHostname() != null)
		{
			hostname.setText(server.getHostname());
		}
		if(server.getPassword() != "" && server.getPassword() != null && server.getPassword().toString() != "")
		{
			Log.d("jada","Password value is:\"" + server.getPassword()+"\"");
			password.setText(server.getPassword());
			passauth.setChecked(true);
			showpw(true);
		}
		else if(server.getCertpath() != null)
		{
			keyauth.setChecked(true);
			showkey(true);
		}
		if(server.getRemoteroot() != "")
		{
			remoteroot.setText(server.getRemoteroot());
		}
		proto.setSelection(server.getProto());
	}
    public void onButtonClicked(View v){
    	switch(v.getId()) {
    		case R.id.server_Button_Genkey:
    			Random randomGenerator = new Random();
    			int randomInt = randomGenerator.nextInt(1000000);
    			RsaKeyGen rsakeygen = new RsaKeyGen(this);
    			//server.setCertpath(rsakeygen.GenerateKey("id_rsa"+randomInt));;
    			server.setCertpath(rsakeygen.GenerateMockKey("id_rsa"+randomInt));
    			
    			Log.d("jada","Setting certpath to :" + server.getCertpath());
    			Toast.makeText(ActivityServer.this, "New keypair calculated", Toast.LENGTH_LONG).show();
    		break;
    		case R.id.server_Button_Showkey:
    			ReadKey readkey = new ReadKey(this);
    			readkey.ReadFile(server.getCertpath());
    		break;
    		case R.id.Server_Button_cancel:
    			this.finish();
    		break;
    		case R.id.Server_Button_save:
    			savesettings();
    			
    		break;
    		case R.id.Server_Button_delete:
    			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    			    @Override
    			    public void onClick(DialogInterface dialog, int which) {
    			        switch (which){
    			        case DialogInterface.BUTTON_POSITIVE:
    			        		db.deleteServer(server.getId());
    			        		((Activity) context).finish();
    			            break;

    			        case DialogInterface.BUTTON_NEGATIVE:
    			            	
    			            break;
    			        }
    			    }
    			};

    			AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setMessage("Are you sure to delete the server?").setPositiveButton("Yes", dialogClickListener)
    			.setNegativeButton("No", dialogClickListener).show();
				
    		break;
    	}
    }
    private void savesettings(){
    	if(isEmpty(port) || isEmpty(hostname)  || isEmpty(servername) || isEmpty(username) || isEmpty(remoteroot) || (passauth.isChecked() && keyauth.isChecked()))
    	{
    		Toast.makeText(ActivityServer.this, "Not all required information supplied", Toast.LENGTH_LONG).show();
    	}
    	else
    	{
			server.setHostname(hostname.getText().toString());
			server.setServername(servername.getText().toString());
			server.setPort(Integer.parseInt(port.getText().toString()));
			Log.d("jada", "Protofield: "+ proto.getSelectedItem().toString());
			if(proto.getSelectedItemId() == 0){
				protoint = 0;
				Log.d("jada", "SFTP");
			}else if(proto.getSelectedItemId() == 1){
				protoint = 1;
				Log.d("jada", "FTP");
			}else if(proto.getSelectedItemId() == 2){
				protoint = 2;
				Log.d("jada", "FTPS");
			}
			else{
				protoint = 0;
			}
			server.setProto(protoint);
			
			String remoteRoot = remoteroot.getText().toString();
			if(remoteRoot.substring(remoteRoot.length() -1 ).equals("/")) 
			{
				remoteRoot =  remoteRoot.substring(0, remoteRoot.length() - 1);
			}
			server.setRemoteroot(remoteRoot);
			
			server.setUsername(username.getText().toString());
			
			if(passauth.isChecked()){
				String passw;
				if (password.getText().toString() == "")
				{
					passw = "checked";
				}
				else
				{
					passw = password.getText().toString();
				}
				server.setPassword(passw);
				server.setCertpath(null);
			}
			if(keyauth.isChecked()){
				if(server.getCertpath()== "")
				{
					server.setCertpath("checked");
				}
				server.setPassword(null);
			}
			
			if ( serverid == 0)
			{
				db.createServer(server);
			}
			else
			{
				db.updateServer(server);
			}
			db.close();
			this.finish();
		}
		
    }
    
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }
    public void onRadioButtonClicked(View v){
    	switch(v.getId()) {
		    case R.id.server_radio_key:
		    	showkey(true);
		    	showpw(false);
		    	
			break;
		    case R.id.server_radio_pw:
		    	showkey(false);
		    	showpw(true);
		    break;
    	}
    }
    private void showkey(Boolean visible)
    {
    	if(visible)
    	{
    		buttongenkey.setVisibility(View.VISIBLE);
	    	buttonshow.setVisibility(View.VISIBLE);
    	}
    	else
    	{
    		buttongenkey.setVisibility(View.GONE);
	    	buttonshow.setVisibility(View.GONE);
    	}
    }
    private void showpw(Boolean visible)
    {
    	if(visible)
    	{
    		passlayout.setVisibility(View.VISIBLE);
	    	password.setVisibility(View.VISIBLE);
	    	pass.setVisibility(View.VISIBLE);
    	}
    	else
    	{
	    	passlayout.setVisibility(View.GONE);
    		password.setVisibility(View.GONE);
	    	pass.setVisibility(View.GONE);
    	}
    }
}
