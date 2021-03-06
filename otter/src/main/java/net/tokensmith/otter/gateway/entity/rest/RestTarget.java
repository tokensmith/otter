package net.tokensmith.otter.gateway.entity.rest;


import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.dispatch.json.validator.Validate;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.translatable.Translatable;

import java.util.List;
import java.util.Map;

public class RestTarget<S extends DefaultSession, U extends DefaultUser, P> {
    private List<Method> methods;
    private String regex;
    private RestResource<U, P> restResource;
    private Class<P> payload;
    private Map<Method, List<MimeType>> contentTypes;
    private Map<Method, List<MimeType>> accepts;
    private List<Label> labels;
    private List<RestBetween<S, U>> before;
    private List<RestBetween<S, U>> after;
    private Validate validate;
    private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> errorTargets; // dispatch errors
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors;
    private String groupName;

    public RestTarget(List<Method> methods, String regex, RestResource<U, P> restResource, Class<P> payload, Map<Method, List<MimeType>> contentTypes, Map<Method, List<MimeType>> accepts, List<Label> labels, List<RestBetween<S, U>> before, List<RestBetween<S, U>> after, Validate validate, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> errorTargets, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors, String groupName) {
        this.methods = methods;
        this.regex = regex;
        this.restResource = restResource;
        this.payload = payload;
        this.contentTypes = contentTypes;
        this.accepts = accepts;
        this.labels = labels;
        this.before = before;
        this.after = after;
        this.validate = validate;
        this.errorTargets = errorTargets; // dispatch errors
        this.restErrors = restErrors;
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

    public Map<Method, List<MimeType>> getAccepts() {
        return accepts;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public List<RestBetween<S, U>> getBefore() {
        return before;
    }

    public void setBefore(List<RestBetween<S, U>> before) {
        this.before = before;
    }

    public List<RestBetween<S, U>> getAfter() {
        return after;
    }

    public void setAfter(List<RestBetween<S, U>> after) {
        this.after = after;
    }

    public Validate getValidate() {
        return validate;
    }

    public void setValidate(Validate validate) {
        this.validate = validate;
    }

    // dispatch errors.
    public Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> getErrorTargets() {
        return errorTargets;
    }

    public void setErrorTargets(Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> errorTargets) {
        this.errorTargets = errorTargets;
    }

    public Map<StatusCode, RestError<U, ? extends Translatable>> getRestErrors() {
        return restErrors;
    }

    public void setRestErrors(Map<StatusCode, RestError<U, ? extends Translatable>> restErrors) {
        this.restErrors = restErrors;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
