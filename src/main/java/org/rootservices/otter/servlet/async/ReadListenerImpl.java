package org.rootservices.otter.servlet.async;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.gateway.servlet.ServletGateway;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


public class ReadListenerImpl implements ReadListener {
    protected static Logger logger = LogManager.getLogger(ReadListenerImpl.class);
    private ServletGateway servletGateway;
    private ServletInputStream input = null;
    private HttpServletRequest req = null;
    private HttpServletResponse res = null;
    private AsyncContext ac = null;
    private Queue queue = new LinkedBlockingQueue();

    public ReadListenerImpl(ServletGateway sg, ServletInputStream in, HttpServletRequest req, HttpServletResponse res, AsyncContext c) {
        servletGateway = sg;
        input = in;
        req = req;
        res = res;
        ac = c;
    }

    @Override
    public void onDataAvailable() throws IOException {
        StringBuilder sb = new StringBuilder();
        int len = -1;
        byte b[] = new byte[1024];
        while (input.isReady() && (len = input.read(b)) != -1 && !input.isFinished()) {
            String data = new String(b, 0, len);
            sb.append(data);
        }
        queue.add(sb.toString());
    }

    @Override
    public void onAllDataRead() throws IOException {
        ServletOutputStream output = res.getOutputStream();
        WriteListener writeListener = new WriteListenerImpl(output, queue, ac);
        output.setWriteListener(writeListener);
        servletGateway.processRequest(req, res);
    }

    @Override
    public void onError(Throwable t) {
        ac.complete();
    }
}
