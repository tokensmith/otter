package org.rootservices.otter.server.path;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 4/3/16.
 */
public class WebAppPath {
    private static String FILE = "file:";
    private static String DEFAULT_WEB_APP = "/src/main/webapp";

    /**
     * @param classURI location of a project's target/classes
     * @return a absolute file path to a project's target/classes
     * @throws URISyntaxException
     */
    public URI fromClassURI(URI classURI) throws URISyntaxException {
        return fromClassURI(classURI, DEFAULT_WEB_APP);
    }

    public URI fromClassURI(URI classURI, String customWebAppLocation) throws URISyntaxException {
        String projectPath = classURI.getPath().split("/target")[0];
        String webAppPath = FILE + projectPath + customWebAppLocation;
        URI webAppURI = new URI(webAppPath);

        return webAppURI;
    }
}
