# otter
Otter is a micro web framework that sits on top of the servlet api 3.1. 

It's feature set includes:
- Embedded servlet container
- Regex routing
- Restful support
- CSRF protection
- Async I/O

## Maven coordinates
```xml
<dependencies>
    <dependency>
        <groupId>org.rootservices</groupId>
        <artifactId>otter</artifactId>
        <version>1.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Gradle
```groovy
compile group: 'org.rootservices', name: 'otter', version: '1.1-SNAPSHOT'
```

## Example Application
A [hello world](https://github.com/RootServices/hello-world) example is available which demonstrates CSRF, Rest, and tex/html responses.
A clone of the hello world application is included in the test suite and will be referenced throughout the documentation. 

## Basic Usage

### Embedded Container

Otter can run in a Jetty powered [embedded servlet container](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/server/HelloServer.java).

### Entry Servlet

Otter needs a [entry servlet](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/EntryServlet.java) which will be used to route requests to resources. Resources are what handle requests.

Your implementation of the entry servlet must be annotated with:
```java 
    @WebServlet(value="/app/*", name="EntryServlet", asyncSupported = true)
```

- The only value that can change is, `name="EntryServlet"`. All the other values must be as shown above.
- `value="/app/\*"` must not change because the [entry filter](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/servlet/EntryFilter.java) prepends `/app/` to all requests that are not jsps.


### Resources

A resource is what handles a request. There are two types of resources that otter supports.
- [Resource](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/controller/Resource.java)
- [RestResource](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/controller/RestResource.java)

Resource is designed to handle any content type, typically used to render text/html.

RestResource is designed to handle application/json. 

### Routing

Routing requests to resources is done in your implementation of the [entry servlet](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/EntryServlet.java#L50-L51)
. 

```java
    servletGateway.get(HelloResource.URL, new HelloResource());
```

### Not Found Resource

A resource is needed to handle 404s, [NotFoundResource](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/NotFoundResource.java).

In addition it must be registered it in your [entry servlet](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/EntryServlet.java#L41-L47).

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

Configure a key to sign cookies with, which can be observed [here](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/EntryServlet.java#L28-L34).

```java
    SymmetricKey key = new SymmetricKey(
    Optional.of("key-1"),
    "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow",
    Use.SIGNATURE
    );

    servletGateway.setSignKey(key);
```

Set cookie and csrf settings in the entry servlet. 
```java
    servletGateway.setCsrfCookieAge(-1);
    servletGateway.setCsrfCookieName("csrf");
    servletGateway.setCsrfCookieSecure(false);
    servletGateway.setCsrfFormFieldName("csrfToken");
```

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

## Releasing to maven central
```bash
$ gradle clean signArchives uploadArchives
```
