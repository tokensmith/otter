package org.rootservices.otter.gateway.servlet.translator;


import org.rootservices.otter.QueryStringToMap;
import org.rootservices.otter.controller.builder.RequestBuilder;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.header.ContentType;
import org.rootservices.otter.router.entity.Method;

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

    public HttpServletRequestTranslator(HttpServletRequestCookieTranslator httpServletCookieTranslator, HttpServletRequestHeaderTranslator httpServletRequestHeaderTranslator, QueryStringToMap queryStringToMap) {
        this.httpServletCookieTranslator = httpServletCookieTranslator;
        this.httpServletRequestHeaderTranslator = httpServletRequestHeaderTranslator;
        this.queryStringToMap = queryStringToMap;
    }

    public Request from(HttpServletRequest containerRequest, String containerBody) throws IOException {

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


        Map<String, List<String>> formData = new HashMap<>();
        if (method == Method.POST && ContentType.FORM_URL_ENCODED.getValue().equals(containerRequest.getContentType())) {
            formData = queryStringToMap.run(Optional.of(containerBody));
        }

        Optional<String> body = Optional.empty();
        if (method == Method.POST && !ContentType.FORM_URL_ENCODED.getValue().equals(containerRequest.getContentType())) {
            body = Optional.of(containerBody);
        }

        return new RequestBuilder()
                .matcher(Optional.empty())
                .method(method)
                .pathWithParams(pathWithParams)
                .cookies(otterCookies)
                .headers(headers)
                .queryParams(queryParams)
                .formData(formData)
                .body(body)
                .csrfChallenge(Optional.empty())
                .build();
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
