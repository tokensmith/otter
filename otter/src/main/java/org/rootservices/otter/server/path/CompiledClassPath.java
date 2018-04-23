package org.rootservices.otter.server.path;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.ProtectionDomain;


public class CompiledClassPath {

    public URI getForClass(Class clazz) throws URISyntaxException {
        ProtectionDomain protectionDomain = clazz.getProtectionDomain();
        URI location = protectionDomain.getCodeSource().getLocation().toURI();

        return location;
    }
}
