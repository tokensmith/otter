package net.tokensmith.otter.gateway.servlet.merger;



import net.tokensmith.otter.gateway.servlet.translator.HttpServletRequestCookieTranslator;
import net.tokensmith.otter.router.entity.io.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class HttpServletResponseMerger {
    protected static Logger LOGGER = LoggerFactory.getLogger(HttpServletResponseMerger.class);
    private HttpServletRequestCookieTranslator httpServletRequestCookieTranslator;

    public HttpServletResponseMerger(HttpServletRequestCookieTranslator httpServletRequestCookieTranslator) {
        this.httpServletRequestCookieTranslator = httpServletRequestCookieTranslator;
    }

    public HttpServletResponse merge(HttpServletResponse response, Cookie[] requestCookies, Answer answer) {

        // headers
        for(Map.Entry<String, String> header: answer.getHeaders().entrySet()) {
            response.setHeader(header.getKey(), header.getValue());
        }

        // cookies
        Map<String, Cookie> containerCookiesMap = deleteAndUpdateCookies(response, requestCookies, answer);
        createCookies(response, containerCookiesMap, answer.getCookies());

        // status code
        response.setStatus(answer.getStatusCode().getCode());

        return response;
    }

    protected Map<String, Cookie> deleteAndUpdateCookies(HttpServletResponse response, Cookie[] requestCookies, Answer answer) {
        Map<String, Cookie> containerCookiesMap = new HashMap<>();

        // no request cookies then they will all be added instead up deleted and updated.
        if (requestCookies == null) {
            return containerCookiesMap;
        }

        // request cookies may have duplicates.. purge them
        Map<Integer, Cookie> purgedCookies = new HashMap<>();
        for(Cookie requestCookie: requestCookies) {
            Integer hashCode = hashCodeForCookie(requestCookie);
            purgedCookies.putIfAbsent(hashCode, requestCookie);
        }

        for(Map.Entry<Integer, Cookie> requestCookieEntry: purgedCookies.entrySet()) {
            Cookie requestCookie = requestCookieEntry.getValue();
            containerCookiesMap.put(requestCookie.getName(), requestCookie);

            // delete cookie
            net.tokensmith.otter.controller.entity.Cookie otterCookie = answer.getCookies().get(requestCookie.getName());
            if(otterCookie == null) {
                containerCookiesMap.remove(requestCookie.getName());

                // delete the cookie.
                requestCookie.setMaxAge(0);
                requestCookie.setValue("");
                requestCookie.setComment("Removing cookie " + Instant.now().getEpochSecond());
                response.addCookie(requestCookie);

                logCookie("Removing cookie from container request", requestCookie);
            } else {
                // is otter cookie different than container cookie?
                Cookie toUpdate =  httpServletRequestCookieTranslator.to.apply(otterCookie);
                toUpdate.setComment("updated at " + Instant.now().getEpochSecond());
                response.addCookie(toUpdate);

                containerCookiesMap.put(toUpdate.getName(), toUpdate);

                // does it need to be deleted as well like above?
                logCookie("Updating cookie in container request", toUpdate);

            }
        }
        return containerCookiesMap;
    }

    protected boolean cookieEquals(Cookie a, Cookie b) {
        if (a == b) return true;
        if (b == null || a.getClass() != b.getClass()) return false;
        return a.getMaxAge() == b.getMaxAge() &&
                a.getSecure() == b.getSecure() &&
                a.getVersion() == b.getVersion() &&
                a.isHttpOnly() == b.isHttpOnly() &&
                Objects.equals(a.getName(), b.getName()) &&
                Objects.equals(a.getValue(), b.getValue()) &&
                Objects.equals(a.getComment(), b.getComment()) &&
                Objects.equals(a.getDomain(), b.getDomain()) &&
                Objects.equals(a.getPath(), b.getPath());
    }

    protected int hashCodeForCookie(Cookie cookie) {
        return Objects.hash(
                cookie.getComment(),
                cookie.getValue(),
                cookie.getComment(),
                cookie.getDomain(),
                cookie.getMaxAge(),
                cookie.getPath(),
                cookie.getSecure(),
                cookie.getVersion(),
                cookie.isHttpOnly()
        );
    }

    // TODO: this method is not under unit test.
    protected void createCookies(HttpServletResponse response,  Map<String, Cookie> containerCookiesMap, Map<String, net.tokensmith.otter.controller.entity.Cookie> otterCookies) {
        for(Map.Entry<String, net.tokensmith.otter.controller.entity.Cookie> otterCookie: otterCookies.entrySet()) {
            Cookie containerCookie = containerCookiesMap.get(otterCookie.getKey());
            if (containerCookie == null) {
                Cookie toAdd =  httpServletRequestCookieTranslator.to.apply(otterCookie.getValue());
                logCookie( "Adding cookie to container response", toAdd);
                response.addCookie(toAdd);
            }
        }
    }

    protected void logCookie(String msg, Cookie cookie) {
        LOGGER.debug(
            "{} -> name: {}, value: {},  comment: {}, domain: {}, maxAge: {}, path: {}, secure: {}, version: {}, httpOnly: {}",
            msg, cookie.getName(), cookie.getValue(), cookie.getComment(), cookie.getDomain(), cookie.getMaxAge(), cookie.getPath(), cookie.getSecure(), cookie.getVersion(), cookie.isHttpOnly()
        );
    }
}
