package org.rootservices.otter.gateway.servlet;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.CookieConfig;
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
import org.rootservices.otter.security.session.between.DecryptSession;
import org.rootservices.otter.security.session.between.EncryptSession;


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
    private EncryptSession encryptSession;
    private DecryptSession decryptSession;
    private Route notFoundRoute;

    public ServletGateway(HttpServletRequestTranslator httpServletRequestTranslator, HttpServletRequestMerger httpServletRequestMerger, HttpServletResponseMerger httpServletResponseMerger, Engine engine, Between prepareCSRF, Between checkCSRF, EncryptSession encryptSession) {
        this.httpServletRequestTranslator = httpServletRequestTranslator;
        this.httpServletRequestMerger = httpServletRequestMerger;
        this.httpServletResponseMerger = httpServletResponseMerger;
        this.engine = engine;
        this.prepareCSRF = prepareCSRF;
        this.checkCSRF = checkCSRF;
        this.encryptSession = encryptSession;
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

    public void getCsrfAndSessionProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(prepareCSRF);

        List<Between> after = new ArrayList<>();
        after.add(encryptSession);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getGet().add(route);
    }

    public void getSessionProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(decryptSession);

        List<Between> after = new ArrayList<>();
        after.add(encryptSession);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(after)
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

    public void postCsrfAndSessionProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(checkCSRF);
        before.add(decryptSession);

        List<Between> after = new ArrayList<>();
        after.add(encryptSession);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(after)
                .build();

        engine.getDispatcher().getPost().add(route);
    }

    public void postSessionProtect(String path, Resource resource) {
        List<Between> before = new ArrayList<>();
        before.add(decryptSession);

        List<Between> after = new ArrayList<>();
        after.add(encryptSession);

        Route route = new RouteBuilder()
                .path(path)
                .resource(resource)
                .before(before)
                .after(after)
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

    public void getRoute(Route route) {
        engine.getDispatcher().getGet().add(route);
    }

    public void postRoute(Route route) {
        engine.getDispatcher().getPost().add(route);
    }

    public void putRoute(Route route) {
        engine.getDispatcher().getPut().add(route);
    }

    public void patchRoute(Route route) {
        engine.getDispatcher().getPatch().add(route);
    }

    public void deleteRoute(Route route) {
        engine.getDispatcher().getDelete().add(route);
    }

    public void connectRoute(Route route) {
        engine.getDispatcher().getConnect().add(route);
    }

    public void optionsRoute(Route route) {
        engine.getDispatcher().getOptions().add(route);
    }

    public void traceRoute(Route route) {
        engine.getDispatcher().getTrace().add(route);
    }

    public void headRoute(Route route) {
        engine.getDispatcher().getHead().add(route);
    }

    // configuration methods below.
    public void setNotFoundRoute(Route notFoundRoute) {
        this.notFoundRoute = notFoundRoute;
    }

    public void setCsrfCookieConfig(CookieConfig csrfCookieConfig) {
        ((CheckCSRF) this.checkCSRF).setCookieName(csrfCookieConfig.getName());
        ((PrepareCSRF) this.prepareCSRF).setCookieConfig(csrfCookieConfig);
    }

    public void setCsrfFormFieldName(String fieldName) {
        ((CheckCSRF) this.checkCSRF).setFormFieldName(fieldName);
    }

    public void setSignKey(SymmetricKey signKey) {
        ((CheckCSRF) this.checkCSRF).getDoubleSubmitCSRF().setPreferredSignKey(signKey);
        ((PrepareCSRF) this.prepareCSRF).getDoubleSubmitCSRF().setPreferredSignKey(signKey);
    }

    public void setRotationSignKeys(Map<String, SymmetricKey> rotationSignKeys) {
        ((CheckCSRF) this.checkCSRF).getDoubleSubmitCSRF().setRotationSignKeys(rotationSignKeys);
        ((PrepareCSRF) this.prepareCSRF).getDoubleSubmitCSRF().setRotationSignKeys(rotationSignKeys);
    }

    public void setSessionCookieConfig(CookieConfig sessionCookieConfig) {
        this.encryptSession.setCookieConfig(sessionCookieConfig);
    }

    public void setEncKey(SymmetricKey encKey) {
        this.encryptSession.setPreferredKey(encKey);
    }

    public DecryptSession getDecryptSession() {
        return decryptSession;
    }

    public void setDecryptSession(DecryptSession decryptSession) {
        this.decryptSession = decryptSession;
    }
}
