package helper;


import helper.entity.FakeResource;
import org.rootservices.otter.controller.builder.ResponseBuilder;
import org.rootservices.otter.controller.entity.Cookie;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.router.entity.Match;
import org.rootservices.otter.router.entity.Regex;
import org.rootservices.otter.router.entity.Route;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FixtureFactory {

    public static Optional<Match> makeMatch(String url) {
        Route route = makeRoute(url);
        Matcher matcher = route.getPattern().matcher(url);
        return  Optional.of(new Match(matcher, route));
    }

    public static Route makeRoute(String regex) {
        Pattern p = Pattern.compile(regex);
        FakeResource resource = new FakeResource();
        return new Route(p, resource);
    }

    public static List<Route> makeRoutes() {
        List<Route> routes = new ArrayList<>();
        routes.add(makeRoute("/api/v1/foo/" + Regex.UUID.getRegex()));
        routes.add(makeRoute("/api/v1/foo/" + Regex.UUID.getRegex() + "/bar"));
        return routes;
    }

    public static Request makeRequest() {
        Request request = new Request();
        request.setCookies(makeCookies());
        return request;
    }

    public static Response makeResponse() {
        return new ResponseBuilder().build();
    }

    public static Map<String, Cookie> makeCookies() {
        List<Cookie> cookieList = new ArrayList<>();
        cookieList.add(makeCookie());

        Map<String, Cookie> cookies = new HashMap<>();
        cookies.put(cookieList.get(0).getName(), cookieList.get(0));
        return cookies;
    }

    public static Cookie makeCookie() {
        Cookie cookie = new Cookie();
        cookie.setName("test");
        return cookie;
    }
}
