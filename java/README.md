# Java

This folder is a java project, containing all java classes that help us
to test and demonstrate the implementation of all features.

## Installation

This project is a maven project. I used IntelliJ idea to create it but eclipse should be fine too... Import you project into your IDE, don't forget to install maven dependancies and you're ready to write some code!


## How to contribute ?

To add a new feature to this project, follow these steps:
 1. Write your code that implements your feature
 2. In the class [Help.java](https://github.com/Mcdostone/industrial-project/blob/master/java/src/main/java/project/industrial/Help.java), register you new class:

``` java
   classes.add(MyAwesomeFeature.class);
```
3. In this [docker/single-container/Makefile](https://github.com/Mcdostone/industrial-project/blob/master/docker/single-container/Makefile), register you new feature with all parameters needed (it easier for us to launch your program)
``` Makefile
new-feature: ## Describe what your feature is
    accumulo project.industrial.MyawesomeFeature -i $$INSTANCE --param1 523 --param2 632
```


## Build the package

To build the package that will be transfered to the docker container, create the **jar**:

``` bash
mvn package
# Or you can use your IDE
```

You should find the jar file located at *java/target/accumulo-1.0-SNAPSHOT*.