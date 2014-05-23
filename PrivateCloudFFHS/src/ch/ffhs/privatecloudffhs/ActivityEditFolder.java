package ch.ffhs.privatecloudffhs;

import java.util.List;

import ch.ffhs.privatecloudffhs.adapter.EditFolderSpinServerAdapter;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import ch.ffhs.privatecloudffhs.util.SimpleFileDialog;
import ch.ffhs.privatecloudffhs.util.SystemUiHider;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ActivityEditFolder extends Activity {
		int folderid;
		PrivateCloudDatabase db;
		Folder folder;
		Spinner serverSpinner;
	    EditFolderSpinServerAdapter adapter;
        TextView selectedFolderText;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Intent iin= getIntent();
	        Bundle b = iin.getExtras();

	        setContentView(R.layout.activity_activity_edit_folder);	        

			db = new PrivateCloudDatabase(getApplicationContext());

			folder = new Folder();
	        selectedFolderText = (TextView) findViewById(R.id.EditFolder_Text_Folder);	        

	        if(b!=null)
	        {
	           folderid =(int) b.getInt("folderid");
	           
			   if(folderid != 0)
			   {
				   //Load from DB
//				   server = db.getServer(serverid);
//					selectedFolderText.
			   }
	        }
	        
	        
	        adapter = new EditFolderSpinServerAdapter(ActivityEditFolder.this, android.R.layout.simple_spinner_item);
	        serverSpinner = (Spinner) findViewById(R.id.EditFolder_Spinner_Servers);
	        serverSpinner.setAdapter(adapter); // Set the custom adapter to the spinner
	       
	        serverSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
	            	Server server = adapter.getItem(position);	                
	                folder.setServer(server.getId());
	            }
	            public void onNothingSelected(AdapterView<?> adapter) {  }
	        });
	        
		}
		
		
	    public void onButtonClicked(View v){
	    	switch(v.getId()) {
		    	case R.id.EditFolder_Button_Select:
	    			SimpleFileDialog FolderChooseDialog =  new SimpleFileDialog(ActivityEditFolder.this, new SimpleFileDialog.SimpleFileDialogListener()
	  				{
	  					@Override
	  					public void onChosenDir(String chosenDir) 
	  					{
	  						// show toast
	  						Toast.makeText(ActivityEditFolder.this, R.string.confirm_folder_added  + chosenDir, Toast.LENGTH_LONG).show();
	  						
	  						folder.setPath(chosenDir);
	  						selectedFolderText.setText(chosenDir);
	  					}
	  				});
	      				
	      			FolderChooseDialog.chooseFile_or_Dir();
	    		break;
	    		
		    	case R.id.EditFolder_Button_Save:
		    		
		    		
						Toast.makeText(ActivityEditFolder.this, folder.getPath() +  " " + folder.getServer(), Toast.LENGTH_LONG).show();

	    		break;
	    	
	    		
	    	}
	    }

	}

