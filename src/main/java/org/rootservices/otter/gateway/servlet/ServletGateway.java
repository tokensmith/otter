package org.rootservices.otter.gateway.servlet;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.builder.ResponseBuilder;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.RouteBuilder;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.security.csrf.between.CheckCSRF;
import org.rootservices.otter.security.csrf.between.PrepareCSRF;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


public class ServletGateway {
    protected static Logger logger = LogManager.getLogger(ServletGateway.class);

    private HttpServletRequestTranslator httpServletRequestTranslator;
    private HttpServletRequestMerger httpServletRequestMerger;
    private HttpServletResponseMerger httpServletResponseMerger;
    private Engine engine;
    private Between prepareCSRF;
    private Between checkCSRF;
    private Route notFoundRoute;

    public ServletGateway(HttpServletRequestTranslator httpServletRequestTranslator, HttpServletRequestMerger httpServletRequestMerger, HttpServletResponseMerger httpServletResponseMerger, Engine engine, Between prepareCSRF, Between checkCSRF) {
        this.httpServletRequestTranslator = httpServletRequestTranslator;
        this.httpServletRequestMerger = httpServletRequestMerger;
        this.httpServletResponseMerger = httpServletResponseMerger;
        this.engine = engine;
        this.prepareCSRF = prepareCSRF;
        this.checkCSRF = checkCSRF;
    }

    public GatewayResponse processRequest(HttpServletRequest containerRequest, HttpServletResponse containerResponse, String body) {
        GatewayResponse gatewayResponse = new GatewayResponse();
        try {
            Request request = httpServletRequestTranslator.from(containerRequest, body);

            Response response = new ResponseBuilder()
                    .headers(new HashMap<>())
                    .cookies(request.getCookies())
                    .payload(Optional.empty())
                    .presenter(Optional.empty())
                    .template(Optional.empty())
                    .build();


            Boolean shouldHalt = false;
            Optional<Response> resourceResponse;
            try {
                resourceResponse = engine.route(request, response);
            } catch (HaltException e) {
                // should not route to the notFoundRoute.
                shouldHalt = true;
                resourceResponse = Optional.of(response);
                logger.debug(e.getMessage(), e);
            }

            // route to not found if it wasn't halted.
            if (!shouldHalt && !resourceResponse.isPresent()) {
                resourceResponse = Optional.of(engine.executeResourceMethod(notFoundRoute, request, response));
            }

            httpServletResponseMerger.merge(containerResponse, containerRequest.getCookies(), resourceResponse.get());
            httpServletRequestMerger.merge(containerRequest, resourceResponse.get());

            if (resourceResponse.get().getPayload().isPresent()) {
                gatewayResponse.setPayload(Optional.of(resourceResponse.get().getPayload().get().toByteArray()));
            } else {
                gatewayResponse.setPayload(Optional.empty());
            }
            gatewayResponse.setTemplate(response.getTemplate());

        } catch (IOException | ServletException e) {
            logger.error(e.getMessage(), e);
            containerResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            containerResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return gatewayResponse;
    }

    public void get(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getGet().add(route);
    }

    public void getCsrfProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(prepareCSRF);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(new ArrayList<>())
                .build();

        engine.getDispatcher().getGet().add(route);
    }

    public void post(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .after(new ArrayList<>())
                .before(new ArrayList<>())
                .build();
        engine.getDispatcher().getPost().add(route);
    }

    public void postCsrfProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(checkCSRF);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(new ArrayList<>())
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void put(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getPut().add(route);
    }

    public void patch(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getPatch().add(route);
    }

    public void delete(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getDelete().add(route);
    }

    public void connect(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getConnect().add(route);
    }

    public void options(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getOptions().add(route);
    }

    public void trace(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getTrace().add(route);
    }

    public void head(String path, Resource resource) {
        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();
        engine.getDispatcher().getHead().add(route);
    }

    // configuration methods below.

    public void setNotFoundRoute(Route notFoundRoute) {
        this.notFoundRoute = notFoundRoute;
    }

    public void setCsrfCookieName(String cookieName) {
        ((CheckCSRF) this.checkCSRF).setCookieName(cookieName);
        ((PrepareCSRF) this.prepareCSRF).setCookieName(cookieName);
    }

    public void setCsrfFormFieldName(String fieldName) {
        ((CheckCSRF) this.checkCSRF).setFormFieldName(fieldName);
    }

    public void setCsrfCookieAge(Integer csrfCookieAge) {
        ((PrepareCSRF) this.prepareCSRF).setMaxAge(csrfCookieAge);
    }

    public void setCsrfCookieSecure(Boolean csrfCookieSecure) {
        ((PrepareCSRF) this.prepareCSRF).setSecure(csrfCookieSecure);
    }

    public void setSignKey(SymmetricKey signKey) {
        ((CheckCSRF) this.checkCSRF).getDoubleSubmitCSRF().setPreferredSignKey(signKey);
        ((PrepareCSRF) this.prepareCSRF).getDoubleSubmitCSRF().setPreferredSignKey(signKey);
    }

    public void setRotationKeys(Map<String, SymmetricKey> rotationSignKeys) {
        ((CheckCSRF) this.checkCSRF).getDoubleSubmitCSRF().setRotationSignKeys(rotationSignKeys);
        ((PrepareCSRF) this.prepareCSRF).getDoubleSubmitCSRF().setRotationSignKeys(rotationSignKeys);
    }

}
