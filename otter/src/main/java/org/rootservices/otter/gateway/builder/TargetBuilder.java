package org.rootservices.otter.gateway.builder;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.gateway.entity.Target;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.translatable.Translatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TargetBuilder<S extends DefaultSession, U extends DefaultUser, P extends Translatable> {
    private List<Method> methods = new ArrayList<>();
    private String regex;
    private Resource<S, U> resource;
    private Map<Method, List<MimeType>> contentTypes = new HashMap<>();
    private List<Label> labels = new ArrayList<>();
    private List<Between<S, U>> before = new ArrayList<>();
    private List<Between<S, U>> after = new ArrayList<>();
    private Map<StatusCode, ErrorTarget<S, U>> errorTargets = new HashMap<>();
    private String groupName;

    public TargetBuilder<S, U, P> method(Method method) {
        methods.add(method);
        return this;
    }

    public TargetBuilder<S, U, P> regex(String regex) {
        this.regex = regex;
        return this;
    }

    public TargetBuilder<S, U, P> resource(Resource<S, U> resource) {
        this.resource = resource;
        return this;
    }

    public TargetBuilder<S, U, P> contentType(MimeType contentType) {
        for(Method method: Method.values()) {
            contentType(method, contentType);
        }
        return this;
    }

    public TargetBuilder<S, U, P> contentType(Method method, MimeType contentType) {
        List<MimeType> mimeTypes = this.contentTypes.get(method);
        if (mimeTypes == null) {
            mimeTypes = new ArrayList<>();
        }
        mimeTypes.add(contentType);
        this.contentTypes.put(method, mimeTypes);
        return this;
    }

    public TargetBuilder<S, U, P> label(Label label) {
        this.labels.add(label);
        return this;
    }

    public TargetBuilder<S, U, P> before(Between<S, U> before) {
        this.before.add(before);
        return this;
    }

    public TargetBuilder<S, U, P> after(Between<S, U> after) {
        this.after.add(after);
        return this;
    }

    public TargetBuilder<S, U, P> errorTarget(StatusCode statusCode, ErrorTarget<S, U> errorTarget) {
        this.errorTargets.put(statusCode, errorTarget);
        return this;
    }

    public TargetBuilder<S, U, P> groupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public Target<S, U, P> build() {
        return new Target<S, U, P>(methods, regex, resource, contentTypes, labels, before, after, errorTargets, groupName);
    }
}
