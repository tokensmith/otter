package net.tokensmith.otter.controller.entity;

import net.tokensmith.otter.translatable.Translatable;

import java.util.List;

public class ClientError implements Translatable {
    private List<Cause> causes;

    public ClientError() {
    }

    public ClientError(List<Cause> causes) {
        this.causes = causes;
    }

    public List<Cause> getCauses() {
        return causes;
    }
}
