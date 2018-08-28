package org.rootservices.otter.gateway.servlet;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.builder.ResponseBuilder;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.gateway.Gateway;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.router.exception.MediaTypeException;
import org.rootservices.otter.router.exception.NotFoundException;
import org.rootservices.otter.security.session.Session;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Servlet API Gateway that translates Servlet API objects to Otter objects
 * and dispatches requests to Otter resources. No Servlet API objects can go past
 * this gateway.
 *
 * @param <S> Session implementation for application
 * @param <U> User object, intended to be a authenticated user.
 */
public class ServletGateway<S extends Session, U> extends Gateway<S, U>  {
    protected static Logger logger = LogManager.getLogger(ServletGateway.class);

    private HttpServletRequestTranslator<S, U> httpServletRequestTranslator;
    private HttpServletRequestMerger httpServletRequestMerger;
    private HttpServletResponseMerger<S> httpServletResponseMerger;

    public ServletGateway(HttpServletRequestTranslator<S, U> httpServletRequestTranslator, HttpServletRequestMerger httpServletRequestMerger, HttpServletResponseMerger<S> httpServletResponseMerger, Engine<S, U> engine, Between<S, U> prepareCSRF, Between<S, U> checkCSRF) {
        super(engine, prepareCSRF, checkCSRF);
        this.httpServletRequestTranslator = httpServletRequestTranslator;
        this.httpServletRequestMerger = httpServletRequestMerger;
        this.httpServletResponseMerger = httpServletResponseMerger;
    }

    public GatewayResponse processRequest(HttpServletRequest containerRequest, HttpServletResponse containerResponse, byte[] body) {
        GatewayResponse gatewayResponse = new GatewayResponse();
        try {
            Request<S, U> request = httpServletRequestTranslator.from(containerRequest, body);

            Response<S> response = new ResponseBuilder<S>()
                    .headers(new HashMap<>())
                    .cookies(request.getCookies())
                    .payload(Optional.empty())
                    .presenter(Optional.empty())
                    .template(Optional.empty())
                    .build();

            Response<S> resourceResponse;
            try {
                resourceResponse = engine.route(request, response);
            } catch (HaltException e) {
                logger.debug(e.getMessage(), e);
                resourceResponse = response;
            } catch (NotFoundException e) {
                Route<S, U> notFoundRoute = errorRoutes.get(StatusCode.NOT_FOUND);
                resourceResponse = engine.executeResourceMethod(notFoundRoute, request, response);
            } catch (MediaTypeException e) {
                Route<S, U> mediaTypeRoute = errorRoutes.get(StatusCode.UNSUPPORTED_MEDIA_TYPE);
                resourceResponse = engine.executeResourceMethod(mediaTypeRoute, request, response);
            }

            httpServletResponseMerger.merge(containerResponse, containerRequest.getCookies(), resourceResponse);
            httpServletRequestMerger.merge(containerRequest, resourceResponse);

            if (resourceResponse.getPayload().isPresent()) {
                gatewayResponse.setPayload(Optional.of(resourceResponse.getPayload().get().toByteArray()));
            } else {
                gatewayResponse.setPayload(Optional.empty());
            }
            gatewayResponse.setTemplate(response.getTemplate());

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            containerResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            containerResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return gatewayResponse;
    }
}
