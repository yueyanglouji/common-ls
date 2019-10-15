package l.s.common.ftp;

import java.io.File;

public class FTP {
	
	private FTP(){
	}
	
	public static FTPConnect newFTPConnect(){
		FTPConnect c = new FTPConnect();
		return c;
	}
	
	public static void main(String[] args) throws Exception{
		FTP.newFTPConnect().connect("10.254.241.243", 21, "lixb", "lixb").upload("E:/temp", new File("/Users/kdc/lixiaobao/git/OurUsers/apps/csv/csv20170405111702742.csv"));
	}
}
