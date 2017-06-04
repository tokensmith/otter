package org.rootservices.otter.gateway.servlet.merger;


import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.gateway.servlet.translator.HttpServletRequestCookieTranslator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HttpServletResponseMerger {
    private HttpServletRequestCookieTranslator httpServletRequestCookieTranslator;

    public HttpServletResponseMerger(HttpServletRequestCookieTranslator httpServletRequestCookieTranslator) {
        this.httpServletRequestCookieTranslator = httpServletRequestCookieTranslator;
    }

    public HttpServletResponse merge(HttpServletResponse containerResponse, Cookie[] containerCookies, Response response) throws IOException {

        // headers
        for(Map.Entry<String, String> header: response.getHeaders().entrySet()) {
            containerResponse.setHeader(header.getKey(), header.getValue());
        }

        // cookies
        Map<String, Cookie> containerCookiesMap = deleteAndUpdateCookies(containerResponse, containerCookies, response);
        createCookies(containerResponse, containerCookiesMap, response.getCookies());

        // status code
        containerResponse.setStatus(response.getStatusCode().getCode());

        // structured data.. json
        if(response.getBody().isPresent()) {
            containerResponse.getWriter().write(response.getBody().get());
        }

        return containerResponse;
    }

    protected Map<String, Cookie> deleteAndUpdateCookies(HttpServletResponse containerResponse, Cookie[] containerCookies, Response response) {
        Map<String, Cookie> containerCookiesMap = new HashMap<>();

        if (containerCookies == null) {
            return containerCookiesMap;
        }

        for(Cookie containerCookie: containerCookies) {
            containerCookiesMap.put(containerCookie.getName(), containerCookie);

            // delete cookie
            org.rootservices.otter.controller.entity.Cookie otterCookie = response.getCookies().get(containerCookie.getValue());
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

    protected void createCookies(HttpServletResponse containerResponse,  Map<String, Cookie> containerCookiesMap, Map<String, org.rootservices.otter.controller.entity.Cookie> otterCookies) {
        for(Map.Entry<String, org.rootservices.otter.controller.entity.Cookie> otterCookie: otterCookies.entrySet()) {
            Cookie containerCookie = containerCookiesMap.get(otterCookie.getKey());
            if (containerCookie == null) {
                Cookie containerCookieToAdd =  httpServletRequestCookieTranslator.to.apply(otterCookie.getValue());
                containerResponse.addCookie(containerCookieToAdd);
            }
        }
    }
}
