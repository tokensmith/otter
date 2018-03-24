package org.rootservices.otter.router.entity;


import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.exception.HaltException;

public interface Between {
    void process(Method method, Request request, Response response) throws HaltException;
}
