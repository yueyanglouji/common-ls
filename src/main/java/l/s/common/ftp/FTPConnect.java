package l.s.common.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPConnect {
	private final Logger log = LoggerFactory.getLogger(getClass());

	FTPClient client;
	
	FTPClientConfig config;
	
	public FTPConnect(){
		this.client = new FTPClient();
		this.config = new FTPClientConfig();
	}
	
	public FTPConnect config(FTPClientConfig config){
		this.config = config;
		
		return this;
	}
	
	public FTPConnect connect(String host, int port, String userName, String password) throws Exception{
		try {
			this.client.configure(config);
			client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

			this.client.connect(host, port);
			
			if(userName != null && !userName.equals("")){
				this.client.login(userName, password);
			}
			
			int reply = client.getReplyCode(); 
			log.debug("ftp reply : " + reply);
			if(!FTPReply.isPositiveCompletion(reply)) {
			    this.client.disconnect();
			    log.error("FTP server refused connection.");
			    throw new RuntimeException("FTP server refused connection.");
			}
			return this;
		} catch (Exception e) {
			if (client.isConnected()) {  
	            try {  
	            	client.disconnect();  
	            } catch (Exception ioe) {
					// nothing.
	            }  
	        }
			throw e;
		}
	}
	
	public FTPConnect upload(String remotePath, File file) throws Exception{
		FileInputStream in = new FileInputStream(file);
		try {
			return upload(remotePath, in, file.getName());
		} catch (Exception e) {
			in.close();
			throw e;
		}
	}
	
	public FTPConnect upload(String remotePath, InputStream in, String fileName) throws Exception{
		try{
			client.changeWorkingDirectory(remotePath);
			client.storeFile(fileName, in);
			in.close();
			return this;
		}catch(Exception e){
			if (client.isConnected()) {  
	            try {  
	            	client.disconnect();  
	            } catch (Exception ioe) {
					//nothing.
	            }  
	        }
			throw e;
		}
	}
	
	public FTPConnect dowanload(String remotePath, String fileName, File file) throws Exception{
		
		if(file.exists() && file.isDirectory()){
			return dowanload(remotePath, fileName, new File(file, fileName));
		}else{
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			try{
				return dowanload(remotePath, fileName, out);
			}catch(Exception e){
				out.close();
				throw e;
			}
		}
	}
	
	public FTPConnect dowanload(String remotePath, String fileName, OutputStream out) throws Exception{
		try{
			client.changeWorkingDirectory(remotePath);
			FTPFile[] fs = client.listFiles(remotePath, file -> file.getName().equals(fileName));
			
			if(fs!=null && fs.length == 1){
				client.retrieveFile(fileName, out);  
	            out.close(); 
			}else{
				throw new FileNotFoundException("file not found path : " + remotePath + " filename : " + fileName);
			}
			return this;
		}catch(Exception e){
			if (client.isConnected()) {  
	            try {  
	            	client.disconnect();  
	            } catch (Exception ioe) {
					//nothing.
	            }  
	        }
			throw e;
		}
	}
	
	public void logout() throws Exception{
		try{
			client.logout();
		}catch(Exception e){
			if (client.isConnected()) {  
	            try {  
	            	client.disconnect();  
	            } catch (Exception ioe) {
					//nothing.
	            }  
	        }
			throw e;
		}
	}
	
}
