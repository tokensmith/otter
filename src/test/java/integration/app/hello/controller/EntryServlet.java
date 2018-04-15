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
        CookieConfig csrfCookieConfig = new CookieConfig("csrf", false, -1);
        servletGateway.setCsrfCookieConfig(csrfCookieConfig);
        servletGateway.setCsrfFormFieldName("csrfToken");

        SymmetricKey key = new SymmetricKey(
                Optional.of("key-1"),
                "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow",
                Use.SIGNATURE
        );

        servletGateway.setSignKey(key);
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

        servletGateway.getCsrfProtect(LoginResource.URL, new LoginResource());
        servletGateway.postCsrfProtect(LoginResource.URL, new LoginResource());
    }
}
