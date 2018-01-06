package org.rootservices.otter.server.container;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.rootservices.otter.server.container.builder.WebAppContextBuilder;
import org.rootservices.otter.server.path.CompiledClassPath;
import org.rootservices.otter.server.path.WebAppPath;
import org.rootservices.otter.servlet.EntryFilter;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;


/**
 * Created by tommackenzie on 4/3/16.
 *
 * A factory that creates a ServletContainer.
 */
public class ServletContainerFactory {
    protected static Logger logger = LogManager.getLogger(ServletContainerFactory.class);
    private static String DIR_ALLOWED_KEY = "org.eclipse.jetty.servlet.Default.dirAllowed";
    private static String WEB_XML = "/WEB-INF/web.xml";
    private CompiledClassPath compiledClassPath;
    private WebAppPath webAppPath;

    public ServletContainerFactory(CompiledClassPath compiledClassPath, WebAppPath webAppPath) {
        this.compiledClassPath = compiledClassPath;
        this.webAppPath = webAppPath;
    }

    /**
     *
     * @param documentRoot root path for the servlet container to run. example, "/"
     * @param clazz a class in your project.
     * @param port the port the container should use. 0 will randomly assign a port.
     * @return a configured instance of ServletContainer
     * @throws URISyntaxException if an issue occurred constructing a URI
     * @throws IOException if issues come up regarding webapp or containerResources
     */
    public ServletContainer makeServletContainer(String documentRoot, Class clazz, int port, String requestLog) throws URISyntaxException, IOException {
        URI compliedClassPath = compiledClassPath.getForClass(clazz);
        URI webApp = webAppPath.fromClassURI(compliedClassPath);

        return makeServletContainer(documentRoot, webApp, compliedClassPath, port, requestLog);
    }

    public ServletContainer makeServletContainer(String documentRoot, Class clazz, String customWebAppLocation, int port, String requestLog) throws URISyntaxException, IOException {
        URI compliedClassPath = compiledClassPath.getForClass(clazz);
        URI webApp = webAppPath.fromClassURI(compliedClassPath, customWebAppLocation);

        return makeServletContainer(documentRoot, webApp, compliedClassPath, port, requestLog);
    }

    /**
     *
     * @param documentRoot root path for the servlet container to run. example, "/"
     * @param webApp absolute file path to the webapp directory in your project.
     * @param compliedClassPath absolute file path to, target/classes/ in your project.
     * @param port the port the container should use. 0 will randomly assign a port.
     * @return a configured instance of ServletContainer
     * @throws IOException if issues come up regarding webapp or containerResources
     */
    public ServletContainer makeServletContainer(String documentRoot, URI webApp, URI compliedClassPath, int port, String requestLog) throws IOException {
        logger.debug("Web App location: " + webApp.toURL());
        logger.debug("Compiled Class path: " + compliedClassPath.toURL());
        Server jetty = new Server(port);

        // dependencies for, WebAppContext
        Configuration[] configurations = makeConfigurations();
        PathResource containerResources = makeFileResource(compliedClassPath);
        String resourceBase = makeResourceBase(webApp);

        WebAppContext context;
        if (compliedClassPath.toURL().getFile().endsWith("war")) {
            logger.debug("Using a war file");
            context = makeWebAppContextForWAR(documentRoot, configurations, containerResources);
        } else {
            logger.debug("Not a war file");

            context = makeWebAppContext(
                    documentRoot, resourceBase, configurations, containerResources
            );
        }
        jetty.setHandler(context);

        ServerConnector serverConnector = makeServerConnector(jetty, port);
        jetty.setConnectors( new Connector[] { serverConnector } );

        // request logs
        NCSARequestLog log = makeRequestLog(requestLog);

        jetty.setRequestLog(log);


        ServletContainer server = new ServletContainerImpl(jetty);
        return server;
    }

    public ServletContainer makeServletContainerFromWar(String documentRoot, URI warFilePath, int port, String requestLog) throws IOException {

        Configuration[] configurations = makeConfigurations();
        PathResource warFileResource = makeFileResource(warFilePath);
        WebAppContext context = makeWebAppContextForWAR(documentRoot, configurations, warFileResource);

        Server jetty = new Server(port);
        jetty.setHandler(context);

        ServerConnector serverConnector = makeServerConnector(jetty, port);
        jetty.setConnectors( new Connector[] { serverConnector } );

        // request logs
        NCSARequestLog log = makeRequestLog(requestLog);

        jetty.setRequestLog(log);



        ServletContainer server = new ServletContainerImpl(jetty);
        return server;
    }

    protected WebAppContext makeWebAppContext(String documentRoot, String resourceBase, Configuration[] configurations, PathResource containerResources) {

        WebAppContext webAppContext = new WebAppContextBuilder()
                .classLoader(Thread.currentThread().getContextClassLoader())
                .resourceBase(resourceBase)
                .configurations(configurations)
                .containerResource(containerResources)
                .initParameter(DIR_ALLOWED_KEY, "false")
                .contextPath(documentRoot)
                .parentLoaderPriority(true)
                .attribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                        ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$")

                .jspServet("org.eclipse.jetty.jsp.JettyJspServlet")
                .errorPageHandler(404, "/notFound")
                .stateless()
                .build();

        webAppContext.addFilter(EntryFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

        return webAppContext;
    }

    protected WebAppContext makeWebAppContextForWAR(String documentRoot, Configuration[] configurations, Resource war) {
        logger.debug("war: " + war.getURI().toString());

        WebAppContext webAppContext = new WebAppContextBuilder()
                .classLoader(Thread.currentThread().getContextClassLoader())
                .configurations(configurations)
                .initParameter(DIR_ALLOWED_KEY, "false")
                .contextPath(documentRoot)
                .parentLoaderPriority(true)
                .attribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                        ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$")
                .jspServet("org.eclipse.jetty.jsp.JettyJspServlet")
                .errorPageHandler(404, "/notFound")
                .stateless()
                .build();

        webAppContext.addFilter(EntryFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        webAppContext.setExtractWAR(true);
        webAppContext.setWarResource(war);

        return webAppContext;
    }

    protected String makeResourceBase(URI webApp) throws MalformedURLException {
        Resource resourceBase = Resource.newResource(webApp);
        return String.valueOf(resourceBase);
    }

    protected String makeWebXmlPath(URI webApp) {
        return webApp.getPath() + WEB_XML;
    }

    protected PathResource makeFileResource(URI classPath) throws IOException {
        return new PathResource(classPath);
    }

    protected Configuration[] makeConfigurations() {
        return new Configuration[]{
                new WebInfConfiguration(),
                new WebXmlConfiguration(),
                new AnnotationConfiguration()
        };
    }

    protected ServerConnector makeServerConnector(Server server, int port) {
        // turn off jetty response header
        HttpConfiguration httpConfig = new HttpConfiguration();
        httpConfig.setSendServerVersion(false);

        HttpConnectionFactory httpFactory = new HttpConnectionFactory( httpConfig );
        ServerConnector serverConnector = new ServerConnector(server, httpFactory);
        serverConnector.setPort(port);

        return serverConnector;
    }

    protected NCSARequestLog makeRequestLog(String logFile) {
        NCSARequestLog requestLog = new NCSARequestLog(logFile);
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone("GMT");
        requestLog.setLogLatency(true);
        requestLog.setRetainDays(90);

        return requestLog;
    }
}
