# Dockerize Accumulo

Accumulo is very boring to install. With docker, it's very easy 
and we limit issues with our machine.

There are 3 dockerfiles:
 - accumulo/Dockerfile: our main docker image (for the moment accumulo + hadoop)
 - zookeeper/Dockerfile: our zookeeper instance
 - hadoop/Dockerfile: contains all stuff for hadoop

## Installation

To build our docker images, just run:
``` bash
    # We supposed you're are in the docker folder !
    make build_all
```

This will download and create all necessary images for our tests.


## Execution

There are 2 steps for the execution. The first one is to run zookeeper and accumulo images

``` bash
    # We supposed you're are in the docker folder !
    make run
```

At the end of the instruction, you will be inside the accumulo container. You can explore
the container (it's linux guys). The last step is to run accumulo:
``` bash
    bash-4.3$  entrypoint.sh
```

About the accumulo image:
 - All the programs are installed in /opt/
 - There are a lot of environment variables


### Some useful commands
``` bash
    docker ps -a # Show active containers
    docker stop <CONTAINER_ID> # Stop the given container
```