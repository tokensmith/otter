package integration.app.hello.server;

import integration.app.hello.controller.HelloResource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.config.AppFactory;
import org.rootservices.otter.server.container.ServletContainer;
import org.rootservices.otter.server.container.ServletContainerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;


public class HelloServer {
    private static final Logger logger = LogManager.getLogger(HelloServer.class);
    public static String DOCUMENT_ROOT = "/";
    public static int PORT = 8080;
    private static String REQUEST_LOG = "logs/jetty/jetty-test-yyyy_mm_dd.request.log";

    public static void main(String[] args) {
        AppFactory otterAppFactory = new AppFactory();
        ServletContainerFactory servletContainerFactory = otterAppFactory.servletContainerFactory();
        File tempDirectory = new File("/tmp");

        ServletContainer server = null;
        try {
            server = servletContainerFactory.makeServletContainer(DOCUMENT_ROOT, HelloResource.class, PORT, tempDirectory, REQUEST_LOG);
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            logger.info("server starting");
            server.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        try {
            server.join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
