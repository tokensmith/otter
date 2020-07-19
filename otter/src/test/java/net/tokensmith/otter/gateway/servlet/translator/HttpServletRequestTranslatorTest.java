package net.tokensmith.otter.gateway.servlet.translator;

import helper.FixtureFactory;
import net.tokensmith.otter.QueryStringToMap;
import net.tokensmith.otter.controller.builder.MimeTypeBuilder;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.gateway.entity.Shape;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.io.Ask;
import net.tokensmith.otter.translator.MimeTypeTranslator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    private HttpServletRequestTranslator subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Shape shape = FixtureFactory.makeShape("1234", "56789");
        subject = new HttpServletRequestTranslator(
                mockHttpServletCookieTranslator,
                mockHttpServletRequestHeaderTranslator,
                mockQueryStringToMap,
                mockMimeTypeTranslator,
                shape.getCookieConfigs()
        );
    }

    @Test
    public void fromWhenPostAndContentTypeIsJsonAndAcceptIsJsonShouldTranslateOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        when(mockContainerRequest.getMethod()).thenReturn("POST");
        when(mockContainerRequest.getScheme()).thenReturn("https");
        when(mockContainerRequest.getServerName()).thenReturn("tokensmith.net");
        when(mockContainerRequest.getServerPort()).thenReturn(443);
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
        when(mockContainerRequest.getHeader(Header.ACCEPT.getValue())).thenReturn(json.toString());

        String body = "{\"integer\": 5, \"integer\": \"4\", \"local_date\": \"2019-01-01\"}";
        Ask actual = subject.from(mockContainerRequest, body.getBytes());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.POST));
        assertThat(actual.getScheme(), is("https"));
        assertThat(actual.getAuthority(), is("tokensmith.net"));
        assertThat(actual.getPort(), is(443));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getContentType(), is(notNullValue()));
        assertThat(actual.getContentType(), is(json));
        assertThat(actual.getAccept(), is(notNullValue()));
        assertThat(actual.getAccept(), is(json));
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
        when(mockContainerRequest.getScheme()).thenReturn("https");
        when(mockContainerRequest.getServerName()).thenReturn("tokensmith.net");
        when(mockContainerRequest.getServerPort()).thenReturn(443);
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
        Ask actual = subject.from(mockContainerRequest, body.getBytes());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.POST));
        assertThat(actual.getScheme(), is("https"));
        assertThat(actual.getAuthority(), is("tokensmith.net"));
        assertThat(actual.getPort(), is(443));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getContentType(), is(notNullValue()));
        assertThat(actual.getContentType(), is(form));
        assertThat(actual.getAccept(), is(nullValue()));
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
    public void fromWhenPutAndContentTypeIsJsonAndAcceptIsJsonShouldTranslateOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        when(mockContainerRequest.getMethod()).thenReturn("PUT");
        when(mockContainerRequest.getScheme()).thenReturn("https");
        when(mockContainerRequest.getServerName()).thenReturn("tokensmith.net");
        when(mockContainerRequest.getServerPort()).thenReturn(443);
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
        when(mockContainerRequest.getHeader(Header.ACCEPT.getValue())).thenReturn(json.toString());

        String body = "{\"integer\": 5, \"integer\": \"4\", \"local_date\": \"2019-01-01\"}";
        Ask actual = subject.from(mockContainerRequest, body.getBytes());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.PUT));
        assertThat(actual.getScheme(), is("https"));
        assertThat(actual.getAuthority(), is("tokensmith.net"));
        assertThat(actual.getPort(), is(443));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getContentType(), is(notNullValue()));
        assertThat(actual.getContentType(), is(json));
        assertThat(actual.getAccept(), is(notNullValue()));
        assertThat(actual.getAccept(), is(json));
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
    public void fromWhenPatchAndContentTypeIsJsonAndAcceptIsJsonShouldTranslateOk() throws Exception {
        HttpServletRequest mockContainerRequest = mock(HttpServletRequest.class);
        when(mockContainerRequest.getMethod()).thenReturn("PATCH");
        when(mockContainerRequest.getScheme()).thenReturn("https");
        when(mockContainerRequest.getServerName()).thenReturn("tokensmith.net");
        when(mockContainerRequest.getServerPort()).thenReturn(443);
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
        when(mockContainerRequest.getHeader(Header.ACCEPT.getValue())).thenReturn(json.toString());

        String body = "{\"integer\": 5, \"integer\": \"4\", \"local_date\": \"2019-01-01\"}";
        Ask actual = subject.from(mockContainerRequest, body.getBytes());

        assertThat(actual, is(notNullValue()));
        assertThat(actual.getMethod(), is(Method.PATCH));
        assertThat(actual.getScheme(), is("https"));
        assertThat(actual.getAuthority(), is("tokensmith.net"));
        assertThat(actual.getPort(), is(443));
        assertThat(actual.getHeaders(), is(notNullValue()));
        assertThat(actual.getHeaders().size(), is(0));
        assertThat(actual.getContentType(), is(notNullValue()));
        assertThat(actual.getContentType(), is(json));
        assertThat(actual.getAccept(), is(notNullValue()));
        assertThat(actual.getAccept(), is(json));
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

    // cookie tests.
    @Test
    public void fromWhenDuplicateCookieThenIgnoreIt() {
        Cookie[] cookies = new Cookie[2];
        cookies[0] = new Cookie("session", "test-value");
        cookies[1] = new Cookie("session", "test-value");

        var otterCookie = new net.tokensmith.otter.controller.entity.Cookie.Builder()
                .name("session")
                .value("test-value")
                .build();

        when(mockHttpServletCookieTranslator.from(any(Cookie.class))).thenReturn(otterCookie);

        Map<String, net.tokensmith.otter.controller.entity.Cookie> actual = subject.from(cookies);
        assertThat(actual.size(), is(1));
        assertThat(actual.get("session").getValue(), is("test-value"));
    }

    @Test
    public void fromWhenHttpOnlyIsFalseThenOverrideToTrue() {
        Cookie[] cookies = new Cookie[1];
        Cookie cookie = new Cookie("session", "test-value");
        cookie.setHttpOnly(false);
        cookies[0] = cookie;

        var otterCookie = new net.tokensmith.otter.controller.entity.Cookie.Builder()
                .name("session")
                .value("test-value")
                .httpOnly(false)
                .build();

        when(mockHttpServletCookieTranslator.from(any(Cookie.class))).thenReturn(otterCookie);

        Map<String, net.tokensmith.otter.controller.entity.Cookie> actual = subject.from(cookies);
        assertThat(actual.size(), is(1));
        assertThat(actual.get("session").getValue(), is("test-value"));
        assertThat(actual.get("session").isHttpOnly(), is(true));
    }
}