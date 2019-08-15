package org.rootservices.hello.server;


import org.rootservices.hello.controller.html.HelloResource;
import org.rootservices.otter.server.HttpServer;
import org.rootservices.otter.server.HttpServerConfig;
import java.util.ArrayList;


public class HelloServer extends HttpServer {
    public static String DOCUMENT_ROOT = "/";
    public static int PORT = 8080;
    private static String REQUEST_LOG = "logs/jetty/jetty-test-yyyy_mm_dd.request.log";

    public static void main(String[] args) {

        HttpServerConfig config = new HttpServerConfig(
                DOCUMENT_ROOT, PORT, REQUEST_LOG, HelloResource.class, new ArrayList<>()
        );
        run(config);
    }
}
