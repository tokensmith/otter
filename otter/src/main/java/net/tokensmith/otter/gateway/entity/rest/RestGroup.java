package net.tokensmith.otter.gateway.entity.rest;


import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.gateway.entity.Label;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.Halt;
import net.tokensmith.otter.translatable.Translatable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;


public class RestGroup<S extends DefaultSession, U extends DefaultUser> {
    private String name;
    private Class<S> sessionClazz;

    private Map<Label, List<RestBetween<S, U>>> labelBefore;
    private Map<Label, List<RestBetween<S, U>>> labelAfter;

    private List<RestBetween<S, U>> befores;
    private List<RestBetween<S, U>> afters;

    // for route run to handle errors.
    private Map<StatusCode, RestError<U, ? extends Translatable>> restErrors;
    // for engine to handle errors
    private Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors;
    // halts - custom halt handlers for security betweens
    private Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts;

    public RestGroup(String name, Class<S> sessionClazz, Map<Label, List<RestBetween<S, U>>> labelBefore, Map<Label, List<RestBetween<S, U>>> labelAfter, List<RestBetween<S, U>> befores, List<RestBetween<S, U>> afters, Map<StatusCode, RestError<U, ? extends Translatable>> restErrors, Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors, Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts) {
        this.name = name;
        this.sessionClazz = sessionClazz;
        this.labelBefore = labelBefore;
        this.labelAfter = labelAfter;
        this.befores = befores;
        this.afters = afters;
        this.restErrors = restErrors;
        this.dispatchErrors = dispatchErrors;
        this.onHalts = onHalts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> getDispatchErrors() {
        return dispatchErrors;
    }

    public void setDispatchErrors(Map<StatusCode, RestErrorTarget<S, U, ? extends Translatable>> dispatchErrors) {
        this.dispatchErrors = dispatchErrors;
    }

    public Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> getOnHalts() {
        return onHalts;
    }

    public void setOnHalts(Map<Halt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse>> onHalts) {
        this.onHalts = onHalts;
    }
}
