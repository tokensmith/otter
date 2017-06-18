package org.rootservices.otter.router.entity;


import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;

public interface Between {
    Boolean process(Method method, Request request, Response response);
}
