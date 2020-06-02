package net.tokensmith.otter.gateway.entity;

import net.tokensmith.jwt.entity.jwk.SymmetricKey;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.StatusCode;

import java.util.Map;
import java.util.Optional;


/**
 * Used to instruct otter on what values to use for:
 *  - csrf sign keys
 *  - csrf fail status code
 *  - csrf fail template
 *  - session encryption keys
 *  - session fail status code
 *  - session fail template
 *  - rotation keys
 *  - async i/o chuck sizes
 */
public class Shape {
    public static final String SESSION_COOKIE_NAME = "session";
    public static final String CSRF_COOKIE_NAME = "csrfToken";

    private SymmetricKey signkey;
    private SymmetricKey encKey;
    private Map<String, SymmetricKey> rotationSignKeys;
    private Map<String, SymmetricKey> rotationEncKeys;
    private Integer writeChunkSize;
    private Integer readChunkSize;

    private Map<String, CookieConfig> cookieConfigs;

    public Shape(SymmetricKey signkey, SymmetricKey encKey, Map<String, SymmetricKey> rotationSignKeys, Map<String, SymmetricKey> rotationEncKeys, Integer writeChunkSize, Integer readChunkSize, Map<String, CookieConfig> cookieConfigs) {
        this.signkey = signkey;
        this.encKey = encKey;
        this.rotationSignKeys = rotationSignKeys;
        this.rotationEncKeys = rotationEncKeys;
        this.writeChunkSize = writeChunkSize;
        this.readChunkSize = readChunkSize;
        this.cookieConfigs = cookieConfigs;
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

    public Integer getWriteChunkSize() {
        return writeChunkSize;
    }

    public void setWriteChunkSize(Integer writeChunkSize) {
        this.writeChunkSize = writeChunkSize;
    }

    public Integer getReadChunkSize() {
        return readChunkSize;
    }

    public void setReadChunkSize(Integer readChunkSize) {
        this.readChunkSize = readChunkSize;
    }

    public CookieConfig getSessionCookie() {
        return cookieConfigs.get(SESSION_COOKIE_NAME);
    }

    public CookieConfig getCsrfCookie() {
        return cookieConfigs.get(CSRF_COOKIE_NAME);
    }

    public Map<String, CookieConfig> getCookieConfigs() {
        return cookieConfigs;
    }

    public void setCookieConfigs(Map<String, CookieConfig> cookieConfigs) {
        this.cookieConfigs = cookieConfigs;
    }
}
