package ch.ffhs.privatecloudffhs.database;

public class SyncFile {
	private int folderId;
	private int id;
	private String path;
	private String localCheckSum;
	private String remoteCheckSum;
	private Boolean conflict;
	private int decision;  // 1 == local; 2 == remote
	
	public SyncFile(int folderid, String path){
		this.folderId = folderid;
		this.path = path;
		conflict = false;
	}

	public int getFolderId() {
		return folderId;
	}


	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	public String getPath(){
		return path;
	}
	
	public void setPath(String path){
		this.path = path;
	}

	public String getLocalCheckSum() {
		return localCheckSum;
	}


	public void setLocalCheckSum(String localCheckSum) {
		this.localCheckSum = localCheckSum;
	}


	public String getRemoteCheckSum() {
		return remoteCheckSum;
	}


	public void setRemoteCheckSum(String remoteCheckSum) {
		this.remoteCheckSum = remoteCheckSum;
	}
	

	public Boolean isConflict() {
		return conflict;
	}


	public void setConflict(Boolean conflict) {
		this.conflict = conflict;
	}	
	
	public int getDecision() {
		return decision;
	}


	public void setDecision(int decision) {
		this.decision = decision;
	}	
	

}
