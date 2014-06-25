package ch.ffhs.privatecloudffhs.test;

import java.util.Iterator;
import java.util.List;

import android.test.AndroidTestCase;
import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.PrivateCloudDatabase;
import ch.ffhs.privatecloudffhs.database.Server;
import ch.ffhs.privatecloudffhs.database.SyncFile;

public class PrivateCloudDatabaseTest extends AndroidTestCase {
	PrivateCloudDatabase privateCloudDatabase;
	public void testPrivateCloudDatabase() {
		privateCloudDatabase = new PrivateCloudDatabase(getContext());
		try{
			assertNotNull(privateCloudDatabase);
		}
		catch(Exception e){
		      fail("Should not have thrown any exception");
		}

		privateCloudDatabase.close();
	}
	public void testCreateServer() {
		privateCloudDatabase = new PrivateCloudDatabase(getContext());
		Server server = new Server("Servername for Junit");
		try{
			privateCloudDatabase.createServer(server);
		}
		catch(Exception e){
		      fail("Should not have thrown any exception");
		}
		
		privateCloudDatabase.close();
	}

	public void testUpdateServer() {
		privateCloudDatabase = new PrivateCloudDatabase(getContext());
		List<Server> servers = privateCloudDatabase.getAllServers();
		Iterator<Server> iterator = servers.iterator();
		Server server = null;
		while (iterator.hasNext()) {
			server = iterator.next();
			if(server.getServername().equals("Servername for Junit"))
			{
				server.setHostname("bla.bli.blu");
				try{
					privateCloudDatabase.updateServer(server);
				}
				catch(Exception e){
				      fail("Should not have thrown any exception");
				}
				
			}
		}
		privateCloudDatabase.close();
	}
	public void testCreateFolder() {
		privateCloudDatabase = new PrivateCloudDatabase(getContext());
		Folder folder = new Folder();
		folder.setPath("Testpath for junit");
		try{
			privateCloudDatabase.createFolder(folder);
		}
		catch(Exception e){
		      fail("Should not have thrown any exception");
		}
		
		privateCloudDatabase.close();
	}
	public void testGetAllFolders() {
		privateCloudDatabase = new PrivateCloudDatabase(getContext());
		List<Folder> folders = privateCloudDatabase.getAllFolders();
		assertNotNull(folders);
		privateCloudDatabase.close();
	}


	public void testCreateFile() {
		privateCloudDatabase = new PrivateCloudDatabase(getContext());
		SyncFile syncfile = new SyncFile(99, "filename for junit");
		try{
			privateCloudDatabase.createFile(syncfile);
		}
		catch(Exception e){
		      fail("Should not have thrown any exception");
		}
		privateCloudDatabase.close();
	}

	public void testUpdateFile() {
		privateCloudDatabase = new PrivateCloudDatabase(getContext());
		List<SyncFile> syncfiles = privateCloudDatabase.getAllFiles();
		Iterator<SyncFile> iterator = syncfiles.iterator();
		SyncFile syncfile = null;
		while (iterator.hasNext()) {
			syncfile = iterator.next();
			if ( syncfile.getPath().equals("filename for junit"))
			{
				SyncFile syncfile1 = privateCloudDatabase.getFile(syncfile.getId());
				syncfile1.setPath("filename2");
				try{
					privateCloudDatabase.updateFile(syncfile1);
				}
				catch(Exception e){
				      fail("Should not have thrown any exception");
				}
				
			}
		}
		
		privateCloudDatabase.close();
	}

	public void testDeleteFile() {
		privateCloudDatabase = new PrivateCloudDatabase(getContext());
		List<SyncFile> syncfiles = privateCloudDatabase.getAllFiles();
		Iterator<SyncFile> iterator = syncfiles.iterator();
		SyncFile syncfile = null;
		while (iterator.hasNext()) {
			syncfile = iterator.next();
			if ( syncfile.getPath().equals("filename for junit"))
			{
				try{
					privateCloudDatabase.deleteFile(syncfile.getId());
				}
				catch(Exception e){
				      fail("Should not have thrown any exception");
				}
				
			}
		}		
		
		privateCloudDatabase.close();
	}

	public void testDeleteFolder() {
		privateCloudDatabase = new PrivateCloudDatabase(getContext());
		List<Folder> folders = privateCloudDatabase.getAllFolders();
		Iterator<Folder> iterator = folders.iterator();
		Folder folder = null;
		while (iterator.hasNext()) {
			folder = iterator.next();
			if ( folder.getPath().equals("Testpath for junit"))
			{
				try{
					privateCloudDatabase.deleteFolder(folder.getId());
				}
				catch(Exception e){
				      fail("Should not have thrown any exception");
				}
				
			}
		}
		privateCloudDatabase.close();
	}
	public void testDeleteServer() {
		
		privateCloudDatabase = new PrivateCloudDatabase(getContext());
		List<Server> servers = privateCloudDatabase.getAllServers();
		Iterator<Server> iterator = servers.iterator();
		Server server = null;
		while (iterator.hasNext()) {
			server = iterator.next();
			if(server.getServername().equals("Servername for Junit"))
			{
				try{
					privateCloudDatabase.deleteServer(server.getId());
				}
				catch(Exception e){
				      fail("Should not have thrown any exception");
				}

			}
		}
		privateCloudDatabase.close();
	}
}
