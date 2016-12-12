package org.rootservices.otter.server.path;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 4/3/16.
 */
public class WebAppPath {

    /**
     * @param classURI location of a project's target/classes
     * @return a absolute file path to a project's target/classes
     * @throws URISyntaxException
     */
    public URI fromClassURI(URI classURI) throws URISyntaxException {
        String projectPath = classURI.getPath().split("/target")[0];
        String webAppPath = "file:" + projectPath + "/src/main/webapp";
        URI webAppURI = new URI(webAppPath);

        return webAppURI;
    }
}
