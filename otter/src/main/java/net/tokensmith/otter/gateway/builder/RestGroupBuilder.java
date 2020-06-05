package net.tokensmith.otter.gateway.builder;

import net.tokensmith.otter.controller.RestResource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.gateway.entity.rest.RestError;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.gateway.entity.rest.RestGroup;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.Halt;
import net.tokensmith.otter.translatable.Translatable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;


public class RestGroupBuilder<S extends DefaultSession, U extends DefaultUser> {
    private String name;
    private Class<S> sessionClazz;

    private Map<Label, List<RestBetween<S, U>>> labelBefore = new HashMap<>();
    private Map<Label, List<RestBetween<S, U>>> labelAfter = new HashMap<>();

    private List<RestBetween<S, U>> befores = new ArrayList<>();
    private List<RestBetween<S, U>> afters = new ArrayList<>();

    // for route run to handle errors.
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors = new HashMap<>();
    // for engine to handle errors
    private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors = new HashMap<>();
    // halts - custom halt handlers for security betweens
    private Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts = new HashMap<>();

    public RestGroupBuilder<S, U> name(String name) {
        this.name = name;
        return this;
    }

    public RestGroupBuilder<S, U> sessionClazz(Class<S> sessionClazz) {
        this.sessionClazz = sessionClazz;
        return this;
    }

    public RestGroupBuilder<S, U> before(Label label, RestBetween<S, U> before) {
        if (Objects.isNull(this.labelBefore.get(label))) {
            this.labelBefore.put(label, new ArrayList<>());
        }
        this.labelBefore.get(label).add(before);
        return this;
    }

    public RestGroupBuilder<S, U> after(Label label, RestBetween<S, U> after) {
        if (Objects.isNull(this.labelAfter.get(label))) {
            this.labelAfter.put(label, new ArrayList<>());
        }
        this.labelAfter.get(label).add(after);
        return this;
    }

    public RestGroupBuilder<S, U> before(RestBetween<S, U> before) {
        this.befores.add(before);
        return this;
    }

    public RestGroupBuilder<S, U> after(RestBetween<S, U> after) {
        this.afters.add(after);
        return this;
    }

    public <P extends Translatable> RestGroupBuilder<S, U> onError(StatusCode statusCode, RestResource<U, P> restResource, Class<P> errorPayload) {
        RestError<U, P> restError = new RestError<>(errorPayload, restResource);
        restErrors.put(statusCode, restError);
        return this;
    }

    public <P extends Translatable> RestGroupBuilder<S, U> onDispatchError(StatusCode statusCode, RestErrorTarget<S, U, P> dispatchError) {
        this.dispatchErrors.put(statusCode, dispatchError);
        return this;
    }

    public <P extends Translatable> RestGroupBuilder<S, U> onHalt(Halt halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse> onHalt) {
        this.onHalts.put(halt, onHalt);
        return this;
    }

    public RestGroup<S, U> build() {
        return new RestGroup<>(
            name,
            sessionClazz,
            labelBefore,
            labelAfter,
            befores,
            afters,
            restErrors,
            dispatchErrors,
            onHalts
        );
    }
}
