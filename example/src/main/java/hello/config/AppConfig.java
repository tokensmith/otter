package hello.config;


import hello.controller.*;
import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.gateway.Gateway;
import org.rootservices.otter.gateway.builder.GroupBuilder;
import org.rootservices.otter.gateway.builder.ShapeBuilder;
import org.rootservices.otter.gateway.builder.TargetBuilder;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.gateway.entity.Shape;
import org.rootservices.otter.gateway.entity.Target;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.security.exception.SessionCtorException;


public class AppConfig implements Configure {
    public static final String API_GROUP = "API";
    public static final String WEB_SITE_GROUP = "WebSite";
    private AppFactory appFactory;

    public AppConfig(AppFactory appFactory) {
        this.appFactory = appFactory;
    }

    @Override
    public Shape shape() {
        SymmetricKey encKey = appFactory.encKey();
        SymmetricKey signKey = appFactory.signKey();

        return new ShapeBuilder()
                .secure(false)
                .encKey(encKey)
                .signkey(signKey)
                .build();
    }

    @Override
    public void groups(Gateway gateway) throws SessionCtorException {
        Group<TokenSession, User> webSiteGroup = new GroupBuilder<TokenSession, User>()
                .name(WEB_SITE_GROUP)
                .sessionClazz(TokenSession.class)
                .build();

        gateway.group(webSiteGroup);

        Group<TokenSession, User> apiGroup = new GroupBuilder<TokenSession, User>()
                .name(API_GROUP)
                .sessionClazz(TokenSession.class)
                .build();

        gateway.group(apiGroup);
    }

    @Override
    public void routes(Gateway gateway) {
        errorRoutes(gateway);

        // requires content type.
        MimeType json = new MimeTypeBuilder().json().build();
        Target<TokenSession, User> helloAPI = new TargetBuilder<TokenSession, User>()
                .method(Method.GET)
                .method(Method.POST)
                .resource(appFactory.helloRestResource())
                .regex(HelloRestResource.URL)
                .contentType(json)
                .groupName(API_GROUP)
                .build();

        gateway.add(helloAPI);

        // does not require content-type
        Target<TokenSession, User> hello = new TargetBuilder<TokenSession, User>()
            .method(Method.GET)
            .resource(new HelloResource())
            .regex(HelloResource.URL)
            .groupName(WEB_SITE_GROUP)
            .build();

        gateway.add(hello);

        // csrf
        Target<TokenSession, User> login = new TargetBuilder<TokenSession, User>()
                .method(Method.GET)
                .method(Method.POST)
                .resource(new LoginResource())
                .regex(LoginResource.URL)
                .label(Label.CSRF)
                .groupName(WEB_SITE_GROUP)
                .build();

        gateway.add(login);

        // csrf & session
        Target<TokenSession, User> loginWithSession = new TargetBuilder<TokenSession, User>()
                .method(Method.GET)
                .method(Method.POST)
                .resource(new LoginSessionResource())
                .regex(LoginSessionResource.URL)
                .label(Label.CSRF)
                .label(Label.SESSION_REQUIRED)
                .groupName(WEB_SITE_GROUP)
                .build();

        gateway.add(loginWithSession);

        // set session
        Target<TokenSession, User> loginSetSessionResource = new TargetBuilder<TokenSession, User>()
                .method(Method.GET)
                .method(Method.POST)
                .resource(new LoginSetSessionResource())
                .regex(LoginSetSessionResource.URL)
                .label(Label.CSRF)
                .label(Label.SESSION_OPTIONAL)
                .groupName(WEB_SITE_GROUP)
                .build();

        gateway.add(loginSetSessionResource);

        // session
        Target<TokenSession, User> protectedTarget = new TargetBuilder<TokenSession, User>()
                .method(Method.GET)
                .method(Method.POST)
                .resource(new ProtectedResource())
                .regex(ProtectedResource.URL)
                .label(Label.SESSION_REQUIRED)
                .groupName(WEB_SITE_GROUP)
                .build();

        gateway.add(protectedTarget);
    }

    public void errorRoutes(Gateway gateway) {
        Route<TokenSession, User> notFoundRoute = new RouteBuilder<TokenSession, User>()
                .resource(new NotFoundResource())
                .build();

        gateway.setErrorRoute(StatusCode.NOT_FOUND, notFoundRoute);

        Route<TokenSession, User> unSupportedMediaTypeRoute = new RouteBuilder<TokenSession, User>()
                .resource(new UnSupportedMediaTypeResource())
                .build();

        gateway.setErrorRoute(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaTypeRoute);

        Route<TokenSession, User> serverErrorRoute = new RouteBuilder<TokenSession, User>()
                .resource(new ServerErrorResource())
                .build();

        gateway.setErrorRoute(StatusCode.SERVER_ERROR, serverErrorRoute);
    }
}
