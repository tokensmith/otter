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
import org.rootservices.otter.security.csrf.between.CheckCSRF;
import org.rootservices.otter.security.csrf.between.PrepareCSRF;

import javax.servlet.ServletException;
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
        this.checkCSRF = checkCSRF;
        this.prepareCSRF = prepareCSRF;
    }

    public void processRequest(HttpServletRequest containerRequest, HttpServletResponse containerResponse) {
        try {
            Request request = httpServletRequestTranslator.from(containerRequest);
            Response response = new ResponseBuilder()
                    .headers(new HashMap<>())
                    .cookies(request.getCookies())
                    .payload(Optional.empty())
                    .presenter(Optional.empty())
                    .template(Optional.empty())
                    .build();

            Optional<Response> resourceResponse =  engine.route(request, response);

            if (!resourceResponse.isPresent()) {
                resourceResponse = Optional.of(engine.executeResourceMethod(notFoundRoute, request, response));
            }

            httpServletResponseMerger.merge(containerResponse, containerRequest.getCookies(), resourceResponse.get());
            httpServletRequestMerger.merge(containerRequest, containerResponse, resourceResponse.get());

        } catch (IOException | ServletException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void get(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getGet().add(route);
    }

    public void getCsrfProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(prepareCSRF);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .build();

        engine.getDispatcher().getGet().add(route);
    }

    public void post(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getPost().add(route);
    }

    public void postCsrfProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(checkCSRF);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void put(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getPut().add(route);
    }

    public void patch(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getPatch().add(route);
    }

    public void delete(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getDelete().add(route);
    }

    public void connect(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getConnect().add(route);
    }

    public void options(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getOptions().add(route);
    }

    public void trace(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getTrace().add(route);
    }

    public void head(String path, Resource resource) {
        Route route = new RouteBuilder().path(path).resource(resource).build();
        engine.getDispatcher().getHead().add(route);
    }

    // configuration methods below.

    public void setNotFoundRoute(Route notFoundRoute) {
        this.notFoundRoute = notFoundRoute;
    }

    /**
     * Assigns the value of the CSRF cookie name to be used.
     *
     * @param cookieName name of the cookie
     */
    public void setCsrfCookieName(String cookieName) {
        ((CheckCSRF) this.checkCSRF).setCookieName(cookieName);
        ((PrepareCSRF) this.prepareCSRF).setCookieName(cookieName);
    }

    /**
     * Assigns the value of the CSRF form field name to be used.
     *
     * @param fieldName name of the form field
     */
    public void setCsrfFormFieldName(String fieldName) {
        ((CheckCSRF) this.checkCSRF).setFormFieldName(fieldName);
    }

    /**
     * Sets the max age a CSRF cookie should live for
     *
     * @param csrfCookieAge age of the cookie
     */
    public void setCsrfCookieAge(Integer csrfCookieAge) {
        ((PrepareCSRF) this.prepareCSRF).setMaxAge(csrfCookieAge);
    }

    /**
     * Sets wether if the CSRF cookie should be set with HTTPS or HTTP
     *
     * @param csrfCookieSecure true if HTTPS, false if HTTP
     */
    public void setCsrfCookieSecure(Boolean csrfCookieSecure) {
        ((PrepareCSRF) this.prepareCSRF).setSecure(csrfCookieSecure);
    }

    /**
     * Assigns the value of sign key to both,
     * checkCSRF's doubleSubmitCSRF and prepareCSRF's doubleSubmitCSRF.
     *
     * The doubleSubmitCSRF should be a singleton, so it probably does not
     * need to be assigned to both instances.
     *
     * It is assigned to both just in case it's not a singleton
     *
     * @param signKey the key to sign cookies with.
     */
    public void setSignKey(SymmetricKey signKey) {
        ((CheckCSRF) this.checkCSRF).getDoubleSubmitCSRF().setPreferredSignKey(signKey);
        ((PrepareCSRF) this.prepareCSRF).getDoubleSubmitCSRF().setPreferredSignKey(signKey);
    }

    /**
     * Assigns the value of rotation keys to both,
     * checkCSRF's doubleSubmitCSRF and prepareCSRF's doubleSubmitCSRF.
     *
     * The doubleSubmitCSRF should be a singleton, so it probably does not
     * need to be assigned to both instances.
     *
     * It is assigned to both just in case it's not a singleton
     *
     * @param rotationSignKeys the previous sign keys
     */
    public void setRotationKeys(Map<String, SymmetricKey> rotationSignKeys) {
        ((CheckCSRF) this.checkCSRF).getDoubleSubmitCSRF().setRotationSignKeys(rotationSignKeys);
        ((PrepareCSRF) this.prepareCSRF).getDoubleSubmitCSRF().setRotationSignKeys(rotationSignKeys);
    }

}
