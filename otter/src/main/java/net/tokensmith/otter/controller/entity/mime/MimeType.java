package net.tokensmith.otter.controller.entity.mime;

import java.util.Map;
import java.util.Objects;

public class MimeType {
    private String type;
    private String subType;
    private Map<String, String> parameters;

    public MimeType() {
    }

    public MimeType(String type, String subType, Map<String, String> parameters) {
        this.type = type;
        this.subType = subType;
        this.parameters = parameters;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MimeType)) return false;
        MimeType that = (MimeType) o;
        return Objects.equals(getType(), that.getType()) &&
                Objects.equals(getSubType(), that.getSubType()) &&
                that.getParameters().size() == this.getParameters().size() &&
                that.getParameters().entrySet().stream().anyMatch(
                    e -> this.getParameters().get(e.getKey()).equals(e.getValue()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getSubType(), getParameters());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(type).append("/").append(subType).append(";");
        parameters.forEach((k, v) ->  builder.append(" ").append(k).append("=").append(v).append(";"));
        return builder.toString();
    }
}
