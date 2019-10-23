package net.tokensmith.otter.gateway.entity;

import org.rootservices.jwt.entity.jwk.SymmetricKey;

import java.util.Map;


/**
 * Used to instruct otter on what values to use for:
 *  - csrf sign keys
 *  - session encryption keys
 *  - rotation keys
 *  - async i/o chuck sizes
 */
public class Shape {
    private Boolean secure;
    private SymmetricKey signkey;
    private SymmetricKey encKey;
    private Map<String, SymmetricKey> rotationSignKeys;
    private Map<String, SymmetricKey> rotationEncKeys;
    private Integer writeChunkSize;
    private Integer readChunkSize;

    public Shape(Boolean secure, SymmetricKey signkey, SymmetricKey encKey, Map<String, SymmetricKey> rotationSignKeys, Map<String, SymmetricKey> rotationEncKeys, Integer writeChunkSize, Integer readChunkSize) {
        this.secure = secure;
        this.signkey = signkey;
        this.encKey = encKey;
        this.rotationSignKeys = rotationSignKeys;
        this.rotationEncKeys = rotationEncKeys;
        this.writeChunkSize = writeChunkSize;
        this.readChunkSize = readChunkSize;
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
}
