package org.rootservices.otter.gateway.servlet;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.builder.ResponseBuilder;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.gateway.Gateway;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.entity.Between;
import org.rootservices.otter.router.exception.HaltException;
import org.rootservices.otter.security.session.Session;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


public class ServletGateway<T extends Session>  extends Gateway<T>  {
    protected static Logger logger = LogManager.getLogger(ServletGateway.class);

    private HttpServletRequestTranslator<T> httpServletRequestTranslator;
    private HttpServletRequestMerger httpServletRequestMerger;
    private HttpServletResponseMerger<T> httpServletResponseMerger;

    public ServletGateway(HttpServletRequestTranslator<T> httpServletRequestTranslator, HttpServletRequestMerger httpServletRequestMerger, HttpServletResponseMerger<T> httpServletResponseMerger, Engine<T> engine, Between<T> prepareCSRF, Between<T> checkCSRF) {
        super(engine, prepareCSRF, checkCSRF);
        this.httpServletRequestTranslator = httpServletRequestTranslator;
        this.httpServletRequestMerger = httpServletRequestMerger;
        this.httpServletResponseMerger = httpServletResponseMerger;
    }

    public GatewayResponse processRequest(HttpServletRequest containerRequest, HttpServletResponse containerResponse, byte[] body) {
        GatewayResponse gatewayResponse = new GatewayResponse();
        try {
            Request<T> request = httpServletRequestTranslator.from(containerRequest, body);

            Response<T> response = new ResponseBuilder<T>()
                    .headers(new HashMap<>())
                    .cookies(request.getCookies())
                    .payload(Optional.empty())
                    .presenter(Optional.empty())
                    .template(Optional.empty())
                    .build();

            Boolean shouldHalt = false;
            Optional<Response<T>> resourceResponse;
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
