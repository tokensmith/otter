package org.rootservices.otter.controller;

import org.rootservices.otter.QueryStringToMap;
import org.rootservices.otter.authentication.HttpBasicEntity;
import org.rootservices.otter.authentication.ParseBearer;
import org.rootservices.otter.authentication.ParseHttpBasic;
import org.rootservices.otter.authentication.exception.BearerException;
import org.rootservices.otter.authentication.exception.HttpBasicException;
import org.rootservices.otter.config.AppFactory;
import org.rootservices.otter.controller.header.AuthScheme;
import org.rootservices.otter.controller.header.Header;
import org.rootservices.otter.translator.JsonTranslator;
import org.rootservices.otter.translator.exception.*;
import org.rootservices.otter.controller.entity.Error;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * Assists in instantiating an entity for POST and PUT requests.
 * Handles returning errors when the entity cannot be instantiated.
 *
 * @param <T> The type of entity to expect in POST and PUT requests
 */
public abstract class RestController<T> extends HttpServlet {
    protected static Logger logger = LogManager.getLogger(RestController.class);
    protected AppFactory factory;
    protected JsonTranslator<T> translator;
    protected QueryStringToMap queryStringToMap;
    protected ParseBearer parseBearer;
    protected ParseHttpBasic parseHttpBasic;
    protected Class<T> type;

    private static final String DUPLICATE_KEY_MSG = "Duplicate Key";
    private static final String INVALID_VALUE_MSG = "Invalid Value";
    private static final String UNKNOWN_KEY_MSG = "Unknown Key";
    private static final String INVALID_PAYLOAD_MSG = "Invalid Payload";

    private static final String DUPLICATE_KEY_DESC = "%s was repeated";
    private static final String INVALID_VALUE_DESC = "%s was invalid";
    private static final String UNKNOWN_KEY_DESC = "%s was not expected";

    @Override
    public void init() throws ServletException {

        factory = new AppFactory();
        this.translator = new JsonTranslator<>(factory.objectMapper());
        this.queryStringToMap = new QueryStringToMap();
        this.parseBearer = new ParseBearer();
        this.parseHttpBasic = new ParseHttpBasic();

        if(this.type == null) {
            this.type = (Class<T>) ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
        }
    }

    /**
     * Instantiates T.
     *
     * @param req the HttpServletRequest
     * @param resp the HttpServletResponse
     * @return null if it was unable to instantiate T or an instance of T
     * @throws IOException if something happened while writing the error payload.
     */
    public T makeEntity(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        T entity;
        try{
            entity = translator.from(req.getReader(), type);
        } catch (DuplicateKeyException e) {
            logger.debug(e.getMessage(), e);
            String desc = String.format(DUPLICATE_KEY_DESC, e.getKey());
            Error error = new Error(DUPLICATE_KEY_MSG, desc);
            prepareResponseWithBody(resp, error, HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } catch (InvalidValueException e) {
            logger.debug(e.getMessage(), e);
            String desc = String.format(INVALID_VALUE_DESC, e.getKey());
            Error error = new Error(INVALID_VALUE_MSG, desc);
            prepareResponseWithBody(resp, error, HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } catch (UnknownKeyException e) {
            logger.debug(e.getMessage(), e);
            String desc = String.format(UNKNOWN_KEY_DESC, e.getKey());
            Error error = new Error(UNKNOWN_KEY_MSG, desc);
            prepareResponseWithBody(resp, error, HttpServletResponse.SC_BAD_REQUEST);
            return null;
        } catch (InvalidPayloadException e) {
            logger.debug(e.getMessage(), e);
            Error error = new Error(INVALID_PAYLOAD_MSG, null);
            prepareResponseWithBody(resp, error, HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return entity;

    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        T entity = makeEntity(req, resp);
        if (entity == null) {
            return;
        }
        doPost(req, resp, entity);
        return;
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        T entity = makeEntity(req, resp);
        if (entity == null) {
            return;
        }
        doPut(req, resp, entity);
        return;
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp, T entity) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp, T entity) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    /**
     * Parses a Bearer authorization header value to just it's token.
     *
     * If its not Bearer then resp becomes:
     *  - status code 401
     *  - 'WWW-Authenticate': 'Bearer'
     *
     * And it will return null
     *
     * @param resp the HttpServletResponse
     * @param authorization the authorization header value
     * @return the token component of a Bearer authorization header
     */
    protected String getBearerToken(HttpServletResponse resp, String authorization) {
        String accessToken;
        try {
            accessToken = parseBearer.parse(authorization);
        } catch (BearerException e) {
            preparedUnAuthorizedResponse(resp, AuthScheme.BEARER.getScheme());
            return null;
        }
        return accessToken;
    }

    /**
     * Parses a Basic authorization header value.
     *
     * If its not Basic then resp becomes:
     *  - status code 401
     *  - 'WWW-Authenticate': 'Basic'
     *
     * And it will return null
     *
     * @param resp the HttpServletResponse
     * @param authorization the authorization header value
     * @return HttpBasicEntity instance with its decoded basic values
     */
    protected HttpBasicEntity getBasicAuth(HttpServletResponse resp, String authorization) {
        HttpBasicEntity httpBasic;
        try {
            httpBasic = parseHttpBasic.run(authorization);
        } catch (HttpBasicException e) {
            preparedUnAuthorizedResponse(resp, AuthScheme.BASIC.getScheme());
            return null;
        }
        return httpBasic;
    }

    protected void preparedUnAuthorizedResponse(HttpServletResponse resp, String authenticateHeaderValue) {
        setDefaultResponseHeaders(resp);
        resp.setHeader(Header.AUTH_MISSING.getValue(), authenticateHeaderValue);
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    protected abstract void setDefaultResponseHeaders(HttpServletResponse resp);

    /**
     * Prepares a HttpServletResponse
     *  - writes the object to the response body
     *  - assigns the status code
     *  - calls setDefaultResponseHeaders for customization of setting headers
     *
     * @param resp HttpServletResponse
     * @param object the entity to marshal to json
     * @param statusCode the http status code
     * @throws IOException if something happened while writing the error payload.
     */
    protected void prepareResponseWithBody(HttpServletResponse resp, Object object, int statusCode) throws IOException {
        setDefaultResponseHeaders(resp);
        resp.setStatus(statusCode);
        String json = null;
        try {
            json = translator.to(object);
        } catch (ToJsonException e) {
            logger.error(e.getMessage(), e);
        }
        resp.getWriter().write(json);
    }
}
