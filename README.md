# otter
Otter is a micro web framework that sits on top of the servlet api 4.0.1 

- [Contribute!](#contribute)
- [Hello World Application](#hello-world-application)
- [Quick Start Application](#quick-start-application)
- [Dependency Coordinates](#dependency-coordinates)
- [Documentation](#documentation)
- [Why?](#why)

## Contribute!
[Contribute!](/docs/Contribute.md)  

## Hello World Application
If you want to get started with all the details head over to the [hello world](/hello-world) 
example application.

## Quick Start Application
If you want to get started quickly head over to the [quick start](/quick-start) 
example application. Only 5 files to get started.

## Dependency coordinates
#### Maven
```xml
<dependencies>
    <dependency>
        <groupId>org.rootservices</groupId>
        <artifactId>otter</artifactId>
        <version>1.4</version>
    </dependency>
</dependencies>
```

#### Gradle
```groovy
compile group: 'org.rootservices', name: 'otter', version: '1.4'
```

## Documentation
Detailed [documentation](/docs/Documentation.md) is available.

## Why?
Otter was created to provide these features:
 - Preference of Generics over Reflection
 - Abstract the I/O framework from the web framework
 - Regex routing
 - Application encrypted sessions
 - Async I/O
 - HTTP 2
 
Otter began embedded within a web application in late 2014. Back then there were not many options for those 
features. It was extracted and open sourced in 2017. 
