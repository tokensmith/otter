# Introduction


## Contents
- [Resource](#resource)
- [Session](#session)
- [User](#user)
- [Configuration](#configuration)
- [Embedded servlet container](#embedded-container)
- [CSRF protection](#csrf)
- [Stateless with encrypted sessions](#stateless)
- [Async I/O](#async-i/o)
- [Delivery of static assets](#static-assets)

### Resource

A resource is what handles a request. There are two types of resources that Otter supports.
- [Resource](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/controller/Resource.java)
- [RestResource](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/controller/RestResource.java)

A **Resource** is designed to handle any content type. It's typically used to render `text/html`. Have a look at 
[HelloResource](https://github.com/RootServices/otter/blob/development/example/src/main/java/hello/controller/HelloResource.java)
 as an example. 

A **RestResource** is designed to handle `application/json`. Have a look at 
[HelloRestResource](https://github.com/RootServices/otter/blob/development/example/src/main/java/hello/controller/HelloRestResource.java)
as an example.
 
Implementing a resource is rather straight forward.
- Implement a Session and User and assign those as the generics, S, T. 
- Override the methods that handle http methods (get, post, put, delete).
- The response status code must be assigned.
- Specify a public ivar for the url path (in regex notation).
- If needed assign the template path to the response's template path variable.

The examples should be sufficient to get started.

### Session
An application must have a `Session` class. This represents a user session and it 
should be a value object. `Session` implementations are passed into Otter via generics in:
- [Resource](#resource)
- [Configuration](#configuration)
- [Entry Servlet](#entry-servlet)
- [Between](#between)

An application may have many `Session` implementations. 

It must have a copy constructor and a equals method. If either of those are not there Otter will not start up.
 
See [TokenSession](https://github.com/RootServices/otter/blob/development/example/src/main/java/hello/security/TokenSession.java) 
as an example.

### User
A User object must be implemented. It's intent is to represent an authenticated user of the application.
It is passed into Otter via generics in:
- [Resource](#resource)
- [Configuration](#configuration)
- [Entry Servlet](#entry-servlet)
- [Between](#between)

An application may have many `User` implementations.

### Between
A `Between` is a rule that may be executed before a request reaches a resource or after a resoure executes a request.
 Otter uses between implementations for CSRF protection and session management.

### Configuration
Otter needs to be configured for CSRF, Session, and Targets. To configure Otter implement the [Configure](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/gateway/Configure.java)
interface. 

An example can be found in [here](https://github.com/RootServices/otter/blob/development/example/src/main/java/hello/config/AppConfig.java).

##### `shape()`
The implementation of `shape()` must return an instance of `Shape`. Otter uses it to construct `between` instances for
CSRF and Session management.

```java
    // You should vault your keys, this is shown for simplicity.
    SymmetricKey signKey = new SymmetricKey(
        Optional.of("key-1"),
        "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow",
        Use.SIGNATURE
    );
    
    // You should vault your keys, this is shown for simplicity.
    SymmetricKey encKey = new SymmetricKey(
        Optional.of("key-2"),
        "MMNj8rE5m7NIDhwKYDmHSnlU1wfKuVvW6G--GKPYkRA",
        Use.ENCRYPTION
    );
    
    return new ShapeBuilder()
            .secure(false)
            .encKey(encKey)
            .signkey(signKey)
            .build();
```

##### `groups()`
The gateway must be configured with groups. They are used to constuct betweens which satisfy CSRF and 
Session restrictions.

```java
    List<Group<? extends DefaultSession, ? extends DefaultUser>> groups = new ArrayList<>();
    
    Group<TokenSession, DefaultUser> webSiteGroup = new GroupBuilder<TokenSession, DefaultUser>()
            .name(WEB_SITE_GROUP)
            .sessionClazz(TokenSession.class)
            .build();

    groups.add(webSiteGroup);

    AuthRestBetween authLegacyRestBetween = new AuthRestBetween();
    Group<ApiSession, ApiUser> apiGroup = new GroupBuilder<ApiSession, ApiUser>()
            .name(API_GROUP)
            .sessionClazz(ApiSession.class)
            .authRequired(authLegacyRestBetween)
            .build();

    groups.add(apiGroup);
    return groups;
```

##### `routes(Gateway gateway)`
Generally, targets instruct Otter which Resource should handle a given request. Below is an example of a `GET` request 
that will be handled by the `HelloResorce`. 
 
```java
    // get request.
    Target<TokenSession, User> hello = new TargetBuilder<TokenSession, User>()
            .method(Method.GET)
            .resource(new HelloResource())
            .regex(HelloResource.URL)
            .groupName(WEB_SITE_GROUP)
            .build();

    gateway.add(hello);
```

###### Not Found

When Otter cannot find a route to satisfy a request it will use it's `notFoundRoute`.
This should be configured in the `routes(Gateway gateway)` implementation.

```java
    Route<TokenSession, User> notFoundRoute = new RouteBuilder<TokenSession, User>()
        .resource(new NotFoundResource())
        .build();

    gateway.setErrorRoute(StatusCode.NOT_FOUND, notFoundRoute);
```

###### UnSupported Media Type
If desired you can use expected content types when configuring Otter. 
```java
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

```

When a request matches the url and not the content type then the configured error route for `StatusCode.UNSUPPORTED_MEDIA_TYPE`
will be executed.

```java
    Route<TokenSession, User> unSupportedMediaTypeRoute = new RouteBuilder<TokenSession, User>()
        .resource(new UnSupportedMediaTypeResource())
        .build();
    
    gateway.setErrorRoute(StatusCode.UNSUPPORTED_MEDIA_TYPE, unSupportedMediaTypeRoute);
```

###### Server Error
When an unexpected error occurs then otter will execute a server error route. Configuring Otter to do so is shown below.

```java
    Route<TokenSession, User> serverErrorRoute = new RouteBuilder<TokenSession, User>()
        .resource(new ServerErrorResource())
        .build();
    
    gateway.setErrorRoute(StatusCode.SERVER_ERROR, serverErrorRoute);
``` 

### Embedded Container

Otter runs in a Jetty powered [embedded servlet container](https://github.com/RootServices/otter/blob/development/example/src/main/java/hello/server/HelloServer.java).
The port, document root, and the request log are all configurable. The [servlet container factory](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/server/container/ServletContainerFactory.java) 
is how the container is configured to run Otter.

##### Entry Filter
An [entry filter](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/servlet/EntryFilter.java) 
is added for all requests and is configured in the [servlet container factory](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/server/container/ServletContainerFactory.java#L174).
It's responsibility is to determine if requests should be forwarded onto the [entry servlet](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/servlet/OtterEntryServlet.java) 
or the container. The entry servlet will dispatch requests to Otter. The container handles requests to render JSPs and 
static assets. When a request should be handled by the entry servlet then `/app` is prepended to the url path. This is 
needed so the entry servlet the will handle the request. Your resource urls do not need to have `/app` Otter's dispatcher 
ignores `/app` when searching for the resource to handle the request.

##### Entry Servlet

Applications must extend the [entry servlet](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/servlet/OtterEntryServlet.java)
and the implementation must be annotated with following  `@WebSerlvet` annotation.
```java 
    @WebServlet(value="/app/*", name="EntryServlet", asyncSupported = true)
```

- `name` may change to a different value
- `value` must not change.

Extending the [entry servlet](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/servlet/OtterEntryServlet.java) 
is required.

An example can be found in [here](https://github.com/RootServices/otter/blob/development/example/src/main/java/hello/config/AppEntryServlet.java)
Which passes `TokenSession` as the `Session`. 

##### Override `makeConfigure()`
This method must return an instance of the [configruation](#configuration) interface.  

### CSRF 

Otter supports CSRF protection by implementing the double submit strategy.

Here is an example of how to protect a login page:

```java
    Target<TokenSession, User> login = new TargetBuilder<TokenSession, User>()
        .method(Method.GET)
        .method(Method.POST)
        .resource(new LoginResource())
        .regex(LoginResource.URL)
        .label(Label.CSRF)
        .groupName(WEB_SITE_GROUP)
        .build();
    
    gateway.add(login);
```

Set the csrf challenge token value on the [login presenter](https://github.com/RootServices/otter/blob/development/example/src/main/java/hello/controller/presenter/LoginPresenter.java#L18).
```java
    LoginPresenter presenter = new LoginPresenter("", request.getCsrfChallenge().get());
```

Render the [CSRF challenge token](https://github.com/RootServices/otter/blob/development/example/src/main/webapp/WEB-INF/jsp/login.jsp#L12) 
on the page.
```java
    <input id="csrfToken" type="hidden" name="csrfToken" value="${presenter.getCsrfChallengeToken()}" / >
```

### Stateless

Otter is stateless. It maintains user sessions with a cookie whose value is encrypted by using JWE. 

To use them the following is needed:
- Configure the cookie and key.
- Implement a session class. See [session](#session) documentaion.
- Add targets to the servletGateway

#### Session

```java
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
```

### Async I/O
I/O is handled asynchronously. That journey begins in the [OtterEntryServlet](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/servlet/OtterEntryServlet.java#L33).

JSPs also are delivered async via [Jetty](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/server/container/builder/WebAppContextBuilder.java#L82).

### Static Assets

Files that are placed in, `src/main/webapp/public` are public as long as they pass the entry filter [regex](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/servlet/EntryFilter.java#L19)

For example, `src/main/webapp/public/assets/js/jquery-3.3.1.min.js` can be retrieved from, `assets/js/jquery-3.3.1.min.js`