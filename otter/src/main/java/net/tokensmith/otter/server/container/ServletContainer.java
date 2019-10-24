package net.tokensmith.otter.server.container;

import java.net.URI;

/**
 * Created by tommackenzie on 4/3/16.
 */
public interface ServletContainer {
    void start() throws Exception;
    void stop() throws Exception;
    void join() throws Exception;
    URI getURI();
}
