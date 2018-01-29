package org.rootservices.otter.servlet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.RestResource;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows requests to be routed to Otter's Entry Servlet or directly
 * to the servlet container. The need for this filter is for rendering jsp
 * servlets and possibly static assets.
 */
@WebFilter(filterName = "EntryFilter", asyncSupported = true)
public class EntryFilter implements Filter {
    private static Pattern TEMPLATE_PATTERN = Pattern.compile("(.*).(jsp|jspf|jspx|xsp|JSP|JSPF|JSPX|XSP|js|css)");
    private static String OTTER_PREFIX = "/app";
    private static String FORWARD_URI = OTTER_PREFIX + "%s";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String context = req.getRequestURI();
        Matcher matcher = TEMPLATE_PATTERN.matcher(context);
        if(matcher.matches() || context.toString().equals("/")) {
            // this will go to the container, not otter's entry servlet.
            chain.doFilter(request, response);
        } else {
            // this will be routed to otter's entry servlet
            request.getRequestDispatcher(String.format(FORWARD_URI, context)).forward(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
