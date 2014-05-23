package ch.ffhs.privatecloudffhs.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;

@SuppressLint("NewApi")
public class EditFolderSpinServerAdapter extends ArrayAdapter<Server>{

    Context context;
	List<Server> list;
	PrivateCloudDatabase db;

	public EditFolderSpinServerAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
        
        db = new PrivateCloudDatabase(context);	
		list = db.getAllServers();

		super.addAll(list);
        
    }

    public int getCount(){
       return list.size();
    }

    public Server getItem(int position){
       return list.get(position);
    }

    public long getItemId(int position){
       return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        
        label.setText(list.get(position).getServername());
        
        return label;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        
        label.setText(list.get(position).getServername());

        return label;
    }
}