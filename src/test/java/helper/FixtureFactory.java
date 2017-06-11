package helper;


import helper.entity.FakeResource;
import org.rootservices.otter.controller.builder.ResponseBuilder;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.header.Header;
import org.rootservices.otter.controller.header.HeaderValue;
import org.rootservices.otter.router.entity.MatchedRoute;
import org.rootservices.otter.router.entity.Regex;
import org.rootservices.otter.router.entity.Route;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FixtureFactory {

    public static Optional<MatchedRoute> makeMatch(String url) {
        Route route = makeRoute(url);
        Matcher matcher = route.getPattern().matcher(url);
        return  Optional.of(new MatchedRoute(matcher, route));
    }

    public static Route makeRoute(String regex) {
        Pattern p = Pattern.compile(regex);
        FakeResource resource = new FakeResource();
        return new Route(p, resource);
    }

    public static List<Route> makeRoutes() {
        return makeRoutes("/api/v1/foo/");
    }

    public static List<Route> makeRoutes(String baseContext) {
        List<Route> routes = new ArrayList<>();
        routes.add(makeRoute(baseContext + Regex.UUID.getRegex()));
        routes.add(makeRoute(baseContext + Regex.UUID.getRegex() + "/bar"));
        return routes;
    }

    public static Map<String, List<String>> makeEmptyQueryParams() {
        Map<String, List<String>> params = new HashMap<>();
        return params;
    }

    public static Request makeRequest() {
        Request request = new Request();
        request.setCookies(makeCookies());
        return request;
    }

    public static Map<String, String> makeHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Header.CACHE_CONTROL.getValue(), HeaderValue.NO_CACHE.getValue());
        return headers;
    }

    public static Response makeResponse() {
        return new ResponseBuilder()
                .headers(new HashMap<>())
                .cookies(new HashMap<>())
                .payload(Optional.empty())
                .template(Optional.empty())
                .ok()
                .build();
    }

    public static Map<String, Cookie> makeCookies() {
        List<Cookie> cookieList = new ArrayList<>();
        cookieList.add(makeCookie());

        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put(cookieList.get(0).getName(), cookieList.get(0));
        return cookies;
    }

    public static Cookie makeCookie() {
        return makeCookie("test");
    }

    public static Cookie makeCookie(String name) {
        Cookie cookie = new Cookie();
        cookie.setName(name);
        cookie.setValue("test-value");
        return cookie;
    }
}
