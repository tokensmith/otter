package net.tokensmith.otter.gateway.servlet.translator;


import net.tokensmith.otter.controller.entity.Cookie;

import java.util.Objects;
import java.util.function.Function;

public class HttpServletRequestCookieTranslator {

    public Cookie from(javax.servlet.http.Cookie containerCookie) {
        return new Cookie.Builder()
            .name(containerCookie.getName())
            .value(containerCookie.getValue())
            .domain(containerCookie.getDomain())
            .maxAge(containerCookie.getMaxAge())
            .path(containerCookie.getPath())
            .version(containerCookie.getVersion())
            .secure(containerCookie.getSecure())
            .httpOnly(containerCookie.isHttpOnly())
            .build();
    }

    public javax.servlet.http.Cookie to(Cookie otterCookie) {
        javax.servlet.http.Cookie containerCookie = new javax.servlet.http.Cookie(otterCookie.getName(), otterCookie.getValue());
        containerCookie.setComment(otterCookie.getComment());

        if(Objects.nonNull(otterCookie.getDomain())) {
            containerCookie.setDomain(otterCookie.getDomain());
        }

        containerCookie.setMaxAge(otterCookie.getMaxAge());
        containerCookie.setPath(otterCookie.getPath());
        containerCookie.setSecure(otterCookie.isSecure());
        containerCookie.setVersion(otterCookie.getVersion());
        containerCookie.setHttpOnly(otterCookie.isHttpOnly());
        return containerCookie;
    }

}
