package org.rootservices.otter.servlet;



import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.gateway.entity.Shape;
import org.rootservices.otter.gateway.servlet.ServletGateway;
import org.rootservices.otter.security.exception.SessionCtorException;
import org.rootservices.otter.servlet.async.OtterAsyncListener;
import org.rootservices.otter.servlet.async.ReadListenerImpl;


import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Entry Servlet for all incoming requests Otter will handle.
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 */
public abstract class OtterEntryServlet<S, U> extends HttpServlet {
    protected static Logger logger = LogManager.getLogger(OtterEntryServlet.class);
    protected OtterAppFactory<S, U> otterAppFactory;
    protected ServletGateway<S, U> servletGateway;

    @Override
    public void init() throws ServletException {
        otterAppFactory = new OtterAppFactory<S, U>();
        Configure<S, U> configure = makeConfigure();
        Shape<S> shape = configure.shape();
        try {
            servletGateway = otterAppFactory.servletGateway(shape);
        } catch (SessionCtorException e) {
            logger.error(e.getMessage(), e);
            throw new ServletException(e);
        }
        configure.routes(servletGateway);
    }

    public abstract Configure<S, U> makeConfigure();

    public void doAsync(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AsyncContext context = request.startAsync(request, response);
        AsyncListener asyncListener = new OtterAsyncListener();
        context.addListener(asyncListener);

        ServletInputStream input = request.getInputStream();
        ReadListener readListener = new ReadListenerImpl(servletGateway, input, context);
        input.setReadListener(readListener);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doAsync(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doAsync(req, resp);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doAsync(req, resp);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doAsync(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doAsync(req, resp);
    }

}

