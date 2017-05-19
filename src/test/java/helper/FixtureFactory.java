package helper;


import org.rootservices.otter.router.entity.Regex;
import org.rootservices.otter.router.entity.Route;

import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FixtureFactory {

    public static Route makeRoute(String regex) {
        Pattern p = Pattern.compile(regex);
        HttpServlet servlet = new FakeServlet();
        return new Route(p, servlet);
    }

    public static List<Route> makeRoutes() {
        List<Route> routes = new ArrayList<>();
        routes.add(makeRoute("/api/v1/foo/" + Regex.UUID.getRegex()));
        routes.add(makeRoute("/api/v1/foo/" + Regex.UUID.getRegex() + "/bar"));
        return routes;
    }
}
