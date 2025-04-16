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
	public StringContentThymeleafExecutor createExecutor() {
		return new StringContentThymeleafExecutor(this.contextType, this.engine, this.locale);
	}
}
