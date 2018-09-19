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
import org.rootservices.otter.gateway.builder.ShapeBuilder;
import org.rootservices.otter.gateway.builder.TargetBuilder;
import org.rootservices.otter.gateway.entity.Label;
import org.rootservices.otter.gateway.entity.Shape;
import org.rootservices.otter.gateway.entity.Target;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;



public class AppConfig implements Configure<TokenSession, User> {
    private AppFactory<TokenSession, User> appFactory;

    public AppConfig(AppFactory<TokenSession, User> appFactory) {
        this.appFactory = appFactory;
    }

    @Override
    public Shape<TokenSession> shape() {
        SymmetricKey encKey = appFactory.encKey();
        SymmetricKey signKey = appFactory.signKey();

        return new ShapeBuilder<TokenSession>()
                .sessionClass(TokenSession.class)
                .secure(false)
                .encKey(encKey)
                .signkey(signKey)
                .build();
    }

    @Override
    public void routes(Gateway<TokenSession, User> gateway) {
        errorRoutes(gateway);
        
        // does not require content-type
        Target<TokenSession, User> hello = new TargetBuilder<TokenSession, User>()
            .method(Method.GET)
            .resource(new HelloResource())
            .regex(HelloResource.URL)
            .build();

        gateway.add(hello);

        // requires content type.
        MimeType json = new MimeTypeBuilder().json().build();
        Target<TokenSession, User> helloAPI = new TargetBuilder<TokenSession, User>()
                .method(Method.GET)
                .method(Method.POST)
                .resource(appFactory.helloRestResource())
                .regex(HelloRestResource.URL)
                .contentType(json)
                .build();

        gateway.add(helloAPI);

        // csrf
        Target<TokenSession, User> login = new TargetBuilder<TokenSession, User>()
                .method(Method.GET)
                .method(Method.POST)
                .resource(new LoginResource())
                .regex(LoginResource.URL)
                .label(Label.CSRF)
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
                .build();

        gateway.add(loginSetSessionResource);

        // session
        Target<TokenSession, User> protectedTarget = new TargetBuilder<TokenSession, User>()
                .method(Method.GET)
                .method(Method.POST)
                .resource(new ProtectedResource())
                .regex(ProtectedResource.URL)
                .label(Label.SESSION_REQUIRED)
                .build();

        gateway.add(protectedTarget);
    }

    public void errorRoutes(Gateway<TokenSession, User> gateway) {
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
