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
        <version>1.2</version>
    </dependency>
</dependencies>
```

#### Gradle
```groovy
compile group: 'org.rootservices', name: 'otter', version: '1.2'
```

## Example Application
A [hello world](https://github.com/RootServices/otter/tree/development/example) 
example application is available which demonstrates CSRF, Sessions, Rest, and `tex/html` responses.
That application will be referenced throughout the documentation. 

## Introduction
- [Resource](#resource)
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
- Override the methods that handle http methods (get, post, put, delete).
- The response status code must be assigned.
- Specify a public ivar for the url path (in regex notation).
- If needed assign the template path to the response's template path variable.

The examples should be sufficient to get started.

### Configuration
Otter needs to be configured for CSRF, Session, and Routes. To configure Otter implement the [Configure](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/gateway/Configure.java)
interface. 

##### `configure(Gateway gateway)`
The implementation of `configure(Gateway gateway)` should configure CSRF and Sessions. Both need
a cookie configuration and symmetric key configuration.

```java
    // CSRF cookie configuration
    CookieConfig csrfCookieConfig = new CookieConfig("csrf", false, -1);
    gateway.setCsrfCookieConfig(csrfCookieConfig);

    // Session cookie configuration.
    CookieConfig sessionCookieConfig = new CookieConfig("session", false, -1);
    gateway.setSessionCookieConfig(sessionCookieConfig);

    // CSRF key configuration.
    SymmetricKey csrfKey = new SymmetricKey(
        Optional.of("key-1"),
        "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow",
        Use.SIGNATURE
    );
    gateway.setSignKey(csrfKey);

    //Session key configuration.
    SymmetricKey encKey = new SymmetricKey(
        Optional.of("key-2"),
        "MMNj8rE5m7NIDhwKYDmHSnlU1wfKuVvW6G--GKPYkRA",
        Use.ENCRYPTION
    );
    gateway.setEncKey(key);
```

##### `routes(Gateway gateway)`
Generally, routes instruct Otter which Resource should handle a given request. Below is an example of a `GET` request 
that will be handled by the `HelloResorce`. 
 
```java
    // route a get request.
    gateway.get(HelloResource.URL, new HelloResource());
```

When Otter cannot find a route to satisfy a request it will use it's `notFoundRoute`.
This should be configured in the `routes(Gateway gateway)` implementation.

```java
    Route notFoundRoute = new RouteBuilder()
        .resource(new NotFoundResource())
        .before(new ArrayList<>())
        .after(new ArrayList<>())
        .build();

    gateway.setNotFoundRoute(notFoundRoute);
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

##### Override `makeConfigure()`
This method must return an instance of the [configruation](#configuration) interface.  

### CSRF 

Otter supports CSRF protection by implementing the double submit strategy.

Here is an example of how to protect a login page:

Use the csrf routing interface to route requests to your resource.

```java
    servletGateway.getCsrfProtect(LoginResource.URL, new LoginResource());
    servletGateway.postCsrfProtect(LoginResource.URL, new LoginResource());
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
- Configure the cookie and key
- Implement a session class
- Implement your DecryptSession between.
- Inject you DecryptSession into the servletGateway.
- Add routes to the servletGateway

#### Configure
See the [Configuration](#configuration) section.

#### Implement session class
You will need to implement a session class which must have a copy constructor and a equals method.
If either of those are not there Otter internals will Halt your requests.

An example [session](https://github.com/RootServices/otter/blob/development/example/src/main/java/hello/security/TokenSession.java)
can be found in the test suite.

#### Implement DecryptSession
An example [implementation](https://github.com/RootServices/otter/blob/development/example/src/main/java/hello/security/SessionBefore.java) 
can be found in the test suite.

#### Inject your DecryptSession
Inject your implementation of the `DecryptSession` into the servletGateway.

```java
    SessionBefore sessionBefore = appFactory.sessionBefore("session", encKey, new HashMap<>());
    gateway.setDecryptSession(sessionBefore);
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
I/O is handled asynchronously. That journey begins in the [OtterEntryServlet](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/servlet/OtterEntryServlet.java#L33).

JSPs also are delivered async via [Jetty](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/server/container/builder/WebAppContextBuilder.java#L82).

### Static Assets

Files that are placed in, `src/main/webapp/public` are public as long as they pass the entry filter [regex](https://github.com/RootServices/otter/blob/development/otter/src/main/java/org/rootservices/otter/servlet/EntryFilter.java#L19)

For example, `src/main/webapp/public/assets/js/jquery-3.3.1.min.js` can be retrieved from, `assets/js/jquery-3.3.1.min.js`

## Releasing to maven central
```bash
$ gradle clean signArchives uploadArchives
```
