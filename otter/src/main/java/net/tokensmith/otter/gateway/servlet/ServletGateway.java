package net.tokensmith.otter.gateway.servlet;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.tokensmith.otter.controller.entity.DefaultSession;
import net.tokensmith.otter.controller.entity.DefaultUser;
import net.tokensmith.otter.gateway.Gateway;
import net.tokensmith.otter.gateway.servlet.merger.HttpServletRequestMerger;
import net.tokensmith.otter.gateway.servlet.merger.HttpServletResponseMerger;
import net.tokensmith.otter.gateway.servlet.translator.HttpServletRequestTranslator;
import net.tokensmith.otter.gateway.translator.LocationTranslator;
import net.tokensmith.otter.gateway.translator.RestLocationTranslator;
import net.tokensmith.otter.router.Engine;
import net.tokensmith.otter.router.builder.AnswerBuilder;
import net.tokensmith.otter.router.entity.io.Answer;
import net.tokensmith.otter.router.entity.io.Ask;
import net.tokensmith.otter.router.exception.HaltException;



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
    protected static Logger LOGGER = LoggerFactory.getLogger(ServletGateway.class);

    private HttpServletRequestTranslator httpServletRequestTranslator;
    private HttpServletRequestMerger httpServletRequestMerger;
    private HttpServletResponseMerger httpServletResponseMerger;
    private Integer writeChunkSize;

    public ServletGateway(HttpServletRequestTranslator httpServletRequestTranslator, HttpServletRequestMerger httpServletRequestMerger, HttpServletResponseMerger httpServletResponseMerger, Engine engine, Map<String, LocationTranslator<? extends DefaultSession, ? extends DefaultUser>> locationTranslators, Map<String, RestLocationTranslator<? extends DefaultUser, ?>> restLocationTranslators, Integer writeChunkSize) {
        super(engine, locationTranslators, restLocationTranslators);
        this.httpServletRequestTranslator = httpServletRequestTranslator;
        this.httpServletRequestMerger = httpServletRequestMerger;
        this.httpServletResponseMerger = httpServletResponseMerger;
        this.writeChunkSize = writeChunkSize;
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
                LOGGER.debug(e.getMessage(), e);
                resourceAnswer = answer;
            }

            httpServletResponseMerger.merge(containerResponse, containerRequest.getCookies(), resourceAnswer);
            httpServletRequestMerger.merge(containerRequest, resourceAnswer);

            if (resourceAnswer.getPayload().isPresent()) {
                gatewayResponse.setPayload(Optional.of(resourceAnswer.getPayload().get()));
            } else {
                gatewayResponse.setPayload(Optional.empty());
            }
            gatewayResponse.setWriteChunkSize(writeChunkSize);
            gatewayResponse.setTemplate(resourceAnswer.getTemplate());

        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            containerResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            containerResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return gatewayResponse;
    }
}
