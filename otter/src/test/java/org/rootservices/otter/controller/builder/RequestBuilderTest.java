package org.rootservices.otter.controller.builder;

import helper.FixtureFactory;
import org.junit.Before;
import org.junit.Test;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Method;

import java.util.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;


public class RequestBuilderTest {
    private RequestBuilder subject;

    @Before
    public void setUp() {
        subject = new RequestBuilder();
    }

    @Test
    public void buildWhenMatcherShouldBeOk() {
        Request actual = subject.matcher(Optional.empty()).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMatcher().isPresent(), is(false));
    }

    @Test
    public void buildWhenPathWithParamsShouldBeOk() {
        String url = "/pathWithParams";
        Request actual = subject.pathWithParams(url).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getPathWithParams(), is(url));
    }

    @Test
    public void buildWhenMethodShouldBeOk() {
        Request actual = subject.method(Method.GET).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.GET));
    }

    @Test
    public void buildWhenContentTypeShouldBeOk() {

        MimeType contentType = new MimeType();
        Request actual = subject.contentType(contentType).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getContentType(), is(notNullValue()));
        assertThat(actual.getContentType(), is(contentType));
    }

    @Test
    public void buildWhenHeadersShouldBeOk() {
        Map<String, String> headers = FixtureFactory.makeHeaders();

        Request actual = subject.headers(headers).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders(), is(headers));
    }

    @Test
    public void buildWhenCookiesShouldBeOk() {
        Map<String, Cookie> cookies = FixtureFactory.makeCookies();

        Request actual = subject.cookies(cookies).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getCookies(), is(cookies));
    }

    @Test
    public void buildWhenQueryParamsShouldBeOk() {
        Map<String, List<String>> queryParams = FixtureFactory.makeEmptyQueryParams();

        Request actual = subject.queryParams(queryParams).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getQueryParams(), is(queryParams));
    }

    @Test
    public void buildWhenFormDataShouldBeOk() {
        Map<String, List<String>> formData = new HashMap<>();
        formData.put("foo", Arrays.asList("bar"));

        Request actual = subject.formData(formData).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getFormData(), is(formData));
    }

    @Test
    public void buildWhenPayloadShouldBeOk() {
        byte[] body = "{\"integer\": 5, \"unknown_key\": \"4\", \"local_date\": \"2019-01-01\"}".getBytes();
        Optional<byte[]> json = Optional.of(body);

        Request actual = subject.body(json).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getBody(), is(json));
    }

    @Test
    public void buildWhenIpAddressShouldBeOk() {

        String ipAddress = "127.0.0.1";
        Request actual = subject.ipAddress(ipAddress).build();

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getIpAddress(), is(ipAddress));
    }
}