package net.tokensmith.otter.gateway.servlet.translator;


import net.tokensmith.otter.QueryStringToMap;
import net.tokensmith.otter.config.CookieConfig;
import net.tokensmith.otter.controller.entity.Cookie;
import net.tokensmith.otter.controller.entity.mime.MimeType;
import net.tokensmith.otter.controller.entity.mime.SubType;
import net.tokensmith.otter.controller.entity.mime.TopLevelType;
import net.tokensmith.otter.controller.header.Header;
import net.tokensmith.otter.router.builder.AskBuilder;
import net.tokensmith.otter.router.entity.Method;
import net.tokensmith.otter.router.entity.io.Ask;
import net.tokensmith.otter.translator.MimeTypeTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * Translator for a HttpServletRequest to a Otter Request
 */
public class HttpServletRequestTranslator  {
    protected static Logger LOGGER = LoggerFactory.getLogger(HttpServletRequestTranslator.class);
    private static String PARAM_DELIMITER = "?";
    private static String EMPTY = "";

    private HttpServletRequestCookieTranslator httpServletCookieTranslator;
    private HttpServletRequestHeaderTranslator httpServletRequestHeaderTranslator;
    private QueryStringToMap queryStringToMap;
    private MimeTypeTranslator mimeTypeTranslator;
    // used to ensure http only is set on incoming cookies
    private Map<String, CookieConfig> cookieConfigs;

    public HttpServletRequestTranslator(HttpServletRequestCookieTranslator httpServletCookieTranslator,
                                        HttpServletRequestHeaderTranslator httpServletRequestHeaderTranslator,
                                        QueryStringToMap queryStringToMap, MimeTypeTranslator mimeTypeTranslator,
                                        Map<String, CookieConfig> cookieConfigs) {
        this.httpServletCookieTranslator = httpServletCookieTranslator;
        this.httpServletRequestHeaderTranslator = httpServletRequestHeaderTranslator;
        this.queryStringToMap = queryStringToMap;
        this.mimeTypeTranslator = mimeTypeTranslator;
        this.cookieConfigs = cookieConfigs;
    }

    public Ask from(HttpServletRequest containerRequest, byte[] containerBody) throws IOException {

        Method method = Method.valueOf(containerRequest.getMethod());

        String pathWithParams = containerRequest.getRequestURI() +
                queryStringForUrl(containerRequest.getQueryString());

        Map<String, Cookie> otterCookies = from(containerRequest.getCookies());

        Map<String, String> headers = httpServletRequestHeaderTranslator.from(containerRequest);
        Optional<String> queryString = Optional.ofNullable(containerRequest.getQueryString());
        Map<String, List<String>> queryParams = queryStringToMap.run(queryString);

        MimeType contentType = mimeTypeTranslator.to(containerRequest.getContentType());
        String acceptFrom = containerRequest.getHeader(Header.ACCEPT.getValue());
        MimeType acceptTo = mimeTypeTranslator.to(acceptFrom);


        Map<String, List<String>> formData = new HashMap<>();
        Optional<byte[]> body = Optional.empty();
        if (isForm(method, contentType)) {
            String form = new String(containerBody);
            formData = queryStringToMap.run(Optional.of(form));
        } else if (method == Method.POST || method == Method.PUT || method == Method.PATCH && !isForm(method, contentType)) {
            body = Optional.of(containerBody);
        }

        String ipAddress = containerRequest.getRemoteAddr();

        return new AskBuilder()
                .matcher(Optional.empty())
                .method(method)
                .scheme(containerRequest.getScheme())
                .authority(containerRequest.getServerName())
                .port(containerRequest.getServerPort())
                .pathWithParams(pathWithParams)
                .contentType(contentType)
                .accept(acceptTo)
                .cookies(otterCookies)
                .headers(headers)
                .queryParams(queryParams)
                .formData(formData)
                .body(body)
                .csrfChallenge(Optional.empty())
                .ipAddress(ipAddress)
                .build();
    }

    protected Map<String, Cookie> from(javax.servlet.http.Cookie[] containerCookies) {
        Map<String, Cookie> otterCookies = new HashMap<>();
        if (Objects.nonNull(containerCookies)) {
            // throw away duplicate cookies.. idk why duplicates occur.
            for (javax.servlet.http.Cookie cookie : containerCookies) {
                Cookie candidate = httpServletCookieTranslator.from(cookie);
                Cookie existing = otterCookies.get(candidate.getName());
                if (Objects.nonNull(existing) && existing.equals(candidate)) {
                    LOGGER.debug("Found a duplicate cookie, {}, ignoring it.", existing.getName());
                } else {
                    // ensure http only is set - some ajax wont pass this is.. idk why
                    CookieConfig expectedConfig = cookieConfigs.get(candidate.getName());
                    if (Objects.nonNull(expectedConfig)) {
                        if (!expectedConfig.getHttpOnly().equals(candidate.isHttpOnly())) {
                            // force it to the default then.
                            LOGGER.debug("httpOnly is being overriden for cookie, {}. expected {}, actual {}",
                                    candidate.getName(), expectedConfig.getHttpOnly(), candidate.isHttpOnly());
                            candidate.setHttpOnly(expectedConfig.getHttpOnly());
                        }
                    }

                    otterCookies.put(candidate.getName(), candidate);
                }
            }
        }
        return otterCookies;
    }

    protected Boolean isForm(Method method, MimeType contentType) {
        return method == Method.POST && TopLevelType.APPLICATION.toString().equals(contentType.getType()) && SubType.FORM.toString().equals(contentType.getSubType());
    }

    protected String queryStringForUrl(String queryString) {
        String queryStringForUrl;
        if (Objects.nonNull(queryString)) {
            queryStringForUrl = PARAM_DELIMITER + queryString;
        } else {
            queryStringForUrl = EMPTY;
        }
        return queryStringForUrl;
    }
}
