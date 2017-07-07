package org.rootservices.otter.servlet.async;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.AsyncContextEvent;
import org.rootservices.otter.gateway.servlet.ServletGateway;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.io.IOException;


public class OtterAsyncListener implements AsyncListener {
    protected static Logger logger = LogManager.getLogger(OtterAsyncListener.class);

    public OtterAsyncListener() {}

    @Override
    public void onComplete(AsyncEvent event) throws IOException {
        logger.debug("Async Done: " + ((AsyncContextEvent) event).getPath());
    }

    @Override
    public void onError(AsyncEvent event) {
        Throwable t  = event.getThrowable();
        String msg = "Error: " + ((AsyncContextEvent) event).getPath() + " " + t.getMessage();
        logger.error(msg, t);
    }

    @Override
    public void onStartAsync(AsyncEvent event) {
        logger.debug("Async Started: " + ((AsyncContextEvent) event).getPath());
    }

    @Override
    public void onTimeout(AsyncEvent event) {
        logger.error("Async timeout: " + ((AsyncContextEvent) event).getPath());
    }
}