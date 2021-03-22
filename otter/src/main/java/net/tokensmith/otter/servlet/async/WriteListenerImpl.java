package net.tokensmith.otter.servlet.async;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.IOException;
import java.util.Objects;
import java.util.Queue;


public class WriteListenerImpl implements WriteListener {
    protected static Logger LOGGER = LoggerFactory.getLogger(WriteListenerImpl.class);
    private ServletOutputStream output = null;
    private Queue queue = null;
    private AsyncContext context = null;

    public WriteListenerImpl(ServletOutputStream sos, Queue q, AsyncContext c) {
        output = sos;
        queue = q;
        context = c;
    }

    @Override
    public void onWritePossible() throws IOException {

        while (Objects.nonNull(queue.peek()) && output.isReady()) {
            byte[] data = (byte[]) queue.poll();
            output.write(data, 0, data.length);
        }
        if (Objects.isNull(queue.peek())) {
            context.complete();
        }
    }

    @Override
    public void onError(Throwable t) {
        context.complete();
        LOGGER.error(t.getMessage(), t);
    }
}