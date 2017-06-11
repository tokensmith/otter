package org.rootservices.otter.gateway.servlet.translator;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.Cookie;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class HttpServletRequestCookieTranslatorTest {
    private HttpServletRequestCookieTranslator subject;

    @Before
    public void setUp() {
        subject = new HttpServletRequestCookieTranslator();
    }

    @Test
    public void fromShouldTranslateOk() {
        Cookie containerCookie = new Cookie("cookie-name", "cookie-value");
        containerCookie.setDomain("www.rootservices.org");
        containerCookie.setMaxAge(100);
        containerCookie.setPath("/account");
        containerCookie.setSecure(true);
        containerCookie.setVersion(0);

        org.rootservices.otter.controller.entity.Cookie actual = subject.from.apply(containerCookie);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getDomain(), is("www.rootservices.org"));
        assertThat(actual.getMaxAge(), is(100));
        assertThat(actual.getPath(), is("/account"));
        assertThat(actual.isSecure(), is(true));
        assertThat(actual.getVersion(), is(0));

    }

    @Test
    public void toShouldTranslateOk() {
        org.rootservices.otter.controller.entity.Cookie otterCookie = new org.rootservices.otter.controller.entity.Cookie();
        otterCookie.setName("cookie-name");
        otterCookie.setValue("cookie-value");
        otterCookie.setDomain("www.rootservices.org");
        otterCookie.setMaxAge(100);
        otterCookie.setPath("/account");
        otterCookie.setSecure(true);
        otterCookie.setVersion(0);

        Cookie actual = subject.to.apply(otterCookie);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getDomain(), is("www.rootservices.org"));
        assertThat(actual.getMaxAge(), is(100));
        assertThat(actual.getPath(), is("/account"));
        assertThat(actual.getSecure(), is(true));
        assertThat(actual.getVersion(), is(0));

    }
}