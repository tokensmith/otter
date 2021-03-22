package net.tokensmith.otter.server.container;

import net.tokensmith.otter.server.HttpServerConfig;
import net.tokensmith.otter.server.container.builder.WebAppContextBuilder;
import net.tokensmith.otter.server.path.CompiledClassPath;
import net.tokensmith.otter.server.path.WebAppPath;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.DispatcherType;
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
    protected static Logger logger = LoggerFactory.getLogger(ServletContainerFactory.class);
    private static String DIR_ALLOWED_KEY = "org.eclipse.jetty.servlet.Default.dirAllowed";
    private static String INCLUDE_JAR_PATTERN = "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern";
    private static String JARS_TO_INCLUDE = ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/jakarta.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$";
    private static String JSP_SERVLET = "org.eclipse.jetty.jsp.JettyJspServlet";
    private static String FALSE = "false";
    private CompiledClassPath compiledClassPath;
    private WebAppPath webAppPath;

    public ServletContainerFactory(CompiledClassPath compiledClassPath, WebAppPath webAppPath) {
        this.compiledClassPath = compiledClassPath;
        this.webAppPath = webAppPath;
    }

    /**
     *
     * @param containerConfig configuration for the servlet container
     * @return a configured instance of ServletContainer
     * @throws URISyntaxException if an issue occurred constructing a URI
     * @throws IOException if issues come up regarding webapp or containerResources
     */
    public ServletContainer makeServletContainer(HttpServerConfig containerConfig) throws URISyntaxException, IOException {
        URI compliedClassPath = compiledClassPath.getForClass(containerConfig.getClazz());
        URI webApp = webAppPath.fromClassURI(compliedClassPath);

        return makeServletContainer(webApp, compliedClassPath, containerConfig);
    }

    /**
     *
     * @param webApp absolute file path to the webapp directory in your project.
     * @param compliedClassPath absolute file path to, target/classes/ in your project.
     * @param containerConfig configuration for the servlet container
     * @return a configured instance of ServletContainer
     * @throws IOException if issues come up regarding webapp or containerResources
     */
    public ServletContainer makeServletContainer(URI webApp, URI compliedClassPath, HttpServerConfig containerConfig) throws IOException {
        logger.debug("Web App location: " + webApp.toURL());
        logger.debug("Compiled Class path: " + compliedClassPath.toURL());
        Server jetty = new Server(containerConfig.getPort());

        // dependencies for, WebAppContext
        Configuration[] configurations = makeConfigurations();
        PathResource containerResources = makeFileResource(compliedClassPath);
        String resourceBase = makeResourceBase(webApp);

        WebAppContext context;
        if (compliedClassPath.toURL().getFile().endsWith("war")) {
            logger.debug("Using a war file");
            context = makeWebAppContextForWAR(configurations, containerResources, containerConfig);
        } else {
            logger.debug("Not a war file");
            context = makeWebAppContext(resourceBase, configurations, containerResources, containerConfig);
        }
        jetty.setHandler(context);

        ServerConnector serverConnector = makeServerConnector(jetty, containerConfig.getPort());
        jetty.setConnectors( new Connector[] { serverConnector } );

        // request logs
        CustomRequestLog log = makeRequestLog(containerConfig.getRequestLog());

        jetty.setRequestLog(log);


        ServletContainer server = new ServletContainerImpl(jetty);
        return server;
    }

    protected WebAppContext makeWebAppContext(String resourceBase, Configuration[] configurations, PathResource containerResources, HttpServerConfig containerConfig) {

        WebAppContext webAppContext = new WebAppContextBuilder()
                .classLoader(Thread.currentThread().getContextClassLoader())
                .resourceBase(resourceBase)
                .configurations(configurations)
                .containerResource(containerResources)
                .initParameter(DIR_ALLOWED_KEY, FALSE)
                .parentLoaderPriority(true)
                .attribute(INCLUDE_JAR_PATTERN, JARS_TO_INCLUDE)
                .jspServlet(JSP_SERVLET)
                .gzipMimeTypes(containerConfig.getGzipMimeTypes())
                .errorPages(containerConfig.getErrorPages())
                .stateless()
                .staticAssetServlet(resourceBase + "/public/")
                .build();

        webAppContext.addFilter(containerConfig.getFilterClass(), "/*", EnumSet.of(DispatcherType.REQUEST));

        return webAppContext;
    }

    protected WebAppContext makeWebAppContextForWAR(Configuration[] configurations, Resource war, HttpServerConfig containerConfig) {
        logger.debug("war: " + war.getURI().toString());

        WebAppContext webAppContext = new WebAppContextBuilder()
                .classLoader(Thread.currentThread().getContextClassLoader())
                .configurations(configurations)
                .initParameter(DIR_ALLOWED_KEY, FALSE)
                .parentLoaderPriority(true)
                .attribute(INCLUDE_JAR_PATTERN, JARS_TO_INCLUDE)
                .jspServlet(JSP_SERVLET)
                .gzipMimeTypes(containerConfig.getGzipMimeTypes())
                .errorPages(containerConfig.getErrorPages())
                .stateless()
                .staticAssetServletWar("/public/")
                .build();

        webAppContext.addFilter(containerConfig.getFilterClass(), "/*", EnumSet.of(DispatcherType.REQUEST));
        webAppContext.setExtractWAR(true);
        webAppContext.setWarResource(war);

        return webAppContext;
    }

    protected String makeResourceBase(URI webApp) throws MalformedURLException {
        Resource resourceBase = Resource.newResource(webApp);
        return String.valueOf(resourceBase);
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
        httpConfig.setSendDateHeader(true);

        ConnectionFactory httpFactory = new HttpConnectionFactory( httpConfig );
        ConnectionFactory http2Factory = new HTTP2CServerConnectionFactory( httpConfig );

        ServerConnector serverConnector = new ServerConnector(server, httpFactory, http2Factory);
        serverConnector.setPort(port);

        return serverConnector;
    }

    protected CustomRequestLog makeRequestLog(String logFile) {
        CustomRequestLog requestLog = new CustomRequestLog(logFile);
        return requestLog;
    }
}
