package org.rootservices.otter.servlet.async;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.gateway.servlet.GatewayResponse;
import org.rootservices.otter.gateway.servlet.ServletGateway;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


public class ReadListenerImpl implements ReadListener {
    protected static Logger logger = LogManager.getLogger(ReadListenerImpl.class);
    private ServletGateway servletGateway;
    private ServletInputStream input = null;
    private AsyncContext ac = null;
    private Queue<byte[]> queue = new LinkedBlockingQueue<byte[]>();

    public ReadListenerImpl(ServletGateway sg, ServletInputStream in, AsyncContext c) {
        servletGateway = sg;
        input = in;
        ac = c;
    }

    @Override
    public void onDataAvailable() throws IOException {

        int len = -1;
        byte fixedBuffer[] = new byte[1024];
        ByteArrayOutputStream variableBuffer = new ByteArrayOutputStream();

        while (input.isReady() && (len = input.read(fixedBuffer)) != -1 && !input.isFinished()) {
            int start = variableBuffer.size();
            variableBuffer.write(fixedBuffer, start, len);
        }
        queue.add(variableBuffer.toByteArray());
    }


    @Override
    public void onAllDataRead() throws IOException {
        HttpServletRequest request = (HttpServletRequest) ac.getRequest();
        HttpServletResponse response = (HttpServletResponse) ac.getResponse();
        byte[] body = queueToByteArray(queue);
        GatewayResponse gatewayResponse = servletGateway.processRequest(request, response, body);

        if (gatewayResponse.getPayload().isPresent()) {
            // its a API .. json
            Queue out = byteArrayToQueue(gatewayResponse.getPayload().get(), 1024);
            ServletOutputStream output = response.getOutputStream();
            WriteListener writeListener = new WriteListenerImpl(output, out, ac);
            output.setWriteListener(writeListener);
        } else if (gatewayResponse.getTemplate().isPresent()){
            // its a jsp.. dispatch to it.
            ac.dispatch(request.getServletContext(), gatewayResponse.getTemplate().get());
        } else {
            // probably the not found resource.
            ac.complete();
        }
    }

    public String queueToString(Queue<byte[]> queue) {
        StringBuilder sb = new StringBuilder();
        while (queue.peek() != null) {
            String data = new String(queue.poll());
            sb.append(data);
        }
        return sb.toString();
    }

    public byte[] queueToByteArray(Queue<byte[]> queue) {
        ByteArrayOutputStream to = new ByteArrayOutputStream();
        while (queue.peek() != null) {
            try {
                to.write(queue.poll());
            } catch (IOException e) {
                // TODO: #16
            }
        }
        return to.toByteArray();
    }

    @Override
    public void onError(Throwable t) {
        logger.error(t.getMessage(), t);
        ac.complete();
    }

    public Queue byteArrayToQueue(byte[] source, int chunksize) {
        Queue<byte[]> out = new LinkedBlockingQueue<byte[]>();

        int start = 0;
        while (start < source.length) {
            int end = Math.min(source.length, start + chunksize);
            out.add(Arrays.copyOfRange(source, start, end));
            start += chunksize;
        }

        return out;
    }
}
