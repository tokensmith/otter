package org.rootservices.otter.servlet.async;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.gateway.servlet.ServletGateway;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class OtterAsyncListener implements AsyncListener {
    protected static Logger logger = LogManager.getLogger(OtterAsyncListener.class);
    private ServletGateway servletGateway;

    public OtterAsyncListener(ServletGateway servletGateway) {
        this.servletGateway = servletGateway;
    }

    @Override
    public void onComplete(AsyncEvent event) throws IOException {
        logger.debug("Async Done");
        HttpServletRequest request = (HttpServletRequest) event.getSuppliedRequest();
        HttpServletResponse response = (HttpServletResponse) event.getSuppliedResponse();
        servletGateway.processRequest(request, response);
    }

    @Override
    public void onError(AsyncEvent event) {
        Throwable t  = event.getThrowable();
        logger.error(t.getMessage(), t);
    }

    @Override
    public void onStartAsync(AsyncEvent event) {
    }

    @Override
    public void onTimeout(AsyncEvent event) {
        logger.error("timeout");
    }
}