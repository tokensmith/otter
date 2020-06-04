package net.tokensmith.otter.gateway.builder;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.gateway.entity.ErrorTarget;
import net.tokensmith.otter.gateway.entity.Group;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.Halt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;


public class GroupBuilder<S extends DefaultSession, U extends DefaultUser> {
    private String name;
    private Class<S> sessionClazz;

    private Map<Label, List<Between<S, U>>> labelBefore = new HashMap<>();
    private Map<Label, List<Between<S, U>>> labelAfter = new HashMap<>();

    private List<Between<S, U>> befores = new ArrayList<>();
    private List<Between<S, U>> afters = new ArrayList<>();

    private Map<StatusCode, Resource<S, U>> errorResources = new HashMap<>();
    private Map<StatusCode, ErrorTarget<S, U>> dispatchErrors = new HashMap<>();

    // halts - custom halt handlers for security betweens
    private Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts = new HashMap<>();

    public GroupBuilder<S, U> name(String name) {
        this.name = name;
        return this;
    }

    public GroupBuilder<S, U> sessionClazz(Class<S> sessionClazz) {
        this.sessionClazz = sessionClazz;
        return this;
    }

    public GroupBuilder<S, U> before(Label label, Between<S, U> before) {
        if (Objects.isNull(this.labelBefore.get(label))) {
            this.labelBefore.put(label, new ArrayList<>());
        }
        this.labelBefore.get(label).add(before);
        return this;
    }

    public GroupBuilder<S, U> after(Label label, Between<S, U> after) {
        if (Objects.isNull(this.labelAfter.get(label))) {
            this.labelAfter.put(label, new ArrayList<>());
        }
        this.labelAfter.get(label).add(after);
        return this;
    }

    public GroupBuilder<S, U> before(Between<S, U> before) {
        this.befores.add(before);
        return this;
    }

    public GroupBuilder<S, U> after(Between<S, U> after) {
        this.afters.add(after);
        return this;
    }

    public GroupBuilder<S, U> onError(StatusCode statusCode, Resource<S, U> errorResource) {
        this.errorResources.put(statusCode, errorResource);
        return this;
    }

    public GroupBuilder<S, U> onDispatchError(StatusCode statusCode, ErrorTarget<S, U> dispatchError) {
        this.dispatchErrors.put(statusCode, dispatchError);
        return this;
    }

    public GroupBuilder<S, U> onHalt(Halt halt, BiFunction<Response<S>, HaltException, Response<S>> onHalt) {
        this.onHalts.put(halt, onHalt);
        return this;
    }

    public Group<S, U> build() {
        return new Group<S, U>(
                name,
                sessionClazz,
                labelBefore,
                labelAfter,
                befores,
                afters,
                errorResources,
                dispatchErrors,
                onHalts
        );
    }
}
