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
    private Map<Label, List<Between<S,U>>> labelBefore;
    private Map<Label, List<Between<S,U>>> labelAfter;
    private List<Between<S,U>> befores;
    private List<Between<S,U>> afters;
    private Map<StatusCode, Resource<S, U>> errorResources;
    private Map<StatusCode, ErrorTarget<S, U>> dispatchErrors;
    private Map<StatusCode, ErrorTarget<S, U>> defaultDispatchErrors;

    // halts - custom halt handlers for security betweens
    private Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts;

    public TranslatorConfig(Class<S> sessionClazz, Map<Label, List<Between<S, U>>> labelBefore, Map<Label, List<Between<S, U>>> labelAfter, List<Between<S,U>> befores, List<Between<S,U>> afters, Map<StatusCode, Resource<S, U>> errorResources, Map<StatusCode, ErrorTarget<S, U>> dispatchErrors, Map<StatusCode, ErrorTarget<S, U>> defaultDispatchErrors, Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts) {
        this.sessionClazz = sessionClazz;
        this.labelBefore = labelBefore;
        this.labelAfter = labelAfter;
        this.befores = befores;
        this.afters = afters;
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

    public Map<Label, List<Between<S, U>>> getLabelBefore() {
        return labelBefore;
    }

    public void setLabelBefore(Map<Label, List<Between<S, U>>> labelBefore) {
        this.labelBefore = labelBefore;
    }

    public Map<Label, List<Between<S, U>>> getLabelAfter() {
        return labelAfter;
    }

    public void setLabelAfter(Map<Label, List<Between<S, U>>> labelAfter) {
        this.labelAfter = labelAfter;
    }

    public List<Between<S, U>> getBefores() {
        return befores;
    }

    public void setBefores(List<Between<S, U>> befores) {
        this.befores = befores;
    }

    public List<Between<S, U>> getAfters() {
        return afters;
    }

    public void setAfters(List<Between<S, U>> afters) {
        this.afters = afters;
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
        private Map<Label, List<Between<S,U>>> labelBefore;
        private Map<Label, List<Between<S,U>>> labelAfter;
        private List<Between<S,U>> befores;
        private List<Between<S,U>> afters;
        private Map<StatusCode, Resource<S, U>> errorResources;
        private Map<StatusCode, ErrorTarget<S, U>> dispatchErrors;
        private Map<StatusCode, ErrorTarget<S, U>> defaultDispatchErrors;
        private Map<Halt, BiFunction<Response<S>, HaltException, Response<S>>> onHalts;

        public Builder<S, U> sessionClazz(Class<S> sessionClazz) {
            this.sessionClazz = sessionClazz;
            return this;
        }

        public Builder<S, U> labelBefore(Map<Label, List<Between<S, U>>> labelBefore) {
            this.labelBefore = labelBefore;
            return this;
        }

        public Builder<S, U> labelAfter(Map<Label, List<Between<S, U>>> labelAfter) {
            this.labelAfter = labelAfter;
            return this;
        }

        public Builder<S, U> befores(List<Between<S, U>> befores) {
            this.befores = befores;
            return this;
        }

        public Builder<S, U> afters(List<Between<S, U>> afters) {
            this.afters = afters;
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
            return new TranslatorConfig<S, U>(
                    sessionClazz,
                    labelBefore,
                    labelAfter,
                    befores,
                    afters,
                    errorResources,
                    dispatchErrors,
                    defaultDispatchErrors,
                    onHalts
            );
        }
    }
}
