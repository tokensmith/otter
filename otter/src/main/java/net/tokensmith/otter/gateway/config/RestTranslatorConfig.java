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
    private Map<Label, List<RestBetween<S, U>>> labelBefore;
    private Map<Label, List<RestBetween<S, U>>> labelAfter;
    private List<RestBetween<S, U>> befores;
    private List<RestBetween<S, U>> afters;
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors;
    private Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors;
    private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors;
    private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors;
    private Validate validate;
    // halts - custom halt handlers for security betweens
    private Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts;

    public RestTranslatorConfig(Class<S> sessionClazz, Map<Label, List<RestBetween<S, U>>> labelBefore, Map<Label, List<RestBetween<S, U>>> labelAfter, List<RestBetween<S, U>> befores, List<RestBetween<S, U>> afters, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors, Map<StatusCode, RestError<U, ? extends Translatable>> defaultErrors, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> defaultDispatchErrors, Validate validate, Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts) {
        this.sessionClazz = sessionClazz;
        this.labelBefore = labelBefore;
        this.labelAfter = labelAfter;
        this.befores = befores;
        this.afters = afters;
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

    public Map<Label, List<RestBetween<S, U>>> getLabelBefore() {
        return labelBefore;
    }

    public void setLabelBefore(Map<Label, List<RestBetween<S, U>>> labelBefore) {
        this.labelBefore = labelBefore;
    }

    public Map<Label, List<RestBetween<S, U>>> getLabelAfter() {
        return labelAfter;
    }

    public void setLabelAfter(Map<Label, List<RestBetween<S, U>>> labelAfter) {
        this.labelAfter = labelAfter;
    }

    public List<RestBetween<S, U>> getBefores() {
        return befores;
    }

    public void setBefores(List<RestBetween<S, U>> befores) {
        this.befores = befores;
    }

    public List<RestBetween<S, U>> getAfters() {
        return afters;
    }

    public void setAfters(List<RestBetween<S, U>> afters) {
        this.afters = afters;
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
        private Map<Label, List<RestBetween<S, U>>> labelBefore;
        private Map<Label, List<RestBetween<S, U>>> labelAfter;
        private List<RestBetween<S, U>> befores;
        private List<RestBetween<S, U>> afters;
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

        public Builder<S, U> labelBefore(Map<Label, List<RestBetween<S, U>>> labelBefore) {
            this.labelBefore = labelBefore;
            return this;
        }

        public Builder<S, U> labelAfter(Map<Label, List<RestBetween<S, U>>> labelAfter) {
            this.labelAfter = labelAfter;
            return this;
        }

        public Builder<S, U> befores(List<RestBetween<S, U>> befores) {
            this.befores = befores;
            return this;
        }

        public Builder<S, U> afters(List<RestBetween<S, U>> afters) {
            this.afters = afters;
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
            return new RestTranslatorConfig<>(
                    sessionClazz,
                    labelBefore,
                    labelAfter,
                    afters,
                    befores,
                    restErrors,
                    defaultErrors,
                    dispatchErrors,
                    defaultDispatchErrors,
                    validate,
                    onHalts
            );
        }
    }
}
