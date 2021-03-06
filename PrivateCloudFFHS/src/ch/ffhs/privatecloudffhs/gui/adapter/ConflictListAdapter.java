package ch.ffhs.privatecloudffhs.gui.adapter;

import java.util.List;

import ch.ffhs.privatecloudffhs.R;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.SyncFile;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

/**
 * ConflictListAdapter
 * 
 * Listen Adapter f�r Dateien mit Konflikten
 *  
 * @author         Thierry Baumann
 */
@SuppressLint("NewApi")
public class ConflictListAdapter extends ArrayAdapter<SyncFile> {
	Context context;
	LayoutInflater inflater;
	List<SyncFile> list;
	SparseBooleanArray mSelectedItemsIds;
	PrivateCloudDatabase db;

	public ConflictListAdapter(Context context) {
		super(context, 0);
		db = new PrivateCloudDatabase(context);

		mSelectedItemsIds = new SparseBooleanArray();
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		SyncFile syncFile = list.get(position);

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_conflict, null);

			holder.path = (TextView) convertView
					.findViewById(R.id.Conflict_List_Path);
			holder.server = (TextView) convertView
					.findViewById(R.id.Conflict_List_Server);
			holder.decision = (Switch) convertView
					.findViewById(R.id.Conflict_List_Decision);
			holder.decision.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					SyncFile file = (SyncFile) buttonView.getTag();
					// local file chosen
					if (isChecked) {
						file.setDecision(1);
					} else {
						file.setDecision(2);
					}
					Log.d("jada",isChecked+"da");
				}
			
			});
			
			/*
			// if switch is toggled, update the decision
			holder.decision.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Switch cb = (Switch) v;
					SyncFile file = (SyncFile) cb.getTag();
					// local file chosen
					if (cb.isChecked()) {
						file.setDecision(1);
					} else {
						file.setDecision(2);
					}
				}
			});
			*/
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		syncFile.setDecision(1);

		holder.path.setText(list.get(position).getPath());

		Folder folder = db.getFolder(list.get(position).getFolderId());
		holder.server.setText(db.getServer(folder.getServerId())
				.getServername());

		holder.decision.setTag(syncFile);
		holder.decision.setChecked(true);

		return convertView;
	}

	/*
	 * public void toggleSelection(int position) { selectView(position,
	 * !mSelectedItemsIds.get(position)); }
	 * 
	 * public void selectView(int position, boolean value) { if (value)
	 * mSelectedItemsIds.put(position, value); else
	 * mSelectedItemsIds.delete(position); notifyDataSetChanged(); }
	 * 
	 * public void removeSelection() { mSelectedItemsIds = new
	 * SparseBooleanArray(); notifyDataSetChanged(); }
	 * 
	 * public SparseBooleanArray getSelectedIds() { return mSelectedItemsIds; }
	 */

	public void refreshList(List<SyncFile> list) {
		this.list = list;

		clear();
		addAll(this.list);

		notifyDataSetChanged();
	}

	public void saveList() {
		for (SyncFile file : list) {
			if (file.getDecision() == 1) {
				file.setRemoteCheckSum(null);
			} else {
				file.setLocalCheckSum(null);
				Log.d("jada","OK");
			}

			file.setConflict(false);
			
			db.updateFile(file);	
		}
		db.close();
	}

	private class ViewHolder {
		public Switch decision;
		TextView path;
		TextView server;
	}
}
