package hello.config;


import hello.controller.*;
import hello.controller.api.HelloRestResource;
import hello.controller.api.between.AuthRestBetween;
import hello.controller.api.model.ApiSession;
import hello.controller.api.model.ApiUser;
import hello.model.Hello;
import hello.security.TokenSession;
import hello.security.User;
import org.rootservices.jwt.entity.jwk.SymmetricKey;
import org.rootservices.otter.controller.builder.MimeTypeBuilder;
import org.rootservices.otter.controller.entity.EmptyPayload;
import org.rootservices.otter.controller.entity.DefaultSession;
import org.rootservices.otter.controller.entity.DefaultUser;
import org.rootservices.otter.controller.entity.StatusCode;
import org.rootservices.otter.controller.entity.mime.MimeType;
import org.rootservices.otter.gateway.Configure;
import org.rootservices.otter.gateway.Gateway;
import org.rootservices.otter.gateway.builder.GroupBuilder;
import org.rootservices.otter.gateway.builder.ShapeBuilder;
import org.rootservices.otter.gateway.builder.target.HtmlTargetBuilder;
import org.rootservices.otter.gateway.builder.target.RestTargetBuilder;
import org.rootservices.otter.gateway.builder.target.TargetBuilder;
import org.rootservices.otter.gateway.entity.Group;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.gateway.entity.Shape;
import org.rootservices.otter.gateway.entity.target.HtmlTarget;
import org.rootservices.otter.gateway.entity.target.RestTarget;
import org.rootservices.otter.gateway.entity.target.Target;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;
import org.rootservices.otter.translatable.Translatable;


import java.util.ArrayList;
import java.util.List;


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
    public List<Group<? extends DefaultSession, ? extends DefaultUser, ? extends Translatable>> groups() {
        List<Group<? extends DefaultSession, ? extends DefaultUser, ? extends Translatable>> groups = new ArrayList<>();

        Group<TokenSession, DefaultUser, EmptyPayload> webSiteGroup = new GroupBuilder<TokenSession, DefaultUser, EmptyPayload>()
                .name(WEB_SITE_GROUP)
                .sessionClazz(TokenSession.class)
                .build();

        groups.add(webSiteGroup);

        AuthRestBetween authRestBetween = new AuthRestBetween();
        Group<ApiSession, ApiUser, EmptyPayload> apiGroup = new GroupBuilder<ApiSession, ApiUser, EmptyPayload>()
                .name(API_GROUP)
                .sessionClazz(ApiSession.class)
                .authRequired(authRestBetween)
                .build();

        groups.add(apiGroup);
        return groups;
    }

    @Override
    public void routes(Gateway gateway) {
        errorRoutes(gateway);

        // requires content type.
        MimeType json = new MimeTypeBuilder().json().build();
        RestTarget<ApiSession, ApiUser, Hello> helloAPI = new RestTargetBuilder<ApiSession, ApiUser, Hello>()
                .method(Method.GET)
                .method(Method.POST)
                .resource(appFactory.helloRestResource())
                .regex(HelloRestResource.URL)
                .payload(Hello.class)
                .label(Label.AUTH_REQUIRED)
                .contentType(json)
                .groupName(API_GROUP)
                .build();

        gateway.add(helloAPI);

        // does not require content-type
        HtmlTarget<TokenSession, User> hello = new HtmlTargetBuilder<TokenSession, User>()
            .method(Method.GET)
            .resource(new HelloResource())
            .regex(HelloResource.URL)
            .groupName(WEB_SITE_GROUP)
            .build();

        gateway.add(hello);

        // csrf
        HtmlTarget<TokenSession, User> login = new HtmlTargetBuilder<TokenSession, User>()
                .method(Method.GET)
                .method(Method.POST)
                .resource(new LoginResource())
                .regex(LoginResource.URL)
                .label(Label.CSRF)
                .groupName(WEB_SITE_GROUP)
                .build();

        gateway.add(login);

        // csrf & session
        HtmlTarget<TokenSession, User> loginWithSession = new HtmlTargetBuilder<TokenSession, User>()
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
        HtmlTarget<TokenSession, User> loginSetSessionResource = new HtmlTargetBuilder<TokenSession, User>()
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
        HtmlTarget<TokenSession, User> protectedTarget = new HtmlTargetBuilder<TokenSession, User>()
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
        Route<TokenSession, User, EmptyPayload> notFoundRoute = new RouteBuilder<TokenSession, User, EmptyPayload>()
                .resource(new NotFoundResource())
                .build();

        gateway.setErrorRoute(StatusCode.NOT_FOUND, notFoundRoute);

        Route<TokenSession, User, EmptyPayload> unSupportedMediaTypeRoute = new RouteBuilder<TokenSession, User, EmptyPayload>()
                .resource(new UnSupportedMediaTypeResource())
                .build();

        gateway.setErrorRoute(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaTypeRoute);

        Route<TokenSession, User, EmptyPayload> serverErrorRoute = new RouteBuilder<TokenSession, User, EmptyPayload>()
                .resource(new ServerErrorResource())
                .build();

        gateway.setErrorRoute(StatusCode.SERVER_ERROR, serverErrorRoute);
    }
}
