package ch.ffhs.privatecloud.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PrivateCloudDatabase extends SQLiteOpenHelper {

	// Logcat tag
    private static final String LOG = "DatabaseHelper";
 
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
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
    private static final String CREATE_TABLE_SERVER = "CREATE TABLE "+ TABLE_SERVER + "(" 
    		+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_SERVERNAME + " TEXT," + KEY_HOST + " TEXT," + KEY_USER + " TEXT," 
    		+ KEY_PASSWORD + " TEXT," + KEY_PORT + " INTEGER," + KEY_PROTO + " INTEGER,"
            + KEY_CERTPATH + " TEXT," + KEY_REMOTEROOT + " TEXT)";
    
    private static final String CREATE_TABLE_FOLDER = "CREATE TABLE "+ TABLE_FOLDER + "(" 
    		+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_SERVER_ID + " INTEGER,"
    		+ KEY_PATH + " TEXT," + KEY_LASTSYNC + " TEXT)";
   
    private static final String CREATE_TABLE_FILE = "CREATE TABLE "+ TABLE_FILE + "(" 
    		+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_FOLDER_ID + " INTEGER,"
    		+ KEY_PATH + " TEXT," + KEY_LOCALCHECKSUM + " TEXT," + KEY_REMOTECHECKSUM + " TEXT,"
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
    
    public List<Folder> getAllFolders() {
        List<Folder> folders = new ArrayList<Folder>();
        String selectQuery = "SELECT  * FROM " + TABLE_FOLDER;
     
        Log.e(LOG, selectQuery);
     
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Folder folder = new Folder(c.getString((c.getColumnIndex(KEY_PATH))), c.getInt((c.getColumnIndex(KEY_SERVER_ID))));
                folder.setLastsync(c.getString((c.getColumnIndex(KEY_LASTSYNC))));
                
                // adding to folders list
                folders.add(folder);
            } while (c.moveToNext());
        }
     
        return folders;
    }
    
    /*
     * Creating a Folder
     */
    public long createFolder(Folder folder) {
        SQLiteDatabase db = this.getWritableDatabase();
     
        ContentValues values = new ContentValues();
        values.put(KEY_SERVER_ID, folder.getServer());
        values.put(KEY_PATH, folder.getPath());
        values.put(KEY_LASTSYNC, folder.getLastsync());
        
        // insert row
        long folder_id = db.insert(TABLE_FOLDER, null, values);
     
        return folder_id;
    }
}
