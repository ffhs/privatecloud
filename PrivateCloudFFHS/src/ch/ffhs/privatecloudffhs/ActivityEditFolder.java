package ch.ffhs.privatecloudffhs;

import java.util.List;

import ch.ffhs.privatecloudffhs.adapter.EditFolderSpinServerAdapter;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import ch.ffhs.privatecloudffhs.util.SimpleFileDialog;
import ch.ffhs.privatecloudffhs.util.SystemUiHider;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AnalogClock;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ActivityEditFolder extends Activity {
		PrivateCloudDatabase db;
		Folder folder;
		Spinner serverSpinner;
	    EditFolderSpinServerAdapter adapter;
        TextView selectedFolderText;
        int folderId;
        Context context;
        private Boolean serverChanged ;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Intent intent = getIntent();
	        Bundle intentExtras = intent.getExtras();

	        setContentView(R.layout.activity_edit_folder);	        
			context = this;

			db = new PrivateCloudDatabase(context);

			folder = new Folder();
	        selectedFolderText = (TextView) findViewById(R.id.EditFolder_Text_Folder);	        

	        if(intentExtras != null)
	        {
	        	folderId = (int)intentExtras.getInt("folderid");
	        	if(folderId != 0)
	        	{
	        		//Load from DB
	        		folder = db.getFolder(folderId);
	        	}
	        }
	        
	        adapter = new EditFolderSpinServerAdapter(ActivityEditFolder.this, android.R.layout.simple_spinner_item);
	        adapter.refreshList(db.getAllServers());
	        
	        serverSpinner = (Spinner) findViewById(R.id.EditFolder_Spinner_Servers);
	        serverSpinner.setAdapter(adapter); // Set the custom adapter to the spinner


	        serverSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
	            	Server server = adapter.getItem(position);	                
	            	int newServerId = server.getId();
	            	
	            	if(folder.getServerId() != newServerId)
	            	{
	            		serverChanged = true;
	            	}
	            	else
	            	{
	            		serverChanged = false;
	            	}
	            	
	                folder.setServerId(newServerId);
	            }
	            public void onNothingSelected(AdapterView<?> adapter) {  }
	        });
	        
	        if(folderId != 0)
	        {
		        serverSpinner.setSelection(adapter.getPosition(adapter.getServerById(folder.getServerId())));
		        selectedFolderText.setText(folder.getPath());
	        }
	        
	        db.closeDB();
		}
		
		
	    public void onButtonClicked(View v){
	    	switch(v.getId()) {
		    	case R.id.EditFolder_Button_Select:
	    			SimpleFileDialog FolderChooseDialog =  new SimpleFileDialog(ActivityEditFolder.this, new SimpleFileDialog.SimpleFileDialogListener()
	  				{
	  					@Override
	  					public void onChosenDir(String chosenDir) 
	  					{	  						
	  						folder.setPath(chosenDir);
	  						selectedFolderText.setText(chosenDir);
	  					}
	  				});
	      				
	      			FolderChooseDialog.chooseFile_or_Dir();
	    		break;
	    		
		    	case R.id.EditFolder_Button_Save:
		    		if(folder.getPath() == null)
		    		{
		    			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		    			 
						// set title
						alertDialogBuilder.setTitle(R.string.error);
			 
						// set dialog message
						alertDialogBuilder
							.setMessage(R.string.error_folder_path)
							.setCancelable(false)
							.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									// if this button is clicked, just close
									// the dialog box and do nothing
									dialog.cancel();
								}
							}
						);				
						
		 				// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();
		 
						// show it
						alertDialog.show();
		    		}
		    		else
		    		{
		    			if(serverChanged)
		    			{
		    				db.deleteFiles(folder.getId());
		    			}
		    			
			    		if(folderId == 0)
			    		{
			    			db.createFolder(folder);	
			    		}
			    		else
			    		{
			    			db.updateFolder(folder);
			    		}
			    		
				        db.closeDB();
		    			this.finish();		    			
		    		}
	    		break;
	    		
		    	case R.id.EditFolder_Button_Cancel:
	    			this.finish();
	    		break;
	    	
	    		
	    	}
	    }

	}

