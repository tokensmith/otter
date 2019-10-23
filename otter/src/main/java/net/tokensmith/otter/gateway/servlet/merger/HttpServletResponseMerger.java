package net.tokensmith.otter.gateway.servlet.merger;



import net.tokensmith.otter.gateway.servlet.translator.HttpServletRequestCookieTranslator;
import net.tokensmith.otter.router.entity.io.Answer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


public class HttpServletResponseMerger {
    private HttpServletRequestCookieTranslator httpServletRequestCookieTranslator;

    public HttpServletResponseMerger(HttpServletRequestCookieTranslator httpServletRequestCookieTranslator) {
        this.httpServletRequestCookieTranslator = httpServletRequestCookieTranslator;
    }

    public HttpServletResponse merge(HttpServletResponse containerResponse, Cookie[] containerCookies, Answer answer) {

        // headers
        for(Map.Entry<String, String> header: answer.getHeaders().entrySet()) {
            containerResponse.setHeader(header.getKey(), header.getValue());
        }

        // cookies
        Map<String, Cookie> containerCookiesMap = deleteAndUpdateCookies(containerResponse, containerCookies, answer);
        createCookies(containerResponse, containerCookiesMap, answer.getCookies());

        // status code
        containerResponse.setStatus(answer.getStatusCode().getCode());

        return containerResponse;
    }

    protected Map<String, Cookie> deleteAndUpdateCookies(HttpServletResponse containerResponse, Cookie[] containerCookies, Answer answer) {
        Map<String, Cookie> containerCookiesMap = new HashMap<>();

        if (containerCookies == null) {
            return containerCookiesMap;
        }

        for(Cookie containerCookie: containerCookies) {
            containerCookiesMap.put(containerCookie.getName(), containerCookie);

            // delete cookie
            net.tokensmith.otter.controller.entity.Cookie otterCookie = answer.getCookies().get(containerCookie.getName());
            if(otterCookie == null) {
                containerCookie.setMaxAge(0);
                containerResponse.addCookie(containerCookie);
            } else {
                // update an existing cookie
                containerCookie = httpServletRequestCookieTranslator.to.apply(otterCookie);
                containerResponse.addCookie(containerCookie);
            }
        }
        return containerCookiesMap;
    }

    // TODO: this method is not under unit test.
    protected void createCookies(HttpServletResponse containerResponse,  Map<String, Cookie> containerCookiesMap, Map<String, net.tokensmith.otter.controller.entity.Cookie> otterCookies) {
        for(Map.Entry<String, net.tokensmith.otter.controller.entity.Cookie> otterCookie: otterCookies.entrySet()) {
            Cookie containerCookie = containerCookiesMap.get(otterCookie.getKey());
            if (containerCookie == null) {
                Cookie containerCookieToAdd =  httpServletRequestCookieTranslator.to.apply(otterCookie.getValue());
                containerResponse.addCookie(containerCookieToAdd);
            }
        }
    }
}
