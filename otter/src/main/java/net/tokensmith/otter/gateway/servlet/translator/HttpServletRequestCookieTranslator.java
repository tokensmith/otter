package net.tokensmith.otter.gateway.servlet.translator;


import net.tokensmith.otter.controller.entity.Cookie;

import java.util.Objects;

public class HttpServletRequestCookieTranslator {

    public Cookie from(jakarta.servlet.http.Cookie containerCookie) {
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

    public jakarta.servlet.http.Cookie to(Cookie otterCookie) {
        jakarta.servlet.http.Cookie containerCookie = new jakarta.servlet.http.Cookie(otterCookie.getName(), otterCookie.getValue());
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
