package org.rootservices.otter.controller.entity.mime;

public enum ParamKey {
    CHARSET;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
