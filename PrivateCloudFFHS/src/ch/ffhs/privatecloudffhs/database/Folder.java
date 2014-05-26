package ch.ffhs.privatecloudffhs.database;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.method.DateTimeKeyListener;

public class Folder {
	private String path;
	private int serverId;
	private String lastsync;
	private int id;
	 
	public Folder(){
	}

	
	public Folder(String path, int server){
		this.path = path;
		this.serverId = server;
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
	
	public int getServerId()
	{
		return serverId;
	}
	
	public void setServerId(int serverId)
	{
		this.serverId = serverId;
	}
	

	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id= id;
	}
}
