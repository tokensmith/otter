package org.rootservices.otter.gateway;


import org.rootservices.otter.security.session.Session;

public interface Configure<T extends Session> {
    void configure(Gateway<T> gateway);
    void routes(Gateway<T> gateway);
}
