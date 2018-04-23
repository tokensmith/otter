package org.rootservices.otter.security.csrf.filter;

import org.rootservices.otter.security.RandomString;
import org.rootservices.otter.security.csrf.Csrf;
import org.rootservices.otter.security.csrf.SynchronizerToken;
import org.rootservices.otter.security.csrf.exception.CsrfException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by tommackenzie on 7/30/15.
 */
public class CsrfPreventionFilter implements Filter {

    private FilterConfig filterConfig;
    private Csrf csrf;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.csrf = new SynchronizerToken(new RandomString());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            csrf.checkTokens((HttpServletRequest) request);
        } catch (CsrfException e) {
            failedCsrf((HttpServletResponse) response);
            return;
        }
        chain.doFilter(request, response);
    }

    protected void failedCsrf(HttpServletResponse httpResponse) throws IOException {
        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Override
    public void destroy() {
        filterConfig = null;
    }


    public void setCsrf(Csrf csrf) {
        this.csrf = csrf;
    }
}
