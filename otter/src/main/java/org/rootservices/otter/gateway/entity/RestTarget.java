package org.rootservices.otter.gateway.entity;


import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translatable.Translatable;

import java.util.List;
import java.util.Map;

public class RestTarget<U extends DefaultUser, P extends Translatable> {
    private List<Method> methods;
    private String regex;
    private RestResource<U, P> restResource;
    private Class<P> payload;
    private Map<Method, List<MimeType>> contentTypes;
    private List<Label> labels;
    private List<RestBetween<U, P>> before;
    private List<RestBetween<U, P>> after;
    private Map<StatusCode, RestErrorTarget<U, P>> errorTargets;
    private String groupName;

    public RestTarget(List<Method> methods, String regex, RestResource<U, P> restResource, Class<P> payload, Map<Method, List<MimeType>> contentTypes, List<Label> labels, List<RestBetween<U, P>> before, List<RestBetween<U, P>> after, Map<StatusCode, RestErrorTarget<U, P>> errorTargets, String groupName) {
        this.methods = methods;
        this.regex = regex;
        this.restResource = restResource;
        this.payload = payload;
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

    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public RestResource<U, P> getRestResource() {
        return restResource;
    }

    public void setRestResource(RestResource<U, P> restResource) {
        this.restResource = restResource;
    }

    public Class<P> getPayload() {
        return payload;
    }

    public void setPayload(Class<P> payload) {
        this.payload = payload;
    }

    public Map<Method, List<MimeType>> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(Map<Method, List<MimeType>> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public List<RestBetween<U, P>> getBefore() {
        return before;
    }

    public void setBefore(List<RestBetween<U, P>> before) {
        this.before = before;
    }

    public List<RestBetween<U, P>> getAfter() {
        return after;
    }

    public void setAfter(List<RestBetween<U, P>> after) {
        this.after = after;
    }

    public Map<StatusCode, RestErrorTarget<U, P>> getErrorTargets() {
        return errorTargets;
    }

    public void setErrorTargets(Map<StatusCode, RestErrorTarget<U, P>> errorTargets) {
        this.errorTargets = errorTargets;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
