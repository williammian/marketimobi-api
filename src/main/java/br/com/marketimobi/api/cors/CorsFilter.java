package br.com.marketimobi.api.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.com.marketimobi.api.config.property.MarketimobiApiProperty;

@Profile("oauth-security")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

	@Autowired
	private MarketimobiApiProperty marketimobiApiProperty;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		//response.setHeader("Access-Control-Allow-Origin", marketimobiApiProperty.getOriginPermitida());
		//response.setHeader("Access-Control-Allow-Origin", "*");
		
		response.setHeader("Access-Control-Allow-Origin", isOriginValido(request.getHeader("Origin")) ? request.getHeader("Origin") : marketimobiApiProperty.getOriginPermitida());
		
        response.setHeader("Access-Control-Allow-Credentials", "true");
		
		//if ("OPTIONS".equals(request.getMethod()) && marketimobiApiProperty.getOriginPermitida().equals(request.getHeader("Origin"))) {
        if ("OPTIONS".equals(request.getMethod()) && isOriginValido(request.getHeader("Origin"))) {
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
        	response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
        	response.setHeader("Access-Control-Max-Age", "3600");
			
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(req, resp);
		}
		
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
	
	private boolean isOriginValido(String origin) {
		if(origin != null 
				&& (origin.contains("marketimobi.com.br") 
						|| origin.contains("localhost")
							|| marketimobiApiProperty.getOriginPermitida().equals(origin))) {
			return true;
		}else {
			return false;
		}
	}

}
