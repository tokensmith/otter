package integration.app.hello.controller;


import integration.app.hello.config.AppConfig;
import org.rootservices.otter.router.RouteBuilder;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.servlet.OtterEntryServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;

@WebServlet(value="/app/*", name="EntryServlet")
public class EntryServlet extends OtterEntryServlet {
    private AppConfig appConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        appConfig = new AppConfig();
        routes();
    }

    public void routes() {
        Route notFoundRoute = new RouteBuilder()
                .resource(new NotFoundResource())
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        servletGateway.setNotFoundRoute(notFoundRoute);

        servletGateway.get(HelloResource.URL, new HelloResource());
        servletGateway.get(HelloRestResource.URL, appConfig.helloRestResource());
        servletGateway.post(HelloRestResource.URL, appConfig.helloRestResource());
    }
}
