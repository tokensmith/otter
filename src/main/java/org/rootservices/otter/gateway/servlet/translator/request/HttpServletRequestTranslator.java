package org.rootservices.otter.gateway.servlet.translator.request;


import org.rootservices.otter.QueryStringToMap;
import org.rootservices.otter.controller.builder.RequestBuilder;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.router.entity.Method;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class HttpServletRequestTranslator {
    private static String PARAM_DELIMITER = "?";
    private static String EMPTY = "";

    private HttpServletRequestCookieTranslator httpServletCookieTranslator;
    private HttpServletRequestHeaderTranslator httpServletRequestHeaderTranslator;
    private QueryStringToMap queryStringToMap;
    private HttpServletRequestUrlTranslator httpServletRequestUrlTranslator;

    public HttpServletRequestTranslator(HttpServletRequestCookieTranslator httpServletCookieTranslator, HttpServletRequestHeaderTranslator httpServletRequestHeaderTranslator, QueryStringToMap queryStringToMap, HttpServletRequestUrlTranslator httpServletRequestUrlTranslator) {
        this.httpServletCookieTranslator = httpServletCookieTranslator;
        this.httpServletRequestHeaderTranslator = httpServletRequestHeaderTranslator;
        this.queryStringToMap = queryStringToMap;
        this.httpServletRequestUrlTranslator = httpServletRequestUrlTranslator;
    }

    public Request from(HttpServletRequest containerRequest) throws IOException {

        Method method = Method.valueOf(containerRequest.getMethod());

        String pathWithParams = containerRequest.getRequestURI() +
                queryStringForUrl(containerRequest.getQueryString());

        Map<String, Cookie> otterCookies = Arrays.asList(containerRequest.getCookies())
            .stream()
            .collect(
                Collectors.toMap(
                    javax.servlet.http.Cookie::getName, httpServletCookieTranslator.from
                )
             );

        Map<String, String> headers = httpServletRequestHeaderTranslator.from(containerRequest);
        Optional<String> queryString = Optional.ofNullable(containerRequest.getQueryString());
        Map<String, List<String>> queryParams = queryStringToMap.run(queryString);

        return new RequestBuilder()
                .method(method)
                .pathWithParams(pathWithParams)
                .authScheme(Optional.empty())
                .cookies(otterCookies)
                .headers(headers)
                .queryParams(queryParams)
                .body(containerRequest.getReader())
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
