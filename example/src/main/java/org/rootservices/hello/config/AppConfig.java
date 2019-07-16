package org.rootservices.hello.config;


import org.rootservices.hello.controller.*;
import org.rootservices.hello.controller.api.between.AuthRestBetween;
import org.rootservices.hello.controller.api.model.ApiUser;

import org.rootservices.hello.controller.api.v2.HelloRestResource;
import org.rootservices.hello.controller.api.v3.BrokenRestResource;
import org.rootservices.hello.controller.api.v3.handler.BadRequestResource;
import org.rootservices.hello.controller.api.v3.handler.ServerErrorResource;
import org.rootservices.hello.controller.api.v3.model.BadRequestPayload;
import org.rootservices.hello.controller.api.v3.model.BrokenPayload;
import org.rootservices.hello.controller.api.v3.model.ServerErrorPayload;
import org.rootservices.hello.model.Hello;
import org.rootservices.hello.security.TokenSession;
import org.rootservices.hello.security.User;
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
import org.rootservices.otter.gateway.entity.rest.RestGroup;
import org.rootservices.otter.gateway.entity.rest.RestTarget;
import org.rootservices.otter.router.builder.RouteBuilder;
import org.rootservices.otter.router.entity.Method;
import org.rootservices.otter.router.entity.Route;


import java.util.ArrayList;
import java.util.List;


public class AppConfig implements Configure {
    public static final String API_GROUP_V2 = "API_V2";
    public static final String API_GROUP_V3 = "API_V3";
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

        org.rootservices.hello.controller.ServerErrorResource serverErrorResource = new org.rootservices.hello.controller.ServerErrorResource();
        Group<TokenSession, DefaultUser> webSiteGroup = new GroupBuilder<TokenSession, DefaultUser>()
                .name(WEB_SITE_GROUP)
                .sessionClazz(TokenSession.class)
                .onError(StatusCode.SERVER_ERROR, serverErrorResource)
                .build();

        groups.add(webSiteGroup);

        return groups;
    }

    @Override
    public List<RestGroup<? extends DefaultUser>> restGroups() {
        List<RestGroup<? extends DefaultUser>> restGroups = new ArrayList<>();

        AuthRestBetween authRestBetween = new AuthRestBetween();

        // uses default bad request handling.
        RestGroup<ApiUser> apiGroupV2 = new RestGroupBuilder<ApiUser>()
                .name(API_GROUP_V2)
                .authRequired(authRestBetween)
                .build();

        restGroups.add(apiGroupV2);

        // has overrides for error handling.
        BadRequestResource badRequestResource = new BadRequestResource();
        ServerErrorResource serverErrorResource = new ServerErrorResource();
        RestGroup<ApiUser> apiGroupV3 = new RestGroupBuilder<ApiUser>()
                .name(API_GROUP_V3)
                .authRequired(authRestBetween)
                .onError(StatusCode.BAD_REQUEST, badRequestResource, BadRequestPayload.class)
                .onError(StatusCode.SERVER_ERROR, serverErrorResource, ServerErrorPayload.class)
                .build();

        restGroups.add(apiGroupV3);

        return restGroups;
    }

    @Override
    public void routes(Gateway gateway) {
        errorRoutes(gateway);


        MimeType json = new MimeTypeBuilder().json().build();

        // resource for v2 api
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

        // resource for v3 api
        org.rootservices.hello.controller.api.v3.HelloRestResource helloRestResourceV3 = new org.rootservices.hello.controller.api.v3.HelloRestResource();
        RestTarget<ApiUser, Hello> helloApiV3 = new RestTargetBuilder<ApiUser, Hello>()
                .method(Method.GET)
                .method(Method.POST)
                .restResource(helloRestResourceV3)
                .regex(helloRestResourceV3.URL)
                .label(Label.AUTH_REQUIRED)
                .contentType(json)
                .groupName(API_GROUP_V3)
                .payload(Hello.class)
                .build();

        gateway.add(helloApiV3);

        // this will always throw a runtime exception and force the error handler.
        BrokenRestResource brokenRestResource = new BrokenRestResource();
        RestTarget<ApiUser, BrokenPayload> brokenApiV3 = new RestTargetBuilder<ApiUser, BrokenPayload>()
                .method(Method.GET)
                .method(Method.POST)
                .method(Method.PATCH)
                .method(Method.PUT)
                .method(Method.DELETE)
                .restResource(brokenRestResource)
                .regex(brokenRestResource.URL)
                .label(Label.AUTH_REQUIRED)
                .contentType(json)
                .groupName(API_GROUP_V3)
                .payload(BrokenPayload.class)
                .build();

        gateway.add(brokenApiV3);

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

        // should be handled by server error resource.
        Target<TokenSession, User> exceptionTarget = new TargetBuilder<TokenSession, User>()
                .method(Method.GET)
                .method(Method.POST)
                .resource(new RunTimeExceptionResource())
                .regex(RunTimeExceptionResource.URL)
                .groupName(WEB_SITE_GROUP)
                .build();

        gateway.add(exceptionTarget);
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

        // TODO: server error handling.
    }
}
