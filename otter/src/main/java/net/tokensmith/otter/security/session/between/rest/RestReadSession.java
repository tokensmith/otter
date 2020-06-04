package net.tokensmith.otter.security.session.between.rest;

import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.dispatch.entity.RestBtwnRequest;
import net.tokensmith.otter.dispatch.entity.RestBtwnResponse;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.between.RestBetween;
import net.tokensmith.otter.router.exception.HaltException;
import net.tokensmith.otter.security.session.exception.InvalidSessionException;
import net.tokensmith.otter.security.session.exception.SessionDecryptException;
import net.tokensmith.otter.security.session.util.Decrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.BiFunction;


/**
 * A RestBetween that will read and decrypt a session that can be used to assign a user to the request.
 * This is useful for requests to an API from a browser.
 *
 * @param <S> Session to decrypt
 * @param <U> The User to pass along to the RestResource
 */
public class RestReadSession<S, U> implements RestBetween<S, U> {
    protected static Logger LOGGER = LoggerFactory.getLogger(RestReadSession.class);
    public static final String COOKIE_NOT_PRESENT = "session cookie not present.";
    public static final String INVALID_SESSION_COOKIE = "Invalid value for the session cookie";

    private String sessionCookieName;
    private Boolean required;
    private BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse> onHalt;
    private Decrypt<S> decrypt;

    public RestReadSession(String sessionCookieName, Boolean required, Decrypt<S> decrypt, BiFunction<RestBtwnResponse, HaltException, RestBtwnResponse> onHalt) {
        this.sessionCookieName = sessionCookieName;
        this.required = required;
        this.decrypt = decrypt;
        this.onHalt = onHalt;
    }

    @Override
    public void process(Method method, RestBtwnRequest<S, U> request, RestBtwnResponse response) throws HaltException {
        Optional<S> session = readSession(request, response);
        request.setSession(session);
    }

    protected Optional<S> readSession(RestBtwnRequest<S, U> request, RestBtwnResponse response) throws HaltException {
        Optional<S> session = Optional.empty();
        Cookie sessionCookie = request.getCookies().get(sessionCookieName);

        if (sessionCookie == null && required) {
            HaltException halt = new HaltException(COOKIE_NOT_PRESENT);
            onHalt(halt, response);
            throw halt;
        } else if (sessionCookie == null && !required) {
            // ok to proceed to resource. The session is not required.
            return session;
        }

        try {
            session = Optional.of(decrypt.decrypt(sessionCookie.getValue()));
        } catch (InvalidSessionException e) {
            LOGGER.error(e.getMessage(), e);
            HaltException halt = new HaltException(INVALID_SESSION_COOKIE, e);
            onHalt(halt, response);
            throw halt;
        } catch (SessionDecryptException e) {
            LOGGER.error(e.getMessage(), e);
            HaltException halt = new HaltException(INVALID_SESSION_COOKIE, e);
            onHalt(halt, response);
            throw halt;
        }

        return session;
    }

    protected void onHalt(HaltException e, RestBtwnResponse response) {
        onHalt.apply(response, e);
    }

    public Boolean getRequired() {
        return required;
    }

    public String getSessionCookieName() {
        return sessionCookieName;
    }
}
