package ch.ffhs.privatecloudffhs;

import ch.ffhs.privatecloudffhs.util.SystemUiHider;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        //clickhandler.onButtonClicked(v);
    }
   



    
    public void onButtonClicked(View v){
    	switch(v.getId()) {
    		case R.id.Main_Button_Settings:
    			Intent settings = new Intent(this,Settings.class);
    			startActivity(settings);
    		break;
    		
    		case R.id.Main_Button_SyncNow:
    			SshConnection sshconnection = new SshConnection(this);
	        	sshconnection.Connect();
    			
    			
    		break;
    		
    		case R.id.Main_Button_Folders:
    			Intent folders = new Intent(this,Folders.class);
    			startActivity(folders);
    		break;
    	}
    }
    
}
