# hello world
This is an example web application that uses all of otter's features. It is used to execute integration tests against Otter. 

It demonstrates:
 - CSRF
 - Session Management
 - `text/html` Content-Type 
 - `applicaton/json` Content-Type

### Looking for Docs?
They are [here](../docs/Documentation.md)

## Run in IDE
To run this app in a IDE use it's [main method](https://github.com/RootServices/otter/blob/development/examples/hello-world/src/main/java/org/rootservices/hello/server/HelloServer.java)
to start it up.

## Produce a war
```bash
$ ./gradlew clean hello-world:war
```

## Execute the example web application
```bash
$ mkdir -p logs/jetty
$ java -jar java -jar hello-world-1.4-SNAPSHOT.war 
```
