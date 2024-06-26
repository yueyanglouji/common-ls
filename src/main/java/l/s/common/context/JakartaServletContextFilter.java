package l.s.common.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

public class JakartaServletContextFilter implements Filter{
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public void destroy() {
		
	}
	
	@SuppressWarnings({"rawtypes" })
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		ServletContext context = ServletContext.getContext();
		context.setRequestEntity(request);
		context.setResponseEntity(response);
		
		if(HttpServletRequest.class.isAssignableFrom(request.getClass())){
			HttpServletRequest r = (HttpServletRequest) request;
			Enumeration headerNames = r.getHeaderNames();
			while(headerNames.hasMoreElements()){
				String headerName = (String)headerNames.nextElement();
				context.addHeaders(headerName, r.getHeader(headerName));
			}
			
			Enumeration paramNames = r.getAttributeNames();
			while(paramNames.hasMoreElements()){
				String paramName = (String)paramNames.nextElement();
				context.addParameters(paramName, r.getAttribute(paramName));
			}
			
			paramNames = r.getParameterNames();
			while(paramNames.hasMoreElements()){
				String paramName = (String)paramNames.nextElement();
				Object value = r.getParameter(paramName);
				if(value == null){
					value = r.getParameterValues(paramName);
				}
				context.addParameters(paramName, value);
			}
			
			context.setRequestUrl(r.getRequestURL().toString());
			
			String clientHost;
			String xForwardedFor = (String)context.getHeader("X-Forwarded-For");
			if(xForwardedFor != null && !xForwardedFor.equals("")){
				clientHost = xForwardedFor;
			}else{
				clientHost = r.getRemoteHost();
			}
			context.setClientHost(clientHost);
			
			log.debug("RequestUrl :: " + context.getRequestUrl());
			log.debug("ClientHost :: " + context.getClientHost());
			log.debug(context.getHeaders().toString());
			log.debug(context.getParameters().toString());
			
		}
		
		chain.doFilter(request, response);
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

	
}
