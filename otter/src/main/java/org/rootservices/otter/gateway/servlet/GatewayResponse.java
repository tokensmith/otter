package org.rootservices.otter.gateway.servlet;


import java.util.Optional;

public class GatewayResponse {
    private Optional<byte[]> payload = Optional.empty();
    private Optional<String> template = Optional.empty();
    private Integer writeChunkSize;

    public GatewayResponse() {}

    public Optional<byte[]> getPayload() {
        return payload;
    }

    public void setPayload(Optional<byte[]> payload) {
        this.payload = payload;
    }

    public Optional<String> getTemplate() {
        return template;
    }

    public void setTemplate(Optional<String> template) {
        this.template = template;
    }

    public Integer getWriteChunkSize() {
        return writeChunkSize;
    }

    public void setWriteChunkSize(Integer writeChunkSize) {
        this.writeChunkSize = writeChunkSize;
    }
}
