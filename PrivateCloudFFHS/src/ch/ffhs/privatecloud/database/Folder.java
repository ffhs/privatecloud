package ch.ffhs.privatecloud.database;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.method.DateTimeKeyListener;

public class Folder {
	private String path;
	private int server;
	private String lastsync;
	 
	public Folder(String path, int server){
		this.path = path;
		this.server = server;
		this.lastsync = null;
	}

	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLastsync() {
		return lastsync;
	}

	public void setLastsync(String lastsync) {
		this.lastsync = lastsync;
	}
	
	public int getServer()
	{
		return server;
	}
	
	public void setServer(int server)
	{
		this.server = server;
	}
}
