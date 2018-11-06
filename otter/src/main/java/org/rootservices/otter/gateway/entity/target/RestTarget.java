package org.rootservices.otter.gateway.entity.target;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.translatable.Translatable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RestTarget<S extends DefaultSession, U extends DefaultUser, P extends Translatable> extends Target<S, U, P> {

    public RestTarget(List<Method> methods, String regex, Resource<S, U, P> resource, Map<Method, List<MimeType>> contentTypes, Optional<Class<P>> payload, List<Label> labels, List<Between<S, U, P>> before, List<Between<S, U, P>> after, Map<StatusCode, ErrorTarget<S, U, P>> errorTargets, String groupName) {
        super(methods, regex, resource, contentTypes, payload, labels, before, after, errorTargets, groupName);
    }
}
