package net.tokensmith.otter.gateway.builder;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.gateway.entity.ErrorTarget;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.gateway.entity.Target;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.Between;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TargetBuilder<S extends DefaultSession, U extends DefaultUser> {
    private List<Method> methods = new ArrayList<>();
    private String regex;
    private Resource<S, U> resource;
    private Map<Method, List<MimeType>> contentTypes = new HashMap<>();
    private Map<Method, List<MimeType>> accepts = new HashMap<>();

    // always default to optional session and optional authentication.
    private List<Label> labels = new ArrayList<>(Arrays.asList(Label.SESSION_OPTIONAL, Label.AUTH_OPTIONAL));

    private List<Between<S, U>> before = new ArrayList<>();
    private List<Between<S, U>> after = new ArrayList<>();
    // legacy error handling.
    private Map<StatusCode, ErrorTarget<S, U>> errorTargets = new HashMap<>();
    private Map<StatusCode, Resource<S, U>> errorResources = new HashMap<>();
    private String groupName;

    public TargetBuilder<S, U> method(Method method) {
        methods.add(method);
        return this;
    }

    /**
     * Exposes GET and POST methods and CSRF protects it.
     *
     * @return this, an instance of the TargetBuilder
     */
    public TargetBuilder<S, U> form() {
        this.method(Method.GET)
            .method(Method.POST);

        this.labels.add(Label.CSRF_PREPARE);
        this.labels.add(Label.CSRF_PROTECT);

        return this;
    }

    public TargetBuilder<S, U> regex(String regex) {
        this.regex = regex;
        return this;
    }

    public TargetBuilder<S, U> resource(Resource<S, U> resource) {
        this.resource = resource;
        return this;
    }

    public TargetBuilder<S, U> contentType(MimeType contentType) {
        for(Method method: Method.values()) {
            contentType(method, contentType);
        }
        return this;
    }

    public TargetBuilder<S, U> contentType(Method method, MimeType contentType) {
        List<MimeType> mimeTypes = this.contentTypes.get(method);
        if (Objects.isNull(mimeTypes)) {
            mimeTypes = new ArrayList<>();
        }
        mimeTypes.add(contentType);
        this.contentTypes.put(method, mimeTypes);
        return this;
    }

    public TargetBuilder<S, U> accept(MimeType contentType) {
        for(Method method: Method.values()) {
            accept(method, contentType);
        }
        return this;
    }

    public TargetBuilder<S, U> accept(Method method, MimeType contentType) {
        List<MimeType> mimeTypes = this.accepts.get(method);
        if (Objects.isNull(mimeTypes)) {
            mimeTypes = new ArrayList<>();
        }
        mimeTypes.add(contentType);
        this.accepts.put(method, mimeTypes);
        return this;
    }

    public TargetBuilder<S, U> authenticate() {

        this.labels.remove(Label.SESSION_OPTIONAL);
        this.labels.remove(Label.AUTH_OPTIONAL);

        this.labels.add(Label.SESSION_REQUIRED);
        this.labels.add(Label.AUTH_REQUIRED);
        return this;
    }

    public TargetBuilder<S, U> anonymous() {
        // remove all session and auth labels.
        this.labels.remove(Label.SESSION_OPTIONAL);
        this.labels.remove(Label.AUTH_OPTIONAL);
        this.labels.remove(Label.SESSION_REQUIRED);
        this.labels.remove(Label.AUTH_REQUIRED);
        return this;
    }

    public TargetBuilder<S, U> before(Between<S, U> before) {
        this.before.add(before);
        return this;
    }

    public TargetBuilder<S, U> after(Between<S, U> after) {
        this.after.add(after);
        return this;
    }

    public TargetBuilder<S, U> onDispatchError(StatusCode statusCode, ErrorTarget<S, U> errorTarget) {
        this.errorTargets.put(statusCode, errorTarget);
        return this;
    }

    public TargetBuilder<S, U> onError(StatusCode statusCode, Resource<S, U> errorResource) {
        this.errorResources.put(statusCode, errorResource);
        return this;
    }

    public TargetBuilder<S, U> groupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public Target<S, U> build() {
        return new Target<S, U>(methods, regex, resource, contentTypes, accepts, labels, before, after, errorTargets, errorResources, groupName);
    }
}
