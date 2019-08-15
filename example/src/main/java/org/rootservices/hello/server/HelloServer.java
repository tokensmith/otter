package org.rootservices.hello.server;


import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.rootservices.hello.controller.html.HelloResource;
import org.rootservices.otter.config.OtterAppFactory;
import org.rootservices.otter.server.container.ServletContainer;
import org.rootservices.otter.server.container.ServletContainerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HelloServer {
    private static final Logger logger = LoggerFactory.getLogger(HelloServer.class);
    public static String DOCUMENT_ROOT = "/";
    public static int PORT = 8080;
    private static String REQUEST_LOG = "logs/jetty/jetty-test-yyyy_mm_dd.request.log";

    public static void main(String[] args) {

        // ServletContainer server = makeServer();
        ServletContainer server = makeServer();

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

    public static ServletContainer makeServer() {
        OtterAppFactory otterAppFactory = new OtterAppFactory();
        ServletContainerFactory servletContainerFactory = otterAppFactory.servletContainerFactory();

        List<ErrorPage> errorPages = new ArrayList<>();

        ServletContainer server = null;
        try {
            server = servletContainerFactory.makeServletContainer(
                    DOCUMENT_ROOT, HelloResource.class, PORT, REQUEST_LOG, errorPages
            );
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return server;
    }


    public static ServletContainer makeServerFromWar() {
        OtterAppFactory otterAppFactory = new OtterAppFactory();
        ServletContainerFactory servletContainerFactory = otterAppFactory.servletContainerFactory();

        URI war = null;
        try {
            war = new URI("");
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }

        List<ErrorPage> errorPages = new ArrayList<>();
        ServletContainer server = null;
        try {
            server = servletContainerFactory.makeServletContainerFromWar(
                    DOCUMENT_ROOT, war, PORT, REQUEST_LOG, errorPages
            );
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return server;
    }
}
