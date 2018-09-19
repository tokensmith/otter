package org.rootservices.otter.gateway.builder;

import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.gateway.entity.Shape;

import java.util.Map;

public class ShapeBuilder<S> {
    private Boolean secure;
    private SymmetricKey signKey;
    private SymmetricKey encKey;
    private Map<String, SymmetricKey> rotationSignKeys;
    private Map<String, SymmetricKey> rotationEncKeys;
    private Class<S> sessionClass;

    public ShapeBuilder<S> secure(Boolean secure) {
        this.secure = secure;
        return this;
    }

    public ShapeBuilder<S> signkey(SymmetricKey signKey) {
        this.signKey = signKey;
        return this;
    }

    public ShapeBuilder<S> encKey(SymmetricKey encKey) {
        this.encKey = encKey;
        return this;
    }

    public ShapeBuilder<S> rotationSignKeys(Map<String, SymmetricKey> rotationSignKeys) {
        this.rotationSignKeys = rotationSignKeys;
        return this;
    }

    public ShapeBuilder<S> rotationEncKeys(Map<String, SymmetricKey> rotationEncKeys) {
        this.rotationEncKeys = rotationEncKeys;
        return this;
    }

    public ShapeBuilder<S> sessionClass(Class<S> sessionClass) {
        this.sessionClass = sessionClass;
        return this;
    }

    public Shape<S> build() {
        return new Shape<S>(secure, signKey, encKey, rotationSignKeys, rotationEncKeys, sessionClass);
    }
}
