package ch.ffhs.privatecloudffhs.gui;

import ch.ffhs.privatecloudffhs.R;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.gui.adapter.FoldersListAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;

/**
 * ActivityFolders
 * 
 * Diese Activity wird verwendet um alle zu synchronisierenden aufzulisten. Ebenso k�nnen Order hinzugef�gt und gel�scht werden.
 
 * @author         Thierry Baumann Martin Müller
 */

@SuppressLint("NewApi")
public class ActivityFolders extends Activity implements MultiChoiceModeListener {

	private ListView listView = null;
	private Context context = null;
	private FoldersListAdapter adapter = null;
	private PrivateCloudDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folders);

		context = this;
		listView = (ListView) findViewById(R.id.Folders_List);

		db = new PrivateCloudDatabase(getApplicationContext());

		adapter = new FoldersListAdapter(context);
		refreshFolderList();

		listView.setAdapter(adapter);
		listView.setMultiChoiceModeListener(this);
		listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Folder selecteditem = adapter.getItem(position);

				Intent editFolders = new Intent(context,
						ActivityEditFolder.class);

				editFolders.putExtra("folderid", selecteditem.getId());
				startActivity(editFolders);
			}
		});

		db.closeDB();
	}

	private void refreshFolderList() {
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
	public void onItemCheckedStateChanged(ActionMode arg0, int arg1, long arg2,
			boolean arg3) {
		final int checkedCount = listView.getCheckedItemCount();
		arg0.setTitle(checkedCount + " Selected");
		adapter.toggleSelection(arg1);
	}

	public void onButtonClicked(View v) {
		switch (v.getId()) {
		case R.id.Folders_Button_Add:
			Intent editFolders = new Intent(this, ActivityEditFolder.class);
			startActivity(editFolders);
			break;

		case R.id.Folders_Button_cancel:
			this.finish();
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		refreshFolderList();
	}
}
