package ch.ffhs.privatecloudffhs;

import ch.ffhs.esa.block4.save_restore_current_state.R;
import ch.ffhs.privatecloudffhs.connection.ReadKey;
import ch.ffhs.privatecloudffhs.connection.RsaKeyGen;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Settings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);
		SharedPreferences settings = getSharedPreferences(R.string.perfname,MODE_PRIVATE);
        counter = settings.getInt(KEY_COUNTER, 0);
        persistText = settings.getString(KEY_PERSIST_TXT, getString(R.string.text_eingeben));
        
        final EditText editPersistText = (EditText) findViewById(R.id.persistentEditText);
        editPersistText.setText(persistText);
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
