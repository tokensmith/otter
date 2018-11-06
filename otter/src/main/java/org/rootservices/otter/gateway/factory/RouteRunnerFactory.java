package org.rootservices.otter.gateway.factory;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.dispatch.HtmlRouteRun;
import org.rootservices.otter.dispatch.RestRouteRunner;
import org.rootservices.otter.dispatch.RouteRunner;
import org.rootservices.otter.dispatch.translator.AnswerTranslator;
import org.rootservices.otter.dispatch.translator.RequestTranslator;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.translatable.Translatable;
import org.rootservices.otter.translator.JsonTranslator;

import java.util.List;
import java.util.Optional;

// TODO: 104 need to integrate with LocationTranslator.
public class RouteRunnerFactory {
    private ObjectReader objectReader;
    private ObjectWriter objectWriter;

    public RouteRunnerFactory(ObjectReader objectReader, ObjectWriter objectWriter) {
        this.objectReader = objectReader;
        this.objectWriter = objectWriter;
    }

    public <S extends DefaultSession, U extends DefaultUser, P extends Translatable> RouteRunner make(Resource<S, U, P> resource, List<Between<S, U, P>> before, List<Between<S, U, P>> after, Optional<Class<P>> payloadClazz) {
        RouteRunner routeRunner = null;

        // TODO: this is a propagating dependency.
        Route<S, U, P> route = new RouteBuilder<S, U, P>()
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        // TODO: these may need to be specific to a RouteRunner
        RequestTranslator<S, U, P> requestTranslator = new RequestTranslator<>();
        AnswerTranslator<S> answerTranslator = new AnswerTranslator<>();

        if (payloadClazz.isPresent()) {
            JsonTranslator<P> jsonTranslator = new JsonTranslator<P> (objectReader.forType(payloadClazz.get()), objectWriter, payloadClazz.get());
            routeRunner = new RestRouteRunner<S, U, P>(jsonTranslator, route, requestTranslator, answerTranslator);
        } else {
            routeRunner = new HtmlRouteRun<S, U, P>(route, requestTranslator, answerTranslator);
        }
        return routeRunner;
    }
}
