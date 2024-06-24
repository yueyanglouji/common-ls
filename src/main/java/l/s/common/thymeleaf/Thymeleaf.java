package l.s.common.thymeleaf;

import java.util.Locale;
import l.s.common.messagesource.GlobalResourceBundleMessageSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;
import org.thymeleaf.templatemode.TemplateMode;

public abstract class Thymeleaf {

	protected AbstractThymeleafContext context;

	protected TemplateEngine engine;

	protected TemplateMode mode;

	protected TemplateMessageResolver templateMessageResolver;
	
	public Thymeleaf(){
		try {
			Class.forName("javax.servlet.http.HttpServletRequest");
			this.context = new JavaxThymeleafContext();
			return;
		} catch (ClassNotFoundException e) {
			//
		}
		try {
			Class.forName("jakarta.servlet.http.HttpServletRequest");
			this.context = new JakartaThymeleafContext();
			return;
		} catch (ClassNotFoundException e) {
			//
		}
		this.context = new ThymeleafContext();
	}

	public Thymeleaf(CONTEXT_TYPE contextType){
		if(contextType == CONTEXT_TYPE.jakartaServlet){
			this.context = new JakartaThymeleafContext();
		}else if(contextType == CONTEXT_TYPE.javaxServlet) {
			this.context = new JavaxThymeleafContext();
		}else {
			this.context = new ThymeleafContext();
		}
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

	/**
	 *
	 * @param request javax.http.HttpServletRequest or jakarta.http.HttpServletRequest
	 * @param response javax.http.HttpServletResponse or jakarta.http.HttpServletResponse
	 * @return this
	 */
	public Thymeleaf setWebContext(Object request, Object response){
		context.setHttpServletRequestAndResponse(request, response);
		return this;
	}

	public String process(String template){
		return engine.process(template, context.getContext());
	}

	public enum CONTEXT_TYPE{
		javaxServlet,
		jakartaServlet,

		notWeb
	}
}
