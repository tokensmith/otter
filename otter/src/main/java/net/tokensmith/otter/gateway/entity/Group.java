package net.tokensmith.otter.gateway.entity;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.Halt;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;


public class Group<S extends DefaultSession, U extends DefaultUser> {
    private String name;
    private Class<S> sessionClazz;

    private Map<Label, List<Between<S, U>>> before;
    private Map<Label, List<Between<S, U>>> after;
    private Map<StatusCode, Resource<S, U>> errorResources;
    private Map<StatusCode, ErrorTarget<S, U>> dispatchErrors;

    // halts - custom halt handlers for security betweens
    private Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts;

    public Group(String name, Class<S> sessionClazz, Map<Label, List<Between<S, U>>> before, Map<Label, List<Between<S, U>>> after, Map<StatusCode, Resource<S, U>> errorResources, Map<StatusCode, ErrorTarget<S, U>> dispatchErrors, Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts) {
        this.name = name;
        this.sessionClazz = sessionClazz;
        this.before = before;
        this.after = after;
        this.errorResources = errorResources;
        this.dispatchErrors = dispatchErrors;
        this.onHalts = onHalts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<S> getSessionClazz() {
        return sessionClazz;
    }

    public void setSessionClazz(Class<S> sessionClazz) {
        this.sessionClazz = sessionClazz;
    }

    public Map<Label, List<Between<S, U>>> getBefore() {
        return before;
    }

    public void setBefore(Map<Label, List<Between<S, U>>> before) {
        this.before = before;
    }

    public Map<Label, List<Between<S, U>>> getAfter() {
        return after;
    }

    public void setAfter(Map<Label, List<Between<S, U>>> after) {
        this.after = after;
    }

    public Map<StatusCode, Resource<S, U>> getErrorResources() {
        return errorResources;
    }

    public void setErrorResources(Map<StatusCode, Resource<S, U>> errorResources) {
        this.errorResources = errorResources;
    }

    public Map<StatusCode, ErrorTarget<S, U>> getDispatchErrors() {
        return dispatchErrors;
    }

    public void setDispatchErrors(Map<StatusCode, ErrorTarget<S, U>> dispatchErrors) {
        this.dispatchErrors = dispatchErrors;
    }

    public Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> getOnHalts() {
        return onHalts;
    }

    public void setOnHalts(Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts) {
        this.onHalts = onHalts;
    }
}
