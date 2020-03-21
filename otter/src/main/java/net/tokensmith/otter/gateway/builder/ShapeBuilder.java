package net.tokensmith.otter.gateway.builder;

import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.gateway.entity.Shape;

import java.util.Map;
import java.util.Optional;

public class ShapeBuilder {
    private Boolean secure;
    private SymmetricKey signKey;
    private Optional<String> csrfFailTemplate = Optional.empty();
    private StatusCode csrfFailStatusCode = StatusCode.FORBIDDEN;
    private SymmetricKey encKey;
    private Optional<String> sessionFailTemplate = Optional.empty();
    private StatusCode sessionFailStatusCode = StatusCode.UNAUTHORIZED;
    private Map<String, SymmetricKey> rotationSignKeys;
    private Map<String, SymmetricKey> rotationEncKeys;
    private Integer writeChunkSize;
    private Integer readChunkSize;

    public ShapeBuilder secure(Boolean secure) {
        this.secure = secure;
        return this;
    }

    public ShapeBuilder signkey(SymmetricKey signKey) {
        this.signKey = signKey;
        return this;
    }

    public ShapeBuilder csrfFailTemplate(Optional<String> csrfFailTemplate) {
        this.csrfFailTemplate = csrfFailTemplate;
        return this;
    }

    public ShapeBuilder csrfFailStatusCode(StatusCode csrfFailStatusCode) {
        this.csrfFailStatusCode = csrfFailStatusCode;
        return this;
    }

    public ShapeBuilder encKey(SymmetricKey encKey) {
        this.encKey = encKey;
        return this;
    }

    public ShapeBuilder sessionFailTemplate(Optional<String> sessionFailTemplate) {
        this.sessionFailTemplate = sessionFailTemplate;
        return this;
    }

    public ShapeBuilder sessionFailStatusCode(StatusCode sessionFailStatusCode) {
        this.sessionFailStatusCode = sessionFailStatusCode;
        return this;
    }

    public ShapeBuilder rotationSignKeys(Map<String, SymmetricKey> rotationSignKeys) {
        this.rotationSignKeys = rotationSignKeys;
        return this;
    }

    public ShapeBuilder rotationEncKeys(Map<String, SymmetricKey> rotationEncKeys) {
        this.rotationEncKeys = rotationEncKeys;
        return this;
    }

    public ShapeBuilder writeChunkSize(Integer writeChunkSize) {
        this.writeChunkSize = writeChunkSize;
        return this;
    }

    public ShapeBuilder readChunkSize(Integer readChunkSize) {
        this.readChunkSize = readChunkSize;
        return this;
    }

    public Shape build() {
        return new Shape(secure, signKey, csrfFailTemplate, csrfFailStatusCode, encKey, sessionFailTemplate, sessionFailStatusCode, rotationSignKeys, rotationEncKeys, writeChunkSize, readChunkSize);
    }
}
