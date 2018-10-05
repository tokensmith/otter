package org.rootservices.otter.gateway.servlet.translator;

import helper.entity.DummySession;
import helper.entity.DummyUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.rootservices.otter.QueryStringToMap;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.translator.MimeTypeTranslator;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class HttpServletRequestTranslatorTest {
    @Mock
    private HttpServletRequestCookieTranslator mockHttpServletCookieTranslator;
    @Mock
    private HttpServletRequestHeaderTranslator mockHttpServletRequestHeaderTranslator;
    @Mock
    private QueryStringToMap mockQueryStringToMap;
    @Mock
    private MimeTypeTranslator mockMimeTypeTranslator;

    private HttpServletRequestTranslator<DummySession, DummyUser> subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subject = new HttpServletRequestTranslator<DummySession, DummyUser>(
                mockHttpServletCookieTranslator,
                mockHttpServletRequestHeaderTranslator,
                mockQueryStringToMap,
                mockMimeTypeTranslator
        );
    }

    @Test
    public void fromWhenCookiesAreNullShouldTranslateOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        when(mockContainerRequest.getMethod()).thenReturn("GET");
        when(mockContainerRequest.getRequestURI()).thenReturn("/foo");
        when(mockContainerRequest.getQueryString()).thenReturn("bar=bar-value");
        when(mockContainerRequest.getCookies()).thenReturn(null);
        when(mockHttpServletRequestHeaderTranslator.from(mockContainerRequest))
            .thenReturn(new HashMap<>());
        when(mockContainerRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("bar", new ArrayList<>());
        queryParams.get("bar").add("bar-value");

        when(mockQueryStringToMap.run(Optional.of("bar=bar-value")))
            .thenReturn(queryParams);

        MimeType json = new MimeTypeBuilder().json().build();
        when(mockMimeTypeTranslator.to(json.toString())).thenReturn(json);

        when(mockContainerRequest.getContentType()).thenReturn(json.toString());

        byte[] containerBody = null;
        Request actual = subject.from(mockContainerRequest, containerBody);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.GET));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getContentType(), is(notNullValue()));
        assertThat(actual.getContentType(), is(json));
        assertThat(actual.getBody().isPresent(), is(false));
        assertThat(actual.getQueryParams(), is(notNullValue()));
        assertThat(actual.getQueryParams(), is(queryParams));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getCookies().size(), is(0));
        assertThat(actual.getPathWithParams(), is("/foo?bar=bar-value"));
        assertThat(actual.getMatcher(), is(notNullValue()));
        assertThat(actual.getMatcher().isPresent(), is(false));
        assertThat(actual.getFormData(), is(notNullValue()));
        assertThat(actual.getFormData().size(), is(0));
        assertThat(actual.getCsrfChallenge().isPresent(), is(false));
        assertThat(actual.getIpAddress(), is(notNullValue()));
        assertThat(actual.getIpAddress(), is("127.0.0.1"));
    }

    @Test
    public void fromWhenPostAndContentTypeIsJsonShouldTranslateOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        when(mockContainerRequest.getMethod()).thenReturn("POST");
        when(mockContainerRequest.getRequestURI()).thenReturn("/foo");
        when(mockContainerRequest.getQueryString()).thenReturn("bar=bar-value");
        when(mockContainerRequest.getCookies()).thenReturn(null);
        when(mockHttpServletRequestHeaderTranslator.from(mockContainerRequest))
                .thenReturn(new HashMap<>());
        when(mockContainerRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("bar", new ArrayList<>());
        queryParams.get("bar").add("bar-value");

        when(mockQueryStringToMap.run(Optional.of("bar=bar-value")))
                .thenReturn(queryParams);

        MimeType json = new MimeTypeBuilder().json().build();
        when(mockMimeTypeTranslator.to(json.toString())).thenReturn(json);

        when(mockContainerRequest.getContentType()).thenReturn(json.toString());

        String body = "{\"integer\": 5, \"integer\": \"4\", \"local_date\": \"2019-01-01\"}";
        Request actual = subject.from(mockContainerRequest, body.getBytes());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.POST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getContentType(), is(notNullValue()));
        assertThat(actual.getContentType(), is(json));
        assertThat(actual.getBody().isPresent(), is(true));
        assertThat(actual.getBody().get(), is(body.getBytes()));
        assertThat(actual.getQueryParams(), is(notNullValue()));
        assertThat(actual.getQueryParams(), is(queryParams));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getCookies().size(), is(0));
        assertThat(actual.getPathWithParams(), is("/foo?bar=bar-value"));
        assertThat(actual.getMatcher(), is(notNullValue()));
        assertThat(actual.getMatcher().isPresent(), is(false));
        assertThat(actual.getFormData(), is(notNullValue()));
        assertThat(actual.getFormData().size(), is(0));
        assertThat(actual.getCsrfChallenge().isPresent(), is(false));
        assertThat(actual.getIpAddress(), is(notNullValue()));
        assertThat(actual.getIpAddress(), is("127.0.0.1"));
    }

    @Test
    public void fromWhenPostAndCookiesAreNullAndContentTypeIsFormUrlEncodedShouldTranslateOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        when(mockContainerRequest.getMethod()).thenReturn("POST");
        when(mockContainerRequest.getRequestURI()).thenReturn("/foo");
        when(mockContainerRequest.getQueryString()).thenReturn("bar=bar-value");
        when(mockContainerRequest.getCookies()).thenReturn(null);
        when(mockHttpServletRequestHeaderTranslator.from(mockContainerRequest))
                .thenReturn(new HashMap<>());
        when(mockContainerRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("bar", Arrays.asList("bar-value"));

        when(mockQueryStringToMap.run(Optional.of("bar=bar-value")))
                .thenReturn(queryParams);

        Map<String, List<String>> formData = new HashMap<>();
        formData.put("form-field", Arrays.asList("form-value"));

        when(mockQueryStringToMap.run(Optional.of("form-field=form-value")))
            .thenReturn(formData);

        MimeType form = new MimeTypeBuilder().form().build();
        when(mockMimeTypeTranslator.to(form.toString())).thenReturn(form);

        when(mockContainerRequest.getContentType()).thenReturn(form.toString());

        String body = "form-field=form-value";
        Request<DummySession, DummyUser> actual = subject.from(mockContainerRequest, body.getBytes());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.POST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getContentType(), is(notNullValue()));
        assertThat(actual.getContentType(), is(form));
        assertThat(actual.getBody().isPresent(), is(false));
        assertThat(actual.getQueryParams(), is(notNullValue()));
        assertThat(actual.getQueryParams(), is(queryParams));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getCookies().size(), is(0));
        assertThat(actual.getPathWithParams(), is("/foo?bar=bar-value"));
        assertThat(actual.getMatcher(), is(notNullValue()));
        assertThat(actual.getMatcher().isPresent(), is(false));
        assertThat(actual.getIpAddress(), is(notNullValue()));
        assertThat(actual.getIpAddress(), is("127.0.0.1"));

        assertThat(actual.getFormData(), is(notNullValue()));
        assertThat(actual.getFormData().size(), is(1));

        assertThat(actual.getFormData().get("form-field"), is(notNullValue()));
        assertThat(actual.getFormData().get("form-field").size(), is(1));
        assertThat(actual.getFormData().get("form-field").get(0), is("form-value"));
    }

    @Test
    public void fromForAskWhenCookiesAreNullShouldTranslateOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        when(mockContainerRequest.getMethod()).thenReturn("GET");
        when(mockContainerRequest.getRequestURI()).thenReturn("/foo");
        when(mockContainerRequest.getQueryString()).thenReturn("bar=bar-value");
        when(mockContainerRequest.getCookies()).thenReturn(null);
        when(mockHttpServletRequestHeaderTranslator.from(mockContainerRequest))
                .thenReturn(new HashMap<>());
        when(mockContainerRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("bar", new ArrayList<>());
        queryParams.get("bar").add("bar-value");

        when(mockQueryStringToMap.run(Optional.of("bar=bar-value")))
                .thenReturn(queryParams);

        MimeType json = new MimeTypeBuilder().json().build();
        when(mockMimeTypeTranslator.to(json.toString())).thenReturn(json);

        when(mockContainerRequest.getContentType()).thenReturn(json.toString());

        byte[] containerBody = null;
        Ask actual = subject.fromForAsk(mockContainerRequest, containerBody);

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.GET));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getContentType(), is(notNullValue()));
        assertThat(actual.getContentType(), is(json));
        assertThat(actual.getBody().isPresent(), is(false));
        assertThat(actual.getQueryParams(), is(notNullValue()));
        assertThat(actual.getQueryParams(), is(queryParams));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getCookies().size(), is(0));
        assertThat(actual.getPathWithParams(), is("/foo?bar=bar-value"));
        assertThat(actual.getMatcher(), is(notNullValue()));
        assertThat(actual.getMatcher().isPresent(), is(false));
        assertThat(actual.getFormData(), is(notNullValue()));
        assertThat(actual.getFormData().size(), is(0));
        assertThat(actual.getCsrfChallenge().isPresent(), is(false));
        assertThat(actual.getIpAddress(), is(notNullValue()));
        assertThat(actual.getIpAddress(), is("127.0.0.1"));
    }

    @Test
    public void fromForAskWhenPostAndContentTypeIsJsonShouldTranslateOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        when(mockContainerRequest.getMethod()).thenReturn("POST");
        when(mockContainerRequest.getRequestURI()).thenReturn("/foo");
        when(mockContainerRequest.getQueryString()).thenReturn("bar=bar-value");
        when(mockContainerRequest.getCookies()).thenReturn(null);
        when(mockHttpServletRequestHeaderTranslator.from(mockContainerRequest))
                .thenReturn(new HashMap<>());
        when(mockContainerRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("bar", new ArrayList<>());
        queryParams.get("bar").add("bar-value");

        when(mockQueryStringToMap.run(Optional.of("bar=bar-value")))
                .thenReturn(queryParams);

        MimeType json = new MimeTypeBuilder().json().build();
        when(mockMimeTypeTranslator.to(json.toString())).thenReturn(json);

        when(mockContainerRequest.getContentType()).thenReturn(json.toString());

        String body = "{\"integer\": 5, \"integer\": \"4\", \"local_date\": \"2019-01-01\"}";
        Ask actual = subject.fromForAsk(mockContainerRequest, body.getBytes());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.POST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getContentType(), is(notNullValue()));
        assertThat(actual.getContentType(), is(json));
        assertThat(actual.getBody().isPresent(), is(true));
        assertThat(actual.getBody().get(), is(body.getBytes()));
        assertThat(actual.getQueryParams(), is(notNullValue()));
        assertThat(actual.getQueryParams(), is(queryParams));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getCookies().size(), is(0));
        assertThat(actual.getPathWithParams(), is("/foo?bar=bar-value"));
        assertThat(actual.getMatcher(), is(notNullValue()));
        assertThat(actual.getMatcher().isPresent(), is(false));
        assertThat(actual.getFormData(), is(notNullValue()));
        assertThat(actual.getFormData().size(), is(0));
        assertThat(actual.getCsrfChallenge().isPresent(), is(false));
        assertThat(actual.getIpAddress(), is(notNullValue()));
        assertThat(actual.getIpAddress(), is("127.0.0.1"));
    }

    @Test
    public void fromForAskWhenPostAndCookiesAreNullAndContentTypeIsFormUrlEncodedShouldTranslateOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        when(mockContainerRequest.getMethod()).thenReturn("POST");
        when(mockContainerRequest.getRequestURI()).thenReturn("/foo");
        when(mockContainerRequest.getQueryString()).thenReturn("bar=bar-value");
        when(mockContainerRequest.getCookies()).thenReturn(null);
        when(mockHttpServletRequestHeaderTranslator.from(mockContainerRequest))
                .thenReturn(new HashMap<>());
        when(mockContainerRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        Map<String, List<String>> queryParams = new HashMap<>();
        queryParams.put("bar", Arrays.asList("bar-value"));

        when(mockQueryStringToMap.run(Optional.of("bar=bar-value")))
                .thenReturn(queryParams);

        Map<String, List<String>> formData = new HashMap<>();
        formData.put("form-field", Arrays.asList("form-value"));

        when(mockQueryStringToMap.run(Optional.of("form-field=form-value")))
                .thenReturn(formData);

        MimeType form = new MimeTypeBuilder().form().build();
        when(mockMimeTypeTranslator.to(form.toString())).thenReturn(form);

        when(mockContainerRequest.getContentType()).thenReturn(form.toString());

        String body = "form-field=form-value";
        Ask actual = subject.fromForAsk(mockContainerRequest, body.getBytes());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.POST));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getContentType(), is(notNullValue()));
        assertThat(actual.getContentType(), is(form));
        assertThat(actual.getBody().isPresent(), is(false));
        assertThat(actual.getQueryParams(), is(notNullValue()));
        assertThat(actual.getQueryParams(), is(queryParams));
        assertThat(actual.getCookies(), is(notNullValue()));
        assertThat(actual.getCookies().size(), is(0));
        assertThat(actual.getPathWithParams(), is("/foo?bar=bar-value"));
        assertThat(actual.getMatcher(), is(notNullValue()));
        assertThat(actual.getMatcher().isPresent(), is(false));
        assertThat(actual.getIpAddress(), is(notNullValue()));
        assertThat(actual.getIpAddress(), is("127.0.0.1"));

        assertThat(actual.getFormData(), is(notNullValue()));
        assertThat(actual.getFormData().size(), is(1));

        assertThat(actual.getFormData().get("form-field"), is(notNullValue()));
        assertThat(actual.getFormData().get("form-field").size(), is(1));
        assertThat(actual.getFormData().get("form-field").get(0), is("form-value"));
    }
}