package ch.ffhs.privatecloudffhs.list;

import java.util.List;

import ch.ffhs.privatecloudffhs.R;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FoldersListAdapter extends ArrayAdapter<Folder>{	
	Context context;
	LayoutInflater inflater;
	List<Folder> list;
	private SparseBooleanArray mSelectedItemsIds;
	
	public FoldersListAdapter(Context context, List<Folder> list) {
		super(context, 0, list);
		mSelectedItemsIds = new SparseBooleanArray();
		this.context = context;
		this.list = list;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_folders, null);
			
			holder.path = (TextView) convertView.findViewById(R.id.Folders_List_Path);
			holder.lastsync= (TextView) convertView.findViewById(R.id.Folders_List_LasySync);
			
			convertView.setTag(holder);
		} 
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.path.setText(list.get(position).getPath());
		holder.lastsync.setText(list.get(position).getLastsyncFormated());

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
		list.add(folder);
		notifyDataSetChanged();
	}
	
	
	
	
	private class ViewHolder {
		TextView path;
		TextView lastsync;
	}
}
