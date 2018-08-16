package hello.config;


import hello.controller.*;
import hello.security.SessionBefore;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.controller.header.ContentType;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.gateway.Gateway;
import org.rootservices.otter.router.RouteBuilder;
import org.rootservices.otter.router.entity.Route;

import java.util.ArrayList;
import java.util.HashMap;

public class AppConfig implements Configure {
    private AppFactory appFactory;

    public AppConfig(AppFactory appFactory) {
        this.appFactory = appFactory;
    }

    @Override
    public void configure(Gateway gateway) {
        // csrf
        CookieConfig csrfCookieConfig = new CookieConfig("csrf", false, -1);
        gateway.setCsrfCookieConfig(csrfCookieConfig);
        gateway.setCsrfFormFieldName("csrfToken");

        SymmetricKey signkey = appFactory.signKey();
        gateway.setSignKey(signkey);

        // session
        CookieConfig sessionCookieConfig = new CookieConfig("session", false, -1);
        SymmetricKey encKey = appFactory.encKey();

        gateway.setSessionCookieConfig(sessionCookieConfig);
        gateway.setEncKey(encKey);

        SessionBefore sessionBeforeBetween = appFactory.sessionBefore("session", encKey, new HashMap<>());
        gateway.setDecryptSession(sessionBeforeBetween);
    }

    @Override
    public void routes(Gateway gateway) {
        Route notFoundRoute = new RouteBuilder()
                .resource(new NotFoundResource())
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        gateway.setNotFoundRoute(notFoundRoute);

        MimeType json = new MimeTypeBuilder().json().build();

        gateway.get(HelloResource.URL, new HelloResource());
        gateway.get(HelloRestResource.URL, appFactory.helloRestResource());
        gateway.post(HelloRestResource.URL, appFactory.helloRestResource());

        // csrf
        LoginResource login = new LoginResource();
        gateway.getCsrfProtect(login.URL, login);
        gateway.postCsrfProtect(login.URL, login);

        // csrf & session
        LoginSessionResource loginWithSession = new LoginSessionResource();
        gateway.getCsrfAndSessionProtect(loginWithSession.URL, loginWithSession);
        gateway.postCsrfAndSessionProtect(loginWithSession.URL, loginWithSession);

        // set session
        LoginSetSessionResource loginSetSessionResource = new LoginSetSessionResource();
        gateway.getCsrfProtect(LoginSetSessionResource.URL, loginSetSessionResource);
        gateway.postCsrfAndSetSession(LoginSetSessionResource.URL, loginSetSessionResource);

        // session
        gateway.getSessionProtect(ProtectedResource.URL, new ProtectedResource());
        gateway.postSessionProtect(ProtectedResource.URL, new ProtectedResource());
    }
}
