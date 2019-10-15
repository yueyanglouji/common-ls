package l.s.common.thymeleaf;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

public class FilePathThymeleaf extends Thymeleaf{

	private String encoding;

	/**
	 * prefix is file directory path
	 */
	private String prefix;

	/**
	 * default suffix is .html
	 */
	private String suffix;

	private boolean cacheAble;

	public FilePathThymeleaf(){
		this.encoding = "UTF-8";
		this.suffix = ".html";
		this.prefix = getWEB_INF_Path();
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setCacheAble(boolean cacheAble) {
		this.cacheAble = cacheAble;
	}

	@Override
	protected void setTemplateResolver(TemplateMode model) {
		FileTemplateResolver resolver = new FileTemplateResolver();
		resolver.setTemplateMode(model);
		resolver.setCacheable(cacheAble);
		resolver.setCharacterEncoding(encoding);

		resolver.setPrefix(prefix);
		resolver.setSuffix(".html");
		engine.setTemplateResolver(resolver);
	}

	public static String getWEB_INF_Path(){
		String filePath =FilePathThymeleaf.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if(filePath.indexOf("WEB-INF") > 0) {
			filePath = filePath.substring(0, filePath.lastIndexOf("WEB-INF"));
		}
		return filePath;
	}

	public FilePathThymeleaf setVariable(String name, Object value){

		context.setVariable(name, value);
		return this;
	}

}
