package org.rootservices.otter.router.entity;



import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.RestResource;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.security.session.Session;

import java.util.List;
import java.util.regex.Pattern;


public class Route {
    private Pattern pattern;
    private List<MimeType> contentTypes;
    private Resource<Session> resource;
    private List<Between<Session>> before;
    private List<Between<Session>> after;

    public Route(Pattern pattern, List<MimeType> contentTypes, Resource<Session> resource, List<Between<Session>> before, List<Between<Session>> after) {
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

    public Resource<Session> getResource() {
        return resource;
    }

    public void setResource(Resource<Session> resource) {
        this.resource = resource;
    }

    public List<Between<Session>> getBefore() {
        return before;
    }

    public void setBefore(List<Between<Session>> before) {
        this.before = before;
    }

    public List<Between<Session>> getAfter() {
        return after;
    }

    public void setAfter(List<Between<Session>> after) {
        this.after = after;
    }

    @Override
    public String toString() {
        return pattern.toString();
    }
}
