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
                throw new RuntimeException("folder create failed");
            }
        }else{
            if(!file.isDirectory()){
                throw new RuntimeException("file type is not a directory");
            }
        }

    }

    public static void mkdirs(String path){
        mkdirs(new File(path));
    }

    public static void mkdirsParent(File file){
        mkdirs(file.getParentFile());
    }

    public static void createNewFile(File file){
        if(!file.exists()){
            try{
                boolean b = file.createNewFile();
                if(!b){
                    throw new RuntimeException("file create failed");
                }
            }catch (Throwable e){
                throw new RuntimeException("file create failed");
            }
        }else{
            if(!file.isFile()){
                throw new RuntimeException("file type is not a file");
            }
        }
    }
}
