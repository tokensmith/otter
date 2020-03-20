package net.tokensmith.otter.servlet;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.tokensmith.otter.config.OtterAppFactory;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.gateway.Configure;
import net.tokensmith.otter.gateway.entity.Group;
import net.tokensmith.otter.gateway.entity.rest.RestGroup;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.gateway.servlet.ServletGateway;
import net.tokensmith.otter.security.exception.SessionCtorException;
import net.tokensmith.otter.servlet.async.OtterAsyncListener;
import net.tokensmith.otter.servlet.async.ReadListenerImpl;


import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.List;


/**
 * Entry Servlet for all incoming requests Otter will handle.
 */
public abstract class OtterEntryServlet extends HttpServlet {
    public static final String DESTROYING_SERVLET = "destroying servlet";
    public static final String INIT_AGAIN = "Servlet initializing after being destroyed. Not initializing Otter again.";
    public static final String INIT_OTTER = "Initializing Otter - Starting";
    public static final String INIT_OTTER_DONE = "Initializing Otter - Done - %s ms";
    protected static Logger LOGGER = LoggerFactory.getLogger(OtterEntryServlet.class);
    protected static OtterAppFactory otterAppFactory;
    protected static ServletGateway servletGateway;

    // async i/o read chunk size
    protected static Integer DEFAULT_READ_CHUNK_SIZE = 1024;
    protected static Integer readChunkSize;

    @Override
    public void init() throws ServletException {

        Long start = Instant.now().toEpochMilli();
        if (hasBeenDestroyed()) {
            LOGGER.info(INIT_AGAIN);
        } else {
            LOGGER.info(INIT_OTTER);
            initOtter();
        }
        Long end = Instant.now().toEpochMilli();
        LOGGER.info(String.format(INIT_OTTER_DONE, end - start));
    }

    public void initOtter() throws ServletException {
        otterAppFactory = new OtterAppFactory();
        Configure configure = makeConfigure();
        Shape shape = configure.shape();

        List<Group<? extends DefaultSession, ? extends DefaultUser>> groups = configure.groups();
        List<RestGroup<? extends DefaultSession, ? extends DefaultUser>> restGroups = configure.restGroups();

        try {
            servletGateway = otterAppFactory.servletGateway(shape, groups, restGroups);
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

