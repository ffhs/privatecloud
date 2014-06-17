package ch.ffhs.privatecloudffhs.gui;

import ch.ffhs.privatecloudffhs.R;
import ch.ffhs.privatecloudffhs.R.id;
import ch.ffhs.privatecloudffhs.R.layout;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.gui.adapter.ConflictListAdapter;
import ch.ffhs.privatecloudffhs.gui.adapter.FoldersListAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.os.Build;

public class ActivityConflict extends Activity {
	 private ListView listView = null;
	 private Context context = null;
	 private ConflictListAdapter adapter = null;
	 private PrivateCloudDatabase db;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conflict);
		
		context = this;
		listView = (ListView) findViewById(R.id.Conflict_List);
		
		db = new PrivateCloudDatabase(getApplicationContext());
		
		adapter	= new ConflictListAdapter(context);
        refreshConflictList();
        
        listView.setAdapter(adapter);        
        
        db.closeDB();
	}
	
	
	private void refreshConflictList()
	{
        adapter.refreshList(db.getAllConflicts());
        db.closeDB();
	}
	
	  public void onButtonClicked(View v){
	    	switch(v.getId()) {
	    		case R.id.Conflict_Button_Save:
	    			adapter.saveList();
	    			this.finish();
	    		break;
	    		
	    		case R.id.Conflict_Button_Cancel:
	    			this.finish();
	    		break;
	    	
	    	}
	    }

}