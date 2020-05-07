package l.s.common.thymeleaf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import l.s.common.messagesource.GlobalResourceBundleMessageSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Thymeleaf {

	protected ThymeleafContext context;

	protected TemplateEngine engine;

	protected TemplateMode mode;

	protected TemplateMessageResolver templateMessageResolver;
	
	public Thymeleaf(){
		this.context = new ThymeleafContext();
	}

	public void setTemplateMode(TemplateMode mode){
		this.mode = mode;
	}

	public void initializationEngine(){
		if(engine == null){
			engine = new TemplateEngine();
			if(mode == null){
				mode = TemplateMode.HTML;
			}
			setTemplateResolver(mode);
			addMessageResolver();
		}
	}

	protected abstract void setTemplateResolver(TemplateMode model);

	protected void addMessageResolver(){
		templateMessageResolver = new TemplateMessageResolver();
		templateMessageResolver.setMessageSource(GlobalResourceBundleMessageSource.getInstance());
		engine.addMessageResolver(templateMessageResolver);
	}

	public Thymeleaf addExtentMessageResolver(final CustomMessageResolver messageResolver){
		engine.addMessageResolver(messageResolver);
		return this;
	}

	public void addDefaultMessage(String key, String value){
		this.templateMessageResolver.getStandardMessageResolver().addDefaultMessage(key, value);
	}

	protected void addLinkBuilder(){
		engine.addLinkBuilder(new StandardLinkBuilder());
	}

	public void setGlobalLocal(Locale locale){
		context.setGlobalLocale(locale);
	}

	public Thymeleaf setThreadLocal(Locale local){
		context.setThreadLocale(local);
		return this;
	}

	public Thymeleaf setVariable(String name, Object value){
		context.setVariable(name, value);
		return this;
	}

	public Thymeleaf setWebContext(HttpServletRequest request, HttpServletResponse response){
		context.setHttpServletRequestAndResponse(request, response);
		return this;
	}

	public String process(String template){
		return engine.process(template, context.getContext());
	}

}
