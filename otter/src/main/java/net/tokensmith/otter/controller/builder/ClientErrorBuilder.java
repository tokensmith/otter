package net.tokensmith.otter.controller.builder;

import net.tokensmith.otter.controller.entity.Cause;
import net.tokensmith.otter.controller.entity.ClientError;

import java.util.ArrayList;
import java.util.List;

public class ClientErrorBuilder {
    private List<Cause> causes = new ArrayList<>();

    public ClientErrorBuilder cause(Cause cause) {
        this.causes.add(cause);
        return this;
    }

    public ClientError build() {
        return new ClientError(causes);
    }
}
