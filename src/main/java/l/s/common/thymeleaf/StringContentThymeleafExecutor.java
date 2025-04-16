package l.s.common.thymeleaf;

import org.thymeleaf.TemplateEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Scanner;

public class StringContentThymeleafExecutor extends ThymeleafExecutor<StringContentThymeleafExecutor>{

    StringContentThymeleafExecutor(Thymeleaf.CONTEXT_TYPE contextType, TemplateEngine engine, Locale locale){
        super(contextType, engine, locale);
    }

    public String processFile(String file, String charset) throws Exception{
        return processFile(new File(file), charset);
    }

    public String processFile(File file, String charset) throws Exception{
        if(!file.exists() || !file.isFile()){
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        return processStream(new FileInputStream(file), charset);
    }

    public String processStream(InputStream in, String charset) throws Exception{
        StringBuilder content = new StringBuilder();
        try(
                Scanner sc = new Scanner(new InputStreamReader(in, charset));
        ){
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                content.append(line);
                content.append("\n");
            }
        }finally {
            l.s.common.util.IoUtil.close(in);
        }
        return process(content.toString());
    }

}
