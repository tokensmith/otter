package org.rootservices.otter.gateway.servlet.translator;


import org.rootservices.otter.QueryStringToMap;
import org.rootservices.otter.controller.builder.RequestBuilder;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.controller.entity.mime.SubType;
import org.rootservices.otter.controller.entity.mime.TopLevelType;
import org.rootservices.otter.controller.header.ContentType;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.security.session.Session;
import org.rootservices.otter.translator.MimeTypeTranslator;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class HttpServletRequestTranslator {
    private static String PARAM_DELIMITER = "?";
    private static String EMPTY = "";

    private HttpServletRequestCookieTranslator httpServletCookieTranslator;
    private HttpServletRequestHeaderTranslator httpServletRequestHeaderTranslator;
    private QueryStringToMap queryStringToMap;
    private MimeTypeTranslator mimeTypeTranslator;

    public HttpServletRequestTranslator(HttpServletRequestCookieTranslator httpServletCookieTranslator,
                                        HttpServletRequestHeaderTranslator httpServletRequestHeaderTranslator,
                                        QueryStringToMap queryStringToMap, MimeTypeTranslator mimeTypeTranslator) {
        this.httpServletCookieTranslator = httpServletCookieTranslator;
        this.httpServletRequestHeaderTranslator = httpServletRequestHeaderTranslator;
        this.queryStringToMap = queryStringToMap;
        this.mimeTypeTranslator = mimeTypeTranslator;
    }

    public Request<Session> from(HttpServletRequest containerRequest, byte[] containerBody) throws IOException {

        Method method = Method.valueOf(containerRequest.getMethod());

        String pathWithParams = containerRequest.getRequestURI() +
                queryStringForUrl(containerRequest.getQueryString());

        Map<String, Cookie> otterCookies = new HashMap<>();
        if (containerRequest.getCookies() != null) {
            otterCookies = Arrays.asList(containerRequest.getCookies())
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    javax.servlet.http.Cookie::getName, httpServletCookieTranslator.from
                            )
                    );
        }
        Map<String, String> headers = httpServletRequestHeaderTranslator.from(containerRequest);
        Optional<String> queryString = Optional.ofNullable(containerRequest.getQueryString());
        Map<String, List<String>> queryParams = queryStringToMap.run(queryString);

        MimeType contentType = mimeTypeTranslator.to(containerRequest.getContentType());

        Map<String, List<String>> formData = new HashMap<>();
        Optional<byte[]> body = Optional.empty();
        if (isForm(method, contentType)) {
            String form = new String(containerBody);
            formData = queryStringToMap.run(Optional.of(form));
        } else if (method == Method.POST && !isForm(method, contentType)) {
            body = Optional.of(containerBody);
        }

        String ipAddress = containerRequest.getRemoteAddr();

        return new RequestBuilder()
                .matcher(Optional.empty())
                .method(method)
                .pathWithParams(pathWithParams)
                .contentType(contentType)
                .cookies(otterCookies)
                .headers(headers)
                .queryParams(queryParams)
                .formData(formData)
                .body(body)
                .csrfChallenge(Optional.empty())
                .ipAddress(ipAddress)
                .build();
    }

    protected Boolean isForm(Method method, MimeType contentType) {
        return method == Method.POST && TopLevelType.APPLICATION.toString().equals(contentType.getType()) && SubType.FORM.toString().equals(contentType.getSubType());
    }

    protected String queryStringForUrl(String queryString) {
        String queryStringForUrl;
        if (queryString != null) {
            queryStringForUrl = PARAM_DELIMITER + queryString;
        } else {
            queryStringForUrl = EMPTY;
        }
        return queryStringForUrl;
    }
}
