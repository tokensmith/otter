# otter
Otter is a micro web framework that sits on top of the servlet api 4.0 

- [Dependency Coordinates](#dependency-coordinates)
- [Example Application](#example-application)
- [Introduction](#introduction)

## Dependency coordinates
#### Maven
```xml
<dependencies>
    <dependency>
        <groupId>org.rootservices</groupId>
        <artifactId>otter</artifactId>
        <version>1.2-SNAPSHOT</version>
    </dependency>
</dependencies>
```

#### Gradle
```groovy
compile group: 'org.rootservices', name: 'otter', version: '1.2-SNAPSHOT'
```

## Example Application
A [hello world](https://github.com/RootServices/hello-world) example is available which demonstrates CSRF, Rest, and tex/html responses.
A clone of the hello world application is included in the test suite and will be referenced throughout the documentation. 

## Introduction
- [Resource](#resource)
- [Embedded servlet container](#embedded-container)
- [Regex routing](#routing)
- [Restful support](#resources)
- [CSRF protection](#csrf)
- [Stateless with encrypted sessions](#stateless)
- [Async I/O](#async-i/o)
- [Delivery of static assets](#static-assets)

### Resource

A resource is what handles a request. There are two types of resources that Otter supports.
- [Resource](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/controller/Resource.java)
- [RestResource](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/controller/RestResource.java)

Resource is designed to handle any content type, typically used to render `text/html`. A simple [example](https://github.com/RootServices/otter/blob/57/src/test/java/integration/app/hello/controller/HelloResource.java) can be found in the test suite. 

RestResource is designed to handle `application/json`. A simple [example](https://github.com/RootServices/otter/blob/57/src/test/java/integration/app/hello/controller/HelloRestResource.java) can be found in the test suite as well.

Implementing a resource is rather straight forward. 
- Override the methods that handle http methods (get, post, put, delete).
- The response status code must be assigned.
- Specify a public ivar for the url path (in regex notation).
- If needed assign the template path to the response's template path variable.

The examples should be sufficient to get started.
 
### Embedded Container

Otter can run in a Jetty powered [embedded servlet container](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/server/HelloServer.java).
The embedded container can be configured to specify the port, document root, and the location of the request log.
The [servlet container factory](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/server/container/ServletContainerFactory.java) is how the container is configured to run Otter.

##### Entry Filter
An [entry filter](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/servlet/EntryFilter.java) 
is added for all requests and is configured do so in the [servlet container factory](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/server/container/ServletContainerFactory.java#L153).
It's responsibility is to determine if requests should be forwarded onto the [entry servlet](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/EntryServlet.java) or the container. 
The entry servlet will dispatch the request to Otter. The container handles requests to render JSPs and static assets. 
When a request should be handled by the entry servlet then `/app` is prepended to the url path. This is needed 
so the entry servlet the will handle the request.

##### Entry Servlet

Applications must extend the [entry servlet](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/EntryServlet.java) and the implementation must be annotated with following  `@WebSerlvet` annotation.
```java 
    @WebServlet(value="/app/*", name="EntryServlet", asyncSupported = true)
```

- `name` may change to a different value
- `value` must not change.

Extending the [entry servlet](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/EntryServlet.java) 
is required. Your implementation will configure the [servlet gateway](https://github.com/RootServices/otter/blob/57/src/main/java/org/rootservices/otter/gateway/servlet/ServletGateway.java)
which instructs Otter how to route your requests to Resources.

###### Override `init`
Your entry servlet must override `init` and it's first line must execute `super.init()`. If you would like to use CSRF 
and sessions then they should be configured in `init` as well.

First, start by passing in the cookie configuration for CSRF and Session.

```java
    // CSRF cookie configuration
    CookieConfig csrfCookieConfig = new CookieConfig("csrf", false, -1);
    servletGateway.setCsrfCookieConfig(csrfCookieConfig);

    // Session cookie configuration.
    CookieConfig sessionCookieConfig = new CookieConfig("session", false, -1);
    servletGateway.setSessionCookieConfig(sessionCookieConfig);
```

Next, configure the Symmetric keys.

```java
    // CSRF key configuration.
    SymmetricKey csrfKey = new SymmetricKey(
        Optional.of("key-1"),
        "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow",
        Use.SIGNATURE
    );

    servletGateway.setSignKey(csrfKey);

    //Session key configuration.
    SymmetricKey encKey = new SymmetricKey(
        Optional.of("key-2"),
        "MMNj8rE5m7NIDhwKYDmHSnlU1wfKuVvW6G--GKPYkRA",
        Use.ENCRYPTION
    );

    servletGateway.setEncKey(key);
```

Lastly, configuration is needed for routes.

### Routing
Generally, routes instruct Otter which Resource should handle a given request.
  
The example entry servlet has a method `routes()` which is called in `init()`.
 

```java
    // route a get request.
    servletGateway.get(HelloResource.URL, new HelloResource());
```

### Not Found Resource


A resource is needed to handle 404s, [NotFoundResource](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/NotFoundResource.java).
Which should be configured in your entry servlet as well.

```java
    Route notFoundRoute = new RouteBuilder()
        .resource(new NotFoundResource())
        .before(new ArrayList<>())
        .after(new ArrayList<>())
        .build();

    servletGateway.setNotFoundRoute(notFoundRoute);
```

### CSRF 

Otter supports CSRF protection by implementing the double submit strategy.

Here is an example of how to protect a login page:

Use the [csrf routing interface](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/EntryServlet.java#L52-L53) to route requests to your resource.

```java
    servletGateway.getCsrfProtect(LoginResource.URL, new LoginResource());
    servletGateway.postCsrfProtect(LoginResource.URL, new LoginResource());
```

Set the csrf challenge token value on the [login presenter](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/LoginResource.java#L18).
```java
    LoginPresenter presenter = new LoginPresenter("", request.getCsrfChallenge().get());
```

Render the [CSRF challenge token](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/webapp/WEB-INF/jsp/login.jsp#L12) on the page.
```java
    <input id="csrfToken" type="hidden" name="csrfToken" value="${presenter.getCsrfChallengeToken()}" / >
```

### Stateless

Otter is stateless. It maintains user sessions with a cookie whose value is encrypted by using JWE. 

To use them the following is needed:
- Configure the cookie and key
- Implement a session class
- Implement your DecryptSession between.
- Inject you DecryptSession into the servletGateway.
- Add routes to the servletGateway

#### Configure
See the [Entry Servlet](#entry-servlet) section.

#### Implement session class
You will need to implement a session class which must have a copy constructor and a equals method.
If either of those are not there Otter internals will Halt your requests.

An example [session](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/security/TokenSession.java) can be found in the test suite.

#### Implement DecryptSession
An example [implementation](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/security/SessionBeforeBetween.java) can be found in the test suite.

#### Inject your DecryptSession
Inject your implementation of the `DecryptSession` into the servletGateway.

```java
    SessionBeforeBetween sessionBeforeBetween = appConfig.sessionBeforeBetween("session", encKey, new HashMap<>());
    servletGateway.setDecryptSession(sessionBeforeBetween);
```

#### Add Routes
```java
    // csrf & session
    LoginSessionResource loginWithSession = new LoginSessionResource();
    servletGateway.getCsrfAndSessionProtect(loginWithSession.URL, loginWithSession);
    servletGateway.postCsrfAndSessionProtect(loginWithSession.URL, loginWithSession);

    // session
    servletGateway.getSessionProtect(ProtectedResource.URL, new ProtectedResource());
    servletGateway.postSessionProtect(ProtectedResource.URL, new ProtectedResource());
```

### Async I/O
I/O is handled asynchronously. That journey begins in the [OtterEntryServlet](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/servlet/OtterEntryServlet.java#L33).

JSPs also are delivered async via [Jetty](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/server/container/builder/WebAppContextBuilder.java#L82).

### Static Assets

Files that are placed in, `src/main/webapp/public` are public as long as they pass the entry filter [regex](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/servlet/EntryFilter.java#L19)

For example, `src/main/webapp/public/assets/js/jquery-3.3.1.min.js` can be retrieved from, `assets/js/jquery-3.3.1.min.js`

## Releasing to maven central
```bash
$ gradle clean signArchives uploadArchives
```
