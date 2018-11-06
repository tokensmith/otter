package org.rootservices.otter.servlet;



import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.gateway.entity.Shape;
import org.rootservices.otter.gateway.servlet.ServletGateway;
import org.rootservices.otter.security.exception.SessionCtorException;
import org.rootservices.otter.servlet.async.OtterAsyncListener;
import org.rootservices.otter.servlet.async.ReadListenerImpl;
import org.rootservices.otter.translatable.Translatable;


import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Entry Servlet for all incoming requests Otter will handle.
 */
public abstract class OtterEntryServlet extends HttpServlet {
    public static final String DESTROYING_SERVLET = "destroying servlet";
    public static final String INIT_AGAIN = "Servlet initializing after being destroyed. Not initializing Otter again.";
    public static final String INIT_OTTER = "Initializing Otter";
    protected static Logger LOGGER = LogManager.getLogger(OtterEntryServlet.class);
    protected static OtterAppFactory otterAppFactory;
    protected static ServletGateway servletGateway;

    // async i/o read chunk size
    protected static Integer DEFAULT_READ_CHUNK_SIZE = 1024;
    protected static Integer readChunkSize;

    @Override
    public void init() throws ServletException {

        if (hasBeenDestroyed()) {
            LOGGER.info(INIT_AGAIN);
        } else {
            LOGGER.info(INIT_OTTER);
            initOtter();
        }
    }

    public void initOtter() throws ServletException {
        otterAppFactory = new OtterAppFactory();
        Configure configure = makeConfigure();
        Shape shape = configure.shape();
        List<Group<? extends DefaultSession, ? extends DefaultUser, ? extends Translatable>> groups = configure.groups();
        try {
            servletGateway = otterAppFactory.servletGateway(shape, groups);
        } catch (SessionCtorException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServletException(e);
        }

        configure.routes(servletGateway);

        // async i/o read chunk size.
        readChunkSize = (shape.getReadChunkSize() != null) ? shape.getReadChunkSize() : DEFAULT_READ_CHUNK_SIZE;

    }

    /**
     * Determines if this servlet has been destroyed. It is possible to check because
     * otterAppFactory and servletGateway are static.
     *
     * @return True if its been destroyed before. False if it has not been destroyed.
     */
    protected Boolean hasBeenDestroyed() {
        Boolean hasBeenDestroyed = false;
        if (otterAppFactory != null || servletGateway != null) {
            hasBeenDestroyed = true;
        }
        return hasBeenDestroyed;
    }

    public abstract Configure makeConfigure();

    public void doAsync(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AsyncContext context = request.startAsync(request, response);
        AsyncListener asyncListener = new OtterAsyncListener();
        context.addListener(asyncListener);

        ServletInputStream input = request.getInputStream();
        ReadListener readListener = new ReadListenerImpl(servletGateway, input, context, readChunkSize);
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

    @Override
    public void destroy() {
        LOGGER.info(DESTROYING_SERVLET);
        super.destroy();
    }
}

