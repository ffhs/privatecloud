package ch.ffhs.privatecloudffhs.gui;

import ch.ffhs.privatecloudffhs.R;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.gui.adapter.ConflictListAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * ActivityConflict
 * 
 * Diese Activity wird verwendet um mit Datei-Konflikten umzugehen. Der Benutzer kann w�hlen ob er den Lokalen oder Remote Stand der Datei auf seinem Ger�t haben m�chte. 
 * Die ausgew�hlte Option wird bei der n�chsten Synchronisation ber�cksichtigt. 
 
 * @author         Thierry Baumann
 */
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

		adapter = new ConflictListAdapter(context);
		refreshConflictList();

		listView.setAdapter(adapter);

		db.closeDB();
	}

	private void refreshConflictList() {
		adapter.refreshList(db.getAllConflicts());
		db.closeDB();
	}

	public void onButtonClicked(View v) {
		switch (v.getId()) {
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
