package ch.ffhs.privatecloudffhs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Settings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);

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
    		
    		
    	}
    }

}
