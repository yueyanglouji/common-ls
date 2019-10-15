package l.s.common.thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

public class EngineExistsThymeleaf extends Thymeleaf{

	public EngineExistsThymeleaf(){
	}

	public EngineExistsThymeleaf(TemplateEngine engine){
		setEngine(engine);
	}

	@Override
	protected void setTemplateResolver(TemplateMode model) {
	}

	public void setEngine(TemplateEngine engine){
		this.engine = engine;
	}

	public EngineExistsThymeleaf setVariable(String name, Object value){

		context.setVariable(name, value);
		return this;
	}

}
