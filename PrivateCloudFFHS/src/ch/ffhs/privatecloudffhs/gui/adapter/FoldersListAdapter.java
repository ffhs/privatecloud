package ch.ffhs.privatecloudffhs.gui.adapter;

import java.util.List;

import ch.ffhs.privatecloudffhs.R;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * FoldersListAdapter
 * 
 * Listen Adapter für zu synchronisierenden Ordner
 *  
 * @author         Thierry Baumann
 */
@SuppressLint("NewApi")
public class FoldersListAdapter extends ArrayAdapter<Folder> {
	Context context;
	LayoutInflater inflater;
	List<Folder> list;
	SparseBooleanArray mSelectedItemsIds;
	PrivateCloudDatabase db;

	public FoldersListAdapter(Context context) {
		super(context, 0);

		mSelectedItemsIds = new SparseBooleanArray();
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_folders, null);

			holder.path = (TextView) convertView
					.findViewById(R.id.Folders_List_Path);
			holder.lastsync = (TextView) convertView
					.findViewById(R.id.Folders_List_LasySync);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.path.setText(list.get(position).getPath());
		holder.lastsync.setText(list.get(position).getLastsync().toString());

		return convertView;
	}

	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);
		notifyDataSetChanged();
	}

	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}

	public void addFolder(Folder folder) {
		this.list.add(folder);
		add(folder);

		notifyDataSetChanged();
	}

	public void refreshList(List<Folder> list) {
		this.list = list;

		clear();
		addAll(this.list);

		notifyDataSetChanged();
	}

	private class ViewHolder {
		TextView path;
		TextView lastsync;
	}
}
