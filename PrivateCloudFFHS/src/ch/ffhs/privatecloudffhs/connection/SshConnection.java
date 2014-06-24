package ch.ffhs.privatecloudffhs.connection;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import ch.ffhs.privatecloudffhs.database.Folder;
import ch.ffhs.privatecloudffhs.database.Server;
import ch.ffhs.privatecloudffhs.database.SyncFile;
import ch.ffhs.privatecloudffhs.sync.SyncConnection;

import com.jcraft.jsch.*;

import android.util.Log;

/**
 * SshConnection
 * 
 * Diese Klasse dient als Basis aller SSH basierten Verbindungen. 
 * 
 * @author         Thierry Baumann
 */
public class SshConnection implements SyncConnection {
	protected ChannelSftp channelSftp = null;
	protected Session session = null;
	protected String remoteDir;
	protected Server server;
	protected Folder folder;
	protected Boolean connected = false;

	protected Boolean error = false;
	protected String errorMsg;

	public SshConnection(Server server, Folder folder) {
		// TODO Auto-generated constructor stub
		this.server = server;
		this.folder = folder;
	}

	@Override
	public Boolean isConnected() {
		return connected;
	}

	private String sendCommand(String command) {
		StringBuilder outputBuffer = new StringBuilder();
		if (session.isConnected()) {
			try {

				Channel channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(command);
				channel.connect();
				InputStream commandOutput = channel.getInputStream();
				int readByte = commandOutput.read();

				while (readByte != 0xffffffff) {
					outputBuffer.append((char) readByte);
					readByte = commandOutput.read();
				}

				channel.disconnect();
			} catch (IOException ioX) {
				Log.d("ERROR SYNCTEST2", ioX.getMessage());
				return null;
			} catch (JSchException jschX) {
				Log.d("ERROR SYNCTEST1", jschX.getMessage());
				return null;
			}
		} else {
			Log.d("jada", "Connection down");
			return "Connection down.";

		}
		return outputBuffer.toString();
	}

	@Override
	public SyncFile uploadFile(File file, SyncFile syncFile) {
		try {
			channelSftp.put(new FileInputStream(file), file.getName());

			// get remote checksum from new uploaded file
			syncFile.setRemoteCheckSum(getRemoteCheckSum(file.getPath()));

			return syncFile;
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("SYNC", "ERROR UPLOAD");
		}

		return null;
	}

	@Override
	public SyncFile downloadFile(File file, SyncFile syncFile) {
		try {
			byte[] buffer = new byte[1024];
			BufferedInputStream bis = new BufferedInputStream(
					channelSftp.get(remoteDir + file.getPath()));
			File newFile = new File(file.getPath());

			OutputStream os = new FileOutputStream(newFile);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			int readCount;
			// System.out.println("Getting: " + theLine);
			while ((readCount = bis.read(buffer)) > 0) {
				bos.write(buffer, 0, readCount);
			}

			bis.close();
			bos.close();

			// get remote checksum from downloaded file
			syncFile.setRemoteCheckSum(getRemoteCheckSum(file.getPath()));

			return syncFile;

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	public void mkDir(String directory) {
		try {
			channelSftp.mkdir(directory);

		} catch (SftpException e) {
			// folder exists on remote dir
			// e.printStackTrace();
		}
	}

	@Override
	public String getRemoteCheckSum(String filePath) {
		String result = "";

		Log.d("jada", "Getting remote checksum");
		StringBuilder command = new StringBuilder();
		command.append("md5sum").append(" ");
		command.append("\"").append(remoteDir).append(filePath).append("\"");

		try {
			result = sendCommand(command.toString());
		} catch (Exception e) {
			System.out.println(e);
		}

		Log.d("jada", "Got remote checksum");
		if (result.length() > 32)
			return result.substring(0, 32);
		return result;
	}

	@Override
	public void initFolderSync(String directory) {
		try {
			channelSftp.cd(directory);
		} catch (SftpException e) {
			e.printStackTrace();
		}
	}

	public Vector listRemoteDir(String path) {
		try {
			return channelSftp.ls(path);
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	protected void checkremotedir() {
		try {
			channelSftp.cd("/");
			String completepath = server.getRemoteroot() + folder.getPath();
			Log.d("jada", "Complete path: " + completepath);
			String[] folders = completepath.split("/");
			for (String folder : folders) {
				if (folder.length() > 0) {
					try {
						channelSftp.cd(folder);
					} catch (SftpException e) {
						Log.d("jada", "creating dir: " + folder);
						channelSftp.mkdir(folder);
						channelSftp.cd(folder);
					}
				}
			}
		} catch (SftpException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	protected void setError(boolean error) {
		this.error = error;
	}

	public Boolean isError() {
		return error;
	}

	protected void setErrorMsa(String errorMsg) {
		String needle = "Exception: ";
		int subIn = errorMsg.indexOf(needle);

		if (subIn > 0) {
			errorMsg = errorMsg.substring(subIn + needle.length());
		}

		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}