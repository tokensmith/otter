package net.tokensmith.otter.gateway.config;

import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.dispatch.json.validator.Validate;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.gateway.entity.rest.RestError;
import net.tokensmith.otter.gateway.entity.rest.RestErrorTarget;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.Halt;
import net.tokensmith.otter.translatable.Translatable;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;


public class RestTranslatorConfig<S extends DefaultSession, U extends DefaultUser> {
    private Class<S> sessionClazz;
    private Map<Label, List<RestBetween<S, U>>> before;
    private Map<Label, List<RestBetween<S, U>>> after;
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors;
    private Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors;
    private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors;
    private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors;
    private Validate validate;
    // halts - custom halt handlers for security betweens
    private Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts;

    public RestTranslatorConfig(Class<S> sessionClazz, Map<Label, List<RestBetween<S, U>>> before, Map<Label, List<RestBetween<S, U>>> after, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors, Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors, Validate validate, Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts) {
        this.sessionClazz = sessionClazz;
        this.before = before;
        this.after = after;
        this.restErrors = restErrors;
        this.defaultErrors = defaultErrors;
        this.dispatchErrors = dispatchErrors;
        this.defaultDispatchErrors = defaultDispatchErrors;
        this.validate = validate;
        this.onHalts = onHalts;
    }

    public Class<S> getSessionClazz() {
        return sessionClazz;
    }

    public void setSessionClazz(Class<S> sessionClazz) {
        this.sessionClazz = sessionClazz;
    }

    public Map<Label, List<RestBetween<S, U>>> getBefore() {
        return before;
    }

    public void setBefore(Map<Label, List<RestBetween<S, U>>> before) {
        this.before = before;
    }

    public Map<Label, List<RestBetween<S, U>>> getAfter() {
        return after;
    }

    public void setAfter(Map<Label, List<RestBetween<S, U>>> after) {
        this.after = after;
    }

    public Map<StatusCode, RestError<U, ? extends Translatable>> getRestErrors() {
        return restErrors;
    }

    public void setRestErrors(Map<StatusCode, RestError<U, ? extends Translatable>> restErrors) {
        this.restErrors = restErrors;
    }

    public Map<StatusCode, RestError<U, ? extends Translatable>> getDefaultErrors() {
        return defaultErrors;
    }

    public void setDefaultErrors(Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors) {
        this.defaultErrors = defaultErrors;
    }

    public Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> getDispatchErrors() {
        return dispatchErrors;
    }

    public void setDispatchErrors(Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors) {
        this.dispatchErrors = dispatchErrors;
    }

    public Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> getDefaultDispatchErrors() {
        return defaultDispatchErrors;
    }

    public void setDefaultDispatchErrors(Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors) {
        this.defaultDispatchErrors = defaultDispatchErrors;
    }

    public Validate getValidate() {
        return validate;
    }

    public void setValidate(Validate validate) {
        this.validate = validate;
    }

    public Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> getOnHalts() {
        return onHalts;
    }

    public void setOnHalts(Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts) {
        this.onHalts = onHalts;
    }

    public static class Builder<S extends DefaultSession, U extends DefaultUser> {
        private Class<S> sessionClazz;
        private Map<Label, List<RestBetween<S, U>>> before;
        private Map<Label, List<RestBetween<S, U>>> after;
        private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors;
        private Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors;
        private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors;
        private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors;
        private Validate validate;
        private Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts;

        public Builder<S, U> sessionClazz(Class<S> sessionClazz) {
            this.sessionClazz = sessionClazz;
            return this;
        }

        public Builder<S, U> before(Map<Label, List<RestBetween<S, U>>> before) {
            this.before = before;
            return this;
        }

        public Builder<S, U> after(Map<Label, List<RestBetween<S, U>>> after) {
            this.after = after;
            return this;
        }

        public Builder<S, U> restErrors(Map<StatusCode, RestError<U, ? extends Translatable>> restErrors) {
            this.restErrors = restErrors;
            return this;
        }

        public Builder<S, U> defaultErrors(Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors) {
            this.defaultErrors = defaultErrors;
            return this;
        }

        public Builder<S, U> dispatchErrors(Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors) {
            this.dispatchErrors = dispatchErrors;
            return this;
        }

        public Builder<S, U> defaultDispatchErrors(Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors) {
            this.defaultDispatchErrors = defaultDispatchErrors;
            return this;
        }

        public Builder<S, U> validate(Validate validate) {
            this.validate = validate;
            return this;
        }

        public Builder<S, U> onHalts(Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts) {
            this.onHalts = onHalts;
            return this;
        }

        public RestTranslatorConfig<S, U> build() {
            return new RestTranslatorConfig<>(sessionClazz, before, after, restErrors, defaultErrors, dispatchErrors, defaultDispatchErrors, validate, onHalts);
        }
    }
}
