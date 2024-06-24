package l.s.common.thymeleaf;

import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

public class FilePathThymeleaf extends AbstractFileThymeleaf{
	public FilePathThymeleaf() {
		super();
	}

	public FilePathThymeleaf(CONTEXT_TYPE contextType) {
		super(contextType);
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

	@Override
	public FilePathThymeleaf setVariable(String name, Object value){

		context.setVariable(name, value);
		return this;
	}

}
