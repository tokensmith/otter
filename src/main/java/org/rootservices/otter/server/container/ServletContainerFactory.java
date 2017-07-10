package org.rootservices.otter.server.container;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.rootservices.otter.server.path.CompiledClassPath;
import org.rootservices.otter.server.path.WebAppPath;
import org.rootservices.otter.servlet.EntryFilter;

import javax.servlet.DispatcherType;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tommackenzie on 4/3/16.
 *
 * A factory that creates a ServletContainer.
 */
public class ServletContainerFactory {

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
     * @param tempDirectory location to put temporary files.
     * @return a configured instance of ServletContainer
     * @throws URISyntaxException if an issue occurred constructing a URI
     * @throws IOException if issues come up regarding webapp or containerResources
     */
    public ServletContainer makeServletContainer(String documentRoot, Class clazz, int port, File tempDirectory) throws URISyntaxException, IOException {
        URI compliedClassPath = compiledClassPath.getForClass(clazz);
        URI webApp = webAppPath.fromClassURI(compliedClassPath);

        return makeServletContainer(documentRoot, webApp, compliedClassPath, port, tempDirectory);
    }

    public ServletContainer makeServletContainer(String documentRoot, Class clazz, String customWebAppLocation, int port, File tempDirectory) throws URISyntaxException, IOException {
        URI compliedClassPath = compiledClassPath.getForClass(clazz);
        URI webApp = webAppPath.fromClassURI(compliedClassPath, customWebAppLocation);

        return makeServletContainer(documentRoot, webApp, compliedClassPath, port, tempDirectory);
    }

    /**
     *
     * @param documentRoot root path for the servlet container to run. example, "/"
     * @param webApp absolute file path to the webapp directory in your project.
     * @param compliedClassPath absolute file path to, target/classes/ in your project.
     * @param port the port the container should use. 0 will randomly assign a port.
     * @param tempDirectory location to put temporary files.
     * @return a configured instance of ServletContainer
     * @throws IOException if issues come up regarding webapp or containerResources
     */
    public ServletContainer makeServletContainer(String documentRoot, URI webApp, URI compliedClassPath, int port, File tempDirectory) throws IOException {
        Server jetty = new Server(port);

        // dependencies for, WebAppContext
        PathResource containerResources = makeFileResource(compliedClassPath);
        String resourceBase = makeResourceBase(webApp);
        String webXmlPath = makeWebXmlPath(webApp);
        Configuration[] configurations = makeConfigurations();

        // dependency for, org.eclipse.jetty.server.Server
        WebAppContext context = makeWebAppContext(
                documentRoot, resourceBase, webXmlPath, configurations, tempDirectory, containerResources
        );

        ServerConnector serverConnector = makeServerConnector(jetty, port);
        jetty.setConnectors( new Connector[] { serverConnector } );

        // Add server context
        jetty.setHandler(context);
        ServletContainer server = new ServletContainerImpl(jetty);
        return server;
    }

    protected WebAppContext makeWebAppContext(String documentRoot, String resourceBase, String webXmlPath, Configuration[] configurations, File tempDirectory, PathResource containerResources)  {
        WebAppContext context = new WebAppContext();

        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setResourceBase(resourceBase);
        context.setConfigurations(configurations);
        context.setTempDirectory(tempDirectory);
        context.getMetaData().addContainerResource(containerResources);
        context.setDescriptor(webXmlPath);
        context.setInitParameter(DIR_ALLOWED_KEY, "false");
        context.setContextPath(documentRoot);
        context.setParentLoaderPriority(true);
        context.addFilter(EntryFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

        context.setAttribute(
            "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
            ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$"
        );
        return context;
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
}
