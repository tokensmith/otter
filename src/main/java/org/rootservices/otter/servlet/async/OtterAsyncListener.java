package org.rootservices.otter.servlet.async;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.gateway.servlet.ServletGateway;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
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
    }

    @Override
    public void onError(AsyncEvent event) {
        Throwable t  = event.getThrowable();
        logger.error(t.getMessage(), t);
    }

    @Override
    public void onStartAsync(AsyncEvent event) {
        logger.debug("Async Started");
    }

    @Override
    public void onTimeout(AsyncEvent event) {
        logger.error("timeout");
    }
}