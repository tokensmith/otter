package org.rootservices.otter.server.container;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.rootservices.otter.server.path.CompiledClassPath;
import org.rootservices.otter.server.path.WebAppPath;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 4/3/16.
 *
 * A factory that creates a ServletContainer.
 */
public class ServletContainerFactory {

    private static String WEB_XML = "/WEB-INF/web.xml";
    private CompiledClassPath compiledClassPath;
    private WebAppPath webAppPath;

    public ServletContainerFactory(CompiledClassPath compiledClassPath, WebAppPath webAppPath) {
        this.compiledClassPath = compiledClassPath;
        this.webAppPath = webAppPath;
    }

    /**
     *
     * @param path root path for the servlet container to run. example, "/"
     * @param clazz a class in your project.
     * @param port the port the container should use. 0 will randomly assign a port.
     * @return
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public ServletContainer makeServletContainer(String path, Class clazz, int port) throws URISyntaxException, MalformedURLException {
        URI compiledClasses = compiledClassPath.getForClass(clazz);
        URI webApp = webAppPath.fromClassURI(compiledClasses);

        return makeServletContainer(path, webApp, compiledClasses, port);
    }

    /**
     *
     * @param path root path for the servlet container to run. example, "/"
     * @param webApp absolute file path to the webapp directory in your project.
     * @param classPath absolute file path to, target/classes/ in your project.
     * @param port the port the container should use. 0 will randomly assign a port.
     * @return
     * @throws MalformedURLException
     */
    public ServletContainer makeServletContainer(String path, URI webApp, URI classPath, int port) throws MalformedURLException {
        Server jetty = new Server(0);

        // dependencies for, WebAppContext
        FileResource containerResources = makeFileResource(classPath);
        String resourceBase = makeResourceBase(webApp);
        String webXmlPath = makeWebXmlPath(webApp);
        Configuration[] configurations = makeConfigurations();
        File tempDirectory = makeTempDirectory();

        // dependency for, org.eclipse.jetty.server.Server
        WebAppContext context = makeWebAppContext(
                path, resourceBase, webXmlPath, configurations, tempDirectory, containerResources
        );

        ServerConnector serverConnector = makeServerConnector(jetty, port);
        jetty.setConnectors( new Connector[] { serverConnector } );

        // Add server context
        jetty.setHandler(context);
        ServletContainer server = new ServletContainerImpl(jetty);
        return server;
    }

    protected WebAppContext makeWebAppContext(String path, String resourceBase, String webXmlPath, Configuration[] configurations, File tempDirectory, FileResource containerResources) throws MalformedURLException {
        WebAppContext context = new WebAppContext();

        context.setClassLoader(Thread.currentThread().getContextClassLoader());
        context.setResourceBase(resourceBase);
        context.setConfigurations(configurations);
        context.setTempDirectory(tempDirectory);
        context.getMetaData().addContainerResource(containerResources);
        context.setDescriptor(webXmlPath);
        context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        context.setContextPath(path);
        context.setParentLoaderPriority(true);

        return context;
    }

    protected String makeResourceBase(URI webApp) throws MalformedURLException {
        Resource resourceBase = Resource.newResource(webApp);
        return String.valueOf(resourceBase);
    }

    protected String makeWebXmlPath(URI webApp) {
        return webApp.getPath() + WEB_XML;
    }

    protected FileResource makeFileResource(URI classPath) {
        return new FileResource(classPath);
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

    protected File makeTempDirectory() {
        return new File("/tmp");
    }
}
