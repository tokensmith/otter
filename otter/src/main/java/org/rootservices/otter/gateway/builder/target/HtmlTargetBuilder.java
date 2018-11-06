package org.rootservices.otter.gateway.builder.target;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.EmptyPayload;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.gateway.entity.target.HtmlTarget;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;


import java.util.*;


// TODO: 104 needs tests.
public class HtmlTargetBuilder<S extends DefaultSession, U extends DefaultUser> {
    private List<Method> methods = new ArrayList<>();
    private String regex;
    private Resource<S, U, EmptyPayload> resource;
    private Map<Method, List<MimeType>> contentTypes = new HashMap<>();
    private List<Label> labels = new ArrayList<>();
    private List<Between<S, U, EmptyPayload>> before = new ArrayList<>();
    private List<Between<S, U, EmptyPayload>> after = new ArrayList<>();
    private Map<StatusCode, ErrorTarget<S, U, EmptyPayload>> errorTargets = new HashMap<>();
    private String groupName;

    public HtmlTargetBuilder<S, U> method(Method method) {
        methods.add(method);
        return this;
    }

    public HtmlTargetBuilder<S, U> regex(String regex) {
        this.regex = regex;
        return this;
    }

    public HtmlTargetBuilder<S, U> resource(Resource<S, U, EmptyPayload> resource) {
        this.resource = resource;
        return this;
    }

    public HtmlTargetBuilder<S, U> contentType(MimeType contentType) {
        for (Method method : Method.values()) {
            contentType(method, contentType);
        }
        return this;
    }

    public HtmlTargetBuilder<S, U> contentType(Method method, MimeType contentType) {
        List<MimeType> mimeTypes = this.contentTypes.get(method);
        if (mimeTypes == null) {
            mimeTypes = new ArrayList<>();
        }
        mimeTypes.add(contentType);
        this.contentTypes.put(method, mimeTypes);
        return this;
    }

    public HtmlTargetBuilder<S, U> label(Label label) {
        this.labels.add(label);
        return this;
    }

    public HtmlTargetBuilder<S, U> before(Between<S, U, EmptyPayload> before) {
        this.before.add(before);
        return this;
    }

    public HtmlTargetBuilder<S, U> after(Between<S, U, EmptyPayload> after) {
        this.after.add(after);
        return this;
    }

    public HtmlTargetBuilder<S, U> errorTarget(StatusCode statusCode, ErrorTarget<S, U, EmptyPayload> errorTarget) {
        this.errorTargets.put(statusCode, errorTarget);
        return this;
    }

    public HtmlTargetBuilder<S, U> groupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public HtmlTarget<S, U> build() {
        return new HtmlTarget<S, U>(methods, regex, resource, contentTypes, Optional.empty(), labels, before, after, errorTargets, groupName);
    }
}
