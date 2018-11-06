package org.rootservices.otter.gateway;


import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.gateway.entity.Shape;
import org.rootservices.otter.translatable.Translatable;

import java.util.List;

/**
 * Interface that must be implemented to configure a Otter application.
 *
 */
public interface Configure {
    Shape shape();
    List<Group<? extends DefaultSession, ? extends DefaultUser, ? extends Translatable>> groups();
    void routes(Gateway gateway);
}
