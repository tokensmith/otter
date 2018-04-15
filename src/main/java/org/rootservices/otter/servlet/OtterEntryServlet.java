package org.rootservices.otter.servlet;



import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.gateway.servlet.ServletGateway;
import org.rootservices.otter.servlet.async.OtterAsyncListener;
import org.rootservices.otter.servlet.async.ReadListenerImpl;


import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



public class OtterEntryServlet extends HttpServlet {
    protected static Logger logger = LogManager.getLogger(OtterEntryServlet.class);
    protected OtterAppFactory appFactory;
    protected ServletGateway servletGateway;

    @Override
    public void init() throws ServletException {
        appFactory = new OtterAppFactory();
        servletGateway = appFactory.servletGateway();

    }

    public void doAsync(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AsyncContext context = request.startAsync(request, response);
        AsyncListener asyncListener = new OtterAsyncListener();
        context.addListener(asyncListener);

        ServletInputStream input = request.getInputStream();
        ReadListener readListener = new ReadListenerImpl(servletGateway, input, context);
        input.setReadListener(readListener);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doAsync(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doAsync(req, resp);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doAsync(req, resp);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doAsync(req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doAsync(req, resp);
    }

}

