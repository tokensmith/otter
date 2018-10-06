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
import org.rootservices.otter.router.factory.BetweenFactory;
import org.rootservices.otter.router.factory.ErrorRouteFactory;
import org.rootservices.otter.router.factory.ErrorRouteRunnerFactory;
import org.rootservices.otter.security.RandomString;
import org.rootservices.otter.security.builder.BetweenBuilder;
import org.rootservices.otter.security.builder.entity.Betweens;
import org.rootservices.otter.security.exception.SessionCtorException;
import org.rootservices.otter.security.csrf.DoubleSubmitCSRF;
import org.rootservices.otter.server.container.ServletContainerFactory;
import org.rootservices.otter.server.path.CompiledClassPath;
import org.rootservices.otter.server.path.WebAppPath;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.MimeTypeTranslator;

import java.util.Base64;


/**
 * Application Factory to construct objects in project.
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 */
public class OtterAppFactory<S, U> {
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
     * Make a JsonTranslator used in RestResource
     *
     * @param clazz Class to be serialized
     * @param <T> Type to be serialized
     * @return instance of a JsonTranslator intended for T
     */
    public <T> JsonTranslator<T> jsonTranslator(Class<T> clazz) {
        return new JsonTranslator<T>(
                objectReader(), objectWriter(), clazz
        );
    }

    public ServletGateway<S, U> servletGateway(Shape<S> shape) throws SessionCtorException {

        return new ServletGateway<S, U>(
                httpServletRequestTranslator(),
                httpServletRequestMerger(),
                httpServletResponseMerger(),
                engine(),
                locationTranslator(shape)
        );
    }

    public LocationTranslator<S, U> locationTranslator(Shape<S> shape) throws SessionCtorException {
        return new LocationTranslator<S, U>(
                betweenFactory(shape)
        );
    }

    public BetweenFactory<S, U> betweenFactory(Shape<S> shape) throws SessionCtorException {
        return new BetweenFactory<S, U>(
                csrfPrepare(shape),
                csrfProtect(shape),
                session(shape),
                sessionOptional(shape)
        );
    }

    public Betweens<S, U> csrfPrepare(Shape<S> shape) {
        return new BetweenBuilder<S, U>()
                .otterFactory(this)
                .secure(shape.getSecure())
                .signKey(shape.getSignkey())
                .rotationSignKeys(shape.getRotationSignKeys())
                .csrfPrepare()
                .build();

    }

    public Betweens<S, U> csrfProtect(Shape<S> shape) {
        return new BetweenBuilder<S, U>()
                .otterFactory(this)
                .secure(shape.getSecure())
                .signKey(shape.getSignkey())
                .rotationSignKeys(shape.getRotationSignKeys())
                .csrfProtect()
                .build();

    }

    public Betweens<S, U> session(Shape<S> shape) throws SessionCtorException {
        return new BetweenBuilder<S, U>()
                .otterFactory(this)
                .secure(shape.getSecure())
                .encKey(shape.getEncKey())
                .rotationEncKey(shape.getRotationEncKeys())
                .sessionClass(shape.getSessionClass())
                .session()
                .build();
    }

    public Betweens<S, U> sessionOptional(Shape<S> shape) throws SessionCtorException {
        return new BetweenBuilder<S, U>()
                .otterFactory(this)
                .secure(shape.getSecure())
                .encKey(shape.getEncKey())
                .rotationEncKey(shape.getRotationEncKeys())
                .sessionClass(shape.getSessionClass())
                .optionalSession()
                .build();
    }

    public Engine<S, U> engine() {
        return new Engine<S, U>(new Dispatcher<S, U>(), new ErrorRouteFactory<S, U>(), new ErrorRouteRunnerFactory());
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

    public HttpServletRequestTranslator<S, U> httpServletRequestTranslator() {
        return new HttpServletRequestTranslator<S, U>(
                httpServletRequestCookieTranslator(),
                new HttpServletRequestHeaderTranslator(),
                new QueryStringToMap(),
                new MimeTypeTranslator()
        );
    }

    public HttpServletRequestMerger httpServletRequestMerger() {
        return new HttpServletRequestMerger();
    }

    public HttpServletResponseMerger<S> httpServletResponseMerger() {
        return new HttpServletResponseMerger<S>(httpServletRequestCookieTranslator());
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
