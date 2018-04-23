package org.rootservices.otter.servlet.async;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.AsyncContextEvent;
import org.rootservices.otter.gateway.servlet.ServletGateway;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class OtterAsyncListener implements AsyncListener {
    protected static Logger logger = LogManager.getLogger(OtterAsyncListener.class);

    public OtterAsyncListener() {}

    @Override
    public void onComplete(AsyncEvent event)  {
        AsyncContextEvent ace = (AsyncContextEvent) event;
        HttpServletRequest hsr = (HttpServletRequest) ace.getAsyncContext().getRequest();
        logger.debug("Async Done: " + hsr.getMethod() + " " + ace.getPath());
    }

    @Override
    public void onError(AsyncEvent event) {
        Throwable t  = event.getThrowable();
        AsyncContextEvent ace = (AsyncContextEvent) event;
        HttpServletRequest hsr = (HttpServletRequest) ace.getAsyncContext().getRequest();

        String msg = "Error: " + hsr.getMethod() + " " + ace.getPath() + " " + t.getMessage();
        logger.error(msg, t);
    }

    @Override
    public void onStartAsync(AsyncEvent event) {
        AsyncContextEvent ace = (AsyncContextEvent) event;
        HttpServletRequest hsr = (HttpServletRequest) ace.getAsyncContext().getRequest();

        logger.debug("Async Started: " + hsr.getMethod() + " " + ace.getPath());
    }

    @Override
    public void onTimeout(AsyncEvent event) {
        AsyncContextEvent ace = (AsyncContextEvent) event;
        HttpServletRequest hsr = (HttpServletRequest) ace.getAsyncContext().getRequest();

        logger.error("Async timeout: " + hsr.getMethod() + " " + ace.getPath());
    }
}