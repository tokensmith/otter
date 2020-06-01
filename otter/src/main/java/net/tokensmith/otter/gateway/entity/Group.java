package net.tokensmith.otter.gateway.entity;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.router.entity.between.Between;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class Group<S extends DefaultSession, U extends DefaultUser> {
    private String name;
    private Class<S> sessionClazz;

    private Map<Label, List<Between<S, U>>> before;
    private Map<Label, List<Between<S, U>>> after;
    private Map<StatusCode, Resource<S, U>> errorResources;
    private Map<StatusCode, ErrorTarget<S, U>> dispatchErrors;

    public Group(String name, Class<S> sessionClazz, Map<Label, List<Between<S, U>>> before, Map<Label, List<Between<S, U>>> after, Map<StatusCode, Resource<S, U>> errorResources, Map<StatusCode, ErrorTarget<S, U>> dispatchErrors) {
        this.name = name;
        this.sessionClazz = sessionClazz;
        this.before = before;
        this.after = after;
        this.errorResources = errorResources;
        this.dispatchErrors = dispatchErrors;
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
}
