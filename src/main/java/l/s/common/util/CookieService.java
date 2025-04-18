package l.s.common.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Deprecated
public class CookieService {

	private static CookieService cookieService;
	
	private final String path = "cookies.sqlite";
	
	private CookieService(boolean reset)throws Exception{
		
		File file = new File(path);
		boolean exists = file.exists();
		if(!exists){
			file.createNewFile();
		}
		Class.forName("org.sqlite.JDBC");
		try(
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement stat = conn.createStatement();
		){
			if(!exists){
				stat.execute("create table cookies(name varchar(100) primary key,value varchar(500))");
			}else{
				if(reset){
					stat.execute("delete from cookies");
				}
			}
		}
	}
	
	public synchronized void removeAllCookies() throws Exception{
		try(
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement stat = conn.createStatement();
		){
			stat.execute("delete from cookies");
		}
	}
	
	public synchronized String getCookieValue(String name) throws Exception{
		try(
				Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
				Statement stat = conn.createStatement();
				ResultSet rs = stat.executeQuery("select * from cookies where name='" + name + "';")
		){
			String value = null;
			while (rs.next()) {
				value = rs.getString("value");
			}
			return value;
		}
	}
	
	public synchronized String getCookies() throws Exception{
		StringBuilder builder = new StringBuilder();
		try(
				Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
				Statement stat = conn.createStatement();
				ResultSet rs = stat.executeQuery("select * from cookies;");
		){
			while (rs.next()) {
				String name = rs.getString("name");
				builder.append(name);
				builder.append("=");
				builder.append(rs.getString("value"));
				builder.append(";");
			}
		}
		if(builder.length()>0){
			builder.deleteCharAt(builder.length()-1);
		}
		return builder.toString();
	}
	
	public synchronized void setCookie(String setCookie)throws Exception{
		if(setCookie == null || setCookie.equals("")){
			return;
		}
		
		String name="";
		String value="";
		boolean isName=true;
		
		for(int i=0;i<setCookie.length();i++){
			char ch = setCookie.charAt(i);
			if(ch == ' '){
				
			}
			else{
				
				if(ch == '=' && isName){
					isName=false;
					continue;
				}else{
					if(ch==';'){
						if(name.toLowerCase().matches("domain|path|expires|secure|max\\-age|httponly")){
							isName=true;
							name="";
							value="";
							continue;
						}else{
							setCookie(name,value);
							isName=true;
							name="";
							value="";
							continue;
						}
					}
					if(ch==' ' && value.equals("")){
						continue;
					}
					if(isName){
						name += ch;
					}else{
						value += ch;
					}
				}
			}
		}
	}
	
	public synchronized void setCookie(String name,String value) throws Exception{
		try(
				Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
				Statement stat = conn.createStatement()
		){
			stat.execute("delete from cookies where name='"+name+"'");
			stat.executeUpdate("insert into cookies(name,value) values('"+name+"','"+value +"'); ");
		}
	}
	
	public synchronized static CookieService getInstance(){
		return getInstance(true);
	}
	
	public synchronized static CookieService getInstance(boolean reset){
		if(cookieService == null){
			try {
				cookieService = new CookieService(reset);
			} catch (Exception e) {
				throw new RuntimeException("firefox path error.");
			}
			
		}
		return cookieService;
	}
}

