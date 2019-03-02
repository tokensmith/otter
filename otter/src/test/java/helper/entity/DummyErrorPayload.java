package helper.entity;

import org.rootservices.otter.translatable.Translatable;

public class DummyErrorPayload implements Translatable {
    private String error;
    private String description;

    public DummyErrorPayload() {
    }

    public DummyErrorPayload(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
