package ch.ffhs.privatecloudffhs;

import ch.ffhs.privatecloudffhs.adapter.FoldersListAdapter;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.util.SimpleFileDialog;
import ch.ffhs.privatecloudffhs.util.SystemUiHider;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.app.Activity;
import android.os.Bundle;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */

@SuppressLint("NewApi")
public class Folders extends Activity  implements MultiChoiceModeListener{

	 private ListView listView = null;
	 private Context context = null;
	 private FoldersListAdapter adapter = null;
	 private PrivateCloudDatabase db;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folders);

		context = this;
		listView = (ListView) findViewById(R.id.Folders_List);
		
		db = new PrivateCloudDatabase(getApplicationContext());
		
		adapter	= new FoldersListAdapter(context);
        refreshFolderList();
        
        listView.setAdapter(adapter);
        listView.setMultiChoiceModeListener(this);
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE_MODAL);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            	Folder selecteditem = adapter.getItem(position);

            	Intent editFolders = new Intent(context, ActivityEditFolder.class);
                
            	editFolders.putExtra("folderid", selecteditem.getId());
  				startActivity(editFolders);
            }
        });
        
        db.closeDB();
	}
	
	private void refreshFolderList()
	{
        adapter.refreshList(db.getAllFolders());
        db.closeDB();
	}

	@Override
	public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
		switch (arg1.getItemId()) {
			case R.id.Folders_Liste_Delete:
				SparseBooleanArray selected = adapter.getSelectedIds();
				for (int i = (selected.size() - 1); i >= 0; i--) {
					if (selected.valueAt(i)) {
						Folder selecteditem = adapter.getItem(selected.keyAt(i));
						adapter.remove(selecteditem);
						db.deleteFolder(selecteditem.getId());
					}
				}
				
		        db.closeDB();
				// Close CAB
				arg0.finish();
			return true;
			
			default:
				
			return false;
		}	
	  }
	@Override
	public boolean onCreateActionMode(ActionMode arg0, Menu arg1) {
		arg0.getMenuInflater().inflate(R.menu.folders, arg1);
		return true;
		
	}
	@Override
	public void onDestroyActionMode(ActionMode arg0) {
		adapter.removeSelection();
	}
	
	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
		return false;
	}
	
	@Override
	public void onItemCheckedStateChanged(ActionMode arg0, int arg1, long arg2, boolean arg3) {
		final int checkedCount = listView.getCheckedItemCount();
		arg0.setTitle(checkedCount + " Selected");
		adapter.toggleSelection(arg1);
	}

    public void onButtonClicked(View v){
    	switch(v.getId()) {
    		case R.id.Folders_Button_Add:
    			Intent editFolders = new Intent(this,ActivityEditFolder.class);
        		startActivity(editFolders);    				
    		break;
    		
    		case R.id.Folders_Button_cancel:
    			this.finish();
    		break;
    	}
    }
    
    public void onResume() {
        super.onResume();

        refreshFolderList();
    }
}

