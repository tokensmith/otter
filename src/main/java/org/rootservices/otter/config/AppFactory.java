package org.rootservices.otter.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.rootservices.otter.QueryStringToMap;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.gateway.servlet.ServletGateway;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.request.HttpServletRequestCookieTranslator;
import org.rootservices.otter.gateway.servlet.translator.request.HttpServletRequestHeaderTranslator;
import org.rootservices.otter.gateway.servlet.translator.request.HttpServletRequestTranslator;
import org.rootservices.otter.gateway.servlet.translator.request.HttpServletRequestUrlTranslator;
import org.rootservices.otter.router.Dispatcher;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.server.container.ServletContainerFactory;
import org.rootservices.otter.server.path.CompiledClassPath;
import org.rootservices.otter.server.path.WebAppPath;
import org.rootservices.otter.translator.JsonTranslator;


/**
 * Created by tommackenzie on 4/5/16.
 *
 * Application Factory merge construct objects in project.
 */
public class AppFactory {

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

    public ServletGateway servletGateway() {
        return new ServletGateway(
                httpServletRequestTranslator(),
                httpServletRequestMerger(),
                httpServletResponseMerger(),
                engine()
        );

    }

    public Engine engine() {
        return new Engine(new Dispatcher());
    }

    public ObjectMapper objectMapper() {
        ObjectMapper om =  new ObjectMapper()
                .setPropertyNamingStrategy(
                        PropertyNamingStrategy.SNAKE_CASE
                )
                .configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true)
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        return om;
    }

    public HttpServletRequestTranslator httpServletRequestTranslator() {
        return new HttpServletRequestTranslator(
                httpServletRequestCookieTranslator(),
                new HttpServletRequestHeaderTranslator(),
                new QueryStringToMap(),
                new HttpServletRequestUrlTranslator()
        );
    }

    public HttpServletRequestMerger httpServletRequestMerger() {
        return new HttpServletRequestMerger(httpServletRequestCookieTranslator());
    }

    public HttpServletResponseMerger httpServletResponseMerger() {
        return new HttpServletResponseMerger(httpServletRequestCookieTranslator());
    }

    public HttpServletRequestCookieTranslator httpServletRequestCookieTranslator() {
        return new HttpServletRequestCookieTranslator();
    }

}
