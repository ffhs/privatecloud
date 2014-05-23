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

public class ServerListAdapter extends ArrayAdapter<Server>{	
	Context context;
	LayoutInflater inflater;
	List<Server> list;
	private SparseBooleanArray mSelectedItemsIds;
	
	public ServerListAdapter(Context context, List<Server> list) {
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
			convertView = inflater.inflate(R.layout.list_servers, null);
			
			holder.servername = (TextView) convertView.findViewById(R.id.Servers_List_Name);
			holder.hostname= (TextView) convertView.findViewById(R.id.Server_List_Hostname);
			
			convertView.setTag(holder);
		} 
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.servername.setText(list.get(position).getservername());
		holder.hostname.setText(list.get(position).gethostname());

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
	
	
	
	
	
	
	private class ViewHolder {
		TextView servername;
		TextView hostname;
	}
}
