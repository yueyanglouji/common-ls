package l.s.common.thymeleaf;

import java.util.Locale;
import l.s.common.messagesource.GlobalResourceBundleMessageSource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;
import org.thymeleaf.templatemode.TemplateMode;

public abstract class Thymeleaf {

	protected TemplateEngine engine;

	protected TemplateMode mode;

	protected TemplateMessageResolver templateMessageResolver;

	protected  CONTEXT_TYPE contextType;

	protected Locale locale;

	public Thymeleaf(){
		this(null);
	}

	public Thymeleaf(CONTEXT_TYPE contextType){
		this.locale = Locale.getDefault();
		if(contextType == null){
			try {
				Class.forName("javax.servlet.http.HttpServletRequest");
				this.contextType = CONTEXT_TYPE.javaxServlet;
				return;
			} catch (ClassNotFoundException e) {
				//
			}
			try {
				Class.forName("jakarta.servlet.http.HttpServletRequest");
				this.contextType = CONTEXT_TYPE.jakartaServlet;
				return;
			} catch (ClassNotFoundException e) {
				//
			}
			this.contextType = CONTEXT_TYPE.notWeb;
		}else{
			this.contextType = contextType;
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

	public abstract ThymeleafExecutor createExecutor();

	public void setLocal(Locale locale) {
		this.locale = locale;
	}

	public enum CONTEXT_TYPE{
		javaxServlet,
		jakartaServlet,

		notWeb
	}
}
