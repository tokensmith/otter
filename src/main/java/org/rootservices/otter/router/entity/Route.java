package org.rootservices.otter.router.entity;

import javax.servlet.http.HttpServlet;
import java.util.regex.Pattern;


public class Route {
    private Pattern pattern;
    private HttpServlet httpServlet;

    public Route(Pattern pattern, HttpServlet httpServlet) {
        this.pattern = pattern;
        this.httpServlet = httpServlet;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public HttpServlet getHttpServlet() {
        return httpServlet;
    }

    public void setHttpServlet(HttpServlet httpServlet) {
        this.httpServlet = httpServlet;
    }
}
