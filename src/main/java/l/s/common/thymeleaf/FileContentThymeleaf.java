package l.s.common.thymeleaf;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileContentThymeleaf extends AbstractFileThymeleaf{

	private final Map<String, String> cacheMap;

	public FileContentThymeleaf(){
		this.cacheMap = new HashMap<>();
	}

	public FileContentThymeleaf(CONTEXT_TYPE contextType) {
		super(contextType);
		this.cacheMap = new HashMap<>();
	}

	@Override
	protected void setTemplateResolver(TemplateMode model) {
		StringTemplateResolver resolver = new StringTemplateResolver();
		resolver.setTemplateMode(model);
		engine.setTemplateResolver(resolver);
	}

	@Override
	public FileContentThymeleafExecutor createExecutor() {
		return new FileContentThymeleafExecutor(this.contextType, this.engine, this.locale, this);
	}

	public String getFileContent(String template) throws Exception{
		if(cacheAble){
			String ret = this.cacheMap.get(template);
			if(ret == null){
				String content = loadFile(template);
				this.cacheMap.put(template, content);
				ret = this.cacheMap.get(template);
			}
			return ret;
		}else{
			return loadFile(template);
		}

	}

	private String loadFile(String template) throws Exception{
		String fileName = template;
		if(!template.endsWith(suffix)){
			fileName = fileName + suffix;
		}
		File file = new File(new File(prefix), fileName);
		if(!file.exists()){
			fileName = template;
			file = new File(new File(prefix), fileName);
		}
		if(!file.exists()){
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		try(Scanner sc = new Scanner(file, this.encoding)){
			StringBuilder b = new StringBuilder();
			while(sc.hasNextLine()){
				b.append(sc.nextLine());
				b.append("\n");
			}
			return b.toString();
		}
	}

	public boolean hasImportThTagInHtml(String template) throws Exception{
		return getFileContent(template).matches("[\\s\\S]*xmlns\\:th\\s*=\\s*\"http\\://www\\.thymeleaf\\.org.*\"[\\s\\S]*");
	}

}
