package hello.config;


import hello.controller.*;
import hello.security.SessionBefore;
import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.config.CookieConfig;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.gateway.Gateway;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.session.between.EncryptSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AppConfig implements Configure<TokenSession, User> {
    private AppFactory<TokenSession, User> appFactory;

    public AppConfig(AppFactory<TokenSession, User> appFactory) {
        this.appFactory = appFactory;
    }

    @Override
    public void configure(Gateway<TokenSession, User> gateway) {
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
        EncryptSession<TokenSession, User> encryptSession = appFactory.encryptSession(sessionCookieConfig, encKey);
        gateway.setEncryptSession(encryptSession);
    }

    @Override
    public void routes(Gateway<TokenSession, User> gateway) {
        errorRoutes(gateway);
        
        // does not require content-type
        gateway.get(HelloResource.URL, new HelloResource());

        // requires content type.
        MimeType json = new MimeTypeBuilder().json().build();
        gateway.add(Method.GET, HelloRestResource.URL, appFactory.helloRestResource(), Arrays.asList(json));
        gateway.add(Method.POST, HelloRestResource.URL, appFactory.helloRestResource(), Arrays.asList(json));

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

    public void errorRoutes(Gateway<TokenSession, User> gateway) {
        Route<TokenSession, User> notFoundRoute = new RouteBuilder<TokenSession, User>()
                .resource(new NotFoundResource())
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        gateway.setErrorRoute(StatusCode.NOT_FOUND, notFoundRoute);

        Route<TokenSession, User> unSupportedMediaTypeRoute = new RouteBuilder<TokenSession, User>()
                .resource(new UnSupportedMediaTypeResource())
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        gateway.setErrorRoute(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaTypeRoute);

        Route<TokenSession, User> serverErrorRoute = new RouteBuilder<TokenSession, User>()
                .resource(new ServerErrorResource())
                .before(new ArrayList<>())
                .after(new ArrayList<>())
                .build();

        gateway.setErrorRoute(StatusCode.SERVER_ERROR, serverErrorRoute);
    }
}
