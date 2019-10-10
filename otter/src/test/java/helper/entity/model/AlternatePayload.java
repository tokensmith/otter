package helper.entity.model;

import java.util.UUID;

public class AlternatePayload {
    private UUID id;

    public AlternatePayload(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
