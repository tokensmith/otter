# hello world
This is an example web application that uses otter. It is used to execute integration tests with otter. 

It demonstrates:
 - CSRF
 - Session Management
 - `text/html` Content-Type 
 - `applicaton/json` Content-Type

### Looking for Docs?
They are [here](/docs/Documentation.md)

## Run in IDE
To run this app in a IDE use it's [main method](https://github.com/RootServices/otter/blob/development/example/src/main/java/org.rootservices.hello/server/HelloServer.java)
to start it up.

## Produce a war
```bash
$ ./gradlew clean example:war
```

## Execute the example web application
```bash
$ mkdir -p logs/jetty
$ java -jar java -jar example-1.4-SNAPSHOT.war 
```
