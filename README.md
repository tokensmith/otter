# otter
micro web framework for servlet api 3.1

Maven coordinates
-----------------
```
<dependencies>
    <dependency>
        <groupId>org.rootservices</groupId>
        <artifactId>otter</artifactId>
        <version>1.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

Maven uber jar
---------------
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

Run an embedded servlet container with Jetty
---------------------------------------------------
```java
package your.package;

import your.package.controller.SomeServlet;
import org.rootservices.otter.server.container.ServletContainer;
import org.rootservices.otter.server.container.ServletContainerFactory;
import org.rootservices.otter.config.AppFactory;

public class HttpServer {
    public static String DOCUMENT_ROOT = "/";
    public static int PORT = 8080;

    public static void main(String[] args) {
        AppFactory otterAppFactory = new AppFactory();
        ServletContainerFactory servletContainerFactory = otterAppFactory.servletContainerFactory();
        File tempDirectory = new File("/tmp");

        ServletContainer server = null;
        try {
            server = servletContainerFactory.makeServletContainer(DOCUMENT_ROOT, SomeServlet.class, PORT, tempDirectory);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
