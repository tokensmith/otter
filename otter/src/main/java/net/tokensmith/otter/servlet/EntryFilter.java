package net.tokensmith.otter.servlet;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows requests to be routed to Otter's Entry Servlet or directly
 * to the servlet container. The need for this filter is for rendering jsp
 * servlets and possibly static assets.
 *
 * If you'd like to have a different regex then
 *  - extend this class
 *  - don't forget to include the @WebFilter annotation
 *  - override staticAssetsRegex()
 *  - HttpServerConfig.Builder().filterClass(YourFillter.class) in the app's main method.
 */
@WebFilter(filterName = "EntryFilter", asyncSupported = true, urlPatterns = {"/*"})
public class EntryFilter implements Filter {
    protected Pattern staticAssetsPattern;
    protected static String OTTER_PREFIX = "/app";
    protected static String FORWARD_URI = OTTER_PREFIX + "%s";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String context = req.getRequestURI();
        Matcher staticAssetsMatcher = staticAssetsPattern().matcher(context);
        if(staticAssetsMatcher.matches()) {
            // this will go to the container, not otter's entry servlet.
            chain.doFilter(request, response);
        } else {
            // this will be routed to otter's entry servlet
            request.getRequestDispatcher(String.format(FORWARD_URI, context)).forward(request, response);
        }
    }

    protected Pattern staticAssetsPattern() {
        if (Objects.isNull(this.staticAssetsPattern)) {
            staticAssetsPattern = Pattern.compile(staticAssetsRegex());
        }
        return staticAssetsPattern;
    }

    protected String staticAssetsRegex() {
        return "(.*).(js|css|map)";
    }

    @Override
    public void destroy() {

    }
}
