# Dockerize Accumulo

Accumulo is very boring to install. With docker, it's very easy 
and we limit issues with our machine.

We build our own docker image for our tests available [here](https://hub.docker.com/r/mcdostone/accumulo/)

## Quick start

To run the image:
``` bash
    make run
```

The command will download the docker image on [hub.docker.com](https://hub.docker.com/r/mcdostone/accumulo/) and runs a container with this image.

At the end of the startup, you will be inside the accumulo container. You can explore the container. **The last step is to start all services (accumulo, hadoop and zookeeper):**
``` bash
    bash-4.3$  entrypoint.sh
```
Nb. All the programs are installed in */opt/*

2 volumes are mounted between your host machine and the container. This enables to share directories and files between the host and the container without rebuilding after each modification:
 - The makefile in this location is mounted too!
        
        # Host                    <-->  # Container
        docker/features/Makefile  <-->  /opt/features/Makefile
        docker/benchmark/Makefile <-->  /opt/benchmark/Makefile


## Test somes features

All features that can be tested with some java code. To run this java code, you need to pass a certain number of params. To be more practital, we created a `Makefile` that can runs a given class.

Just run **make help** to see the feature you can test.
``` bash
    # /opt/features
    make help
``` 

On your host machine, you have the following rule:
``` bash
    make transfer_jar
``` 
This enables you to build the version of the jar and transfers it to the container.
(We don't use volume because we don't know why but jar isn't reloaded correctly).

## Test java classes for benchmarking

Like the previous section, we have another `Makefile` that lists of java classes you can run to benchmark accumulo. See the help for more details:
```bash
    # /opt/benchmark
    make help
```
## Some useful commands
``` bash
    docker ps -a # Show active containers
    docker stop <CONTAINER_ID> # Stop the given container
```