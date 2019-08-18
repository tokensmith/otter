package org.rootservices.otter.controller.entity.mime;

public enum TopLevelType {
    TEXT, IMAGE, AUDIO, VIDEO, APPLICATION, MULTIPART;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
