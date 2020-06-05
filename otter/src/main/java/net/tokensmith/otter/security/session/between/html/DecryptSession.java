package net.tokensmith.otter.security.session.between.html;


import net.tokensmith.otter.security.session.util.Decrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.request.Request;
import net.tokensmith.otter.controller.entity.response.Response;
import net.tokensmith.otter.controller.entity.StatusCode;
import net.tokensmith.otter.security.session.exception.InvalidSessionException;
import net.tokensmith.otter.security.exception.SessionCtorException;
import net.tokensmith.otter.security.session.exception.SessionDecryptException;
import net.tokensmith.otter.router.entity.between.Between;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.exception.HaltException;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;


/**
 * A Between that will encrypt a session.
 *
 * @param <S> Session object, intended to contain user session data.
 * @param <U> User object, intended to be a authenticated user.
 */
public class DecryptSession<S, U> implements Between<S, U> {
    public static final String INVALID_SESSION_COOKIE = "Invalid value for the session cookie";
    public static final String COOKIE_NOT_PRESENT = "session cookie not present.";
    public static final String FAILED_TO_COPY_REQUEST_SESSION = "failed to copy request session";
    public static final String COULD_NOT_CALL_THE_SESSION_COPY_CONSTRUCTOR = "Could not call the session's copy constructor";
    protected static Logger LOGGER = LoggerFactory.getLogger(DecryptSession.class);

    private Constructor<S> ctor;
    private String sessionCookieName;
    private Boolean required;
    private BiFunction<Response<S>, HaltException, Response<S>> onHalt;
    private Decrypt<S> decrypt;

    public DecryptSession(Constructor<S> ctor, String sessionCookieName, BiFunction<Response<S>, HaltException, Response<S>> onHalt, Boolean required, Decrypt<S> decrypt) {
        this.ctor = ctor;
        this.sessionCookieName = sessionCookieName;
        this.onHalt = onHalt;
        this.required = required;
        this.decrypt = decrypt;
    }

    @Override
    public void process(Method method, Request<S, U> request, Response<S> response) throws HaltException {
        Optional<S> session;
        Cookie sessionCookie = request.getCookies().get(sessionCookieName);

        if (Objects.isNull(sessionCookie) && required) {
            HaltException halt = new HaltException(COOKIE_NOT_PRESENT);
            onHalt(halt, response);
            throw halt;
        } else if (Objects.isNull(sessionCookie) && !required) {
            // ok to proceed to resource. The session is not required.
            request.setSession(Optional.empty());
            return;
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

        // Copies the request session and assigns it to, response.
        // This is required because the after between, EncryptSession, does an .equals() to
        // determine if the session has changed. If it changed then it will be re encrypted.
        request.setSession(session);
        S responseSession;

        try {
            responseSession = copy(session.get());
        } catch (SessionCtorException e) {
            LOGGER.error(e.getMessage(), e);
            HaltException halt = new HaltException(FAILED_TO_COPY_REQUEST_SESSION, e);
            onHalt(halt, response);
            throw halt;
        }
        response.setSession(Optional.of(responseSession));
    }

    /**
     * Copies S and then returns the copy.
     *
     * @param from the session to copy
     * @return an instance of T that is a copy of session
     * @throws SessionCtorException when ctor could not executed
     */
    protected S copy(S from) throws SessionCtorException {
        S copy;
        try {
            copy = ctor.newInstance(from);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new SessionCtorException(COULD_NOT_CALL_THE_SESSION_COPY_CONSTRUCTOR,e);
        }
        return copy;
    }

    /**
     * This method will be called before a Halt Exception is thrown.
     * Override this method if you wish to change the behavior on the
     * response right before a Halt Exception is going to be thrown.
     * An Example would be, you may want to redirect the user to a login page.
     *
     * @param e a HaltException
     * @param response a Response
     */
    protected void onHalt(HaltException e, Response<S> response) {
        response = onHalt.apply(response, e);
    }

    public Boolean getRequired() {
        return required;
    }

    public String getSessionCookieName() {
        return sessionCookieName;
    }
}
