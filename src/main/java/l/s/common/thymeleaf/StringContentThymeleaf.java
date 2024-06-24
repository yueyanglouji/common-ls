package l.s.common.thymeleaf;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.io.*;
import java.util.Scanner;

public class StringContentThymeleaf extends Thymeleaf{

	public StringContentThymeleaf(){

	}

	public StringContentThymeleaf(CONTEXT_TYPE contextType) {
		super(contextType);
	}

	@Override
	protected void setTemplateResolver(TemplateMode model) {
		StringTemplateResolver resolver = new StringTemplateResolver();
		resolver.setTemplateMode(model);
		engine.setTemplateResolver(resolver);
	}

	@Override
	public StringContentThymeleaf setVariable(String name, Object value){

		context.setVariable(name, value);
		return this;
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
		Scanner sc = new Scanner(new InputStreamReader(in, charset));
		StringBuilder content = new StringBuilder();
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			content.append(line);
			content.append("\n");
		}
		sc.close();
		in.close();

		return process(content.toString());
	}
}
