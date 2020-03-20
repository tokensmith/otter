package net.tokensmith.otter.gateway;


import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.gateway.entity.Group;
import net.tokensmith.otter.gateway.entity.rest.RestGroup;
import net.tokensmith.otter.gateway.entity.Shape;

import java.util.List;

/**
 * Interface that must be implemented to configure a Otter application.
 *
 */
public interface Configure {
    /**
     * Returns a shape instance which instructs otter what to use for the csrf sign keys, session encrption keys,
     * rotation keys, and async i/o chuck sizes.
     *
     * @return an instance of a Shape
     */
    Shape shape();

    /**
     * Returns a list of Groups which is used to share betweens, Session, and User amongst Routes.
     *
     * @return a List of Groups
     */
    List<Group<? extends DefaultSession, ? extends DefaultUser>> groups();

    /**
     * Returns a list of RestGroup which is used to share RestBetweens and User amongst RestRoutes.
     *
     * @return a List of RestGroup
     */
    List<RestGroup<? extends DefaultSession, ? extends DefaultUser>> restGroups();

    /**
     * Add Routes and RestRoutes to the gateway here.
     *
     * @param gateway An instance of the gateway.
     */
    void routes(Gateway gateway);
}
