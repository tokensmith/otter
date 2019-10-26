package net.tokensmith.otter.gateway.builder;

import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.gateway.entity.Shape;

import java.util.Map;

public class ShapeBuilder {
    private Boolean secure;
    private SymmetricKey signKey;
    private SymmetricKey encKey;
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

    public ShapeBuilder encKey(SymmetricKey encKey) {
        this.encKey = encKey;
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
        return new Shape(secure, signKey, encKey, rotationSignKeys, rotationEncKeys, writeChunkSize, readChunkSize);
    }
}
