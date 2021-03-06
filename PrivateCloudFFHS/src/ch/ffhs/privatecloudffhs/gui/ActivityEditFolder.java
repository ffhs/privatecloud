package ch.ffhs.privatecloudffhs.gui;

import ch.ffhs.privatecloudffhs.R;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import ch.ffhs.privatecloudffhs.gui.adapter.EditFolderSpinServerAdapter;
import ch.ffhs.privatecloudffhs.gui.util.SimpleFileDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * ActivityEditFolder
 * 
 * Diese Activity wird verwendet um einen lokalen Order f�r die synchronisation auszuw�hlen. Zus�tzlich kann auch der Server ausgew�hlt werden.
 
 * @author         Thierry Baumann
 */
public class ActivityEditFolder extends Activity {
	private PrivateCloudDatabase db;
	private Folder folder;
	private Spinner serverSpinner;
	private EditFolderSpinServerAdapter adapter;
	private TextView selectedFolderText;
	private int folderId;
	private Context context;
	private Boolean serverChanged;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Bundle intentExtras = intent.getExtras();

		setContentView(R.layout.activity_edit_folder);
		context = this;

		db = new PrivateCloudDatabase(context);

		folder = new Folder();
		selectedFolderText = (TextView) findViewById(R.id.EditFolder_Text_Folder);

		if (intentExtras != null) {
			folderId = intentExtras.getInt("folderid");
			if (folderId != 0) {
				// Load from DB
				folder = db.getFolder(folderId);
			}
		}

		adapter = new EditFolderSpinServerAdapter(ActivityEditFolder.this,
				android.R.layout.simple_spinner_item);
		adapter.refreshList(db.getAllServers());

		serverSpinner = (Spinner) findViewById(R.id.EditFolder_Spinner_Servers);
		serverSpinner.setAdapter(adapter); // Set the custom adapter to the
											// spinner

		serverSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				Server server = adapter.getItem(position);
				int newServerId = server.getId();

				if (folder.getServerId() != newServerId) {
					serverChanged = true;
				} else {
					serverChanged = false;
				}

				folder.setServerId(newServerId);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapter) {
			}
		});

		if (folderId != 0) {
			serverSpinner.setSelection(adapter.getPosition(adapter
					.getServerById(folder.getServerId())));
			selectedFolderText.setText(folder.getPath());
		}

		db.closeDB();
	}

	public void onButtonClicked(View v) {
		switch (v.getId()) {
		case R.id.EditFolder_Button_Select:
			SimpleFileDialog FolderChooseDialog = new SimpleFileDialog(
					ActivityEditFolder.this,
					new SimpleFileDialog.SimpleFileDialogListener() {
						@Override
						public void onChosenDir(String chosenDir) {
							folder.setPath(chosenDir);
							selectedFolderText.setText(chosenDir);
						}
					});

			FolderChooseDialog.chooseFile_or_Dir();
			break;

		case R.id.EditFolder_Button_Save:
			if (folder.getPath() == null) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				// set title
				alertDialogBuilder.setTitle(R.string.error);

				// set dialog message
				alertDialogBuilder
						.setMessage(R.string.error_folder_path)
						.setCancelable(false)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, just close
										// the dialog box and do nothing
										dialog.cancel();
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			} else {
				if (serverChanged) {
					db.deleteFiles(folder.getId());
				}

				if (folderId == 0) {
					db.createFolder(folder);
				} else {
					db.updateFolder(folder);
				}

				db.closeDB();
				this.finish();
			}
			break;

		case R.id.EditFolder_Button_Cancel:
			this.finish();
			break;

		}
	}

}
