package net.tokensmith.otter.gateway.servlet.translator;

import org.junit.Before;
import org.junit.Test;

import jakarta.servlet.http.Cookie;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class HttpServletRequestCookieTranslatorTest {
    private HttpServletRequestCookieTranslator subject;

    @Before
    public void setUp() {
        subject = new HttpServletRequestCookieTranslator();
    }

    @Test
    public void fromShouldTranslateOk() {
        Cookie containerCookie = new Cookie("cookie-name", "cookie-value");
        containerCookie.setDomain("www.tokensmith.net");
        containerCookie.setMaxAge(100);
        containerCookie.setPath("/account");
        containerCookie.setSecure(true);
        containerCookie.setVersion(0);
        containerCookie.setHttpOnly(true);

        net.tokensmith.otter.controller.entity.Cookie actual = subject.from(containerCookie);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getDomain(), is("www.tokensmith.net"));
        assertThat(actual.getMaxAge(), is(100));
        assertThat(actual.getPath(), is("/account"));
        assertThat(actual.isSecure(), is(true));
        assertThat(actual.getVersion(), is(0));
        assertThat(actual.isHttpOnly(), is(true));

    }

    @Test
    public void toShouldTranslateOk() {
        var otterCookie = new net.tokensmith.otter.controller.entity.Cookie.Builder()
            .name("cookie-name")
            .value("cookie-value")
            .domain("www.tokensmith.net")
            .maxAge(100)
            .path("/account")
            .secure(true)
            .version(0)
            .httpOnly(true)
            .build();

        Cookie actual = subject.to(otterCookie);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getDomain(), is("www.tokensmith.net"));
        assertThat(actual.getMaxAge(), is(100));
        assertThat(actual.getPath(), is("/account"));
        assertThat(actual.getSecure(), is(true));
        assertThat(actual.getVersion(), is(0));
        assertThat(actual.isHttpOnly(), is(true));

    }
}