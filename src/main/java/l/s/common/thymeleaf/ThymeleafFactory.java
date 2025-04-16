package l.s.common.thymeleaf;

import l.s.common.context.Application;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.Locale;

public class ThymeleafFactory {

	public static StringContentThymeleaf getStringContentThymeleaf(TemplateMode model){
		return getStringContentThymeleaf(model, Application.getContext().getLocale());
	}

	public static StringContentThymeleaf getStringContentThymeleaf(TemplateMode model, Locale locale){
		StringContentThymeleaf thymeleaf = new StringContentThymeleaf();
		thymeleaf.setTemplateMode(model);
		thymeleaf.setLocal(locale);
		thymeleaf.initializationEngine();

		return thymeleaf;
	}

	public static StringContentThymeleaf getStringContentThymeleaf(TemplateMode model, Locale locale, Thymeleaf.CONTEXT_TYPE contextType){
		StringContentThymeleaf thymeleaf = new StringContentThymeleaf(contextType);
		thymeleaf.setTemplateMode(model);
		thymeleaf.setLocal(locale);
		thymeleaf.initializationEngine();

		return thymeleaf;
	}

	public static FilePathThymeleaf getFilePathThymeleaf(TemplateMode model, String encoding, String prefix, String suffix, boolean cacheAble){
		return getFilePathThymeleaf(model, Application.getContext().getLocale(), encoding, prefix, suffix, cacheAble);
	}

	public static FilePathThymeleaf getFilePathThymeleaf(TemplateMode model, Locale locale, String encoding, String prefix, String suffix, boolean cacheAble){
		FilePathThymeleaf thymeleaf = new FilePathThymeleaf();
		thymeleaf.setTemplateMode(model);
		thymeleaf.setLocal(locale);
		thymeleaf.setEncoding(encoding);
		thymeleaf.setPrefix(prefix);
		thymeleaf.setSuffix(suffix);
		thymeleaf.setCacheAble(cacheAble);
		thymeleaf.initializationEngine();

		return thymeleaf;
	}

	public static FilePathThymeleaf getFilePathThymeleaf(TemplateMode model, Locale locale, String encoding, String prefix, String suffix, boolean cacheAble, Thymeleaf.CONTEXT_TYPE contextType){
		FilePathThymeleaf thymeleaf = new FilePathThymeleaf(contextType);
		thymeleaf.setTemplateMode(model);
		thymeleaf.setLocal(locale);
		thymeleaf.setEncoding(encoding);
		thymeleaf.setPrefix(prefix);
		thymeleaf.setSuffix(suffix);
		thymeleaf.setCacheAble(cacheAble);
		thymeleaf.initializationEngine();

		return thymeleaf;
	}

	public static FileContentThymeleaf getFileContentThymeleaf(TemplateMode model, String encoding, String prefix, String suffix, boolean cacheAble){
		return getFileContentThymeleaf(model, Application.getContext().getLocale(), encoding, prefix, suffix, cacheAble);
	}

	public static FileContentThymeleaf getFileContentThymeleaf(TemplateMode model, Locale locale, String encoding, String prefix, String suffix, boolean cacheAble){
		FileContentThymeleaf thymeleaf = new FileContentThymeleaf();
		thymeleaf.setTemplateMode(model);
		thymeleaf.setLocal(locale);
		thymeleaf.setEncoding(encoding);
		thymeleaf.setPrefix(prefix);
		thymeleaf.setSuffix(suffix);
		thymeleaf.setCacheAble(cacheAble);
		thymeleaf.initializationEngine();

		return thymeleaf;
	}

	public static FileContentThymeleaf getFileContentThymeleaf(TemplateMode model, Locale locale, String encoding, String prefix, String suffix, boolean cacheAble, Thymeleaf.CONTEXT_TYPE contextType){
		FileContentThymeleaf thymeleaf = new FileContentThymeleaf(contextType);
		thymeleaf.setTemplateMode(model);
		thymeleaf.setLocal(locale);
		thymeleaf.setEncoding(encoding);
		thymeleaf.setPrefix(prefix);
		thymeleaf.setSuffix(suffix);
		thymeleaf.setCacheAble(cacheAble);
		thymeleaf.initializationEngine();

		return thymeleaf;
	}

	public static EngineExistsThymeleaf getEngineExistsThymeleaf(TemplateEngine engine){
		return getEngineExistsThymeleaf(engine, Application.getContext().getLocale());
	}

	public static EngineExistsThymeleaf getEngineExistsThymeleaf(TemplateEngine engine, Locale locale){
		EngineExistsThymeleaf thymeleaf = new EngineExistsThymeleaf(engine);
		thymeleaf.setLocal(locale);
		return thymeleaf;
	}

	public static EngineExistsThymeleaf getEngineExistsThymeleaf(TemplateEngine engine, Locale locale, Thymeleaf.CONTEXT_TYPE contextType){
		EngineExistsThymeleaf thymeleaf = new EngineExistsThymeleaf(engine, contextType);
		thymeleaf.setLocal(locale);
		return thymeleaf;
	}

}
