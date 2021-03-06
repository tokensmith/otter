package net.tokensmith.otter.router.builder;

import helper.FixtureFactory;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.io.Ask;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AskBuilderTest {
    private AskBuilder subject;

    @Before
    public void setUp() {
        subject = new AskBuilder();
    }

    @Test
    public void buildWhenMatcherShouldBeOk() {
        Ask actual = subject.matcher(Optional.empty()).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMatcher().isPresent(), is(false));
    }

    @Test
    public void buildWhenBaseURIPartsShouldBeOk() {
        String scheme = "http";
        String authority = "tokensmith.net";
        Integer port = 443;

        Ask actual = subject.scheme(scheme)
                .authority(authority)
                .port(port)
                .build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getScheme(), is(scheme));
        assertThat(actual.getAuthority(), is(authority));
        assertThat(actual.getPort(), is(port));
    }

    @Test
    public void buildWhenPathWithParamsShouldBeOk() {
        String contextPath = "/pathWithParams";
        Ask actual = subject.pathWithParams(contextPath).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPathWithParams(), is(contextPath));
    }

    @Test
    public void buildWhenMethodShouldBeOk() {
        Ask actual = subject.method(Method.GET).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.GET));
    }

    @Test
    public void buildWhenContentTypeShouldBeOk() {

        MimeType contentType = new MimeType();
        Ask actual = subject.contentType(contentType).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getContentType(), is(notNullValue()));
        assertThat(actual.getContentType(), is(contentType));
    }

    @Test
    public void buildWhenAcceptShouldBeOk() {

        MimeType contentType = new MimeType();
        Ask actual = subject.accept(contentType).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getAccept(), is(notNullValue()));
        assertThat(actual.getAccept(), is(contentType));
    }

    @Test
    public void buildWhenHeadersShouldBeOk() {
        Map<String, String> headers = FixtureFactory.makeHeaders();

        Ask actual = subject.headers(headers).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders(), is(headers));
    }

    @Test
    public void buildWhenCookiesShouldBeOk() {
        Map<String, Cookie> cookies = FixtureFactory.makeCookies();

        Ask actual = subject.cookies(cookies).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCookies(), is(cookies));
    }

    @Test
    public void buildWhenQueryParamsShouldBeOk() {
        Map<String, List<String>> queryParams = FixtureFactory.makeEmptyQueryParams();

        Ask actual = subject.queryParams(queryParams).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getQueryParams(), is(queryParams));
    }

    @Test
    public void buildWhenFormDataShouldBeOk() {
        Map<String, List<String>> formData = new HashMap<>();
        formData.put("foo", Arrays.asList("bar"));

        Ask actual = subject.formData(formData).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getFormData(), is(formData));
    }

    @Test
    public void buildWhenPayloadShouldBeOk() {
        byte[] body = "{\"integer\": 5, \"unknown_key\": \"4\", \"local_date\": \"2019-01-01\"}".getBytes();
        Optional<byte[]> json = Optional.of(body);

        Ask actual = subject.body(json).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getBody(), is(json));
    }

    @Test
    public void buildWhenIpAddressShouldBeOk() {

        String ipAddress = "127.0.0.1";
        Ask actual = subject.ipAddress(ipAddress).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getIpAddress(), is(ipAddress));
    }

}