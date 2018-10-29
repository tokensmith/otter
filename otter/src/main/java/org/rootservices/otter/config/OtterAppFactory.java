package org.rootservices.otter.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.rootservices.jwt.config.JwtAppFactory;
import org.rootservices.otter.QueryStringToMap;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.gateway.LocationTranslatorFactory;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.gateway.entity.Shape;
import org.rootservices.otter.gateway.servlet.ServletGateway;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestCookieTranslator;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestHeaderTranslator;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import org.rootservices.otter.gateway.translator.LocationTranslator;
import org.rootservices.otter.router.Dispatcher;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.factory.ErrorRouteRunnerFactory;
import org.rootservices.otter.security.RandomString;
import org.rootservices.otter.security.exception.SessionCtorException;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;
import org.rootservices.otter.server.container.ServletContainerFactory;
import org.rootservices.otter.server.path.CompiledClassPath;
import org.rootservices.otter.server.path.WebAppPath;
import org.rootservices.otter.translatable.Translatable;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.MimeTypeTranslator;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Application Factory to construct objects in project.
 */
public class OtterAppFactory {
    public static Integer WRITE_CHUNK_SIZE = 1024;
    private static ObjectMapper objectMapper;
    private static ObjectReader objectReader;
    private static ObjectWriter objectWriter;

    public CompiledClassPath compiledClassPath() {
        return new CompiledClassPath();
    }

    public WebAppPath webAppPath() {
        return new WebAppPath();
    }

    public ServletContainerFactory servletContainerFactory() {
        return new ServletContainerFactory(
                compiledClassPath(),
                webAppPath()
        );
    }

    /**
     * Make a JsonTranslator used in RestResource.
     * It must be used exclusively for {@code Class<T> clazz}
     *
     * @param clazz Class to be serialized
     * @param <T> Type to be serialized
     * @return instance of a JsonTranslator intended for T
     */
    public <T> JsonTranslator<T> jsonTranslator(Class<T> clazz) {
        return new JsonTranslator<T>(
                objectReader().forType(clazz), objectWriter(), clazz
        );
    }

    public ServletGateway servletGateway(Shape shape, List<Group<? extends DefaultSession,? extends DefaultUser>> groups) throws SessionCtorException {
        LocationTranslatorFactory locationTranslatorFactory = locationTranslatorFactory(shape);
        Map<String, LocationTranslator<? extends DefaultSession, ? extends DefaultUser, ? extends Translatable>> locationTranslators = locationTranslators(locationTranslatorFactory, groups);

        Integer writeChunkSize = (shape.getWriteChunkSize() != null) ? shape.getWriteChunkSize() : WRITE_CHUNK_SIZE;

        return new ServletGateway(
                httpServletRequestTranslator(),
                httpServletRequestMerger(),
                httpServletResponseMerger(),
                engine(),
                locationTranslators,
                writeChunkSize
        );
    }

    public Engine engine() {
        return new Engine(new Dispatcher(), new ErrorRouteRunnerFactory());
    }

    public LocationTranslatorFactory locationTranslatorFactory(Shape shape) {
        return new LocationTranslatorFactory(shape);
    }

    @SuppressWarnings("unchecked")
    public <S extends DefaultSession, U extends DefaultUser, P extends Translatable> Map<String, LocationTranslator<? extends S, ? extends U, ? extends P>> locationTranslators(LocationTranslatorFactory locationTranslatorFactory, List<Group<? extends S, ? extends U>> groups) throws SessionCtorException {
        Map<String, LocationTranslator<? extends S, ? extends U, ? extends P>> locationTranslators = new HashMap<>();

        for(Group<? extends S, ? extends U> group: groups) {
            Group<S, U> castedGroup = (Group<S,U>) group;
            locationTranslators.put(
                    group.getName(),
                    locationTranslatorFactory.make(
                            castedGroup.getSessionClazz(),
                            castedGroup.getAuthRequired(),
                            castedGroup.getAuthOptional()
                    )
            );
        }

        return locationTranslators;
    }

    public ObjectMapper objectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper()
                    .setPropertyNamingStrategy(
                            PropertyNamingStrategy.SNAKE_CASE
                    )
                    .configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true)
                    .registerModule(new Jdk8Module())
                    .registerModule(new JavaTimeModule());
        }
        return objectMapper;
    }

    public ObjectReader objectReader() {
        if (objectReader == null) {
            objectReader = objectMapper().reader();
        }
        return objectReader;
    }

    public ObjectWriter objectWriter() {
        if (objectWriter == null) {
            objectWriter = objectMapper().writer();
        }
        return objectWriter;
    }

    public HttpServletRequestTranslator httpServletRequestTranslator() {
        return new HttpServletRequestTranslator(
                httpServletRequestCookieTranslator(),
                new HttpServletRequestHeaderTranslator(),
                new QueryStringToMap(),
                new MimeTypeTranslator()
        );
    }

    public HttpServletRequestMerger httpServletRequestMerger() {
        return new HttpServletRequestMerger();
    }

    public HttpServletResponseMerger httpServletResponseMerger() {
        return new HttpServletResponseMerger(httpServletRequestCookieTranslator());
    }

    public HttpServletRequestCookieTranslator httpServletRequestCookieTranslator() {
        return new HttpServletRequestCookieTranslator();
    }

    public JwtAppFactory jwtAppFactory() {
        return new JwtAppFactory();
    }

    public DoubleSubmitCSRF doubleSubmitCSRF() {
        return new DoubleSubmitCSRF(jwtAppFactory(), new RandomString());
    }

    public Base64.Decoder urlDecoder() {
        return Base64.getUrlDecoder();
    }
}
