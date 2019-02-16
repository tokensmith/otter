package org.rootservices.otter.gateway;


import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.gateway.entity.RestGroup;
import org.rootservices.otter.gateway.entity.Shape;

import java.util.List;

/**
 * Interface that must be implemented to configure a Otter application.
 *
 */
public interface Configure {
    Shape shape();
    List<Group<? extends DefaultSession, ? extends DefaultUser>> groups();
    List<RestGroup<? extends DefaultUser>> restGroups();
    void routes(Gateway gateway);
}
