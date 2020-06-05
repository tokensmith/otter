package net.tokensmith.otter.servlet.async;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.tokensmith.otter.gateway.servlet.GatewayResponse;
import net.tokensmith.otter.gateway.servlet.ServletGateway;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


public class ReadListenerImpl implements ReadListener {
    protected static Logger LOGGER = LoggerFactory.getLogger(ReadListenerImpl.class);
    private ServletGateway servletGateway;
    private ServletInputStream input = null;
    private AsyncContext ac = null;
    private Integer readChunkSize;
    private Queue<byte[]> queue = new LinkedBlockingQueue<byte[]>();

    public ReadListenerImpl(ServletGateway sg, ServletInputStream in, AsyncContext ac, Integer readChunkSize) {
        this.servletGateway = sg;
        this.input = in;
        this.ac = ac;
        this.readChunkSize = readChunkSize;
    }

    @Override
    public void onDataAvailable() throws IOException {

        int len = -1;
        byte fixedBuffer[] = new byte[readChunkSize];
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
            // its an API .. json
            Queue out = byteArrayToQueue(gatewayResponse.getPayload().get(), gatewayResponse.getWriteChunkSize());
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

    public byte[] queueToByteArray(Queue<byte[]> queue) {
        ByteArrayOutputStream to = new ByteArrayOutputStream();
        while (Objects.nonNull(queue.peek())) {
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
        LOGGER.error(t.getMessage(), t);
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
