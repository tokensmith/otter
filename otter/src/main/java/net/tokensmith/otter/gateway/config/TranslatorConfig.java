package net.tokensmith.otter.gateway.config;

import net.tokensmith.otter.controller.Resource;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.gateway.entity.ErrorTarget;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.Halt;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class TranslatorConfig<S extends DefaultSession, U extends DefaultUser> {
    private Class<S> sessionClazz;
    private Map<Label, List<Between<S,U>>> before;
    private Map<Label, List<Between<S,U>>> after;
    private Map<StatusCode, Resource<S, U>> errorResources;
    private Map<StatusCode, ErrorTarget<S, U>> dispatchErrors;
    private Map<StatusCode, ErrorTarget<S, U>> defaultDispatchErrors;

    // halts - custom halt handlers for security betweens
    private Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts;

    public TranslatorConfig(Class<S> sessionClazz, Map<Label, List<Between<S, U>>> before, Map<Label, List<Between<S, U>>> after, Map<StatusCode, Resource<S, U>> errorResources, Map<StatusCode, ErrorTarget<S, U>> dispatchErrors, Map<StatusCode, ErrorTarget<S, U>> defaultDispatchErrors, Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts) {
        this.sessionClazz = sessionClazz;
        this.before = before;
        this.after = after;
        this.errorResources = errorResources;
        this.dispatchErrors = dispatchErrors;
        this.defaultDispatchErrors = defaultDispatchErrors;
        this.onHalts = onHalts;
    }

    public Class<S> getSessionClazz() {
        return sessionClazz;
    }

    public void setSessionClazz(Class<S> sessionClazz) {
        this.sessionClazz = sessionClazz;
    }

    public Map<Label, List<Between<S, U>>> getBefore() {
        return before;
    }

    public void setBefore(Map<Label, List<Between<S, U>>> before) {
        this.before = before;
    }

    public Map<Label, List<Between<S, U>>> getAfter() {
        return after;
    }

    public void setAfter(Map<Label, List<Between<S, U>>> after) {
        this.after = after;
    }

    public Map<StatusCode, Resource<S, U>> getErrorResources() {
        return errorResources;
    }

    public void setErrorResources(Map<StatusCode, Resource<S, U>> errorResources) {
        this.errorResources = errorResources;
    }

    public Map<StatusCode, ErrorTarget<S, U>> getDispatchErrors() {
        return dispatchErrors;
    }

    public void setDispatchErrors(Map<StatusCode, ErrorTarget<S, U>> dispatchErrors) {
        this.dispatchErrors = dispatchErrors;
    }

    public Map<StatusCode, ErrorTarget<S, U>> getDefaultDispatchErrors() {
        return defaultDispatchErrors;
    }

    public void setDefaultDispatchErrors(Map<StatusCode, ErrorTarget<S, U>> defaultDispatchErrors) {
        this.defaultDispatchErrors = defaultDispatchErrors;
    }

    public Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> getOnHalts() {
        return onHalts;
    }

    public void setOnHalts(Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts) {
        this.onHalts = onHalts;
    }

    public static class Builder<S extends DefaultSession, U extends DefaultUser> {
        private Class<S> sessionClazz;
        private Map<Label, List<Between<S,U>>> before;
        private Map<Label, List<Between<S,U>>> after;
        private Map<StatusCode, Resource<S, U>> errorResources;
        private Map<StatusCode, ErrorTarget<S, U>> dispatchErrors;
        private Map<StatusCode, ErrorTarget<S, U>> defaultDispatchErrors;
        private Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts;

        public Builder<S, U> sessionClazz(Class<S> sessionClazz) {
            this.sessionClazz = sessionClazz;
            return this;
        }

        public Builder<S, U> before(Map<Label, List<Between<S, U>>> before) {
            this.before = before;
            return this;
        }

        public Builder<S, U> after(Map<Label, List<Between<S, U>>> after) {
            this.after = after;
            return this;
        }

        public Builder<S, U> errorResources(Map<StatusCode, Resource<S, U>> errorResources) {
            this.errorResources = errorResources;
            return this;
        }

        public Builder<S, U> dispatchErrors(Map<StatusCode, ErrorTarget<S, U>> dispatchErrors) {
            this.dispatchErrors = dispatchErrors;
            return this;
        }

        public Builder<S, U> defaultDispatchErrors(Map<StatusCode, ErrorTarget<S, U>> defaultDispatchErrors) {
            this.defaultDispatchErrors = defaultDispatchErrors;
            return this;
        }

        public Builder<S, U> onHalts(Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts) {
            this.onHalts = onHalts;
            return this;
        }

        public TranslatorConfig<S, U> build() {
            return new TranslatorConfig<S, U>(sessionClazz, before, after, errorResources, dispatchErrors, defaultDispatchErrors, onHalts);
        }
    }
}
