package org.rootservices.otter.router.entity;



import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.security.session.Session;

import java.util.List;
import java.util.regex.Pattern;


public class Route<S extends Session, U> {
    private Pattern pattern;
    private List<MimeType> contentTypes;
    private Resource<S, U> resource;
    private List<Between<S, U>> before;
    private List<Between<S, U>> after;

    public Route(Pattern pattern, List<MimeType> contentTypes, Resource<S, U> resource, List<Between<S, U>> before, List<Between<S, U>> after) {
        this.pattern = pattern;
        this.contentTypes = contentTypes;
        this.resource = resource;
        this.before = before;
        this.after = after;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public List<MimeType> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(List<MimeType> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public Resource<S, U> getResource() {
        return resource;
    }

    public void setResource(Resource<S, U> resource) {
        this.resource = resource;
    }

    public List<Between<S, U>> getBefore() {
        return before;
    }

    public void setBefore(List<Between<S, U>> before) {
        this.before = before;
    }

    public List<Between<S, U>> getAfter() {
        return after;
    }

    public void setAfter(List<Between<S, U>> after) {
        this.after = after;
    }

    @Override
    public String toString() {
        return pattern.toString();
    }
}
