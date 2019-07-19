package org.rootservices.otter.servlet.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jetty.server.AsyncContextEvent;


import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;



public class OtterAsyncListener implements AsyncListener {
    protected static Logger LOGGER = LoggerFactory.getLogger(OtterAsyncListener.class);

    public OtterAsyncListener() {}

    @Override
    public void onComplete(AsyncEvent event)  {
        AsyncContextEvent ace = (AsyncContextEvent) event;
        HttpServletRequest hsr = (HttpServletRequest) ace.getAsyncContext().getRequest();
        LOGGER.debug("Async Done: " + hsr.getMethod() + " " + ace.getPath());
    }

    @Override
    public void onError(AsyncEvent event) {
        Throwable t  = event.getThrowable();
        AsyncContextEvent ace = (AsyncContextEvent) event;
        HttpServletRequest hsr = (HttpServletRequest) ace.getAsyncContext().getRequest();

        String msg = "ErrorPayload: " + hsr.getMethod() + " " + ace.getPath() + " " + t.getMessage();
        LOGGER.error(msg, t);
    }

    @Override
    public void onStartAsync(AsyncEvent event) {
        AsyncContextEvent ace = (AsyncContextEvent) event;
        HttpServletRequest hsr = (HttpServletRequest) ace.getAsyncContext().getRequest();

        LOGGER.debug("Async Started: " + hsr.getMethod() + " " + ace.getPath());
    }

    @Override
    public void onTimeout(AsyncEvent event) {
        AsyncContextEvent ace = (AsyncContextEvent) event;
        HttpServletRequest hsr = (HttpServletRequest) ace.getAsyncContext().getRequest();

        LOGGER.error("Async timeout: " + hsr.getMethod() + " " + ace.getPath());
    }
}