package l.s.common.ftp;

public class FTP {
	
	private FTP(){
	}
	
	public static FTPConnect newFTPConnect(){
		return new FTPConnect();
	}
}
