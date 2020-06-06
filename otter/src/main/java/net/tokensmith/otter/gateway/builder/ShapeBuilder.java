package net.tokensmith.otter.gateway.builder;

import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.gateway.entity.Shape;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

public class ShapeBuilder {
    private SymmetricKey signKey;
    private SymmetricKey encKey;
    private Map<String, SymmetricKey> rotationSignKeys;
    private Map<String, SymmetricKey> rotationEncKeys;
    private Integer writeChunkSize;
    private Integer readChunkSize;
    private Map<String, CookieConfig> cookieConfigs = new HashMap<>();

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

    public ShapeBuilder sessionCookieConfig(CookieConfig cookieConfig) {
        this.cookieConfigs.put(Shape.SESSION_COOKIE_NAME, cookieConfig);
        return this;
    }

    public ShapeBuilder csrfCookieConfig(CookieConfig cookieConfig) {
        this.cookieConfigs.put(Shape.CSRF_COOKIE_NAME, cookieConfig);
        return this;
    }

    public ShapeBuilder cookieConfig(CookieConfig cookieConfig) {
        this.cookieConfigs.put(cookieConfig.getName(), cookieConfig);
        return this;
    }

    public Shape build() {

        // csrf default
        cookieConfigs.putIfAbsent(
            Shape.CSRF_COOKIE_NAME,
            new CookieConfig(Shape.CSRF_COOKIE_NAME, false, -1, true)
        );

        // session default
        cookieConfigs.putIfAbsent(
            Shape.SESSION_COOKIE_NAME,
            new CookieConfig(Shape.SESSION_COOKIE_NAME, false, -1, true)
        );

        return new Shape(
            signKey,
            encKey,
            rotationSignKeys,
            rotationEncKeys,
            writeChunkSize,
            readChunkSize,
            cookieConfigs
        );
    }
}
