package ch.ffhs.privatecloudffhs.list;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.method.DateTimeKeyListener;

public class Server {
	private String servername;
	private String hostname;
	 
	public Server(String servername){
		this.servername=servername;
		this.hostname = "No Hostname defined";
	}

	public Server(String servername, String hostname){
		this.servername = servername;
		this.hostname = hostname;
	}
	
	public String getservername() {
		return servername;
	}

	public void setservername(String servername) {
		this.servername = servername;
	}

	public String gethostname() {
		return hostname;
	}
	

	

	public void sethostname(String hostname) {
		this.hostname = hostname;
	}	 
}
