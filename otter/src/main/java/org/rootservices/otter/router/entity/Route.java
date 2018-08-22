package org.rootservices.otter.router.entity;



import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.security.session.Session;

import java.util.List;
import java.util.regex.Pattern;


public class Route<T extends Session> {
    private Pattern pattern;
    private List<MimeType> contentTypes;
    private Resource<T> resource;
    private List<Between<T>> before;
    private List<Between<T>> after;

    public Route(Pattern pattern, List<MimeType> contentTypes, Resource<T> resource, List<Between<T>> before, List<Between<T>> after) {
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

    public Resource<T> getResource() {
        return resource;
    }

    public void setResource(Resource<T> resource) {
        this.resource = resource;
    }

    public List<Between<T>> getBefore() {
        return before;
    }

    public void setBefore(List<Between<T>> before) {
        this.before = before;
    }

    public List<Between<T>> getAfter() {
        return after;
    }

    public void setAfter(List<Between<T>> after) {
        this.after = after;
    }

    @Override
    public String toString() {
        return pattern.toString();
    }
}
