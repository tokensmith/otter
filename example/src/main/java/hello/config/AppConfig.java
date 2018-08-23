package hello.config;


import hello.controller.*;
import hello.security.SessionBefore;
import hello.security.TokenSession;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.gateway.Gateway;
import org.rootservices.otter.router.RouteBuilder;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.session.between.EncryptSession;

import java.util.ArrayList;
import java.util.HashMap;

public class AppConfig implements Configure<TokenSession> {
    private AppFactory<TokenSession> appFactory;

    public AppConfig(AppFactory<TokenSession> appFactory) {
        this.appFactory = appFactory;
    }

    @Override
    public void configure(Gateway<TokenSession> gateway) {
        // csrf
        CookieConfig csrfCookieConfig = new CookieConfig("csrf", false, -1);
        gateway.setCsrfCookieConfig(csrfCookieConfig);
        gateway.setCsrfFormFieldName("csrfToken");

        SymmetricKey signkey = appFactory.signKey();
        gateway.setSignKey(signkey);

        // session
        CookieConfig sessionCookieConfig = new CookieConfig("session", false, -1);
        SymmetricKey encKey = appFactory.encKey();

        SessionBefore sessionBeforeBetween = appFactory.sessionBefore("session", encKey, new HashMap<>());
        gateway.setDecryptSession(sessionBeforeBetween);

        // TODO: should this be named differently? it is inconsistent with sessionBefore.
        EncryptSession<TokenSession> encryptSession = appFactory.encryptSession(sessionCookieConfig, encKey);
        gateway.setEncryptSession(encryptSession);
    }

    @Override
    public void routes(Gateway<TokenSession> gateway) {
        Route<TokenSession> notFoundRoute = new RouteBuilder<TokenSession>()
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
        gateway.getCsrfProtect(LoginResource.URL, login);
        gateway.postCsrfProtect(LoginResource.URL, login);

        // csrf & session
        LoginSessionResource loginWithSession = new LoginSessionResource();
        gateway.getCsrfAndSessionProtect(LoginSessionResource.URL, loginWithSession);
        gateway.postCsrfAndSessionProtect(LoginSessionResource.URL, loginWithSession);

        // set session
        LoginSetSessionResource loginSetSessionResource = new LoginSetSessionResource();
        gateway.getCsrfProtect(LoginSetSessionResource.URL, loginSetSessionResource);
        gateway.postCsrfAndSetSession(LoginSetSessionResource.URL, loginSetSessionResource);

        // session
        gateway.getSessionProtect(ProtectedResource.URL, new ProtectedResource());
        gateway.postSessionProtect(ProtectedResource.URL, new ProtectedResource());
    }
}
