# Documentation

## Contents
- [Scaffolding](#scaffolding)
- [Fundamentals](#fundamentals)
    - [Resource](#resource)
    - [RestResource](#resource)
    - [Between](#between)
    - [Target](#target)
    - [Group](#group)
    - [RestBetween](#restbetween)
    - [RestTarget](#target)
    - [RestGroup](#group)
- [Authentication](#authentication)
    - [Session](#session)
    - [User](#user)
    - [Required Authentication](#required-authentication-between)
    - [Optional Authentication](#optional-authentication-between)
    - [Resource Authentication](#resource-authentication)
    - [RestRestource Authentication](#restresource-authentication)    
- [Error Handling](#error-handling)
- [Configuration](#configuration)
    - [Configure](#configure)
    - [Entry Servlet](#entry-servlet)
    - [Main Method](#main-method)
- [CSRF protection](#csrf)
- [Delivery of static assets](#static-assets)

### Scaffolding

Here is a layout of a project. Which can be observed in the [hello world application](https://github.com/RootServices/otter/tree/development/hello-world).
```bash
    project/
        src/
            main/
                java/{groupId}.{artifactId}
                    config/
                        AppConfig.java
                        AppEntryServlet.java
                    server/
                        AppServer.java
                resources/
            webapp/
                public/
                WEB-INF/
                    jsp/    
            test/
```

`AppConfig.java` contains the [configuration](#configuration) to set up your web application.

`AppEntryServlet.java` allows [servlet container requests](#entry-servlet) to be sent to otter.

`AppServer.java` the application's [main method](#main-method) to start the web application.

### Fundamentals
#### Resource
A [Resource](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/controller/Resource.java) is what handles an http request. It can accept any `Content-Type`, it's typically used to render `text/html`.

#### RestResource
A [RestResource](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/controller/RestResource.java) is designed to accept and respond with the `Content-Type`, `application/json`. Sorry, there is no support for `applicaiton/xml`.

#### Between
A [Between](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/router/entity/between/Between.java) allows a rule to be executed before a request reaches a Resource or after a Resource executes. Also referred to as a before and a after.

#### Target
A [Target](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/gateway/entity/Target.java) instructs otter which http methods to allow for a given resource and its regex url.

```java
    Target<TokenSession, User> hello = new TargetBuilder<TokenSession, User>()
        .groupName(WEB_SITE_GROUP)
        .method(Method.GET)
        .resource(new HelloResource())
        .regex(HelloResource.URL)
        .build();
```

#### Group
A [Group](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/gateway/entity/Group.java) allows sharing Session, User, and Error handling amongst Targets.

Sharing error handling.
```java
    var serverErrorResource = new org.rootservices.hello.controller.html.ServerErrorResource();
    Group<TokenSession, User> webSiteGroup = new GroupBuilder<TokenSession, User>()
            .name(WEB_SITE_GROUP)
            .sessionClazz(TokenSession.class)
            .authOptional(new AuthOptBetween())
            .authRequired(new AuthBetween())
            .onError(StatusCode.SERVER_ERROR, serverErrorResource)
            .build();
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
A [RestBetween](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/router/entity/between/RestBetween.java) allows a rule to be executed before a request reaches a RestResource or after a RestResource executes. Also referred to as a before and a after.

#### RestTarget

A [RestTarget](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/gateway/entity/rest/RestTarget.java) instructs otter which http methods to allow for a given rest resource and its regex url.

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
A [RestGroup](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/gateway/entity/rest/RestGroup.java) allows sharing User and Error handling amongst RestTargets.

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

### Authentication
Authentication in otter is dependent on the value objects:
 - [Session](#session)
 - [User](#user)

Next, two different authentication betweens are needed which are configured in `Group` and `RestGroup`.
 - [required authentication](#required-authentication-between)
 - [optional authentication](#optional-authentication-between)
 
#### Session
If an application that uses Resources and also needs to have authentication then it must implement a `Session`. A 
`Session` is a cookie that is `http-only` and it's value is a `JWE`.

Sessions in otter are stateless. 
 - There is no state stored on the web server.
 - You can use data stored in the session to retrieve the user's profile, such as a access token or session id.

Session don'ts:
 - Do not put data into the session that may become stale, such as RBAC.
   
Session implementations:
 - Must extend [DefaultSession](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/controller/entity/DefaultSession.java)
 - Must have a copy constructor.

**Why JWE?**
 
The threats are: 
 - Session hijacking by modifying values of the session cookie to take over a different session.
 - In the instance the session cookie is revealed then sensitive data is not easily accessible. 
 
#### User
If an application implements a Resource or RestResource and also needs to have authentication then it must implement a `User`.

User implementations:
 - Must extend [DefaultUser](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/controller/entity/DefaultUser.java)

#### Required Authentication Between
Given the required authentication between

When authentication succeeds
Then assign the request user to the appropriate user
 
When authentication fails
Then possibly set the status code to 401
And throw a halt exception. 
           

#### Optional Authentication Between
Given the optional authentication between

When the Session is present 
and authentication succeeds
Then assign the request user to the appropriate user
 
When the Session is present 
and authentication fails
Then possibly set the status code to 401
And throw a halt exception. 

When the Session is not present 
Then all the request to reach the resource. 


#### Resource Authentication
```java
    var serverErrorResource = new org.rootservices.hello.controller.html.ServerErrorResource();
    Group<TokenSession, User> webSiteGroup = new GroupBuilder<TokenSession, User>()
            .name(WEB_SITE_GROUP)
            .sessionClazz(TokenSession.class)
            .authOptional(new AuthOptBetween())
            .authRequired(new AuthBetween())
            .onError(StatusCode.SERVER_ERROR, serverErrorResource)
            .build();

    groups.add(webSiteGroup);
```

Then, to require authentication for a Resource use, `.authenticate()`.

```java
    Target<TokenSession, User> hello = new TargetBuilder<TokenSession, User>()
        .groupName(WEB_SITE_GROUP)
        .method(Method.GET)
        .resource(new HelloResource())
        .regex(HelloResource.URL)
        .authenticate()
        .build();
```

If, `authenticate()` is not used, then it will use the optional authenticate between.

#### RestResource Authentication

```java
    AuthRestBetween authRestBetween = new AuthRestBetween();

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

Then, to require authentication for a Resource use, `.authenticate()`.

```java
    var helloRestResourceV3 = new org.rootservices.hello.controller.api.v3.HelloRestResource();
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

If, `authenticate()` is not used, then it will use the optional authenticate between.

### Error Handling

#### Resource
In the event that an unexpected error occurs in a Resource it can be handled by configuring the Group or Target with 
how to react to the error.

#### RestResource
In the event that an unexpected, unsupported media type, or bad request error occurs in a Resource it can be handled by 
configuring the RestGroup or RestTarget with how to react to the error.

### Configuration

### Not Founds
To configure how to handle urls that are not found use the interface, `gateway.notFound(..)` for both `Target` and 
`RestTarget`. The regex must be specified which will be used to determine which `Resource` or `RestResouce` to execute.
This allows applications to have many ways to react to a not found url based on the url regex.

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

Have a look a the hello world application for an [example](https://github.com/RootServices/otter/blob/development/hello-world/src/main/java/org/rootservices/hello/config/AppConfig.java).

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

Have a look a the hello world application for an [example](https://github.com/RootServices/otter/blob/development/hello-world/src/main/java/org/rootservices/hello/config/AppEntryServlet.java).

#### Main Method

Otter runs in a Jetty powered [embedded servlet container](https://github.com/RootServices/otter/blob/development/hello-world/src/main/java/org/rootservices/hello/server/HelloServer.java).
The port, document root, and the request log are all configurable.

Have a look a the hello world application for an [example](https://github.com/RootServices/otter/blob/development/hello-world/src/main/java/org/rootservices/hello/server/HelloServer.java)

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

In the [Login Resource](https://github.com/RootServices/otter/blob/development/hello-world/src/main/java/org/rootservices/hello/controller/html/LoginResource.java#L20) 
set the csrf challenge token to the appropriate ivar in the [login presenter](https://github.com/RootServices/otter/blob/development/hello-world/src/main/java/org/rootservices/hello/controller/html/presenter/LoginPresenter.java).
```java
    LoginPresenter presenter = new LoginPresenter("", request.getCsrfChallenge().get());
```

Render the [CSRF challenge token](https://github.com/RootServices/otter/blob/development/hello-world/src/main/webapp/WEB-INF/jsp/login.jsp#L12) 
on the page.
```java
    <input id="csrfToken" type="hidden" name="csrfToken" value="${presenter.getCsrfChallengeToken()}" / >
```

Done, it is CSRF protected.
### Static Assets

Files that are placed in, `src/main/webapp/public` are public as long as they pass the entry filter [regex](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/servlet/EntryFilter.java#L18)

For example, `src/main/webapp/public/assets/js/jquery-3.3.1.min.js` can be retrieved from, `assets/js/jquery-3.3.1.min.js`