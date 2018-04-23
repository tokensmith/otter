package org.rootservices.otter.server.container;

import org.eclipse.jetty.server.Server;

import java.net.URI;


/**
 * Created by tommackenzie on 4/3/16.
 */
public class ServletContainerImpl implements ServletContainer {
    private Server server;

    public ServletContainerImpl(Server server) {
        this.server = server;
    }

    @Override
    public void start() throws Exception {
        server.setDumpAfterStart(true);
        server.start();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

    @Override
    public void join() throws Exception {
        server.join();
    }

    @Override
    public URI getURI() {
        return server.getURI();
    }
}
