package ch.ffhs.privatecloudffhs.list;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.method.DateTimeKeyListener;

public class Folder {
	private String path;
	private Date lastsync;
	 
	public Folder(String path){
		this.path=path;
		this.lastsync = new Date();
	}

	public Folder(String path, Date lastsync){
		this.path = path;
		this.lastsync = lastsync;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getLastsync() {
		return lastsync;
	}
	
	public String getLastsyncFormated() {
		return new SimpleDateFormat("dd.MM.yyyy H:m:s").format(lastsync);
	}
	

	public void setLastsync(Date lastsync) {
		this.lastsync = lastsync;
	}	 
}
