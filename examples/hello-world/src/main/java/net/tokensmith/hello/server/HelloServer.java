package net.tokensmith.hello.server;


import net.tokensmith.hello.controller.html.HelloResource;
import net.tokensmith.otter.server.HttpServer;
import net.tokensmith.otter.server.HttpServerConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HelloServer extends HttpServer {
    public static String DOCUMENT_ROOT = "/";
    public static int PORT = 8080;
    private static String REQUEST_LOG = "logs/jetty/jetty-test-yyyy_mm_dd.request.log";

    public static void main(String[] args) {

        List<String> gzipMimeTypes = Arrays.asList(
                "text/html", "text/plain", "text/xml",
                "text/css", "application/javascript", "text/javascript",
                "application/json");

        HttpServerConfig config = new HttpServerConfig(
                DOCUMENT_ROOT, PORT, REQUEST_LOG, HelloResource.class, gzipMimeTypes, new ArrayList<>()
        );
        run(config);
    }
}
