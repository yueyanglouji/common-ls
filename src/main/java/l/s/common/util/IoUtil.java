package l.s.common.util;

import javax.xml.stream.XMLEventWriter;
import java.io.Closeable;
import java.io.File;
import java.io.Flushable;

public class IoUtil {
    public static void close(Closeable closeable){

        if(closeable != null){
            try {
                if(Flushable.class.isAssignableFrom(closeable.getClass())){
                    Flushable f = (Flushable) closeable;
                    f.flush();
                }
            }catch (Throwable e){
                //
            }
            try{
                closeable.close();
            }catch (Throwable e){
                //
            }
            try{
                closeable.close();
            }catch (Throwable e){
                //
            }
        }
    }

    public static void close(XMLEventWriter closeable){
        if(closeable != null){
            try{
                closeable.flush();
            }catch (Throwable e){
                //
            }
            try{
                closeable.close();
            }catch (Throwable e){
                //
            }
        }
    }
    public static void mkdirs(File file){
        if(!file.exists()){
            boolean b = file.mkdirs();
            if(!b){
                throw new RuntimeException("Folder create failed. File path:" + file.getAbsolutePath());
            }
        }else{
            if(!file.isDirectory()){
                throw new RuntimeException("File already exists and file type is not a directory. Folder create failed. File path:" + file.getAbsolutePath());
            }
        }

    }

    public static void mkdirs(String path){
        mkdirs(new File(path));
    }

    public static void mkdirsParents(File file){
        mkdirs(file.getParentFile());
    }

    public static void createNewFile(File file){
        if(!file.exists()){
            try{
                mkdirsParents(file);
                boolean b = file.createNewFile();
                if(!b){
                    throw new RuntimeException("File create failed, file path:" + file.getAbsolutePath());
                }
            }catch (Throwable e){
                throw new RuntimeException("File create failed, file path:" + file.getAbsolutePath(), e);
            }
        }else{
            if(!file.isFile()){
                throw new RuntimeException("File already exists and file type is not a file. File create failed. File path:" + file.getAbsolutePath());
            }
        }
    }
}
