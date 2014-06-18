package ch.ffhs.privatecloudffhs.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class Folder {
	private String path;
	private int serverId;
	private String lastsync;
	private int id;

	private SimpleDateFormat sdf;

	public Folder(){
		this(null, 0);
	}

	
	public Folder(String path, int server){
		this.path = path;
		this.serverId = server;
		this.sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		this.lastsync = "01.01.1970 00:00";
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
	
	public void setLastsync(Date date) {
		this.lastsync = sdf.format(date);
	}
	
	public void setLastsync(String lastsync) {
		try {
			setLastsync(sdf.parse(lastsync));
		} catch (ParseException e) {
			e.printStackTrace();
		}
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
