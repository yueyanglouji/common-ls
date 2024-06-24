package l.s.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;

public class FileS {
	
	/**
	 * 
	 * @param sorce
	 * 			要剪切的文件或文件夹。
	 * @param toFile
	 * 			目的文件夹。
	 * @throws Exception
	 */
	public void moveAndRenameFile(File sorce,File toFile)throws Exception{
		System.out.println(sorce.getAbsolutePath());
		if(sorce.isDirectory()){
			throw new RuntimeException(sorce.getAbsolutePath() + " is not a file.");
		}else{
			if(!toFile.getParentFile().exists()){
				toFile.getParentFile().mkdirs();
			}
			FileInputStream fileInputStream = new FileInputStream(sorce);
			BufferedInputStream in = new BufferedInputStream(fileInputStream);
			
			FileOutputStream fileOutputStream = new FileOutputStream(toFile);
			BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);
			int n=0;
			byte[] b = new byte[1024];
			while((n=in.read(b,0,b.length))!=-1){
				out.write(b,0,n);
			}
			out.flush();
			in.close();
			fileInputStream.close();
			out.close();
			fileOutputStream.close();
			sorce.delete();
		}
	}
	/**
	 * 
	 * @param sorce
	 * 			要剪切的文件或文件夹。
	 * @param toDir
	 * 			目的文件夹。
	 * @throws Exception
	 */
	public void moveFiles(File sorce,File toDir)throws Exception{
		System.out.println(sorce.getAbsolutePath());
		if(sorce.isDirectory()){
			File file = new File(toDir.getAbsolutePath()+"/"+sorce.getName());
			file.mkdir();
			File[] childsFile = sorce.listFiles();
			for(File f:childsFile){
				moveFiles(f, file);
			}
		}else{
			if(!toDir.exists()){
				toDir.mkdirs();
			}
			FileInputStream fileInputStream = new FileInputStream(sorce);
			BufferedInputStream in = new BufferedInputStream(fileInputStream);
			
			FileOutputStream fileOutputStream = new FileOutputStream(new File(toDir.getAbsolutePath()+"/"+sorce.getName()));
			BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);
			int n=0;
			byte[] b = new byte[1024];
			while((n=in.read(b,0,b.length))!=-1){
				out.write(b,0,n);
			}
			out.flush();
			in.close();
			fileInputStream.close();
			out.close();
			fileOutputStream.close();
		}
		sorce.delete();
	}
	/**
	 * 
	 * @param sorce
	 * 			要复制的文件或文件夹。
	 * @param toDir
	 * 			目的文件夹。
	 * @throws Exception
	 */
	public void copyFiles(File sorce,File toDir)throws Exception{
		FileFilter filter = new FileFilter(){

			public boolean accept(File pathname) {
				return true;
			}
			
		};
		this.copyFiles(sorce, toDir, filter);
	}
	
	/**
	 * 
	 * @param sorce
	 * 			要复制的文件或文件夹。
	 * @param toDir
	 * 			目的文件夹。
	 * 
	 * @param filter
	 * 			拦截器。
	 * @throws Exception
	 */
	public void copyFiles(File sorce,File toDir,FileFilter filter)throws Exception{
		System.out.println(sorce.getAbsolutePath());
		if(sorce.isDirectory()){
			File file = new File(toDir.getAbsolutePath()+"/"+sorce.getName());
			file.mkdir();
			File[] childsFile = sorce.listFiles(filter);
			for(File f:childsFile){
				copyFiles(f, file);
			}
		}else{
			if(!toDir.exists()){
				toDir.mkdirs();
			}
			FileInputStream fileInputStream = new FileInputStream(sorce);
			BufferedInputStream in = new BufferedInputStream(fileInputStream);
			
			FileOutputStream fileOutputStream = new FileOutputStream(new File(toDir.getAbsolutePath()+"/"+sorce.getName()));
			BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);
			int n=0;
			byte[] b = new byte[1024];
			while((n=in.read(b,0,b.length))!=-1){
				out.write(b,0,n);
			}
			out.flush();
			in.close();
			fileInputStream.close();
			out.close();
			fileOutputStream.close();
		}
	}
	
	public void copyFile(File sourceFile,File toFile)throws Exception{
		FileInputStream fileInputStream = new FileInputStream(sourceFile);
		BufferedInputStream in = new BufferedInputStream(fileInputStream);
		if(!toFile.getParentFile().exists()){
			toFile.getParentFile().mkdirs();
		}
		FileOutputStream fileOutputStream = new FileOutputStream(toFile);
		BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);
		int n=0;
		byte[] b = new byte[1024];
		while((n=in.read(b,0,b.length))!=-1){
			out.write(b,0,n);
		}
		out.flush();
		in.close();
		fileInputStream.close();
		out.close();
		fileOutputStream.close();
	}
	
	public boolean deleteFiles(File source){
		if(source.isDirectory()){
			File[] childsFile = source.listFiles();
			for(File f:childsFile){
				deleteFiles(f);
			}
		}
		return source.delete();
	}
	
	/**
	 * 
	 * @param sourceFiles		//压缩的文件或文件夹，若文件或文件夹为多个，个文件的相对目录不可重复
	 * @param out			//输出文件。
	 * @throws Exception
	 */
	public void packageFiles(File[] sourceFiles, OutputStream out,String charset)throws Exception{
		Charset chs= Charset.forName(charset);
		ZipOutputStream zipout = new ZipOutputStream(out,chs);
		FileFilter fileFilter = new FileFilter(){

			public boolean accept(File pathname) {
				if(pathname.isDirectory()||!pathname.isHidden()){
					return true;
				}
				return false;
			}
			
		};
		for(File source:sourceFiles){
			packageFiles(source, zipout ,source.getName(),fileFilter);
		}
		zipout.close();
	}
	/**
	 * 
	 * @param sourceFile		//压缩的文件或文件夹
	 * @param out			//输出文件。
	 * @throws Exception
	 */
	public void packageFiles(File sourceFile, OutputStream out,String charset) throws Exception{
		Charset chs= Charset.forName(charset);
		ZipOutputStream zipout = new ZipOutputStream(out,chs);
		FileFilter fileFilter = new FileFilter(){

			public boolean accept(File pathname) {
				if(pathname.isDirectory()||!pathname.isHidden()){
					return true;
				}
				return false;
			}
			
		};
		packageFiles(sourceFile, zipout,sourceFile.getName(),fileFilter);
		zipout.close();
	}
	
	/**
	 * 
	 * @param sourceFiles		//压缩的文件或文件夹，若文件或文件夹为多个，个文件的相对目录不可重复
	 * @param out			//输出文件。
	 * @throws Exception
	 */
	public void packageFiles(File[] sourceFiles,OutputStream out,FileFilter fileFilter,String charset)throws Exception{
		Charset chs= Charset.forName(charset);
		ZipOutputStream zipout = new ZipOutputStream(out,chs);
		
		for(File source:sourceFiles){
			packageFiles(source, zipout ,source.getName(),fileFilter);
		}
		zipout.close();
	}
	/**
	 * 
	 * @param sourceFile		//压缩的文件或文件夹
	 * @param out			//输出流。windows-31j
	 * @throws Exception
	 */
	public void packageFiles(File sourceFile, OutputStream out,FileFilter fileFilter,String charset) throws Exception{
		Charset chs= Charset.forName(charset);
		ZipOutputStream zipout = new ZipOutputStream(out,chs);

		packageFiles(sourceFile, zipout,sourceFile.getName(),fileFilter);
		zipout.close();
	}
	
	private void packageFiles(File sourceFile, ZipOutputStream zipout,String zipEntryName,FileFilter fileFilter) throws Exception{
		ZipEntry entry = new ZipEntry(zipEntryName);

		if(sourceFile.isDirectory()){
			
			File[] childs = sourceFile.listFiles(fileFilter);
			for(int i=0;i<childs.length;i++){
				String nextEntryName = zipEntryName+"/"+childs[i].getName();
				packageFiles(childs[i], zipout,nextEntryName,fileFilter);
			}
		}else{
			boolean isPackageFile = fileFilter.accept(sourceFile);
			if(isPackageFile==false){
				return;
			}
			zipout.putNextEntry(entry);
			FileInputStream in = new FileInputStream(sourceFile);
			byte[] b = new byte[1024];
			int length;
			while((length = in.read(b))!=-1){
				zipout.write(b,0,length);
			}
			zipout.closeEntry();
			in.close();
		}

	}
	
	
	//--------------------package ta.gz
	
	/**
	 * 
	 * @param sourceFiles		//压缩的文件或文件夹，若文件或文件夹为多个，个文件的相对目录不可重复
	 * @param toFile			//输出文件。
	 * @throws Exception
	 */
	public void packageFilesTar(File[] sourceFiles, File toFile)throws Exception{
		TarOutputStream tarout = getTarOutPutStream(toFile);

		FileFilter fileFilter = new FileFilter(){

			public boolean accept(File pathname) {
				String filename = pathname.getName().toLowerCase();
				if(pathname.isDirectory()||filename.matches(".*\\.(jpg|png|gif|txt|xml|)")){
					return true;
				}
				return false;
			}
			
		};
		for(File source:sourceFiles){
			packageFilesTar(source, tarout ,source.getName(),fileFilter);
		}
		tarout.close();
		
		compress(toFile);
	}
	
	/**
	 * 
	 * @param sourceFile		//压缩的文件或文件夹
	 * @param toFile			//输出文件。
	 * @throws Exception
	 */
	public void packageFilesTar(File sourceFile, File toFile) throws Exception{
		TarOutputStream tarout = getTarOutPutStream(toFile);

		FileFilter fileFilter = new FileFilter(){

			public boolean accept(File pathname) {
				String filename = pathname.getName().toLowerCase();
				if(pathname.isDirectory()||filename.matches(".*\\.(jpg|png|gif|txt|xml|)")){
					return true;
				}
				return false;
			}
			
		};
		packageFilesTar(sourceFile, tarout,sourceFile.getName(),fileFilter);
		tarout.close();
		
		compress(toFile);
	}
	
	/**
	 * 
	 * @param sourceFiles		//压缩的文件或文件夹，若文件或文件夹为多个，个文件的相对目录不可重复
	 * @param toFile			//输出文件。
	 * @throws Exception
	 */
	public void packageFilesTar(File[] sourceFiles, File toFile,FileFilter fileFilter)throws Exception{
		TarOutputStream tarout = getTarOutPutStream(toFile);
		
		for(File source:sourceFiles){
			packageFilesTar(source, tarout ,source.getName(),fileFilter);
		}
		tarout.close();
		
		compress(toFile);
	}
	/**
	 * 
	 * @param sourceFile		//压缩的文件或文件夹
	 * @param toFile			//输出文件。
	 * @throws Exception
	 */
	public void packageFilesTar(File sourceFile, File toFile,FileFilter fileFilter) throws Exception{
		TarOutputStream tarout = getTarOutPutStream(toFile);

		packageFilesTar(sourceFile, tarout,sourceFile.getName(),fileFilter);
		tarout.close();
		
		compress(toFile);
	}
	
	private void packageFilesTar(File sourceFile, TarOutputStream tarout,String tarEntryName,FileFilter fileFilter) throws Exception{
		TarEntry entry = new TarEntry(tarEntryName);
		
		if(sourceFile.isDirectory()){
			
			File[] childs = sourceFile.listFiles(fileFilter);
			for(int i=0;i<childs.length;i++){
				String nextEntryName = tarEntryName+"/"+childs[i].getName();
				packageFilesTar(childs[i], tarout,nextEntryName,fileFilter);
			}
		}else{
			boolean isPackageFile = fileFilter.accept(sourceFile);
			if(isPackageFile==false){
				return;
			}
			entry.setSize(sourceFile.length());
			tarout.putNextEntry(entry);
			FileInputStream in = new FileInputStream(sourceFile);
			byte[] b = new byte[1024];
			int length;
			while((length = in.read(b))!=-1){
				tarout.write(b,0,length);
			}
			tarout.closeEntry();
			in.close();
		}

	}
	
	private TarOutputStream getTarOutPutStream(File toFile)throws Exception{
		String path = toFile.getAbsolutePath();
		if(path.endsWith(".tar.gz")){
			path = path.substring(0,path.length()-3);
		}
		else{
			throw new RuntimeException("file name must end with .tar.gz ");
		}
		TarOutputStream tarOutputStream = new TarOutputStream(new FileOutputStream(new File(path)));
		tarOutputStream.setLongFileMode(TarOutputStream.LONGFILE_GNU);
		return tarOutputStream;
	}
	
	private void compress(File toFile) {
		FileInputStream in = null;
		GZIPOutputStream out = null;
		File tarFile = null;
		try {
			String path = toFile.getAbsolutePath();
			if(path.endsWith(".tar.gz")){
				path = path.substring(0,path.length()-3);
			}else{
				throw new RuntimeException("file name must end with .tar.gz ");
			}
			tarFile = new File(path);
			in = new FileInputStream(tarFile);
			out = new GZIPOutputStream(new FileOutputStream(toFile));

			byte[] b = new byte[1024];
			int number = 0;
			while ((number = in.read(b)) != -1) {
				out.write(b, 0, number);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}

				if (out != null) {
					out.close();
				}
				if(tarFile!=null&&tarFile.exists()){
					tarFile.delete();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void unpackageFilesZip(String zipfilePath,String toPath) throws Exception{
		File file = new File(zipfilePath);
		File tofile = new File(toPath);
		unpackageFilesZip(file,tofile);
	}
	
	public void unpackageFilesZip(File zipfile,File toFile) throws Exception{
		ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
		ZipEntry entry = null;
		String outPath = toFile.getAbsolutePath()+"/";
		while((entry=zin.getNextEntry())!=null){
			if(entry.isDirectory()){
				continue;
			}
			String entryName = entry.getName();
			System.out.println(entryName);
			String path = outPath + entryName;
			File outFile = new File(path);
			if(!outFile.getParentFile().exists()){
				outFile.getParentFile().mkdirs();
			}
			
			FileOutputStream fout = new FileOutputStream(new File(path));
			byte[] b = new byte[1024];
			int len;
			while((len=zin.read(b))!=-1){
				fout.write(b, 0, len);
			}
			fout.flush();
			fout.close();
			zin.closeEntry();
		}
		zin.close();
	}
	
	class StringAdaptor{
		String str;
	}
	
	/** 
     * 读取文件创建时间 
     */  
    public Date getCreateTime_windows(File file) throws Exception{  
        String filePath = file.getAbsolutePath();
        final String fileName = file.getName();
        try {  
            String cmd = "cmd /C dir "           
                    + filePath.replaceAll(" ","\" \"").replaceAll("　","\"　\"").replace("(", "\"(\"").replace(")", "\")\"")
                    + "/tc";
            
            System.out.println("cmd : " + cmd);
            
            final Process process = Runtime.getRuntime().exec(cmd);
    		
    		ExecutorService service = Executors.newSingleThreadExecutor();
    		
    		final StringAdaptor stringAdaptor = new StringAdaptor();
    		Future<String> future = service.submit(new Callable<String>() {

    			@Override
    			public String call() throws Exception {
    				
    				InputStream in = process.getInputStream();
    				Scanner scanner = new Scanner(in, "MS932");
    				while(scanner.hasNextLine()){
    					String line = scanner.nextLine();
    					System.out.println(line);
    					if(!line.trim().equals("") && line.trim().toLowerCase().endsWith(fileName.toLowerCase())){  
    	                    String time = line.substring(0,17);
    	                    stringAdaptor.str = time;
    	                }                             
    				}
    				scanner.close();
    				
    				InputStream error = process.getErrorStream();
    				Scanner errorscanner = new Scanner(error);
    				while(errorscanner.hasNextLine()){
    					String line = errorscanner.nextLine();
    					System.out.println(line);
    				}
    				errorscanner.close();
    				in.close();
    				
    				return "0";
    			}
    			
    		});
    		
    		try {
    			future.get(10, TimeUnit.SECONDS);
    		} catch (Exception e) {
    			e.printStackTrace();
    			future.cancel(true);
    		}finally{
    			service.shutdownNow();
    		}
    		
    		String strTime = stringAdaptor.str;
    		//2015/09/01  08:40
    		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    		Date date = format.parse(strTime);
    		return date;
    		
        } catch (Exception e) {  
            e.printStackTrace(); 
            throw e;
        }         
    }  
    /** 
     * 读取文件修改时间的方法
     */   
    public Date getModifiedTime(File file){  
        long time = file.lastModified();
        Date date = new Date(time);
        return date;
    }
	
}
