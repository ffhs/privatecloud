package ch.ffhs.privatecloudffhs.connection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.res.AssetManager;
import android.content.Context;
import android.util.Log;

import com.jcraft.jsch.*;
public class RsaKeyGen {
	Context context;
	public RsaKeyGen(Context context) {
		super();
		this.context = context;
	}
	public String GenerateMockKey(String keysmallname){
		String appRootDir = context.getApplicationInfo().dataDir;
	    String rsakeypath = appRootDir + "/id_rsa";
		InputStream in = null;
        OutputStream out = null;
        try {
          AssetManager mngr = context.getAssets();
          in = mngr.open("id_rsa");
          new File(rsakeypath+keysmallname).createNewFile();
          out = new FileOutputStream(rsakeypath+keysmallname);
          copyFile(in, out);
          in.close();
          in = null;
          out.flush();
          out.close();
          out = null;
          in = mngr.open("id_rsa.pub");
          new File(rsakeypath+keysmallname+".pub").createNewFile();
          out = new FileOutputStream(rsakeypath+keysmallname+".pub");
          copyFile(in, out);
          in.close();
          in = null;
          out.flush();
          out.close();
          out = null;
          return rsakeypath+keysmallname;
        } catch(Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
	
	}
	private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }
	public void GenerateKey(String keysmallname){
	    JSch jsch=new JSch();
	    String appRootDir = context.getApplicationInfo().dataDir;
	    String rsakeypath = appRootDir + "/id_rsa"+keysmallname;
	    Log.d("cloud","Keylocation:"+rsakeypath);
	    try{
	      KeyPair kpair=KeyPair.genKeyPair(jsch, 1);
	      kpair.setPassphrase("");
	      kpair.writePrivateKey(rsakeypath);
	      kpair.writePublicKey(rsakeypath+".pub", "Das isch dr erst key");
	      System.out.println("Finger print: "+kpair.getFingerPrint());
	      kpair.dispose();
	    }
	    catch(Exception e){
	      System.out.println(e);
	    }
		
	}
}
