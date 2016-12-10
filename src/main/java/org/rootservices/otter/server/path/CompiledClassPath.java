package org.rootservices.otter.server.path;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.ProtectionDomain;

/**
 * Created by tommackenzie on 4/3/16.
 */
public class CompiledClassPath {

    public URI getForClass(Class clazz) throws URISyntaxException {
        ProtectionDomain protectionDomain = clazz.getProtectionDomain();
        URI location = protectionDomain.getCodeSource().getLocation().toURI();

        return location;
    }
}
