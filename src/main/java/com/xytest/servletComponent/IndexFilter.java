package com.xytest.servletComponent;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by Administrator on 2017/05/20.
 */
@Slf4j
//@WebFilter(urlPatterns = "/*", filterName = "indexFilter")
public class IndexFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("init IndexFilter");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("doFilter IndexFilter");
        filterChain.doFilter(servletRequest,servletResponse);
        log.info("filter after");
    }

    @Override
    public void destroy() {

    }
}
