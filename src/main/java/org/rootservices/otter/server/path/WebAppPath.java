package org.rootservices.otter.server.path;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 4/3/16.
 */
public class WebAppPath {

    /**
     * Given a URI
     * When its a project's target/classes location
     * Then return the URI to the project's webapp location.
     *
     * @param classURI
     * @return
     * @throws URISyntaxException
     */
    public URI fromClassURI(URI classURI) throws URISyntaxException {
        String projectPath = classURI.getPath().split("/target")[0];
        String webAppPath = "file:" + projectPath + "/src/main/webapp";
        URI webAppURI = new URI(webAppPath);

        return webAppURI;
    }
}
