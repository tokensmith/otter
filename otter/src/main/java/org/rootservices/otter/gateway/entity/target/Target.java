package org.rootservices.otter.gateway.entity.target;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.translatable.Translatable;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class Target<S extends DefaultSession, U extends DefaultUser, P extends Translatable> {
    private List<Method> methods;
    private String regex;
    private Resource<S, U, P> resource;
    private Map<Method, List<MimeType>> contentTypes;
    private Optional<Class<P>> payload;
    private List<Label> labels;
    private List<Between<S, U, P>> before;
    private List<Between<S, U, P>> after;
    private Map<StatusCode, ErrorTarget<S, U, P>> errorTargets;
    private String groupName;

    public Target(List<Method> methods, String regex, Resource<S, U, P> resource, Map<Method, List<MimeType>> contentTypes, Optional<Class<P>> payload, List<Label> labels, List<Between<S, U, P>> before, List<Between<S, U, P>> after, Map<StatusCode, ErrorTarget<S, U, P>> errorTargets, String groupName) {
        this.methods = methods;
        this.regex = regex;
        this.resource = resource;
        this.contentTypes = contentTypes;
        this.payload = payload;
        this.labels = labels;
        this.before = before;
        this.after = after;
        this.errorTargets = errorTargets;
        this.groupName = groupName;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public String getRegex() {
        return regex;
    }

    public Resource<S, U, P> getResource() {
        return resource;
    }

    public Map<Method, List<MimeType>> getContentTypes() {
        return contentTypes;
    }

    public Optional<Class<P>> getPayload() {
        return payload;
    }

    public void setPayload(Optional<Class<P>> payload) {
        this.payload = payload;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public List<Between<S, U, P>> getBefore() {
        return before;
    }

    public List<Between<S, U, P>> getAfter() {
        return after;
    }

    public Map<StatusCode, ErrorTarget<S, U, P>> getErrorTargets() {
        return errorTargets;
    }

    public void setErrorTargets(Map<StatusCode, ErrorTarget<S, U, P>> errorTargets) {
        this.errorTargets = errorTargets;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
