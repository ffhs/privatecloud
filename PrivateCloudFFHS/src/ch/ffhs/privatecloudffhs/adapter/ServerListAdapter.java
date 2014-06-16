package ch.ffhs.privatecloudffhs.adapter;

import java.util.List;

import ch.ffhs.privatecloudffhs.R;
import ch.ffhs.privatecloudffhs.database.Server;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressLint("NewApi")
public class ServerListAdapter extends ArrayAdapter<Server>{	
	Context context;
	LayoutInflater inflater;
	List<Server> list;
	SparseBooleanArray mSelectedItemsIds;

	public ServerListAdapter(Context context) {
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
			convertView = inflater.inflate(R.layout.list_servers, null);
			
			holder.servername = (TextView) convertView.findViewById(R.id.Servers_List_Name);
			holder.hostname= (TextView) convertView.findViewById(R.id.Server_List_Hostname);
			
			convertView.setTag(holder);
		} 
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.servername.setText(list.get(position).getServername());
		holder.hostname.setText(list.get(position).getHostname());

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
	public void refreshList(List<Server> list) {
		this.list = list;
		clear();
		addAll(this.list);
		notifyDataSetChanged();
	}
	

	private class ViewHolder {
		TextView servername;
		TextView hostname;
	}
}
