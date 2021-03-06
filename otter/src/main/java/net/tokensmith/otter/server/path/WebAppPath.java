package net.tokensmith.otter.server.path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by tommackenzie on 4/3/16.
 */
public class WebAppPath {
    protected static Logger LOGGER = LoggerFactory.getLogger(WebAppPath.class);
    private static String FILE = "file:";
    private static String DEFAULT_WEB_APP = "/src/main/webapp";
    private static String GRADLE_PATH = "/build";
    private static String INTELLIJ_PATH = "/out";
    private static String MVN_PATH = "/target";

    /**
     * @param classURI location of a project's classes
     * @return an absolute file path to a project's webapp directory
     * @throws URISyntaxException if an issue occurred constructing the URI
     */
    public URI fromClassURI(URI classURI) throws URISyntaxException {
        return fromClassURI(classURI, DEFAULT_WEB_APP);
    }

    /**
     * @param classURI location of a project's classes
     * @param customWebAppLocation the webapp location to append to the project's path, /src/main/webapp
     * @return an absolute file path to a project's webapp directory
     * @throws URISyntaxException if the webApp URI cannot be constructed.
     */
    public URI fromClassURI(URI classURI, String customWebAppLocation) throws URISyntaxException {
        String projectPath;

        if (classURI.getPath().contains(MVN_PATH)) {
            projectPath = makeProjectPath(classURI.getPath(), MVN_PATH);
        } else if (classURI.getPath().contains(GRADLE_PATH)) {
            projectPath = makeProjectPath(classURI.getPath(), GRADLE_PATH);
        } else {
            projectPath = makeProjectPath(classURI.getPath(), INTELLIJ_PATH);
        }

        String webAppPath = FILE + projectPath + customWebAppLocation;
        URI webAppURI = new URI(webAppPath);

        return webAppURI;
    }

    /**
     * Given a classURI Then return it's project path.
     *
     * @param classURI location of a project's classes
     * @param splitter /build or /target
     * @return an absolute file path to a project
     */
    protected String makeProjectPath(String classURI, String splitter) {
        StringBuilder projectPath = new StringBuilder();
        String[] parts = classURI.split(splitter);

        for(int i = 0; i < parts.length - 1; i++) {
            projectPath.append(parts[i]);
            if (i < parts.length - 2) {
                projectPath.append(splitter);
            }
        }

        return projectPath.toString();
    }
}
