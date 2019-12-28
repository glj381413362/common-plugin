package com.enhance.core.filter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @author gongliangjun 2019/07/01 11:18
 */
public class AddTraceIdFilter implements Filter {


	/** logger */
	private static final Logger log = LoggerFactory.getLogger(AddTraceIdFilter.class);
	private final static String TRACEID  = "X-B3-TraceId";
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String traceId = request.getHeader("X-B3-TraceId");
		if (StringUtils.isEmpty(traceId)) {
			log.info("添加TraceId");
			traceId = UUID.randomUUID().toString();
			request.setAttribute("X-B3-TraceId", traceId);
		}
		MDC.put(TRACEID,traceId);
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {

	}
}
