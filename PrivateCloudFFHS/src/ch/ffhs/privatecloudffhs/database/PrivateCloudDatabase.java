package ch.ffhs.privatecloudffhs.database;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * PrivateCloudDatabase
 * 
 * PrivateCloud Datenbank-Anbindung
 * 
 * @author         Thierry Baumann
 */
public class PrivateCloudDatabase extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 2;

	// Database Name
	private static final String DATABASE_NAME = "privateCloud.db";

	// Table Names
	private static final String TABLE_SERVER = "servers";
	private static final String TABLE_FOLDER = "folders";
	private static final String TABLE_FILE = "files";

	// Common column names
	private static final String KEY_ID = "id";
	private static final String KEY_PATH = "path";

	// SERVERS Table - column names
	private static final String KEY_SERVERNAME = "servername";
	private static final String KEY_HOST = "hostname";
	private static final String KEY_USER = "user";
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_PORT = "port";
	private static final String KEY_PROTO = "proto";
	private static final String KEY_CERTPATH = "certpath";
	private static final String KEY_REMOTEROOT = "remoteroot";

	// FOLDERS Table - column names
	private static final String KEY_SERVER_ID = "server_id";
	private static final String KEY_LASTSYNC = "lastsync";

	// FILES Table - column names
	private static final String KEY_FOLDER_ID = "folder_id";
	private static final String KEY_LOCALCHECKSUM = "localchecksum";
	private static final String KEY_REMOTECHECKSUM = "remotechecksum";
	private static final String KEY_CONFLICT = "conflict";

	// Table Create Statements
	// FOLDER table create statement
	private static final String CREATE_TABLE_SERVER = "CREATE TABLE "
			+ TABLE_SERVER + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_SERVERNAME + " TEXT," + KEY_HOST + " TEXT," + KEY_USER
			+ " TEXT," + KEY_PASSWORD + " TEXT," + KEY_PORT + " INTEGER,"
			+ KEY_PROTO + " INTEGER," + KEY_CERTPATH + " TEXT,"
			+ KEY_REMOTEROOT + " TEXT)";

	private static final String CREATE_TABLE_FOLDER = "CREATE TABLE "
			+ TABLE_FOLDER + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_SERVER_ID + " INTEGER," + KEY_PATH + " TEXT," + KEY_LASTSYNC
			+ " TEXT)";

	private static final String CREATE_TABLE_FILE = "CREATE TABLE "
			+ TABLE_FILE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_FOLDER_ID + " INTEGER," + KEY_PATH + " TEXT,"
			+ KEY_LOCALCHECKSUM + " TEXT," + KEY_REMOTECHECKSUM + " TEXT,"
			+ KEY_CONFLICT + " INTEGER)";

	public PrivateCloudDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_SERVER);
		db.execSQL(CREATE_TABLE_FOLDER);
		db.execSQL(CREATE_TABLE_FILE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILE);

		// create new tables
		onCreate(db);
	}

	/*
	 * getting all Folders
	 */
	public List<Folder> getAllFolders() {
		List<Folder> folders = new ArrayList<Folder>();
		String selectQuery = "SELECT  * FROM " + TABLE_FOLDER;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Folder folder = new Folder(c.getString((c
						.getColumnIndex(KEY_PATH))), c.getInt((c
						.getColumnIndex(KEY_SERVER_ID))));
				folder.setLastsync(c.getString((c.getColumnIndex(KEY_LASTSYNC))));
				folder.setId(c.getInt((c.getColumnIndex(KEY_ID))));

				// adding to folders list
				folders.add(folder);
			} while (c.moveToNext());
		}

		return folders;
	}

	/*
	 * get single Folder
	 */
	public Folder getFolder(int folderId) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_FOLDER + " WHERE "
				+ KEY_ID + " = " + folderId;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.moveToFirst()) {
			Folder folder = new Folder(
					c.getString((c.getColumnIndex(KEY_PATH))), c.getInt((c
							.getColumnIndex(KEY_SERVER_ID))));

			folder.setLastsync(c.getString((c.getColumnIndex(KEY_LASTSYNC))));
			folder.setId(c.getInt((c.getColumnIndex(KEY_ID))));

			return folder;
		}

		return null;
	}

	/*
	 * get single Folder
	 */
	public Folder getLastSyncedFolder() {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_FOLDER + " ORDER BY "
				+ KEY_LASTSYNC + " DESC LIMIT 1";

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.moveToFirst()) {
			Folder folder = new Folder(
					c.getString((c.getColumnIndex(KEY_PATH))), c.getInt((c
							.getColumnIndex(KEY_SERVER_ID))));

			folder.setLastsync(c.getString((c.getColumnIndex(KEY_LASTSYNC))));
			folder.setId(c.getInt((c.getColumnIndex(KEY_ID))));

			return folder;
		}

		return null;
	}

	/*
	 * Creating a Folder
	 */
	public long createFolder(Folder folder) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SERVER_ID, folder.getServerId());
		values.put(KEY_PATH, folder.getPath());
		if (folder.getLastsync() == null) {
			values.put(KEY_LASTSYNC, "Sun Mar 06 11:28:16 IST 2011");
		} else {
			values.put(KEY_LASTSYNC, folder.getLastsync().toString());
		}
		// insert row
		long folderId = db.insert(TABLE_FOLDER, null, values);

		return folderId;
	}

	/*
	 * Updating a Folder
	 */
	public int updateFolder(Folder folder) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SERVER_ID, folder.getServerId());
		values.put(KEY_PATH, folder.getPath());
		values.put(KEY_LASTSYNC, folder.getLastsync().toString());
		values.put(KEY_ID, folder.getId());

		// updating row
		return db.update(TABLE_FOLDER, values, KEY_ID + " = ?",
				new String[] { String.valueOf(folder.getId()) });
	}

	/*
	 * Deleting a Folder
	 */
	public void deleteFolder(long folderId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FOLDER, KEY_ID + " = ?",
				new String[] { String.valueOf(folderId) });
	}

	/*
	 * getting all Files
	 */
	public List<SyncFile> getAllFiles() {
		List<SyncFile> files = new ArrayList<SyncFile>();
		String selectQuery = "SELECT  * FROM " + TABLE_FILE;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				SyncFile file = new SyncFile(c.getInt(c
						.getColumnIndex(KEY_FOLDER_ID)), c.getString(c
						.getColumnIndex(KEY_PATH)));

				file.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				file.setLocalCheckSum(c.getString(c
						.getColumnIndex(KEY_LOCALCHECKSUM)));
				file.setRemoteCheckSum(c.getString(c
						.getColumnIndex(KEY_REMOTECHECKSUM)));
				file.setConflict(c.getInt(c.getColumnIndex(KEY_CONFLICT)) == 1 ? true
						: false);

				// adding to files list
				files.add(file);
			} while (c.moveToNext());
		}

		return files;
	}

	/*
	 * get single File
	 */
	public SyncFile getFile(int fileId) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_FILE + " WHERE "
				+ KEY_ID + " = " + fileId;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.moveToFirst()) 
		{
			SyncFile file = new SyncFile(c.getInt(c
					.getColumnIndex(KEY_FOLDER_ID)), c.getString(c
					.getColumnIndex(KEY_PATH)));

			file.setId(c.getInt((c.getColumnIndex(KEY_ID))));
			file.setLocalCheckSum(c.getString(c
					.getColumnIndex(KEY_LOCALCHECKSUM)));
			file.setRemoteCheckSum(c.getString(c
					.getColumnIndex(KEY_REMOTECHECKSUM)));
			file.setConflict(c.getInt(c.getColumnIndex(KEY_CONFLICT)) == 1 ? true
					: false);

			return file;
		}

		return null;
	}

	/*
	 * get single File
	 */
	public SyncFile getFile(String path, int folderId) {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_FILE + " WHERE "
				+ KEY_FOLDER_ID + " = " + folderId + " AND " + KEY_PATH
				+ " = ? ";


		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, new String[] {path});

		if (c != null && c.moveToFirst()) {
			SyncFile file = new SyncFile(c.getInt(c
					.getColumnIndex(KEY_FOLDER_ID)), c.getString(c
					.getColumnIndex(KEY_PATH)));

			file.setId(c.getInt((c.getColumnIndex(KEY_ID))));
			file.setLocalCheckSum(c.getString(c
					.getColumnIndex(KEY_LOCALCHECKSUM)));
			file.setRemoteCheckSum(c.getString(c
					.getColumnIndex(KEY_REMOTECHECKSUM)));
			file.setConflict(c.getInt(c.getColumnIndex(KEY_CONFLICT)) == 1 ? true
					: false);

			return file;
		}

		return null;
	}

	/*
	 * Creating a File
	 */
	public long createFile(SyncFile file) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FOLDER_ID, file.getFolderId());
		values.put(KEY_PATH, file.getPath());
		values.put(KEY_LOCALCHECKSUM, file.getLocalCheckSum());
		values.put(KEY_REMOTECHECKSUM, file.getRemoteCheckSum());
		values.put(KEY_CONFLICT, (file.isConflict() ? 1 : 0));

		// insert row
		long fileId = db.insert(TABLE_FILE, null, values);

		return fileId;
	}

	/*
	 * Updating a File
	 */
	public int updateFile(SyncFile file) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FOLDER_ID, file.getFolderId());
		values.put(KEY_PATH, file.getPath());
		values.put(KEY_LOCALCHECKSUM, file.getLocalCheckSum());
		values.put(KEY_REMOTECHECKSUM, file.getRemoteCheckSum());
		values.put(KEY_CONFLICT, (file.isConflict() ? 1 : 0));

		// updating row
		return db.update(TABLE_FILE, values, KEY_ID + " = ?",
				new String[] { String.valueOf(file.getId()) });
	}

	/*
	 * Deleting a File
	 */
	public void deleteFile(long fileId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FILE, KEY_ID + " = ?",
				new String[] { String.valueOf(fileId) });
	}

	/*
	 * Deleting Files
	 */
	public void deleteFiles(long folderId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FILE, KEY_FOLDER_ID + " = ?",
				new String[] { String.valueOf(folderId) });
	}

	/*
	 * getting all Servers
	 */
	public List<Server> getAllServers() {
		List<Server> servers = new ArrayList<Server>();
		String selectQuery = "SELECT  * FROM " + TABLE_SERVER;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Server server = new Server(c.getString((c
						.getColumnIndex(KEY_SERVERNAME))), c.getString((c
						.getColumnIndex(KEY_HOST))));

				server.setUsername(c.getString((c.getColumnIndex(KEY_USER))));
				server.setPassword(c.getString((c.getColumnIndex(KEY_PASSWORD))));
				server.setPort(c.getInt((c.getColumnIndex(KEY_PORT))));
				server.setPassword(c.getString((c.getColumnIndex(KEY_PASSWORD))));
				server.setProto(c.getInt((c.getColumnIndex(KEY_PROTO))));
				server.setCertpath(c.getString((c.getColumnIndex(KEY_CERTPATH))));
				server.setRemoteroot(c.getString((c
						.getColumnIndex(KEY_REMOTEROOT))));
				server.setId(c.getInt((c.getColumnIndex(KEY_ID))));

				// adding to folders list
				servers.add(server);
			} while (c.moveToNext());
		}

		return servers;
	}

	/*
	 * get single Server
	 */
	public Server getServer(int serverId) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_SERVER + " WHERE "
				+ KEY_ID + " = " + serverId;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null && c.moveToFirst()) {
			Server server = new Server(c.getString((c
					.getColumnIndex(KEY_SERVERNAME))), c.getString((c
					.getColumnIndex(KEY_HOST))));

			server.setUsername(c.getString((c.getColumnIndex(KEY_USER))));
			server.setPassword(c.getString((c.getColumnIndex(KEY_PASSWORD))));
			server.setPort(c.getInt((c.getColumnIndex(KEY_PORT))));
			server.setPassword(c.getString((c.getColumnIndex(KEY_PASSWORD))));
			server.setProto(c.getInt((c.getColumnIndex(KEY_PROTO))));
			server.setCertpath(c.getString((c.getColumnIndex(KEY_CERTPATH))));
			server.setRemoteroot(c.getString((c.getColumnIndex(KEY_REMOTEROOT))));
			server.setId(c.getInt((c.getColumnIndex(KEY_ID))));

			return server;
		}

		return null;
	}

	/*
	 * Creating a Server
	 */
	public long createServer(Server server) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SERVERNAME, server.getServername());
		values.put(KEY_HOST, server.getHostname());
		values.put(KEY_USER, server.getUsername());
		values.put(KEY_PASSWORD, server.getPassword());
		values.put(KEY_PORT, server.getPort());
		values.put(KEY_PROTO, server.getProto());
		values.put(KEY_CERTPATH, server.getCertpath());
		values.put(KEY_REMOTEROOT, server.getRemoteroot());

		// insert row
		long serverId = db.insert(TABLE_SERVER, null, values);

		return serverId;
	}

	public int updateServer(Server server) {
		SQLiteDatabase db = this.getWritableDatabase();


//		if (server.getPassword() != null) {
//			String password = server.getPassword();
//		}
//		if (server.getCertpath() != null) {
//			String certPath = server.getCertpath();
//		}

		ContentValues values = new ContentValues();
		values.put(KEY_SERVERNAME, server.getServername());
		values.put(KEY_HOST, server.getHostname());
		values.put(KEY_USER, server.getUsername());
		values.put(KEY_PASSWORD, server.getPassword());
		values.put(KEY_PORT, server.getPort());
		values.put(KEY_PROTO, server.getProto());
		values.put(KEY_CERTPATH, server.getCertpath());
		values.put(KEY_REMOTEROOT, server.getRemoteroot());

		return db.update(TABLE_SERVER, values, KEY_ID + " = ?",
				new String[] { String.valueOf(server.getId()) });
	}

	public void deleteServer(long serverId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SERVER, KEY_ID + " = ?",
				new String[] { String.valueOf(serverId) });
	}

	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();

		if (db != null && db.isOpen())
			db.close();
	}

	public boolean isanyconflict() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT  * FROM " + TABLE_FILE + " WHERE "
				+ KEY_CONFLICT + " = " + 1;
		Cursor c = db.rawQuery(selectQuery, null);
		if (c.getCount() == 0) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * getting all Files
	 */
	public List<SyncFile> getAllConflicts() {
		List<SyncFile> files = new ArrayList<SyncFile>();
		String selectQuery = "SELECT  * FROM " + TABLE_FILE + " WHERE "
				+ KEY_CONFLICT + " = " + 1;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				SyncFile file = new SyncFile(c.getInt(c
						.getColumnIndex(KEY_FOLDER_ID)), c.getString(c
						.getColumnIndex(KEY_PATH)));

				file.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				file.setLocalCheckSum(c.getString(c
						.getColumnIndex(KEY_LOCALCHECKSUM)));
				file.setRemoteCheckSum(c.getString(c
						.getColumnIndex(KEY_REMOTECHECKSUM)));
				file.setConflict(c.getInt(c.getColumnIndex(KEY_CONFLICT)) == 1 ? true
						: false);
				Log.d("SYNC DB CONFILCT FILE", file.getPath());
				// adding to files list
				files.add(file);
			} while (c.moveToNext());
		}

		return files;
	}

}
