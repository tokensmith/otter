package org.rootservices.otter.server.path;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 4/3/16.
 */
public class WebAppPath {
    private static String FILE = "file:";
    private static String DEFAULT_WEB_APP = "/src/main/webapp";
    private static String GRADLE_PATH = "/build";
    private static String MVN_PATH = "/target";

    /**
     * @param classURI location of a project's classes
     * @return a absolute file path to a project's classes
     * @throws URISyntaxException if an issue occurred constructing the URI
     */
    public URI fromClassURI(URI classURI) throws URISyntaxException {
        return fromClassURI(classURI, DEFAULT_WEB_APP);
    }

    public URI fromClassURI(URI classURI, String customWebAppLocation) throws URISyntaxException {
        String projectPath;

        if (classURI.getPath().contains(MVN_PATH)) {
            projectPath = classURI.getPath().split(MVN_PATH)[0];
        } else {
            projectPath = classURI.getPath().split(GRADLE_PATH)[0];
        }

        String webAppPath = FILE + projectPath + customWebAppLocation;
        URI webAppURI = new URI(webAppPath);

        return webAppURI;
    }
}
