package integration.app.hello.controller;


import integration.app.hello.config.AppConfig;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.jwt.entity.jwk.Use;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.router.RouteBuilder;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.servlet.OtterEntryServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.Optional;

@WebServlet(value="/app/*", name="EntryServlet", asyncSupported = true)
public class EntryServlet extends OtterEntryServlet {
    private AppConfig appConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        appConfig = new AppConfig();

        // csrf
        CookieConfig csrfCookieConfig = new CookieConfig("csrf", false, -1);
        servletGateway.setCsrfCookieConfig(csrfCookieConfig);
        servletGateway.setCsrfFormFieldName("csrfToken");


        SymmetricKey signkey = appConfig.signKey();
        servletGateway.setSignKey(signkey);

        // session
        CookieConfig sessionCookieConfig = new CookieConfig("session", false, -1);
        SymmetricKey encKey = appConfig.encKey();

        servletGateway.setSessionCookieConfig(sessionCookieConfig);
        servletGateway.setEncKey(encKey);
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

        // csrf
        LoginResource login = new LoginResource();
        servletGateway.getCsrfProtect(login.URL, login);
        servletGateway.postCsrfProtect(login.URL, login);

        // session
        LoginSessionResource loginWithSession = new LoginSessionResource();
        servletGateway.getCsrfProtect(loginWithSession.URL, loginWithSession);
        servletGateway.postCsrfAndSessionProtect(loginWithSession.URL, loginWithSession);
    }
}
