package org.rootservices.otter.router.entity;


import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.security.session.Session;

public interface Between<T extends Session> {
    void process(Method method, Request<T> request, Response<T> response) throws HaltException;
}
