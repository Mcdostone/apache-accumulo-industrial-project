# Dockerize Accumulo

Accumulo is very boring to install. With docker, it's very easy 
and we limit issues with our machine.

There are 4 dockerfiles for the moment:
 - accumulo/Dockerfile: our main docker image (for the moment accumulo + hadoop)
 - zookeeper/Dockerfile: our zookeeper instance
 - hadoop/Dockerfile: contains all stuff for hadoop
 - single-container/Dockerfile: Zookeeper + Hadoop + Accumulo bundled in 1 image

## Installation and execution

To build our docker images, just run:
``` bash
    # We supposed you're are in the docker folder !
    make build
    make run
```

At the end of the instruction, you will be inside the accumulo container. You can explore the container. **The last step is to run accumulo:**
``` bash
    bash-4.3$  entrypoint.sh
```
Nb. All the programs are installed in */opt/*


## Some useful commands
``` bash
    docker ps -a # Show active containers
    docker stop <CONTAINER_ID> # Stop the given container
```