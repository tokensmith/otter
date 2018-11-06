package org.rootservices.otter.gateway.builder.target;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.gateway.entity.target.Target;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.translatable.Translatable;

import java.util.*;

public class TargetBuilder<S extends DefaultSession, U extends DefaultUser, P extends Translatable> {
    private List<Method> methods = new ArrayList<>();
    private String regex;
    private Resource<S, U, P> resource;
    private Map<Method, List<MimeType>> contentTypes = new HashMap<>();
    private Optional<Class<P>> payload = Optional.empty();
    private List<Label> labels = new ArrayList<>();
    private List<Between<S, U, P>> before = new ArrayList<>();
    private List<Between<S, U, P>> after = new ArrayList<>();
    private Map<StatusCode, ErrorTarget<S, U, P>> errorTargets = new HashMap<>();
    private String groupName;

    public TargetBuilder<S, U, P> method(Method method) {
        methods.add(method);
        return this;
    }

    public TargetBuilder<S, U, P> regex(String regex) {
        this.regex = regex;
        return this;
    }

    public TargetBuilder<S, U, P> resource(Resource<S, U, P> resource) {
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

    public TargetBuilder<S, U, P> payload(Class<P> payload) {
        this.payload = Optional.of(payload);
        return this;
    }

    public TargetBuilder<S, U, P> label(Label label) {
        this.labels.add(label);
        return this;
    }

    public TargetBuilder<S, U, P> before(Between<S, U, P> before) {
        this.before.add(before);
        return this;
    }

    public TargetBuilder<S, U, P> after(Between<S, U, P> after) {
        this.after.add(after);
        return this;
    }

    public TargetBuilder<S, U, P> errorTarget(StatusCode statusCode, ErrorTarget<S, U, P> errorTarget) {
        this.errorTargets.put(statusCode, errorTarget);
        return this;
    }

    public TargetBuilder<S, U, P> groupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public Target<S, U, P> build() {
        return new Target<S, U, P>(methods, regex, resource, contentTypes, payload, labels, before, after, errorTargets, groupName);
    }
}
