# Java

This folder is a java project, containing all java classes that help us
to test and demonstrate the implementation of all features.

## Installation

This project is a maven project. I used IntelliJ idea to create it but eclipse should be fine too... Import you project into your IDE, don't forget to install maven dependancies and you're ready to write some code!

## Build the package

To build the package that will be transfered to the docker container, create the **jar**:

``` bash
mvn package
# Or you can use your IDE
```

You should find the jar file located at *java/target/accumulo-1.0-SNAPSHOT*.