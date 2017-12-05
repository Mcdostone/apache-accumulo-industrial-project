# Dockerize Accumulo

Accumulo is very boring to install. With docker, it's very easy 
and we limit issues with our machine.

There are 4 dockerfiles for the moment:
 - accumulo/Dockerfile: our main docker image (for the moment accumulo + hadoop)
 - zookeeper/Dockerfile: our zookeeper instance
 - hadoop/Dockerfile: contains all stuff for hadoop
 - single-container/Dockerfile: Zookeeper + Hadoop + Accumulo bundled in 1 image

## Installation

For the beginning, we use *single-container/Dockerfile* image which bundle
all the needed programs. The following instructions work for this image!

To build the image, just run:
``` bash
    # We supposed you're are in the docker folder !
    make build
```

## Running the image

Once the image is created, you can run it with:
``` bash
    # We supposed you're are in the docker folder !
    make run
```

At the end of the instruction, you will be inside the accumulo container. You can explore the container. **The last step is to start accumulo:**
``` bash
    bash-4.3$  entrypoint.sh
```
Nb. All the programs are installed in */opt/*

1 volume is mounted between your host machine and the container. This enables to share directories and files between the host and the container without rebuilding after each modification:
 - The makefile in this location is mounted too!
        
        # Host                           <-->  Container
        docker/single-container/Makefile <-->  /opt/Makefile


## Test somes features

All features that can be tested with some java code is available with a Makefile located at */opt/*.

Just run **make help** to see the feature you can test.
``` bash
    make help
``` 

On your host machine, you have the following rule:
``` bash
    make transfer_jar
``` 
This enables you to build the version of the jar and transfers it to the container.
(We don't use volume because we don't know why but jar isn't reloaded correctly).

## Some useful commands
``` bash
    docker ps -a # Show active containers
    docker stop <CONTAINER_ID> # Stop the given container
```