package hello.config;


import hello.controller.*;
import hello.controller.api.HelloLegacyRestResource;
import hello.controller.api.HelloRestResource;
import hello.controller.api.between.AuthLegacyRestBetween;
import hello.controller.api.between.AuthRestBetween;
import hello.controller.api.model.ApiSession;
import hello.controller.api.model.ApiUser;
import hello.model.Hello;
import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.gateway.Gateway;
import org.rootservices.otter.gateway.builder.*;
import org.rootservices.otter.gateway.entity.*;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;


import java.util.ArrayList;
import java.util.List;


public class AppConfig implements Configure {
    public static final String API_GROUP = "API";
    public static final String API_GROUP_V2 = "API_V2";
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
    public List<Group<? extends DefaultSession, ? extends DefaultUser>> groups() {
        List<Group<? extends DefaultSession, ? extends DefaultUser>> groups = new ArrayList<>();

        Group<TokenSession, DefaultUser> webSiteGroup = new GroupBuilder<TokenSession, DefaultUser>()
                .name(WEB_SITE_GROUP)
                .sessionClazz(TokenSession.class)
                .build();

        groups.add(webSiteGroup);

        AuthLegacyRestBetween authLegacyRestBetween = new AuthLegacyRestBetween();
        Group<ApiSession, ApiUser> apiGroup = new GroupBuilder<ApiSession, ApiUser>()
                .name(API_GROUP)
                .sessionClazz(ApiSession.class)
                .authRequired(authLegacyRestBetween)
                .build();

        groups.add(apiGroup);

        return groups;
    }

    @Override
    public List<RestGroup<? extends DefaultUser>> restGroups() {
        List<RestGroup<? extends DefaultUser>> restGroups = new ArrayList<>();

        AuthRestBetween authRestBetween = new AuthRestBetween();
        RestGroup<ApiUser> apiGroupV2 = new RestGroupBuilder<ApiUser>()
                .name(API_GROUP_V2)
                .authRequired(authRestBetween)
                .build();

        restGroups.add(apiGroupV2);

        return restGroups;
    }

    @Override
    public void routes(Gateway gateway) {
        errorRoutes(gateway);

        // Legacy Rest - requires content type.
        MimeType json = new MimeTypeBuilder().json().build();
        Target<ApiSession, ApiUser> helloAPI = new TargetBuilder<ApiSession, ApiUser>()
                .method(Method.GET)
                .method(Method.POST)
                .resource(appFactory.helloLegacyRestResource())
                .regex(HelloLegacyRestResource.URL)
                .label(Label.AUTH_REQUIRED)
                .contentType(json)
                .groupName(API_GROUP)
                .build();

        gateway.add(helloAPI);

        RestTarget<ApiUser, Hello> helloApiV2 = new RestTargetBuilder<ApiUser, Hello>()
                .method(Method.GET)
                .method(Method.POST)
                .restResource(new HelloRestResource())
                .regex(HelloRestResource.URL)
                .label(Label.AUTH_REQUIRED)
                .contentType(json)
                .groupName(API_GROUP_V2)
                .payload(Hello.class)
                .build();

        gateway.add(helloApiV2);

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
