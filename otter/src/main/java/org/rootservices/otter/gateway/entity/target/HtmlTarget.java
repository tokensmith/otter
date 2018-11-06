package org.rootservices.otter.gateway.entity.target;

import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.EmptyPayload;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.entity.ErrorTarget;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Method;


import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HtmlTarget<S extends DefaultSession, U extends DefaultUser> extends Target<S, U, EmptyPayload> {
    public HtmlTarget(List<Method> methods, String regex, Resource<S, U, EmptyPayload> resource, Map<Method, List<MimeType>> contentTypes, Optional<Class<EmptyPayload>> payload, List<Label> labels, List<Between<S, U, EmptyPayload>> before, List<Between<S, U, EmptyPayload>> after, Map<StatusCode, ErrorTarget<S, U, EmptyPayload>> errorTargets, String groupName) {
        super(methods, regex, resource, contentTypes, payload, labels, before, after, errorTargets, groupName);
    }
}
