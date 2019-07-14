package org.rootservices.otter.gateway.builder;


import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.gateway.entity.rest.RestError;
import org.rootservices.otter.gateway.entity.rest.RestErrorTarget;
import org.rootservices.otter.gateway.entity.rest.RestTarget;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.between.RestBetween;
import org.rootservices.otter.translatable.Translatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RestTargetBuilder<U extends DefaultUser, P> {
    private List<Method> methods = new ArrayList<>();
    private String regex;
    private RestResource<U, P> restResource;
    private Class<P> payload;
    private Map<Method, List<MimeType>> contentTypes = new HashMap<>();
    private List<Label> labels = new ArrayList<>();
    private List<RestBetween<U>> before = new ArrayList<>();
    private List<RestBetween<U>> after = new ArrayList<>();
    private Map<StatusCode, RestErrorTarget<U, P>> errorTargets = new HashMap<>();
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors = new HashMap<>();
    private String groupName;

    public RestTargetBuilder<U, P> method(Method method) {
        this.methods.add(method);
        return this;
    }

    public RestTargetBuilder<U, P> regex(String regex) {
        this.regex = regex;
        return this;
    }

    public RestTargetBuilder<U, P> restResource(RestResource<U, P> restResource) {
        this.restResource = restResource;
        return this;
    }

    public RestTargetBuilder<U, P> payload(Class<P> payload) {
        this.payload = payload;
        return this;
    }

    public RestTargetBuilder<U, P> contentType(MimeType contentType) {
        for(Method method: Method.values()) {
            contentType(method, contentType);
        }
        return this;
    }

    public RestTargetBuilder<U, P> contentType(Method method, MimeType contentType) {
        List<MimeType> mimeTypes = this.contentTypes.get(method);
        if (mimeTypes == null) {
            mimeTypes = new ArrayList<>();
        }
        mimeTypes.add(contentType);
        this.contentTypes.put(method, mimeTypes);
        return this;
    }

    public RestTargetBuilder<U, P> label(Label label) {
        this.labels.add(label);
        return this;
    }

    public RestTargetBuilder<U, P> before(RestBetween<U> before) {
        this.before.add(before);
        return this;
    }

    public RestTargetBuilder<U, P> after(RestBetween<U> after) {
        this.after.add(after);
        return this;
    }

    // legacy error handling.
    public RestTargetBuilder<U, P> errorTarget(StatusCode statusCode, RestErrorTarget<U, P> errorTarget) {
        this.errorTargets.put(statusCode, errorTarget);
        return this;
    }

    public <E extends Translatable> RestTargetBuilder<U, P> onError(StatusCode statusCode, RestResource<U, E> restResource, Class<E> errorPayload) {
        RestError<U, E> restError = new RestError<>(errorPayload, restResource);
        restErrors.put(statusCode, restError);
        return this;
    }

    public RestTargetBuilder<U, P> groupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public RestTarget<U, P> build() {
        return new RestTarget<>(methods, regex, restResource, payload, contentTypes, labels, before, after, errorTargets, restErrors, groupName);
    }
}
