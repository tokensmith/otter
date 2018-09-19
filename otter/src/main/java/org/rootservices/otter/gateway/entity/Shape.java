package org.rootservices.otter.gateway.entity;

import org.rootservices.jwt.entity.jwk.SymmetricKey;

import java.util.Map;

public class Shape<S> {
    private Boolean secure;
    private SymmetricKey signkey;
    private SymmetricKey encKey;
    private Map<String, SymmetricKey> rotationSignKeys;
    private Map<String, SymmetricKey> rotationEncKeys;
    private Class<S> sessionClass;

    public Shape(Boolean secure, SymmetricKey signkey, SymmetricKey encKey, Map<String, SymmetricKey> rotationSignKeys, Map<String, SymmetricKey> rotationEncKeys, Class<S> sessionClass) {
        this.secure = secure;
        this.signkey = signkey;
        this.encKey = encKey;
        this.rotationSignKeys = rotationSignKeys;
        this.rotationEncKeys = rotationEncKeys;
        this.sessionClass = sessionClass;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public SymmetricKey getSignkey() {
        return signkey;
    }

    public void setSignkey(SymmetricKey signkey) {
        this.signkey = signkey;
    }

    public SymmetricKey getEncKey() {
        return encKey;
    }

    public void setEncKey(SymmetricKey encKey) {
        this.encKey = encKey;
    }

    public Map<String, SymmetricKey> getRotationSignKeys() {
        return rotationSignKeys;
    }

    public void setRotationSignKeys(Map<String, SymmetricKey> rotationSignKeys) {
        this.rotationSignKeys = rotationSignKeys;
    }

    public Map<String, SymmetricKey> getRotationEncKeys() {
        return rotationEncKeys;
    }

    public void setRotationEncKeys(Map<String, SymmetricKey> rotationEncKeys) {
        this.rotationEncKeys = rotationEncKeys;
    }

    public Class<S> getSessionClass() {
        return sessionClass;
    }

    public void setSessionClass(Class<S> sessionClass) {
        this.sessionClass = sessionClass;
    }
}
