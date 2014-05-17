package ch.ffhs.privatecloudffhs;

import ch.ffhs.privatecloudffhs.list.Folder;
import ch.ffhs.privatecloudffhs.list.FoldersListAdapter;
import ch.ffhs.privatecloudffhs.util.SystemUiHider;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

	 ListView listView=null;
	 Context contex=null;
	 FoldersListAdapter adapter=null;
	 private List<Folder> list=new ArrayList<Folder>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folders);

		contex=this;
		listView = (ListView) findViewById(R.id.Folders_List);
		
		// load some dummy data
        for(int index=0; index< 4; index++){
        	Folder test = new Folder("testpath" + index);
        	list.add(test);
        }
        
        adapter	= new FoldersListAdapter(contex, list);
        listView.setAdapter(adapter);
        listView.setMultiChoiceModeListener(this);
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE_MODAL);
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
					}
				}
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
  				SimpleFileDialog FolderChooseDialog =  new SimpleFileDialog(Folders.this, "FolderChoose", new SimpleFileDialog.SimpleFileDialogListener()
  				{
  					@Override
  					public void onChosenDir(String chosenDir) 
  					{
  						// show toast
  						Toast.makeText(Folders.this, R.string.confirm_folder_added  + chosenDir, Toast.LENGTH_LONG).show();
  						
  						// add selected folder to list
  						adapter.addFolder(new Folder(chosenDir));
  					}
  				});
      				
      			FolderChooseDialog.chooseFile_or_Dir();
    		break;
    		case R.id.Folders_Button_cancel:
    			this.finish();
    		break;
    	}
    }
}

