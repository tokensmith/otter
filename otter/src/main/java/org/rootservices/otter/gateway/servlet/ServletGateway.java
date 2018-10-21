package org.rootservices.otter.gateway.servlet;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.gateway.Gateway;
import org.rootservices.otter.gateway.LocationTranslatorFactory;
import org.rootservices.otter.gateway.servlet.merger.HttpServletRequestMerger;
import org.rootservices.otter.gateway.servlet.merger.HttpServletResponseMerger;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import org.rootservices.otter.gateway.translator.LocationTranslator;
import org.rootservices.otter.router.Engine;
import org.rootservices.otter.router.builder.AnswerBuilder;
import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Servlet API Gateway that translates Servlet API objects to Otter objects
 * and dispatches requests to Otter resources. No Servlet API objects can go past
 * this gateway.
 *
 */
public class ServletGateway extends Gateway {
    protected static Logger logger = LogManager.getLogger(ServletGateway.class);

    private HttpServletRequestTranslator httpServletRequestTranslator;
    private HttpServletRequestMerger httpServletRequestMerger;
    private HttpServletResponseMerger httpServletResponseMerger;

    public ServletGateway(HttpServletRequestTranslator httpServletRequestTranslator, HttpServletRequestMerger httpServletRequestMerger, HttpServletResponseMerger httpServletResponseMerger, Engine engine, Map<String, LocationTranslator<? extends DefaultSession, ? extends DefaultUser>> locationTranslators) {
        super(engine, locationTranslators);
        this.httpServletRequestTranslator = httpServletRequestTranslator;
        this.httpServletRequestMerger = httpServletRequestMerger;
        this.httpServletResponseMerger = httpServletResponseMerger;
    }

    public GatewayResponse processRequest(HttpServletRequest containerRequest, HttpServletResponse containerResponse, byte[] body) {
        GatewayResponse gatewayResponse = new GatewayResponse();
        try {
            Ask ask = httpServletRequestTranslator.from(containerRequest, body);

            Answer answer = new AnswerBuilder()
                    .headers(new HashMap<>())
                    .cookies(ask.getCookies())
                    .payload(Optional.empty())
                    .presenter(Optional.empty())
                    .template(Optional.empty())
                    .build();

            Answer resourceAnswer;
            try {
                resourceAnswer = engine.route(ask, answer);
            } catch (HaltException e) {
                logger.debug(e.getMessage(), e);
                resourceAnswer = answer;
            }

            httpServletResponseMerger.merge(containerResponse, containerRequest.getCookies(), resourceAnswer);
            httpServletRequestMerger.merge(containerRequest, resourceAnswer);

            if (resourceAnswer.getPayload().isPresent()) {
                gatewayResponse.setPayload(Optional.of(resourceAnswer.getPayload().get().toByteArray()));
            } else {
                gatewayResponse.setPayload(Optional.empty());
            }
            gatewayResponse.setTemplate(resourceAnswer.getTemplate());

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
