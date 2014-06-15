package ch.ffhs.privatecloudffhs.database;

public class Server {
	private String servername;
	private String hostname;
	private String username;
	private String password;
	private String remoteroot;
	private String certpath;
	private int port;
	private int proto;
	private int id;
	
	public Server(String servername){
		this.servername=servername;
		this.hostname = "No Hostname defined";
	}

	public Server(String servername, String hostname){
		this.servername = servername;
		this.hostname = hostname;
	}
	
	public String getServername() {
		return servername;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemoteroot() {
		return remoteroot;
	}

	public void setRemoteroot(String remoteroot) {
		this.remoteroot = remoteroot;
	}

	public String getCertpath() {
		return certpath;
	}

	public void setCertpath(String certpath) {
		this.certpath = certpath;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getProto() {
		return proto;
	}

	public void setProto(int proto) {
		this.proto = proto;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
