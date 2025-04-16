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
import java.nio.file.Path;
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

@Deprecated
public class FileUtil {
	
	/**
	 * 
	 * @param source Source file or directory.
	 * @param toFile Dist directory.
	 * @throws Exception Exception
	 */
	public static void moveAndRenameFile(File source,File toFile)throws Exception{
		if(source.isDirectory()){
			throw new RuntimeException(source.getAbsolutePath() + " is not a file.");
		}else{
			if(!toFile.getParentFile().exists()){
				toFile.getParentFile().mkdirs();
			}
			try(
					FileInputStream fileInputStream = new FileInputStream(source);
					BufferedInputStream in = new BufferedInputStream(fileInputStream);

					FileOutputStream fileOutputStream = new FileOutputStream(toFile);
					BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);
					){
				int n;
				byte[] b = new byte[1024];
				while((n=in.read(b,0,b.length))!=-1){
					out.write(b,0,n);
				}
				out.flush();
				source.delete();
			}


		}
	}
	/**
	 *
	 * @param source Source file or directory.
	 * @param toDir Dist directory.
	 * @throws Exception Exception
	 */
	public static void moveFiles(File source,File toDir)throws Exception{
		if(source.isDirectory()){
			File file = new File(toDir.getAbsolutePath()+"/"+source.getName());
			file.mkdir();
			File[] childsFile = source.listFiles();
			for(File f:childsFile){
				moveFiles(f, file);
			}
		}else{
			if(!toDir.exists()){
				toDir.mkdirs();
			}
			try(
					FileInputStream fileInputStream = new FileInputStream(source);
					BufferedInputStream in = new BufferedInputStream(fileInputStream);

					FileOutputStream fileOutputStream = new FileOutputStream(new File(toDir.getAbsolutePath()+"/"+source.getName()));
					BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);
					){
				int n;
				byte[] b = new byte[1024];
				while((n=in.read(b,0,b.length))!=-1){
					out.write(b,0,n);
				}
				out.flush();
			}
		}
		source.delete();
	}
	/**
	 *
	 * @param source Source file or directory.
	 * @param toDir Dist directory.
	 * @throws Exception Exception
	 */
	public static void copyFiles(File source,File toDir)throws Exception{
		FileFilter filter = new FileFilter(){

			@Override
			public boolean accept(File pathname) {
				return true;
			}
			
		};
		copyFiles(source, toDir, filter);
	}
	
	/**
	 *
	 * @param source Source file or directory.
	 * @param toDir Dist directory.
	 * @param filter Filter   
	 * @throws Exception Exception
	 */
	public static void copyFiles(File source, File toDir,FileFilter filter)throws Exception{
		if(!source.exists()){
			return;
		}
		if(source.isDirectory()){
			File file = new File(toDir.getAbsolutePath(), source.getName());
			File[] childsFile = source.listFiles(filter);
			if(childsFile != null){
				IoUtil.mkdirs(file);
				for(File f:childsFile){
					copyFiles(f, file);
				}
			}
		}else{
			if(!toDir.exists()){
				IoUtil.mkdirs(toDir);
			}
			copyFile(source, new File(toDir.getAbsolutePath(), source.getName()));
		}
	}
	
	public static void copyFile(File sourceFile, File toFile)throws Exception{
		if(!sourceFile.exists()){
			return;
		}
		try(
				FileInputStream fileInputStream = new FileInputStream(sourceFile);
				BufferedInputStream in = new BufferedInputStream(fileInputStream);

				FileOutputStream fileOutputStream = new FileOutputStream(toFile);
				BufferedOutputStream out = new BufferedOutputStream(fileOutputStream);
		){
			IoUtil.mkdirsParents(toFile);
			int n;
			byte[] b = new byte[1024];
			while((n=in.read(b,0,b.length))!=-1){
				out.write(b,0,n);
			}
			out.flush();
		}
	}
	
	public static boolean deleteFiles(File source){
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
	 * @param sourceFiles   Source files or directories.
	 * @param out			output stream.
	 * @throws Exception Exception
	 */
	public static void packageFiles(File[] sourceFiles, OutputStream out,String charset)throws Exception{
		Charset chs= Charset.forName(charset);
		try(
				ZipOutputStream zipout = new ZipOutputStream(out,chs);
				){
			FileFilter fileFilter = new FileFilter(){

				@Override
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
		}finally {
			l.s.common.util.IoUtil.close(out);
		}
	}
	/**
	 *
	 * @param sourceFile   Source file or directory.
	 * @param out			output stream.
	 * @throws Exception Exception
	 */
	public static void packageFiles(File sourceFile, OutputStream out,String charset) throws Exception{
		Charset chs= Charset.forName(charset);
		try(ZipOutputStream zipout = new ZipOutputStream(out,chs)){
			FileFilter fileFilter = new FileFilter(){

				@Override
				public boolean accept(File pathname) {
					if(pathname.isDirectory()||!pathname.isHidden()){
						return true;
					}
					return false;
				}

			};
			packageFiles(sourceFile, zipout,sourceFile.getName(),fileFilter);
		}finally {
			l.s.common.util.IoUtil.close(out);
		}


	}
	
	/**
	 *
	 * @param sourceFiles   Source files or directories.
	 * @param out			output stream.
	 * @throws Exception Exception
	 */
	public static void packageFiles(File[] sourceFiles,OutputStream out,FileFilter fileFilter,String charset)throws Exception{
		Charset chs= Charset.forName(charset);
		try(
			ZipOutputStream zipout = new ZipOutputStream(out,chs);
			){
			for(File source:sourceFiles){
				packageFiles(source, zipout ,source.getName(),fileFilter);
			}
        }finally {
			l.s.common.util.IoUtil.close(out);
        }
	}
	/**
	 *
	 * @param sourceFile   Source file or directory.
	 * @param out			output stream.
	 * @throws Exception Exception
	 */
	public static void packageFiles(File sourceFile, OutputStream out,FileFilter fileFilter,String charset) throws Exception{
		Charset chs= Charset.forName(charset);
		try (
				ZipOutputStream zipout = new ZipOutputStream(out,chs);
				){
			packageFiles(sourceFile, zipout,sourceFile.getName(),fileFilter);
		} finally {
			l.s.common.util.IoUtil.close(out);
        }
    }
	
	private static void packageFiles(File sourceFile, ZipOutputStream zipout,String zipEntryName,FileFilter fileFilter) throws Exception{
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
			try(FileInputStream in = new FileInputStream(sourceFile);){
				byte[] b = new byte[1024];
				int length;
				while((length = in.read(b))!=-1){
					zipout.write(b,0,length);
				}
				zipout.closeEntry();
			}
		}
	}
	
	/**
	 *
	 * @param sourceFiles   Source files or directories.
	 * @param toFile		Dist file.
	 * @throws Exception Exception
	 */
	public static void packageFilesTarGz(File[] sourceFiles, File toFile)throws Exception{
		try(
			TarOutputStream tarout = getTarOutPutStream(toFile);
		){
			FileFilter fileFilter = new FileFilter(){

				@Override
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
		}
		compress(toFile);
	}
	
	/**
	 *
	 * @param sourceFile   Source file or directory.
	 * @param toFile	   Dist file.
	 * @throws Exception Exception
	 */
	public static void packageFilesTarGz(File sourceFile, File toFile) throws Exception{
		try (
			TarOutputStream tarout = getTarOutPutStream(toFile);
		) {
			FileFilter fileFilter = new FileFilter(){

				@Override
				public boolean accept(File pathname) {
					String filename = pathname.getName().toLowerCase();
					if(pathname.isDirectory()||filename.matches(".*\\.(jpg|png|gif|txt|xml|)")){
						return true;
					}
					return false;
				}

			};
			packageFilesTar(sourceFile, tarout,sourceFile.getName(),fileFilter);
		}
		compress(toFile);

	}
	
	/**
	 *
	 * @param sourceFiles   Source files or directories.
	 * @param toFile		Dist file.
	 * @param fileFilter Filter
	 * @throws Exception Exception
	 */
	public static void packageFilesTar(File[] sourceFiles, File toFile,FileFilter fileFilter)throws Exception{
		try(
			TarOutputStream tarout = getTarOutPutStream(toFile);
		){
			for(File source:sourceFiles){
				packageFilesTar(source, tarout ,source.getName(),fileFilter);
			}
		}
		compress(toFile);
	}
	/**
	 *
	 * @param sourceFile   Source file or directory.
	 * @param toFile	    Dist file.
	 * @param fileFilter Filter
	 * @throws Exception Exception
	 */
	public static void packageFilesTar(File sourceFile, File toFile,FileFilter fileFilter) throws Exception{
		try(
			TarOutputStream tarout = getTarOutPutStream(toFile);
		){
			packageFilesTar(sourceFile, tarout,sourceFile.getName(),fileFilter);
		}
		compress(toFile);
	}
	
	private static void packageFilesTar(File sourceFile, TarOutputStream tarout,String tarEntryName,FileFilter fileFilter) throws Exception{
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
			try(
					FileInputStream in = new FileInputStream(sourceFile);
					){
				byte[] b = new byte[1024];
				int length;
				while((length = in.read(b))!=-1){
					tarout.write(b,0,length);
				}
				tarout.closeEntry();
			}
		}
	}
	
	private static TarOutputStream getTarOutPutStream(File toFile)throws Exception{
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
	
	private static void compress(File toFile) throws IOException {
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
		} finally {
			l.s.common.util.IoUtil.close(in);
			l.s.common.util.IoUtil.close(out);
			if(tarFile!=null&&tarFile.exists()){
				tarFile.delete();
			}
		}
	}
	
	public static void unpackageFilesZip(String zipfilePath,String toPath) throws Exception{
		File file = new File(zipfilePath);
		File tofile = new File(toPath);
		unpackageFilesZip(file,tofile);
	}
	
	public static void unpackageFilesZip(File zipfile,File toFile) throws Exception{
		try(
			FileInputStream fileInputStream = new FileInputStream(zipfile);
			ZipInputStream zin = new ZipInputStream(fileInputStream);
		){
			ZipEntry entry = null;
			String outPath = toFile.getAbsolutePath()+"/";
			while((entry=zin.getNextEntry())!=null){
				if(entry.isDirectory()){
					continue;
				}
				String entryName = entry.getName();
				String path = outPath + entryName;
				File outFile = new File(path);
				IoUtil.mkdirsParents(outFile);
				try(
					FileOutputStream fout = new FileOutputStream(new File(path));
				){
					byte[] b = new byte[1024];
					int len;
					while((len=zin.read(b))!=-1){
						fout.write(b, 0, len);
					}
					fout.flush();
				}
				zin.closeEntry();
			}
		}
	}
	
	static class StringAdaptor{
		String str;
	}
	
	/** 
     * File create time
     */  
    public static Date getCreateTimeWindows(File file) throws Exception{
		Path filePath = file.toPath();
		java.nio.file.attribute.BasicFileAttributes attributes = java.nio.file.Files.readAttributes(filePath, java.nio.file.attribute.BasicFileAttributes.class);
		java.nio.file.attribute.FileTime creationTime = attributes.creationTime();

		long strTime = creationTime.toMillis();
		return new Date(strTime);
    }

    /** 
     * File modified time.
     */   
    public static Date getModifiedTime(File file){
        long time = file.lastModified();
        Date date = new Date(time);
        return date;
    }

	public static String getTempDir() throws IOException {
		String dir = System.getProperty("java.io.tmpdir");
		if(dir == null || !new File(dir).exists()){
			dir = new File("./temp").getCanonicalPath();
		}
		return dir;
	}
	
}
