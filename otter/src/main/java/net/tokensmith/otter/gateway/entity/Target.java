package net.tokensmith.otter.gateway.entity;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.Between;

import java.util.List;
import java.util.Map;


public class Target<S extends DefaultSession, U extends DefaultUser> {
    private List<Method> methods;
    private String regex;
    private Resource<S, U> resource;
    private Map<Method, List<MimeType>> contentTypes;
    private Map<Method, List<MimeType>> accepts;
    private List<Label> labels;
    private List<Between<S, U>> before;
    private List<Between<S, U>> after;
    private Map<StatusCode, ErrorTarget<S, U>> errorTargets;
    private Map<StatusCode, Resource<S, U>> errorResources;
    private String groupName;

    public Target(List<Method> methods, String regex, Resource<S, U> resource, Map<Method, List<MimeType>> contentTypes, Map<Method, List<MimeType>> accepts, List<Label> labels, List<Between<S, U>> before, List<Between<S, U>> after, Map<StatusCode, ErrorTarget<S, U>> errorTargets, Map<StatusCode, Resource<S, U>> errorResources, String groupName) {
        this.methods = methods;
        this.regex = regex;
        this.resource = resource;
        this.contentTypes = contentTypes;
        this.accepts = accepts;
        this.labels = labels;
        this.before = before;
        this.after = after;
        this.errorTargets = errorTargets;
        this.errorResources = errorResources;
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

    public Map<Method, List<MimeType>> getAccepts() {
        return accepts;
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

    public Map<StatusCode, Resource<S, U>> getErrorResources() {
        return errorResources;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
