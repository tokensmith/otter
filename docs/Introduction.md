# Introduction

## Contents
- [Scaffolding](#scaffolding)
- [Fundamentals](#fundamentals)
    - [Resource](#resource)
    - [RestResource](#resource)
    - [Session](#session)
    - [User](#user)
    - [Between](#between)
    - [Target](#target)
    - [Group](#group)
    - [RestBetween](#restbetween)
    - [RestTarget](#target)
    - [RestGroup](#group)
- [Configuration](#configuration)
    - [Configure](#configure)
    - [Entry Servlet](#entry-servlet)
    - [Main Method](#main-method)
- [CSRF protection](#csrf)
- [Delivery of static assets](#static-assets)

### Scaffolding

Here is a layout of a project. Which can be observed in the [example application](https://github.com/RootServices/otter/tree/development/example).
```bash
    project/
        src/
            main/
                java/{groupId}.{artifactId}
                    config/
                    server/
                resources/
            webapp/
                public/
                WEB-INF/
                    jsp/    
            test/
```

### Fundamentals
#### Resource
A [Resource](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/controller/Resource.java) is what handles an http request. It can accept any `Content-Type`, it's typically used to render `text/html`.

#### RestResource
A [RestResource]() is designed to accept and respond with the `Content-Type`, `application/json`. Sorry, there is no support for `applicaiton/xml`.
 
#### Session
If an application implements a Resource then it must implement a `Session`.

Sessions in otter are stateless. 
 - There is no state stored on the web server.
 - You can use data stored in the session to retrieve the user's profile, such as a access token or session id.

Session don'ts:
 - Do not put data into the session that may become stale, such as RBAC.
   
Session implementations:
 - Must extend [DefaultSession](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/controller/entity/DefaultSession.java)
 - Must have a copy constructor.
 
The session cookie is a compact JWE. 

**Why JWE?**
 
The threats are: 
 - Session hijacking by modifying values of the session cookie to take over a different session.
 - In the instance the session cookie is revealed then sensitive data is not easily accessible. 
 
#### User
If an application implements a Resource or RestResource then it must implement a `User`.

User implementations:
 - Must extend [DefaultUser](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/controller/entity/DefaultUser.java)

#### Between
A [Between](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/router/entity/between/Between.java) allows a rule to be executed before a request reaches a Resource or after a resource executes. Also referred to as a before and a after.

#### Target
A [Target]() instructs otter which http methods to allow for a given resource and its regex url.

```java
    Target<TokenSession, User> hello = new TargetBuilder<TokenSession, User>()
        .groupName(WEB_SITE_GROUP)
        .method(Method.GET)
        .resource(new HelloResource())
        .regex(HelloResource.URL)
        .build();
```

#### Group
A [Group](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/gateway/entity/Group.java) allows sharing betweens, Session, User, and Error handling amongst Targets.

Sharing error handling.
```java
    var serverErrorResource = new ServerErrorResource();
    Group<TokenSession, User> webSiteGroup = new GroupBuilder<TokenSession, User>()
            .name(WEB_SITE_GROUP)
            .sessionClazz(TokenSession.class)
            .onError(StatusCode.SERVER_ERROR, serverErrorResource)
            .build();
    
    groups.add(webSiteGroup);
```

```java
    Target<TokenSession, User> hello = new TargetBuilder<TokenSession, User>()
        .groupName(WEB_SITE_GROUP)
        .method(Method.GET)
        .resource(new HelloResource())
        .regex(HelloResource.URL)
        .build();
```

All Targets that call, `.groupName(WEB_SITE_GROUP)` will inherit that group's features.

#### RestBetween
A [RestBetween](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/router/entity/between/RestBetween.java) allows a rule to be executed before a request reaches a rest resource or after a rest resource executes. Also referred to as a before and a after.

#### RestTarget

A [RestTarget]() instructs otter which http methods to allow for a given rest resource and its regex url.

```java
    var helloRestResourceV3 = new HelloRestResource();
    RestTarget<ApiUser, Hello> helloApiV3 = new RestTargetBuilder<ApiUser, Hello>()
            .groupName(API_GROUP_V3)
            .method(Method.GET)
            .method(Method.POST)
            .restResource(helloRestResourceV3)
            .regex(helloRestResourceV3.URL)
            .authenticate()
            .contentType(json)
            .payload(Hello.class)
            .build();
```

#### RestGroup
A [RestGroup]() allows sharing rest betweens, User, and Error handling amongst RestTargets.

Sharing error handling..
```java
    BadRequestResource badRequestResource = new BadRequestResource();
    ServerErrorResource serverErrorResource = new ServerErrorResource();
    RestGroup<ApiUser> apiGroupV3 = new RestGroupBuilder<ApiUser>()
            .name(API_GROUP_V3)
            .authRequired(authRestBetween)
            .authOptional(authRestBetween)
            .onError(StatusCode.BAD_REQUEST, badRequestResource, BadRequestPayload.class)
            .onError(StatusCode.SERVER_ERROR, serverErrorResource, ServerErrorPayload.class)
            .build();
```

```java
    var helloRestResourceV3 = new HelloRestResource();
    RestTarget<ApiUser, Hello> helloApiV3 = new RestTargetBuilder<ApiUser, Hello>()
            .groupName(API_GROUP_V3)
            .method(Method.GET)
            .method(Method.POST)
            .restResource(helloRestResourceV3)
            .regex(helloRestResourceV3.URL)
            .authenticate()
            .contentType(json)
            .payload(Hello.class)
            .build();
```

### Configuration

#### Configure
Configuring otter is done by implementing [Configure](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/gateway/Configure.java). 
The implementation instructs otter how to:
 - Set CSRF signature key
 - Set Session encryption signature key
 - Read and Write chunk sizes - use for async i/o.
 - Route requests to Resources
 - Route requests to RestResources
 - Group Resources together to use the same Session and User
 - Group RestResources together to use the same User
 - Handle Errors

Have a look a the hello world application for an [example](https://github.com/RootServices/otter/blob/development/example/src/main/java/org/rootservices/hello/config/AppConfig.java).

#### Entry Servlet
An otter application needs to extend [OtterEntryServlet](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/servlet/OtterEntryServlet.java). This is needed to route requests from the servlet conatiner
to otter. 

It must override the following:
```java
    @Override
    public Configure makeConfigure() {
        return new AppConfig(new AppFactory());
    }
```

`makeConfigure()` must return your `configure` implementation.

Have a look a the hello world application for an [example](https://github.com/RootServices/otter/blob/development/example/src/main/java/org/rootservices/hello/config/AppEntryServlet.java).

#### Main Method

Otter runs in a Jetty powered [embedded servlet container](https://github.com/RootServices/otter/blob/development/example/src/main/java/org.rootservices.hello/server/HelloServer.java).
The port, document root, and the request log are all configurable. The [servlet container factory](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/server/container/ServletContainerFactory.java) 
is how the container is configured to run Otter.

Have a look a the hello world application for an [example](https://github.com/RootServices/otter/blob/development/example/src/main/java/org/rootservices/hello/server/HelloServer.java)

### CSRF

Otter supports CSRF protection by implementing the double submit strategy.

Here is an example of how to protect a login page:

In the configure implementation:
```java
    Target<TokenSession, User> login = new TargetBuilder<TokenSession, User>()
            .groupName(WEB_SITE_GROUP)
            .form()
            .resource(new LoginResource())
            .regex(LoginResource.URL)
            .build();
    
    gateway.add(login);
```

Set the csrf challenge token value on the [login presenter](https://github.com/RootServices/otter/blob/development/example/src/main/java/org.rootservices.hello/controller/presenter/LoginPresenter.java#L18).
```java
    LoginPresenter presenter = new LoginPresenter("", request.getCsrfChallenge().get());
```

Render the [CSRF challenge token](https://github.com/RootServices/otter/blob/development/example/src/main/webapp/WEB-INF/jsp/login.jsp#L12) 
on the page.
```java
    <input id="csrfToken" type="hidden" name="csrfToken" value="${presenter.getCsrfChallengeToken()}" / >
```

### Static Assets

Files that are placed in, `src/main/webapp/public` are public as long as they pass the entry filter [regex](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/servlet/EntryFilter.java#L19)

For example, `src/main/webapp/public/assets/js/jquery-3.3.1.min.js` can be retrieved from, `assets/js/jquery-3.3.1.min.js`