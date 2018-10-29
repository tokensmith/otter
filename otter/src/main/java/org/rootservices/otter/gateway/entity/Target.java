package org.rootservices.otter.gateway.entity;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.translatable.Translatable;

import java.util.List;
import java.util.Map;


public class Target<S extends DefaultSession, U extends DefaultUser, P extends Translatable> {
    private List<Method> methods;
    private String regex;
    private Resource<S, U> resource;
    private Map<Method, List<MimeType>> contentTypes;
    private List<Label> labels;
    private List<Between<S, U>> before;
    private List<Between<S, U>> after;
    private Map<StatusCode, ErrorTarget<S, U>> errorTargets;
    private String groupName;

    public Target(List<Method> methods, String regex, Resource<S, U> resource, Map<Method, List<MimeType>> contentTypes, List<Label> labels, List<Between<S, U>> before, List<Between<S, U>> after, Map<StatusCode, ErrorTarget<S, U>> errorTargets, String groupName) {
        this.methods = methods;
        this.regex = regex;
        this.resource = resource;
        this.contentTypes = contentTypes;
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

    public Resource<S, U> getResource() {
        return resource;
    }

    public Map<Method, List<MimeType>> getContentTypes() {
        return contentTypes;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public List<Between<S, U>> getBefore() {
        return before;
    }

    public List<Between<S, U>> getAfter() {
        return after;
    }

    public Map<StatusCode, ErrorTarget<S, U>> getErrorTargets() {
        return errorTargets;
    }

    public void setErrorTargets(Map<StatusCode, ErrorTarget<S, U>> errorTargets) {
        this.errorTargets = errorTargets;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
