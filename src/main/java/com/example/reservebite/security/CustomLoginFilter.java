//package com.example.reservebite.security;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//
//public class CustomLoginFilter implements Filter {
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        Filter.super.init(filterConfig);
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
//        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
//
//        String requestURI = httpRequest.getRequestURI();
//
//        if ("/".equals(requestURI) || "/index.html".equals(requestURI)) {
//            filterChain.doFilter(servletRequest, servletResponse);
//        } else {
//            httpResponse.sendRedirect("/login");
//        }
//    }
//
//    @Override
//    public void destroy() {
//        Filter.super.destroy();
//    }
//}
