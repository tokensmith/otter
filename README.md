# otter
Otter is a micro web framework that sits on top of the servlet api 3.1. 

It's feature set includes:
- Embedded servlet container
- Regex routing
- Restful support
- CSRF protection
- Async I/O

## Maven coordinates
```
<dependencies>
    <dependency>
        <groupId>org.rootservices</groupId>
        <artifactId>otter</artifactId>
        <version>1.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Example Application
An example application can be found [here](https://github.com/RootServices/otter/tree/development/src/test/java/integration/app). Which is used for integration tests and will be referenced throughout the documentation..


## Anatomy of a application

### Embedded Container

Otter can run in a Jetty powered [embedded servlet container](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/server/HelloServer.java).

### Entry Servlet

Otter needs a [entry servlet](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/EntryServlet.java) which will be used to route requests to the resources that handle the requests.
Your implementation of the entry servlet must be annotated with, ```@WebServlet(value="/app/*", name="EntryServlet", asyncSupported = true)```


### Resources

A resource is what handles a request. There are two types of resources that otter supports.
- [Resource](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/controller/Resource.java)
- [RestResource](https://github.com/RootServices/otter/blob/development/src/main/java/org/rootservices/otter/controller/RestResource.java)

Resource is designed to handle any type of content type, typically used to render text/html.
RestResource is designed to handle the content type, application/json. 

### Routing

Routing requests is done in the entry servlet. Given a resource it will need to register a route for each http method that should be handled. An example of this is [here](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/EntryServlet.java#L50-L51)


### CSRF

Otter supports CSRF protection by implementing the double submit strategy, which is setting a cookie with it's value the CSRF token and placing a form field with same CSRF token on the page. Both, the cookie and the form field must match for CSRF to pass. 

To enable CSRF:
- Configure a key to sign cookies with, which can be observed [here](https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/EntryServlet.java#L28-L34).
- Use the (csrf routing interface)[https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/EntryServlet.java#L52-L53] to route requests to your resource.
- Configure the (presenter)[https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/hello/controller/LoginResource.java#L18] to set the value of the CSRF challenge token.
- Render the (CSRF challenge token)[https://github.com/RootServices/otter/blob/development/src/test/java/integration/app/webapp/WEB-INF/jsp/login.jsp#L12] on the page.


## Maven uber jar
There are probably a handful of ways to create a uber jar this works for me.
 
```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-dependency-plugin</artifactId>
    <version>2.10</version>
    <executions>
        <execution>
            <id>copy-dependencies</id>
            <phase>package</phase>
            <goals>
                <goal>copy-dependencies</goal>
            </goals>
            <configuration>
                <includeScope>compile</includeScope>
            </configuration>
        </execution>
    </executions>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>2.4.3</version>
        <executions>
            <execution>
                <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
            </execution>
    </executions>
    <configuration>
        <minimizeJar>true</minimizeJar>
        <transformers>
            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                <mainClass>your.package.HttpServer</mainClass>
            </transformer>
            <transformer implementation="org.apache.maven.plugins.shade.resource.DontIncludeResourceTransformer">
                <resource>.SF</resource>
            </transformer>
        </transformers>
    </configuration>
</plugin>
```
